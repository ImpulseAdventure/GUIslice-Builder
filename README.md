# GUIslice Builder

Please refer to the wiki page for installation and usage details:

[GUIslice Builder - Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki/GUIslice-Builder)

![image](https://user-images.githubusercontent.com/8510097/90728338-9a8be100-e279-11ea-969e-cbd8bb0ac6c6.png)

## Brief Overview
The GUIslice Builder is a standalone desktop application that is more than a layout tool for your UI. It's designed as 
Low Code GUI Generator for User Interfaces that make use of GUIslice API. 

The cross-platform utility includes a WYSIWYG graphical editor that enables drag & drop placement of UI elements. Once a GUI has been laid out, the Builder can then generate a fully functional GUIslice Graphical UI complete with plug-in points for your custom application. 

The GUIslice API framework code can handle hardware like Arduino, ESP8266, ESP32 and more. It supports Graphic libraries like 
Adafruit's GFX, M5Stack, TFT_eSPI and LINUX with a wide variety of TFT Display Drivers and combinations of Touch support chips.

The generated output code (*.c, *.ino, *_GSLC.h) includes all of the necessary defines, UI storage elements and initialization 
in addition to the placement of the UI elements. This will greatly improve the ease in creating a new Graphical Application.

You can find Example project files inside GUIslice/examples/builder

## Disclaimer ##
The Software is not designed for use in devices or situations where there may be physical injury if the Software has errors.

## Builder Contents
Note that the Builder executables and User Guide are attached to the latest GUIslice Builder Repository [Release Notes](https://github.com/ImpulseAdventure/GUIslice-Builder/releases):

## Builder Source Code
The Builder source code is located in this repository [ImpulseAdventure/GUIslice-Builder](https://github.com/ImpulseAdventure/GUIslice-Builder)
- Build instructions can be found in `BUILD.txt`

## Release History

### Hot Fix 0.17.b04
- Issue `#184` GUIslice Builder crash when changing Minimum Value at Ramp Gauge

### Enhancements for 0.17.b03
- Issue `#147` Allow multiline text for text button
NOTE: Sizing of a Text button using multiple lines of text is the users responsiblity. You will need to set 
the height and width manually.  Ending each line inside the Label with \n. Example, "line1\nline2\nline3".
The Builder will remove each \n and replace with a decimal 10 (newline) when you press enter key.

### Hot Fix 0.17.b02
Yet another fix for critical Log4Shell vulnerability inside Apache Log4j. The vulnerability applies 2.0 to 2.15.0 of Apache Log4j.  

### Hot Fix 0.17.b01
Fix for critical Log4Shell vulnerability inside Apache Log4j. The vulnerability was publicly disclosed via GitHub on December 9, 2021. Versions 2.0 and 2.14.1 of Apache Log4j have been impacted.  

### Enhancements for 0.17.0
GUIslice 0.17 added improved support for external inputs such as physical buttons, encoders or pins. Users can now 
traverse and edit most widgets in the GUI using external inputs. This feature required minor changes to Builder. 
Some elements now require a slightly larger margin around them (ie. 1px) to accommodate a potential focus frame. 
For example, between a slider and the listbox.

Added support for UTFT library using Target Platform "utft". Full native font support for fonts downloaded from 
[UTFT Fonts](http://www.rinkydinkelectronics.com/r_fonts.php). Simply download and drop them into GUIsliceBuilder/fonts/utft.
The Builder ships with the UTFT DefaultFonts (SmallFont, BigFont, and SevenSegNumFont) installed.
See User Guide section 5.7 Adding Fonts for more details.

### Bug Fixes for 0.17.0
- Issue `#175` Inserting a unicode character into text field caused crash
- Issue `#174` Crash when input non-ascii chars when you first didn't change to font that supports unicode.
Note you still must select an appropiate font before enterring characters or they will appear as blanks. 
- Issue `#174` Crash when deleting a base page.
- Issue `#174` Crash if you are inside color chooser and click on blank space inside recent colors box.
- GUIslice discussion -> xTextBox - writing to a specific character position `#392`.  Setting a Grid positions minor or major to a width or height of zero caused crashes. 

### Enhancements for 0.16.0

With this release the Builder becomes a true WYSIWYG editor for most platforms. The Builder now reads and parses 
your actual Platform Font header and c files and renders them inside the Builder. This native font support will now 
be able to give you accurate sizing and positioning information for supported fonts. Plus your text will be now 
displayed exactly as it will appear on your target TFT display. 

The Native Font support includes, Adafruit's Builtin GLCD fonts, and Adafruit's GFX compatable fonts, and 
Teensy ILI9341_t3 fonts. This Native Font support includes the ability to add your own fonts to the Builder 
simply by dropping them into the proper folders. You may also tell the Builder to simulate fonts it doesn't directly support. 

See the User Guide for more information about font handling and how to add your custom fonts.

GUIslice API 0.16.0 has upgraded the Keypad support. This is a breaking change for the API so the Builder 
will now require running with GUIslice API 0.16.0 and higher. For further information on keypads 
refer to the wiki page [Custom KeyPads](https://github.com/ImpulseAdventure/GUIslice/wiki/Custom-KeyPads)

The Builder has added support for ctrl-Z for Undo and ctrl-Y for Redo.

### Bug Fixes for 0.16.b011
- Issue 169 - Textbox double quotes are not escaped in generated code
- Issue 172 - Fill enable , Disable not working with text input

### Enhancements 0.16.b010
Added support to group toggle buttons.

### Bug Fixes for 0.16.b010
- Issue 157 - Target platform linux fails to generate callback for seekbar
- Issue 167 - Fill enable , Disable not working with text input
- Issue 168 - Image Button and Toggle Button Jump page ENUM not generating code

### Enhancements 0.16.b009
Added support for Image Buttons to behave as a toggle button. Simply set Property View: Toggle? = true 
this will then use the new GUIslice API XToggleImgbtn control.

### HotFix for 0.16.b009
 - Bug Fix Character Map causes fatal crash issue `#165` 

### HotFix for 0.16.b008
 - Bug Fix for DEL button on keyboard causes crash issue `#154`  
 - Bug Fix for Copy paste of Image Button causes crash `#155`

### Enhancements 0.16.b007
Replaced Ribbon with a simplier design using JRibbonBar provided by Csekme Krisztián. 
His github project is at:
[JRibbonBar](https://github.com/csekme/JRibbonMenu)

Hopefully this will fix the crashes and burns we have been having on Apple's OSX and various odd cursor behaviors that have occurred on different Linux platforms.

Also added two additional Themes: Solarized Dark and Light themes.

Our User Guide has been updated with the new UI layout.

### Enhancements 0.16.b006
Added support for TFT_eSPI smoothfonts (*.vlw). Google's Dosis Bold and NotoSans Bold is built in. 

You may add your own vlw fonts by following the updated User Guide chapter 5.7 Adding Fonts and Appendix G Creating VLW Fonts.

### Bug Fixes 0.16.b006
 - Bug No. 205 No CallBack from Checkbox using Flash API issue `#347` 

### HotFix for 0.16.b005
 - Bug No. 204 Crash  occurs while selecting NotoLatin1 when size of font is > 28 issue `#143`
 - Bug No. 203 Box using Flash API can't do code gen for Draw callback
 - Bug No. 202 Draw function selected for Box frame the rounded property should be disabled API issue `#326`

### Enhancements for 0.16.b004
Added CharacterMap dialog to "Text" propery fields for UIElements Text, Text Button, Text Input, Spinner, and Number Input.

This feature will allow you access to graphics characters previousily hidden in Adafruit's classic (Builtin) fonts and other fonts. 
You can also access ISO8859 international characters using a newly added NotoLatin1 font.

First set your desired font then invoke CharacterMap by right clicking on the "Text" property and selecting CharacterMap. 
Select your characters one at a time and then press copy to append them to your text field.

The User Guide has been updated documenting this new feature.  

The UI Element Spinner has been upgraded to support changing the Increment and Decrement Arrow characters using the API call gslc_ElemXSpinnerSetChars().


### Bug Fixes 0.16.b004
 - Bug No. 201 Text button frame cannot be disabled issue `#139`

### HotFix 0.16.b003
 - Bug No. 200 New Keypad support causes input field button case-statement to be deleted and recreated
 - Bug No. 199 Sizing of rect for Text with margin doesn't include space on right-side

### HotFix 0.16.b002
GUISlice crashing after clicking generate code issue `#134`
- Bug No. 198 Assigning storage to text field gives incorrect height API issue `#292`

### HotFix 0.16.b001
 - Bug No. 197 Crash opening project with screen larger that 320x240
 - Bug No. 196 Dup gslc_ElemSetTxtCol when text not set to the default color

### Bug Fixes 0.16.0
 - Bug No. 195 Enhancement - Add Top/Bot to Text Button text alignment fields `#127`
 - Bug No. 194 gslc_GetImageFromRAM creates const pointer issue `#126` 
 - Bug No. 193 codegen fails to work for GSLCX_CHECKBOX_STYLE_ BOX or ROUND issue `#128`
 - Bug No. 191 Crash if slider min=max
 - Bug No. 189 Add shortcut ctrl+z for UnDo
 - Bug No. 187 App window not controllable with Java built-in themes issue `#112`
 - Bug No.  98 Text element "Fill Enabled=false" doesn't render with transparency

