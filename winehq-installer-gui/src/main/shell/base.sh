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
    #echo "Detected: $DISTRO $VERSION"
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
function check_dependencies() {
    local missing=()
    local deps=("curl" "lsb_release" "wget" "grep" "awk" "sort")

    for cmd in "${deps[@]}"; do
        if ! command -v "$cmd" >/dev/null 2>&1; then
            missing+=("$cmd")
        fi
    done

    if [ "${#missing[@]}" -eq 0 ]; then
        #echo "All dependencies are installed."
        return
    fi

    echo "Missing dependencies: ${missing[*]}"
    echo "Attempting to install them..."

    if [ -f /etc/os-release ]; then
        . /etc/os-release
        DISTRO=$ID
    else
        echo "Unable to detect your Linux distribution. Please install these manually: ${missing[*]}"
        exit 1
    fi

    case "$DISTRO" in
        ubuntu|debian|linuxmint|pop|zorin)
            sudo apt update
            sudo apt install -y "${missing[@]}"
            ;;
        fedora)
            sudo dnf install -y "${missing[@]}"
            ;;
        arch)
            sudo pacman -Sy --noconfirm "${missing[@]}"
            ;;
        *)
            echo " Unsupported distro: $DISTRO. Install manually: ${missing[*]}"
            exit 1
            ;;
    esac
}

# ------------------ Core Functions ------------------
list_versions() {
    BASE_URL="https://dl.winehq.org/wine/source"

    # Fetch branches like "7.x" or "10.x"
    branches=$(curl -s "$BASE_URL/" | grep -Po '(?<=href=")[0-9]+(?:\.[0-9]+)?(?:\.x)?/' | sed 's#/$##')

    if [[ -z "$branches" ]]; then
        return 1
    fi

    all_versions=()

    for br in $branches; do
        branch_url="$BASE_URL/$br/"

        # Try to get 3-part versions first (like 7.0.1)
        tarballs=$(curl -s "$branch_url" | grep -Po 'wine-[0-9]+\.[0-9]+\.[0-9]+\.tar\.xz' || true)

        # If no 3-part versions, try 2-part (like 10.0)
        if [[ -z "$tarballs" ]]; then
            tarballs=$(curl -s "$branch_url" | grep -Po 'wine-[0-9]+\.[0-9]+\.tar\.xz' || true)
        fi

        if [[ -z "$tarballs" ]]; then
            continue
        fi

        # Extract just version numbers, strip prefix/suffix
        for tb in $tarballs; do
            version=${tb#wine-}
            version=${version%.tar.xz}
            all_versions+=("$version")
        done
    done

    if [[ ${#all_versions[@]} -eq 0 ]]; then
        return 1
    fi

    # Print sorted unique versions only
    printf "%s\n" "${all_versions[@]}" | sort -V | uniq
}





list_versions() {
    BASE_URL="https://dl.winehq.org/wine/source"

    # Fetch the folder names — match something like 1.0/ or 7.x/ or 10.x/
    branches=$(curl -s "$BASE_URL/" | grep -Po '(?<=href=")[0-9]+(?:\.[0-9]+)?(?:\.x)?/' | sed 's#/$##')

    if [[ -z "$branches" ]]; then
        return 1
    fi

    all_versions=()

    for br in $branches; do
        branch_url="$BASE_URL/$br/"

        # Fetch tarballs with 3-part version like wine-7.0.1.tar.xz
        tarballs=$(curl -s "$branch_url" | grep -Po 'wine-[0-9]+\.[0-9]+\.[0-9]+\.tar\.xz' || true)

        # If none found, try 2-part versions like wine-10.0.tar.xz
        if [[ -z "$tarballs" ]]; then
            tarballs=$(curl -s "$branch_url" | grep -Po 'wine-[0-9]+\.[0-9]+\.tar\.xz' || true)
        fi

        if [[ -z "$tarballs" ]]; then
            continue
        fi

        # Remove duplicates just in case
        tarballs=$(echo "$tarballs" | sort -u)
        for tb in $tarballs; do
            version=$(echo "$tb" | sed -E 's/wine-//; s/\.tar\.xz//')
            all_versions+=("$version")
        done
    done

    if [[ ${#all_versions[@]} -eq 0 ]]; then
        return 1
    fi

    printf "%s\n" "${all_versions[@]}" | sort -V | uniq
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
check_dependencies
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

#echo "Completed. Logs saved to $LOG_FILE"