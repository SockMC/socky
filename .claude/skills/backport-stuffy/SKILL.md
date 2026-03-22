---
name: backport-stuffy
description: Backport a newly added StuffyBlock from main to older version branches (1.21.1, 1.20.1). Adapts Java API calls, data file paths, and JSON syntax to match each branch's conventions. Invoked via "/backport_stuffy" or auto-triggered when the user requests to make a new stuffy compatible with other supported versions.
---

# Skill: Backport StuffyBlock to Older Branches

## Overview

The `main` branch targets MC 1.21.4+. The `1.21.1` and `1.20.1` branches both require adaptations in Java code, data file paths, and JSON syntax, though not all adaptations apply to both branches — see each section for details.

All examples throughout this skill use `pushpush` as a stand-in for the actual new stuffy. Substitute the real name, constant, and identifier throughout.

## Step 1 — Identify the new stuffy

On `main`, find the most recently added StuffyBlock by checking `git log` for the newest commit that adds a block. Identify:
- Its **name** (e.g., `pushpush`)
- Its **constant name** in `Blocks.java` (e.g., `PUSHPUSH_BLOCK`)
- Its **Identifier** field in `Ids.java` (e.g., `PUSHPUSH`)
- Its **hit box** `Block.createCuboidShape(...)` arguments
- All files added on `main` for it (recipe, loot table, blockstate, block model, item model, item definition)

## Step 2 — Merge main into each older branch

For each of `1.21.1` and `1.20.1` in turn:

```bash
git checkout 1.21.1   # (then repeat for 1.20.1)
git merge main --no-commit --no-ff
```

Resolve the conflicts and make the following adaptations before committing.

---

## Adaptations for older branches (1.21.1 and 1.20.1)

### A. `Ids.java` — Identifier constructor (1.20.1 only)

`1.21.1` uses `Identifier.of(...)` and needs no change here. On `1.20.1` only:

**Change to:**
```java
public static final Identifier PUSHPUSH = new Identifier(MOD_ID, "pushpush");
```

### B. `Blocks.java` — no changes needed

All branches use the same 4-argument `register()` call site. The block field declaration and the `ItemGroupEvents` entry in `initialize()` both merge in cleanly from `main`.

### C. `Items.java` — no changes needed

Block items are handled inside `Blocks.register()` on all branches. Nothing to add here.

### D. Recipe files — path and JSON syntax

**Path change:** `data/socky/recipe/` → `data/socky/recipes/` (plural)

**Shaped recipe — key values:** Change item reference strings to objects:
```json
// main:
"key": { "#": "minecraft:light_gray_wool" }

// older branches:
"key": { "#": { "item": "minecraft:light_gray_wool" } }
```

**Shaped/shapeless recipe — result field:** Change `"id"` to `"item"`:
```json
// main:
"result": { "id": "socky:pushpush", "count": 1 }

// older branches:
"result": { "item": "socky:pushpush", "count": 1 }
```

**Shapeless recipe — ingredients:** Change strings to objects:
```json
// main:
"ingredients": ["socky:foo", "socky:bar"]

// older branches:
"ingredients": [{ "item": "socky:foo" }, { "item": "socky:bar" }]
```

### E. Loot table files — keep as-is

Both older branches use `data/socky/loot_table/blocks/NAME.json` (same path as `main`). Keep the file brought in by the merge unchanged.

### F. Item definition files — do not port

Older branches have no `assets/socky/items/` directory. **Delete** `assets/socky/items/NAME.json` if it was brought in by the merge; item rendering is driven by `models/item/NAME.json` alone on these branches.

### G. `gradle.properties` — resolve version conflict

The merge will conflict here because each branch tracks a different MC version. Only modify `mod_version` to match any increment made on `main` (i.e. keep minor versions and alpha versions in lock-step, but keep version suffixes unique to each MC version targeted). Leave all other dependency versions unchanged.

### H. `lang/en_us.json` — no changes needed

The display name entry (e.g., `"block.socky.pushpush": "Push Push"`) is identical across all branches and merges in cleanly.

### I. Asset files — no changes needed

`assets/socky/blockstates/NAME.json`, `assets/socky/models/block/NAME.json`, `assets/socky/models/item/NAME.json`, and `assets/socky/textures/` files are identical across all branches. Keep them as-is from the merge.

---

## Step 3 — Commit

After making the above adaptations on each branch:

```bash
git add <files>
git commit
```

Use the same commit message style as main (e.g., `Add <name> stuffy`).

## Step 4 — Verify

On each older branch, run `./gradlew build` to confirm the mod compiles cleanly.
