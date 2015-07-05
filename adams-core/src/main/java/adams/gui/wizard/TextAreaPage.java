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

/**
 * TextAreaPage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;

import java.awt.BorderLayout;

/**
 * Wizard page that use a {@link BaseTextArea} for entering free-form text.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class TextAreaPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the comma-separated list of all items. */
  public static final String KEY_TEXT = "text";

  /** the text area. */
  protected BaseTextArea m_TextArea;

  /**
   * Default constructor.
   */
  public TextAreaPage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public TextAreaPage(String pageName) {
    this();
    setPageName(pageName);
  }

  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_TextArea = new BaseTextArea(5, 40);
    add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
  }

  /**
   * Returns the number of columns in the TextArea.
   *
   * @return number of columns &gt;= 0
   */
  public int getColumns() {
    return m_TextArea.getColumns();
  }

  /**
   * Sets the number of columns for this TextArea.  Does an invalidate()
   * after setting the new value.
   *
   * @param columns the number of columns &gt;= 0
   * @exception IllegalArgumentException if columns is less than 0
   */
  public void setColumns(int columns) {
    m_TextArea.setColumns(columns);
  }

  /**
   * Returns the number of rows in the TextArea.
   *
   * @return the number of rows &gt;= 0
   */
  public int getRows() {
    return m_TextArea.getRows();
  }

  /**
   * Sets the number of rows for this TextArea.  Calls invalidate() after
   * setting the new value.
   *
   * @param rows the number of rows &gt;= 0
   * @exception IllegalArgumentException if rows is less than 0
   */
  public void setRows(int rows) {
    m_TextArea.setRows(rows);
  }

  /**
   * Returns the underlying text area.
   * 
   * @return		the text area
   */
  public BaseTextArea getTextArea() {
    return m_TextArea;
  }
  
  /**
   * Sets the text.
   * 
   * @param value	the text
   */
  public void setText(String value) {
    m_TextArea.setText(value);
  }

  /**
   * Returns the text.
   *
   * @return            the text
   */
  public String getText() {
    return m_TextArea.getText();
  }

  /**
   * Sets the content of the page (ie parameters) as properties.
   *
   * @param value	the parameters as properties
   */
  public void setProperties(Properties value) {
    if (value.hasKey(KEY_TEXT))
      m_TextArea.setText(value.getProperty(KEY_TEXT));
    else
      m_TextArea.setText("");
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	result;
    
    result = new Properties();
    result.setProperty(KEY_TEXT, m_TextArea.getText());
    
    return result;
  }
}
