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
 * ImageTransformer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.core.FlowContextHandler;

/**
 * Abstract base class for AbstractImage transformations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of image to process
 */
public interface ImageTransformer<T extends AbstractImageContainer>
  extends OptionHandler, Comparable, CleanUpHandler, QuickInfoSupporter, FlowContextHandler {

  /**
   * Transforms the given image.
   *
   * @param img		the image to transform
   * @return		the generated image(s)
   */
  public T[] transform(T img);
}
