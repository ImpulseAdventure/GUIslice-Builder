<center>
    <H1>
        How to add a new widget to the builder
    </H1>
    <H2>
        Developer Guides
    </H2>
    <H3>
        Ver: 0.13.0
    </H3>
</center>




























**Publication date and software version**

Published August, 2019. Based on GUIsliceslice Builder 0.13.0

**Copyright**

This document is Copyright © 2019 by Paul Conti. You may distribute or modify it under the terms of the MIT License.  https://opensource.org/licenses/MIT

All trademarks within this guide belong to their legitimate owners.





---------
<div style="page-break-after: always;"></div>

# Introduction

This document is meant to give developers an overview on adding new widgets or UI elements to the Builder.

As example, I have documented the steps taken to add XRingGauge extended element.

Fifteen steps in all.

# Step 1 builder.common.EnumFactory

Edit EnumFactory and increase the Constant numberOfTypes by 1 in this case it becomes:
```
  /** The Constant numberOfTypes. */
  static final public int numberOfTypes  = 25;
```
Add a new Enum String reference to EnumFactory at the top of the class:
```
  /** The Constant RINGGAUGE. */
  static final public String RINGGAUGE =   "RingGauge";
```
Add this new string to typeStrings array at the end, it was:
```
  /** The type strings. */
  static public String[] typeStrings = 
    { PAGE, 
      ...
      ALPHAKEYPAD
    };
```
and now becomes
```  
    /** The type strings. */
  static public String[] typeStrings = 
    { PAGE, 
      ...
      ALPHAKEYPAD, 
      RINGGAUGE
    };
```

---------
<div style="page-break-after: always;"></div>

Add the user visible default enum to the enumStrings array at the same index spot as the typeStrings array (last position). 
```
  /** The enum strings. */
  static public String[] enumStrings = 
    { 
      "E_PAGE",
      ...
      "E_POP_KEYPADTXT",
      "E_ELEM_RINGGAUGE",
    };
```

---------
<div style="page-break-after: always;"></div>

# Step 2- Create a model
## 2.1 Create RingGaugeModel.java
Create the model file for the Widget inside builder.models package.

## 2.2 Add properties to the model
See examples of similar widgets as a guide. In this case we add to RingGaugeModel with its properties. We start with defining indexes into our common data[][] array for each property. 
Example:
```   
  /** The Property Index Constants. */
  static private final int PROP_FONT                  = 7;
  static private final int PROP_TEXT_SZ               = 8;
  static private final int PROP_STARTING_ANGLE        = 9;
        ...
  static private final int PROP_GRADIENT_END_COLOR    = 20;
  static private final int PROP_INACTIVE_COLOR        = 21;
  static private final int PROP_TEXT_COLOR            = 22;
```
Note we start with index 7.  This is because the system defines 0-6 for such properties as PROP_KEY, PROP_ENUM, PROP_X, PROP_Y, PROP_WIDTH,PROP_HEIGHT, and PROP_ELEMENTREF. These are common properties all widgets must have and our base class WidgetModel defines and handles them.

We next define the default values.  These we make public in case code generation needs to identify whether or not users overrode the defaults. Not generally needed but useful sometimes.  
Example:
``` 
  /** The Property Defaults */
  static public final Integer DEF_TEXT_SZ               = Integer.valueOf(10);
  static public final Integer DEF_STARTING_ANGLE        = Integer.valueOf(0);
  static public final Integer DEF_ANGULAR_RANGE         = Integer.valueOf(360);
  static public final Boolean DEF_DIRECTION             = Boolean.TRUE;
      ...
  static public final Color   DEF_INACTIVE_COLOR        = new Color(64,64,64); // GUISlice DRK2
  static public final Color   DEF_TEXT_COLOR            = Color.YELLOW;

   We also must define the default height and width of our new widget:

  static private final int DEF_WIDTH = 100;
  static private final int DEF_HEIGHT= 100;
```

---------
<div style="page-break-after: always;"></div>

