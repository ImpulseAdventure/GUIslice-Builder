package builder.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import builder.prefs.RecentFiles;

public class RecentFilePanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private JList<File> list;
  private FileListModel listModel = null;
  private JFileChooser fileChooser;
  protected RecentFiles recentStorage;
  static final int MAXFILES = 5;

  public RecentFilePanel() {
    if (listModel == null) {
      listModel = new FileListModel();
    }
    list = new JList<>(listModel);
  }
  
  public RecentFilePanel(JFileChooser chooser) {
    fileChooser = chooser;
    JLabel title = new JLabel("Recent Files", JLabel.CENTER);
    if (listModel == null) {
      listModel = new FileListModel();
    }
    list = new JList<>(listModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new FileListCellRenderer());

    setLayout(new BorderLayout());
    add(title, BorderLayout.NORTH);
    add(new JScrollPane(list), BorderLayout.CENTER);

    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          File file = list.getSelectedValue();
          // You might like to check to see if the file still exists...
          fileChooser.setSelectedFile(file);
        }
      }
    });
  }

  public void clearList() {
    listModel.clear();
  }

  public void add(File file) {
    listModel.add(file);
  }

  public String getCurrentFolder() {
    return listModel.getCurrentFolder();
  }

  
  public class FileListModel extends AbstractListModel<File> {
    private static final long serialVersionUID = 1L;
    /** lru cache of recent file names */
    public  List<String> lruList;
    public  List<File> files;

    public FileListModel() {
      files = new ArrayList<>();
      if (recentStorage == null)
        recentStorage = new RecentFiles(MAXFILES);
      lruList = recentStorage.load();
      for (String fileRef : lruList) {
        File file = new File(fileRef);
        if (file.exists()) {
          files.add(file);
        }
      }
    }

    public void add(File file) {
        setMostRecentFile(file);
        fireContentsChanged(this, 0, getSize());
    }
    
    public String getCurrentFolder() {
      if (files.size() > 0) {
        return files.get(0).getParent();
      }
      return null;
    }

    @Override
    public int getSize() {
      return files.size();
    }

    @Override
    public File getElementAt(int index) {
      return files.get(index);
    }

    public void clear() {
      int size = files.size() - 1;
      if (size >= 0) {
        files.clear();
        fireIntervalRemoved(this, 0, size);
      }
    }

    
    /**
     * Sets the most recent file accessed.
     *
     * @param fileName
     *          the new most recent file accessed.
     */
    public void setMostRecentFile(File file) {
      String fileName = file.getPath();
      String temp = "";
      boolean bFound = false;
      if (lruList.size() > 0) {
        /* update our lru but first check and see if the file is already present
         * if so, make sure its the first in our list
         * 
         * The complication here is the list API's use
         * of add and set. 
         * add(index,element) will insert at index but 
         * also push everyone down by 1 
         * while set will replace the element.
         */
        for (int i=0; i<lruList.size(); i++) {
          temp = lruList.get(i);
          if (lruList.get(i).equals(fileName)) {
            // its in our list so make it the first value
            if (i==0)
              return;
            // push everyone above down by 1 then place it first
            for(int j=i; j>0; j--) {
              temp = lruList.get(j-1);
              lruList.set(j,lruList.get(j-1));
            }
            lruList.set(0,fileName);
            bFound = true;
            break;
          }
        }
        if (!bFound) {
          /* file name not in our list so push everyone 
           * down by one and place our new file at top
           */
          int n = lruList.size();
          if (n == MAXFILES) {
            /* we reached max size so make a hole
             * by pushing everyone down by one
             * and dropping the list's last value
             * then add fileName to the top.
             */
            for (int i=n-1; i>0; i--) {
              temp = lruList.get(i-1);
              lruList.set(i,temp);
            }
            lruList.set(0,fileName);
          } else {
            /* extend our list by 1 while placing
             * fileName at the top
             */
            lruList.add(0,fileName);
          }
        }
      } else {
        // empty list so just add file name
        lruList.add(fileName);
      }
      recentStorage.store(lruList);
    }
  }

  public class FileListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof File) {
        File file = (File) value;
        Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
        setIcon(ico);
        setToolTipText(file.getParent());
        setText(file.getName());
      }
      return this;
    }

  }

}

