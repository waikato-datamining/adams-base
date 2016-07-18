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
 * AbstractWorkspacePanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.workspace;

import adams.core.Utils;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.TitleGenerator;

import javax.swing.JMenuBar;

/**
 * The ancestor for a workspace panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWorkspacePanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = 7314544066929763500L;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TitleGenerator = new TitleGenerator(getDefaultTitle(), true);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }
  
  /**
   * Closes the dialog.
   */
  public void close() {
    GUIHelper.closeParent(this);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public abstract JMenuBar getMenuBar();

  /**
   * Updates title and menu items.
   */
  public void update() {
    updateTitle();
    updateMenu();
  }
  
  /**
   * Returns the title generator in use.
   * 
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Updates the title of the dialog.
   */
  protected abstract void updateTitle();

  /**
   * updates the enabled state of the menu items.
   */
  protected abstract void updateMenu();

  /**
   * Returns the default title.
   *
   * @return		the default title
   */
  protected abstract String getDefaultTitle();

  /**
   * Sets the base title to use for the title generator.
   * 
   * @param value	the title to use
   * @see		#m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    update();
  }
  
  /**
   * Returns the base title in use by the title generator.
   * 
   * @return		the title in use
   * @see		#m_TitleGenerator
   */
  public String getTitle() {
    return m_TitleGenerator.getTitle();
  }

  /**
   * Logs the exception with no dialog.
   * 
   * @param t		the exception
   */
  public void logMessage(Throwable t) {
    logMessage(Utils.throwableToString(t));
  }
  
  /**
   * Logs the message.
   * 
   * @param msg		the log message
   */
  public abstract void logMessage(String msg);

  /**
   * Logs the exception and also displays an error dialog.
   *
   * @param msg		the log message
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(String msg, Throwable t, String title) {
    logError(msg + "\n" + Utils.throwableToString(t), title);
  }

  /**
   * Logs the exception and also displays an error dialog.
   * 
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(Throwable t, String title) {
    logError(Utils.throwableToString(t), title);
  }
  
  /**
   * Logs the error message and also displays an error dialog.
   * 
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public abstract void logError(String msg, String title);
}
