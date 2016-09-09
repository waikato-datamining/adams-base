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
 * AbstractChooserPanelWithDirectoryListerSupport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.core.ClassLister;
import adams.core.io.lister.DirectoryListerHandler;

/**
 * Ancestor for chooser panels that handle directory listers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractChooserPanelWithDirectoryListerSupport<T>
  extends AbstractChooserPanel<T>
  implements DirectoryListerHandler {

  private static final long serialVersionUID = 5317183406919541169L;

  /**
   * Returns the type of chooser (description).
   *
   * @return		the type
   */
  public abstract String getChooserType();

  /**
   * Returns the choosers classes.
   *
   * @return		the classes
   */
  public static Class[] getChoosers() {
    return ClassLister.getSingleton().getClasses(AbstractChooserPanelWithDirectoryListerSupport.class);
  }
}
