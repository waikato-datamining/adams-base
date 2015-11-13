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
 * WekaSetInstanceValue.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.weka.WekaAttributeIndex;
import adams.flow.core.Token;
import weka.core.Attribute;
import weka.core.Instance;

/**
 <!-- globalinfo-start -->
 * Sets a value in a WEKA Instance.<br>
 * Notes:<br>
 * - relational values cannot be set<br>
 * - '?' (without single quotes) is interpreted as missing value
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
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
 * &nbsp;&nbsp;&nbsp;default: SetInstanceValue
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
 * <pre>-index &lt;java.lang.String&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The 1-based index to set the value for in the WEKA Instance.
 * &nbsp;&nbsp;&nbsp;default: last
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set in the WEKA Instance.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaSetInstanceValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4710366291340930250L;

  /** the attribute index to set in the Instance. */
  protected WekaAttributeIndex m_Index;

  /** the value to set. */
  protected String m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Sets a value in a WEKA Instance.\n"
      + "Notes:\n"
      + "- relational values cannot be set\n"
      + "- '?' (without single quotes) is interpreted as missing value";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "index",
	    new WekaAttributeIndex(WekaAttributeIndex.LAST));

    m_OptionManager.add(
	    "value", "value",
	    "?");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Index = new WekaAttributeIndex("last");
  }

  /**
   * Sets the 1-based attribute index to set in the Instance.
   *
   * @param value	the 1-based index
   */
  public void setIndex(WekaAttributeIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the 1-based attribute index to set in the Instance.
   *
   * @return		the field
   */
  public WekaAttributeIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The 1-based index to set the value for in the WEKA Instance.";
  }

  /**
   * Sets the value to set in the report.
   *
   * @param value	the value to set
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to set in the report.
   *
   * @return		the value to set
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
    return "The value to set in the WEKA Instance.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "index", m_Index);
    result += QuickInfoHelper.toString(this, "value", m_Value, " -> ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
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
    Instance	inst;
    int		index;

    result = null;

    inst = (Instance) m_InputToken.getPayload();
    inst = (Instance) inst.copy();
    m_Index.setData(inst.dataset());
    index = m_Index.getIntIndex();

    try {
      if (m_Value.equals("?")) {
	inst.setMissing(index);
      }
      else {
	switch (inst.attribute(index).type()) {
	  case Attribute.NUMERIC:
	    inst.setValue(index, Utils.toDouble(m_Value));
	    break;

	  case Attribute.DATE:
	    inst.setValue(index, inst.attribute(index).parseDate(m_Value));
	    break;

	  case Attribute.NOMINAL:
	  case Attribute.STRING:
	    inst.setValue(index, m_Value);
	    break;

	  case Attribute.RELATIONAL:
	    result = "Relational attributes cannot be set!";
	    break;

	  default:
	    result = "Unhandled attribute type: " + inst.attribute(index).type();
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to set value: " + m_Index.getIndex() + " -> " + m_Value, e);
    }

    // broadcast data
    if (result == null)
      m_OutputToken = new Token(inst);

    return result;
  }
}
