# Configuration de Cloudflare Workers comme Proxy TMDB

Ce guide explique comment configurer Cloudflare Workers pour sécuriser votre clé API TMDB. Au lieu d'exposer la clé dans votre application mobile, les requêtes passent par un Worker Cloudflare qui injecte la clé côté serveur.

## Pourquoi utiliser un proxy ?

- **Sécurité** : La clé API n'est jamais exposée dans le code client (APK/IPA)
- **Contrôle** : Possibilité d'ajouter du rate limiting, du caching, ou des filtres
- **Révocation simplifiée** : Changez la clé côté serveur sans mettre à jour l'app
- **Gratuit** : Cloudflare Workers offre 100 000 requêtes/jour gratuitement

## Prérequis

- Un compte Cloudflare (gratuit) : https://dash.cloudflare.com/sign-up
- Une clé API TMDB : https://www.themoviedb.org/settings/api
- Node.js installé (pour Wrangler CLI)

## Étape 1 : Créer un compte Cloudflare

1. Allez sur https://dash.cloudflare.com/sign-up
2. Créez un compte avec votre email ou réseau social
3. Dans le menu de gauche, Compute & AI > Workers & Pages
4. Cliquez sur le bouton "Create Application" en haut à droite
5. Sélectionnez "Start with Hello World" ou sur un template de votre choix
6. Choisissez un nom de sous-domaine (il s'auto-complètera avec votre pseudo.workers.dev) puis cliquez sur "Deploy"

## Étape 2 : Installer Wrangler CLI

Wrangler est l'outil CLI officiel de Cloudflare pour gérer les Workers.

```bash
# Installation globale
npm install -g wrangler

# Vérifier l'installation
wrangler --version

# Se connecter à Cloudflare
wrangler login
```

Cette commande ouvrira votre navigateur pour autoriser Wrangler.

## Étape 3 : Créer le projet Worker

```bash
# Créer un nouveau projet
mkdir tmdb-proxy && cd tmdb-proxy

# Initialiser le projet
wrangler init tmdb-proxy

# Choisir les options :
# - Type: "Hello World" Worker
# - Language: TypeScript (recommandé) ou JavaScript
# - Git: selon votre préférence
```

## Étape 4 : Configurer le Worker

### 4.1 Configuration `wrangler.toml`

Modifiez le fichier `wrangler.toml` :

```toml
name = "tmdb-proxy"
main = "src/index.ts"
compatibility_date = "2024-01-01"

# Variables d'environnement (NON sensibles)
[vars]
TMDB_BASE_URL = "https://api.themoviedb.org/3"
```

### 4.2 Ajouter la clé API comme secret

**Ne jamais mettre la clé API dans le code ou wrangler.toml !**

```bash
# Ajouter le secret (vous serez invité à entrer la valeur)
wrangler secret put TMDB_API_KEY
# Entrez votre clé API TMDB quand demandé
```

### 4.3 Code du Worker

Remplacez le contenu de `src/index.ts` :

```typescript
interface Env {
  TMDB_API_KEY: string;
  TMDB_BASE_URL: string;
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    // CORS headers pour permettre les appels depuis l'app
    const corsHeaders = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization",
    };

    // Gérer les requêtes OPTIONS (preflight CORS)
    if (request.method === "OPTIONS") {
      return new Response(null, { headers: corsHeaders });
    }

    try {
      const url = new URL(request.url);

      // Extraire le chemin après le domaine du worker
      // Ex: /movie/popular -> https://api.themoviedb.org/3/movie/popular
      const tmdbPath = url.pathname;

      // Construire l'URL TMDB avec la clé API
      const tmdbUrl = new URL(`${env.TMDB_BASE_URL}${tmdbPath}`);

      // Copier les query params de la requête originale
      url.searchParams.forEach((value, key) => {
        tmdbUrl.searchParams.set(key, value);
      });

      // Ajouter la clé API
      tmdbUrl.searchParams.set("api_key", env.TMDB_API_KEY);

      // Faire la requête à TMDB
      const tmdbResponse = await fetch(tmdbUrl.toString(), {
        method: request.method,
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
      });

      // Récupérer la réponse
      const data = await tmdbResponse.text();

      // Retourner la réponse avec les headers CORS
      return new Response(data, {
        status: tmdbResponse.status,
        headers: {
          ...corsHeaders,
          "Content-Type": "application/json",
          // Cache pendant 5 minutes pour réduire les appels
          "Cache-Control": "public, max-age=300",
        },
      });
    } catch (error) {
      return new Response(
        JSON.stringify({
          error: "Proxy error",
          message: error instanceof Error ? error.message : "Unknown error"
        }),
        {
          status: 500,
          headers: {
            ...corsHeaders,
            "Content-Type": "application/json",
          },
        }
      );
    }
  },
};
```

## Étape 5 : Tester localement

```bash
# Lancer le worker en local
wrangler dev

# Le worker sera accessible sur http://localhost:8787
# Testez avec :
curl "http://localhost:8787/movie/popular?language=fr-FR"
```

## Étape 6 : Déployer

```bash
# Déployer sur Cloudflare
wrangler deploy

# Vous obtiendrez une URL comme :
# https://tmdb-proxy.<votre-sous-domaine>.workers.dev
```

## Étape 7 : Configurer l'application

### Android/KMP

Dans votre fichier `secrets.properties` :

```properties
# Remplacez par votre URL de Worker
TMDB_PROXY_URL=https://tmdb-proxy.votre-sous-domaine.workers.dev
```

Dans le code Ktor (exemple) :

```kotlin
// Avant (exposait la clé)
// private const val BASE_URL = "https://api.themoviedb.org/3"
// httpClient.get("$BASE_URL/movie/popular?api_key=$API_KEY")

// Après (sécurisé via proxy)
private const val PROXY_URL = BuildConfig.TMDB_PROXY_URL

suspend fun getPopularMovies(): List<Movie> {
    return httpClient.get("$PROXY_URL/movie/popular") {
        parameter("language", "fr-FR")
        parameter("page", 1)
    }.body()
}
```

### iOS

Dans `secrets.xcconfig` :

```
TMDB_PROXY_URL = https://tmdb-proxy.votre-sous-domaine.workers.dev
```

## Fonctionnalités avancées (optionnel)

### Rate Limiting

Ajoutez un rate limiting pour éviter les abus :

```typescript
// Dans wrangler.toml, ajoutez :
// [[unsafe.bindings]]
// name = "RATE_LIMITER"
// type = "ratelimit"

// Dans le code, vérifiez avant de proxy
const { success } = await env.RATE_LIMITER.limit({ key: clientIP });
if (!success) {
  return new Response("Rate limit exceeded", { status: 429 });
}
```

### Cache avec KV

Pour un cache plus persistant :

```bash
# Créer un namespace KV
wrangler kv:namespace create "TMDB_CACHE"

# Ajouter dans wrangler.toml
# [[kv_namespaces]]
# binding = "TMDB_CACHE"
# id = "<id-retourné>"
```

### Authentification basique

Pour restreindre l'accès au proxy :

```typescript
// Vérifier un header d'authentification
const authHeader = request.headers.get("X-App-Token");
if (authHeader !== env.APP_SECRET_TOKEN) {
  return new Response("Unauthorized", { status: 401 });
}
```

## Monitoring

Consultez les métriques dans le dashboard Cloudflare :
1. Allez sur https://dash.cloudflare.com
2. Workers & Pages > tmdb-proxy
3. Onglet "Metrics" pour voir les requêtes, erreurs, latence

## Coûts

| Plan | Requêtes/jour | Prix |
|------|---------------|------|
| Free | 100 000 | Gratuit |
| Paid | 10 millions+ | $5/mois + $0.50/million |

Pour la plupart des applications en développement, le plan gratuit est largement suffisant.

## Dépannage

### Erreur 401 Unauthorized
- Vérifiez que le secret `TMDB_API_KEY` est bien configuré : `wrangler secret list`
- Vérifiez que la clé TMDB est valide sur https://www.themoviedb.org/settings/api

### Erreur CORS
- Vérifiez que les headers CORS sont bien présents dans la réponse
- Testez d'abord avec `curl` pour isoler le problème

### Worker non accessible
- Vérifiez le déploiement : `wrangler deployments list`
- Consultez les logs : `wrangler tail`

## Ressources

- [Documentation Cloudflare Workers](https://developers.cloudflare.com/workers/)
- [API TMDB](https://developer.themoviedb.org/docs)
- [Wrangler CLI](https://developers.cloudflare.com/workers/wrangler/)
