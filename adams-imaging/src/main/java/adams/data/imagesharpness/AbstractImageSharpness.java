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
 * AbstractImageSharpness.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.imagesharpness;

import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;

/**
 * Ancestor for classes that determine whether an image is sharp (ie focused)
 * or not.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageSharpness
  extends AbstractOptionHandler {

  /**
   * Hook method for performing checks.
   * </p>
   * Default implementation only ensures that image is present.
   *
   * @param cont the container to check
   */
  protected void check(AbstractImageContainer cont) {
    if (cont == null)
      throw new IllegalStateException("No image container provided!");
    if (cont.getImage() == null)
      throw new IllegalStateException("No image provided!");
  }

  /**
   * Checks the sharpness of an image.
   *
   * @param cont the image to check
   * @return true if sharp
   */
  protected abstract boolean isImageSharp(AbstractImageContainer cont);

  /**
   * Checks the sharpness of an image.
   *
   * @param cont the image to check
   * @return true if sharp
   */
  public boolean isSharp(AbstractImageContainer cont) {
    check(cont);
    return isImageSharp(cont);
  }
}
