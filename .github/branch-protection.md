# Branch Protection Setup Guide

This guide explains how to configure branch protection rules for the `main` branch to ensure code quality and prevent accidental merges.

## Required Settings

1. **Require pull request reviews before merging**
   - Required number of reviewers: 1
   - Dismiss stale pull request approvals when new commits are pushed: Yes
   - Require review from Code Owners: Optional

2. **Require status checks to pass before merging**
   - Required status checks:
     - `test` (from CI workflow)
     - `build` (from CI workflow)
   - Require branches to be up to date before merging: Yes

3. **Require conversation resolution before merging**: Yes

4. **Do not allow bypassing the above settings**: Recommended

## How to Configure

1. Go to your repository on GitHub
2. Navigate to Settings → Branches
3. Click "Add rule" or edit the existing rule for `main`
4. Configure the settings as described above
5. Save the rule

## Benefits

- Ensures all code is reviewed before merging
- Prevents merging broken code (CI must pass)
- Maintains code quality standards
- Enforces test coverage requirements