Now tricky part here is that when we add new properties we want them to get unique meta_id's for each property.  This allows us to read older project files and allows us to re-arrange the order of presentation in future versions of the builder.  This has proven quite handy so far. So how do we keep track?  open src/main/java/resources/templates/meta_ids.csv and you see the list of existing properties. We will reuse the existing ids for common properties like enums, elementref, and basic colors but we need to add to  list for the new properties.
Here I add:
```
      RING-100,Starting Angle
      RING-101,Angular Range
      RING-102,Direction Clockwise?
      RING-103,Min
      RING-104,Max
      RING-105,Starting Value
      RING-106,Number of Segments
      RING-107,Line Thickness
      RING-108,Use Gradient Color
      RING-109,Active Flat Color
      RING-110,Active Gradient Color Start
      RING-111,Active Gradient Color End
      RING-112,Inactive Color
```
## 2.3 Model Constructor
Next we create the RingGaugeModel constructor it follows the same pattern all widgets use.
``` 
    /**
     * Instantiates a new text model.
     */
    public RingGaugeModel() {
      ff = FontFactory.getInstance();
      initProperties();
    }
```
The FontFactory instance is needed to deal with fonts.  Some models will also need the ColorFactory.  
​    
---------
<div style="page-break-after: always;"></div>

## 2.4 Initialize our Model's Properties
After all of this we can initialize the properties of RingGaugeModel. Create initProperties routine. Again it follows the same pattern all models follow but simply with different properties.
```    
    /**
     * Initializes the properties.
     */
    protected void initProperties()
    {
      widgetType = EnumFactory.RINGGAUGE;
      data = new Object[23][5];

      initCommonProps(DEF_WIDTH, DEF_HEIGHT);
```
First: You must define the size of our data array. We ended our property indexes at 22 but since we are 0 based we define it as data[23][5]. You need to look at WidgetModel to understand why I use 5 columns instead of 2 columns.

Second: You must call initCommonProps().  This is a routine in our base class WidgetModel that will setup our common properties.

Now you have to initialize each property one by one. Use the convenience routine initProp() which takes 6 arguments.  The first is the row inside our data array. The next 5 are the column values.
Example:
```    
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"Field Size",DEF_TEXT_SZ);
```

 - Here PROP_TEXT_SZ is the row index into our data array, its defined above as 8. The property text size refers to the storage needed by the text part of our RingGauge and is an integer. 

 - Setting the second parameter to our property's class allows all of the magic to take place. If set it to class JTextField.class it will think we have a font defined and if the user clicks on it the FontChooser comes up.  Set it to Color.class and a ColorChooser will be invoked.
​
 - "TXT-205" is the meta-id for text storage and is used by all text properties.
​
 - The next parameter is for the READ-ONLY setting.  If set to TRUE users will this property greyed out and not be allowed to change its value. For this field we set it Boolean.FALSE so users can modify it.
​
 - "Field Size" is the text users will see on screen.  This should be inside a resource bundle so we can translate it but for now...
​
 - DEF_TEXT_SZ is the starting value for this property.  Note that it can't be int 10 or crashes  and burns will follow.  It must be a first class object and must match the second parameter we passed in.

Note that the order we initialize our properties isn't important but keeping them in order will save your sanity when debugging. :)    

Once initProps() is completed you might need to add a changeValueAt and/or Read Model functions. RingGaugeModel needs both. 


---------
<div style="page-break-after: always;"></div>

## 2.5 Add Convenience Functions
The next item is to add some convenience functions. These will be used by code gen to access properties. Usually  to determine if any optional API calls need to be made. 

For example, code gen will need to test if users want to change  direction from clockwise to counter clockwise. We will have a Boolean  object stored in row "PROP_DIRECTION" for this data but we want a simple  way to access it from other Classes. Asking for a pointer to 
```
    data[PROP_DIRECTION] [PROP_VAL_VALUE] 
```
Now this is a bit too cumbersome. So instead we add:
```
    /**
     * Checks if is Clockwise direction.
     *
     * @return true, if is Clockwise
     */
    public boolean isClockwise() {
      return ((Boolean) data[PROP_DIRECTION][PROP_VAL_VALUE]).booleanValue();
    }
```
I prefer to add one getter for each property and then I don't need to  think about it later. No need to deal with setters. They already exist  for common properties and are likely the only ones that will be  required. 
​    
---------
<div style="page-break-after: always;"></div>

## 2.6 changeValueAt Function
Back to changeValueAt function to understand why we need it you must  know a little more about Java Swing's JTable. JTables define certain  functions that can be overridden to customize behavior. 
​    
Our application supports Undo and Redo. This subject requires a document on its own but a simple explanation of the flow is that in our  base class we have overridden the JTable function setValueAt(). 

