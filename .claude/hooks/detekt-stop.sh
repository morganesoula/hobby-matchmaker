#!/bin/bash
# Stop: run Detekt once on the Kotlin files edited during this turn, scoped to their modules.
input=$(cat)
session=$(jq -r '.session_id // "default"' <<<"$input")
retry=$(jq -r '.stop_hook_active // false' <<<"$input")
pending="${TMPDIR:-/tmp}/claude-detekt-$session"
[ -s "$pending" ] || exit 0

files=()
while IFS= read -r f; do [ -f "$f" ] && files+=("$f"); done < <(sort -u "$pending")
[ ${#files[@]} -gt 0 ] || { rm -f "$pending"; exit 0; }

cd "$CLAUDE_PROJECT_DIR" || exit 0
# Module of each file = path before /src/, e.g. features/movies/presentation
modules=$(printf '%s\n' "${files[@]}" | sed -E "s#^$CLAUDE_PROJECT_DIR/##; s#/src/.*##" | sort -u | paste -sd, -)
out=$(./gradlew -q detektAll -PdetektFiles="$(IFS=,; echo "${files[*]}")" 2>&1)
status=$?
rm -f "$pending"
[ $status -eq 0 ] && exit 0

issues=$(echo "$out" | grep -E '\.kt:[0-9]+' | sed "s#$CLAUDE_PROJECT_DIR/##" | head -40)
[ -n "$issues" ] || issues=$(echo "$out" | tail -30)

if [ "$retry" = "true" ]; then
  # Already blocked once this turn: don't loop, just tell the user.
  jq -n --arg m "Detekt still reports issues in [$modules]:"$'\n'"$issues" '{systemMessage: $m}'
  exit 0
fi

# Re-queue the files so the fix pass gets re-checked on the next Stop.
printf '%s\n' "${files[@]}" > "$pending"
echo "Detekt issues in modules [$modules] — fix them:" >&2
echo "$issues" >&2
exit 2
