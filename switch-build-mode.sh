#!/bin/bash

# Script pour basculer entre les modes de build Android-only et Full (iOS+Android)
# Usage: ./switch-build-mode.sh [android|full|status]

set -e

LOCAL_PROPS="local.properties"
IOS_ENABLED_KEY="kmp.buildTargets.ios.enabled"

show_status() {
    if grep -q "^${IOS_ENABLED_KEY}=false" "$LOCAL_PROPS" 2>/dev/null; then
        echo "🚀 Mode actuel: ANDROID-ONLY (build rapide)"
        echo "   iOS targets sont désactivés"
    elif grep -q "^${IOS_ENABLED_KEY}=true" "$LOCAL_PROPS" 2>/dev/null; then
        echo "🐌 Mode actuel: FULL BUILD (Android + iOS)"
        echo "   Tous les targets sont activés"
    else
        echo "⚠️  Mode non configuré, utilise le défaut (Full Build)"
        echo "   Exécutez './switch-build-mode.sh android' pour optimiser"
    fi
}

set_android_mode() {
    echo "🚀 Activation du mode ANDROID-ONLY (build rapide)..."

    # Supprimer l'ancienne ligne si elle existe
    sed -i.bak "/${IOS_ENABLED_KEY}/d" "$LOCAL_PROPS" 2>/dev/null || true

    # Ajouter la nouvelle configuration
    echo "" >> "$LOCAL_PROPS"
    echo "# Build Performance Optimization" >> "$LOCAL_PROPS"
    echo "${IOS_ENABLED_KEY}=false" >> "$LOCAL_PROPS"

    echo "✅ Mode ANDROID-ONLY activé"
    echo ""
    echo "Gains attendus:"
    echo "  • 40-60% de temps de build en moins"
    echo "  • ~300 au lieu de ~943 bibliothèques transformées"
    echo "  • Compilation iOS complètement skippée"
    echo ""
    echo "⚠️  Pour builder iOS, exécutez: ./switch-build-mode.sh full"
}

set_full_mode() {
    echo "🔄 Activation du mode FULL BUILD (Android + iOS)..."

    # Supprimer l'ancienne ligne si elle existe
    sed -i.bak "/${IOS_ENABLED_KEY}/d" "$LOCAL_PROPS" 2>/dev/null || true

    # Ajouter la nouvelle configuration
    echo "" >> "$LOCAL_PROPS"
    echo "# Build Performance - Full Build Mode" >> "$LOCAL_PROPS"
    echo "${IOS_ENABLED_KEY}=true" >> "$LOCAL_PROPS"

    echo "✅ Mode FULL BUILD activé"
    echo ""
    echo "⚠️  Attention:"
    echo "  • Builds seront plus lents (iOS compilé)"
    echo "  • Nécessaire pour tester/builder iOS"
    echo "  • ~943 bibliothèques seront transformées"
    echo ""
    echo "💡 Pour développement Android quotidien: ./switch-build-mode.sh android"
}

clean_build() {
    echo "🧹 Nettoyage des caches de build..."
    ./gradlew clean || echo "⚠️  Gradle clean a échoué, continuez manuellement"
    echo "✅ Nettoyage terminé"
}

show_help() {
    echo "Usage: ./switch-build-mode.sh [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  android    Active le mode ANDROID-ONLY (recommandé, build rapide)"
    echo "  full       Active le mode FULL BUILD (Android + iOS)"
    echo "  status     Affiche le mode actuel"
    echo "  help       Affiche cette aide"
    echo ""
    echo "Examples:"
    echo "  ./switch-build-mode.sh android    # Mode développement quotidien"
    echo "  ./switch-build-mode.sh full       # Avant de builder iOS"
    echo "  ./switch-build-mode.sh status     # Vérifier le mode actif"
}

# Main
case "${1:-status}" in
    android)
        set_android_mode
        echo ""
        echo "💡 Recommandation: Nettoyez les caches avec './gradlew clean'"
        read -p "Voulez-vous nettoyer maintenant? (y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            clean_build
        fi
        ;;
    full)
        set_full_mode
        echo ""
        echo "💡 Recommandation: Nettoyez les caches avec './gradlew clean'"
        read -p "Voulez-vous nettoyer maintenant? (y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            clean_build
        fi
        ;;
    status)
        show_status
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "❌ Commande inconnue: $1"
        echo ""
        show_help
        exit 1
        ;;
esac