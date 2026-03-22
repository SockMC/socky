---
name: create-stuffy
description: Creates all scaffolding for a new StuffyBlock — validates source assets, checks or creates data/asset JSON files, and updates Java registration. Requires at minimum a display name. Invoked via "/create-stuffy" or auto-triggered when the user asks to add or create a new stuffy.
---

# Skill: Create a New Stuffy

## Overview

This skill wires up a new `StuffyBlock` end-to-end. The user must have already placed a `.bbmodel` in source assets and a block model JSON in the assets tree. Everything else is either validated in-place or generated from defaults.

All examples use `mr_test` / `MR_TEST` / `Mr. Test` as a stand-in. Substitute the real values throughout.

---

## Step 0 — Verify branch

Run `git branch --show-current` and check the result.

- **If the current branch is `main`**: proceed normally.
- **If the current branch is anything else**: stop immediately and tell the user:

  > `/create-stuffy` must be run from the `main` branch. You are currently on `<branch>`. Please switch to `main` and re-run the command.

  Do not proceed with any further steps.

---

## Step 1 — Gather inputs

Parse the skill invocation for the following. Ask the user for any that are missing before proceeding.

| Input | Format | Example |
|-------|--------|---------|
| **Display name** | Human-readable, may include spaces and punctuation | `Mr. Test` |
| **Snake-case ID** | Derive from display name (lowercase, spaces→`_`, drop punctuation except `_`). Confirm with user before proceeding. | `mr_test` |


Derived values used throughout:

- **UPPER_SNAKE**: `{snake_id}` uppercased — e.g. `MR_TEST`
- **Constant name**: `{UPPER_SNAKE}_BLOCK` — e.g. `MR_TEST_BLOCK`
- **Lang key**: `block.socky.{snake_id}` — e.g. `block.socky.mr_test`

---

## Step 2 — Validate source assets

Check that both of these files exist:

```
src/main/resources/source_assets/socky/{snake_id}.bbmodel
src/main/resources/assets/socky/models/block/{snake_id}.json
```

If either is missing, stop and tell the user. The `.bbmodel` belongs in `source_assets/socky/` and the block model JSON belongs in `assets/socky/models/block/`. Do not proceed until both are present.

If the block model JSON exists, parse it and confirm it is valid JSON. If it is malformed, report the error and stop.

Also check the `"parent"` field. All StuffyBlock block models should declare:

```json
"parent": "minecraft:block/orientable_with_bottom"
```

If the field is absent or has a different value, warn the user — this may be intentional for an unusual stuffy, but it is worth confirming before proceeding. If the model comes from Blockbench, this can be specified in the project settings prior to export (File → Project → Parent Model).

Also check the `"textures"` field of the block model JSON:

- **Indexed textures**: There should be at least one numeric-key entry (e.g., `"0"`, `"1"`, etc.). Warn if none are present.
- **Particle texture**: There should be a `"particle"` entry. Warn if it is absent.
- **Namespace**: Every texture value must be qualified with the `socky:` namespace (e.g., `"socky:block/mr_test"`). If any value lacks the `socky:` prefix, raise an error and stop — it is possible but very unlikely the user intends to reference a built-in texture. If the model comes from Blockbench, the texture's namespace can be set by right-clicking it in the Textures panel and selecting Properties from the context menu, where `Folder` should be `block` and namespace should be `socky`. 
- **Default names**: If any texture value's name segment looks like a generic default — e.g., `texture`, `texture0`, `tex`, `block`, or any name that does not resemble the stuffy's own `{snake_id}` — warn the user. This usually means the texture was not renamed in the `.bbmodel` file before exporting.

A correct `"textures"` block for `mr_test` looks like:

```json
"textures": {
    "0": "socky:block/mr_test",
    "particle": "socky:block/mr_test"
}
```

---

## Step 3 — Check textures

Derive the expected block texture path(s) from the `"textures"` object parsed in Step 1. For each unique non-`"particle"` texture value (e.g., `"socky:block/mr_test"`), convert the namespace path to a file path by the rule:

```
{namespace}:{path_type}/{name}  →  src/main/resources/assets/{namespace}/textures/{path_type}/{name}.png
```

For example: `"socky:block/mr_test"` → `src/main/resources/assets/socky/textures/block/mr_test.png`

Check for each derived block texture file, plus the item texture:

