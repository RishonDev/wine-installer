echo "Backup Wine Cache?"
read -p "Ready? [Y/n]: " -n 1 -r
if [[ $REPLY =~ ^[Yy]$ ]]
    then
    mv ~/.wine ~/.wine.bak
fi

rm -r ~/.wine
sudo apt-get purge wine

while getopts "vsdt" n
do
  case "$n" in
    v) echo "1.0.0" ;;
    s) sudo apt-get purge winehq-stable
       sudo apt-get install winehq-devel;;
    d) sudo apt-get purge winehq-devel
       sudo apt-get install winehq-devel;;
    t) sudo apt-get purge winehq-devel
       sudo apt-get install winehq-devel;;
    \?) usage;;
  esac
done
