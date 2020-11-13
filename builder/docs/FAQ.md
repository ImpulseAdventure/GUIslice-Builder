

# Frequently Asked Questions

Please refer to the wiki page for installation and usage details:

GUIslice Builder
- [Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki/GUIslice-Builder)

GUIslice API
- Extensive [Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki) guides available
- [GUIslice API documentation (online)](https://impulseadventure.github.io/GUIslice/modules.html) & [(PDF)](https://github.com/ImpulseAdventure/GUIslice/raw/master/docs/GUIslice_ref.pdf)

**Publication date and software versions**

Published November, 2020. Based on GUIslice Builder 0.16.0 and GUIslice API Library 0.16.0

## Using the GUIsliceBuilder

### GUIsliceBuilder fails to start in windows.

Check GUIsliceBuilder/logs/builder.log for any errors.

The Builder logs everything so if the folder GUIsliceBuilder/logs is empty then it
sounds like the environment variable JAVE_HOME isn't set correctly.

Open a terminal cmd window using cmd.exe
First enter 
```
echo %JAVE_HOME%
```
If you are reusing Arduino's IDE Java than it would be something like
C:\Program Files (x86)\Arduino\java

It must point to the top level folder containing Java runtime whereupon /bin/javaw.exe will be appended 
to actually run the builder. 

If you notice the path set inside JAVA_HOME is incorrect you can simply rerun the Builder's install to correct it.

If that looks ok enter
```
%JAVA_HOME%\bin\java -version
```
You should get something like
```
java version "1.8.0_192"
Java(TM) SE Runtime Environment (build 1.8.0_192-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.192-b12, mixed mode)
```
If you read our wiki for the installation you will note we require Java 1.8 not any higher although 
it has been used with Java 9 and 11 without issue.

If all of this is good then cd to the GUIsliceBuilder installation folder for example:
```
cd C:\Program Files (x86)\GUIsliceBuilder
```
then try running the Builder directly using this command
```
%JAVA_HOME%\bin\java -jar lib\builder-0.15.b006.jar
```
Assuming you are using 0.15.b006 otherwise look inside the lib folder for the name of the jar file.
Now just check for whatever error is reported.


### I switched a project over to using flash for UI Elements and it's not working, Why?

If you have an older project one thing to check for inside your callbacks is a statement to access your Element, say for pElem->Id, and you see:
```
  gslc_tsElem*    pElem     = pElemRef->pElem;
```
You will need to change this for Flash based elements and instead use:
```
  gslc_tsElem* pElem = gslc_GetElemFromRef(&m_gui,pElemRef);
```
The newer Builder will no longer generate the `pElement->pElem` code and all examples have also been update with the new code.

### Can't get a floating point number in a Text Field

Attempting to use 'sprintf(acTxt,4,"%f",fValue);' won't work with Arduino's runtime.
Floating Point number are not supported by sprintf or snprintf. However you can use dtostrf().
Example:
```
static float f_val = 123.6794;
static char outstr[15];

void setup() {
  dtostrf(f_val,7, 3, outstr);

  Serial.begin(9600);
  Serial.println(outstr);
}

void loop(){
}
```
Output: 124.679


### Compiler Error when I try and use TFT_espi Display Driver
If the error is Builder config "Edit->Options->General->Target Platform" should be "arduino TFT_eSPI"

Then its means you are using one of GUIslice's "esp-tftespi-*.h" config files but you haven't told the Builder that this is your target platform.

The Builder supports the following target platforms:
-  arduino
-  arduino TFT_espi 
-  linux

Just follow the error message's guidance to reset your target platform. This allows the Builder to correctly handle fonts between the different packages.

---------
<div style="page-break-after: always;"></div>

### Can you change a Text String at runtime?

Yes. You just need to first assign storage to the text string inside the property view and optionally you should assign a meaningful name to the ElementRef so you can better keep track.
As an example say I have a global counter m_nCount and a Text UI Element called E_TXT_COUNT:

| Key                   | Text$2               |
|-----------------------|----------------------|
| ENUM                  | E_TXT_COUNT     |
| X                     | 70                   |
| Y                     | 60                   |
| Width                 | 44                   |
| Height                | 12                   |
| ElementRef            | <span style="color: red;">**m_pElemCnt**</span>           |
| Font                  | BuiltIn(1x)->5x8pt7b |
| Text                  |                      |
| UTF-8?                | false                |
| External Storage Size | <span style="color: red;">**7**</span>                    |
| Text Alignment        | GSLC_ALIGN_MID_LEFT  |
| Fill Enabled?         | true                 |
| Frame Enabled?        | false                |
| Use Flash API?        | false                |
| Use Default Colors?   | true                 |

Now inside my program I can reference this text element and place the value of m_nCount into it for display.

```
uint32_t   m_nCount = 0;
char       acTxt[8];
  m_nCount++;
  snprintf(acTxt, MAX_STR, "%lu", m_nCount / 5);
  gslc_ElemSetTxtStr(&m_gui, m_pElemCnt, acTxt);
```

Note the `%lu` if I had defined m_nCount as uint16_t I would have used `%u` instead.
You can see a full example inside ex04 supplied with GUIslice API library.

---------
<div style="page-break-after: always;"></div>

## GUIslice API

### What is the difference between Base Page, Page, and Popup page?

Actually this is pretty simple.

I suspect you already understand what a page is.  Simply a full screen menu.  You may have as many as you want given enough memory.

A popup page is one that will overlay the current page still showing whatever is not covered up by the popup. The keypad is an example.

You can have optionally one base page.  This is where you place any elements you want to show on all menus. Say a status line on the bottom of the display.

The base page is output first, followed by the current page on top followed by any popups that might occur.  They are simply layers.

### Why doesn't PoPup page have properties such as location and size?
This is because the Popup page is an overlay.  It has no size. Whatever elements you place on it overlays the page it came from.

A common usage would be to add a box as the first UI element to frame the popup but its not required nor are you limited to one framing box.

### How can I get input field to work with a Popup?

You will have noticed that if you add say a Input field Text or Number the Keypad comes up and after the user enters data the Keypad goes away taking you back to the original page that first invoked your Popup.

Turns out you can easily get your Popup to re-display with the new input data.

The key to whole thing is inside the CbKeypad callback.  You simply need to add a line to re-display your popup screen after the keypad input get as so:

```
      case E_INPUT_QUESTION: 
        gslc_ElemXKeyPadInputGet(pGui, m_pElemQuestion, pvData);
        gslc_PopupShow(&m_gui, E_PG_POPUP_INFO, true);
        break;
```

### How can I turn Elements on and off

If your UI Element is click-able you can turn this feature on/off with this call:

```
  gslc_tsElemRef*  m_pElemName       = NULL;
  gslc_ElemSetClickEn(&m_gui,m_pElemName,true|false);
```

---------
<div style="page-break-after: always;"></div>

### How can I display to the user that an element is disabled?

I would suggest changing the colors of the frame and background.

```
  gslc_tsElemRef*  m_pMyBTN       = NULL;
  gslc_ElemSetClickEn(&m_gui,m_pMyBTN,false);
  gslc_ElemSetCol(&m_gui,m_pSetupBTN,GSLC_COL_RED,GSLC_COL_BLACK,GSLC_COL_BLACK);
```

Then when you re-enable it simply restore click-able and its colors.

```
  gslc_ElemSetClickEn(&m_gui,m_pMyBTN,true);
  gslc_ElemSetCol(&m_gui,m_pMyBTN,GSLC_COL_BLUE_DK2,GSLC_COL_BLUE_DK4,GSLC_COL_BLUE_DK2);
```

### How can I Hide a UI Element until some external event occurs?

You just make it visible or not.

```
  gslc_tsElemRef*  m_pElemName       = NULL;
  gslc_ElemSetVisible(&m_gui,m_pElemName,true|false);
```

### XSpinner Element missing arrows if non built-in fonts used

Simply tell the XSpinner Element what characters to use instead, say '+' and '-'.

```
  gslc_tsElemRef*  m_pElemSpinner1   = NULL;
  gslc_ElemXSpinnerSetChars(&m_gui,m_pElemSpinner1,'+', '-');
```

### Can I change the Text Label of a TXTButton at runtime?

Yes.

```
  gslc_tsElemRef*  m_pMyBTN       = NULL;
  gslc_ElemSetTxtStr(&m_gui, m_pMyBTN, "????");
```

Note however, The button can't dynamically change size at runtime so don't place more characters then will fit in the button's frame.

### Can I make a long-press button to continuously add a value until released?

Button callback functions are triggered when you first start touching a button, while still holding and when releasing it. 
 
Starting with the basic ex02_ard_btn_txt example, replace the callback with the following:
```
  int16_t m_nCount = 0;         // Simple counter
  uint32_t m_tmLastPress = 0;   // Time of last press interval

  // Button callbacks
  bool CbBtnQuit(void* pvGui,void *pvElemRef,gslc_teTouch eTouch,int16_t nX,int16_t nY)
  {
    if (eTouch == GSLC_TOUCH_DOWN_IN) {         // Started press (inside button)
      m_tmLastPress = millis();
    } else if (eTouch == GSLC_TOUCH_UP_IN) {    // Released (inside button)
      m_nCount += 1;
      GSLC_DEBUG_PRINT("Count = %d\n",m_nCount);
    } else if (eTouch == GSLC_TOUCH_MOVE_IN) {  // Continued press (inside button)
      if ((millis() - m_tmLastPress) > 500) {
        m_nCount += 10;
        m_tmLastPress = millis();
        GSLC_DEBUG_PRINT("Count = %d\n",m_nCount);
      }
    }
    return true;
  }
```

### How can I reset the buffer of the XTextBox element?

Call:

```
  gslc_tsElemRef*  m_pElemTextbox    = NULL;
  gslc_ElemXTextboxReset(&m_gui,m_pElemTextbox);
```

