# Plan d'implémentation Offline-First pour HobbyMatchMaker

## Résumé Exécutif

Ce plan transforme HobbyMatchMaker en une application **offline-first** complète avec refactorisation agressive de l'architecture actuelle. Basé sur l'analyse approfondie du codebase, il se concentre sur les priorités critiques :

**Priorités P0 (Must-Have):**
- ✅ Infrastructure de synchronisation (ConnectivityMonitor + SyncQueue)
- ✅ Invitations sociales offline (actuellement complètement cassé)
- ✅ Pré-chargement des détails de films (éviter les erreurs "connection issue")
- ✅ Background sync (WorkManager/BGTasks)

**Approche:** Refactorisation agressive - modification en profondeur des repositories, ajout de ConnectivityMonitor partout, migrations complexes pour une architecture optimale.

**Effort estimé:** 6-8 semaines (1 développeur senior)

---

## 🎯 Architecture Cible

### Principes Offline-First

1. **Local-First**: Toutes les lectures depuis SQLDelight (source unique de vérité)
2. **Optimistic Updates**: Écriture locale immédiate + sync async vers remote
3. **Sync Queue**: File persistante pour toutes les opérations nécessitant le réseau
4. **Reactive Connectivity**: State Flow pour l'état réseau observé par tous les repositories
5. **Background Sync**: Synchronisation automatique périodique même app fermée

### Architecture en Couches

```
┌─────────────────────────────────────────────────────┐
│                  UI Layer (Compose)                  │
│         ViewModel observe StateFlow<UiState>         │
└─────────────────┬───────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────┐
│              Domain Layer (UseCases)                 │
│    - ObserveInvitationsUseCase                      │
│    - PrefetchMovieDetailsUseCase                    │
│    - SendInvitationUseCase                          │
└─────────────────┬───────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────┐
│           Data Layer (Repositories)                  │
│  ┌──────────────────────────────────────────────┐  │
│  │  ConnectivityMonitor (inject partout)        │  │
│  │  SyncManager (inject partout)                │  │
│  └──────────────────────────────────────────────┘  │
│                                                      │
│  Repository Pattern (refactorisé):                  │
│  - Observe: SQLDelight Flow only                   │
│  - Write: Local first + sync via SyncManager       │
│  - Sync: Background refresh quand online           │
└─────────────────┬───────────────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼─────┐    ┌────────▼────────┐
│  SQLDelight │    │  Remote Sources │
│  (Local DB) │    │ (Firebase/Ktor) │
│             │    │                 │
│ - movies    │    │ - Firestore     │
│ - invites   │    │ - TMDB API      │
│ - sync_queue│    │                 │
└─────────────┘    └─────────────────┘
```

---

## 📦 Phase 1: Infrastructure Core (Semaine 1-2)

### 1.1 ConnectivityMonitor (Reactive)

**Nouvelle interface commune:**

```kotlin
// core/network/src/commonMain/kotlin/.../connectivity/ConnectivityMonitor.kt
interface ConnectivityMonitor {
    val isConnected: StateFlow<Boolean>
    val connectionState: StateFlow<ConnectivityState>
    suspend fun awaitConnection() // Suspend jusqu'à connexion réseau
}

sealed interface ConnectivityState {
    data object Connected : ConnectivityState
    data object Disconnected : ConnectivityState
}
```

**Implémentations platform-specific:**

**Android** (NetworkCallback API):
```kotlin
// core/network/src/androidMain/kotlin/.../connectivity/ConnectivityMonitorImpl.android.kt
actual class ConnectivityMonitorImpl(
    context: Context,
    scope: CoroutineScope
) : ConnectivityMonitor {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    val isConnected: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val connectionState: StateFlow<ConnectivityState>
        field = MutableStateFlow<ConnectivityState>(
            ConnectivityState.Disconnected
        )

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isConnected.value = true
            connectionState.value = ConnectivityState.Connected
        }

        override fun onLost(network: Network) {
            isConnected.value = false
            connectionState.value = ConnectivityState.Disconnected
        }
    }

    init {
        // État initial
        isConnected.value = hasActiveConnection()
        connectionState.value = if (_isConnected.value)
            ConnectivityState.Connected
        else
            ConnectivityState.Disconnected

        // Enregistrement du callback
        connectivityManager?.registerDefaultNetworkCallback(networkCallback)
    }

    override suspend fun awaitConnection() {
        isConnected.first { it }
    }

    private fun hasActiveConnection(): Boolean {
        val network = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }
}
```

**iOS** (NWPathMonitor):
```kotlin
// core/network/src/iosMain/kotlin/.../connectivity/ConnectivityMonitorImpl.ios.kt
actual class ConnectivityMonitorImpl(
    scope: CoroutineScope
) : ConnectivityMonitor {

    private val monitor = NWPathMonitor()
    val isConnected: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val connectionState: StateFlow<ConnectivityState>
        field = MutableStateFlow<ConnectivityState>(
            ConnectivityState.Disconnected
        )

    init {
        monitor.pathUpdateHandler = { path ->
            val connected = path.status == NWPathStatusSatisfied
            isConnected.value = connected
            connectionState.value = if (connected)
                ConnectivityState.Connected
            else
                ConnectivityState.Disconnected
        }
        monitor.start(queue = DispatchQueue.main)
    }

    override suspend fun awaitConnection() {
        isConnected.first { it }
    }
}
```

**DI Module:**
```kotlin
// core/network/src/commonMain/kotlin/.../di/NetworkModule.kt
val networkModule = module {
    single<ConnectivityMonitor> {
        ConnectivityMonitorImpl(
            context = androidContext(), // Android
            // OR scope = get() // iOS
            scope = get()
        )
    }
}
```

**Fichiers à créer:**
- `/core/network/src/commonMain/kotlin/com/msoula/hobbymatchmaker/core/network/connectivity/ConnectivityMonitor.kt`
- `/core/network/src/androidMain/kotlin/com/msoula/hobbymatchmaker/core/network/connectivity/ConnectivityMonitorImpl.android.kt`
- `/core/network/src/iosMain/kotlin/com/msoula/hobbymatchmaker/core/network/connectivity/ConnectivityMonitorImpl.ios.kt`

---

### 1.2 Sync Queue System

**Extension du schéma SQLDelight:**

