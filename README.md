# get-wine-latest.sh
If you get a `permission denied` on any script present in this repository, even with `sudo`, run `sudo. chmod +x *.sh` 

(The READEME.md is not a file to be executed)

a simple set of shell scripts to Install\Remove the latest version of wine from winehq's official repositories without any error.

To install wine, Simply copy this:

Ubuntu:
```
sudo apt install git
git clone https://github.com/RishonDev/winehq-installer.git
cd winehq-installer
chmod +x *.sh
./wine-installer-ubuntu.sh
```
