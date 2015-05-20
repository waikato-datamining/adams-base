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
 * AbstractExperimenterPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

/**
 * Ancestor for panels in the experimenter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AbstractExperimenterPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 8569236330801128393L;
  
  /** the owner. */
  protected ExperimenterPanel m_Owner;
  
  /**
   * Sets the experimenter this panel belongs to.
   * 
   * @param value	the owner
   * @see		#ownerChanged()
   */
  public void setOwner(ExperimenterPanel value) {
    m_Owner = value;
    ownerChanged();
  }
  
  /**
   * Returns the experimenter this panel belongs to.
   * 
   * @return		the owner
   */
  public ExperimenterPanel getOwner() {
    return m_Owner;
  }
  
  /**
   * Gets called when the owner changes.
   * <br><br>
   * Default implementation does nothing
   */
  protected void ownerChanged() {
  }
  
  /**
   * Logs the message.
   * 
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    if (getOwner() != null)
      getOwner().logMessage(msg);
    else
      System.out.println(msg);
  }
  
  /**
   * Logs the error message and also displays an error dialog.
   * 
   * @param msg		the error message
   */
  public void logError(String msg, String title) {
    if (getOwner() != null) {
      getOwner().logError(msg, title);
    }
    else {
      System.err.println(msg);
      GUIHelper.showErrorMessage(this,
	  msg,
	  title);
    }
  }
}
