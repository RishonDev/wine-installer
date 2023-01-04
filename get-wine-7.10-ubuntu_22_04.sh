echo "Setting up dependencies..."
sudo add-apt-repository universe
sudo dpkg --add-architecture i386
wget -nc https://dl.winehq.org/wine-builds/ubuntu/dists/jammy/winehq-jammy.sources
sudo mv winehq-jammy.sources /etc/apt/sources.list.d/
sudo apt update
wget -nc https://dl.winehq.org/wine-builds/winehq.key
sudo mv winehq.key /usr/share/keyrings/winehq-archive.key
sudo apt update
echo "Installing wine 7.10 ...."
sudo apt install winehq-stable winetricks

echo "Setting up wine..."
winecfg
echo "Done!"

