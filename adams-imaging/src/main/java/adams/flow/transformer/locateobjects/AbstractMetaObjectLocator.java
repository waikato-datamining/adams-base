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
 * AbstractMetaObjectLocator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.locateobjects;

import java.awt.image.BufferedImage;

/**
 * Ancestor for object locators that enhance a base locator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaObjectLocator
  extends AbstractObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = -3309646673529069652L;

  /** the base locator. */
  protected AbstractObjectLocator m_Locator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "locator", "locator",
	    new PassThrough());
  }

  /**
   * Sets the base locator.
   *
   * @param value	the locator
   */
  public void setLocator(AbstractObjectLocator value) {
    m_Locator = value;
    reset();
  }

  /**
   * Returns the base locator.
   *
   * @return		the locator
   */
  public AbstractObjectLocator getLocator() {
    return m_Locator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locatorTipText() {
    return "The base locator to use.";
  }

  /**
   * Checks whether the input can be used.
   * <p/>
   * Ensures that base locator is set.
   *
   * @param image	the image to check
   */
  @Override
  protected void check(BufferedImage image) {
    super.check(image);

    if (m_Locator == null)
      throw new IllegalStateException("No base locator set!");
  }
}
