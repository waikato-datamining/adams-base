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
 * AbstractOpenCVFeatureGenerator.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.features;

import adams.data.image.AbstractImageFeatureGenerator;
import adams.data.opencv.OpenCVImageContainer;

/**
 * Abstract base class for OpenCV feature generators.
 *
 * Derived classes only have to override the <code>doProcess</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOpenCVFeatureGenerator
  extends AbstractImageFeatureGenerator<OpenCVImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 4566948525813804085L;

}
