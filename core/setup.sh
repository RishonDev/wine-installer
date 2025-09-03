#Add password setup here
while getopts ":a:bc:" flag;do
    echo "flag -$flag, Argument $OPTARG";
done