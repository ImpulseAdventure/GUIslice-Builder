# The is the full set of fonts supported by the Builder.
# Includes arduinofonts.csv for arduino platform and linuxfonts.csv for linux.
# You can add or remove fonts from this file to support any font that exists on your target platform.
#
# The file is in CSV Format with a column header line - See RFC 4180
# Using a '#' character to comment out lines is an unsupported extension to this RFC so if you want
# to import this file to another program like Excel you need to delete lines with '#' first.
#
# Here DisplayName refers to the actual font on the target platform - Arduino for example.
# Logical means the font we should use when Builder displays text since 
# we can't actually use the target platforms fonts inside the Builder. 
# However, since the Builder is written in Java you should be able to use 
# any TTF font as a logical font on your machine, but finding them is left up to you. 
# 
# Java ships with five platform independent fonts: Dialog, DialogInput, SansSerif, Serif, and Monospaced.
# I have choosen to use Dialog to represent Adafruit's built-in fonts which are 5x8 and plain only.
# Adafruit GFX only scales them up from 1 to N. As an example scale of 2 gives you a character 10x16.
# I have supported scales of 1 to 5.  You can edit this as you desire.
#
# Now thae Java independent fonts really don't match exactly what you will see on your embedded platform.
# However its possible to load on your Java platform the fonts you can use on the target platform
# in some cases.  For example, GNU's freefonts are supplied by AdaFruit's GFX package.
# If you load these fonts on your java platform you can get the builder to use them instead of
# of the Java independent fonts.  See the Builder's User Guide for more details.
#
# One thing you should keep in mind is that fonts take up a fair amount of memory
# so you should limit your selection to one or two fonts if your target platform is an Arduino.  
#
# Use # to comment out any lines you don't want or simply delete them.
#
# WARNING! No error checking exists in the font code so be very careful with any edits.
# I personally used a spreadsheet to create these files then did an export to CSV.
# I strongly suggest you do the same if you have any major edits to make.
#
#     Column Titles - see GUIslice.h gslc_FontAdd() for its API parameter names and what they mean.
#
