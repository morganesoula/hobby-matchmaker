#!/bin/bash
# PostToolUse: record each Kotlin file Claude edits that belongs to a Gradle module (no Gradle run here).
input=$(cat)
file=$(jq -r '.tool_input.file_path // empty' <<<"$input")
session=$(jq -r '.session_id // "default"' <<<"$input")
[[ "$file" == "$CLAUDE_PROJECT_DIR"/*.kt && "$file" == */src/* && "$file" != */build/* ]] || exit 0

echo "$file" >> "${TMPDIR:-/tmp}/claude-detekt-$session"
exit 0
