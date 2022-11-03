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
 * AbstractOpenCVMultiImageOperation.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.multiimageoperation;

import adams.data.image.AbstractMultiImageOperation;
import adams.data.opencv.OpenCVImageContainer;

/**
 * Abstract base class for operations that require multiple images of type OpenCVImageContainer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOpenCVMultiImageOperation
  extends AbstractMultiImageOperation<OpenCVImageContainer> {

  private static final long serialVersionUID = 8469793062316240081L;
}