When users click on a JTable cell and make a new entry JTable calls  setValueAt giving the row and column of the modified table and the new  cells contents. 

Every sample program I've seen using a JTable model simply takes the  data received here and plops it into the data array. Now, If we did that  Undo and Redo would be impossible. Instead we override setValueAt routine as so: 
```
    @Override
    public void setValueAt(Object value, int row, int col) {
      if (col == COLUMN_VALUE) {
        // commands are used to support undo and redo actions.
        PropertyCommand c = new PropertyCommand(this, value, row);
        execute(c);
      }
    }
```
Here we are using the Command Object pattern to implement Undo. You might think we just changed the property value but no we instead did a great deal of book keeping to track that the user wants to make this  change. Only then we call another routine changeValueAt() that JTable  knows nothing about. 

The changeValueAt() will actually be called by our Command structure  and will actually modify our table cell with the new data. Again needs to be in another document for a full explanation. 

Here you only need to know why we need a custom version of changeValueAt. Compared to other models RingGauge is fairly simple but it does have one quirk. initProps sets Use Gradient Colors? to FALSE and therefore also sets Gradient Start and End Colors to read-only.
​    
What happens when or if the user answers TRUE for Use Gradient colors? The fields for Gradient Start and End Colors are still read-only!
​    

---------
<div style="page-break-after: always;"></div>

We fix this inside a overridden changeValuAt routine like so:
```    
    if (row == PROP_USE_GRADIENT) {
      if (useGradientColors()) {
        data[PROP_GRADIENT_START_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_GRADIENT_END_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_ACTIVE_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_GRADIENT_START_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_GRADIENT_END_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_ACTIVE_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
      }   
    }
```
This is a simplification of course but you can look the actual code over for  a better understanding.
Because we needed changeValuAt we also need to supply a readModel() routine to do the same thing for when we open a saved project.  Otherwise, fields might be marked READ-ONLY that should be modifiable once loaded into memory.
Its beyond the scope of this document to go much further into creating a model. Looking at other models should help make things clearer.

# Step 3 Create Widget
Add the widget to our builder.widgets package.  This class is responsible for drawing the widget,  Again see example widgets in this package as a guide. Here you might also want to check the GUIslice draw code for this element. Often, the drawing code is similar to Java's 2D paint code. So here we create RingGaugeWidget,java class and override the paintComponent function.

---------
<div style="page-break-after: always;"></div>

# Step 4 Add Widget to WidgetFactory
In order for the builder to create the widget we need to modify WidgetFactory.

Its responsible for creating new widgets.  It starts by creating a copy of the  widget at a x, y position that's been passed into it.  This will most often be a random position (paste operation being an exception). The Widget's constructor is responsible to also creating the Widget's model and keeping a copy inside its  private data.  The model however doesn't yet have a unique key, unique enum, or default element reference. The factory must generate these next and assign  them to the model. All of this is easier to show than explain here is our new case statement for RingGauge:
``` 
      case EnumFactory.RINGGAUGE:
        widget = new RingGaugeWidget(x, y);
        model = (RingGaugeModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = RingGaugeModel.ELEMENTREF_NAME;
          strKey = model.getKey();
          n = strKey.indexOf("$");
          strCount = strKey.substring(n+1, strKey.length());
          ref = ref + strCount;
          ((RingGaugeModel) model).setElementRef(ref);
        }
        break;
```
One final note:  While it doesn't require any code changes you should know that once outside the case statement we turn on event handling for this widget. Without this none of the views would know to update their paint routines on changes.

# Step 5 Create Widget's Icon 
Now we have our model and widget for RingGauge but users will still be unaware of the new feature.  Also, while WidgetFactory can create a new RingGauge no one knows to ask. So now we need start by creating an icon that represents a RingGauge and adding it to our resources so Java can load it.  

Widget icons are stored in folder builder/src/main/java/resources/icons/controls The icon should be a png file and 32-by-32 size in keeping with the other icons.
Note that we have code that can resize the icon as needed.

So we now add "ringgauge_32x.png" to our resources.   

---------
<div style="page-break-after: always;"></div>

# Step 6 Add Widget To Ribbon
Now we make RingGauge control visible to our users by modifying our builder.views.Ribbon class.  The ribbon is divided into tasks and bands. Tasks are the main tabs, in this case "Toolbox" and "Page Layout". While bands further divide the "Tasks" into sections. 

