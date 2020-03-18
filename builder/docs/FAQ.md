
# Frequently Asked Questions

Please refer to the wiki page for installation and usage details:

GUIslice Builder
- [Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki/GUIslice-Builder)

GUIslice API
- Extensive [Documentation](https://github.com/ImpulseAdventure/GUIslice/wiki) guides available
- [GUIslice API documentation (online)](https://impulseadventure.github.io/GUIslice/modules.html) & [(PDF)](https://github.com/ImpulseAdventure/GUIslice/raw/master/docs/GUIslice_ref.pdf)

## Using the GUIsliceBuilder

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

The key to whole thing is inside the CbKeypad callback.  You simply need to add a line to re-display your popup screen after hiding the keypad popup as so:

```
      case E_INPUT_QUESTION: 
        gslc_ElemSetTxtStr(pGui, m_pElemQuestion, gslc_ElemXKeyPadDataValGet(pGui, pvData));
        gslc_PopupHide(&m_gui);

        gslc_PopupShow(&m_gui, E_PG_POPUP_INFO, true);
        break;
```

### How can I turn Elements on and off

If your UI Element is click-able you can turn this feature on/off with this call:

```
  gslc_tsElemRef*  m_pElemName       = NULL;
  gslc_ElemSetClickEn(&m_gui,m_pElemName,true|false);
```
        
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

### Can I change the Text Label of a TXTBUtton at runtime?

Yes.
 
```
  gslc_tsElemRef*  m_pMyBTN       = NULL;
  gslc_ElemSetTxtStr(&m_gui, m_pMyBTN, "????");
```

Do note however, The button can't dynamically change size at runtime so don't place more characters then will fit in the button's frame.

### How can I reset the buffer of the XTextBox element?

Call:

```
  gslc_tsElemRef*  m_pElemTextbox    = NULL;
  gslc_ElemXTextboxReset(&m_gui,m_pElemTextbox);
```

