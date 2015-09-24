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
 * AbstractBoofCVFeatureGenerator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.features;

import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.AbstractImageFeatureGenerator;

/**
 * Abstract base class for BoofCV feature generation.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBoofCVFeatureGenerator
  extends AbstractImageFeatureGenerator<BoofCVImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 4566948525813804085L;
}
