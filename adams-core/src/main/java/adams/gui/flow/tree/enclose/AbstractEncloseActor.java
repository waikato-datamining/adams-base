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
 * AbstractEncloseActor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.enclose;

import adams.core.ClassLister;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.gui.core.MenuItemComparator;
import adams.gui.flow.tree.StateContainer;

import javax.swing.JMenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ancestor for classes that can enclose actors somehow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEncloseActor
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -3692601200334292986L;

  /** the logger for static methods. */
  protected static Logger LOGGER = LoggingHelper.getLogger(AbstractEncloseActor.class);

  /**
   * Checks whether this enclose suggestion is available.
   *
   * @param state	the current state
   * @return		true if enclosing is possible
   */
  protected abstract boolean canEnclose(StateContainer state);

  /**
   * Returns a menu item that will perform the enclosing if selected.
   *
   * @param state	the current state
   * @return		the list of potential swaps
   */
  public abstract JMenuItem enclose(StateContainer state);

  /**
   * Generates all the possible enclose menu items.
   *
   * @param state	the current state
   * @return		the list of potential swaps
   */
  public static JMenuItem[] encloseAll(StateContainer state) {
    List<JMenuItem>		result;
    Class[]			classes;
    AbstractEncloseActor 	enclose;

    result  = new ArrayList<>();
    classes = ClassLister.getSingleton().getClasses(AbstractEncloseActor.class);
    for (Class cls: classes) {
      try {
	enclose = (AbstractEncloseActor) cls.newInstance();
	if (enclose.canEnclose(state))
	  result.add(enclose.enclose(state));
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Failed to retrieve enclose suggestions from: " + cls.getName(), e);
      }
    }

    Collections.sort(result, new MenuItemComparator());

    return result.toArray(new JMenuItem[result.size()]);
  }
}
