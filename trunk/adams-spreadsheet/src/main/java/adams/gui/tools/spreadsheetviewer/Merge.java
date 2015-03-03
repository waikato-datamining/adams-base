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
 * Merge.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.transformer.SpreadSheetMerge;

/**
 * Merges multiple spreadsheets into a single one.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Merge
  extends AbstractSelectedSheetsDataPluginWithGOE {

  /** for serialization. */
  private static final long serialVersionUID = -1680562922203169642L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges multiple spreadsheets into a single one (side-by-side).";
  }

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  @Override
  public String getMenuText() {
    return "Merge...";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getMenuIcon() {
    return "merge.png";
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
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return Actor.class;
  }

  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new SpreadSheetMerge();
  }

  /**
   * Returns whether the class can be changed in the GOE.
   * 
   * @return		true if class can be changed by the user
   */
  @Override
  protected boolean getCanChangeClassInDialog() {
    return false;
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
   * Processes all the selected panels.
   * 
   * @return		spreadsheet if successful, otherwise null
   */
  @Override
  protected SpreadSheet process() {
    SpreadSheet		result;
    SpreadSheetMerge	merge;
    SpreadSheet[]	sheets;
    int			i;
    Token		token;
    String		msg;
    
    if (m_SelectedPanels.length < 2) {
      getLogger().warning("At least two panels must be selected!");
      return null;
    }

    result = null;
    merge  = (SpreadSheetMerge) m_Editor.getValue();
    sheets = new SpreadSheet[m_SelectedPanels.length];
    for (i = 0; i < m_SelectedPanels.length; i++)
      sheets[i] = m_SelectedPanels[i].getSheet();
    token = new Token(sheets);
    merge.input(token);
    msg = merge.execute();
    if (msg == null) {
      if (merge.hasPendingOutput()) {
	token = merge.output();
	result = (SpreadSheet) token.getPayload();
	merge.cleanUp();
      }
      else {
	getLogger().severe("Merge did not generate any output!");
      }
    }
    else {
      getLogger().severe("Failed to merge spreadsheets: " + msg);
    }
    
    return result;
  }
}
