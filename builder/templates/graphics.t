<APP_HDR>
// FILE: [$<FILENAME>]
// Created by GUIslice Builder version: [$<VERSION>]
//
// GUIslice Builder Generated File
//
// For the latest guides, updates and support view:
// https://github.com/ImpulseAdventure/GUIslice
//
<STOP>
<BACKGROUND>
  
  // Set Background to a flat color
  gslc_SetBkgndColor(&m_gui,$<BACKGROUND_COLOR>);
<STOP>
<BACKGROUND_IMAGE>
  
  // Set Background to an image
  gslc_SetBkgndImage(&m_gui,$<IMG-109>$<IMG-101>,$<IMG-102>));
<STOP>
<BOX>
   
  // Create $<COM-002> box
  pElemRef = gslc_ElemCreateBox(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>});
<STOP>
<BOX_P>
   
  // Create $<COM-002> box in flash
  gslc_ElemCreateBox_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<COL-302>,$<COL-303>,$<COL-010>,true,NULL,NULL);
<STOP>
<BOX_ALL_FUNCT_P>
   
  // Create $<COM-002> box in flash
  gslc_ElemCreateBox_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<COL-302>,$<COL-303>,$<COL-010>,true,&CbDrawScanner,&CbTickScanner);
<STOP>
<BOX_DRAW_FUNCT_P>
   
  // Create $<COM-002> box in flash
  gslc_ElemCreateBox_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<COL-302>,$<COL-303>,$<COL-010>,true,&CbDrawScanner,NULL);
<STOP>
<BOX_TICK_FUNCT_P>
   
  // Create $<COM-002> box in flash
  gslc_ElemCreateBox_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<COL-302>,$<COL-303>,$<COL-010>,true,NULL,&CbTickScanner);
<STOP>
<BUTTON_CB>
// Common Button callback
bool CbBtnCommon(void* pvGui,void *pvElemRef,gslc_teTouch eTouch,int16_t nX,int16_t nY)
{
  // Typecast the parameters to match the GUI and element types
  gslc_tsGui*     pGui     = (gslc_tsGui*)(pvGui);
  gslc_tsElemRef* pElemRef = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem    = gslc_GetElemFromRef(pGui,pElemRef);

  if ( eTouch == GSLC_TOUCH_UP_IN ) {
    // From the element's ID we can determine which button was pressed.
    switch (pElem->nId) {
//<Button Enums !Start!>
$<CALLBACK>
//<Button Enums !End!>
      default:
        break;
    }
  }
  return true;
}
<STOP>
<BUTTON_CB_CASE>
      case $<COM-002>:
        break;
<STOP>
<BUTTON_CB_CODE>
      case $<COM-002>:
$<CODE_SEGMENT>
        break;
<STOP>
<BUTTON_CB_CHGPAGE>
      case $<COM-002>:
        gslc_SetPageCur(&m_gui, $<TBNT-101>);
        break;
<STOP>
<BUTTON_CB_HIDEPOPUP>
      case $<COM-002>:
        gslc_PopupHide(&m_gui);
        break;
<STOP>
<BUTTON_CB_INPUT>
      case $<COM-002>:
        // Clicked on edit field, so show popup box and associate with this text field
        gslc_ElemXKeyPadInputAsk(&m_gui, $<KEY-019>, $<KEY-002>, $<COM-019>);
        break;
<STOP>
<BUTTON_CB_SHOWPOPUP>
      case $<COM-002>:
        gslc_PopupShow(&m_gui, $<TBTN-104>, true);
        break;
<STOP>
<BUTTON_CB_TOGGLE>
      case $<COM-002>:
        // TODO Add code for Toggle button ON/OFF state
        if (gslc_ElemXTogglebtnGetState(&m_gui, $<COM-019>)) {
          ;
        }
        break;
<STOP>
<CHECKBOX>
   
  // create checkbox $<COM-002>
  pElemRef = gslc_ElemXCheckboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_asXCheck$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-005>},false,$<RBTN-102>,$<COL-305>,$<CBOX-100>);
<STOP>
<CHECKBOX_P>
   
  // create checkbox $<COM-002> in flash 
  gslc_ElemXCheckboxCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-005>,$<COL-303>,true,
    GSLC_GROUP_ID_NONE,false,$<RBTN-102>,$<COL-305>,$<CBOX-100>);
<STOP>
<CHECKBOX_GROUP>
   
  // create checkbox $<COM-002>
  pElemRef = gslc_ElemXCheckboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_asXCheck$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},true,$<RBTN-102>,$<COL-305>,$<CBOX-100>);
<STOP>
<CHECKBOX_GROUP_P>
   
  // create checkbox $<COM-002> in flash 
  gslc_ElemXCheckboxCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<RBTN-101>,true,$<RBTN-102>,$<COL-305>,$<CBOX-100>);
<STOP>
<CHECKBOXSETSTATE>
  gslc_ElemXCheckboxSetStateFunc(&m_gui, pElemRef, &CbCheckbox);
<STOP>
<CHECKBOXSETSTATE_P>
  gslc_ElemXCheckboxSetStateFunc(&m_gui, $<COM-019>, &CbCheckbox);
<STOP>
<CHECKBOX_CB>
// Checkbox / radio callbacks
// - Creating a callback function is optional, but doing so enables you to
//   detect changes in the state of the elements.
bool CbCheckbox(void* pvGui, void* pvElemRef, int16_t nSelId, bool bState)
{
  gslc_tsGui*     pGui      = (gslc_tsGui*)(pvGui);
  gslc_tsElemRef* pElemRef  = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem     = gslc_GetElemFromRef(pGui,pElemRef);
  if (pElemRef == NULL) {
    return false;
  }
  
  boolean bChecked = gslc_ElemXCheckboxGetState(pGui,pElemRef);

  // Determine which element issued the callback
  switch (pElem->nId) {
//<Checkbox Enums !Start!>
$<CALLBACK>
//<Checkbox Enums !End!>
    default:
      break;
  } // switch
  return true;
}
<STOP>
<CHECKBOX_CB_CASE>
    case $<COM-002>:
      break;
