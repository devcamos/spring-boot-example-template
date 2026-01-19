#!/bin/bash
# Package verification script
# This script verifies that all Java files have package declarations that match their directory structure

echo "🔍 Verifying package declarations match directory structure..."
echo ""

errors=0
total=0

find src -name "*.java" -type f | while read file; do
    total=$((total + 1))
    pkg=$(grep "^package " "$file" 2>/dev/null | head -1 | sed 's/package //;s/;//')
    
    if [ -n "$pkg" ]; then
        # Convert package to expected directory path
        expected_dir=$(echo "$pkg" | tr '.' '/')
        
        # Get actual directory from file path
        if [[ "$file" == src/main/java/* ]]; then
            file_dir=$(echo "$file" | sed 's|^src/main/java/||;s|/[^/]*\.java$||')
        elif [[ "$file" == src/test/java/* ]]; then
            file_dir=$(echo "$file" | sed 's|^src/test/java/||;s|/[^/]*\.java$||')
        else
            continue
        fi
        
        if [ "$expected_dir" != "$file_dir" ]; then
            echo "❌ MISMATCH FOUND:"
            echo "   File: $file"
            echo "   Package: $pkg"
            echo "   Expected directory: $expected_dir"
            echo "   Actual directory: $file_dir"
            echo ""
            errors=$((errors + 1))
        fi
    fi
done

if [ $errors -eq 0 ]; then
    echo "✅ All $total Java files have correct package declarations!"
    echo ""
    echo "If your IDE still shows errors, try:"
    echo "  - IntelliJ IDEA: File → Invalidate Caches / Restart"
    echo "  - Eclipse: Project → Clean → Clean all projects"
    echo "  - VS Code: Reload Window (Cmd+Shift+P → 'Reload Window')"
else
    echo "❌ Found $errors package mismatch(es)"
    exit 1
fi
