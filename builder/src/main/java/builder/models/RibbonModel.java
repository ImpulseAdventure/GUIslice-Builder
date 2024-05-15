package builder.models;

import java.awt.event.ActionListener;

public interface RibbonModel {
  public ActionListener getIncomingActionListener();
  public void addActionListener(ActionListener listener);
}