<STOP>
<COLOR>
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
<STOP>
<COLOR_FILL>
  gslc_ElemSetCol(&m_gui,pElemRef,GSLC_COL_WHITE,$<COL-303>,GSLC_COL_BLACK);
<STOP>
<COLOR_IMAGE>
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,GSLC_COL_WHITE,GSLC_COL_WHITE);
<STOP>
<COLOR_P>
  // gslc_ElemSetCol(); currently not supported by the FLASH _P calls.
<STOP>
<CORNERS_ROUNDED>
  gslc_ElemSetRoundEn(&m_gui, pElemRef, true);
<STOP>
<CORNERS_ROUNDED_P>
  // gslc_ElemSetRoundEn(); currently not supported by the FLASH _P calls.
<STOP>
<DRAWFUNC>
  // Set the callback function to handle all drawing for the element
  gslc_ElemSetDrawFunc(&m_gui,pElemRef,&CbDrawScanner);
<STOP>
<DEFINE_ELEM_PAGE>

#define MAX_ELEM_$<STRIP_ENUM> $<COUNT> // # Elems total on page
<STOP>
<DEFINE_ELEM_RAM>
#define MAX_ELEM_$<STRIP_ENUM>_RAM MAX_ELEM_$<STRIP_ENUM> // # Elems in RAM
<STOP>
<DEFINE_ELEM_RAM_P>
#if (GSLC_USE_PROGMEM)
  #define MAX_ELEM_$<STRIP_ENUM>_PROG $<FLASH> // # Elems in Flash
#else
  #define MAX_ELEM_$<STRIP_ENUM>_PROG 0 // No Elems in Flash
#endif
#define MAX_ELEM_$<STRIP_ENUM>_RAM MAX_ELEM_$<STRIP_ENUM> - MAX_ELEM_$<STRIP_ENUM>_PROG 
<STOP>
<DRAWBOX_CB>
 
// Scanner drawing callback function
// - This is called when E_ELEM_SCAN is being rendered
bool CbDrawScanner(void* pvGui,void* pvElemRef,gslc_teRedrawType eRedraw)
{
  int nInd;

  // Typecast the parameters to match the GUI and element types
  gslc_tsGui*     pGui     = (gslc_tsGui*)(pvGui);
  gslc_tsElemRef* pElemRef = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem    = gslc_GetElemFromRef(pGui,pElemRef); 

  // Create shorthand variables for the origin
  int16_t  nX = pElem->rElem.x;
  int16_t  nY = pElem->rElem.y;

  // Draw the background
  gslc_tsRect rInside = pElem->rElem;
  rInside = gslc_ExpandRect(rInside,-1,-1);
  gslc_DrawFillRect(pGui,rInside,pElem->colElemFill);

  // Enable localized clipping
  gslc_SetClipRect(pGui,&rInside);

  //TODO - Add your drawing graphic primitives

  // Disable clipping region
  gslc_SetClipRect(pGui,NULL);

  // Draw the frame
  gslc_DrawFrameRect(pGui,pElem->rElem,pElem->colElemFrame);

  // Clear the redraw flag
  gslc_ElemSetRedraw(&m_gui,pElemRef,GSLC_REDRAW_NONE);

  return true;
}
<STOP>
<ELEM_COMMENT>
// Include extended elements
<STOP>
<ELEM_INCLUDE>
#include "elem/$<WIDGET>.h"
<STOP>
<ELEMENT_REF>
  $<COM-019> = pElemRef;
<STOP>
<ELEMENT_REF_FIND_P>
  $<COM-019> = gslc_PageFindElemById(&m_gui,$<COM-000>,$<COM-002>);
<STOP>
<EXTERN_UTFT_FONT>
extern uint8_t $<UTFT_FONT>[];
<STOP>
<ELEMENT_REF_EXTERN>
extern gslc_tsElemRef* $<ELEMREF>;
<STOP>
<ELEMENT_REF_SAVE>
gslc_tsElemRef* $<18>$<ELEMREF>= NULL;
<STOP>
<FILE_HDR>
// FILE: [$<FILENAME>]
// Created by GUIslice Builder version: [$<VERSION>]
//
// GUIslice Builder Generated GUI Framework File
//
// For the latest guides, updates and support view:
// https://github.com/ImpulseAdventure/GUIslice
//
<STOP>
<FILL_EN>
  gslc_ElemSetFillEn(&m_gui,pElemRef,$<COM-011>);
<STOP>
<FILL_EN_P>
  // gslc_ElemSetFillEn(); currently not supported by the FLASH _P calls.
<STOP>
<FONT_DEFINE>
#define $<FONT_REF> "$<DEFINE_FILE>"
<STOP>
<FONT_EXTERN>
extern uint8_t $<EXTERN_STORAGE>[];
<STOP>
<FONT_INCLUDE>
#include "$<INCLUDE_FILE>"
<STOP>
<FONT_LOAD>
    if (!gslc_FontSet(&m_gui,$<FONT_ID>,$<FONT_REFTYPE>,$<FONT_REF>,$<FONT_SZ>)) { return; }
<STOP>
<FONT_MODE>
    gslc_FontSetMode(&m_gui, $<FONT_ID>, $<FONT_MODE>);
<STOP>
<FRAME_EN>
  gslc_ElemSetFrameEn(&m_gui,pElemRef,$<COM-010>);