```sql
-- core/database/src/commonMain/sqldelight/com/msoula/.../hmm_database.sq

CREATE TABLE sync_queue (
    id TEXT PRIMARY KEY NOT NULL,
    operation_type TEXT NOT NULL, -- 'SEND_INVITATION', 'UPDATE_INVITATION', 'ADD_FAVORITE', etc.
    payload TEXT NOT NULL,         -- JSON sérialisé de l'opération
    user_id TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_retry_at INTEGER,
    status TEXT NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED'
    error_message TEXT
);

CREATE INDEX sync_queue_status_created ON sync_queue(status, created_at);
CREATE INDEX sync_queue_user ON sync_queue(user_id);

-- Queries
insertSyncOperation:
INSERT INTO sync_queue(id, operation_type, payload, user_id, created_at, retry_count, status)
VALUES (?, ?, ?, ?, ?, ?, ?);

getPendingOperations:
SELECT * FROM sync_queue
WHERE status = 'PENDING'
ORDER BY created_at ASC;

updateOperationStatus:
UPDATE sync_queue
SET status = ?, retry_count = ?, last_retry_at = ?, error_message = ?
WHERE id = ?;

deleteOperation:
DELETE FROM sync_queue WHERE id = ?;

observePendingCount:
SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING';

getFailedOperations:
SELECT * FROM sync_queue WHERE status = 'FAILED';
```

**Modèles de domaine:**

```kotlin
// core/common/src/commonMain/kotlin/.../sync/SyncOperation.kt
@Serializable
sealed interface SyncOperation {
    val id: String
    val userId: String
    val createdAt: Long

    @Serializable
    @SerialName("send_invitation")
    data class SendInvitation(
        override val id: String,
        override val userId: String,
        override val createdAt: Long,
        val targetUserId: String,
        val message: String?
    ) : SyncOperation

    @Serializable
    @SerialName("update_invitation")
    data class UpdateInvitationStatus(
        override val id: String,
        override val userId: String,
        override val createdAt: Long,
        val invitationId: String,
        val newStatus: String // "ACCEPTED", "REJECTED", "CANCELLED"
    ) : SyncOperation

    @Serializable
    @SerialName("add_favorite")
    data class AddFavorite(
        override val id: String,
        override val userId: String,
        override val createdAt: Long,
        val movieId: Long
    ) : SyncOperation

    @Serializable
    @SerialName("remove_favorite")
    data class RemoveFavorite(
        override val id: String,
        override val userId: String,
        override val createdAt: Long,
        val movieId: Long
    ) : SyncOperation
}

enum class SyncStatus {
    PENDING, IN_PROGRESS, COMPLETED, FAILED
}
```

**SyncManager (orchestrateur):**

```kotlin
// core/network/src/commonMain/kotlin/.../sync/SyncManager.kt
interface SyncManager {
    suspend fun enqueueOperation(operation: SyncOperation): AppResult<Unit, AppError>
    suspend fun processPendingOperations(): AppResult<Int, AppError> // retourne nb traités
    fun observePendingCount(): Flow<Int>
    suspend fun cancelOperation(operationId: String): AppResult<Unit, AppError>
    suspend fun retryFailedOperations(): AppResult<Int, AppError>
}

class SyncManagerImpl(
    private val database: HobbyMatchMakerDatabase,
    private val connectivityMonitor: ConnectivityMonitor,
    private val syncExecutor: SyncExecutor,
    private val json: Json,
    private val dispatcherProvider: DispatcherProvider,
    private val scope: CoroutineScope
) : SyncManager {

    override suspend fun enqueueOperation(
        operation: SyncOperation
    ): AppResult<Unit, AppError> = withContext(dispatcherProvider.io) {
        safeCallStorage {
            val payload = json.encodeToString(
                SyncOperation.serializer(),
                operation
            )

            database.syncQueueQueries.insertSyncOperation(
                id = operation.id,
                operation_type = operation::class.simpleName ?: "UNKNOWN",
                payload = payload,
                user_id = operation.userId,
                created_at = operation.createdAt,
                retry_count = 0,
                status = "PENDING"
            )

            // Trigger immédiat si connecté
            if (connectivityMonitor.isConnected.value) {
                scope.launch { processPendingOperations() }
            }
        }
    }

    override suspend fun processPendingOperations(): AppResult<Int, AppError> =
        withContext(dispatcherProvider.io) {
            if (!connectivityMonitor.isConnected.value) {
                return@withContext AppResult.Failure(
                    AppError.Network.Unreachable
                )
            }

            val pending = database.syncQueueQueries
                .getPendingOperations()
                .executeAsList()

            var processedCount = 0

            pending.forEach { item ->
                // Désérialisation
                val operation = try {
                    json.decodeFromString<SyncOperation>(item.payload)
                } catch (e: SerializationException) {
                    Logger.e("Failed to deserialize operation: ${e.message}")
                    return@forEach
                }

                // Marquage en cours
                database.syncQueueQueries.updateOperationStatus(
                    status = "IN_PROGRESS",
                    retry_count = item.retry_count,
                    last_retry_at = Clock.System.now().toEpochMilliseconds(),
                    error_message = null,
                    id = item.id
                )

                // Exécution
                when (val result = syncExecutor.execute(operation)) {
                    is AppResult.Success -> {
                        database.syncQueueQueries.deleteOperation(item.id)
                        processedCount++
                    }
                    is AppResult.Failure -> {
                        val newRetryCount = item.retry_count + 1
                        val newStatus = if (newRetryCount >= MAX_RETRIES) "FAILED" else "PENDING"

                        database.syncQueueQueries.updateOperationStatus(
                            status = newStatus,
                            retry_count = newRetryCount,
                            last_retry_at = Clock.System.now().toEpochMilliseconds(),
                            error_message = result.error.toString(),
                            id = item.id
                        )
                    }
                }
            }

            AppResult.Success(processedCount)
        }

    override fun observePendingCount(): Flow<Int> {
        return database.syncQueueQueries
            .observePendingCount()
            .asFlow()
            .mapToOne(dispatcherProvider.io)
            .map { it.toInt() }
    }

    override suspend fun cancelOperation(
        operationId: String
    ): AppResult<Unit, AppError> = withContext(dispatcherProvider.io) {
        safeCallStorage {
            database.syncQueueQueries.deleteOperation(operationId)
        }
    }

    override suspend fun retryFailedOperations(): AppResult<Int, AppError> =
        withContext(dispatcherProvider.io) {
            val failed = database.syncQueueQueries.getFailedOperations().executeAsList()

            failed.forEach { item ->
                database.syncQueueQueries.updateOperationStatus(
                    status = "PENDING",
                    retry_count = 0, // Reset retry count
                    last_retry_at = null,
                    error_message = null,
                    id = item.id
                )
            }

            processPendingOperations()
        }

    companion object {
        private const val MAX_RETRIES = 3
    }
}
```

**SyncExecutor (délègue aux repositories):**

```kotlin
// core/network/src/commonMain/kotlin/.../sync/SyncExecutor.kt
interface SyncExecutor {
    suspend fun execute(operation: SyncOperation): AppResult<Unit, AppError>
}

class SyncExecutorImpl(
    private val socialRepository: SocialRepository,
    private val movieRepository: MovieRepository
) : SyncExecutor {

    override suspend fun execute(operation: SyncOperation): AppResult<Unit, AppError> {
        return when (operation) {
            is SyncOperation.SendInvitation -> {
                socialRepository.sendInvitationRemote(
                    targetUserId = operation.targetUserId,
                    message = operation.message
                )
            }

            is SyncOperation.UpdateInvitationStatus -> {
                socialRepository.updateInvitationStatusRemote(
                    invitationId = operation.invitationId,
                    newStatus = operation.newStatus
                )
            }

            is SyncOperation.AddFavorite -> {
                movieRepository.addFavoriteRemote(operation.movieId)
            }

            is SyncOperation.RemoveFavorite -> {
                movieRepository.removeFavoriteRemote(operation.movieId)
            }
        }
    }
}
```

