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

/**
 * Cast.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.flow.core.ControlActor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Casts the incoming data to the specified classname. For arrays, use '[]' at the end (can be used multiple times).<br>
 * By using adams.flow.core.Unknown (default) you should be able to avoid specifying the actual classname
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Cast
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-classname &lt;adams.core.base.BaseClassname&gt; (property: classname)
 * &nbsp;&nbsp;&nbsp;The classname to cast the incoming data to.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.Unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Cast
  extends AbstractTransformer
  implements ControlActor {

  /** for serialization. */
  private static final long serialVersionUID = -6615127883045169960L;

  /** the Class to cast to. */
  protected BaseClassname m_Classname;

  /** the actual class to cast to. */
  protected Class m_ActualClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Casts the incoming data to the specified classname. For arrays, "
	+ "use '" + Utils.ARRAY_INDICATOR + "' at the end (can be used multiple times).\n"
	+ "By using " + Unknown.class.getName() + " (default) you should be able to avoid specifying the actual classname";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classname", "classname",
      new BaseClassname(Unknown.class.getName()));
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualClass = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classname", m_Classname);
  }

  /**
   * Sets the classname.
   *
   * @param value	the classname
   */
  public void setClassname(BaseClassname value) {
    m_Classname = value;
    reset();
  }

  /**
   * Returns the classname.
   *
   * @return		the classname
   */
  public BaseClassname getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The classname to cast the incoming data to.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    Class	cls;

    cls = getClassname().classValue();

    if (cls == null)
      return new Class[]{Unknown.class};
    else
      return new Class[]{cls};
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_ActualClass = getClassname().classValue();
      if (m_ActualClass == null)
	result = "Failed to instantiate class '" + getClassname() + "'!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      if (m_ActualClass == Unknown.class)
        m_OutputToken = new Token(m_InputToken.getPayload());
      else
        m_OutputToken = new Token(m_ActualClass.cast(m_InputToken.getPayload()));
      m_OutputToken.setProvenance(m_InputToken.getProvenance());
    }
    catch (Exception e) {
      result = handleException("Failed to cast to '" + getClassname() + "': ", e);
    }

    return result;
  }
}
