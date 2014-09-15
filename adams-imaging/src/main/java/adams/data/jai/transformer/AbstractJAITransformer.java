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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import adams.core.JAIHelper;
import adams.data.base.transformer.AbstractBufferedImageTransformer;

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
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6509685876509009633L;

  static {
    JAIHelper.disableMediaLib();
  }
}
