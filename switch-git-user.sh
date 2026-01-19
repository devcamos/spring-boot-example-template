#!/bin/bash
# Git User Switcher
# Usage: ./switch-git-user.sh [motivuslabs|devcamos]

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}⚠️  Not in a git repository${NC}"
    echo "   Run this script from the project root directory"
    exit 1
fi

# Get current config
CURRENT_NAME=$(git config user.name 2>/dev/null || echo "not set")
CURRENT_EMAIL=$(git config user.email 2>/dev/null || echo "not set")

echo -e "${BLUE}=== Git User Switcher ===${NC}\n"
echo -e "${BLUE}Current Configuration:${NC}"
echo -e "  Name:  ${CURRENT_NAME}"
echo -e "  Email: ${CURRENT_EMAIL}\n"

# If profile specified, use it; otherwise show menu
if [ -n "$1" ]; then
    PROFILE=$1
else
    echo -e "${BLUE}Available profiles:${NC}"
    echo -e "  1) motivuslabs (Business - devonte@nexusmotivus.ai)"
    echo -e "  2) devcamos (Personal - devontedcamos@gmail.com)"
    echo ""
    read -p "Select profile (1 or 2): " choice
    
    case $choice in
        1) PROFILE="motivuslabs" ;;
        2) PROFILE="devcamos" ;;
        *) 
            echo -e "${YELLOW}❌ Invalid choice${NC}"
            exit 1
            ;;
    esac
fi

# Set user based on profile
case $PROFILE in
    motivuslabs)
        NAME="motivuslabs"
        EMAIL="devonte@nexusmotivus.ai"
        ;;
    devcamos)
        NAME="devcamos"
        EMAIL="devontedcamos@gmail.com"
        ;;
    *)
        echo -e "${YELLOW}❌ Invalid profile: $PROFILE${NC}"
        echo "   Use: motivuslabs or devcamos"
        exit 1
        ;;
esac

# Set git config (project-specific)
git config user.name "$NAME"
git config user.email "$EMAIL"

# Verify
echo ""
echo -e "${GREEN}✅ Git user switched successfully!${NC}\n"
echo -e "${BLUE}New Configuration:${NC}"
echo -e "  Name:  ${GREEN}$(git config user.name)${NC}"
echo -e "  Email: ${GREEN}$(git config user.email)${NC}\n"

# Show current remote
echo -e "${BLUE}Current remote:${NC}"
git remote -v 2>/dev/null || echo "  (no remote configured)"

echo ""
echo -e "${YELLOW}📋 Note:${NC}"
echo -e "  This is project-specific configuration (not global)."
echo -e "  All commits in this repository will use the above credentials."
