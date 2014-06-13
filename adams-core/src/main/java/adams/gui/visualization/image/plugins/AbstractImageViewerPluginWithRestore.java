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
 * AbstractImageViewerPluginWithRestore.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;

/**
 * Ancestor for plugins that allow restore of original state of image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageViewerPluginWithRestore
  extends AbstractImageViewerPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 6600786494338624691L;

  /** the backup of the current state. */
  protected List m_Backup;
  
  /**
   * Backs up the current image state.
   * 
   * @return		the backup
   */
  public List backup() {
    List	result;
    
    result = new ArrayList();
    result.add(m_CurrentPanel.getCurrentImage());
    result.add(m_CurrentPanel.getImageProperties());
    result.add(m_CurrentPanel.getAdditionalProperties());
    result.add(m_CurrentPanel.getScale());
    
    return result;
  }

  /**
   * Restores the image state.
   * 
   * @param state	the state backup
   */
  public void restore(List state) {
    BufferedImageContainer	cont;
    
    cont = new BufferedImageContainer();
    cont.setImage((BufferedImage) state.get(0));
    cont.setReport((Report) state.get(1));
    m_CurrentPanel.setCurrentImage(cont);
    m_CurrentPanel.setAdditionalProperties((Report) state.get(2));
    m_CurrentPanel.setScale((Double) state.get(3));
  }
  
  /**
   * Returns the current backup.
   * 
   * @return		the backup, null if none present
   */
  public List getBackup() {
    return m_Backup;
  }
  
  /**
   * The actual interaction with the user.
   * 
   * @return		null if OK, otherwise error message
   */
  protected abstract String doInteract();
  
  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    m_Backup = backup();
    result   = doInteract();
    if (result != null)
      restore(m_Backup);
    m_Backup = null;
    
    return result;
  }
}
