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
 * WekaInstanceFileReader.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.instance.Instance;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.InstanceReader;

/**
 <!-- globalinfo-start -->
 * Loads a WEKA dataset from disk with a specified reader and passes on the adams.core.instance.Instance objects.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: InstanceFileReader
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.AbstractDataContainerReader [options]&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for importing the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.InstanceReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceFileReader
  extends AbstractDataContainerFileReader<Instance> {

  /** for serialization. */
  private static final long serialVersionUID = -8968191728988750040L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Loads a WEKA dataset from disk with a specified reader and passes on "
      + "the adams.core.instance.Instance objects.";
  }

  /**
   * Returns the default reader to use.
   *
   * @return		the default reader
   */
  protected AbstractDataContainerReader getDefaultReader() {
    return new InstanceReader();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the data type
   */
  public Class[] generates() {
    return new Class[]{Instance.class};
  }
}
