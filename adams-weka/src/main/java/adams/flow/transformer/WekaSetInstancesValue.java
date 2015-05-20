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
 * WekaSetInstancesValue.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Attribute;
import weka.core.Instances;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.weka.WekaAttributeIndex;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Sets a value in a WEKA Instances object.<br>
 * Notes:<br>
 * - relational values cannot be set<br>
 * - '?' (without single quotes) is interpreted as missing value
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaSetInstancesValue
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
 * <pre>-row &lt;adams.core.Index&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The 1-based index of the row.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column &lt;adams.data.weka.WekaAttributeIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to set the value in.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set in the dataset.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6830 $
 */
public class WekaSetInstancesValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4710366291340930250L;

  /** the index of the row. */
  protected Index m_Row;

  /** the column to update. */
  protected WekaAttributeIndex m_Column;

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
        "Sets a value in a WEKA Instances object.\n"
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
	    "row", "row",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "column", "column",
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

    m_Row    = new Index("first");
    m_Column = new WekaAttributeIndex("last");
  }

  /**
   * Sets the 1-based index of the row.
   *
   * @param value	the 1-based index
   */
  public void setRow(Index value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the 1-based index of the row.
   *
   * @return		the 1-based index
   */
  public Index getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The 1-based index of the row.";
  }

  /**
   * Sets the column index.
   *
   * @param value	the index
   */
  public void setColumn(WekaAttributeIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column index.
   *
   * @return		the field
   */
  public WekaAttributeIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column to set the value in.";
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
    return "The value to set in the dataset.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "column", m_Column, "col: ");
    result += QuickInfoHelper.toString(this, "row", m_Row, ", row: ");
    result += QuickInfoHelper.toString(this, "value", m_Value, " -> ");

    return result;
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
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instances	inst;
    int		row;
    int		index;

    result = null;

    inst = (Instances) m_InputToken.getPayload();
    inst = new Instances(inst);
    m_Row.setMax(inst.numInstances());
    m_Column.setData(inst);
    row   = m_Row.getIntIndex();
    index = m_Column.getIntIndex();

    if (row == -1)
      result = "Failed to retrieve row: " + m_Row.getIndex();
    else if (index == -1)
      result = "Failed to retrieve column: " + m_Column.getIndex();

    if (result == null) {
      try {
	if (m_Value.equals("?")) {
	  inst.instance(row).setMissing(index);
	}
	else {
	  switch (inst.attribute(index).type()) {
	    case Attribute.NUMERIC:
	      inst.instance(row).setValue(index, Utils.toDouble(m_Value));
	      break;

	    case Attribute.DATE:
	      inst.instance(row).setValue(index, inst.attribute(index).parseDate(m_Value));
	      break;

	    case Attribute.NOMINAL:
	    case Attribute.STRING:
	      inst.instance(row).setValue(index, m_Value);
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
	result = handleException("Failed to set value: " + m_Column.getIndex() + " -> " + m_Value, e);
      }
    }

    // broadcast data
    if (result == null)
      m_OutputToken = new Token(inst);

    return result;
  }
}
