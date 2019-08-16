# GUIslice Builder

Please refer to the wiki page for installation and usage details:

[GUIslice Builder - Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki/GUIslice-Builder)

### Builder Contents
[GUIslice Builder Sources](https://github.com/ImpulseAdventure/GUIslice-Builder-source)

Note that the Builder executables and User Guide are attached to the latest GUIslice API releases. [Release Notes](https://github.com/ImpulseAdventure/GUIslice/releases):

### Brief Overview
The GUIslice Builder is a standalone desktop application that is designed to help generate layouts for GUIslice.

The cross-platform utility includes a graphical editor that enables drag & drop placement of UI elements. Once a GUI has been laid out, the Builder can then generate the functional GUIslice skeleton framework code, for both Arduino and LINUX targets.

The generated output code (.c, .ino) includes all of the necessary defines, UI storage elements and initialization in addition to the placement of the UI elements. This should greatly improve the ease in creating a new GUI.

You can find Example project files inside GUIslice/examples/builder

### Known Issues for Release 0.13.0

 - Bug No. 48 Crash on generate code in macOS when space in file name. 
 - Bug No. 86 Need to double click mouse twice sometimes to get a selection.
 - Bug No. 91 Fails to trap "invalid" filenames that are not permitted by the Arduino IDE

### Release History

#### Changes for 0.13.0

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

##### Removed Features
 - Import of .ino or .c files. Maintenance of this feature was simply too high to continue support. 
 
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
 - Bug No. 97 Progress Bar Frame not showing up on TFT simulation screen
