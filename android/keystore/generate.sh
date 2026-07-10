#!/bin/bash
# ==============================================================
# Keystore Generation Script
# Run this script ONCE locally to generate your release keystore.
# Then upload the keystore and its credentials to GitHub Secrets.
# ==============================================================

set -e

KEYSTORE_DIR="$(cd "$(dirname "$0")" && pwd)"
KEYSTORE_FILE="$KEYSTORE_DIR/keystore.jks"
KEYSTORE_PASSWORD="GraceDiary2024!"
KEY_ALIAS="grace-diary"
KEY_PASSWORD="GraceDiary2024!"
VALIDITY_DAYS=10000
DNAME="CN=Grace's Diary, OU=Development, O=GraceApps, L=City, ST=State, C=US"

echo "=========================================="
echo "  Keystore Generator for Grace's Diary"
echo "=========================================="
echo ""

# Check if keytool is available
if ! command -v keytool &> /dev/null; then
    echo "ERROR: keytool not found. Please install JDK."
    exit 1
fi

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "Keystore already exists at: $KEYSTORE_FILE"
    echo "Do you want to overwrite it? (y/N)"
    read -r answer
    if [ "$answer" != "y" ] && [ "$answer" != "Y" ]; then
        echo "Aborted."
        exit 0
    fi
    rm -f "$KEYSTORE_FILE"
fi

# Generate keystore
echo "Generating keystore..."
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY_DAYS" \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "$DNAME"

echo ""
echo "=========================================="
echo "  Keystore Generated Successfully!"
echo "=========================================="
echo ""
echo "  File: $KEYSTORE_FILE"
echo "  Alias: $KEY_ALIAS"
echo "  Keystore Password: $KEYSTORE_PASSWORD"
echo "  Key Password: $KEY_PASSWORD"
echo ""
echo "=========================================="
echo "  IMPORTANT: Add these to GitHub Secrets:"
echo "=========================================="
echo ""
echo "  1. Encode keystore to base64:"
echo "     base64 -i \"$KEYSTORE_FILE\" | pbcopy"
echo ""
echo "  2. Add these secrets to your GitHub repository:"
echo "     Settings > Secrets and variables > Actions"
echo ""
echo "  | Secret Name        | Value                           |"
echo "  |--------------------|---------------------------------|"
echo "  | KEYSTORE_BASE64    | (paste the base64 output above) |"
echo "  | KEYSTORE_PASSWORD  | $KEYSTORE_PASSWORD              |"
echo "  | KEY_ALIAS          | $KEY_ALIAS                      |"
echo "  | KEY_PASSWORD       | $KEY_PASSWORD                   |"
echo ""
echo "  ⚠️  Keep these credentials safe!"
echo "  ⚠️  Never commit the keystore or passwords to git."
echo "=========================================="