The "Toolbar" task has bands for "pages", "text", "controls", "gauges", and "misc" for anything we can't find a better home for. Since RingGauge is a Gauge we add it to the "Gauges" band. 
We do this by adding it to "initGauges()" routine.  We need to place it in the same order as we want users to see.  
We also need to decide if the icon should normally be full size or if its not important. You will note in the code that both Progressbar and Slider have been added with "MEDIUM" size. They look fine large or small. In the RingGauge case it looks like crap reduced in size so we set the size to "TOP" and place it first so the others can be reduced in size if the ribbon needs to do so. The code inside initGauges looks like:
```   
    btn_ringgauge = new JCommandButton("Ring Gauge",
        cu.getResizableIcon("resources/icons/controls/ringgauge_32x.png"));
    btn_ringgauge.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("ringgauge");
        }
      });
    btn_ringgauge.setActionRichTooltip(new RichTooltip(
        "Ring Gauge",
        "Awaiting details to complete implemention."));
    band.addCommandButton(btn_ringgauge, TOP);
```
You should now notice a mb.sendActionCommand emitting "ringauge".

In builder.views.MenuBar you will see setActionCommands which are Java Swing supported events.  These show up in classes that register as Listeners and implement the Java class ActionListener.

The JRibbon class isn't a swing class and it only takes JCommandButtons or other swing objects that are wrapped in JRibbonComponent.
For example, to use JSpinner you add a "new JRibbonComponent(new JSpinner( etc...);".
Why, is this important? JCommandButtons only take a subset of swing options and setActionCommand isn't one of them.  Now it would be possible to do everything required by RingGauge inside this ActionPerformed routine but I prefer to use one class that acts as the main listener so all of our code is in one place.  So I use my own event class MsgBoard and MsgEvents to send my events around to other views and listeners.

Class builder.views.RibbonListener handles our Ribbon events and our MenuBar events. These events are coded as simple strings, in the RingGauge case its "ringgauge".

---------
<div style="page-break-after: always;"></div>

# Step 7 Modify RibbonListener
Now modify builder.views.RibbonListener to act upon the new "ringgauge" command.
RibbonListener has two routines that listen for events. actionPerformed() which is swing supported and gets events from our MenuBar (file new, open, save etc..) and updateEvents() which will receive events from our ribbon.  Now some options are on both our Ribbon and on the MenuBar.  In this case the code is duplicated between them.  
The idea however is to keep this code short and simple just respond to the event and pass it along to our Controller (in the MODEL/VIEW/CONTROLLER object pattern) since it is the only one with actual implementation code.

The "ringgauge" is a ribbon only command so we modify updateEvents by adding a new case statement as so:
```   
      case "ringgauge":
        createWidget(EnumFactory.RINGGAUGE);
        break;
```
As I said short and sweet.  createWidget code doesn't require any changes but so you can understand what its doing I show here:
```   
    public void createWidget(String name) {
      int  x = rand.nextInt(generalModel.getWidth()-25);
      int  y = rand.nextInt(generalModel.getHeight()-25);
      Widget w = WidgetFactory.getInstance().createWidget(name, x, y);
      if (w != null) {
        controller.addWidget(w);
      }
    }
```
We assign a random position to the new widget and call on WidgetFactory to do its magic. Finally, we ask the Controller to place the widget on our current page element.

---------
<div style="page-break-after: always;"></div>

# Step 8 Test Code to this point
After all of this you can now test your new code.  Compile and run the builder and debug any issues that come up.  Most likely your control won't look quite right and you will need play with the draw code and make revisions.  Be sure to test all of the properties.  If you crash on startup and seem to be inside the JTable two of the most common errors are invalid index which means your data[][] was set too  small for the number of properties or null value exception which means either you skipped a property or the data[][] array is too large.  
If all else fails in your examination of your code add this debug code to your new subclassed model:
```   
  //debug code
  @Override
  public Object getValueAt(int row, int col) {
    if (col == 1) {
      if (data[row][PROP_VAL_VALUE] == null) {
        System.out.println("row: " + row + " " + data[row][PROP_VAL_NAME] + " = null");
      } else {
        System.out.println("row: " + row + " " + data[row][PROP_VAL_NAME] + " = " +
          data[row][PROP_VAL_VALUE].toString());
      }
    }
    return data[row][col+3];
  }
```
If this doesn't help or its not either of the two above cases, well that why you get the big bucks.

