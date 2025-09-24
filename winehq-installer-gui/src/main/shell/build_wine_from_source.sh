#!/usr/bin/env bash
set -euo pipefail

VERSION="$1" # Default to Wine 9.0 if not specified
SRC_URL="https://dl.winehq.org/wine/source/${VERSION%.*}.x/wine-$VERSION.tar.xz"

echo "Building Wine $VERSION from source..."

# Detect package manager for dependencies
if command -v apt >/dev/null 2>&1; then
    echo "Installing build dependencies (Debian/Ubuntu)..."
    sudo apt update
    sudo apt install -y build-essential flex bison \
        gcc-multilib g++-multilib \
        libx11-dev libfreetype-dev libglib2.0-dev \
        libgnutls28-dev libunwind-dev
elif command -v dnf >/dev/null 2>&1; then
    echo "Installing build dependencies (Fedora)..."
    sudo dnf groupinstall -y "Development Tools"
    sudo dnf builddep -y wine
elif command -v pacman >/dev/null 2>&1; then
    echo "Installing build dependencies (Arch)..."
    sudo pacman -Sy --noconfirm base-devel \
        lib32-freetype2 lib32-libpng lib32-gnutls
else
    echo "Unknown package manager. Please install Wine build dependencies manually."
    exit 1

fi

# Download and extract Wine source
wget -q "$SRC_URL" -O "wine-$VERSION.tar.xz"
tar -xf "wine-$VERSION.tar.xz"
cd "wine-$VERSION"

# Configure and build
./configure --enable-win64
make -ij"$(nproc)"
sudo make -i install

# Initialize Wine prefix
echo "Initializing Wine prefix..."
WINEPREFIX="$HOME/.wine" wineboot --init >/dev/null 2>&1 || true

echo "Wine $VERSION built and installed successfully."