**DI Module:**
```kotlin
// core/network/src/commonMain/kotlin/.../di/SyncModule.kt
val syncModule = module {
    single<SyncManager> {
        SyncManagerImpl(
            database = get(),
            connectivityMonitor = get(),
            syncExecutor = get(),
            json = get(),
            dispatcherProvider = get(),
            scope = get() // Custom scope lié au lifecycle app
        )
    }

    single<SyncExecutor> {
        SyncExecutorImpl(
            socialRepository = get(),
            movieRepository = get()
        )
    }
}
```

**Fichiers à créer/modifier:**
- `/core/database/src/commonMain/sqldelight/.../hmm_database.sq` (ajouter table sync_queue)
- `/core/common/src/commonMain/kotlin/.../sync/SyncOperation.kt` (nouveau)
- `/core/network/src/commonMain/kotlin/.../sync/SyncManager.kt` (nouveau)
- `/core/network/src/commonMain/kotlin/.../sync/SyncExecutor.kt` (nouveau)

---

### 1.3 Background Sync Workers

**Android - WorkManager:**

```kotlin
// composeApp/src/androidMain/kotlin/.../workers/SyncWorker.kt
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val syncManager: SyncManager by inject()

    override suspend fun doWork(): Result {
        return when (val result = syncManager.processPendingOperations()) {
            is AppResult.Success -> {
                Result.success(
                    workDataOf("processed_count" to result.data)
                )
            }
            is AppResult.Failure -> {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure(
                        workDataOf("error" to result.error.toString())
                    )
                }
            }
        }
    }

    companion object {
        const val WORK_NAME = "sync_pending_operations"

        fun schedulePeriodicSync(context: Context) {
            val workManager = WorkManager.getInstance(context)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    10,
                    TimeUnit.SECONDS
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}
```

**Enregistrement dans Application:**
```kotlin
// composeApp/src/androidMain/kotlin/.../HobbyMatchMakerApplication.kt
class HobbyMatchMakerApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        // ... existing Koin setup ...

        // Schedule background sync
        SyncWorker.schedulePeriodicSync(this)
    }
}
```

**iOS - Background Tasks:**

```swift
// iosApp/iosApp/BackgroundSync/BackgroundSyncManager.swift
import BackgroundTasks
import ComposeApp

class BackgroundSyncManager {
    static let shared = BackgroundSyncManager()
    private let taskIdentifier = "com.msoula.hobbymatchmaker.sync"

    func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: taskIdentifier,
            using: nil
        ) { task in
            self.handleSync(task: task as! BGProcessingTask)
        }
    }

    func scheduleNextSync() {
        let request = BGProcessingTaskRequest(identifier: taskIdentifier)
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 min

        do {
            try BGTaskScheduler.shared.submit(request)
            print("✅ Background sync scheduled")
        } catch {
            print("❌ Failed to schedule background sync: \(error)")
        }
    }

    private func handleSync(task: BGProcessingTask) {
        let syncManager = KoinKt.koin().get(objCClass: SyncManager.self) as! SyncManager

        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }

        Task {
            let result = await syncManager.processPendingOperations()

            let success = result is AppResultSuccess
            task.setTaskCompleted(success: success)

            // Schedule prochain sync
            if success {
                self.scheduleNextSync()
            }
        }
    }
}
```

**Enregistrement dans AppDelegate:**
```swift
// iosApp/iosApp/iOSApp.swift
@main
struct iOSApp: App {

    init() {
        KoinKt.doInitKoin()
        BackgroundSyncManager.shared.registerBackgroundTasks()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onReceive(
                    NotificationCenter.default.publisher(
                        for: UIApplication.didEnterBackgroundNotification
                    )
                ) { _ in
                    BackgroundSyncManager.shared.scheduleNextSync()
                }
        }
    }
}
```

**Info.plist update:**
```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.msoula.hobbymatchmaker.sync</string>
</array>
```

**Fichiers à créer:**
- `/composeApp/src/androidMain/kotlin/.../workers/SyncWorker.kt`
- `/iosApp/iosApp/BackgroundSync/BackgroundSyncManager.swift`
- Modifier `/composeApp/src/androidMain/kotlin/.../HobbyMatchMakerApplication.kt`
- Modifier `/iosApp/iosApp/iOSApp.swift`
- Modifier `/iosApp/iosApp/Info.plist`

---

## 📱 Phase 2: Social - Invitations Offline (Semaine 3-4)

### 2.1 Schéma Base de Données pour Invitations

```sql
-- core/database/src/commonMain/sqldelight/.../hmm_database.sq

CREATE TABLE invitation (
    id TEXT PRIMARY KEY NOT NULL,
    sender_id TEXT NOT NULL,
    sender_username TEXT NOT NULL,
    sender_profile_picture TEXT,
    receiver_id TEXT NOT NULL,
    status TEXT NOT NULL,              -- 'PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED'
    message TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    sync_status TEXT NOT NULL DEFAULT 'SYNCED' -- 'SYNCED', 'PENDING_SYNC'
);

CREATE INDEX invitation_receiver_status ON invitation(receiver_id, status);
CREATE INDEX invitation_sender ON invitation(sender_id);
CREATE INDEX invitation_sync_status ON invitation(sync_status);

-- Queries
insertInvitation:
INSERT OR REPLACE INTO invitation(
    id, sender_id, sender_username, sender_profile_picture,
    receiver_id, status, message, created_at, updated_at, sync_status
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

observeReceivedInvitations:
SELECT * FROM invitation
WHERE receiver_id = ? AND status = 'PENDING'
ORDER BY created_at DESC;

observeSentInvitations:
SELECT * FROM invitation
WHERE sender_id = ? AND status IN ('PENDING', 'ACCEPTED')
ORDER BY created_at DESC;

updateInvitationStatus:
UPDATE invitation
SET status = ?, updated_at = ?, sync_status = ?
WHERE id = ?;

deleteInvitation:
DELETE FROM invitation WHERE id = ?;

getInvitationById:
SELECT * FROM invitation WHERE id = ?;
```

