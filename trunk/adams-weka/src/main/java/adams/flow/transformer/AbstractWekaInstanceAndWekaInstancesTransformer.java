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
 * AbstractWekaInstanceAndWekaInstancesTransformer.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.transformer.AbstractTransformer;

/**
 * Transformer that processes weka.core.Instance, weka.core.Instances
 * or adams.data.instance.Instance objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaInstanceAndWekaInstancesTransformer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1777730597434025843L;

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] accepts() {
    return new Class[]{weka.core.Instance.class, weka.core.Instances.class, adams.data.instance.Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] generates() {
    return new Class[]{weka.core.Instance.class, weka.core.Instances.class, adams.data.instance.Instance.class};
  }
}
