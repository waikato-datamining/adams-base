/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * SimpleHTML.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheetpreview;

import adams.core.net.HtmlUtils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.gui.core.BaseScrollPane;

import javax.swing.JEditorPane;
import java.awt.BorderLayout;

/**
 * Simply displays the specified columns as HTML.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleHTML
  extends AbstractSpreadSheetPreview {

  private static final long serialVersionUID = -3455538265861369251L;

  /**
   * Displays the rows as HTML.
   */
  public static class SimpleTextPanel
    extends AbstractSpreadSheetPreviewPanel {

    private static final long serialVersionUID = -3009467848041701061L;

    /** the pane for displaying the HTML. */
    protected JEditorPane m_PaneHTML;

    /** the column range to display. */
    protected SpreadSheetColumnRange m_Columns;

    /** the string to use for missing values. */
    protected String m_MissingValue;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Columns = new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setLayout(new BorderLayout());

      m_PaneHTML = new JEditorPane();
      m_PaneHTML.setContentType("text/html");
      m_PaneHTML.setEditable(false);
      add(new BaseScrollPane(m_PaneHTML), BorderLayout.CENTER);
    }

    /**
     * Sets the columns to display.
     *
     * @param value 	the columns
     */
    public void setColumns(SpreadSheetColumnRange value) {
      m_Columns = value;
    }

    /**
     * Returns the columns to display.
     *
     * @return 		the columns
     */
    public SpreadSheetColumnRange getColumns() {
      return m_Columns;
    }

    /**
     * Sets the string to use for missing values.
     *
     * @param value 	the string
     */
    public void setMissingValue(String value) {
      m_MissingValue = value;
    }

    /**
     * Returns the string to use for missing values.
     *
     * @return 		the string
     */
    public String getMissingValue() {
      return m_MissingValue;
    }

    /**
     * Previews the spreadsheet.
     *
     * @param sheet	the sheet to preview
     * @param rows	the rows to preview
     * @return		null if successfully previewed, otherwise error message
     */
    @Override
    protected String doPreview(SpreadSheet sheet, int[] rows) {
      StringBuilder	text;
      int[] 		cols;
      int		i;
      int		n;
      Row		row;
      String		cell;

      m_PaneHTML.setText("");

      m_Columns.setData(sheet);
      cols = m_Columns.getIntIndices();

      text = new StringBuilder();
      text.append("<html\n>");
      for (i = 0; i < rows.length; i++) {
        if (i > 0)
          text.append("<br><br>\n");
        text.append("<table border=\"1\" cellspacing=\"0\">\n");
	row = sheet.getRow(rows[i]);
        for (n = 0; n < cols.length; n++) {
	  text.append("<tr>");
	  text.append("<td valign=\"top\">");
	  text.append("<b>");
          text.append(sheet.getColumnName(cols[n]));
	  text.append("</b>");
	  text.append("</td>");
	  text.append("<td>");
          if (row.hasCell(cols[n]) && !row.getCell(cols[n]).isMissing())
	    cell = row.getCell(cols[n]).getContent();
	  else
	    cell = m_MissingValue;
	  text.append(HtmlUtils.convertLines(HtmlUtils.toHTML(cell), true));
	  text.append("</td>");
	  text.append("</tr>\n");
	}
	text.append("</table>\n");
      }
      text.append("</html\n>");

      m_PaneHTML.setText(text.toString());
      m_PaneHTML.setCaretPosition(0);

      return null;
    }
  }

  /** the column range to display. */
  protected SpreadSheetColumnRange m_Columns;

  /** the string to use for missing values. */
  protected String m_MissingValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply displays the specified columns as HTML.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));

    m_OptionManager.add(
      "missing-value", "missingValue",
      "N/A");
  }

  /**
   * Sets the columns to display.
   *
   * @param value 	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to display.
   *
   * @return 		the columns
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The spreadsheet columns to display.";
  }

  /**
   * Sets the string to use for missing values.
   *
   * @param value 	the string
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the string to use for missing values.
   *
   * @return 		the string
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The string to use for missing values.";
  }

  /**
   * Generates the preview panel.
   *
   * @return		the preview panel, null if none generated
   */
  @Override
  public AbstractSpreadSheetPreviewPanel generate() {
    SimpleTextPanel	result;

    result = new SimpleTextPanel();
    result.setColumns(m_Columns.getClone());
    result.setMissingValue(m_MissingValue);

    return result;
  }
}
