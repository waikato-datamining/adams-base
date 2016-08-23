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
 * AbstractMenuItemDefinition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu;

import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import java.io.Serializable;

/**
 * Ancestor for menu items of terminal applications.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMenuItemDefinition
  implements Serializable, Comparable<AbstractMenuItemDefinition> {

  /** the "Program" category. */
  public final static String CATEGORY_PROGRAM = "Program";

  /** the "Tools" category. */
  public final static String CATEGORY_TOOLS = "Tools";

  /** the "Maintenance" category. */
  public final static String CATEGORY_MAINTENANCE = "Maintenance";

  /** the "Help" category. */
  public final static String CATEGORY_HELP = "Help";

  private static final long serialVersionUID = -6292351973681147424L;

  /** the owning application. */
  protected AbstractTerminalApplication m_Owner;

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractMenuItemDefinition() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AbstractMenuItemDefinition(AbstractTerminalApplication owner) {
    super();

    m_Owner = owner;

    initialize();
  }

  /**
   * Initializes members.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }

  /**
   * Sets the owning application.
   *
   * @param value	the owner
   */
  public void setOwner(AbstractTerminalApplication value) {
    m_Owner = value;
  }

  /**
   * Returns the owning application.
   *
   * @return		the owner
   */
  public AbstractTerminalApplication getOwner() {
    return m_Owner;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public abstract String getCategory();

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public abstract String getTitle();

  /**
   * Prepares before the execution.
   *
   * @param context	the context to use
   */
  protected void preRun(WindowBasedTextGUI context) {
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected abstract void doRun(WindowBasedTextGUI context);

  /**
   * Cleans up after the execution.
   *
   * @param context	the context to use
   */
  protected void postRun(WindowBasedTextGUI context) {
    //content.removeAllComponents();
  }

  /**
   * Returns the Runnable to use.
   *
   * @param context	the context to use
   * @return		the runnable
   */
  public Runnable getRunnable(final WindowBasedTextGUI context) {
    return () -> {
      preRun(context);
      doRun(context);
      postRun(context);
    };
  }

  /**
   * Uses category and title for sorting.
   *
   * @param o		the other definition to compare with
   * @return		less than zero, zero, or greater than zero if this
   * 			menuitem is less than, equal to or greater than the
   * 			other definition
   * @see 		#getCategory()
   * @see		#getTitle()
   */
  @Override
  public int compareTo(AbstractMenuItemDefinition o) {
    int		result;

    result = getCategory().compareTo(o.getCategory());
    if (result == 0)
      result = getTitle().compareTo(o.getTitle());

    return result;
  }

  /**
   * Checks whether the obj is the same definition (using category/title).
   *
   * @param obj		the object to compare with
   * @return		true if the same definition
   * @see		#compareTo(AbstractMenuItemDefinition)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractMenuItemDefinition)
      && (compareTo((AbstractMenuItemDefinition) obj) == 0);
  }
}
