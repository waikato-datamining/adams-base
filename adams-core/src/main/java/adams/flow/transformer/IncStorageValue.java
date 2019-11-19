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
 * IncStorageValue.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Increments the value of a storage value by either an integer or double increment.<br>
 * If the storage value has not been set yet, it will get set to 0.<br>
 * If the storage value contains a non-numerical value, no increment will be performed.<br>
 * It is also possible to directly output the updated storage value (while discarding the input token).
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
 * &nbsp;&nbsp;&nbsp;default: IncStorageValue
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage value to increment.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-inc-type &lt;INTEGER|DOUBLE&gt; (property: incrementType)
 * &nbsp;&nbsp;&nbsp;The type of increment to perform.
 * &nbsp;&nbsp;&nbsp;default: INTEGER
 * </pre>
 * 
 * <pre>-inc-int &lt;int&gt; (property: integerIncrement)
 * &nbsp;&nbsp;&nbsp;The increment in case of INTEGER increments.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-inc-double &lt;double&gt; (property: doubleIncrement)
 * &nbsp;&nbsp;&nbsp;The increment in case of DOUBLE increments.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 * <pre>-output-storage-value &lt;boolean&gt; (property: outputStorageValue)
 * &nbsp;&nbsp;&nbsp;If enabled, the input token gets discarded and the current storage value 
 * &nbsp;&nbsp;&nbsp;get forwarded instead.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class IncStorageValue
  extends AbstractTransformer 
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 4563837784851442207L;

  /**
   * The type of increment to perform.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum IncrementType {
    /** integer increment. */
    INTEGER,
    /** long increment. */
    LONG,
    /** floating point increment. */
    DOUBLE
  }

  /** the name of the storage value. */
  protected StorageName m_StorageName;

  /** the type of increment to perform. */
  protected IncrementType m_IncrementType;

  /** the integer increment. */
  protected int m_IntegerIncrement;

  /** the double increment. */
  protected double m_DoubleIncrement;

  /** whether to output the incremented storage value instead of input token. */
  protected boolean m_OutputStorageValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Increments the value of a storage value by either an integer/long or double "
      + "increment.\n"
      + "If the storage value has not been set yet, it will get set to 0.\n"
      + "If the storage value contains a non-numerical value, no increment will be "
      + "performed.\n"
      + "It is also possible to directly output the updated storage value (while "
      + "discarding the input token).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());

    m_OptionManager.add(
	    "inc-type", "incrementType",
	    IncrementType.INTEGER);

    m_OptionManager.add(
	    "inc-int", "integerIncrement",
	    1);

    m_OptionManager.add(
	    "inc-double", "doubleIncrement",
	    1.0);

    m_OptionManager.add(
	    "output-storage-value", "outputStorageValue",
	    false);
  }

  /**
   * Returns whether storage items are being updated.
   * 
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName);

    variable = QuickInfoHelper.getVariable(this, "incrementType");
    result += QuickInfoHelper.toString(this, "incrementType", m_IncrementType, ", ");
    if (variable == null) {
      result += ", inc: ";
      switch (m_IncrementType) {
	case INTEGER:
	case LONG:
	  result += QuickInfoHelper.toString(this, "integerIncrement", m_IntegerIncrement);
	  break;
	case DOUBLE:
	  result += QuickInfoHelper.toString(this, "doubleIncrement", m_DoubleIncrement);
	  break;
	default:
	  throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
      }
    }
    result += QuickInfoHelper.toString(this, "outputStorageValue", m_OutputStorageValue, "output storage", ", ");

    return result;
  }

  /**
   * Sets the name of the variable to update.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the variable to update.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage value to increment.";
  }

  /**
   * Sets the type of increment to perform.
   *
   * @param value	the type
   */
  public void setIncrementType(IncrementType value) {
    m_IncrementType = value;
    reset();
  }

  /**
   * Returns the type of increment to perform.
   *
   * @return		the type
   */
  public IncrementType getIncrementType() {
    return m_IncrementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String incrementTypeTipText() {
    return "The type of increment to perform.";
  }

  /**
   * Sets the increment value for integer/long increments.
   *
   * @param value	the increment
   */
  public void setIntegerIncrement(int value) {
    m_IntegerIncrement = value;
    reset();
  }

  /**
   * Returns the increment value for integer/long increments.
   *
   * @return		the increment
   */
  public int getIntegerIncrement() {
    return m_IntegerIncrement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String integerIncrementTipText() {
    return "The increment in case of " + IncrementType.INTEGER + " or " + IncrementType.LONG + " increments.";
  }

  /**
   * Sets the increment value for double increments.
   *
   * @param value	the increment
   */
  public void setDoubleIncrement(double value) {
    m_DoubleIncrement = value;
    reset();
  }

  /**
   * Returns the increment value for double increments.
   *
   * @return		the increment
   */
  public double getDoubleIncrement() {
    return m_DoubleIncrement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String doubleIncrementTipText() {
    return "The increment in case of " + IncrementType.DOUBLE + " increments.";
  }

  /**
   * Sets whether to output the storage value instead of the input token.
   *
   * @param value	true if to output the storage value
   */
  public void setOutputStorageValue(boolean value) {
    m_OutputStorageValue = value;
    reset();
  }

  /**
   * Returns whether to output the storage value instead of the input token.
   *
   * @return		true if to output the storage value
   */
  public boolean getOutputStorageValue() {
    return m_OutputStorageValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputStorageValueTipText() {
    return "If enabled, the input token gets discarded and the current storage value get forwarded instead.";
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Number	value;

    result = getOptionManager().ensureVariableForPropertyExists("storageName");

    if (result == null) {
      try {
	if (getStorageHandler().getStorage().has(m_StorageName)) {
	  switch (m_IncrementType) {
	    case INTEGER:
	      value = (Integer) getStorageHandler().getStorage().get(m_StorageName);
	      break;
	    case LONG:
	      value = (Long) getStorageHandler().getStorage().get(m_StorageName);
	      break;
	    case DOUBLE:
	      value = (Double) getStorageHandler().getStorage().get(m_StorageName);
	      break;
	    default:
	      throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
	  }
	}
	else {
	  switch (m_IncrementType) {
	    case INTEGER:
	      value = new Integer(0);
	      break;
	    case LONG:
	      value = new Long(0L);
	      break;
	    case DOUBLE:
	      value = new Double(0.0);
	      break;
	    default:
	      throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
	  }
	}
      }
      catch (Exception e) {
	value = null;
      }

      if (value != null) {
	switch (m_IncrementType) {
	  case INTEGER:
	    value = new Integer(value.intValue() + m_IntegerIncrement);
	    break;
	  case LONG:
	    value = new Long(value.longValue() + m_IntegerIncrement);
	    break;
	  case DOUBLE:
	    value = new Double(value.doubleValue() + m_DoubleIncrement);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
	}
	getStorageHandler().getStorage().put(m_StorageName, value);
	if (isLoggingEnabled())
	  getLogger().info("Incremented storage '" + m_StorageName + "': " + value);
	m_OutputToken = new Token(value);
      }

      if (!m_OutputStorageValue)
	m_OutputToken = m_InputToken;
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_OutputStorageValue) {
      switch (m_IncrementType) {
	case INTEGER:
	  return new Class[]{Integer.class};
	case LONG:
	  return new Class[]{Long.class};
	case DOUBLE:
	  return new Class[]{Double.class};
	default:
	  throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
      }
    }
    else {
      return new Class[]{Unknown.class};
    }
  }
}