<STOP>
<FRAME_EN_P>
  // gslc_ElemSetFrameEn(); currently not supported by the FLASH _P calls.
<STOP>
<GLOW_EN>
  gslc_ElemSetGlowEn(&m_gui,pElemRef,true);
<STOP>
<GUI_ELEMENT>
gslc_tsElem                     m_as$<STRIP_KEY>Elem[MAX_ELEM_$<STRIP_ENUM>_RAM];
gslc_tsElemRef                  m_as$<STRIP_KEY>ElemRef[MAX_ELEM_$<STRIP_ENUM>];
<STOP>
<GRAPH>

  // Create graph $<COM-002>
  pElemRef = gslc_ElemXGraphCreate(&m_gui,$<COM-002>,$<COM-000>,
    &m_sGraph$<COM-018>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<TXT-211>,(int16_t*)&m_anGraphBuf$<COM-018>,
        $<GRPH-100>,$<COL-309>);
  gslc_ElemXGraphSetStyle(&m_gui,pElemRef, $<GRPH-102>, 5);
<STOP>
<GROUP>
  gslc_ElemSetGroup(&m_gui,pElemRef,$<RBTN-101>);
<STOP>
<HDR_INCLUDE>
#include "$<FILENAME>"

<STOP>
<IMAGE_DEFINE>
 
  // Create $<COM-002> using Image 
  pElemRef = gslc_ElemCreateImg(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    $<IMG-109>$<IMG-101>,$<IMG-102>));
<STOP>
<IMAGE_EXTERN>
 
  // Create $<COM-002> using Image 
  pElemRef = gslc_ElemCreateImg(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    $<IMG-109>$<IMG-108>,$<IMG-102>));
<STOP>
<IMGBUTTON_DEFINE>
  
  // Create $<COM-002> button with image label
  pElemRef = gslc_ElemCreateBtnImg(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
          $<IBTN-110>$<IBTN-102>,$<IBTN-104>),
          $<IBTN-113>$<IBTN-103>,$<IBTN-104>),
          &CbBtnCommon);
<STOP>
<IMGBUTTON_EXTERN>
  
  // Create $<COM-002> button with image label
  pElemRef = gslc_ElemCreateBtnImg(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
          $<IBTN-110>$<IBTN-108>,$<IBTN-104>),
          $<IBTN-113>$<IBTN-109>,$<IBTN-104>),
          &CbBtnCommon);
<STOP>
<IMGTOGGLE_DEFINE>
  
  // Create $<COM-002> button with image label
  pElemRef = gslc_ElemXToggleImgbtnCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sToggleImg$<COM-018>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
          $<IBTN-110>$<IBTN-102>,$<IBTN-104>),
          $<IBTN-113>$<IBTN-103>,$<IBTN-104>),
          false,&CbBtnCommon);
<STOP>
<IMGTOGGLE_EXTERN>
  
  // Create $<COM-002> button with image label
  pElemRef = gslc_ElemXToggleImgbtnCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sToggleImg$<COM-018>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
          $<IBTN-110>$<IBTN-108>,$<IBTN-104>),
          $<IBTN-113>$<IBTN-109>,$<IBTN-104>),
          false,&CbBtnCommon);
<STOP>
<IMAGETRANSPARENT>
  gslc_ElemSetFillEn(&m_gui,pElemRef,false);
<STOP>
<IMGBTNTRANSPARENT>
  gslc_ElemSetFillEn(&m_gui,pElemRef,false);
<STOP>
<INPUTFRAME_EN>
  gslc_ElemSetFrameEn(&m_gui,pElemRef,true);
<STOP>
<KEYPAD_CB>
// KeyPad Input Ready callback
bool CbKeypad(void* pvGui, void *pvElemRef, int16_t nState, void* pvData)
{
  gslc_tsGui*     pGui     = (gslc_tsGui*)pvGui;
  gslc_tsElemRef* pElemRef = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem    = gslc_GetElemFromRef(pGui,pElemRef);

  // From the pvData we can get the ID element that is ready.
  int16_t nTargetElemId = gslc_ElemXKeyPadDataTargetIdGet(pGui, pvData);
  if (nState == XKEYPAD_CB_STATE_DONE) {
    // User clicked on Enter to leave popup
    // - If we have a popup active, pass the return value directly to
    //   the corresponding value field
    switch (nTargetElemId) {
//<Keypad Enums !Start!>
$<CALLBACK>
//<Keypad Enums !End!>
      default:
        break;
    }
  } else if (nState == XKEYPAD_CB_STATE_CANCEL) {
    // User escaped from popup, so don't update values
    gslc_PopupHide(&m_gui);
  }
  return true;
}
<STOP>
<KEYPAD_CB_CASE>
      case $<COM-002>:
        gslc_ElemXKeyPadInputGet(pGui, $<COM-019>, pvData);
        gslc_PopupHide(&m_gui);
        break;
<STOP>
<KEYPAD_CONFIG_NUM>
  static gslc_tsXKeyPadCfg_Num sCfg;
  sCfg = gslc_ElemXKeyPadCfgInit_Num();
  gslc_ElemXKeyPadCfgSetFloatEn_Num(&sCfg, $<FLOAT_EN>);
  gslc_ElemXKeyPadCfgSetSignEn_Num(&sCfg, $<SIGN_EN>);
<STOP>
<KEYPAD_CONFIG_TXT>
  static gslc_tsXKeyPadCfg_Alpha sCfgTx;
  sCfgTx = gslc_ElemXKeyPadCfgInit_Alpha();
<STOP>
<KEYPAD_CREATE_NUM>
  $<ELEMREF> = gslc_ElemXKeyPadCreate_Num(&m_gui, $<WIDGET_ENUM>, $<PAGE_ENUM>,
    &$<STORAGE>, $<X>, $<Y>, $<FONT_ID>, &sCfg);
  gslc_ElemXKeyPadValSetCb(&m_gui, $<ELEMREF>, &CbKeypad);
