# GUIslice Builder

Please refer to the wiki page for installation and usage details:

[GUIslice Builder - Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki/GUIslice-Builder)

### Builder Contents
Note that the Builder executables and User Guide are attached to the latest GUIslice Builder Repository [Release Notes](https://github.com/ImpulseAdventure/GUIslice-Builder/releases):

### Builder Source Code
The Builder source code is located in this repository [ImpulseAdventure/GUIslice-Builder](https://github.com/ImpulseAdventure/GUIslice-Builder)
- Build instructions can be found in `BUILD.txt`

### Brief Overview
The GUIslice Builder is a standalone desktop application that is designed to help generate layouts for GUIslice.

The cross-platform utility includes a graphical editor that enables drag & drop placement of UI elements. Once a GUI has been laid out, the Builder can then generate the functional GUIslice skeleton framework code, for both Arduino and LINUX targets.

The generated output code (*.c, *.ino, *_GSLC.h) includes all of the necessary defines, UI storage elements and initialization in addition to the placement of the UI elements. This should greatly improve the ease in creating a new GUI.

You can find Example project files inside GUIslice/examples/builder

### Release History

#### Changes for 0.13.b024

Update to User Guide for version 0.13.b024

Support for new Themes based upon the FlatLaf project also supporting user loading of Intellij Themes.  The existing custom themes have been removed due to the lack of support. See Appendix D of the new User Guide.

Further enhancements to the code generation of Button callbacks.  See Appendix E of the User Guide for more details.

Button callbacks "Jump to Page? True/False" and "Show Popup Page? True/False" have been replaced with simply specifying the Target Page to go to.  See Section 4.18 Text Button of the User Guide for more detalls.

Code Generation templates are now exposed and editable for customization. See Appendix F of the User Guide.

##### Bug Fixes
 - Bug No. 154 Jump to Page?=true for button then not setting PAGE ENUM crashes code gen
 - Bug No. 153 Unable to generate UTF-8 character Literals
 - Bug No. 152 Windows 7 users can't resize Builder's TFT Simulation display
 
#### Changes for 0.13.b023
Enhancement that will now save and restore window frame and panel resizing.

Added support for Google's Dosis and Noto(tm) fonts with their permissive licensing.  This is in addition to GNU's FreeFonts and their restrictive GNU General Public License version 3.0 or later license.

To use these fonts with Adafruit GFX simply copy them from the Builder's installation folder GUIsliceBuilder/gfx_fonts to your Adafruit_GFX_Library/fonts folder.

As before you may continue to add your own fonts to the Builder by following the instructions in the User Guide Appendix B.

#### Changes for 0.13.b022
Replaces 0.13.b021 which crashed if you used FreeFonts or any other custom fonts due to fonts not installed correctly in the *_GSLC.h file.

Enhancement to split [project name].ino file into  [project name].ino and [project name]_GSLC.h header that contains GUIslice API storage, Fonts, Element Initialization and other items users generally don't need to modify.  While [project name].ino will remain where users must do their code injections.

Target Platform linux will continue to use a single [project name].c file. Also, existing projects will be upgraded automatically with the existing [project name].ino renamed to [project name].ino.orig in case of any problems with the upgrade.  

Enhancement to File Chooser to maintain a list of recently accessed project files and to open the last accessed folder.

#### Changes for 0.13.b020
Fix for SnapTo grid will only work for dragging single object. Multiple objects being dragged will still ignore SnapTo setting otherwise spacing between objects would get distorted.

##### Bug Fixes
 - Bug No. 148 Dragging an object doesn't snap to grid when preferences has SnapTo=true

 #### Changes for 0.13.b019
Enhancements and bug fixes made to paste allowing users to copy a group of UI Elements and keep the original positioning.  Checkboxes and Radio Buttons now will create a default Element Reference name when users turn on callbacks.

Support is back in for dragging multiple UI Elements. This had been removed due to problems handling Zoom magnification.

We now support selecting Jpeg images if you are using TFT_espi drivers. It is assumed in this case you will be using SPIFFS. This is in anticipation of support being added to GUIslice API.  

##### Bug Fixes
 - Bug No. 146 Request that input fields support Text property
 - Bug No. 147 Pasting an Element with ElementRef incorrectly copies it.

#### Changes for 0.13.b018

##### Bug Fixes
 - Bug No. 143 Arduino IDE Auto Format will move tags so Builder can't find them
 - Bug No. 144 Changing Keypad font to one not previously used will result in it not being found
 - Bug No. 145 Code gen output for gslc_ElemCreateTxt_P_R has spurious  ">".

#### Changes for 0.13.b017
Enhancement to Button Callbacks to better determine when case statements need to be regenerated. We now test for button model modifications against any existing source code like adding a jump to page, showpopup, change to page, change in the name of page enum, change of a pElementRef name for Input Fields.

##### Bug Fixes
 - Bug No. 141 Input + Text fields need default colors turned off to see frame box at runtime.
 - Bug No. 142 Improve button callback generation vs existing source code.

#### Changes for 0.13.b016
Added Grid, Zoom In and Zoom Out icons to the mini-toolbar to better support non-english keyboards.

##### Bug Fixes
trap java.lang.IllegalStateException: cannot open system clipboard

#### Changes for 0.13.b015

##### Bug Fixes
 - Bug No. 138 Copy & paste of Numeric Input causes duplicate m_pElemVal 
 - Bug No. 139 Copy & paste of Numeric Input rendering ignores field size
 - Bug No. 140 Numeric Input Field Size renders one less character

#### Changes for 0.13.b014

##### Bug Fixes
 - Bug No. 137 Tree view reorganization causes fatal error

#### Changes for 0.13.b013

##### Bug Fixes
 - Bug No. 132 Application hangs when trying to run it under Linux Debian Buster with xfce4 desktop
 - Bug No. 133 Fatal error when deleting/selecting popup in tree view
 - Bug No. 134 Provide error check in template to catch case user selects incorrect Target Platform (ie. TFT_eSPI)
 - Bug No. 135 In some themes, the app window is not controllable because of missing title bar
 - Bug No. 136 Number Input ERROR: GetXDataFromRef(Type 4116, Line 254) Elem type mismatch

#### Changes for 0.13.b012
Bug 132 turns out to be a major issue on Linux with the Optional LAFs that causes the UI to only appear as the user moves the mouse over hidden UI panels. The Ribbon support we use is based upon an open project called insubstantial. This project also supplies our optional Look and Feels. The project, however, is no longer supported and the bug fix for linux is way beyond our ability to repair. A new version of the project exists and is supported but requires Java 9 and above. 

The Ribbon and other features work fine so the solution is to remove the optional LAFs for linux. They will still be available in windows 10 since no issues have been reported. Now however, Users will startup in the default Java system LAF for their platform.  Users (except for Macs) can go to edit->options->Themes and change to any available LAF.

A future version will upgrade LAFs to a supported Open Source Project that has modern LAFs for swing. For example, The FlatLAF project.

##### Bug Fixes
- Bug No. 132 Application hangs when trying to run it under Linux Debian Buster with xfce4 desktop

#### Changes for 0.13.b011
The Listbox edit list items dialog (entered by pressing '...' in its property tab) now has Move Up and Down for reordering items. You could and still can simply select a row and drag then drop to a new location but the new move commands will make it clearer.

##### Bug Fixes
 - Bug No. 127 Incorrect visual display width for ListBox without scrollbar
 - Bug No. 128 Fatal error when ctrl-c copy then paste element selected in treeview
 - Bug No. 129 Fatal error when pasting after undo of copy element
 - Bug No. 130 Random duplicate key and enum collisions of UI elements
 - Bug No. 131 Enhancement - add move up and move down commands to Listbox edit items dialog

 #### Changes for 0.13.b010

##### Bug Fixes
 - Bug No. 122 VSCode/PlatformIO Type error with gen code (char*) for ListBox should be (uint8_t*)
 - Bug No. 123 code gen fails when any text/label contains '$'
 - Bug No. 124 case statements generated for deleted UI elements in callbacks
 - Bug No. 125 code gen for keypad input fails with IllegalArgumentException
 - Bug No. 126 E_FONT_TXT5 not declared for keypad if no other text fields present

#### Changes for 0.13.b009 

##### Bug Fixes
 - Bug No. 120 gen code defines an empty string for ring gauge text while builder shows percent
 - Bug No. 121 Builder doesn't return the keypad result back to the linked text element in callback

#### Changes for 0.13.b008 

##### Bug Fixes
 - Bug No. 118 Platform  Arduino TFT_eSPI, should not have include Fonts/...
 - Bug No. 119 Delete extra page causes main page removal

#### Changes for 0.13.b007 

##### Bug Fixes
 - Bug No. 117 Code Gen for Target Platform -Arduino TFT_eSPI Missing template: <FONT_TFT_ESPI>

#### Changes for 0.13.b006 

##### Bug Fixes
 - Bug No. 116 Listbox gslc_ElemXListboxItemsSetGap missing colGap

#### Changes for 0.13.b005 

##### Bug Fixes
 - Bug No.  76 (re-open)Image file selection always opens to arduino_res should remember last directory accessed
 - Bug No. 113 Flash _P() macros with custom color parameters needs to be wrapped with parentheses
 - Bug No. 114 Builder should validate image formats to be sure they are supported
 - Bug No. 115 Builder should validate Page ENUM is used for jump page on Text+Image Button Callbacks

#### Changes for 0.13.0.4 Hot Fix for Use Flash API calls

##### Bug Fixes
 - Bug No. 107 RingGauge: undo button doesn't update Flat Color / Gradient color property visibility
 - Bug No. 112 Code gen fails for "Use Flash API" mode

 #### Changes for 0.13.0.3 Hot Fix for Adafruit GFX font usage

##### Bug Fixes
 - Bug No. 104 RingGauge: default Flat Color property should be GSLC_COLOR_BLUE_LT4
 - Bug No. 105 RingGauge: RingGauge: default Inactive Color property should be (gslc_tsColor) {0,0,48}
 - Bug No. 106 RingGauge: TFT Simulation incorrectly shows Flat Color property
 - Bug No. 108 RingGauge: codegen invalid API call to ElemXRingGaugeSetColorActiveFlat()
 - Bug No. 109 Can't Install GUIsliceBuilder as a NON-Admin user
 - Bug No. 110 (Issue 28) Compilation error in Adafruit-GFX.h fonts include typo 
 - Bug No. 111 (Issue 26) Compilation error: gslc_ElemXRingGaugeSetAngleRange(&m_gui,pElemRef, 270, 360, RING-102);

#### Changes for 0.13.0.2 Hot Fix for projects created with early beta versions of the builder

##### Bug Fixes
 - Bug No. 102 crash running code generation due to missing tags in .ino or .c files for older projects
 - Bug No. 103 Upgrading older project files causes duplicate storage to be assigned

#### Changes for 0.13.0.1 Hot Fix for linux and mac/os

##### Bug Fixes
 - Bug No. 100 Linux target platform gives missing FONT_INCLUDE template 
 - Bug No. 101 Builder fails to load in mac/os can't find starting class

#### Changes for 0.13.0

### Known Issues for Release 0.13.0

 - Bug No. 48 Crash on generate code in macOS when space in file name. 
 - Bug No. 86 Need to double click mouse twice sometimes to get a selection.
 - Bug No. 91 Fails to trap "invalid" filenames that are not permitted by the Arduino IDE

##### New UI Elements Supported

 - Line
 - Listbox
 - Number Input
 - Radial Gauge
 - Ramp Gauge
 - Ring Gauge
 - Spinner
 - Text Input
 - Base Page
 - PopUp Dialog Page

#### Removed Features
 - Import Button for importing non-builder created projects. Round trip edits of builder created files are still supported. Maintence costs for the Import feature were too high to continue support.

##### Bug Fixes
 - Bug No. 7  Support transparency in BMP 
 - Bug No. 12 All widgets should allow renaming ElementRef field
 - Bug No. 27 Ability to duplicate an elements 
 - Bug No. 65 Creating saved ElemRef should follow ElemCreate instead of using PageFindElemById
 - Bug No. 66 XTextBox bad values sent to the gslc_ElemXSliderCreate() 
 - Bug No. 68 Regenerating code increments m_sX* array indices in ElemX*Create()
 - Bug No. 69 Box UI for tick func generates gslc_ElemSetTxtAlign() instead of gslc_ElemSetTickFunc()
 - Bug No. 70 Code gen puts out gslc_ElemCreateBox instead of gslc_ElemCreateBox_P version for arduino flash
 - Bug No. 71 Code gen for XGraph crashes 
 - Bug No. 72 remove Auto gen id in arduino flash _P routines 
 - Bug No. 73 remove save folder restrictions
 - Bug No. 75 Widget select+move sometimes unresponsive
 - Bug No. 76 Image file selection always opens to arduino_res Remember last directory in which an image was selected
 - Bug No. 77 Changed global option Target Image Directory isn't retained without pressing Enter first before OK 
 - Bug No. 79 Added status bar to avoid confirmation dialogs 
 - Bug No. 80 Deleting a Button Image does not remove the corresponding case in the CbBtnCommon callback function.
 - Bug No. 81 Extended widgets headers no longer use GUIslice_ex.h 
 - Bug No. 82 keyboard shortcutsfor the frequently used System related functions (Save Export Delete etc.). 
 - Bug No. 83 request to resize panes 
 - Bug No. 85 grouping radio buttons always assigns GROUP0 
 - Bug No. 87 Crtrl-D is supported for deletions but not Command Delete key
 - Bug No. 88 Default target image path needs leading slash “/“ on SD filenames
 - Bug No. 89 Element drag when in zoomed mode doesn't track mouse 
 - Bug No. 90 checkboxes and radio buttons should have either width or height. 
 - Bug No. 92 Change the default background color to black 
 - Bug No. 96 Fatal error can cause crash log loop 
 - Bug No. 97 Install not change project directory
 - Bug No. 99 Progress Bar Frame not showing up on TFT simulation screen

