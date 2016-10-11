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
 * AbstractTextDisplayPanel.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.flow.core.Token;
import adams.gui.core.ExtensionFileFilter;

/**
 * Ancestor for panels that can be created from tokens and supply the
 * underlying text.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTextDisplayPanel
  extends AbstractDisplayPanel
  implements TextSupplier, UpdateableDisplayPanel {

  /** for serialization. */
  private static final long serialVersionUID = 4636549254255374437L;

  /**
   * Initializes the panel.
   *
   * @param name	the name of the panel
   */
  public AbstractTextDisplayPanel(String name) {
    super(name);
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    return null;
  }

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
  
  /**
   * Returns a potentially updated token. Uses {@link #supplyText()} to
   * return a textual token.
   * 
   * @return		the token, null if not available
   * @see		#supplyText()
   */
  @Override
  public Token getUpdatedToken() {
    String	current;
    
    current = supplyText();
    if (current == null)
      return null;
    else
      return new Token(current);
  }
}
