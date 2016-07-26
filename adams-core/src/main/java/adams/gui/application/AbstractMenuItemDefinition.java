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

/*
 * MenuItemCodelet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.application;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.option.OptionUtils;
import adams.gui.core.GUIHelper;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * Abstract ancestor for definining menu items in the ApplicationFrame menu.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMenuItemDefinition
  implements Serializable, Comparable<AbstractMenuItemDefinition> {

  /** for serialization. */
  private static final long serialVersionUID = -2406133385745656034L;

  /** the "Program" category. */
  public final static String CATEGORY_PROGRAM = "Program";

  /** the "Visualization" category. */
  public final static String CATEGORY_VISUALIZATION = "Visualization";

  /** the "Machine learning" category. */
  public final static String CATEGORY_MACHINELEARNING = "Machine learning";

  /** the "Tools" category. */
  public final static String CATEGORY_TOOLS = "Tools";

  /** the "Wizard" category. */
  public final static String CATEGORY_WIZARD = "Wizard";

  /** the "Maintenance" category. */
  public final static String CATEGORY_MAINTENANCE = "Maintenance";

  /** the "Help" category. */
  public final static String CATEGORY_HELP = "Help";

  /** the owning application. */
  protected AbstractApplicationFrame m_Owner;

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
  public AbstractMenuItemDefinition(AbstractApplicationFrame owner) {
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
  public void setOwner(AbstractApplicationFrame value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owning application.
   *
   * @return		the owner
   */
  public AbstractApplicationFrame getOwner() {
    return m_Owner;
  }

  /**
   * Returns whether the menu item is available.
   *
   * @return		true if available
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return null;
  }

  /**
   * Returns the icon.
   *
   * @return		the icon or null if no icon available
   */
  public ImageIcon getIcon() {
    ImageIcon		result;

    result = null;

    if (getIconName() != null) {
      if (getIconName().indexOf("/") > -1)
        result = GUIHelper.getExternalIcon(getIconName());
      else
        result = GUIHelper.getIcon(getIconName());
    }

    return result;
  }

  /**
   * Returns the JMenuItem to use.
   *
   * @return		the menu item
   */
  public abstract JMenuItem getMenuItem();

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public abstract String getTitle();

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

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public abstract boolean isSingleton();

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public abstract UserMode getUserMode();

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public abstract String getCategory();

  /**
   * Returns whether the menu item requires a restartable application.
   *
   * @return		true if a restartable application is required
   */
  public boolean requiresRestartableApplication() {
    return false;
  }

  /**
   * Creates a frame and returns it. Doesn't change width or height.
   *
   * @param c			the component to place, can be null
   * @return			the generated frame
   */
  protected ChildFrame createChildFrame(Component c) {
    return createChildFrame(c, new Dimension(-1, -1));
  }

  /**
   * creates a frame and returns it.
   *
   * @param c			the component to place, can be null
   * @param size		the size of the frame, ignored if -1 and -1
   * @return			the generated frame
   */
  protected ChildFrame createChildFrame(Component c, Dimension size) {
    if (getOwner() != null)
      return getOwner().createChildFrame(getTitle(), c, size, getIconName());
    else
      return AbstractApplicationFrame.createChildFrame(null, getTitle(), c, size, getIconName());
  }

  /**
   * Creates a window and returns it. Doesn't change width or height.
   *
   * @param c			the component to place, can be null
   * @return			the generated window
   */
  protected ChildWindow createChildWindow(Component c) {
    return createChildWindow(c, new Dimension(-1, -1));
  }

  /**
   * creates a window and returns it.
   *
   * @param c			the component to place, can be null
   * @param size		the size of the frame, ignored if -1 and -1
   * @return			the generated window
   */
  protected ChildWindow createChildWindow(Component c, Dimension size) {
    if (getOwner() != null)
      return getOwner().createChildWindow(getTitle(), c, size, getIconName());
    else
      return AbstractApplicationFrame.createChildWindow(null, getTitle(), c, size, getIconName());
  }

  /**
   * Returns the logger.
   * 
   * @return		the logger
   */
  protected Logger getLogger() {
    return getOwner().getLogger();
  }

  /**
   * Returns a list with classnames of codelets.
   *
   * @return		the codelet classnames
   */
  public static String[] getMenuItemDefinitions() {
    return ClassLister.getSingleton().getClassnames(AbstractMenuItemDefinition.class);
  }

  /**
   * Instantiates the menu item definition from the given commandline
   * (i.e., classname and optional parameters).
   *
   * @param owner	the owning application frame
   * @param cmdline	the classname (and optional parameters) of the
   * 			menu item definition to instantiate
   * @return		the instantiated menu item definition
   * 			or null if an error occurred
   */
  public static AbstractMenuItemDefinition forCommandLine(AbstractApplicationFrame owner, String cmdline) {
    AbstractMenuItemDefinition result;
    String[]			tmp;
    String			classname;
    String[]			params;

    try {
      tmp       = OptionUtils.splitOptions(cmdline);
      classname = tmp[0];
      params    = new String[tmp.length - 1];
      System.arraycopy(tmp, 1, params, 0, tmp.length - 1);
      result    = forName(owner, classname, params);
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate menu item definition: " + cmdline);
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Returns a menu item definition based on the classname and optional
   * parameters.
   *
   * @param owner	the owning application frame
   * @param classname	the class name of the menu item
   * @param params	the optional parameters, can be null
   * @return		the generated menu item definition, null in case of an error
   */
  public static AbstractMenuItemDefinition forName(AbstractApplicationFrame owner, String classname, String[] params) {
    AbstractMenuItemDefinition result;
    Class			cls;
    Constructor			constr;

    try {
      cls    = Class.forName(classname);
      constr = cls.getConstructor(new Class[]{AbstractApplicationFrame.class});
      result = (AbstractMenuItemDefinition) constr.newInstance(new Object[]{owner});
      if (result instanceof AdditionalParameterHandler) {
	if (params == null)
	  params = new String[0];
	((AdditionalParameterHandler) result).setAdditionalParameters(params);
      }
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate menu item definition: " + classname + "/" + Utils.arrayToString(params));
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}