<STOP>
<KEYPAD_CREATE_TXT>
  $<ELEMREF> = gslc_ElemXKeyPadCreate_Alpha(&m_gui, $<WIDGET_ENUM>, $<PAGE_ENUM>,
    &$<STORAGE>, $<X>, $<Y>, $<FONT_ID>, &sCfgTx);
  gslc_ElemXKeyPadValSetCb(&m_gui, $<ELEMREF>, &CbKeypad);
<STOP>
<KEYPAD_BUTTONGAP>
  gslc_ElemXKeyPadCfgSetButtonSpace((gslc_tsXKeyPadCfg*)$<CONFIG>, $<GAPX>, $<GAPY>);
<STOP>
<KEYPAD_BUTTONSZ>
  gslc_ElemXKeyPadCfgSetButtonSz((gslc_tsXKeyPadCfg*)$<CONFIG>, $<BUTTONSZ_W>, $<BUTTONSZ_H>);
<STOP>
<KEYPAD_ROUNDBUTTONS>
  gslc_ElemXKeyPadCfgSetRoundEn((gslc_tsXKeyPadCfg*)$<CONFIG>, $<ROUND_EN>);
<STOP>
<KEYPAD_ELEM_NUM>
gslc_tsXKeyPad                  $<STORAGE>;
<STOP>
<KEYPAD_ELEM_TEXT>
gslc_tsXKeyPad                  $<STORAGE>;
<STOP>
<KEYPAD_PAGE>
gslc_tsElem                     $<STORAGE>[1];
gslc_tsElemRef                  $<STORAGE>Ref[1];
<STOP>
<LINE>

  // Create $<COM-002> line 
  pElemRef = gslc_ElemCreateLine(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<X1>,$<Y1>);
  gslc_ElemSetCol(&m_gui,pElemRef,GSLC_COL_BLACK,$<COL-303>,$<COL-303>);
<STOP>
<LISTBOX>
   
  // Create listbox
  pElemRef = gslc_ElemXListboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sListbox$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<TXT-211>,
    (uint8_t*)&m_acListboxBuf$<COM-018>,sizeof(m_acListboxBuf$<COM-018>),$<LIST-102>);
  gslc_ElemXListboxSetSize(&m_gui, pElemRef, $<TXT-209>, $<TXT-210>); // $<TXT-209> rows, $<TXT-210> columns
  gslc_ElemXListboxItemsSetSize(&m_gui, pElemRef, $<LIST-110>, $<LIST-111>);
  gslc_ElemSetTxtMarginXY(&m_gui, pElemRef, $<LIST-100>, $<LIST-101>);
  gslc_ElemSetTxtCol(&m_gui,pElemRef,$<COL-301>);
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
  gslc_ElemXListboxSetSelFunc(&m_gui, pElemRef, &CbListbox);
<STOP>
<LISTBOX_ITEM>
  gslc_ElemXListboxAddItem(&m_gui, pElemRef, "$<TEXT>");
<STOP>
<LISTBOX_GAP>
  gslc_ElemXListboxItemsSetGap(&m_gui, pElemRef, $<LIST-106>,$<LIST-107>);
<STOP>
<LISTBOX_CB>
bool CbListbox(void* pvGui, void* pvElemRef, int16_t nSelId)
{
  gslc_tsGui*     pGui     = (gslc_tsGui*)(pvGui);
  gslc_tsElemRef* pElemRef = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem    = gslc_GetElemFromRef(pGui, pElemRef);
  char            acTxt[MAX_STR + 1];
  
  if (pElemRef == NULL) {
    return false;
  }

  // From the element's ID we can determine which listbox was active.
  switch (pElem->nId) {
//<Listbox Enums !Start!>
$<CALLBACK>
//<Listbox Enums !End!>
    default:
      break;
  }
  return true;
}
<STOP>
<LISTBOX_CB_CASE>
    case $<COM-002>:
      if (nSelId != XLISTBOX_SEL_NONE) {
        gslc_ElemXListboxGetItem(&m_gui, pElemRef, nSelId, acTxt, MAX_STR);
      }
      break;
<STOP>
<LISTBOXSLIDER_1>
   
  // Create wrapping box for listbox $<COM-002> and scrollbar
  pElemRef = gslc_ElemCreateBox(&m_gui,GSLC_ID_AUTO,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>});
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
  
  // Create listbox
  pElemRef = gslc_ElemXListboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sListbox$<COM-018>,
    (gslc_tsRect){$<COM-003>+2,$<COM-004>+4,$<COM-005>-4-$<SLD-111>,$<COM-006>-7},$<TXT-211>,
    (uint8_t*)&m_acListboxBuf$<COM-018>,sizeof(m_acListboxBuf$<COM-018>),$<LIST-102>);
  gslc_ElemXListboxSetSize(&m_gui, pElemRef, $<TXT-209>, $<TXT-210>); // $<TXT-209> rows, $<TXT-210> columns
  gslc_ElemXListboxItemsSetSize(&m_gui, pElemRef, $<LIST-110>, $<LIST-111>);
  gslc_ElemSetTxtMarginXY(&m_gui, pElemRef, $<LIST-100>, $<LIST-101>);
  gslc_ElemSetTxtCol(&m_gui,pElemRef,$<COL-301>);
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
  gslc_ElemXListboxSetSelFunc(&m_gui, pElemRef, &CbListbox);
