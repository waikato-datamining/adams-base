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
 * SimpleText.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheetpreview;

import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;

import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Simply displays the specified columns as text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleText
  extends AbstractSpreadSheetPreview {

  private static final long serialVersionUID = -3455538265861369251L;

  /**
   * Displays the rows in a text area.
   */
  public static class SimpleTextPanel
    extends AbstractSpreadSheetPreviewPanel {

    private static final long serialVersionUID = -3009467848041701061L;

    /** the text area for displaying the data. */
    protected BaseTextArea m_TextArea;

    /** the column range to display. */
    protected SpreadSheetColumnRange m_Columns;

    /** the string to use for missing values. */
    protected String m_MissingValue;

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setLayout(new BorderLayout());

      m_TextArea = new BaseTextArea();
      m_TextArea.setTextFont(Fonts.getMonospacedFont());
      add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
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
     * Sets the font for the text area.
     *
     * @param value 	the font
     */
    public void setTextFont(Font value) {
      m_TextArea.setTextFont(value);
    }

    /**
     * Returns the font for the text area.
     *
     * @return 		the font
     */
    public Font getTextFont() {
      return m_TextArea.getTextFont();
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

      m_TextArea.setText("");

      m_Columns.setData(sheet);
      cols = m_Columns.getIntIndices();

      text = new StringBuilder();
      for (i = 0; i < rows.length; i++) {
	row = sheet.getRow(rows[i]);
        if (i > 0)
          text.append("---\n\n");
        for (n = 0; n < cols.length; n++) {
          text.append(sheet.getColumnName(cols[n])).append(":\n");
          if (row.hasCell(cols[n]) && !row.getCell(cols[n]).isMissing())
	    cell = row.getCell(cols[n]).getContent();
	  else
	    cell = m_MissingValue;
	  text.append(Utils.indent(cell, 2)).append("\n");
	}
      }

      m_TextArea.setText(text.toString());
      m_TextArea.setCaretPosition(0);

      return null;
    }
  }

  /** the column range to display. */
  protected SpreadSheetColumnRange m_Columns;

  /** the font to use. */
  protected Font m_Font;

  /** the string to use for missing values. */
  protected String m_MissingValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply displays the specified columns as text.";
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
      "font", "font",
      getDefaultFont());

    m_OptionManager.add(
      "missing-value", "missingValue",
      "?");
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
   * Returns the default font for the dialog.
   *
   * @return		the default font
   */
  protected Font getDefaultFont() {
    return Fonts.getMonospacedFont();
  }

  /**
   * Sets the font for the text area.
   *
   * @param value 	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font for the text area.
   *
   * @return 		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font for the text area.";
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
    result.setTextFont(m_Font);
    result.setColumns(m_Columns.getClone());
    result.setMissingValue(m_MissingValue);

    return result;
  }
}
