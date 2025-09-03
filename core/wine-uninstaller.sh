path=$(pwd)
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
read -p "Are you sure to uninstall wine? [Y/n]: " -n 1 -r
if [[ $REPLY =~ ^[Yy]$ ]]
    then
    read -p "Backup wine cache? [Y/n]: " -n 1 -r
    if [[ $REPLY =~ ^[Yy]$ ]]
        then
        mv ~/.wine ~/.wine.bak
    fi
    cd $HOME
    echo "Uninstalling Wine... "
    {
    spin &
    SPIN_PID=$!
    trap "kill -9 $SPIN_PID" `seq 0 15`
    rm -r .wine
    rm .config/menus/applications-merged/wine*
    rm -r .local/share/applications/wine
    rm .local/share/desktop-directories/wine*
    rm .local/share/icons/????_*.xpm
    sudo apt purge --autoremove winehq-stable -y
    sudo apt-get update
    sudo apt-get autoclean -y
    sudo apt-get clean -y
    sudo apt-get autoremove -y
    } &> /dev/null

    kill -9 $SPIN_PID
    cd $path || exit 1
fi