<STOP>
<LISTBOXSLIDER_2>

  // Create vertical scrollbar for listbox
  pElemRef = gslc_ElemXSliderCreate(&m_gui,$<BAR-113>,$<COM-000>,&m_sListScroll$<COM-018>,
          (gslc_tsRect){$<COM-003>+$<COM-005>-2-$<SLD-111>,$<COM-004>+4,$<SLD-111>,$<COM-006>-8},0,$<BAR-115>,0,$<SLD-103>,true);
  gslc_ElemSetCol(&m_gui,pElemRef,$<BAR-116>,$<BAR-117>,$<BAR-116>);
  gslc_ElemXSliderSetPosFunc(&m_gui,pElemRef,&CbSlidePos);
  $<BAR-114> = pElemRef;
<STOP>
<MAX_PAGE>
#define $<23>$<NAME> $<COUNT>
<STOP>
<PAGEADD>
  gslc_PageAdd(&m_gui,$<PAGE_ENUM>,m_as$<STRIP_KEY>Elem,MAX_ELEM_$<STRIP_ENUM>_RAM,m_as$<STRIP_KEY>ElemRef,MAX_ELEM_$<STRIP_ENUM>);
<STOP>
<PAGEADDKEYPAD>
  gslc_PageAdd(&m_gui,$<PAGE_ENUM>,$<STORAGE>,1,$<STORAGE>Ref,1);  // KeyPad
<STOP>
<PAGEBASE>

  // Now mark $<PAGE_ENUM> as a "base" page which means that it's elements
  // are always visible. This is useful for common page elements.
  gslc_SetPageBase(&m_gui, $<PAGE_ENUM>);

<STOP>
<PAGECOMMENT>

  // -----------------------------------
  // PAGE: $<PAGE_ENUM>
  
<STOP>
<PAGECUR>

  // NOTE: The current page defaults to the first page added. Here we explicitly
  //       ensure that the main page is the correct page no matter the add order.
  gslc_SetPageCur(&m_gui,$<PAGE_ENUM>);
<STOP>
<PROGMEM>
// Define the maximum number of elements per page
// - To enable the same code to run on devices that support storing
//   data into Flash (PROGMEM) and those that don't, we can make the
//   number of elements in Flash dependent upon GSLC_USE_PROGMEM
// - This should allow both Arduino and ARM Cortex to use the same code.
<STOP>
<PROGRESSBAR>

  // Create progress bar $<COM-002> 
  pElemRef = gslc_ElemXProgressCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXBarGauge$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<BAR-102>,$<BAR-103>,$<BAR-104>,$<COL-308>,$<BAR-100>);
<STOP>
<PROGRESSBAR_P>

  // Create $<COM-002> progress bar in flash
  gslc_ElemXProgressCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<BAR-102>,$<BAR-103>,$<BAR-104>,
    $<COL-302>,$<COL-303>,$<COL-308>,$<BAR-100>);
<STOP>
<RAMPGAUGE>

  // Create progress bar $<COM-002> 
  pElemRef = gslc_ElemXRampCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXRampGauge$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<BAR-102>,$<BAR-103>,
    $<BAR-104>,GSLC_COL_YELLOW,false);
<STOP>
<RAMPSTYLE>
  gslc_ElemXGaugeSetStyle(&m_gui,pElemRef, GSLCX_GAUGE_STYLE_RAMP);
<STOP>
<RADIALGAUGE>

  // Create progress bar $<COM-002> 
  pElemRef = gslc_ElemXRadialCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXRadialGauge$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<BAR-102>,$<BAR-103>,$<BAR-104>,$<COL-308>);
<STOP>
<RADIALIND>
  gslc_ElemXRadialSetIndicator(&m_gui,pElemRef,$<COL-308>,$<BAR-109>,$<BAR-110>,$<BAR-111>);
<STOP>
<RADIALTICKS>
  gslc_ElemXRadialSetTicks(&m_gui,pElemRef,$<BAR-108>,$<BAR-106>,$<BAR-107>);
<STOP>
<RADIALFLIP>
  gslc_ElemXRadialSetFlip(&m_gui,pElemRef,true);
<STOP>
<RADIOBUTTON>
  
  // Create radio button $<COM-002>
  pElemRef = gslc_ElemXCheckboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_asXRadio$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-005>},true,$<RBTN-102>,$<COL-305>,$<CBOX-100>);
<STOP>
<RADIOBUTTON_P>
   
  // create radiobutton $<COM-002> in flash 
  gslc_ElemXCheckboxCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-005>,$<COL-303>,true,
    $<RBTN-101>,true,$<RBTN-102>,$<COL-305>,$<CBOX-100>);
<STOP>
<RESOURCE_DEFINE>
#define $<25>$<DEFINE> "$<IMAGE_NAME>"
<STOP>
<RESOURCE_EXTERN>
extern "C" unsigned char $<EXTERN_NAME>[];
<STOP>
<RESOURCE_PROGMEM>
extern "C" const unsigned short $<EXTERN_NAME>[] PROGMEM;
<STOP>
<RESOURCE_PROGMEM_RAW>
extern "C" const unsigned char $<EXTERN_NAME>[] PROGMEM;
<STOP>
<RINGGAUGE>

  // Create ring gauge $<COM-002> 
  static char m_sRingText$<COM-018>[$<TXT-205>] = "";
  pElemRef = gslc_ElemXRingGaugeCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXRingGauge$<COM-018>,
          (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
          (char*)m_sRingText$<COM-018>,$<TXT-205>,$<TXT-211>);
  gslc_ElemXRingGaugeSetValRange(&m_gui, pElemRef, $<RING-103>, $<RING-104>);
  gslc_ElemXRingGaugeSetVal(&m_gui, pElemRef, $<RING-105>); // Set initial value
<STOP>
<RINGGAUGE_FLATCOL>
  gslc_ElemXRingGaugeSetColorActiveFlat(&m_gui,pElemRef, $<RING-109>);
