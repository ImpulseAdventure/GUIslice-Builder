package builder.models;

import java.awt.event.ActionListener;
import java.util.EventListener;

public interface RibbonModel {
  public ActionListener getIncomingActionListener();
  public void addEventListener(EventListener listener);
}
