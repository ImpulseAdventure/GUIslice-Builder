/**
 *
 * The MIT License
 *
 * Copyright 2018-2020 Paul Conti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package builder.widgets;

import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.models.BoxModel;
import builder.models.CheckBoxModel;
//import builder.models.CircleModel;
import builder.models.GraphModel;
import builder.models.ImageModel;
import builder.models.ImgButtonModel;
import builder.models.LineModel;
import builder.models.ListBoxModel;
import builder.models.NumberInputModel;
import builder.models.ProgressBarModel;
import builder.models.RadialGaugeModel;
import builder.models.RadioButtonModel;
import builder.models.RampGaugeModel;
import builder.models.RingGaugeModel;
import builder.models.SeekbarModel;
import builder.models.SliderModel;
import builder.models.SpinnerModel;
import builder.models.TextBoxModel;
import builder.models.TextInputModel;
import builder.models.TextModel;
import builder.models.ToggleButtonModel;
import builder.models.TxtButtonModel;
import builder.models.WidgetModel;
import builder.widgets.Widget;

/**
 * A factory for creating Widget objects.
 * 
 * @author Paul Conti
 * 
 */
public class WidgetFactory  {
  
  /** The instance. */
  private static WidgetFactory instance = null;
  
  /**
   * Gets the single instance of WidgetFactory.
   *
   * @return single instance of WidgetFactory
   */
  public static synchronized WidgetFactory getInstance()  {
      if (instance == null) {
          instance = new WidgetFactory();
      }
      return instance;
  }  

  /**
   * Instantiates a new widget factory.
   */
  public WidgetFactory()
  {
  }

  /**
   * Creates a new Widget object.
   *
   * @param widgetType
   *          the widget type
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   * @return the widget
   */
  public Widget createWidget(String widgetType, int x, int y)
  {
//    System.out.println("WF create: " + widgetType + " X=" + x +" Y=" + y);
    Widget widget=null;
    WidgetModel model=null;
    String ref = "";    
    switch(widgetType) {
      case EnumFactory.BOX:
        widget = new BoxWidget(x, y);
        model = (BoxModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
        
      case EnumFactory.CHECKBOX:
        widget = new CheckBoxWidget(x, y);
        model = (CheckBoxModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
/*        
      case EnumFactory.CIRCLE:
        widget = new CircleWidget(x, y);
        model = (CircleModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
*/       
      case EnumFactory.GRAPH:
        widget = new GraphWidget(x, y);
        model = (GraphModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), GraphModel.ELEMENTREF_NAME);
          ((GraphModel) model).setElementRef(ref);
        }
        break;
        
      case EnumFactory.IMAGE:
        widget = new ImageWidget();
        model = (ImageModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
        
      case EnumFactory.IMAGEBUTTON:
        widget = new ImgButtonWidget();
        model = (ImgButtonModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
        
      case EnumFactory.LINE:
        widget = new LineWidget(x, y);
        model = (LineModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
        
      case EnumFactory.LISTBOX:
        widget = new ListBoxWidget(x, y);
        model = (ListBoxModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), ListBoxModel.ELEMENTREF_NAME);
          ((ListBoxModel) model).setElementRef(ref);
        }
        break;
      
      case EnumFactory.NUMINPUT:
        widget = new NumberInputWidget(x, y);
        model = (NumberInputModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), NumberInputModel.ELEMENTREF_NAME);
          ((NumberInputModel) model).setElementRef(ref);
        }
        break;
      
      case EnumFactory.PROGRESSBAR:
        widget = new ProgressBarWidget(x, y);
        model = (ProgressBarModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), ProgressBarModel.ELEMENTREF_NAME);
          ((ProgressBarModel) model).setElementRef(ref);
        }
        break;
        
      case EnumFactory.RADIOBUTTON:
        widget = new RadioButtonWidget(x, y);
        model = (RadioButtonModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
        
      case EnumFactory.RAMPGAUGE:
        widget = new RampGaugeWidget(x, y);
        model = (RampGaugeModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), RampGaugeModel.ELEMENTREF_NAME);
          ((RampGaugeModel) model).setElementRef(ref);
        }
        break;
        
      case EnumFactory.RADIALGAUGE:
        widget = new RadialGaugeWidget(x, y);
        model = (RadialGaugeModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), RadialGaugeModel.ELEMENTREF_NAME);
          ((RadialGaugeModel) model).setElementRef(ref);
        }
        break;
        
      case EnumFactory.RINGGAUGE:
        widget = new RingGaugeWidget(x, y);
        model = (RingGaugeModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), RingGaugeModel.ELEMENTREF_NAME);
          ((RingGaugeModel) model).setElementRef(ref);
        }
        break;
        
      case EnumFactory.SEEKBAR:
        widget = new SeekbarWidget(x, y);
        model = (SeekbarModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), SeekbarModel.ELEMENTREF_NAME);
          ((SeekbarModel) model).setElementRef(ref);
        }
        break;
  
      case EnumFactory.SLIDER:
        widget = new SliderWidget(x, y);
        model = (SliderModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), SliderModel.ELEMENTREF_NAME);
          ((SliderModel) model).setElementRef(ref);
        }
        break;
  
      case EnumFactory.SPINNER:
        widget = new SpinnerWidget(x, y);
        model = (SpinnerModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = SpinnerModel.ELEMENTREF_NAME;
          ref = CommonUtils.createElemName(model.getKey(), SpinnerModel.ELEMENTREF_NAME);
          ((SpinnerModel) model).setElementRef(ref);
        }
        break;
      
      case EnumFactory.TEXT:
        widget = new TextWidget(x, y);
        model = (TextModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
  
      case EnumFactory.TEXTBOX:
        widget = new TextBoxWidget(x, y);
        model = (TextBoxModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), TextBoxModel.ELEMENTREF_NAME);
          ((TextBoxModel) model).setElementRef(ref);
        }
        break;
  
      case EnumFactory.TEXTBUTTON:
        widget = new TxtButtonWidget(x, y);
        model = (TxtButtonModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        break;
        
      case EnumFactory.TEXTINPUT:
        widget = new TextInputWidget(x, y);
        model = (TextInputModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
          ref = CommonUtils.createElemName(model.getKey(), TextInputModel.ELEMENTREF_NAME);
          ((TextInputModel) model).setElementRef(ref);
        }
        break;
      
      case EnumFactory.TOGGLEBUTTON:
        widget = new ToggleButtonWidget(x, y);
        model = (ToggleButtonModel) widget.getModel();
        if (widget != null) {
          model.setKey(EnumFactory.getInstance().createKey(widgetType));
          model.setEnum(EnumFactory.getInstance().createEnum(widgetType));
        }
        ref = CommonUtils.createElemName(model.getKey(), ToggleButtonModel.ELEMENTREF_NAME);
        ((ToggleButtonModel) model).setElementRef(ref);
        break;
        
      default:
        break;
    }
    if (widget != null) {
      model.TurnOnEvents();
    }
    return widget;
  }
}
