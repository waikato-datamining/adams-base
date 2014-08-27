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
 * AbstractImageJFeatureGenerator.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.features;

import adams.core.ImageJHelper;
import adams.data.image.AbstractImageFeatureGenerator;
import adams.data.imagej.ImagePlusContainer;

/**
 * Abstract base class for ImageJ feature generators.
 *
 * Derived classes only have to override the <code>doProcess(ImagePlus)</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageJFeatureGenerator
  extends AbstractImageFeatureGenerator<ImagePlusContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 4566948525813804085L;
  
  /**
   * Optional checks of the image.
   * <p/>
   * Ensures that ImageJ plugins path is set.
   *
   * @param img		the image to check
   */
  @Override
  protected void checkImage(ImagePlusContainer img) {
    super.checkImage(img);
    
    ImageJHelper.setPluginsDirectory();
  }
}
