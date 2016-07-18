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
 * Compatibility.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.datatabactions;

import adams.core.Utils;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.data.DataContainer;

import java.awt.event.ActionEvent;

/**
 * Checks the compatibility of the selected datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Compatibility
  extends AbstractDataTabAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Compatibility() {
    super();
    setName("Compatibility");
    setIcon("validate.png");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    StringBuilder	result;
    DataContainer[]	conts;
    int			i;
    int			n;
    String		msg;

    result = new StringBuilder();
    conts  = getSelectedData();
    for (i = 0; i < conts.length - 1; i++) {
      for (n = i + 1; n < conts.length; n++) {
	msg = conts[i].getData().equalHeadersMsg(conts[n].getData());
	result.append(
	    "--> " + conts[i].getData().relationName() + " [" + conts[i].getSourceFull() + "]\n"
	    + "and " + conts[n].getData().relationName() + " [" + conts[n].getSourceFull() + "]\n"
	    + Utils.indent((msg == null) ? "match" : msg, 4) + "\n");
      }
    }
    GUIHelper.showInformationMessage(getOwner(), result.toString(), "Compatibility");
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(getTable().getSelectedRowCount() > 1);
  }
}
