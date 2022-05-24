package builder.prefs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
//import java.util.Observable;
import java.util.prefs.Preferences;

import builder.Builder;

//public class RecentFiles extends Observable {
public class RecentFiles {

  private static final String MY_NODE = "com/impulseadventure/builder/recentfiles";
  private final static String RECENT_ITEM_STRING = "recent.item."; 
  private Preferences fPrefs;
  private int nMaxFiles;
  
  public RecentFiles(int nMaxFiles) {
    this.nMaxFiles = nMaxFiles;
    // get rid of the bugged Preferences warning - not needed in Java 9 and above
    System.setErr(new PrintStream(new OutputStream() {
        public void write(int b) throws IOException {}
    }));
    String prefNode = MY_NODE + Builder.VERSION_NO;
    fPrefs = Preferences.userRoot().node(prefNode);
    
  }

  public List<String> load()
  {
    List<String> items = new ArrayList<String>();
    for (int i = 0; i < nMaxFiles; i++) {
      String val = fPrefs.get(RECENT_ITEM_STRING + i, "");
      if (!val.equals("")) {
        items.add(val);
      } else break;
    }
    return items;
  }
  
  public void store(List<String> items) {
    for (int i = 0; i < nMaxFiles; i++) {
      if (i < items.size()) {
        fPrefs.put(RECENT_ITEM_STRING + i, items.get(i));
      } else {
        fPrefs.remove(RECENT_ITEM_STRING + i);
      }
    }
    /* remove setChanged() and notifyObservers() for Java 9 and above */
//    setChanged();
//    notifyObservers();
  }

}
