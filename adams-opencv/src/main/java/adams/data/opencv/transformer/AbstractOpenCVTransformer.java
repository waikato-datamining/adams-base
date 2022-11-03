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
 * AbstractOpenCVTransformer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.transformer;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageTransformer;
import adams.data.opencv.OpenCVImageContainer;

/**
 * Abstract base class for OpenCV transformations.
 *
 * Derived classes only have to override the <code>doTransform(BufferedImage)</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOpenCVTransformer
  extends AbstractImageTransformer<OpenCVImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6509685876509009633L;

  /**
   * Returns a list with classnames of transformations.
   *
   * @return		the transformation classnames
   */
  public static String[] getTransformations() {
    return ClassLister.getSingleton().getClassnames(AbstractOpenCVTransformer.class);
  }

  /**
   * Instantiates the transformation with the given options.
   *
   * @param classname	the classname of the transformation to instantiate
   * @param options	the options for the transformation
   * @return		the instantiated transformation or null if an error occurred
   */
  public static AbstractOpenCVTransformer forName(String classname, String[] options) {
    AbstractOpenCVTransformer result;

    try {
      result = (AbstractOpenCVTransformer) OptionUtils.forName(AbstractOpenCVTransformer.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the transformation from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			transformation to instantiate
   * @return		the instantiated transformation
   * 			or null if an error occurred
   */
  public static AbstractOpenCVTransformer forCommandLine(String cmdline) {
    return (AbstractOpenCVTransformer) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
