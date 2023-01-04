# get-wine-latest.sh
If you get a permission denied on any script present in this repository, even with sudo, run `sudo. chmod +x *.sh`
(The READEME.md is not a file to be executed)

a simple set of shell scripts to Install\Remove the latest version of wine from winehq's official repositories without any error.
To install wine, Simply copy this:

Ubuntu 22.04:
```
sudo apt install wget
wget https://raw.githubusercontent.com/RishonDev/get-wine-latest.sh/main/get-wine-7.10-ubuntu_22_04.sh
chmod +x get-wine-7.10-ubuntu_22_04.sh
./get-wine-7.10-ubuntu_22_04.sh
```
Ubuntu 20.04:
```
sudo apt install wget
wget https://raw.githubusercontent.com/RishonDev/get-wine-latest.sh/main/get-wine-7.10-ubuntu_20_04.sh
chmod +x get-wine-7.10-ubuntu_20_04.sh
./get-wine-7.10-ubuntu_20_04.sh
```

I won't be adding support for 21.10 since it's reaching it's end of life. i won't be adding for 18.04 xenial as well since it doesn't have the rquired pakages in it's repositories to make wine function properly.

