---
name: ordering-modifier-chains
description: Use this skill to diagnose and fix Jetpack Compose Modifier ordering bugs — wrong paint region for background, wrong click area for clickable, wrong clipping for clip, wrong measurement for padding/size, surprising graphicsLayer scope. Covers the wrap-the-next-modifier mental model, the canonical pitfalls (padding vs background, clickable placement, clip before background, graphicsLayer placement), and why hoisting an entire Modifier chain via remember { Modifier.… } is rarely a real perf win because identical chains already reuse their modifier nodes. Use when the developer asks "why does the click area extend past the visible button", "why is my background painted in the wrong place", "does Modifier order matter", "should I cache my Modifier chain", or reviews a diff that reorders modifiers.
license: Apache-2.0. See LICENSE for complete terms.
metadata:
  author: Jaewoong Eum (skydoves)
  keywords:
  - jetpack-compose
  - performance
  - modifier-order
  - clickable
  - clip
  - background
  - padding
  - graphics-layer
  - touch-target
---

# Ordering Modifier Chains — Why `padding(8.dp).background(Red)` ≠ `background(Red).padding(8.dp)`

Modifier order matters because each modifier wraps the next as a function. `Modifier.padding(8.dp).background(Red)` shrinks constraints first, so the background paints INSIDE the padded region — the surrounding 8 dp margin has no red. Conversely, `Modifier.background(Red).padding(8.dp)` paints red across the parent's full bounds before insetting the content. Wrong order is the most common Compose UI bug after missing keys. This skill teaches Claude how to read a chain top-to-bottom and reorder it correctly.

## When to use this skill

- The developer asks "why does the click area extend past the visible button?", "why is my ripple bouncing on the wrong shape?", "why is the background painted in the wrong area?", or "does `Modifier` order matter?".
- A code review shows a chain like `Modifier.padding(...).clickable { }`, `Modifier.background(...).clip(...)`, or `Modifier.alpha(state.value).graphicsLayer { }`.
- A button's tap target leaks into the surrounding padding, or the corner radius is not honored on the background.
- A clip on a parent is failing to mask a child draw, or `graphicsLayer { alpha = 0.5f }` is half-applying to siblings.
- The developer is considering hoisting a `Modifier` chain via `remember { Modifier.… }` "for perf".

## When NOT to use this skill

- The chain order is correct and the symptom is elsewhere (state read in the wrong phase, missing keys in a lazy layout) — fix that instead.
- The custom modifier itself is the problem (`composed { }` legacy, missing diff) → migrate it to `Modifier.Node`.
- The question is whether to make a custom modifier — this skill assumes you already have a chain of built-ins and need to order them right.

## Prerequisites

