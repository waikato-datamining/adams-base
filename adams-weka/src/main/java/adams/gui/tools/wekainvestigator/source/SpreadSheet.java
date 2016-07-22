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
 * SpreadSheet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.source;

import adams.data.io.input.SpreadSheetReader;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.tools.wekainvestigator.data.SpreadSheetContainer;

import java.awt.event.ActionEvent;

/**
 * For loading ADAMS spreadsheets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheet
  extends AbstractSource {

  private static final long serialVersionUID = 5646388990155938153L;

  /** the filechooser. */
  protected SpreadSheetFileChooser m_FileChooser;

  /**
   * Instantiates the action.
   */
  public SpreadSheet() {
    super();
    setName("SpreadSheet");
    setIcon("spreadsheet.png");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    int					retVal;
    SpreadSheetReader			reader;
    adams.data.spreadsheet.SpreadSheet	sheet;
    SpreadSheetContainer		cont;

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showOpenDialog(m_Owner);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    reader = m_FileChooser.getReader();
    sheet  = reader.read(m_FileChooser.getSelectedFile());
    if (sheet == null) {
      logError("Failed to load spreadsheet: " + m_FileChooser.getSelectedFile(), "Error reading");
      return;
    }

    cont = new SpreadSheetContainer(reader, m_FileChooser.getSelectedFile());
    getData().add(cont);
    fireDataChange();
  }
}
