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
 * AbstractJAITransformer.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import adams.core.ClassLister;
import adams.core.JAIHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageTransformer;
import adams.data.image.BufferedImageContainer;

/**
 * Abstract base class for JAI transformations.
 *
 * Derived classes only have to override the <code>doTransform(BufferedImage)</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractJAITransformer
  extends AbstractImageTransformer<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6509685876509009633L;

  static {
    JAIHelper.disableMediaLib();
  }

  /**
   * Returns a list with classnames of transformations.
   *
   * @return		the transformation classnames
   */
  public static String[] getTransformations() {
    return ClassLister.getSingleton().getClassnames(AbstractJAITransformer.class);
  }

  /**
   * Instantiates the transformation with the given options.
   *
   * @param classname	the classname of the transformation to instantiate
   * @param options	the options for the transformation
   * @return		the instantiated transformation or null if an error occurred
   */
  public static AbstractJAITransformer forName(String classname, String[] options) {
    AbstractJAITransformer	result;

    try {
      result = (AbstractJAITransformer) OptionUtils.forName(AbstractJAITransformer.class, classname, options);
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
  public static AbstractJAITransformer forCommandLine(String cmdline) {
    return (AbstractJAITransformer) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
