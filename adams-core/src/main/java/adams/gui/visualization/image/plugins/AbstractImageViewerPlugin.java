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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.util.Hashtable;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
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
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -8139858776265449470L;

  /** for storing the last setup for a plugin. */
  protected static Hashtable<Class,Object> m_LastSetup;
  static {
    m_LastSetup = new Hashtable<Class,Object>();
  }

  /** the current panel. */
  protected ImagePanel m_CurrentPanel;

  /** whether the user canceled the operation. */
  protected boolean m_CanceledByUser;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  public abstract String getCaption();

  /**
   * Checks whether there is a setup available for the class of this object.
   *
   * @param obj		the object to check for
   * @return		true if a setup is available
   */
  protected boolean hasLastSetup() {
    return m_LastSetup.containsKey(getClass());
  }

  /**
   * Returns the last setup for this object's class.
   *
   * @param obj		the object (actually the class) to get the setup for
   * @return		the setup, null if none available
   */
  protected Object getLastSetup() {
    return m_LastSetup.get(getClass());
  }

  /**
   * Stores the setup for this object's class.
   *
   * @param obj		the object (actually the class) to get the setup for
   * @param setup	the setup to store
   */
  protected void setLastSetup(Object setup) {
    m_LastSetup.put(getClass(), setup);
  }

  /**
   * Returns whether the operation was canceled by the user.
   *
   * @return		true if the user canceled the operation
   */
  public boolean getCanceledByUser() {
    return m_CanceledByUser;
  }

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  public abstract boolean canExecute(ImagePanel panel);

  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message
   */
  protected abstract String doExecute();

  /**
   * Executes the plugin.
   *
   * @param panel	the panel to use the plugin on
   * @return		null if OK, otherwise error message
   */
  public String execute(ImagePanel panel) {
    m_CurrentPanel   = panel;
    m_CanceledByUser = false;
    return doExecute();
  }

  /**
   * Returns a list with classnames of plugins.
   *
   * @return		the plugin classnames
   */
  public static String[] getPlugins() {
    return ClassLister.getSingleton().getClassnames(AbstractImageViewerPlugin.class);
  }
}
