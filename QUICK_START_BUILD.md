# ⚡ Quick Start - Optimiser vos Builds

## 🎯 TL;DR - Accélérez vos builds de 60%

### Option 1: Script automatique (Recommandé)
```bash
./switch-build-mode.sh android
```

### Option 2: Configuration manuelle
Éditez `local.properties` et ajoutez :
```properties
kmp.buildTargets.ios.enabled=false
```

### Effet
- ✅ Builds **40-60% plus rapides**
- ✅ Parfait pour développement Android quotidien
- ✅ Tests JVM fonctionnent normalement
- ⚠️ Désactive temporairement la compilation iOS

## 🔄 Basculer vers mode Full (iOS + Android)
```bash
./switch-build-mode.sh full
```

## 📚 Documentation Complète
Voir [BUILD_OPTIMIZATION.md](BUILD_OPTIMIZATION.md) pour:
- Analyse détaillée des performances
- Toutes les optimisations appliquées
- Troubleshooting
- Commandes avancées

## 🏃 Premiers pas

1. **Configurer le mode Android-only:**
   ```bash
   ./switch-build-mode.sh android
   ```

2. **Nettoyer les anciens caches:**
   ```bash
   ./gradlew clean
   ```

3. **Builder le projet:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Vérifier les logs:** Vous devriez voir :
   ```
   ⏩ Skipping iOS targets for ... (disabled via kmp.buildTargets.ios.enabled)
   ```

## 🎮 Commandes Rapides

```bash
# Vérifier le mode actuel
./switch-build-mode.sh status

# Mode Android rapide
./switch-build-mode.sh android

# Mode Full (iOS + Android)
./switch-build-mode.sh full

# Aide
./switch-build-mode.sh help
```

---

**Note:** Le fichier `local.properties` est git-ignoré. Chaque développeur peut choisir son mode préféré.