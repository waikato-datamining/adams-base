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
 * SetMapValue.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Sets a value in a java.util.Map object.<br>
 * The value can be either supplied as string using the 'value' property or obtained from a callable actor (property 'source').
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
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
 * &nbsp;&nbsp;&nbsp;default: SetMapValue
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The key of the value to set.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-source &lt;adams.flow.core.CallableActorReference&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The callable source to obtain the value from.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetMapValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5937471470417243026L;

  /** the key to set. */
  protected String m_Key;

  /** the value to set. */
  protected String m_Value;

  /** the callable source to obtain the source from. */
  protected CallableActorReference m_Source;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable source actor. */
  protected Actor m_SourceActor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Sets a value in a " + Map.class.getName() + " object.\n"
      + "The value can be either supplied as string using the 'value' property "
      + "or obtained from a callable actor (property 'source').";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "");

    m_OptionManager.add(
      "value", "value",
      "");

    m_OptionManager.add(
      "source", "source",
      new CallableActorReference("unknown"));
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_SourceActor = null;
  }

  /**
   * Sets the key of the value to set.
   *
   * @param value 	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key of the value to set.
   *
   * @return 		the key
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The key of the value to set.";
  }

  /**
   * Sets the value to set.
   *
   * @param value 	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to set.
   *
   * @return 		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value to set.";
  }

  /**
   * Sets the callable source to obtain the value from.
   *
   * @param value	the reference
   */
  public void setSource(CallableActorReference value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the callable source to obtain the value from.
   *
   * @return		the reference
   */
  public CallableActorReference getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The callable source to obtain the value from.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.util.Map.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Map.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.util.Map.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Map.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = null;
    
    value  = QuickInfoHelper.toString(this, "key", m_Key);
    if (value != null) {
      result  = value;
      result += " = ";
      result += QuickInfoHelper.toString(this, "value", (m_Value.isEmpty() ? "-none-" : m_Value));
      result += " or from ";
      result += QuickInfoHelper.toString(this, "source", m_Source);
    }
	
    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getSource());
  }

  @Override
  public String setUp() {
    String		result;
    Actor		source;

    result = super.setUp();

    if (result == null) {
      source  = findCallableActor();
      if (source != null) {
	if (source instanceof OutputProducer)
	  m_SourceActor = source;
	else
	  result = "Callable actor '" + m_Source + "' does not produce any output!";
      }
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
    Map 	map;
    Token	token;

    result = null;

    map = (Map) m_InputToken.getPayload();
    if (m_SourceActor == null) {
      map.put(m_Key, m_Value);
    }
    else {
      token  = null;
      result = m_SourceActor.execute();
      if (result != null) {
	result = "Callable actor '" + m_Source + "' execution failed:\n" + result;
      }
      else {
	if (((OutputProducer) m_SourceActor).hasPendingOutput())
	  token = ((OutputProducer) m_SourceActor).output();
	else
	  result = "Callable actor '" + m_Source + "' did not generate any output!";
      }
      if (token != null)
	map.put(m_Key, token.getPayload());
    }
    
    m_OutputToken = new Token(map);
    
    return result;
  }
}