**DAO:**
```kotlin
// core/database/src/commonMain/kotlin/.../services/InvitationDAO.kt
interface InvitationDAO {
    fun observeReceivedInvitations(userId: String): Flow<List<Invitation>>
    fun observeSentInvitations(userId: String): Flow<List<Invitation>>
    suspend fun upsertInvitation(invitation: Invitation)
    suspend fun updateStatus(id: String, status: String, syncStatus: String)
    suspend fun deleteInvitation(id: String)
}

class InvitationDAOImpl(
    private val database: HobbyMatchMakerDatabase,
    private val dispatcherProvider: DispatcherProvider
) : InvitationDAO {

    override fun observeReceivedInvitations(userId: String): Flow<List<Invitation>> {
        return database.invitationQueries
            .observeReceivedInvitations(userId)
            .asFlow()
            .mapToList(dispatcherProvider.io)
    }

    override fun observeSentInvitations(userId: String): Flow<List<Invitation>> {
        return database.invitationQueries
            .observeSentInvitations(userId)
            .asFlow()
            .mapToList(dispatcherProvider.io)
    }

    override suspend fun upsertInvitation(invitation: Invitation) {
        withContext(dispatcherProvider.io) {
            database.invitationQueries.insertInvitation(
                id = invitation.id,
                sender_id = invitation.sender_id,
                sender_username = invitation.sender_username,
                sender_profile_picture = invitation.sender_profile_picture,
                receiver_id = invitation.receiver_id,
                status = invitation.status,
                message = invitation.message,
                created_at = invitation.created_at,
                updated_at = invitation.updated_at,
                sync_status = invitation.sync_status
            )
        }
    }

    override suspend fun updateStatus(id: String, status: String, syncStatus: String) {
        withContext(dispatcherProvider.io) {
            database.invitationQueries.updateInvitationStatus(
                status = status,
                updated_at = Clock.System.now().toEpochMilliseconds(),
                sync_status = syncStatus,
                id = id
            )
        }
    }

    override suspend fun deleteInvitation(id: String) {
        withContext(dispatcherProvider.io) {
            database.invitationQueries.deleteInvitation(id)
        }
    }
}
```

---

### 2.2 Refactorisation SocialRepository (Approche Agressive)

**Nouveau LocalDataSource:**
```kotlin
// features/social/data/src/commonMain/kotlin/.../dataSources/local/SocialLocalDataSourceImpl.kt
interface SocialLocalDataSource {
    fun observeReceivedInvitations(userId: String): Flow<List<InvitationEntity>>
    fun observeSentInvitations(userId: String): Flow<List<InvitationEntity>>
    suspend fun upsertInvitation(invitation: InvitationEntity)
    suspend fun updateInvitationStatus(id: String, status: String, syncStatus: String)
    suspend fun syncInvitationsFromRemote(invitations: List<InvitationEntity>)
}

class SocialLocalDataSourceImpl(
    private val invitationDAO: InvitationDAO,
    private val dispatcherProvider: DispatcherProvider
) : SocialLocalDataSource {

    override fun observeReceivedInvitations(userId: String): Flow<List<InvitationEntity>> {
        return invitationDAO.observeReceivedInvitations(userId)
            .map { list -> list.map { it.toInvitationEntity() } }
    }

    override fun observeSentInvitations(userId: String): Flow<List<InvitationEntity>> {
        return invitationDAO.observeSentInvitations(userId)
            .map { list -> list.map { it.toInvitationEntity() } }
    }

    override suspend fun upsertInvitation(invitation: InvitationEntity) {
        invitationDAO.upsertInvitation(invitation.toInvitation())
    }

    override suspend fun updateInvitationStatus(
        id: String,
        status: String,
        syncStatus: String
    ) {
        invitationDAO.updateStatus(id, status, syncStatus)
    }

    override suspend fun syncInvitationsFromRemote(
        invitations: List<InvitationEntity>
    ) = withContext(dispatcherProvider.io) {
        invitations.forEach { invitation ->
            upsertInvitation(invitation.copy(sync_status = "SYNCED"))
        }
    }
}
```

**Repository refactorisé (Local-First + Optimistic Updates):**

```kotlin
// features/social/data/src/commonMain/kotlin/.../repositories/SocialRepositoryImpl.kt
class SocialRepositoryImpl(
    private val localDataSource: SocialLocalDataSource,
    private val remoteDataSource: SocialRemoteDataSource,
    private val connectivityMonitor: ConnectivityMonitor,
    private val syncManager: SyncManager,
    private val sessionRepository: SessionRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val scope: CoroutineScope
) : SocialRepository {

    // ✅ OBSERVATION = LOCAL ONLY (source unique de vérité)
    override fun observeReceivedInvitations(
        userId: String
    ): Flow<List<InvitationDomainModel>> {

        return combine(
            localDataSource.observeReceivedInvitations(userId),
            connectivityMonitor.connectionState
        ) { localInvitations, connectionState ->

            // Background sync si connecté
            if (connectionState is ConnectivityState.Connected) {
                scope.launch {
                    syncInvitationsFromRemote(userId)
                }
            }

            localInvitations.map { it.toDomainModel() }
        }
    }

    override fun observeSentInvitations(
        userId: String
    ): Flow<List<InvitationDomainModel>> {

        return combine(
            localDataSource.observeSentInvitations(userId),
            connectivityMonitor.connectionState
        ) { localInvitations, connectionState ->

            if (connectionState is ConnectivityState.Connected) {
                scope.launch {
                    syncInvitationsFromRemote(userId)
                }
            }

            localInvitations.map { it.toDomainModel() }
        }
    }

    // ✅ WRITE = LOCAL FIRST + QUEUE POUR SYNC
    override suspend fun sendInvitation(
        targetUserId: String,
        message: String?
    ): AppResult<Unit, AppError> = withContext(dispatcherProvider.io) {

        val currentUserId = sessionRepository.getCurrentUserId()
            ?: return@withContext AppResult.Failure(AppError.Authentication.Unauthenticated)

        val currentUserProfile = sessionRepository.getCurrentUserProfile()

        val invitation = InvitationEntity(
            id = UUID.randomUUID().toString(),
            sender_id = currentUserId,
            sender_username = currentUserProfile?.pseudo ?: "Unknown",
            sender_profile_picture = currentUserProfile?.avatar_url,
            receiver_id = targetUserId,
            status = "PENDING",
            message = message,
            created_at = Clock.System.now().toEpochMilliseconds(),
            updated_at = Clock.System.now().toEpochMilliseconds(),
            sync_status = "PENDING_SYNC"
        )

        // 1. Optimistic local write
        localDataSource.upsertInvitation(invitation)

        // 2. Enqueue pour sync (immédiat si online, différé sinon)
        syncManager.enqueueOperation(
            SyncOperation.SendInvitation(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                targetUserId = targetUserId,
                message = message
            )
        )

        AppResult.Success(Unit) // Toujours succès du point de vue utilisateur
    }

    override suspend fun updateInvitationStatus(
        invitationId: String,
        newStatus: InvitationStatus
    ): AppResult<Unit, AppError> = withContext(dispatcherProvider.io) {

        val currentUserId = sessionRepository.getCurrentUserId()
            ?: return@withContext AppResult.Failure(AppError.Authentication.Unauthenticated)

        // 1. Optimistic local update
        localDataSource.updateInvitationStatus(
            id = invitationId,
            status = newStatus.name,
            syncStatus = "PENDING_SYNC"
        )

        // 2. Enqueue pour sync
        syncManager.enqueueOperation(
            SyncOperation.UpdateInvitationStatus(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                invitationId = invitationId,
                newStatus = newStatus.name
            )
        )

        AppResult.Success(Unit)
    }

    // ✅ SYNC METHODS (appelées par SyncExecutor ou background sync)
    suspend fun sendInvitationRemote(
        targetUserId: String,
        message: String?
    ): AppResult<Unit, AppError> = withContext(dispatcherProvider.io) {
        remoteDataSource.sendInvitation(targetUserId, message)
            .onSuccess {
                // Marquer comme synced dans local DB
                // (trouvé via targetUserId + sender + PENDING_SYNC status)
            }
    }

    suspend fun updateInvitationStatusRemote(
        invitationId: String,
        newStatus: String
    ): AppResult<Unit, AppError> = withContext(dispatcherProvider.io) {
        remoteDataSource.updateInvitationStatus(invitationId, newStatus)
            .onSuccess {
                localDataSource.updateInvitationStatus(
                    id = invitationId,
                    status = newStatus,
                    syncStatus = "SYNCED"
                )
            }
    }

    private suspend fun syncInvitationsFromRemote(userId: String) {
        when (val result = remoteDataSource.getAllInvitations(userId)) {
            is AppResult.Success -> {
                localDataSource.syncInvitationsFromRemote(result.data)
            }
            is AppResult.Failure -> {
                // Log mais ne fail pas - le cache local reste valide
                Logger.w("Failed to sync invitations: ${result.error}")
            }
        }
    }
}
```