---------
<div style="page-break-after: always;"></div>

# Step 9 Code Generation
Well now that you are here you can add our new RingGauge to a project, save and restore it (hopefully), but users will expect one more thing.  It to show up in their code.
Now RingGauge is simple, no callbacks or other new features so this won't be hard. If the new widget is more complex; Look for examples that come close to its functionality.
First up is to understand the basic flow of code generation.  We start inside class builder.codegen.CodeGenerator which will do all file handling and will setup the overall flow of code generation.  This flow is called "pipes".  Linux has a slightly different flow than Arduino but this doesn't affect us here.  For us the pipes we are interested in are:

 -  builder.codegen.IncludesPipe which will handle outputting: #include "elem/XRingGauge.h"
 -  builder.codegen.ExtraElementPipe which should output: gslc_tsXRingGauge m_sXRingGauge;
 -  builder.codegen.SaveRefPipe which will output: gslc_tsElemRef*  m_pElemXRingGauge = NULL;
 -  builder.codegen.InitGuiPipe which needs a new case statement to call out RingGaugeCodeBlock

Of course, you need to create builder.codegen.block.RingGaugeCodeBlock.java to handle actual Creation API.  We will add that class later for now just the case statement to InitGuiPipe.outputAPI.
```
      case EnumFactory.RINGGAUGE:
        RingGaugeCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
```
Now if there were a flash version of RingGauge you would need the case statement to divert to another routine to handle _P APIs, say builder.codegen.flash.RingGauge_P_CodeBlock   

# Step 10 - Edit IncludesPipe.java to support RingGauge
Inside doCodeGen() you will see another large switch statement. We need to add a RingGauge case statement with the name of our include file minus "elem/" and the ".h" which is assumed by the template "<ELEM_INCLUDE>".
```
        case EnumFactory.RINGGAUGE:
          headerList.add("XRingGauge");
          break;
```

---------
<div style="page-break-after: always;"></div>

# Step 11 - Edit ExtraElementPipe.java
Add RingGauge to ExtraElementPipe.doCodeGen near the middle (line 157) you will find a for loop:
```
    for (WidgetModel m : cg.getModels()) {
      // check to flash API version
      if (m.useFlash())
        continue;
```
Add an if statement to deal with XRingGauge and remember the names must be unique so add the widgets key number value (the number after the $ as in "RingGauge$1") to our standard storage name for XRingGauge elements like so:
```
      if (m.getType().equals(EnumFactory.RINGGAUGE)) {
        strElement = "gslc_tsXRingGauge";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXRingGauge" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
```
You can look over CodeUtil.java for convenience functions.

# Step 12- SaveRefPipe.java 
Nothing special about RingGauge so no code change here since SaveRefPipe already handles any widgets with ElemRefs by taking the values from our models. Just be sure the RingGauge model has support for a unique name for the element ref.

# Step 13- Edit InitGuiPipe.java
This pipe deals with everything for creation of widgets. As stated above we need to add a case statement to forward code generation to our new routine RingGaugeCodeBlock.
Inside outputAPI() routine is one large switch statement that looks a lot like the one in WidgetFactory.
Here we add the code to jump to RingGaugeCodeBlock.
```    
      case EnumFactory.RINGGAUGE:
        RingGaugeCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
```

---------
<div style="page-break-after: always;"></div>

