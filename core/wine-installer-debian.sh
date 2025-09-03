log_path =""
while true; do
  case "$1" in
    -v | --verion ) VERBOSE=true; shift ;;
    -l | --log ) DEBUG=true; shift ;;
    -m | --memory ) MEMORY="$2"; shift 2 ;;
    --debugfile ) DEBUGFILE="$2"; shift 2 ;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done

RELEASE=$(lsb_release -sr)
spin()
{
  spinner="/|\\-/|\\-"
  while :
  do
    for i in `seq 0 7`
    do
      echo -n "${spinner:$i:1}"
      echo -en "\010"
      sleep 0.1
    done
  done
}

#Creates Logs
mkdir ./logs
touch ./logs/repoLog.txt
touch ./logs/repoLog2.txt
touch ./logs/installLog.txt
touch ./logs/configLog.txt
echo "Enabling 32-bit support... "
spin &
SPIN_PID=$!
trap "kill -9 $SPIN_PID" `seq 0 15`
sudo dpkg --add-architecture i386 || { echo 'ERROR: Unable to enable 32-bit support.' ; exit 1; }
kill -9 $SPIN_PID


echo "Adding the repositories... "
spin &
SPIN_PID=$!
trap "kill -9 $SPIN_PID" `seq 0 15`
#Insert interactive shell here
sudo add-apt-repository universe -y &>> ./logs/repoLog2.txt
{
if [ "$RELEASE" = "20.04" ]; then
    sudo wget -NP /etc/apt/sources.list.d/ https://dl.winehq.org/wine-builds/ubuntu/dists/focal/winehq-focal.sources
    sudo apt update
fi

if [ "$RELEASE" = "22.04" ]; then
    sudo wget -NP /etc/apt/sources.list.d/ https://dl.winehq.org/wine-builds/ubuntu/dists/jammy/winehq-jammy.sources
    sudo apt update
fi


if [ "$RELEASE" = "23.04" ]; then
    sudo wget -NP /etc/apt/sources.list.d/ https://dl.winehq.org/wine-builds/ubuntu/dists/lunar/winehq-lunar.sources
    sudo apt update
fi
if [ "$RELEASE" = "23.10" ]; then
    sudo wget -NP /etc/apt/sources.list.d/ https://dl.winehq.org/wine-builds/ubuntu/dists/mantic/winehq-mantic.sources
    sudo apt update
fi
if [ "$RELEASE" = "24.04" ]; then
    sudo wget -NP /etc/apt/sources.list.d/ https://dl.winehq.org/wine-builds/ubuntu/dists/mantic/winehq-mantic.sources
    sudo apt update

else
    echo "Unsupported release"
fi
    sudo mkdir -pm755 /etc/apt/keyrings
    sudo wget -O /etc/apt/keyrings/winehq-archive.key https://dl.winehq.org/wine-builds/winehq.key
    sudo apt update
} &> ./logs/repoLog.txt
kill -9 $SPIN_PID
clear
echo "1)Stable build (Recommended)"
echo "2)Development build (Recommended for testing use only)"
echo "3)Staging build (Recommended for testing use only)"
read -p "Select build channel:" build -n 1 -r

if [ "$build" = "1" ]
  build = "stable"
fi
if ["$build" = "2"]
  build = "devel"
fi
if ["$build" = "3"]
  build = "staging"
fi
read -p "Ready? [Y/n]: " -n 1 -r
if [[ $REPLY =~ ^[Yy]$ ]]
    then
    echo
    spin &
    SPIN_PID=$!
    trap "kill -9 $SPIN_PID" `seq 0 15`
    sudo apt install winehq-"$build" winetricks &> ./logs/installLog.txt || {echo "ERROR: dpkg locked" && exit 1}
    winecfg &> ./logs/configLog.txt
else
    echo
    echo "Abort."
fi
echo "The logs can be found at" $(pwd)"/logs/"
