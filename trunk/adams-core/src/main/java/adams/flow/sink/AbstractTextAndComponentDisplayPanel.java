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
 * AbstractTextAndComponentDisplayPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import javax.swing.JComponent;

import adams.gui.core.ExtensionFileFilter;

/**
 * Ancestor for panels that can be created from tokens and supply the
 * underlying component or text.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTextAndComponentDisplayPanel
  extends AbstractDisplayPanel
  implements ComponentSupplier, TextSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -2404789994825903954L;

  /**
   * Initializes the panel.
   *
   * @param name	the name of the panel
   */
  public AbstractTextAndComponentDisplayPanel(String name) {
    super(name);
  }

  /**
   * Supplies the component.
   *
   * @return		the component, null if none available
   */
  public abstract JComponent supplyComponent();

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  public abstract ExtensionFileFilter getCustomTextFileFilter();

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  public abstract String supplyText();
}