**Fichiers à créer/modifier:**
- `/core/database/src/commonMain/sqldelight/.../hmm_database.sq` (table invitation)
- `/core/database/src/commonMain/kotlin/.../services/InvitationDAO.kt` (nouveau)
- `/features/social/data/src/commonMain/kotlin/.../dataSources/local/SocialLocalDataSourceImpl.kt` (refactorisé)
- `/features/social/data/src/commonMain/kotlin/.../repositories/SocialRepositoryImpl.kt` (refactorisation majeure)

---

## 🎬 Phase 3: Movies - Pré-chargement des Détails (Semaine 5)

### 3.1 UseCase de Pré-chargement

```kotlin
// features/movies/domain/src/commonMain/kotlin/.../usecase/PrefetchMovieDetailsUseCase.kt
class PrefetchMovieDetailsUseCase(
    private val movieRepository: MovieRepository,
    private val connectivityMonitor: ConnectivityMonitor,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(
        movieIds: List<Long>
    ) = withContext(dispatcherProvider.io) {

        // Skip si offline
        if (!connectivityMonitor.isConnected.value) {
            Logger.d("Skipping prefetch: offline")
            return@withContext
        }

        // Batch de 5 films en parallèle
        movieIds.chunked(5).forEach { batch ->
            supervisorScope {
                batch.map { movieId ->
                    async {
                        // Vérifier si déjà en cache
                        if (!movieRepository.isMovieDetailCached(movieId)) {
                            movieRepository.fetchMovieDetail(
                                movieId = movieId,
                                forceRefresh = false
                            )
                        }
                    }
                }.awaitAll()
            }
        }
    }
}
```

---

### 3.2 Méthode Helper dans Repository

```kotlin
// features/movies/data/src/commonMain/kotlin/.../repositories/MovieRepositoryImpl.kt
class MovieRepositoryImpl(
    // ... existing dependencies ...
) : MovieRepository {

    // ... existing methods ...

    override suspend fun isMovieDetailCached(movieId: Long): Boolean =
        withContext(dispatcherProvider.io) {
            val movie = movieLocalDataSource.getMovieById(movieId)

            // Considéré en cache si synopsis + actors présents
            !movie?.synopsis.isNullOrBlank() &&
                movieLocalDataSource.hasActors(movieId)
        }
}
```

---

### 3.3 Intégration dans MovieViewModel

```kotlin
// features/movies/presentation/src/commonMain/kotlin/.../presentation/MovieViewModel.kt
class MovieViewModel(
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val prefetchMovieDetailsUseCase: PrefetchMovieDetailsUseCase,
    // ... other dependencies
) : ViewModel() {

    private fun observeMovies() {
        scope.launch {
            observeAllMoviesUseCase().collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        _screenState.update { UiState.Success(result.data) }

                        // Pré-charger les 10 premiers films visibles
                        val visibleMovieIds = result.data.take(10).map { it.id }
                        scope.launch {
                            prefetchMovieDetailsUseCase(visibleMovieIds)
                        }
                    }
                    is AppResult.Failure -> {
                        _screenState.update {
                            UiState.Error(mapper.toUIText(result.error))
                        }
                    }
                }
            }
        }
    }

    // Appelé lors du scroll dans le carousel
    fun onMoviesScrolled(currentIndex: Int, movies: List<MovieUiModel>) {
        // Pré-charger 5 films avant et 5 films après
        val startIndex = (currentIndex - 5).coerceAtLeast(0)
        val endIndex = (currentIndex + 5).coerceAtMost(movies.size - 1)

        val visibleRange = startIndex..endIndex
        val movieIdsToPreload = movies
            .slice(visibleRange)
            .map { it.id }

        scope.launch {
            prefetchMovieDetailsUseCase(movieIdsToPreload)
        }
    }
}
```

---

### 3.4 Hook de Scroll dans Compose

```kotlin
// features/movies/presentation/src/commonMain/kotlin/.../ui/MoviesContent.kt
@Composable
fun MoviesContent(
    movies: List<MovieUiModel>,
    onMovieScrolled: (currentIndex: Int) -> Unit,
    // ... other params
) {
    val pagerState = rememberPagerState { movies.size }

    // Trigger prefetch on page change
    LaunchedEffect(pagerState.currentPage) {
        onMovieScrolled(pagerState.currentPage)
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        MovieCard(movie = movies[page])
    }
}
```

**Fichiers à créer/modifier:**
- `/features/movies/domain/src/commonMain/kotlin/.../usecase/PrefetchMovieDetailsUseCase.kt` (nouveau)
- `/features/movies/data/src/commonMain/kotlin/.../repositories/MovieRepositoryImpl.kt` (ajouter isMovieDetailCached)
- `/features/movies/presentation/src/commonMain/kotlin/.../presentation/MovieViewModel.kt` (intégrer prefetch)
- `/features/movies/presentation/src/commonMain/kotlin/.../ui/MoviesContent.kt` (hook scroll)

---

## 🎨 Phase 4: UX - Indicateur de Sync (Semaine 6)

### 4.1 Composable Sync Status Banner

