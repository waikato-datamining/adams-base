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
 * SetArrayElement.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.lang.reflect.Array;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Sets an element of an array and forwards the updated array.
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
 * &nbsp;&nbsp;&nbsp;default: SetArrayElement
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
 * <pre>-index &lt;adams.core.Index&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the element to set; An index is a number starting with 1; the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used as well: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp; last_1, last
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-origin &lt;VALUE|STORAGE&gt; (property: origin)
 * &nbsp;&nbsp;&nbsp;Whether to use the provided 'value' or get the data from storage.
 * &nbsp;&nbsp;&nbsp;default: VALUE
 * </pre>
 * 
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set in the array (if origin is VALUE).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the stored value to use (if origin is STORAGE).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetArrayElement
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5436016357221787534L;

  /** whether to use the provided value or a storage item. */
  public enum Origin {
    /** provided value. */
    VALUE,
    /** from storage. */
    STORAGE
  }
  
  /** the index of the element to set. */
  protected Index m_Index;
  
  /** where to get the array element from. */
  protected Origin m_Origin;
  
  /** the value of the element to set. */
  protected String m_Value;
  
  /** the storage name. */
  protected StorageName m_StorageName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets an element of an array and forwards the updated array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "index",
	    new Index("1"));

    m_OptionManager.add(
	    "origin", "origin",
	    Origin.VALUE);

    m_OptionManager.add(
	    "value", "value",
	    "");

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Index = new Index();
  }

  /**
   * Sets the index (1-based).
   *
   * @param value	the index
   */
  public void setIndex(Index value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index (1-based).
   *
   * @return		the index
   */
  public Index getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the element to set.";
  }

  /**
   * Sets whether to use the provided value or the value from storage.
   *
   * @param value	the origin
   */
  public void setOrigin(Origin value) {
    m_Origin = value;
    reset();
  }

  /**
   * Returns whether to use the provided value or the value from storage.
   *
   * @return		the origin
   */
  public Origin getOrigin() {
    return m_Origin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String originTipText() {
    return "Whether to use the provided 'value' or get the data from storage.";
  }

  /**
   * Sets the value to set in the array.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to set in the array.
   *
   * @return		the value
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
    return "The value to set in the array (if origin is " + Origin.VALUE + ").";
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
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
    return "The name of the stored value to use (if origin is " + Origin.STORAGE + ").";
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

    result = QuickInfoHelper.toString(this, "index", m_Index, "Index = ");

    variable = QuickInfoHelper.getVariable(this, "origin");
    if (variable != null) {
      result += ", origin = " + variable;
    }
    else {
      switch (m_Origin) {
	case VALUE:
	  result += QuickInfoHelper.toString(this, "value", "\"" + m_Value + "\"", ", Value = ");
	  break;
	case STORAGE:
	  result += QuickInfoHelper.toString(this, "storageName", m_StorageName, ", Storage = ");
	  break;
	default:
	  throw new IllegalStateException("Unhandled origin: " + m_Origin);
      }
    }
    
    return result;
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
    Object	array;

    result = null;

    try {
      array = m_InputToken.getPayload();
      m_Index.setMax(Array.getLength(array));
      switch (m_Origin) {
	case VALUE:
	  Array.set(array, m_Index.getIntIndex(), OptionUtils.valueOf(array.getClass().getComponentType(), m_Value));
	  break;
	case STORAGE:
	  Array.set(array, m_Index.getIntIndex(), getStorageHandler().getStorage().get(m_StorageName));
	  break;
	default:
	  throw new IllegalStateException("Unhandled origin: " + m_Origin);
      }
      m_OutputToken = new Token(array);
    }
    catch (Exception e) {
      result = handleException("Failed to set array element: " + m_Index.getIndex(), e);
    }

    return result;
  }
}
