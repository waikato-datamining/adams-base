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
 * Copy.java
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.CloneHandler;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.io.Serializable;

/**
 <!-- globalinfo-start -->
 * Creates copies of the tokens being passed through. In auto-mode, if the payload of the token implements java.io.Serializable, then a deep copy is created. If the payload implements the adams.core.CloneHandler interface, then this approach is used to create a copy of the payload. In all other cases, the payload is just forwarded.
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
 * &nbsp;&nbsp;&nbsp;default: Copy
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type &lt;AUTO|CLONEHANDLER|SERIALIZATION|SHALLOWCOPY&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of copy to perform.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Copy
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -735652783986676809L;

  /**
   * The type of copy to perform.
   */
  public enum CopyType {
    AUTO,
    CLONEHANDLER,
    SERIALIZATION,
    SHALLOWCOPY,
  }

  /** the type of copy to perform. */
  protected CopyType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Creates copies of the tokens being passed through. In auto-mode, if the payload "
      + "of the token implements " + Serializable.class.getName() + ", then a "
      + "deep copy is created. If the payload implements the "
      + CloneHandler.class.getName() + " interface, then this approach is used "
      + "to create a copy of the payload. In all other cases, the payload is "
      + "just forwarded.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      CopyType.AUTO);
  }

  /**
   * Sets the type of copy to perform.
   *
   * @param value	the type
   */
  public void setType(CopyType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of copy to perform.
   *
   * @return 		the type
   */
  public CopyType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of copy to perform.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type, "type: ");
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
    return new Class[]{Unknown.class};
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
      switch (m_Type) {
	case AUTO:
	  m_OutputToken = m_InputToken.getClone();
	  break;
	case CLONEHANDLER:
	  m_OutputToken = new Token(ObjectCopyHelper.copyObject(ObjectCopyHelper.CopyType.CLONEHANDLER, m_InputToken.getPayload()));
	  if (m_OutputToken.hasProvenance())
	    m_OutputToken.setProvenance(m_OutputToken.getProvenance().getClone());
	  break;
	case SERIALIZATION:
	  m_OutputToken = new Token(ObjectCopyHelper.copyObject(ObjectCopyHelper.CopyType.SERIALIZABLE, m_InputToken.getPayload()));
	  if (m_OutputToken.hasProvenance())
	    m_OutputToken.setProvenance(m_OutputToken.getProvenance().getClone());
	  break;
	case SHALLOWCOPY:
	  m_OutputToken = new Token(OptionUtils.shallowCopy(m_InputToken.getPayload()));
	  if (m_OutputToken.hasProvenance())
	    m_OutputToken.setProvenance(m_OutputToken.getProvenance().getClone());
	  break;
	default:
	  throw new IllegalStateException("Unhandled copy type: " + m_Type);
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to copy object:", e);
    }

    return result;
  }
}
