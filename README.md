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

The generated output code (.c, .ino) includes all of the necessary defines, UI storage elements and initialization in addition to the placement of the UI elements. This should greatly improve the ease in creating a new GUI.

You can find Example project files inside GUIslice/examples/builder

### Release History

#### Changes for 0.13.b011

##### Bug Fixes
 - Bug No. 127 Incorrect visual width for ListBox without scrollbar
 - Bug No. 128 Fatal error when ctrl-c copy then paste element selected in treeview
 - Bug No. 129 Fatal error when pasting invalid referenced element
 - Bug No. 130 Random duplicate key and enum collisions of UI elements

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
 