<STOP>
<RINGGAUGE_GRADIENTCOL>
  gslc_ElemXRingGaugeSetColorActiveGradient(&m_gui, pElemRef, $<RING-110>, $<RING-111>);
<STOP>
<RINGGAUGE_INACTIVECOL>
  gslc_ElemXRingGaugeSetColorInactive(&m_gui,pElemRef, $<RING-112>);
<STOP>
<RINGGAUGE_LINE>
  gslc_ElemXRingGaugeSetThickness(&m_gui,pElemRef, $<RING-107>);
<STOP>
<RINGGAUGE_RANGE>
  gslc_ElemXRingGaugeSetAngleRange(&m_gui,pElemRef, $<RING-100>, $<RING-101>, $<RING-102>);
<STOP>
<RINGGAUGE_SEGMENTS>
  gslc_ElemXRingGaugeSetQuality(&m_gui,pElemRef, $<RING-106>);
<STOP>
<ROTATE>
  gslc_GuiRotate(&m_gui, $<ROTATION>);
<STOP>
<SEEKBAR>

  // Create seekbar $<COM-002> 
  pElemRef = gslc_ElemXSeekbarCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXSeekbar$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<SLD-100>,$<SLD-101>,$<SLD-102>,
    $<SLD-108>,$<SLD-109>,$<SLD-103>,$<COL-315>,$<COL-316>,$<COL-317>,$<SLD-104>);
  gslc_ElemXSeekbarSetPosFunc(&m_gui,pElemRef,&CbSlidePos);
<STOP>
<SEEKBAR_P>

  // Create seekbar $<COM-002> 
  gslc_ElemXSeekbarCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<SLD-100>,$<SLD-101>,$<SLD-102>,$<SLD-108>,$<SLD-109>,$<SLD-103>,
    $<COL-315>,$<COL-316>,$<COL-317>,$<SLD-104>,$<COL-302>,$<COL-303>);
  pElemRef = gslc_PageFindElemById(&m_gui,$<COM-000>,$<COM-002>);
  gslc_ElemXSeekbarSetPosFunc(&m_gui,pElemRef,&CbSlidePos);
  $<COM-019> = pElemRef;
<STOP>
<SEEKBAR_STYLE>
  gslc_ElemXSeekbarSetStyle(&m_gui,pElemRef,$<SLD-107>,$<COL_307>,$<SLD-110>,$<COL-318>,
    $<SLD-105>,$<SLD-106>,$<COL-306>);
<STOP>
<SLIDER>

  // Create slider $<COM-002> 
  pElemRef = gslc_ElemXSliderCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXSlider$<COM-018>,
          (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<SLD-100>,$<SLD-101>,$<SLD-102>,$<SLD-103>,$<SLD-104>);
  gslc_ElemXSliderSetStyle(&m_gui,pElemRef,$<SLD-107>,$<COL_307>,$<SLD-105>,$<SLD-106>,$<COL-306>);
  gslc_ElemXSliderSetPosFunc(&m_gui,pElemRef,&CbSlidePos);
<STOP>
<SLIDER_P>

  // Create slider $<COM-002> 
  gslc_ElemXSliderCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
          $<SLD-100>,$<SLD-101>,$<SLD-102>,$<SLD-103>,$<SLD-104>,$<COL-302>,$<COL-303>);
  pElemRef = gslc_PageFindElemById(&m_gui,$<COM-000>,$<COM-002>);
  gslc_ElemXSliderSetStyle(&m_gui,pElemRef,$<SLD-107>,$<COL_307>,$<SLD-105>,$<SLD-106>,$<COL-306>);
  gslc_ElemXSliderSetPosFunc(&m_gui,pElemRef,&CbSlidePos);
  $<COM-019> = pElemRef;
<STOP>
<SLIDER_CB>

// Callback function for when a slider's position has been updated
bool CbSlidePos(void* pvGui,void* pvElemRef,int16_t nPos)
{
  gslc_tsGui*     pGui     = (gslc_tsGui*)(pvGui);
  gslc_tsElemRef* pElemRef = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem    = gslc_GetElemFromRef(pGui,pElemRef);
  int16_t         nVal;

  // From the element's ID we can determine which slider was updated.
  switch (pElem->nId) {
//<Slider Enums !Start!>
$<CALLBACK>
//<Slider Enums !End!>
    default:
      break;
  }

  return true;
}
<STOP>
<SEEKBAR_CB_CASE>
    case $<COM-002>:
      // Fetch the slider position
      nVal = gslc_ElemXSeekbarGetPos(pGui,$<COM-019>);
      break;
<STOP>
<SLIDER_CB_CASE>
    case $<COM-002>:
      // Fetch the slider position
      nVal = gslc_ElemXSliderGetPos(pGui,$<COM-019>);
      break;
<STOP>
<SPINNER>

  // Add Spinner element
  pElemRef = gslc_ElemXSpinnerCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXSpinner$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<SLD-100>,$<SLD-101>,$<SLD-102>,$<SPIN-100>,$<TXT-211>,$<COM-013>,&CbSpinner);
<STOP>
<SPINNER_ARROWS>
  gslc_ElemXSpinnerSetChars(&m_gui,pElemRef,(uint8_t)$<ARROW_UP>,(uint8_t)$<ARROW_DOWN>);