- Basic familiarity with `Modifier` composition vocabulary (`padding`, `background`, `clip`, `clickable`, `graphicsLayer`).
- Compose UI any supported version — modifier ordering semantics have been stable since 1.0.
- A real-device, release build for any final perf claim about hoisting (skydoves hot take #5: debug builds lie).

## Workflow

- [ ] **1. Identify the symptom precisely.** Match it to one of: paint region wrong, click / pointer area wrong, ripple bounds wrong, clip area wrong, padding measurement wrong, `graphicsLayer` scope wrong. Each maps to a specific reorder.

- [ ] **2. Read the chain top-to-bottom — each modifier's effect applies to everything BELOW it.** Treat the chain as nested function calls: `Modifier.a().b().c()` means "a wraps (b wraps (c))". The top modifier is the outermost wrapper; the bottom modifier is closest to the content.

- [ ] **3. Apply the canonical reorder for the symptom.** The patterns below cover the four most common cases (`padding` vs `background`, `clickable` placement, `clip` before `background`, `graphicsLayer` scope).

- [ ] **4. Pay special attention to these modifiers — order is observable, not stylistic:**
    - `clickable` — the pointer hit area is the bounds AT ITS POSITION in the chain. Anything ABOVE it (outer) is included; anything BELOW it (inner) is not.
    - `clip` — clips everything BELOW it (inner). Anything ABOVE it is unclipped.
    - `background` — paints across the bounds AT ITS POSITION in the chain.
    - `padding` — subtracts from the available space passed to everything BELOW it.
    - `graphicsLayer` — wraps everything BELOW it in a render layer (alpha, transforms, clipping all apply to the subtree under it).

- [ ] **5. Decide on touch-target accessibility.** Material's 48dp minimum touch target is achieved by placing `clickable` ABOVE the visual padding so the padded area is tappable. The trade-off: ripples will also fire in the padding. For visual-area-only clicks, `clickable` goes BELOW `padding`.

- [ ] **6. Resist hoisting a `Modifier` chain via `remember` "for perf".** Built-in modifiers implement `equals`, so a freshly built but identical chain lets Compose reuse the existing modifier nodes on recomposition; `remember { Modifier.fillMaxWidth().padding(16.dp) }` is a micro-optimization that rarely shows up in a profile. **MUST NOT** add such a `remember` without first proving with a `FrameTimingMetric` benchmark that the unhoisted chain measurably regresses.

## Patterns

### Pattern: background OUTSIDE vs INSIDE padding

```kotlin
// WRONG (when the intent is "padded box with red fill")
Box(Modifier.padding(8.dp).background(Color.Red))
// WRONG because: padding is applied first; background then paints across the inner (padded) bounds — the result is a smaller red region, with no red in the padded margin. If the design intent was "red card with 8dp internal padding", red is on the wrong side of the padding.
```

```kotlin
// RIGHT — red fills the box; content is inset by 8dp.
Box(Modifier.background(Color.Red).padding(8.dp)) { /* content */ }
```

The reverse is also a valid composition — but only when the developer specifically wants the red to NOT extend into the surrounding padding (e.g. a small inner badge). Default to `background → padding` for "card with internal padding".

### Pattern: clickable ABOVE vs BELOW padding (touch target vs visual area)

```kotlin
// WRONG (when the click should fire only on the visible content)
Box(Modifier.clickable { onTap() }.padding(16.dp))
// WRONG because: clickable is ABOVE (outside) the padding, so its hit region is the full bounds
// INCLUDING the 16dp padding. Taps in the padding fire onTap and the ripple bounces past the visible content.
```

```kotlin
// RIGHT — padding is outside clickable, so the click only fires on the inner (visible) content area.
Box(Modifier.padding(16.dp).clickable { onTap() })
```

```kotlin
// RIGHT — accessibility 48dp touch target: extend the tap area into the padding intentionally.
Box(
    Modifier
        .padding(8.dp)            // outer spacing
        .clickable { onTap() }    // tap fires across the next size step
        .padding(16.dp)           // visual padding inside the tap area
)
```

The 48dp minimum touch target (Material) is achieved by deliberately placing `clickable` so that it sits OUTSIDE the visual padding but INSIDE any external spacing. Pick whichever matches the design intent — neither order is "more correct"; they describe different products.

### Pattern: clip BEFORE vs AFTER background

```kotlin
// WRONG (rounded card with red fill)
Box(Modifier.background(Color.Red).clip(RoundedCornerShape(8.dp)))
// WRONG because: background paints first across square bounds; clip then applies to children only. The red rectangle is unclipped — corners stay square.
```

```kotlin
// RIGHT — clip wraps the background, so the red is rounded.
Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Red))
```

For modifiers that combine clip + background semantics, `Modifier.background(color = ..., shape = ...)` does both in one node and avoids the ordering question:

```kotlin
// RIGHT — single modifier handles shape clipping and fill atomically.
Box(Modifier.background(Color.Red, RoundedCornerShape(8.dp)))
```

### Pattern: graphicsLayer wraps everything BELOW it

```kotlin
// RIGHT — alpha applies to the entire subtree (background + content).
Box(
    Modifier
        .graphicsLayer { alpha = 0.5f }
        .background(Color.Red)
        .padding(8.dp)
) { Text("Half-faded card") }
```

```kotlin
// WRONG — alpha only fades the content; background is fully opaque.
Box(
    Modifier
        .background(Color.Red)
        .padding(8.dp)
        .graphicsLayer { alpha = 0.5f }
) { Text("Surprising opacity") }
// WRONG because: graphicsLayer wraps only what's below it; the background painted above is not in its layer.
```

This is the most common "why is my fade weird" bug after wrong-phase animation reads. Place `graphicsLayer` at (or near) the top of the chain when the intent is "fade this entire visual unit".

### Pattern: clickable inside graphicsLayer (the hit-test surprise)

```kotlin
// RIGHT — graphicsLayer { } wraps clickable; ripple is captured into the layer.
Box(
    Modifier
        .graphicsLayer { alpha = 0.9f }
        .clickable { onTap() }
        .padding(16.dp)
)
```

`graphicsLayer` alpha does NOT change hit-test geometry — `clickable` hit-tests against its own position in the chain. Transforms (translation, scale, rotation) set in a `graphicsLayer` ABOVE `clickable` ARE applied to pointer coordinates, so the tappable area follows the transformed visual. A transform placed BELOW `clickable` only moves the visuals, leaving the tap area at the untransformed bounds.

### Pattern: hoisting a Modifier chain — usually not a win

```kotlin
// OK but rarely necessary
@Composable
fun SnackList(snacks: ImmutableList<Snack>) {
    val rowModifier = remember { Modifier.fillMaxWidth().padding(16.dp) }
    LazyColumn {
        items(snacks, key = { it.id }) { snack -> SnackRow(snack, rowModifier) }
    }
}
```

```kotlin
// Equally fine in practice
@Composable
fun SnackList(snacks: ImmutableList<Snack>) {
    LazyColumn {
        items(snacks, key = { it.id }) { snack ->
            SnackRow(snack, Modifier.fillMaxWidth().padding(16.dp))
        }
    }
}
```

Built-in modifiers are compared with `equals`, so the two snippets above produce `equals`-compatible chains and Compose reuses the existing nodes either way. Hoisting via `remember` saves at most a single allocation per item per recomposition — measurable in synthetic micro-benchmarks, invisible in `FrameTimingMetric` on real surfaces. **MUST NOT** add the `remember` line "for perf" without a `FrameTimingMetric` regression to point at. The much bigger win is usually migrating a `Modifier.composed { }` factory to `Modifier.Node`.

### Pattern: padding then size — the unmeasurable order

```kotlin
// Both compile; both are valid; they mean different things.
Box(Modifier.size(100.dp).padding(8.dp))   // 100dp box, 8dp inner padding → 84dp content area
Box(Modifier.padding(8.dp).size(100.dp))   // 100dp content area, 8dp outer margin → 116dp total
```

`size` constrains the bounds at its position in the chain; `padding` subtracts from the available space passed to everything below. Read top-to-bottom: the order describes the layout, it does not "do the same thing in a different order".

## Mandatory rules

- **MUST** read modifier chains top-to-bottom and reason about each modifier as a wrapper around everything below it.
- **MUST** place `clickable` AFTER `padding` (padding then clickable, where clickable is below padding in the chain) when the click should be the visible area only. Place `clickable` BEFORE `padding` when the design requires the padded area to be tappable (Material 48dp touch target).
- **MUST** place `clip` BEFORE `background` to clip the background paint. Or use `Modifier.background(color, shape)` to avoid the ordering question entirely.
- **MUST** place `graphicsLayer` near the top of the chain when its transforms / alpha must apply to the whole visual unit (including the background).
- **MUST NOT** hoist a `Modifier` chain via `remember { Modifier.… }` solely "for perf" without a `FrameTimingMetric` regression to point at — identical chains already reuse their modifier nodes and the saved allocation rarely shows up in a profile.
- **MUST NOT** add a `Modifier.composed { }` factory to "fix" an ordering issue. If a custom modifier needs to be in a specific position, it can be a `Modifier.Node` directly.
- **PREFERRED:** explicit `padding → clickable → padding` sandwich for tappable items where accessibility (48dp minimum) is the goal — outer spacing, the clickable area sized to include the visual padding, and inner padding for the content.
- **PREFERRED:** `Modifier.background(color = ..., shape = ...)` over the `clip(shape).background(color)` pair when both are needed.

## Verification

- [ ] The painted region (background, border) matches the design intent — no surprise red bleed past the corner radius, no red inside an unintended padding margin.
- [ ] The click / ripple area matches the design intent — taps in the padding fire (or do not fire) deliberately, not by accident.
- [ ] `clip` masks every layer below it; clipped corners look right on every theme.
- [ ] `graphicsLayer { alpha = ... }` (or `scaleX`, `rotationZ`, etc.) affects the intended subtree only; background and content fade together when expected.
- [ ] For any hoisted `Modifier` chain, a `FrameTimingMetric` macrobenchmark in **release + R8 + real device** shows a measurable improvement vs the unhoisted version. If not, drop the `remember`.
- [ ] No `Modifier.composed { }` was added to work around a chain-order question (re-grep the module).

## References

- Android Developers — Modifiers: https://developer.android.com/develop/ui/compose/modifiers
- Android Developers — Custom modifiers (Modifier.Node): https://developer.android.com/develop/ui/compose/custom-modifiers
- Android Developers — Graphics modifiers: https://developer.android.com/develop/ui/compose/graphics/draw/modifiers
- Android Developers — Phases of Jetpack Compose: https://developer.android.com/develop/ui/compose/phases
- Android Developers — Performance overview: https://developer.android.com/develop/ui/compose/performance
- Ben Trengrove — Debugging recomposition: https://medium.com/androiddevelopers/jetpack-compose-debugging-recomposition-bfcf4a6f8d37
- Chris Banes — Compose performance tag: https://chrisbanes.me/tags/jetpack-compose-performance/
- skydoves — 6 Jetpack Compose Guidelines: https://medium.com/proandroiddev/6-jetpack-compose-guidelines-to-optimize-your-app-performance-be18533721f9
- skydoves — compose-performance hub: https://github.com/skydoves/compose-performance
