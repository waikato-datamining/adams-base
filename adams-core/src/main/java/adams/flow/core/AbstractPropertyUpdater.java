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
 * AbstractPropertyUpdater.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;

/**
 * Abstract ancestor for actors that manipulate properties of objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPropertyUpdater
  extends AbstractActor {

  /** for serialization. */
  private static final long serialVersionUID = 8068932300654252910L;

  /** the property path. */
  protected String m_Property;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "property", "property",
	    "");
  }

  /**
   * Sets the property to update.
   *
   * @param value	the property
   */
  public void setProperty(String value) {
    m_Property = value;
    reset();
  }

  /**
   * Returns the property to update.
   *
   * @return		the property
   */
  public String getProperty() {
    return m_Property;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propertyTipText() {
    return "The property to update.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "property", (m_Property.isEmpty() ? "-none-" : m_Property), "property: ");
  }

  /**
   * Updates the property.
   *
   * @param s		the string to set
   */
  protected abstract void updateProperty(String s);
}
