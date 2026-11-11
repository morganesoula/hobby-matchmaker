---
name: bug-tracker
description: |
  Log, format and triage bugs for HobbyMatchMaker in GitHub Issues (the repo's tracker). Use this skill whenever the user reports a bug found on a device, says "log this bug", "add this to the tracker", "triage", "what bugs are open", or when a fix should be linked to a known issue. Trigger on phrases like "bug", "issue", "found a problem on device", "track this", "open bugs", or "close the bug".
---

# Bug Tracker — GitHub Issues

Bugs live in GitHub Issues of `morganesoula/hobby-matchmaker`, not in chat or memory files. Keep it small:
one format, a few labels, no process beyond this file.

## Ask first

Creating, editing or closing an issue (and creating labels) is outward-facing. **Show the drafted issue and
wait for a yes** before running `gh`. Reading issues (`gh issue list/view`) needs no confirmation.

## Bug format

Title: `[screen] short symptom` (e.g. `[Profile] guest sees a blank screen`).

Body (same as `.github/ISSUE_TEMPLATE/bug.md`):

```
**Platform / version:** Android | iOS, app version (from Settings, e.g. 1.1.0 build 3)
**Account state:** guest | signed in
**Steps:**
1. ...
**Expected:** ...
**Actual:** ... (exact text of any error message)
**Cause (if known):** file or flow, verified in code only
```

Never put personal data in an issue: no emails, real names or test-account credentials. Use "test account".

## Labels

`bug` always, plus any of: `ui`, `guest`, `android`, `ios`, `blocker` (crash, data loss, or Play policy risk).
Create a missing label only after asking.

## Filing

```
/opt/homebrew/bin/gh issue create --title "[Profile] guest sees a blank screen" --label bug,guest --body-file <file>
```

- `gh` lives in `/opt/homebrew/bin`, which is not on the shell's default PATH: call it by its absolute path, one
  command per call (no `export PATH=...;` prefix or shell variables, they stop the permission rule from matching).
- `gh issue create` and `gh label create` are allowed in `.claude/settings.local.json`; `gh issue close/edit`
  still ask.
- Write the body to the scratchpad directory, never to the repo.

## Fixing

- One bug = one commit when possible. Commit format from CLAUDE.md, with the issue number:
  `fix (scope): message #12 --finished`. No Claude co-author line.
- Closing is done by the user after their device check ("I check after every step"), or by `gh issue close 12`
  once they confirm.

## Triage

`gh issue list --label bug --state open`. Group by `blocker` first, then `guest`/`ui`. Propose an order, one
step per turn, each followed by the user's device check.

## Review

After about 10 bugs, ask the user whether the labels and template still help; trim anything unused.
