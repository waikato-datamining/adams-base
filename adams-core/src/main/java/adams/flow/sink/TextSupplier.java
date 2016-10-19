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
 * TextSupplier.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.core.TextSupporter;
import adams.gui.core.ExtensionFileFilter;

/**
 * Interface for sinks that supply a string. This can be used for saving
 * this text.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TextSupplier
  extends TextSupporter {

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption();

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter();
  
  /**
   * Supplies the text. May get called even if actor hasn't been executed yet.
   *
   * @return		the text, null if none available
   */
  public String supplyText();
}
