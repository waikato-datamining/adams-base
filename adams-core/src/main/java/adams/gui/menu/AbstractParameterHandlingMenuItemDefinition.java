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
 * AbstractParameterHandlingMenuItemDefinition.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.AdditionalParameterHandler;

/**
 * Abstract menu item definition for definitions that handle parameters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractParameterHandlingMenuItemDefinition
  extends AbstractMenuItemDefinition
  implements AdditionalParameterHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1668108539323372465L;

  /** the additional parameters. */
  protected String[] m_Parameters;

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractParameterHandlingMenuItemDefinition() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  protected AbstractParameterHandlingMenuItemDefinition(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Parameters = new String[0];
  }

  /**
   * Sets the additional parameters.
   *
   * @param params	the parameters
   */
  public void setAdditionalParameters(String[] params) {
    m_Parameters = params;
  }
}