# Step 14 Modify arduino.t and linux.t template files for RungGauge
Now we get a break from the builder a bit and examine a sample GUIslice program that uses our new widget. We need to pull out the API calls and  and add them to our template files so we can do the code generation. One thing here makes the job a little easier is that the Arduino and Linux versions are the same and we don't have to support _P Flash. 
So we only need to edit src/main/java/resources/templates/arduino.t and copy over any additions to linux.t. If we needed to support Flash versions we also add Flash _P templates for each option.  No need to add flash versions to "linux.t".  You can look at <CHECKBOX_P> and <CHECKBOXSETSTATE_P> for an example.
We start with what we need for creation and look at ex42_ard_ring.ino:
```
   // Create a RingGauge
   static char m_str10[10] = "";
   pElemRef = gslc_ElemXRingGaugeCreate(&m_gui, E_ELEM_XRING, E_PG_MAIN, &m_sXRingGauge,
     (gslc_tsRect) { 80, 80, 100, 100 }, m_str10, 10, E_FONT_DIAL);
   gslc_ElemXRingGaugeSetRange(&m_gui, pElemRef, 0, 100);
   gslc_ElemXRingGaugeSetPos(&m_gui, pElemRef, 60); // Set initial value
```
Now I could argue that the last two calls are optional but I'm going to include them anyways. My new template looks like this with macro substitution.
```  
<RINGGAUGE>
  
  // Create ring gauge $<COM-002> 
  static char m_sRingText$<COM-018>[$<TXT-205>] = "";
  pElemRef = gslc_ElemXRingGaugeCreate(&m_gui,$<COM-002>,$<COM-000>,&m_sXRingGauge$<COM-018>,
          (gslc_tsRect){$<COM-003>,$<COM-004>,$<COM-005>,$<COM-006>},
          (char*)m_sRingText$<COM-018>,$<TXT-205>,$<TXT-211>);
  gslc_ElemXRingGaugeSetRange(&m_gui, pElemRef, $<RING-103>, $<RING-104>);
  gslc_ElemXRingGaugeSetPos(&m_gui, pElemRef, $<RING-105>); // Set initial value
<STOP>
```
Looking closely you will notice E_ELEM_XRING became $<COM-002> and E_PG_MAIN becomes $<COM-000>.
All macros begin with "$<" and end with ">".  In between we use the meta-ids used in the initProp() calls our model is using to make our properties unique.  What this means is that we can get our model to do most of the work for us. 
Additional templates will be needed to handle any options we have for our RingGauge.
```
<RINGGAUGE_RANGE>
  gslc_ElemXRingGaugeSetAngleRange(&m_gui,pElemRef, $<RING-100>, $<RING-101>, RING-102);
<STOP>
```

---------
<div style="page-break-after: always;"></div>

# Step 15 Create builder.codegen.block.RingGaugeCodeBlock.java
Now we come to the final part.  Creating the actual code to output. You should examine RingGaugeCodeBlock.java to see what is required.
A small part is shown here so you can see that our RingGauge model and <RINGGAUGE> template is doing most of the work of code generation.
```    
  static public StringBuilder process(CodeGenerator cg, TemplateManager tm, StringBuilder sBd, String pageEnum, WidgetModel wm) {
    RingGaugeModel m = (RingGaugeModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    template = tm.loadTemplate(RINGGAUGE_TEMPLATE);
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);
    
    template = tm.loadTemplate(ELEMENTREF_TEMPLATE);
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);

    template.clear();
    outputLines.clear();
    map.clear();
    return sBd;   
  }
```
We still need to deal with options, however. One example is use Gradient colors, example code:
```
    if (m.useGradientColors()) {
      template = tm.loadTemplate(GRADIENTCOL_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
```
  Another is change of line thickness:
```
    if (m.getLineThickness() != RingGaugeModel.DEF_LINE_SZ) {
      template = tm.loadTemplate(LINE_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
```
You should now have a better understanding of the process and at least know what code and packages to look over.
Now your real work begins debugging the code generation.  I would like to say that after all of this and the careful planning that went into it everything will just work the first time.  In the case it took me a couple of hours to debug this widget.
The first time with default setting work ok. Then I started testing options. Tried text background.  Oops.  Turns out my documentation is wrong no such call as gslc_ElemXRungGaugeSetColorBackground.  No problem easy to remove code. Bring up Builder and crash and burn. Null value exception in JTable. Forgot to decrement data from 24 to 23 items. Try again and test active flat color and get this output in my test.ino file.
```
    gslc_ElemXRingGaugeSetRingColorFlat(&m_gui,pElemRef, GSLC_COL_ORANGE);
<STOP
<RINGGAUGE_GRADIENTCOL>
  gslc_ElemXRingGaugeSetRingColorGradient(&m_gui, pElemRef, GSLC_COL_RED, GSLC_COL_BLUE);
<STOP
<RINGGAUGE_INACTIVECOL>
  gslc_ElemXRingGaugeSetRingColorInactive(&m_gui,pElemRef, $<RING-113>);
```
Finger slippen.  Missing '>' at the end of '<STOP' Fixed and try again.  Test another option. Get a invalid complier error $ turns out a macro defined as $<RING-113> should have been $<RING-112> for inactive color property.  Well, you get the idea.
After that it just adding in code to match ex42_ard_ring.ino and testing each operation with a compile and run.  Test adjusting colors for the gradient selection, Takes time...
