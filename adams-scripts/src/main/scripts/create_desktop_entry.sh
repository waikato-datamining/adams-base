#!/bin/bash
#
# Creates a new .desktop file for the specified script.
#
# For more information see:
# - Quick overview
#   https://wiki.archlinux.org/title/Desktop_entries
# - Full specs:
#   https://specifications.freedesktop.org/desktop-entry-spec/latest/recognized-keys.html
# - Categories:
#   https://specifications.freedesktop.org/menu-spec/latest/category-registry.html

# the usage of this script
function usage()
{
   echo
   echo "${0##*/} -s <script> -n <name> [-i <icon>] [-c <categories>] -o <desktop> [-t] [-h]"
   echo
   echo "Creates a .desktop file for the specified script."
   echo
   echo "For more information see:"
   echo "- Quick overview:"
   echo "  https://wiki.archlinux.org/title/Desktop_entries"
   echo "- Full specs:"
   echo "  https://specifications.freedesktop.org/desktop-entry-spec/latest/recognized-keys.html"
   echo "- Categories:"
   echo "  https://specifications.freedesktop.org/menu-spec/latest/category-registry.html"
   echo
   echo " -h   this help"
   echo " -s   <script>"
   echo "      the script to create the .desktop file for"
   echo " -n   <name>"
   echo "      the name to display in the GUI"
   echo " -i   <icon>"
   echo "      the path to the icon to use, typically a PNG image"
   echo " -c   <categories>"
   echo "      the categories string to use, default: $categories_default"
   echo " -o   <desktop>"
   echo "      the file to save the generated .desktop entry to"
   echo " -t   whether to run in a terminal"
   echo
}

root=`expr "$0" : '\(.*\)/'`
categories_default="Science;"
script=""
name=""
icon=""
categories="$categories_default"
output=""
terminal="false"

# interpret parameters
while getopts ":hts:n:i:c:o:" flag
do
   case $flag in
      s) script=$OPTARG
         ;;
      n) name=$OPTARG
         ;;
      i) icon=$OPTARG
         ;;
      c) categories=$OPTARG
         ;;
      o) output=$OPTARG
         ;;
      t) terminal="true"
         ;;
      h) usage
         exit 0
         ;;
      *) usage
         exit 1
         ;;
   esac
done

if [ "$script" = "" ] || [ ! -f "$script" ]
then
  echo
  echo "No script specified or does not exist!"
  echo
  exit 1
fi

if [ "$name" = "" ]
then
  echo
  echo "No name specified!"
  echo
  exit 1
fi

if [ "$output" = "" ]
then
  echo
  echo "No output file specified!"
  echo
  exit 1
fi

# create .desktop file
echo "[Desktop Entry]" > $output
echo "Type=Application" >> $output
echo "Terminal=$terminal" >> $output
echo "Name=$name" >> $output
echo "Exec=$script" >> $output
if [ "$icon" != "" ] && [ -f "$icon" ]
then
  echo "Icon=$icon" >> $output
fi
echo "Categories=$categories" >> $output
echo "" >> $output
