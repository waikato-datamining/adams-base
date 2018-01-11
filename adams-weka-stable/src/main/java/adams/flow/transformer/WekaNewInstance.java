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
 * WekaNewInstance.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.lang.reflect.Constructor;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Creates a new weka.core.Instance-derived object, with all values marked as missing.<br>
 * The class implementing the weka.core.Instance interface needs to have a constructor that takes the number of attributes as sole parameter.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaNewInstance
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-instance-class &lt;java.lang.String&gt; (property: instanceClass)
 * &nbsp;&nbsp;&nbsp;The full class name of the weka.core.Instance-derived object to instantiate.
 * &nbsp;&nbsp;&nbsp;default: weka.core.DenseInstance
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaNewInstance
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -786486914801535807L;

  /** the class of instance to create. */
  protected String m_InstanceClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Creates a new weka.core.Instance-derived object, with all values "
      + "marked as missing.\n"
      + "The class implementing the weka.core.Instance interface needs to "
      + "have a constructor that takes the number of attributes as sole "
      + "parameter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "instance-class", "instanceClass",
	    DenseInstance.class.getName());
  }

  /**
   * Sets the class name of the Instance object to create.
   *
   * @param value	the class
   */
  public void setInstanceClass(String value) {
    try {
      Class.forName(value);
      m_InstanceClass = value;
      reset();
    }
    catch (Exception e) {
      handleException("Failed to instantiate class '" + value + "' - ignored!", e);
    }
  }

  /**
   * Returns the the class name of the Instance object to create.
   *
   * @return		the class
   */
  public String getInstanceClass() {
    return m_InstanceClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String instanceClassTipText() {
    return "The full class name of the weka.core.Instance-derived object to instantiate.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "instanceClass", m_InstanceClass);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instance.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instance.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instances	data;
    Instance	inst;
    Class	cls;
    Constructor	constr;

    result = null;

    data = (Instances) m_InputToken.getPayload();

    try {
      cls    = Class.forName(m_InstanceClass);
      constr = cls.getConstructor(new Class[]{Integer.TYPE});
      inst   = (Instance) constr.newInstance(new Object[]{data.numAttributes()});
      inst.setDataset(data);
      m_OutputToken = new Token(inst);
    }
    catch (Exception e) {
      result = handleException("Failed to create new instance: ", e);
    }

    return result;
  }
}
