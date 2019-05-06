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
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.conversion.Conversion;
import adams.data.conversion.ObjectToObject;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Sets a value in a java.util.Map object.<br>
 * The value can be either supplied as string using the 'value' property, obtained from a callable actor (property 'source') or from a storage item (property 'storage').
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
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The key of the value to set.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type &lt;VALUE|SOURCE|STORAGE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;Determines how to obtain the value to store in the map.
 * &nbsp;&nbsp;&nbsp;default: VALUE
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
 * <pre>-storage &lt;adams.flow.control.StorageName&gt; (property: storage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the value from.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-conversion &lt;adams.data.conversion.Conversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The type of conversion to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.ObjectToObject
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SetMapValue
  extends AbstractTransformer
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = -5937471470417243026L;

  /** the source of the other report. */
  public enum SourceType {
    VALUE,
    SOURCE,
    STORAGE
  }

  /** the key to set. */
  protected String m_Key;

  /** the value to set. */
  protected String m_Value;

  /** the source type. */
  protected SourceType m_Type;

  /** the callable source to obtain the source from. */
  protected CallableActorReference m_Source;

  /** the storage item. */
  protected StorageName m_Storage;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable source actor. */
  protected Actor m_SourceActor;

  /** for processing the value. */
  protected Conversion m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Sets a value in a " + Map.class.getName() + " object.\n"
        + "The value can be either supplied as string using the 'value' property, "
        + "obtained from a callable actor (property 'source') or from a storage "
	+ "item (property 'storage').";
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
      "type", "type",
      SourceType.VALUE);

    m_OptionManager.add(
      "value", "value",
      "");

    m_OptionManager.add(
      "source", "source",
      new CallableActorReference(CallableActorReference.UNKNOWN));

    m_OptionManager.add(
      "storage", "storage",
      new StorageName());

    m_OptionManager.add(
      "conversion", "conversion",
      new ObjectToObject());
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
   * Sets the type of source.
   *
   * @param value	the type
   */
  public void setType(SourceType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of source.
   *
   * @return		the type
   */
  public SourceType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "Determines how to obtain the value to store in the map.";
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
   * Sets the value storage item.
   *
   * @param value	the storage item
   */
  public void setStorage(StorageName value) {
    m_Storage = value;
    reset();
  }

  /**
   * Returns the value storage item.
   *
   * @return		the storage item
   */
  public StorageName getStorage() {
    return m_Storage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageTipText() {
    return "The storage item to obtain the value from.";
  }

  /**
   * Sets the type of conversion to perform.
   *
   * @param value	the type of conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
    m_Conversion.setOwner(this);
    reset();
  }

  /**
   * Returns the type of conversion to perform.
   *
   * @return		the type of conversion
   */
  public Conversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The type of conversion to perform.";
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

    result  = QuickInfoHelper.toString(this, "key", (m_Key.isEmpty() ? "-nokey-" : m_Key));
    result += " = ";
    switch (m_Type) {
      case VALUE:
	result += QuickInfoHelper.toString(this, "value", (m_Value.isEmpty() ? "-none-" : m_Value));
	break;
      case SOURCE:
	result += QuickInfoHelper.toString(this, "source", m_Source + " (source)");
	break;
      case STORAGE:
	result += QuickInfoHelper.toString(this, "storage", m_Storage + " (storage)");
	break;
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");

    return result;
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return (m_Type == SourceType.STORAGE);
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
      m_SourceActor = null;
      if (m_Type == SourceType.SOURCE) {
	source = findCallableActor();
	if (source != null) {
	  if (source instanceof OutputProducer)
	    m_SourceActor = source;
	  else
	    result = "Callable actor '" + m_Source + "' does not produce any output!";
	}
	else {
	  result = "Failed to locate source actor: " + m_Source;
	}
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
    Object	value;
    Map 	map;
    Token	token;

    result = null;

    map   = (Map) m_InputToken.getPayload();
    value = null;
    if (map == null) {
      result = "Null token instead of map received at input!";
    }
    else {
      switch (m_Type) {
	case VALUE:
	  value = m_Value;
	  break;

	case SOURCE:
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
	    value = token.getPayload();
	  break;

	case STORAGE:
	  if (getStorageHandler().getStorage().has(m_Storage))
	    value = getStorageHandler().getStorage().get(m_Storage);
	  else
	    result = "Storage item not found: " + m_Storage;
	  break;

	default:
	  throw new IllegalStateException("Unhandled type: " + m_Type);
      }

      if (value != null) {
	m_Conversion.setInput(value);
	result = m_Conversion.convert();
	if (result == null) {
	  value = m_Conversion.getOutput();
	  map.put(m_Key, value);
	}
	m_Conversion.cleanUp();
      }

      m_OutputToken = new Token(map);
    }

    return result;
  }
}
