
Additional ILI9341_T3 fonts avaible from github:
https://github.com/PaulStoffregen/ILI9341_fonts

Example, say you want the Droid Sans fonts:
Create a folder for a new family within this folder fonts/t3 called Droid
now create subfolders BOLD, PLAIN 
Then copy the headers and c files to their proper folders and the new structure should look like:

fonts
  |- t3
    |- Arial
    |- DroidSans
      |- BOLD
        font_DroidSans_Bold.c
        font_DroidSans_Bold.h
      |- PLAIN
        font_DroidSans,c
        font_DroidSans.h
        
Restart the Builder and they should show up in the Font Chooser Dialog.        
        