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
 * Append.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import javax.swing.JPanel;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.gui.dialog.ApprovalDialog;

/**
 * Appends all the selected spreadsheets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Append
  extends AbstractSelectedSheetsDataPlugin {

  /** for serialization. */
  private static final long serialVersionUID = -407256934764293407L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the selected spreadsheets into a single spreadsheet, one after the other.";
  }

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  @Override
  public String getMenuText() {
    return "Append...";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getMenuIcon() {
    return "append.png";
  }

  /**
   * Returns whether the processed sheet should rather get placed ("in-place")
   * in the same tab rather than in a new one.
   * 
   * @return		true if to replace current sheet with processed one
   */
  @Override
  public boolean isInPlace() {
    return false;
  }

  /**
   * Returns whether the panel can be processed.
   * 
   * @param panel	the panel to check
   * @return		true if can be processed
   */
  @Override
  public boolean canProcess(SpreadSheetPanel panel) {
    return super.canProcess(panel) && (panel.getOwner().getTabCount() > 1);
  }
  
  /**
   * Creates the panel with the configuration (return null to suppress display).
   * 
   * @param dialog	the dialog that is being created
   * @return		the generated panel, null to suppress
   */
  @Override
  protected JPanel createConfigurationPanel(ApprovalDialog dialog) {
    return null;
  }

  /**
   * Processes all the selected panels.
   * 
   * @return		spreadsheet if successful, otherwise null
   */
  @Override
  protected SpreadSheet process() {
    SpreadSheet		result;
    int			i;
    
    if (m_SelectedPanels.length < 2) {
      getLogger().warning("At least two panels must be selected!");
      return null;
    }
    
    result = m_SelectedPanels[0].getSheet().getClone();
    for (i = 1; i < m_SelectedPanels.length; i++)
      result = SpreadSheetHelper.append(result, m_SelectedPanels[i].getSheet(), true);

    return result;
  }
}