<STOP>
<SPINNER_CB>
// Spinner Input Ready callback
bool CbSpinner(void* pvGui, void *pvElemRef, int16_t nState, void* pvData)
{
  gslc_tsGui*     pGui = (gslc_tsGui*)pvGui;
  gslc_tsElemRef* pElemRef = (gslc_tsElemRef*)(pvElemRef);
  gslc_tsElem*    pElem = gslc_GetElemFromRef(pGui,pElemRef);

  // NOTE: pvData is NULL
  if (nState == XSPINNER_CB_STATE_UPDATE) {
    // From the element's ID we can determine which input field is ready.
    switch (pElem->nId) {
//<Spinner Enums !Start!>
$<CALLBACK>
//<Spinner Enums !End!>
      default:
        break;
    }
  }
}
<STOP>
<SPINNER_CB_CASE>
      case $<COM-002>:
        //TODO- Add Spinner handling code
        // using gslc_ElemXSpinnerGetCounter(&m_gui, &$<XDATA>);
        break;
<STOP>
<TEXT>
  
  // Create $<COM-002> text label
  pElemRef = gslc_ElemCreateTxt(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    (char*)$<TEXT>,0,$<TXT-211>);
<STOP>
<TEXT_P>
  
  // Create $<COM-002> text label using flash API
  gslc_ElemCreateTxt_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<TEXT>,&m_asFont[$<TXT-211>],
    $<COL-301>,$<COL-302>,$<COL-303>,$<TXT-213>,false,true);
<STOP>
<TEXTALIGN>
  gslc_ElemSetTxtAlign(&m_gui,pElemRef,$<TXT-213>);
<STOP>
<TEXTALIGN_P>
  // gslc_ElemSetTxtAlign(); currently not supported by the FLASH _P calls.
<STOP>
<TEXTBOX>
   
  // Create textbox
  pElemRef = gslc_ElemXTextboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sTextbox$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<TXT-211>,
    (char*)&m_acTextboxBuf$<COM-018>,$<TXT-209>,$<TXT-210>);
  gslc_ElemXTextboxWrapSet(&m_gui,pElemRef,$<TXT-208>);
  gslc_ElemSetTxtCol(&m_gui,pElemRef,$<COL-301>);
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
  $<COM-019> = pElemRef;
<STOP>
<TEXTBOXSLIDER_1>
   
  // Create wrapping box for textbox $<COM-002> and scrollbar
  pElemRef = gslc_ElemCreateBox(&m_gui,GSLC_ID_AUTO,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>});
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
  
  // Create textbox
  pElemRef = gslc_ElemXTextboxCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sTextbox$<COM-018>,
    (gslc_tsRect){$<COM-003>+2,$<COM-004>+4,$<COM-005>-4-$<SLD-111>,$<COM-006>-7},$<TXT-211>,
    (char*)&m_acTextboxBuf$<COM-018>,$<TXT-209>,$<TXT-210>);
  gslc_ElemXTextboxWrapSet(&m_gui,pElemRef,$<TXT-208>);
  gslc_ElemSetTxtCol(&m_gui,pElemRef,$<COL-301>);
  gslc_ElemSetCol(&m_gui,pElemRef,$<COL-302>,$<COL-303>,$<COL-304>);
  $<COM-019> = pElemRef;
<STOP>
<TEXTBOXSLIDER_2>

  // Create vertical scrollbar for textbox
  pElemRef = gslc_ElemXSliderCreate(&m_gui,$<BAR-113>,$<COM-000>,&m_sTextScroll$<COM-018>,
          (gslc_tsRect){$<COM-003>+$<COM-005>-2-$<SLD-111>,$<COM-004>+4,$<SLD-111>,$<COM-006>-8},0,$<BAR-115>,0,$<SLD-103>,true);
  gslc_ElemSetCol(&m_gui,pElemRef,$<BAR-116>,$<BAR-117>,$<BAR-116>);
  gslc_ElemXSliderSetPosFunc(&m_gui,pElemRef,&CbSlidePos);
  $<BAR-114> = pElemRef;
<STOP>
<TEXT_COLOR>
  gslc_ElemSetTxtCol(&m_gui,pElemRef,$<COL-301>);
<STOP>
<TEXT_INPUT>
  
  // Create $<COM-002> text input field
  static char m_sInputText$<COM-018>[$<TXT-205>] = $<TEXT>;
  pElemRef = gslc_ElemCreateTxt(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    (char*)m_sInputText$<COM-018>,$<TXT-205>,$<TXT-211>);
<STOP>
<TEXT_INPUT_P>
  
  // Create $<COM-002> text input field
  static char m_sInputText$<COM-018>[$<TXT-205>] = $<TEXT>;
  gslc_ElemCreateTxt_P_R_ext(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    m_sInputText$<COM-018>,$<TXT-205>,&m_asFont[$<TXT-211>],
    $<COL-301>,$<COL-301>,$<COL-302>,$<COL-303>,$<TXT-213>,$<TXT-212>,$<TXT-212>,
    true,true,true,false,NULL,NULL,&CbBtnCommon,NULL);
<STOP>
<TEXT_INPUT_NUM>
  
  // Create $<COM-002> numeric input field
  static char m_sInputNumber$<COM-018>[$<TXT-205>] = $<TEXT>;
  pElemRef = gslc_ElemCreateTxt(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    (char*)m_sInputNumber$<COM-018>,$<TXT-205>,$<TXT-211>);
<STOP>
<TEXT_INPUT_NUM_P>
  
  // Create $<COM-002> numeric input field
  static char m_sInputNumber$<COM-018>[$<TXT-205>] = $<TEXT>;
  gslc_ElemCreateTxt_P_R_ext(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    m_sInputNumber$<COM-018>,$<TXT-205>,&m_asFont[$<TXT-211>],
    $<COL-301>,$<COL-301>,$<COL-302>,$<COL-303>,$<TXT-213>,$<TXT-212>,$<TXT-212>,
    true,true,true,false,NULL,NULL,&CbBtnCommon,NULL);
<STOP>
<TEXT_MARGIN>
  gslc_ElemSetTxtMargin(&m_gui,pElemRef,$<TXT-212>);
