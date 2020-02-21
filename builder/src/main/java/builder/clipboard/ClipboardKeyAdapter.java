package builder.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * KeyAdapter to detect Windows standard cut, copy and paste keystrokes on a JTable and put them to the clipboard in Excel friendly plain text format. Assumes
 * that null represents an empty column for cut operations. Replaces line breaks and tabs in copied cells to spaces in the clipboard.
 * 
 * @author Charles Cordingley http://www.cordinc.com/blog/2010/12/cut-and-paste-from-java-swing.html
 * @see java.awt.event.KeyAdapter
 * @see javax.swing.JTable
 */
public class ClipboardKeyAdapter extends KeyAdapter {

	private static final String LINE_BREAK = "\n";
	private static final String CELL_BREAK = "\t";
	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	private final JTable table;

	public ClipboardKeyAdapter(JTable table) {
		this.table = table;
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.isControlDown()) {
			if (event.getKeyCode() == KeyEvent.VK_C) { // Copy
				cancelEditing();
				copyToClipboard(false);
			} else if (event.getKeyCode() == KeyEvent.VK_X) { // Cut
				cancelEditing();
				copyToClipboard(true);
			} else if (event.getKeyCode() == KeyEvent.VK_V) { // Paste
				cancelEditing();
				pasteFromClipboard();
			}
		}
	}

	private void copyToClipboard(boolean isCut) {
		int numCols = table.getSelectedColumnCount();
		int numRows = table.getSelectedRowCount();
		int[] rowsSelected = table.getSelectedRows();
		int[] colsSelected = table.getSelectedColumns();
		if (numRows != rowsSelected[rowsSelected.length - 1] - rowsSelected[0] + 1 || numRows != rowsSelected.length
				|| numCols != colsSelected[colsSelected.length - 1] - colsSelected[0] + 1 || numCols != colsSelected.length) {

			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
			return;
		}

		StringBuffer excelStr = new StringBuffer();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				excelStr.append(escape(table.getValueAt(rowsSelected[i], colsSelected[j])));
				if (isCut) {
					table.setValueAt(null, rowsSelected[i], colsSelected[j]);
				}
				if (j < numCols - 1) {
					excelStr.append(CELL_BREAK);
				}
			}
			excelStr.append(LINE_BREAK);
		}

		StringSelection sel = new StringSelection(excelStr.toString());
    try {
      CLIPBOARD.setContents(sel, sel);
    } catch (Exception e) {
      // trap class java.lang.IllegalStateException: cannot open system clipboard
      return;
    }
	}

	private void pasteFromClipboard() {
		int startRow = table.getSelectedRows()[0];
		int startCol = table.getSelectedColumns()[0];

		String pasteString = "";
		try {
			pasteString = (String) (CLIPBOARD.getContents(this).getTransferData(DataFlavor.stringFlavor));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid Paste Type", "Invalid Paste Type", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String[] lines = pasteString.split(LINE_BREAK);
		for (int i = 0; i < lines.length; i++) {
			String[] cells = lines[i].split(CELL_BREAK);
			for (int j = 0; j < cells.length; j++) {
				if (table.getRowCount() > startRow + i && table.getColumnCount() > startCol + j) {
					table.setValueAt(cells[j], startRow + i, startCol + j);
				}
			}
		}
	}

	private void cancelEditing() {
		if (table.getCellEditor() != null) {
			table.getCellEditor().cancelCellEditing();
		}
	}

	private String escape(Object cell) {
		return cell.toString().replace(LINE_BREAK, " ").replace(CELL_BREAK, " ");
	}
}