```
src/main/resources/assets/socky/textures/block/mr_test.png   ← derived from block model JSON
src/main/resources/assets/socky/textures/item/{snake_id}.png
```

**If a texture is missing:** Do not create a placeholder. Tell the user exactly where to save it and pause for confirmation before continuing. If the texture name looks like a default (e.g., `texture`, `texture0`), remind the user that they likely forgot to rename it in the `.bbmodel` file. Example message:

> Block texture not found. Please save it to:
> `src/main/resources/assets/socky/textures/block/mr_test.png`
> Note: the texture name `texture` looks like a Blockbench default — did you forget to rename it in the `.bbmodel`?

**If a texture exists at the correct path:** Acknowledge it and continue.

**If a texture exists at a wrong path** (e.g., misnamed or in a sibling directory): Report the discrepancy and ask the user to move it before continuing.

---

## Step 4 — Check and create data/asset files

For each file below, check whether it already exists.

- **If it exists:** Parse and validate JSON syntax. If malformed, report the error and stop. If valid, leave it unchanged and note it was found.
- **If it does not exist:** Create it using the default template shown, substituting `{snake_id}` throughout.

### 3a. Blockstates — `src/main/resources/assets/socky/blockstates/{snake_id}.json`

```json
{
  "variants": {
    "facing=north": {
      "model": "socky:block/{snake_id}"
    },
    "facing=south": {
      "model": "socky:block/{snake_id}",
      "y": 180
    },
    "facing=east": {
      "model": "socky:block/{snake_id}",
      "y": 90
    },
    "facing=west": {
      "model": "socky:block/{snake_id}",
      "y": -90
    }
  }
}
```

### 3b. Item model — `src/main/resources/assets/socky/models/item/{snake_id}.json`

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "socky:item/{snake_id}"
  }
}
```

If this file already exists, check its `"parent"` field. The expected value is `"item/generated"`.

### 3c. Item definition — `src/main/resources/assets/socky/items/{snake_id}.json`

```json
{
  "model": {
    "type": "minecraft:model",
    "model": "socky:item/{snake_id}"
  }
}
```

### 3d. Loot table — `src/main/resources/data/socky/loot_table/blocks/{snake_id}.json`

```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "socky:{snake_id}"
        }
      ],
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ]
    }
  ]
}
```

### 3e. Recipe — `src/main/resources/data/socky/recipe/{snake_id}.json`

If this file does not exist, create a placeholder shaped recipe and **warn the user** that the pattern and ingredient(s) are placeholders that should be updated:

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "###",
    "###",
    "# #"
  ],
  "key": {
    "#": "minecraft:white_wool"
  },
  "result": {
    "id": "socky:{snake_id}",
    "count": 1
  }
}
```

If the recipe already exists, validate it as JSON and check that `"result"."id"` matches `"socky:{snake_id}"`. Report any mismatch.

---

## Step 5 — Update `lang/en_us.json`

File: `src/main/resources/assets/socky/lang/en_us.json`

Add the entry `"block.socky.{snake_id}": "{display_name}"` in alphabetical order by key. Keys are sorted lexicographically on the full key string (e.g., `block.socky.ambrose` < `block.socky.mr_test`).

If the key already exists with the correct value, skip silently. If it exists with a different value, report the conflict and ask the user how to proceed.

---

## Step 6 — Update `Ids.java`

File: `src/main/java/net/sockmc/socky/Ids.java`

Add a new `Identifier` constant in alphabetical order by constant name:

```java
public static final Identifier {UPPER_SNAKE} = Identifier.of(MOD_ID, "{snake_id}");
```

Insert it so that the block of `Identifier` fields remains sorted alphabetically by constant name (e.g., `AMBROSE` < `MR_OLIVE` < `MR_TEST` < `PUSHPUSH`). Match the surrounding indentation exactly.

If the constant already exists, confirm its value matches and skip.

---

## Step 7 — Update `Blocks.java`

File: `src/main/java/net/sockmc/socky/Blocks.java`

### 6a. Derive hitbox

If the user supplied a specific hitbox in the invocation prompt, use that value directly.

Otherwise, compute the axis-aligned bounding box (AABB) of all elements in `"elements"` of the block model JSON and use it as the `Block.createCuboidShape(minX, minY, minZ, maxX, maxY, maxZ)` arguments.