```kotlin
// core/design/src/commonMain/kotlin/.../components/SyncStatusBanner.kt
@Composable
fun SyncStatusBanner(
    modifier: Modifier = Modifier,
    syncManager: SyncManager = koinInject(),
    connectivityMonitor: ConnectivityMonitor = koinInject()
) {
    val pendingCount by syncManager.observePendingCount().collectAsState(initial = 0)
    val isConnected by connectivityMonitor.isConnected.collectAsState()

    AnimatedVisibility(
        visible = !isConnected || pendingCount > 0,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = if (isConnected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isConnected) {
                        Icons.Default.CloudQueue
                    } else {
                        Icons.Default.CloudOff
                    },
                    contentDescription = null,
                    tint = if (isConnected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            !isConnected -> "Mode hors ligne"
                            pendingCount > 0 -> "$pendingCount action${if (pendingCount > 1) "s" else ""} en attente"
                            else -> "Tout est synchronisé"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (isConnected) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )

                    if (!isConnected) {
                        Text(
                            text = "Vos modifications seront synchronisées à la reconnexion",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isConnected) {
                                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            }
                        )
                    }
                }

                if (isConnected && pendingCount > 0) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
```

---

### 4.2 Intégration dans Scaffold Principal

```kotlin
// composeApp/src/commonMain/kotlin/.../AppNavHost.kt
@Composable
fun AppScaffold(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            Column {
                SyncStatusBanner() // ← Nouvelle bannière
                // ... existing TopAppBar si applicable
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = /* ... */,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ... existing navigation graph
        }
    }
}
```

**Fichiers à créer/modifier:**
- `/core/design/src/commonMain/kotlin/.../components/SyncStatusBanner.kt` (nouveau)
- `/composeApp/src/commonMain/kotlin/.../AppNavHost.kt` (intégrer bannière)

---

## 📊 Schéma de Base de Données Complet

```sql
-- core/database/src/commonMain/sqldelight/.../hmm_database.sq

-- ========================================
-- TABLES EXISTANTES (inchangées)
-- ========================================

-- movie (✅ déjà optimisé pour offline avec localCoverFilePath)
-- actor + movie_actor_cross_ref (✅ ok)
-- user_profile (✅ ok)
-- social_circle_member (✅ ok)
-- user_cache (✅ ok)

-- ========================================
-- NOUVELLES TABLES
-- ========================================

-- 1. SYNC QUEUE
CREATE TABLE sync_queue (
    id TEXT PRIMARY KEY NOT NULL,
    operation_type TEXT NOT NULL,
    payload TEXT NOT NULL,
    user_id TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_retry_at INTEGER,
    status TEXT NOT NULL DEFAULT 'PENDING',
    error_message TEXT
);

CREATE INDEX sync_queue_status_created ON sync_queue(status, created_at);
CREATE INDEX sync_queue_user ON sync_queue(user_id);

-- 2. INVITATION
CREATE TABLE invitation (
    id TEXT PRIMARY KEY NOT NULL,
    sender_id TEXT NOT NULL,
    sender_username TEXT NOT NULL,
    sender_profile_picture TEXT,
    receiver_id TEXT NOT NULL,
    status TEXT NOT NULL,
    message TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    sync_status TEXT NOT NULL DEFAULT 'SYNCED'
);

CREATE INDEX invitation_receiver_status ON invitation(receiver_id, status);
CREATE INDEX invitation_sender ON invitation(sender_id);
CREATE INDEX invitation_sync_status ON invitation(sync_status);
```

---

## 🚀 Roadmap d'Implémentation

### Sprint 1: Infrastructure (Semaine 1-2)
**Objectif:** Fondations solides pour offline-first

**Tâches:**
1. ✅ ConnectivityMonitor (Android + iOS)
2. ✅ Schéma sync_queue + DAO
3. ✅ SyncManager + SyncExecutor
4. ✅ Tests unitaires (SyncManager, ConnectivityMonitor)
5. ✅ DI Koin setup

**Livrable:** Infrastructure de sync opérationnelle, testée, prête pour intégration

---

### Sprint 2: Background Sync (Semaine 2-3)
**Objectif:** Synchronisation automatique en arrière-plan

**Tâches:**
1. ✅ SyncWorker Android (WorkManager)
2. ✅ BackgroundSyncManager iOS (BGTaskScheduler)
3. ✅ Configuration manifests/plist
4. ✅ Tests d'intégration (simulation offline/online)
5. ✅ Monitoring des workers (logs, analytics)

**Livrable:** Sync automatique toutes les 15 min quand online

---

### Sprint 3: Social Offline (Semaine 3-4)
**Objectif:** Invitations fonctionnent 100% offline

**Tâches:**
1. ✅ Schéma invitation + DAO
2. ✅ SocialLocalDataSource (refactorisé)
3. ✅ SocialRepository (optimistic updates)
4. ✅ SyncOperation.SendInvitation + UpdateInvitationStatus
5. ✅ Tests end-to-end (send/accept/reject offline)

**Livrable:** Feature social complètement offline

---

### Sprint 4: Movies Prefetch (Semaine 5)
**Objectif:** Détails de films disponibles offline

**Tâches:**
1. ✅ PrefetchMovieDetailsUseCase
2. ✅ MovieRepository.isMovieDetailCached()
3. ✅ Intégration dans MovieViewModel
4. ✅ Hook de scroll dans MoviesContent
5. ✅ Tests de performance (batch prefetch)

**Livrable:** Détails pré-chargés pour films visibles

---

### Sprint 5: UX Polish (Semaine 6)
**Objectif:** Feedback utilisateur clair

**Tâches:**
1. ✅ SyncStatusBanner composable
2. ✅ Intégration dans Scaffold
3. ✅ Animation transitions
4. ✅ Tests UI (Compose Preview, screenshot tests)
5. ✅ QA testing complet

**Livrable:** Application production-ready avec UI offline claire

---

## 🧪 Stratégie de Tests

### Tests Unitaires

