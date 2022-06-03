#!/bin/bash
#Written by Seifeldin Ismail - Group 1 - Night Owls

date_format=$(date +%Y-%m-%d)

log=/var/opt/backup-log/error.log

file=/opt/scripts/Backup/filesBackup.txt


#for checking files/folders
for line in $(cat $file)

do

if [ ! -e "$line" ];
then

echo "$date_format: Error backing up $line. Check $file and check if the file/directory exist" >> $log
sed -i "s:${line}::g" "${file}"

fi

done

sed -i '/^$/d' ${file}
#Deletes additional empty lines

tar -czf /media/backup/backup-"${date_format}".tar.gz -T "$file" 2> ${log}
#-T is for taking input from

find /media/backup -mtime +7 -exec rm {} \; 2>/dev/null
