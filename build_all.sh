#!/usr/bin/env bash
set -euo pipefail

usage() {
    echo "Usage: $(basename "$0") [-o OUTPUT_DIR] [BRANCH ...]"
    echo
    echo "Build mod JARs across branches and version targets."
    echo
    echo "  -o OUTPUT_DIR  Directory to copy JARs into (default: ./dist)"
    echo "  BRANCH ...     Branches to build (default: all local branches)"
    echo
    echo "On branches with a versions/ directory, builds once per target."
    echo "On branches without one, does a single default build."
    echo
    echo "Examples:"
    echo "  $(basename "$0")                                  Build all branches to ./dist"
    echo "  $(basename "$0") -o ~/Desktop/release             Build all branches to a specific directory"
    echo "  $(basename "$0") -o ~/Desktop/release main 1.21.4 Build only specific branches"
    echo "  $(basename "$0") -o ~/Desktop/release 1.21.4      Build one branch (all its version targets)"
    exit 1
}

output_dir="./dist"
while getopts "o:h" opt; do
    case "$opt" in
        o) output_dir="$OPTARG" ;;
        h) usage ;;
        *) usage ;;
    esac
done
shift $((OPTIND - 1))

# Collect branches
if [[ $# -gt 0 ]]; then
    branches=("$@")
else
    branches=()
    while IFS= read -r b; do
        branches+=("$b")
    done < <(git for-each-ref --format='%(refname:short)' refs/heads/)
fi

# Ensure clean working tree
if ! git diff --quiet || ! git diff --cached --quiet; then
    echo "Error: Working tree has uncommitted changes. Commit or stash them first."
    exit 1
fi

original_branch="$(git symbolic-ref --short HEAD)"
mkdir -p "$output_dir"
output_dir="$(cd "$output_dir" && pwd)"

cleanup() {
    echo "Returning to branch '$original_branch'..."
    git checkout "$original_branch" --quiet
}
trap cleanup EXIT

total_jars=0

for branch in "${branches[@]}"; do
    echo "========================================"
    echo "Branch: $branch"
    echo "========================================"
    git checkout "$branch" --quiet

    # Determine version targets for this branch
    if [[ -d versions ]]; then
        targets=()
        for f in versions/*.properties; do
            t="$(basename "$f" .properties)"
            targets+=("$t")
        done
    else
        targets=("")
    fi

    for target in "${targets[@]}"; do
        if [[ -n "$target" ]]; then
            echo "  Building target: $target"
            ./gradlew clean build -Ptarget_version="$target" --quiet
        else
            echo "  Building default target"
            ./gradlew clean build --quiet
        fi

        # Copy mod JARs (exclude sources JARs)
        for jar in build/libs/*.jar; do
            [[ "$jar" == *-sources.jar ]] && continue
            cp "$jar" "$output_dir/"
            echo "    -> $(basename "$jar")"
            ((total_jars++))
        done
    done
done

echo
echo "========================================"
echo "Done! $total_jars JAR(s) written to $output_dir"
echo "========================================"
