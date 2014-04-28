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
 * SpreadSheetQueryChooserPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.awt.Dialog.ModalityType;

import adams.gui.dialog.SpreadSheetQueryDialog;
import adams.parser.SpreadSheetQueryText;

/**
 * A panel that contains a text field with the current spreadsheet query and a
 * button for bringing up an editor for the query.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetQueryChooserPanel
  extends AbstractChooserPanel<SpreadSheetQueryText> {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the dialog for editing the query. */
  protected SpreadSheetQueryDialog m_DialogSpreadSheetQuery;
  
  /**
   * Initializes the panel with default query.
   */
  public SpreadSheetQueryChooserPanel() {
    this(new SpreadSheetQueryText("SELECT *"));
  }

  /**
   * Initializes the panel with the given query.
   *
   * @param query		the query to use
   */
  public SpreadSheetQueryChooserPanel(SpreadSheetQueryText query) {
    super();

    setCurrent(query);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    if (getParentDialog() != null)
      m_DialogSpreadSheetQuery = new SpreadSheetQueryDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      m_DialogSpreadSheetQuery = new SpreadSheetQueryDialog(getParentFrame(), true);
    m_DialogSpreadSheetQuery.setTitle("Edit spreadsheet query");
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected SpreadSheetQueryText doChoose() {
    m_DialogSpreadSheetQuery.setVisible(true);
    if (m_DialogSpreadSheetQuery.getOption() != SpreadSheetQueryDialog.APPROVE_OPTION)
      return null;
    return m_DialogSpreadSheetQuery.getQuery();
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(SpreadSheetQueryText value) {
    return value.getValue();
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected SpreadSheetQueryText fromString(String value) {
    return new SpreadSheetQueryText(value);
  }
}
