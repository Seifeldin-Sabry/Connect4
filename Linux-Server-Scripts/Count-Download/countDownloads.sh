#!/bin/bash
#Written by Seifeldin Ismail - Group 1 - NightOwls

#Starttime for the script
startTime=$(date +%s.%N)

#file path of html file
htmlLocation=/var/www/html/HTML/download-Statistics.html

#Date: sed deletes the 0 as specified on Infra Document
dateFunction=$(date +%d\ %b  | sed 's%/0%/%g' | tr '[:upper:]' '[:lower:]')

#Format of the "last updated on task" detail on infra document
lastUpdatedDate=$(date +%d\ %b\ %H:%M | tr '[:upper:]' '[:lower:]')


#Name of zip-file to look for in logs
#We get both the connect4.jar and connect4.zip
#Depending on which button was pressed
zipName=connect4

apacheLog=/var/log/apache2/access.log


#grep -c stands for --count: counts lines matching string
amountOfDownloads=$(grep -Ec "$(date +%d/%b/%Y)(.)+($zipName)" ${apacheLog})


if [ ! -e "${htmlLocation}" ]
then
  echo "creating html file"
  touch "${htmlLocation}"
  echo "created html file"
  chmod 664 "${htmlLocation}"

#  echo -e "<!DOCTYPE html> <html lang=\"en\">\n<head>\n<meta charset=\"UTF-8\">\n<title>Download Statistics</title>\n<link rel=\"stylesheet\" href=\"../CSS/stylesheet.css\"\n<link rel=\"icon\" href=\"../MEDIA/layout/icon.png\">\n<link rel=\"apple-touch-icon\" href=\"../MEDIA/layout/icon.png\">\n</head>\n<body>\n<a href=\"download.html\">Go back</a>\n" >> "${htmlLocation}"
  echo -e "<!DOCTYPE html>
  <html lang=\"en\">\n
  <head>\n
  <meta charset=\"UTF-8\">\n
  <title>Download Statistics</title>\n
  <link rel=\"stylesheet\" href=\"../CSS/stylesheet.css\"\n
 <link rel=\"icon\" href=\"../MEDIA/layout/icon.png\">\n
  <link rel=\"apple-touch-icon\" href=\"../MEDIA/layout/icon.png\">\n
  </head>\n
  <body>\n
  <a href=\"download.html\">Go back</a>\n" >> "${htmlLocation}"
else


#This was an attempt at making the script better. You can ignore this.
#if [[ $(cat "${htmlLocation}" | grep "${dateFunction}") == "${dateFunction}" ]]
#then
#  echo "pattern matches, we should delete that line and make a new one"
#  sed -i "/$(date +%d\ %b  | sed 's%/0%/%g' | tr '[:upper:]' '[:lower:]')/d" "${htmlLocation}"
#fi


#these sed commands delete lines that contain These 'Patterns/Strings'.
  sed -i '/Total/d' "${htmlLocation}"
  sed -i '/This script took/d' "${htmlLocation}"
  sed -i '/Last updated on/d' "${htmlLocation}"
fi


if [ "${amountOfDownloads}" -gt 0 ]
then
  echo "<p>${dateFunction} - ${amountOfDownloads} downloads</p>" >> "${htmlLocation}"
fi


# shellcheck disable=SC2002
#for the total download I'm doing 3 things:
#1. finding the <p> elements
#2. finding the numbers (amount of downloads for all days)
#3. adding them in an implicit for loop (awk += $4)
#the '$' is for the field. A typical line in the script looks like this: <p>07 jan - x downloads.
# the '-' is the 3rd field and the number is the 4th so im just stacking up the numbers
#finally, to set the variable, the PRINT outputs the sum into the variable

totalDownload=$(cat "${htmlLocation}" | grep "<p>" | grep -E "[^0-9][0-9]+" | awk '{ SUM += $4;} END { print SUM;}')
echo -e "<p>Total: ${totalDownload} downloads.</p>" >> "${htmlLocation}"

endTime=$(date +%s.%N)
runTime=$(echo "$endTime" - "$startTime" | bc -l)

printf "<p>This script took %.2f seconds to run</p>" "$runTime" >> "${htmlLocation}"
echo -e "<p>Last updated on ${lastUpdatedDate}</p></body></html>" >> "${htmlLocation}"
