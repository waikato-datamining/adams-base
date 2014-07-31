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
 * AbstractBufferedImageFeatureGenerator.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.adams.features;

import adams.core.ClassLister;
import adams.core.JAIHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageFeatureGenerator;
import adams.data.image.BufferedImageContainer;

/**
 * Abstract base class for BufferedImage feature generators.
 *
 * Derived classes only have to override the <code>doProcess(BufferedImage)</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBufferedImageFeatureGenerator
  extends AbstractImageFeatureGenerator<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 4566948525813804085L;

  static {
    JAIHelper.disableMediaLib();
  }

  /**
   * Returns a list with classnames of transformations.
   *
   * @return		the transformation classnames
   */
  public static String[] getFlatteners() {
    return ClassLister.getSingleton().getClassnames(AbstractBufferedImageFeatureGenerator.class);
  }

  /**
   * Instantiates the transformation with the given options.
   *
   * @param classname	the classname of the transformation to instantiate
   * @param options	the options for the transformation
   * @return		the instantiated transformation or null if an error occurred
   */
  public static AbstractBufferedImageFeatureGenerator forName(String classname, String[] options) {
    AbstractBufferedImageFeatureGenerator	result;

    try {
      result = (AbstractBufferedImageFeatureGenerator) OptionUtils.forName(AbstractBufferedImageFeatureGenerator.class, classname, options);
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
  public static AbstractBufferedImageFeatureGenerator forCommandLine(String cmdline) {
    return (AbstractBufferedImageFeatureGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
