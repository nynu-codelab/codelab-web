# Motion Spec: NYNU Code Lab Logo Animation

## Brand Analysis

**Logo Structure:**
- **Mark**: Rounded rectangle terminal prompt with `>_` inside + yellow accent dot
- **Wordmark**: "NYNU" (cyan) + "Code Lab" (white)
- **Icon variant**: Smaller square terminal mark (bottom-right corner)

**Semantic Parts (Animation Actors):**
1. `#mark-frame` — Terminal rectangle outline (rounded corners)
2. `#mark-chevron` — The `>` chevron inside the terminal
3. `#mark-underscore` — The `_` cursor/underscore
4. `#dot` — Yellow accent dot
5. `#wordmark-nynu` — "NYNU" text
6. `#wordmark-code` — "Code Lab" text

## Personality

**Brand Words:** *swift, precise, confident*

**Energy/Tone Position:** High energy, Serious-to-mid tone
- The terminal/code aesthetic suggests engineered precision
- Tech lab identity calls for technical confidence, not playfulness
- Geometric shapes → engineered motion

**Personality Preset:** Energetic / Bold (adapted for tech precision)
```css
--p2m-duration: 1500ms;
--p2m-ease-enter: cubic-bezier(0.16, 1, 0.3, 1);      /* explosive out */
--p2m-ease-settle: cubic-bezier(0.34, 1.56, 0.64, 1);
--p2m-squash: 0.10;
--p2m-overshoot: 1.06;
```

## Usage Context

Splash/intro reveal (1500ms total), landing on static final state.

## Choreography Sketch

**Reveal Pattern:** Staggered Assembly + Draw-On hybrid

1. **Anticipation (0-300ms / 0-20%)**: 
   - Blank screen, slight scale-down of mark position
2. **Action (300-1050ms / 20-70%)**:
   - Terminal frame draws on (stroke reveal, left-to-right)
   - Chevron appears with scale-pop
   - Underscore blinks in (cursor effect)
   - Yellow dot bounces in with overshoot
   - "NYNU" slides up from below
   - "Code Lab" follows with stagger
3. **Follow-through (1050-1500ms / 70-100%)**:
   - All parts settle to final positions
   - Brief cursor blink (secondary action)

**Part Timing (stagger by 8-12% of part duration):**
- Frame: starts at 0ms
- Chevron: +120ms
- Underscore: +200ms  
- Dot: +350ms
- "NYNU": +450ms
- "Code Lab": +600ms

## Timeline Table

| Part | Start | Duration | Easing | Properties |
|------|-------|----------|--------|------------|
| Frame stroke | 300ms | 700ms | natural ease | stroke-dashoffset 1→0 |
| Chevron | 420ms | 400ms | ease-enter | scale 0→1, opacity |
| Underscore | 500ms | 300ms | ease-enter | opacity blink |
| Dot | 650ms | 450ms | ease-settle | scale bounce, opacity |
| NYNU | 750ms | 500ms | ease-enter | translateY, opacity |
| Code Lab | 900ms | 500ms | ease-enter | translateY, opacity |
| Cursor blink | 1100ms | 400ms | ease | opacity pulse |

## Easing Tokens

```css
--p2m-ease-enter: cubic-bezier(0.16, 1, 0.3, 1);
--p2m-ease-settle: cubic-bezier(0.34, 1.56, 0.64, 1);
--p2m-ease-natural: cubic-bezier(0.4, 0, 0.2, 1);
```

## Reduced Motion

Under `prefers-reduced-motion: reduce`, show final static logo immediately.
