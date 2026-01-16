# Kotzilla Configuration

Ce projet utilise un seul fichier `kotzilla.json` centralisé à la racine au lieu d'avoir un fichier par module.

## Comment ça fonctionne

1. **Fichier centralisé** : Un seul fichier `/kotzilla.json` à la racine du projet
2. **Liens symboliques auto-générés** : Au build, des liens symboliques sont automatiquement créés dans chaque module qui utilise Kotzilla
3. **Gitignore** : Le fichier `kotzilla.json` et tous les liens symboliques sont dans `.gitignore` pour protéger votre API key

## Configuration pour nouveaux développeurs

1. Copier `kotzilla.json.example` → `kotzilla.json`
2. Remplacer les valeurs par vos propres clés Kotzilla :
   ```json
   {
     "sdkVersion": "1.3.1",
     "keys": [
       {
         "appId": "YOUR_APP_ID",
         "applicationPackageName": "com.msoula.hobbymatchmaker",
         "keyId": "YOUR_KEY_ID",
         "isDefault": true,
         "apiKey": "YOUR_API_KEY"
       }
     ]
   }
   ```
3. Build le projet - les liens symboliques seront créés automatiquement

## Architecture

Les plugins de convention (`MultiplatformConventionPlugin`, `MultiplatformMinimalistPlugin`, `MultiplatformComposeConventionPlugin`) :
- Détectent si `kotzilla.json` existe à la racine
- Créent automatiquement des liens symboliques dans chaque module
- N'appliquent le plugin Kotzilla que si le fichier existe

## Avantages

✅ Un seul fichier à maintenir
✅ Pas de duplication
✅ Configuration cohérente sur tous les modules
✅ Sécurité : clés API exclues de Git
✅ Setup automatique pour les développeurs