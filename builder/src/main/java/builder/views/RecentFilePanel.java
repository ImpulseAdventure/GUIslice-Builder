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

import builder.prefs.GeneralEditor;

public class RecentFilePanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private JList<File> list;
  private FileListModel listModel = null;
  private JFileChooser fileChooser;

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
    private static final int MAXFILES = 6;
    /** lru cache of recent file names */
    public  ArrayList<String> lruList;
    public  List<File> files;

    public FileListModel() {
      files = new ArrayList<>();
      lruList = new ArrayList<String>();
      String listOfFiles = GeneralEditor.getInstance().getRecentFilesList();
      if (listOfFiles != null) {
        String[] fileList = listOfFiles.split(File.pathSeparator);
        for (String fileRef : fileList) {
            File file = new File(fileRef);
            if (file.exists()) {
                files.add(file);
                lruList.add(fileRef);
            }
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
      // update our lru but first check and see if the file is already present
      // if so, remove it then add to front of list
      String fileName = file.getPath();
      lruList.remove(fileName);
      lruList.add(0, fileName);
      if (lruList.size() > MAXFILES) {
        lruList.remove(MAXFILES);
      }
//      files.clear();
      StringBuilder sb = new StringBuilder();
      for (int index = 0; index < lruList.size(); index++) {
        fileName = lruList.get(index);
        file = new File(fileName);
//  No point in adding file to files list since it won't ever be used again.
//        files.add(file);
        if (sb.length() > 0) {
          sb.append(File.pathSeparator);
        }
        sb.append(file.getPath());
      }
      System.out.println();
      GeneralEditor.getInstance().setRecentFilesList(sb.toString());
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

