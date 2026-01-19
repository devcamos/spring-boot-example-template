# Package Rename Guide

This template uses `com.example.template` as a placeholder package name that should be replaced with your actual package name.

## Quick Find & Replace

To rename the package to your own (e.g., `com.yourcompany.yourapp`), perform these find/replace operations:

### 1. Package Name
- **Find**: `com.example.template`
- **Replace**: `com.yourcompany.yourapp`
- **Files**: All `.java` files, `.yml` files, `.xml` files

### 2. Group ID (Maven)
- **Find**: `com.example`
- **Replace**: `com.yourcompany`
- **File**: `pom.xml`

### 3. Artifact ID (Maven)
- **Find**: `example-template`
- **Replace**: `your-app-name`
- **Files**: `pom.xml`, `README.md`, `docker-compose.yml`, CI workflows

### 4. Application Class Name
- **Find**: `ExampleTemplateApplication`
- **Replace**: `YourAppApplication`
- **File**: `src/main/java/com/example/template/ExampleTemplateApplication.java`
- **Also**: Rename the file itself

### 5. Database Names (Optional)
- **Find**: `example_db`, `example_user`, `example_pass`
- **Replace**: Your database names
- **Files**: `application.yml`, `docker-compose.yml`, `README.md`

### 6. Directory Structure
After renaming packages, move the directories:
```bash
mv src/main/java/com/example/template src/main/java/com/yourcompany/yourapp
mv src/test/java/com/example/template src/test/java/com/yourcompany/yourapp
```

## IDE Support

Most IDEs (IntelliJ IDEA, Eclipse, VS Code) support package renaming:
- **IntelliJ IDEA**: Right-click package → Refactor → Rename
- **Eclipse**: Right-click package → Refactor → Rename
- **VS Code**: Use find/replace across files

## Verification

After renaming, verify:
1. All imports are updated
2. `pom.xml` groupId and artifactId are updated
3. Application class name matches file name
4. Configuration files reference correct package
5. Tests still compile and run
