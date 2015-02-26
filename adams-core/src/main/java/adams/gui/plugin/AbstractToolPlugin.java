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
 * AbstractToolPlugin.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.plugin;

import adams.core.logging.LoggingObject;
import adams.gui.core.GUIHelper;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.util.Hashtable;

/**
 * Ancestor for plugins for a tool panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractToolPlugin<T extends JPanel>
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -8139858776265449470L;

  /** for storing the last setup for a plugin. */
  protected static Hashtable<Class,Object> m_LastSetup;
  static {
    m_LastSetup = new Hashtable<Class,Object>();
  }

  /** the current panel. */
  protected T m_CurrentPanel;

  /** whether the user canceled the operation. */
  protected boolean m_CanceledByUser;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  public abstract String getCaption();

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  public String getIconName() {
    return null;
  }

  /**
   * Returns the icon.
   *
   * @return		the icon or the empty icon if no icon name available
   */
  public ImageIcon getIcon() {
    ImageIcon		result;

    if (getIconName() != null) {
      if (getIconName().contains("/"))
        result = GUIHelper.getExternalIcon(getIconName());
      else
        result = GUIHelper.getIcon(getIconName());
    }
    else {
      result = GUIHelper.getEmptyIcon();
    }

    return result;
  }

  /**
   * Checks whether there is a setup available for the class of this object.
   *
   * @return		true if a setup is available
   */
  protected boolean hasLastSetup() {
    return m_LastSetup.containsKey(getClass());
  }

  /**
   * Returns the last setup for this object's class.
   *
   * @return		the setup, null if none available
   */
  protected Object getLastSetup() {
    return m_LastSetup.get(getClass());
  }

  /**
   * Stores the setup for this object's class.
   *
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
   * Checks whether the plugin can be executed given the specified panel.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  public abstract boolean canExecute(T panel);

  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message. Using an empty 
   * 			string will suppress the error message display and
   * 			the creation of a log entry.
   */
  protected abstract String doExecute();

  /**
   * Creates the log message.
   * 
   * @return		the message, null if none available
   */
  protected abstract String createLogEntry();

  /**
   * Performs the actual logging.
   *
   * @param msg		the message to log
   */
  protected abstract void doLog(String msg);

  /**
   * Logs the successful action to the log.
   */
  protected void log() {
    String	msg;
    
    msg = createLogEntry();
    if (msg != null)
      doLog(msg);
  }
  
  /**
   * Executes the plugin.
   *
   * @param panel	the panel to use the plugin on
   * @return		null if OK, otherwise error message
   */
  public String execute(T panel) {
    String	result;
    
    m_CurrentPanel   = panel;
    m_CanceledByUser = false;
    result = doExecute();
    
    if (result == null)
      log();
    
    return result;
  }
}
