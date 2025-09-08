#!/usr/bin/env bash
# ======================================================
#  Wine Installer & Manager
#  Author: Rishon (revamped from original project)
#  Description:
#    A universal Wine installer/manager for Linux
# ======================================================

set -euo pipefail

LOG_FILE="./wine-installer.log"
exec > >(tee -a "$LOG_FILE") 2>&1

REPO_URL="https://dl.winehq.org/wine/source"

# ------------------ Usage ------------------
function usage() {
cat <<EOF
Usage: $0 [options]

Options:
  --list                  List all available Wine versions from WineHQ
  --install <ver|stable|staging|devel>
                          Install a specific Wine version or latest branch
  --download <version>    Download source tarball for given version
  --uninstall             Remove Wine
  --repair                Reinstall current Wine
  --revert                Revert to distro-provided Wine
  --current               Show currently installed Wine version
  --clean                 Remove Wine configs (~/.wine, ~/.config/wine)
  --upgrade               Upgrade Wine packages to latest available
  --binarypath            Show path of active Wine binary
  -h, --help              Show this help
EOF
}

# ------------------ Distro Detection ------------------
function detect_distro() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        DISTRO=$ID
        VERSION=$VERSION_ID
    else
        echo "Could not detect distribution"
        exit 1
    fi
    echo "Detected: $DISTRO $VERSION"
}

# Map derivative codenames back to Ubuntu base
function get_ubuntu_codename() {
    CODENAME=$(lsb_release -cs)
    case "$CODENAME" in
        victoria|vera|vanessa|una) echo "jammy" ;;  # Mint 21.x -> 22.04
        ulyssa|ulyana|uma) echo "focal" ;;          # Mint 20.x -> 20.04
        jammy) echo "jammy" ;;                      # Pop!_OS 22.04
        focal) echo "focal" ;;                      # Pop!_OS 20.04
        zorin16) echo "focal" ;;                    # Zorin 16.x -> 20.04
        *) echo "$CODENAME" ;;
    esac
}

# ------------------ Core Functions ------------------
function list_versions() {
    echo "Fetching available Wine versions from WineHQ..."
    curl -s "$REPO_URL/" | \
      grep -oP '(?<=href=")[0-9]+\.[0-9]+(?=/")' | \
      while read branch; do
        curl -s "$REPO_URL/$branch/" | \
          grep -oP 'wine-\K[0-9]+\.[0-9]+(\.[0-9]+)?(?=\.tar\.xz)'
      done | sort -V | uniq
}

function install_version() {
    VERSION=$1
    echo "Requested install target: $VERSION"

    case "$DISTRO" in
        ubuntu|debian|linuxmint|pop|zorin)
            sudo dpkg --add-architecture i386
            sudo mkdir -pm755 /etc/apt/keyrings

            CODENAME=$(get_ubuntu_codename)
            REPO_SRC="https://dl.winehq.org/wine-builds/ubuntu/dists/$CODENAME/winehq-$CODENAME.sources"

            echo "Using codename: $CODENAME"
            if wget --spider "$REPO_SRC" 2>/dev/null; then
                # 1. Add repo first
                sudo wget -NP /etc/apt/sources.list.d/ "$REPO_SRC"
                # 2. Add key second
                wget -nc https://dl.winehq.org/wine-builds/winehq.key -O /etc/apt/keyrings/winehq-archive.key
                # 3. Update
                sudo apt update

                case "$VERSION" in
                    stable|staging|devel)
                        echo "Installing latest $VERSION branch..."
                        if ! sudo apt install --install-recommends "winehq-$VERSION" -y; then
                            echo "Failed to install latest $VERSION branch. Falling back to distro Wine..."
                            revert_wine
                        fi
                        ;;
                    *)
                        echo "Installing specific version $VERSION..."
                        if ! sudo apt install --install-recommends "winehq-stable=$VERSION" -y; then
                            echo "Version $VERSION not found in repo. Falling back to distro Wine..."
                            revert_wine
                        fi
                        ;;
                esac
            else
                echo "WineHQ does not provide packages for codename $CODENAME."
                echo "Falling back to distro-provided Wine..."
                revert_wine
            fi
            ;;
        fedora)
            if [[ "$VERSION" =~ ^(stable|staging|devel)$ ]]; then
                echo "Fedora repos only ship latest stable. Installing distro wine..."
                revert_wine
            else
                echo "Fedora does not support installing version $VERSION directly. Use --download."
            fi
            ;;
        arch)
            if [[ "$VERSION" =~ ^(stable|staging|devel)$ ]]; then
                echo "Arch repos only ship latest stable. Installing distro wine..."
                revert_wine
            else
                echo "Arch does not support installing version $VERSION directly. Use --download."
            fi
            ;;
        *)
            echo "Unsupported distribution: $DISTRO"
            ;;
    esac
}