For each element in `"elements"`:
1. Generate its 8 corners from `"from"` = [fx, fy, fz] and `"to"` = [tx, ty, tz]:
   ```
   (fx,fy,fz), (tx,fy,fz), (fx,ty,fz), (tx,ty,fz),
   (fx,fy,tz), (tx,fy,tz), (fx,ty,tz), (tx,ty,tz)
   ```
2. If `"rotation"` is present and `"angle"` ≠ 0, apply the rotation to each corner.
   Given `{"angle": θ°, "axis": A, "origin": [ox, oy, oz]}`, for each corner [px, py, pz]:
   - Translate: `u = px-ox, v = py-oy, w = pz-oz`
   - Rotate (θ in radians):
     - axis **x**: `u'=u, v'=v·cos(θ)−w·sin(θ), w'=v·sin(θ)+w·cos(θ)`
     - axis **y**: `u'=u·cos(θ)+w·sin(θ), v'=v, w'=−u·sin(θ)+w·cos(θ)`
     - axis **z**: `u'=u·cos(θ)−v·sin(θ), v'=u·sin(θ)+v·cos(θ), w'=w`
   - Translate back: `px' = u'+ox, py' = v'+oy, pz' = w'+oz`
3. Collect all resulting points across all elements.

Round and clamp:
- minX = floor(min of all x), clamped to [0, 16]
- minY = floor(min of all y), clamped to [0, 16]
- minZ = floor(min of all z), clamped to [0, 16]
- maxX = ceil(max of all x), clamped to [0, 16]
- maxY = ceil(max of all y), clamped to [0, 16]
- maxZ = ceil(max of all z), clamped to [0, 16]

### 6b. Block field

Add a new static field in alphabetical order by constant name (same ordering rule as `Ids.java`):

```java
public static final Block {CONSTANT_NAME} = register(Ids.{UPPER_SNAKE},
        s -> new StuffyBlock(s, Block.createCuboidShape({hitbox})),
        AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOL).nonOpaque(),
        new Item.Settings().maxCount(16)
);
```

Match the indentation of the surrounding fields exactly. If the constant already exists, confirm the hitbox matches and stop if it differs (do not overwrite without asking).

### 6c. Item group registration

In `initialize()`, inside the `ItemGroupEvents.modifyEntriesEvent` lambda, add:

```java
itemGroup.add({CONSTANT_NAME}.asItem());
```

Insert it in alphabetical order by constant name among the existing `add` calls. Match surrounding indentation.

---

## Step 8 — Update `README.md`

File: `README.md`

Append `- {display_name}` to the bulleted list of items under "It adds the following items with associated recipes and models:". Insert it at the end of the list (do not sort — the list is in no particular order).

If an entry for `{display_name}` already exists in the list, skip silently.

---

## Step 9 — Bump version in `gradle.properties`

File: `gradle.properties`

### 8a. Read current version

Read the `mod_version` value (e.g., `1.0-alpha.5`) and `target_version` value (e.g., `1.21.8`).

### 8b. Check for a release tag

Run:

```
git tag --list "{mod_version}+*"
```

- **No matching tags found**: The current branch is unreleased. No version change is needed — skip the rest of this step.
- **At least one matching tag found**: A release has already been cut for this version. Compute the next version and update `gradle.properties`.

### 8c. Compute next version

Parse `mod_version` as a semantic version with an optional prerelease identifier. Supported formats:

| Format | Example | Rule |
|--------|---------|------|
| Prerelease (alpha) | `1.0-alpha.5` | Increment the prerelease number: → `1.0-alpha.6` |
| Prerelease (beta) | `2.1-beta.2` | Increment the prerelease number: → `2.1-beta.3` |
| Stable release | `1.1.3` | Minor version bump, patch resets to 0: → `1.2.0` |

For prerelease versions the identifier is the part after the `-` (e.g., `alpha.5`). Increment only the trailing numeric component of that identifier.

### 8d. Write new version

Update the `mod_version` line in `gradle.properties` with the computed next version. Preserve all surrounding content exactly.

---

## Step 10 — Summary

After completing all steps, print a checklist showing which files were:
- ✓ Found and validated (existing)
- ✓ Created (new default)
- ✗ Missing / blocked (texture not yet placed, etc.)
- ⚠ Needs attention (placeholder recipe, any warnings)

Remind the user to:
1. Replace the placeholder recipe with the intended crafting pattern, if a default was generated.
2. Run `./gradlew build` to confirm the mod compiles cleanly.
3. Run `/backport-stuffy` if this stuffy should be made available on older supported branches.
