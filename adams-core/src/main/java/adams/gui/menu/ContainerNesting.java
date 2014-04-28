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
 * ContainerNesting.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JOptionPane;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTree;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;

/**
 * Displays the ContainerNesting dialog.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ContainerNesting
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 6149133251059325645L;

  /**
   * Initializes the menu item with no owner.
   */
  public ContainerNesting() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ContainerNesting(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    String	classname;
    BaseTree	tree;
    BasePanel	panel;
    
    classname = JOptionPane.showInputDialog("Please enter class name of Swing container:");
    if (classname == null)
      return;
    
    try {
      tree = adams.gui.tools.ContainerNesting.analyze((Container) Class.forName(classname).newInstance());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(null, "Failed to generate tree:\n" + e);
      return;
    }
    
    panel = new BasePanel(new BorderLayout());
    panel.add(new BaseScrollPane(tree));
    tree.setShowsRootHandles(true);
    tree.expand((BaseTreeNode) tree.getModel().getRoot());
    
    createChildFrame(panel, 800, 800);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Container nesting";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.DEVELOPER;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_MAINTENANCE;
  }
}