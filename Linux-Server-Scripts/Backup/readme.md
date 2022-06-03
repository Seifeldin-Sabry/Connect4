# This is the linux backup script.

### Steps of the script

1. Open the file 'filesBackup.txt' located in /opt/scripts/Backup/
2. Type in all directories you want to be backed up (1 directory/file per line)
3. Very important to check your spelling, the script will run even if you spell something wrong. 
4. In case an invalid line was found, it will delete it and inform you that it has been deleted in the log. 
5. Lastly, it also removes one-week-old backups.

### Schedules

- The script is scheduled to run every day at 1am.
- Backups that are at least one-week-old, are deleted to save space.

### Locations

- Backups: /media/backup
- Error log: /var/opt/backup-log/error.log
