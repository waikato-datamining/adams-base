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
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.datatab;

import adams.flow.core.Token;
import adams.flow.transformer.WekaInstancesAppend;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.core.Instances;

import java.awt.event.ActionEvent;

/**
 * Appends the selected datasets into single dataset (one-after-the-other).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Merge
 */
public class Append
  extends AbstractDataTabAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Append() {
    super();
    setName("Append");
    setIcon("append.png");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer[]	conts;
    Instances[]		data;
    int			i;
    Token 		token;
    WekaInstancesAppend append;
    String		msg;
    MemoryContainer	cont;

    // collect data
    conts = getSelectedData();
    data  = new Instances[conts.length];
    msg   = "";
    for (i = 0; i < conts.length; i++) {
      if (i > 0)
	msg += ", ";
      data[i] = conts[i].getData();
      msg += conts[i].getData().relationName();
    }
    logMessage("Appending: " + msg);

    // transform
    token  = new Token(data);
    append = new WekaInstancesAppend();
    msg    = append.setUp();
    if (msg == null) {
      append.input(token);
      msg = append.execute();
      if (msg == null) {
	token = append.output();
	cont = new MemoryContainer((Instances) token.getPayload());
	getData().add(cont);
	fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
      }
    }
    append.destroy();

    if (msg != null)
      logError(msg, "Failed to append datasets");
    else
      logMessage("Appended datasets successfully!");
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(getTable().getSelectedRowCount() > 1);
  }
}
