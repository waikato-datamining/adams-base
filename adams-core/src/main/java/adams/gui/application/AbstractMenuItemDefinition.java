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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.application;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.gui.core.GUIHelper;

/**
 * Abstract ancestor for definining menu items in the ApplicationFrame menu.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMenuItemDefinition
  implements Serializable {

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
   * Hook method that gets executed just before calling "launch()".
   * <br><br>
   * Default implementation does nothing.
   */
  public void preLaunch() {
  }

  /**
   * Launches the functionality of the menu item.
   */
  public abstract void launch();

  /**
   * Hook method that gets executed just after calling "launch()".
   * <br><br>
   * Default implementation does nothing.
   */
  public void postLaunch() {
  }

  /**
   * Returns the JMenuItem to use.
   *
   * @return		the menu item
   * @see		#launch()
   */
  public JMenuItem getMenuItem() {
    JMenuItem	result;

    result = new JMenuItem();
    result.setIcon(getIcon());
    result.setText(getTitle());
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (isSingleton() && getOwner().containsWindow(getTitle())) {
	  Child child = getOwner().getWindow(getTitle());
	  if (child != null)
	    getOwner().showWindow(child);
	}
	else {
	  Runnable run = new Runnable() {
	    @Override
	    public void run() {
	      preLaunch();
	      launch();
	      postLaunch();
	    }
	  };
	  SwingUtilities.invokeLater(run);
	}
      }
    });

    return result;
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public abstract String getTitle();

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
   * @see		#isVisible()
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
    return createChildFrame(c, -1, -1);
  }

  /**
   * creates a frame and returns it.
   *
   * @param c			the component to place, can be null
   * @param width		the width of the frame, ignored if -1
   * @param height		the height of the frame, ignored if -1
   * @return			the generated frame
   */
  protected ChildFrame createChildFrame(Component c, int width, int height) {
    if (getOwner() != null)
      return getOwner().createChildFrame(getTitle(), c, width, height, getIconName());
    else
      return AbstractApplicationFrame.createChildFrame(null, getTitle(), c, width, height, getIconName());
  }

  /**
   * Creates a window and returns it. Doesn't change width or height.
   *
   * @param c			the component to place, can be null
   * @return			the generated window
   */
  protected ChildWindow createChildWindow(Component c) {
    return createChildWindow(c, -1, -1);
  }

  /**
   * creates a window and returns it.
   *
   * @param c			the component to place, can be null
   * @param width		the width of the frame, ignored if -1
   * @param height		the height of the frame, ignored if -1
   * @return			the generated window
   */
  protected ChildWindow createChildWindow(Component c, int width, int height) {
    if (getOwner() != null)
      return getOwner().createChildWindow(getTitle(), c, width, height, getIconName());
    else
      return AbstractApplicationFrame.createChildWindow(null, getTitle(), c, width, height, getIconName());
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
    AbstractMenuItemDefinition	result;
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
    AbstractMenuItemDefinition	result;
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