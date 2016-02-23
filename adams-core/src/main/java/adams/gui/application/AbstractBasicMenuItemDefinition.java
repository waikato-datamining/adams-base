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

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

/**
 * Abstract ancestor for definining menu items in the ApplicationFrame menu.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBasicMenuItemDefinition
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -2406133385745656034L;

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractBasicMenuItemDefinition() {
    super();
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AbstractBasicMenuItemDefinition(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Whether to use a simple runnable for launching or a separate thread.
   *
   * @return		true if to use separate thread
   */
  protected boolean getUseThread() {
    return false;
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
    result.addActionListener((ActionEvent e) -> {
      if (isSingleton() && getOwner().containsWindow(getTitle())) {
        Child child = getOwner().getWindow(getTitle());
        if (child != null)
          getOwner().showWindow(child);
      }
      else {
        if (!getUseThread()) {
          SwingUtilities.invokeLater(() -> {
            preLaunch();
            launch();
            postLaunch();
          });
        }
        else {
          Thread thread = new Thread(() -> {
            preLaunch();
            launch();
            postLaunch();
          });
          thread.start();
        }
      }
    });

    return result;
  }
}