```kotlin
// core/network/src/commonTest/kotlin/.../sync/SyncManagerTest.kt
class SyncManagerTest {

    private lateinit var syncManager: SyncManager
    private lateinit var mockDatabase: HobbyMatchMakerDatabase
    private lateinit var mockConnectivityMonitor: ConnectivityMonitor
    private lateinit var mockSyncExecutor: SyncExecutor

    @Before
    fun setup() {
        // Mock setup
    }

    @Test
    fun `enqueueOperation should save to database and trigger immediate sync if online`() = runTest {
        // Given
        coEvery { mockConnectivityMonitor.isConnected } returns flowOf(true)
        val operation = SyncOperation.SendInvitation(/* ... */)

        // When
        val result = syncManager.enqueueOperation(operation)

        // Then
        assertTrue(result is AppResult.Success)
        verify { mockDatabase.syncQueueQueries.insertSyncOperation(any()) }
        coVerify { mockSyncExecutor.execute(any()) }
    }

    @Test
    fun `processPendingOperations should fail gracefully when offline`() = runTest {
        // Given
        coEvery { mockConnectivityMonitor.isConnected } returns flowOf(false)

        // When
        val result = syncManager.processPendingOperations()

        // Then
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Network.Unreachable)
    }

    @Test
    fun `processPendingOperations should retry failed operations with exponential backoff`() = runTest {
        // Given
        val pendingOp = createMockPendingOperation(retryCount = 1)
        coEvery { mockDatabase.syncQueueQueries.getPendingOperations() } returns listOf(pendingOp)
        coEvery { mockSyncExecutor.execute(any()) } returns AppResult.Failure(AppError.Network.Timeout)

        // When
        syncManager.processPendingOperations()

        // Then
        verify {
            mockDatabase.syncQueueQueries.updateOperationStatus(
                status = "PENDING",
                retry_count = 2,
                match { it > 0 }, // last_retry_at
                any(),
                pendingOp.id
            )
        }
    }
}
```

---

### Tests d'Intégration

```kotlin
// features/social/data/src/commonTest/kotlin/.../SocialRepositoryIntegrationTest.kt
@RunWith(AndroidJUnit4::class)
class SocialRepositoryIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: SocialRepository
    private lateinit var database: HobbyMatchMakerDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = createInMemoryDatabase(context)

        repository = SocialRepositoryImpl(
            localDataSource = SocialLocalDataSourceImpl(database.invitationDAO()),
            remoteDataSource = FakeSocialRemoteDataSource(),
            connectivityMonitor = FakeConnectivityMonitor(),
            syncManager = FakeSyncManager(),
            sessionRepository = FakeSessionRepository(),
            dispatcherProvider = TestDispatcherProvider(),
            scope = TestScope()
        )
    }

    @Test
    fun `sendInvitation offline should save locally and queue for sync`() = runTest {
        // Given
        val fakeConnectivity = FakeConnectivityMonitor(initiallyConnected = false)

        // When
        val result = repository.sendInvitation(
            targetUserId = "user123",
            message = "Hello!"
        )

        // Then
        assertTrue(result is AppResult.Success)

        // Vérifier DB locale
        val invitations = database.invitationQueries.observeSentInvitations("currentUser")
            .executeAsList()
        assertEquals(1, invitations.size)
        assertEquals("PENDING_SYNC", invitations.first().sync_status)

        // Vérifier sync queue
        val queuedOps = database.syncQueueQueries.getPendingOperations().executeAsList()
        assertEquals(1, queuedOps.size)
        assertEquals("SendInvitation", queuedOps.first().operation_type)
    }

    @Test
    fun `observeReceivedInvitations should sync from remote when online`() = runTest {
        // Given
        val fakeRemote = FakeSocialRemoteDataSource(
            invitations = listOf(
                FakeInvitation(id = "inv1", sender_id = "user456")
            )
        )
        val fakeConnectivity = FakeConnectivityMonitor(initiallyConnected = true)

        // When
        val flow = repository.observeReceivedInvitations("currentUser")

        // Then
        flow.test {
            val emissions = awaitItem()

            // Devrait avoir syncé depuis remote
            delay(100) // Attendre background sync
            assertEquals(1, emissions.size)
            assertEquals("inv1", emissions.first().id)

            // Vérifier DB locale mise à jour
            val localInvitations = database.invitationQueries
                .observeReceivedInvitations("currentUser")
                .executeAsList()
            assertEquals(1, localInvitations.size)
            assertEquals("SYNCED", localInvitations.first().sync_status)
        }
    }
}
```

---

### Tests End-to-End (Manuel)

**Scénario 1: Invitation Offline Flow**
1. ✅ Activer mode avion
2. ✅ Ouvrir l'app → bannière "Mode hors ligne" visible
3. ✅ Aller dans Social → rechercher un utilisateur (cache)
4. ✅ Envoyer invitation → succès immédiat (optimistic)
5. ✅ Invitation visible dans "Invitations envoyées" avec icône sync pending
6. ✅ Désactiver mode avion
7. ✅ Bannière passe à "1 action en attente" avec spinner
8. ✅ Après ~5s, bannière disparaît → sync réussi
9. ✅ Vérifier sur autre device que invitation est bien arrivée

**Scénario 2: Prefetch Movies**
1. ✅ Connexion stable
2. ✅ Ouvrir l'app → carousel de films
3. ✅ Scroller jusqu'au 5ème film → vérifier logs "Prefetching movie 10-15"
4. ✅ Activer mode avion
5. ✅ Taper sur film 12 → détail s'affiche instantanément (pas d'erreur)
6. ✅ Taper sur film 50 (non prefetch) → snackbar "Détails non disponibles hors ligne"

**Scénario 3: Background Sync**
1. ✅ Mode avion activé
2. ✅ Envoyer 3 invitations
3. ✅ Fermer l'app complètement
4. ✅ Désactiver mode avion
5. ✅ Attendre 15 min (ou déclencher manuellement avec `adb shell cmd jobscheduler run`)
6. ✅ Vérifier logs: "SyncWorker: processed 3 operations"
7. ✅ Rouvrir app → aucune action en attente

---

## 📁 Fichiers Critiques à Créer/Modifier

### Créer (Nouveaux Fichiers)

**Core Infrastructure:**
1. `/core/network/src/commonMain/kotlin/.../connectivity/ConnectivityMonitor.kt`
2. `/core/network/src/androidMain/kotlin/.../connectivity/ConnectivityMonitorImpl.android.kt`
3. `/core/network/src/iosMain/kotlin/.../connectivity/ConnectivityMonitorImpl.ios.kt`
4. `/core/network/src/commonMain/kotlin/.../sync/SyncManager.kt`
5. `/core/network/src/commonMain/kotlin/.../sync/SyncExecutor.kt`
6. `/core/common/src/commonMain/kotlin/.../sync/SyncOperation.kt`
7. `/composeApp/src/androidMain/kotlin/.../workers/SyncWorker.kt`
8. `/iosApp/iosApp/BackgroundSync/BackgroundSyncManager.swift`

**Social Feature:**
9. `/core/database/src/commonMain/kotlin/.../services/InvitationDAO.kt`
10. `/features/social/data/src/commonMain/kotlin/.../mapper/InvitationMapper.kt`

**Movies Feature:**
11. `/features/movies/domain/src/commonMain/kotlin/.../usecase/PrefetchMovieDetailsUseCase.kt`

**UX:**
12. `/core/design/src/commonMain/kotlin/.../components/SyncStatusBanner.kt`

---

### Modifier (Refactorisation)

**Database:**
1. `/core/database/src/commonMain/sqldelight/.../hmm_database.sq`
   - Ajouter tables: `sync_queue`, `invitation`
   - Ajouter indexes optimisés

