#!/bin/bash
version="4.0"
flags=""
OS=$(lsb_release -is)
printversion(){
  echo "WineHQ installer version $version"
  echo "Wine "
}
ubuntu(){
  ./core/winhq-installer-ubuntu.sh
}
debian(){
  ./core/winhq-installer-debain.sh
}
fedora(){
  ./core/winhq-installer-fedora.sh
}
print_usage() {
  echo "Options:"
  echo "--ubuntu | -u     Enters the Ubuntu installer of wine"
  echo "-fedora           Enters the Fedora installer of wine"
  echo "-debian  | -d     Enters the Debian installer of wine"
  echo "-version | -v     Prints app information"
  echo "-upgrade | -u     Upgrades your Wine. Ubuntu updates may be applied for the smooth experience for wine."
  echo "-verbose |        Displays all the logs on screen."
  echo "-silent           Finishes All the processes with no output. Useful for using it in the background.."
  echo "-log              Adds debug logs to process any errors."

}
while true; do
  case "$1" in
    --verbose ) VERBOSE=true; shift ;;
    -u |--ubuntu ) DEBUG=true; shift ;;
    -fedora ) MEMORY="$2"; shift 2 ;;
    --debugfile ) DEBUGFILE="$2"; shift 2 ;;
    --minheap )
      JAVA_MISC_OPT="$JAVA_MISC_OPT -XX:MinHeapFreeRatio=$2"; shift 2 ;;
    --maxheap )
      JAVA_MISC_OPT="$JAVA_MISC_OPT -XX:MaxHeapFreeRatio=$2"; shift 2 ;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done
while getopts 'udfv:ve' flag; do
  case "${flag}" in
    u) ubuntu ;;
    fedora) fedora ;;
    debian) debian ;;
    ve) printversion ;;
    verbose) flags="$flags -v" ;;
    *) print_usage
       exit 1 ;;
  esac
done
echo