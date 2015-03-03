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
 * AbstractImageViewerPlugin.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.gui.plugin.AbstractToolPlugin;
import adams.gui.visualization.image.ImagePanel;

/**
 * Ancestor for plugins for the ImageViewer.
 * <p/>
 * If the plugin modifies any panel, then an undo point should be added 
 * before updating the panel. Here is an example:
 * <pre>
 * m_CurrentPanel.addUndoPoint("Saving undo data...", "Filtering image: " + getCaption());
 * </pre> 
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageViewerPlugin
  extends AbstractToolPlugin<ImagePanel> {

  /** for serialization. */
  private static final long serialVersionUID = -8139858776265449470L;

  /**
   * Performs the actual logging.
   *
   * @param msg		the message to log
   */
  protected void doLog(String msg) {
    m_CurrentPanel.log(msg);
  }
}
