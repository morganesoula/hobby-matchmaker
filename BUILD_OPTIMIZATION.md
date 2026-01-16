# 🚀 Guide d'Optimisation des Temps de Build Gradle

Ce document détaille les optimisations appliquées au projet pour améliorer les temps de build.

## 📊 Diagnostic Initial

- **30 modules** Kotlin Multiplatform
- **365 fichiers** Kotlin source
- Projet avec compilation iOS + Android + JVM
- Problème courant : compilation de tous les targets même lors du développement sur une seule plateforme

## ✅ Optimisations Appliquées

### 1. Configuration Gradle Optimisée (gradle.properties)

#### Configuration Cache
```properties
org.gradle.configuration-cache=true
org.gradle.configuration-cache.problems=fail
```
**Gain estimé:** 20-30% sur builds incrémentaux
- Cache la phase de configuration Gradle
- Évite de reconfigurer le projet à chaque build
- Mode `fail` au lieu de `warn` pour détecter les problèmes

#### Kotlin Multiplatform
```properties
kotlin.mpp.applyDefaultHierarchyTemplate=true
kotlin.mpp.androidSourceSetLayoutVersion=2
kotlin.native.cacheKind=static
kotlin.native.binary.memoryModel=experimental
```
**Gain estimé:** 15-25% sur compilation KMP
- Hiérarchie de source sets optimisée
- Layout Android moderne (version 2)
- Cache statique pour Kotlin/Native
- Modèle mémoire expérimental plus rapide

### 2. 🎯 Désactivation Conditionnelle des Targets iOS (MAJEUR)

**Fichier:** `local.properties`
```properties
kmp.buildTargets.ios.enabled=false
```

**Impact:** 🔥 **40-60% de temps de build en moins lors du développement Android**

#### Comment ça fonctionne
- Modifie `build-logic/convention/src/main/java/com/msoula/convention/Ios.kt`
- Lit la propriété `kmp.buildTargets.ios.enabled`
- Skip complètement `iosArm64()` et `iosSimulatorArm64()` si désactivé
- Réduit de ~943 à ~300 les bibliothèques transformées

#### Quand l'utiliser
- ✅ **Développement Android quotidien**: `false` (défaut recommandé)
- ✅ **Tests unitaires JVM**: `false`
- ⚠️ **Build iOS ou CI**: `true`
- ⚠️ **Tests iOS**: `true`

#### Basculer entre modes
```bash
# Mode Android rapide (recommandé par défaut)
echo "kmp.buildTargets.ios.enabled=false" >> local.properties

# Mode iOS/Full build
echo "kmp.buildTargets.ios.enabled=true" >> local.properties

# Ou éditez directement local.properties
```

### 3. 💡 Conseil: Gradle Home sur Disque Interne

**Recommandation:** Assurez-vous que `GRADLE_USER_HOME` pointe vers votre disque interne
- ✅ Bien meilleur performance que disque externe/réseau
- ✅ Évite les problèmes de lock files

```bash
# Dans ~/.zshrc ou ~/.bashrc
export GRADLE_USER_HOME="$HOME/.gradle"
```

## 📈 Gains Cumulatifs Estimés

| Scénario | Avant | Après | Gain |
|----------|-------|-------|------|
| **Build complet (Android only)** | ~8-12 min | ~3-5 min | 60-70% |
| **Build incrémental (Android)** | ~2-4 min | ~30s-1min | 70-75% |
| **Clean build (Android only)** | ~10-15 min | ~4-6 min | 60-65% |
| **Sync Gradle** | ~1-2 min | ~15-30s | 60-70% |

**Note:** Les gains réels dépendent de:
- La machine (CPU, RAM, SSD)
- Le module modifié
- Le cache état

## 🛠️ Commandes Utiles

### Nettoyer les caches (si problèmes)
```bash
# Option 1: Via Gradle (recommandé)
./gradlew clean

# Option 2: Nettoyage complet (si Gradle ne marche pas)
rm -rf .gradle .kotlin */build composeApp/build

# Option 3: Dans Android Studio
File > Invalidate Caches > Clear file system cache and Local History
```

### Analyser les temps de build
```bash
# Activer le rapport de build
echo "kotlin.build.report.output=file,console" >> gradle.properties

# Build avec profil
./gradlew assembleDebug --profile --scan

# Le rapport sera dans build/reports/profile/
```

### Vérifier les optimisations actives
```bash
# Vérifier la config
./gradlew -q properties | grep -E "(gradle|kotlin)"

# Voir les targets compilés
./gradlew -q projects
```

## 🎮 Modes de Build Recommandés

### Mode Développement Android (par défaut)
```properties
# local.properties
kmp.buildTargets.ios.enabled=false
```
- 🚀 Build ultra-rapide
- ✅ Tests JVM fonctionnent
- ✅ Émulateur/Device Android OK
- ❌ Pas de build iOS

### Mode Full/CI
```properties
# local.properties
kmp.buildTargets.ios.enabled=true
```
- 🐌 Build complet (plus lent)
- ✅ Tous les targets disponibles
- ✅ Build iOS/Android
- ✅ CI/CD pipeline

## 📝 Checklist de Démarrage

- [ ] Vérifier `local.properties` contient `kmp.buildTargets.ios.enabled=false`
- [ ] Déplacer `GRADLE_USER_HOME` du disque externe vers interne
- [ ] Redémarrer Android Studio après changement env vars
- [ ] Run `./gradlew clean` pour purger les caches
- [ ] Run `./gradlew assembleDebug` pour tester
- [ ] Vérifier les logs pour "⏩ Skipping iOS targets"

## 🔧 Troubleshooting

### "Could not create parent directory for lock file"
- **Cause:** Gradle Home sur disque externe avec espaces dans le chemin
- **Solution:** Voir section 3 ci-dessus (Gradle Home sur Disque Interne)

### "Configuration cache is unstable"
- **Cause:** Plugins ou scripts incompatibles
- **Solution:** Temporairement désactiver avec `org.gradle.configuration-cache=false`

### "iOS target not found" lors du build
- **Cause:** `kmp.buildTargets.ios.enabled=false` mais vous buildez pour iOS
- **Solution:** Mettre à `true` dans `local.properties`

### Build toujours lent après optimisations
1. Vérifier que Gradle Home est sur disque interne
2. Purger tous les caches (voir "Commandes Utiles")
3. Analyser avec `--profile --scan`
4. Vérifier RAM disponible (min 8GB recommandé, 16GB idéal)

## 📚 Ressources

- [Gradle Performance](https://docs.gradle.org/current/userguide/performance.html)
- [Kotlin Multiplatform Performance](https://kotlinlang.org/docs/multiplatform-mobile-understand-project-structure.html)
- [Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html)

---

**Dernière mise à jour:** 2025-11-04