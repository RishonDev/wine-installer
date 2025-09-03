#!/bin/bash

# Get the release version
RELEASE=$(lsb_release -sr)

# Parse command-line options
while getopts ":v:" opt; do
  case $opt in
    v)
      echo "4.0.0"
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      echo "-v Displays the version"
      exit 1
      ;;
  esac
done

# Spinner function for progress indication
spin() {
  spinner="/|\\-/|\\-"
  while :; do
    for i in $(seq 0 7); do
      echo -n "${spinner:$i:1}"
      echo -en "\010"
      sleep 0.1
    done
  done
}

# Create logs directory and log files
mkdir -p ./logs
touch ./logs/repoLog.txt ./logs/repoLog2.txt ./logs/installLog.txt ./logs/configLog.txt

# Enable 32-bit support
echo "Enabling 32-bit support..."
spin &
SPIN_PID=$!
trap "kill -9 $SPIN_PID" $(seq 0 15)
sudo dpkg --add-architecture i386 || {
  echo 'ERROR: Unable to enable 32-bit support.'
  exit 1
}
kill -9 $SPIN_PID

# Add the appropriate repository based on release version
echo "Adding the repositories..."
spin &
SPIN_PID=$!
trap "kill -9 $SPIN_PID" $(seq 0 15)
{
  if [ "$RELEASE" = "41" ]; then
    sudo dnf5 config-manager addrepo --from-repofile=https://dl.winehq.org/wine-builds/fedora/41/winehq.repo
    sudo dnf update
  elif [ "$RELEASE" = "40" ]; then
    sudo dnf config-manager --add-repo https://dl.winehq.org/wine-builds/fedora/40/winehq.repo
    sudo apt update
  fi
} &> ./logs/repoLog.txt || true
kill -9 $SPIN_PID

clear

# Prompt user to select build type
echo "1) Stable build (Recommended)"
echo "2) Development build (Recommended for testing use only)"
echo "3) Staging build (Recommended for testing use only)"
read -p "Select build channel: " build

case $build in
  1) build="stable" ;;
  2) build="devel" ;;
  3) build="staging" ;;
  *) echo "Invalid selection. Exiting."; exit 1 ;;
esac

# Confirm before proceeding
read -p "Ready? [Y/n]: " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
  echo "Installing WineHQ $build build..."
  spin &
  SPIN_PID=$!
  trap "kill -9 $SPIN_PID" $(seq 0 15)
  sudo dnf install winehq-"$build" winetricks &> ./logs/installLog.txt
  winecfg &> ./logs/configLog.txt
  kill -9 $SPIN_PID
else
  echo "Abort."
  exit 1
fi

# Display log directory path
echo "The logs can be found at $(pwd)/logs/"
