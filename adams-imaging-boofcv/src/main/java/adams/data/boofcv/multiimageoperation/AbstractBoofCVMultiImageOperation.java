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
 * AbstractBoofCVMultiImageOperation.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.AbstractMultiImageOperation;

/**
 * Abstract base class for operations that require multiple images of type BoofCVImageContainer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBoofCVMultiImageOperation
  extends AbstractMultiImageOperation<BoofCVImageContainer> {

  private static final long serialVersionUID = 5861891266938039031L;
}
