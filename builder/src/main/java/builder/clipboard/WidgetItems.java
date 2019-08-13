package builder.clipboard;

import java.util.List;

import builder.models.WidgetModel;

public class WidgetItems {

  private List<WidgetModel> items;

  public WidgetItems(List<WidgetModel> items){
     this.setItems(items);
  }

  public List<WidgetModel> getItems(){
     return this.items;
  }

  public void setItems(List<WidgetModel> items){
     this.items = items;
  }

}
