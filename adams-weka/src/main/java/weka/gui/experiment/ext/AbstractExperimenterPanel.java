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
   */
  public void setOwner(ExperimenterPanel value) {
    m_Owner = value;
  }
  
  /**
   * Returns the experimenter this panel belongs to.
   * 
   * @return		the owner
   */
  public ExperimenterPanel getOwner() {
    return m_Owner;
  }
}
