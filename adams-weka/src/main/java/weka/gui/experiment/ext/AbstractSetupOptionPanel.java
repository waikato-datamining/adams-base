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
 * AbstractSetupOptionPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import adams.gui.core.BasePanel;

/**
 * Ancestor for panels that get added to a {@link AbstractSetupPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSetupOptionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -8401733616002637499L;
  
  /** the setup panel this option panel belongs to. */
  protected AbstractSetupPanel m_Owner;
  
  /** whether to ignored changes. */
  protected boolean m_IgnoreChanges;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_IgnoreChanges = false;
  }
  
  /**
   * Sets the setup panel this option panel belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(AbstractSetupPanel value) {
    m_Owner = value;
  }
  
  /**
   * Returns the setup panel this option panel belongs to.
   * 
   * @return		the owner
   */
  public AbstractSetupPanel getOwner() {
    return m_Owner;
  }
  
  /**
   * Sets the modified flag in the owner.
   */
  protected void modified() {
    if (m_IgnoreChanges)
      return;
    if (m_Owner != null)
      m_Owner.setModified(true);
  }
}