function download_binaries() {
    VERSION=$1
    echo "Downloading Wine source tarball for version $VERSION..."
    MAJOR=$(echo "$VERSION" | cut -d. -f1-2)
    mkdir -p ./wine-downloads
    if ! wget "$REPO_URL/$MAJOR/wine-$VERSION.tar.xz" -P ./wine-downloads/; then
        echo "Version $VERSION not found on WineHQ."
        exit 2
    fi
    echo "Downloaded wine-$VERSION.tar.xz to ./wine-downloads/"
}

function uninstall_wine() {
    echo "Removing Wine..."
    case "$DISTRO" in
        ubuntu|debian|linuxmint|pop|zorin) sudo apt purge winehq* wine* -y ;;
        fedora) sudo dnf remove wine* -y ;;
        arch) sudo pacman -Rns wine -y ;;
    esac
}

function repair_wine() {
    echo "Repairing Wine installation..."
    uninstall_wine
    case "$DISTRO" in
        ubuntu|debian|linuxmint|pop|zorin)
            sudo apt install --install-recommends winehq-stable -y
            ;;
        fedora)
            sudo dnf reinstall wine -y
            ;;
        arch)
            sudo pacman -S wine --noconfirm
            ;;
    esac
}

function revert_wine() {
    echo "Reverting to distro-provided Wine..."
    case "$DISTRO" in
        ubuntu|debian|linuxmint|pop|zorin) sudo apt install wine -y ;;
        fedora) sudo dnf install wine -y ;;
        arch) sudo pacman -S wine -y ;;
    esac
}

function current_wine() {
    echo "Checking current Wine installation..."
    if command -v wine >/dev/null 2>&1; then
        WINE_VER=$(wine --version)
        echo "Wine detected: $WINE_VER"
        case "$DISTRO" in
            ubuntu|debian|linuxmint|pop|zorin)
                ORIGIN=$(apt-cache policy winehq-stable 2>/dev/null | grep 'Installed' | awk '{print $2}')
                if [ "$ORIGIN" != "(none)" ]; then
                    echo "Installed via WineHQ repo (winehq-stable)"
                else
                    echo "Likely installed via distro repository"
                fi
                ;;
            fedora)
                rpm -q wine &>/dev/null && echo "Installed via Fedora repo" ;;
            arch)
                pacman -Qi wine &>/dev/null && echo "Installed via Arch repo" ;;
        esac
    else
        echo "No Wine installation found on this system."
    fi
}

function clean_wine() {
    echo "Cleaning Wine prefix (~/.wine) and configs..."
    rm -rf ~/.wine
    rm -f ~/.config/wine
    echo "Wine configs cleaned."
}

function upgrade_wine() {
    echo "Upgrading Wine packages..."
    case "$DISTRO" in
        ubuntu|debian|linuxmint|pop|zorin)
            sudo apt update && sudo apt install --only-upgrade winehq-stable wine-stable -y
            ;;
        fedora)
            sudo dnf upgrade wine* -y
            ;;
        arch)
            sudo pacman -Syu wine --noconfirm
            ;;
    esac
}

function binarypath_wine() {
    if command -v wine >/dev/null 2>&1; then
        echo "Wine binary path: $(command -v wine)"
    else
        echo "Wine is not installed."
    fi
}

# ------------------ Main ------------------
if [ $# -eq 0 ]; then usage; exit 0; fi
detect_distro

case "$1" in
  --list) list_versions ;;
  --install) shift; install_version "$1" ;;
  --download) shift; download_binaries "$1" ;;
  --uninstall) uninstall_wine ;;
  --repair) repair_wine ;;
  --revert) revert_wine ;;
  --current) current_wine ;;
  --clean) clean_wine ;;
  --upgrade) upgrade_wine ;;
  --binarypath) binarypath_wine ;;
  -h|--help) usage ;;
  *) echo "Unknown option: $1"; usage; exit 1 ;;
esac

echo "Completed. Logs saved to $LOG_FILE"