**Repositories (Refacto majeure):**
2. `/features/social/data/src/commonMain/kotlin/.../repositories/SocialRepositoryImpl.kt`
   - Inject ConnectivityMonitor + SyncManager
   - Optimistic updates partout
   - Local-first observations

3. `/features/movies/data/src/commonMain/kotlin/.../repositories/MovieRepositoryImpl.kt`
   - Ajouter `isMovieDetailCached()`
   - Inject ConnectivityMonitor

**ViewModels:**
4. `/features/movies/presentation/src/commonMain/kotlin/.../presentation/MovieViewModel.kt`
   - Intégrer PrefetchMovieDetailsUseCase
   - Hook onMoviesScrolled

5. `/features/social/presentation/src/commonMain/kotlin/.../presentation/SocialViewModel.kt`
   - Gérer états sync (pending_sync indicator)

**Application Setup:**
6. `/composeApp/src/androidMain/kotlin/.../HobbyMatchMakerApplication.kt`
   - Schedule SyncWorker

7. `/iosApp/iosApp/iOSApp.swift`
   - Register BackgroundSyncManager

8. `/iosApp/iosApp/Info.plist`
   - Ajouter BGTaskSchedulerPermittedIdentifiers

**DI Modules:**
9. `/core/network/src/commonMain/kotlin/.../di/NetworkModule.kt`
   - Provide ConnectivityMonitor, SyncManager, SyncExecutor

---

## ⚠️ Points d'Attention & Risques

### Risques Techniques

| Risque | Impact | Probabilité | Mitigation |
|--------|--------|-------------|------------|
| **Migration SQLDelight complexe** | Élevé | Moyen | Tests exhaustifs en dev, migration incrémentale avec fallback |
| **Conflits de sync** | Moyen | Moyen | Last-write-wins simple (v1), puis field-level merge (v2) |
| **Battery drain (background sync)** | Moyen | Faible | Constraints WorkManager (battery not low, connected only) |
| **Quota Firebase** | Élevé | Faible | Rate limiting, exponential backoff, batch operations |
| **iOS BGTask non garanti** | Faible | Moyen | User education ("sync manuel disponible"), foreground sync prioritaire |

---

### Contraintes Platform-Specific

**Android:**
- WorkManager nécessite GooglePlay Services (fallback: JobScheduler pour AOSP)
- Doze mode peut retarder sync → acceptable car non critique
- Permissions: `ACCESS_NETWORK_STATE` (déjà présent)

**iOS:**
- BGTask peut être annulée par l'OS (budgets système)
- Debugging BGTask difficile (nécessite Xcode schemes spéciaux)
- Requiert "Background Modes" capability

---

## 📈 Métriques de Succès

**Technique:**
- ✅ 100% des features prioritaires (social invitations, movie details) disponibles offline
- ✅ <200ms latence pour reads depuis cache local
- ✅ >95% taux de succès de sync (mesure sur 30 jours)
- ✅ 0 perte de données (queue persiste même après app kill)

**UX:**
- ✅ Aucune erreur "connection issue" pour opérations en cache
- ✅ Feedback visuel clair (bannière sync) dans <100ms après state change
- ✅ <3 taps pour retry manuel en cas d'échec sync

**Performance:**
- ✅ Battery drain <2% par jour pour background sync
- ✅ Database size <50MB après 1000 movies + 100 users cachés
- ✅ Prefetch ne bloque jamais l'UI (async avec supervisorScope)

---

## 🎓 Documentation à Créer

### Pour Développeurs

1. **`docs/OFFLINE_ARCHITECTURE.md`**
   - Vue d'ensemble de l'architecture offline-first
   - Diagrammes de flux (local-first, sync queue)
   - Guide d'ajout de nouvelles opérations syncables

2. **`docs/SYNC_OPERATIONS.md`**
   - Liste complète des SyncOperation types
   - Comment ajouter une nouvelle opération
   - Debugging sync failures

3. **`docs/TESTING_OFFLINE.md`**
   - Guide de tests offline (simuler network conditions)
   - Scénarios de tests end-to-end
   - Tools (Network Link Conditioner, Android Studio throttling)

### Pour CLAUDE.md

Ajouter section:
```markdown
## Offline-First Architecture

HobbyMatchMaker suit une approche **offline-first** rigoureuse :

### Principes
- Toute lecture depuis SQLDelight (source unique de vérité)
- Écriture locale optimiste + sync async via SyncQueue
- ConnectivityMonitor injectable dans tous les repositories
- Background sync toutes les 15 min (WorkManager/BGTask)

### Ajouter une Nouvelle Opération Syncable

1. Définir dans `SyncOperation.kt`:
```kotlin
@Serializable
@SerialName("your_operation")
data class YourOperation(
    override val id: String,
    override val userId: String,
    override val createdAt: Long,
    val customField: String
) : SyncOperation
```

2. Implémenter dans `SyncExecutor`:
```kotlin
is SyncOperation.YourOperation -> {
    repository.executeYourOperationRemote(operation.customField)
}
```

3. Enqueue dans repository:
```kotlin
syncManager.enqueueOperation(
    SyncOperation.YourOperation(/* ... */)
)
```

### Never Do
- ❌ Bypass sync queue pour writes nécessitant le réseau
- ❌ Observer directement remote sources (toujours passer par local DB)
- ❌ Bloquer l'UI sur network calls (toujours optimistic + async sync)
```

---

## 🎯 Prochaines Étapes (Post-MVP)

**Phase 2 Features (nice-to-have, non inclus dans ce plan):**

1. **Résolution de conflits UI** (P3)
   - Dialog pour choisir entre local/remote en cas de conflit
   - Field-level merge (ex: bio modifiée local, avatar modifié remote → merge both)

2. **Offline User Search avec FTS5** (P3)
   - Full-text search sur users cachés
   - Support multilingual tokenization

3. **Movie Match Detection Offline** (P3)
   - Cache des favoris du cercle social
   - Détection de match purement locale

4. **Pagination Multi-Pages** (P2)
   - Cache des pages 1-5 automatiquement
   - Infinite scroll avec fallback "Offline - load more online"

---

## 📝 Conclusion

Ce plan transforme HobbyMatchMaker en une **application offline-first de classe mondiale** avec :

✅ **Infrastructure solide** : ConnectivityMonitor + SyncQueue + Background Sync
✅ **Features critiques offline** : Invitations sociales + Détails de films
✅ **UX claire** : Bannière de sync, feedback immédiat, pas de frustration
✅ **Architecture scalable** : Facile d'ajouter de nouvelles opérations syncables
✅ **Tests complets** : Unitaires, intégration, end-to-end

**Effort total estimé :** 6 semaines (1 développeur senior expérimenté KMP)

**Prêt pour production :** Fin semaine 6, avec ~2 semaines de buffer QA recommandées

---

**Auteur:** Claude Code (Anthropic)
**Date:** 2026-02-03
**Version:** 1.0 - Plan Initial Approuvé