<STOP>
<TEXT_UPDATE>
  
  // Create $<COM-002> runtime modifiable text
  static char m_sDisplayText$<COM-018>[$<TXT-205>] = $<TEXT>;
  pElemRef = gslc_ElemCreateTxt(&m_gui,$<COM-002>,$<COM-000>,(gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    (char*)m_sDisplayText$<COM-018>,$<TXT-205>,$<TXT-211>);
<STOP>
<TEXT_UPDATE_P>
  
  // Create $<COM-002> modifiable text using flash API
  static char m_sDisplayText$<COM-018>[$<TXT-205>] = $<TEXT>;
  gslc_ElemCreateTxt_P_R_ext(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    m_sDisplayText$<COM-018>,$<TXT-205>,&m_asFont[$<TXT-211>],
    $<COL-301>,$<COL-301>,$<COL-302>,$<COL-303>,$<TXT-213>,$<TXT-212>,$<TXT-212>,
    $<COM-010>,$<COM-011>,false,false,NULL,NULL,NULL,NULL);
<STOP>
<TEXT_UTF8>
  gslc_ElemSetTxtEnc(&m_gui,pElemRef,GSLC_TXT_ENC_UTF8);
<STOP>
<TEXT_UTF8_P>
  gslc_ElemSetTxtEnc(&m_gui,pElemRef,GSLC_TXT_ENC_UTF8);
<STOP>
<TICK_CB>

bool CbTickScanner(void* pvGui,void* pvScope)
{
  gslc_tsGui*     pGui      = (gslc_tsGui*)(pvGui);
  gslc_tsElemRef* pElemRef  = (gslc_tsElemRef*)(pvScope);
  gslc_tsElem*    pElem     = gslc_GetElemFromRef(pGui,pElemRef);

  //TODO add your custom code here 

  return true;
}
<STOP>
<TICKFUNC>
  // Set the callback function to update content automatically
  gslc_ElemSetTickFunc(&m_gui,pElemRef,&CbTickScanner);
<STOP>
<TOUCH_EN>
  gslc_ElemSetClickEn(&m_gui, pElemRef, true);
  gslc_ElemSetTouchFunc(&m_gui, pElemRef, &CbBtnCommon);
<STOP>
<TOUCH_EN_P>
  // gslc_ElemSetTouchFunc(); currently not supported by the FLASH _P calls.
<STOP>
<TRANSPARENCY_COLOR>
  gslc_SetTransparentColor(&m_gui, $<COLOR>);
<STOP>
<TXTBUTTON>
  
  // create $<COM-002> button with text label
  pElemRef = gslc_ElemCreateBtnTxt(&m_gui,$<COM-002>,$<COM-000>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},(char*)$<TEXT>,0,$<TXT-211>,&CbBtnCommon);
<STOP>
<TXTBUTTON_P>
  
  // create $<COM-002> button with text label
  gslc_ElemCreateBtnTxt_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<TEXT>,&m_asFont[$<TXT-211>],
    $<COL-301>,$<COL-302>,$<COL-303>,$<COL-302>,
    $<COL-304>,$<TXT-213>,true,true,&CbBtnCommon,NULL);
<STOP>
<TXTBUTTON_UPDATE>
  
  // Create $<COM-002> button with modifiable text label
  static char m_strbtn$<COM-018>[$<TXT-205>] = $<TEXT>;
  pElemRef = gslc_ElemCreateBtnTxt(&m_gui,$<COM-002>,$<COM-000>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
    (char*)m_strbtn$<COM-018>,$<TXT-205>,$<TXT-211>,&CbBtnCommon);
<STOP>
<TXTBUTTON_UPDATE_P>
  
  // Create $<COM-002> button with modifiable text label
  static char m_strbtn$<COM-018>[$<TXT-205>] = "$<TXT-202>";
  gslc_ElemCreateBtnTxt_P_R(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    m_strbtn$<COM-018>,$<TXT-205>,&m_asFont[$<TXT-211>],
    $<COL-301>,$<COL-302>,$<COL-303>,$<COL-302>,$<COL-304>,
    $<TXT-213>,$<COM-010>,$<COM-011>,&CbBtnCommon,NULL);
<STOP>
<TOGGLEBUTTON>
  
  // Create toggle button $<COM-002>
  pElemRef = gslc_ElemXTogglebtnCreate(&m_gui,$<COM-002>,$<COM-000>,&m_asXToggle$<COM-018>,
    (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},$<COL-317>,$<COL-319>,$<COL-320>,
    $<RBTN-102>,$<CBOX-100>,&CbBtnCommon);
  $<COM-019> = pElemRef;
<STOP>
<TOGGLEBUTTON_P>
   
  // create toggle button $<COM-002> in flash 
  gslc_ElemXTogglebtnCreate_P(&m_gui,$<COM-002>,$<COM-000>,$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>,
    $<COL-317>,$<COL-319>,$<COL-320>,
    $<RBTN-102>,$<CBOX-100>,&CbBtnCommon);
  pElemRef = gslc_PageFindElemById(&m_gui,$<COM-000>,$<COM-002>);
  $<COM-019> = pElemRef;
<STOP>
<WARNING_CONFIG>

// Ensure optional features are enabled in the configuration
<STOP>
<WARNING_COMPOUND>
#if !(GSLC_FEATURE_COMPOUND)
  #error "Config: GSLC_FEATURE_COMPOUND required for this program but not enabled. Please see: https://github.com/ImpulseAdventure/GUIslice/wiki/Configuring-GUIslice"
#endif
<STOP>
<WARNING_SD>
#if !(GSLC_SD_EN)
  #error "Config: GSLC_SD_EN required for this program but not enabled. Please see: https://github.com/ImpulseAdventure/GUIslice/wiki/Configuring-GUIslice"
#endif
<STOP>
<END>
