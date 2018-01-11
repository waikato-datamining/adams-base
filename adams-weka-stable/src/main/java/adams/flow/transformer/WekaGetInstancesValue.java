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
 * WekaGetInstancesValue.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Attribute;
import weka.core.Instances;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.weka.WekaAttributeIndex;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Retrieves a value from a WEKA Instances object.<br>
 * Notes:<br>
 * - date and relational values are forwarded as strings<br>
 * - missing values are output as '?' (without the single quotes)<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaGetInstancesValue
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
 * &nbsp;&nbsp;&nbsp;The column to set get the value from.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6830 $
 */
public class WekaGetInstancesValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3057815118900209749L;

  /** the index of the row. */
  protected Index m_Row;

  /** the column index. */
  protected WekaAttributeIndex m_Column;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Retrieves a value from a WEKA Instances object.\n"
      + "Notes:\n"
      + "- date and relational values are forwarded as strings\n"
      + "- missing values are output as '?' (without the single quotes)\n";
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
	    new WekaAttributeIndex(WekaAttributeIndex.FIRST));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Row    = new Index("first");
    m_Column = new WekaAttributeIndex("first");
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
   * Sets the column.
   *
   * @param value	the index
   */
  public void setColumn(WekaAttributeIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column.
   *
   * @return		the index
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
    return "The column to set get the value from.";
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
   * @return		<!-- flow-generates-start -->java.lang.Double.class, java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Double.class, String.class};
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
    int		index;
    int		row;

    result = null;

    inst = (Instances) m_InputToken.getPayload();
    m_Column.setData(inst);
    m_Row.setMax(inst.numInstances());
    index = m_Column.getIntIndex();
    row = m_Row.getIntIndex();
    
    if (row == -1)
      result = "Failed to retrieve row: " + m_Row.getIndex();
    else if (index == -1)
      result = "Failed to retrieve column: " + m_Column.getIndex();

    if (result == null) {
      try {
	if (inst.instance(row).isMissing(index)) {
	  m_OutputToken = new Token("?");
	}
	else {
	  switch (inst.attribute(index).type()) {
	    case Attribute.NUMERIC:
	      m_OutputToken = new Token(inst.instance(row).value(index));
	      break;

	    case Attribute.DATE:
	    case Attribute.NOMINAL:
	    case Attribute.STRING:
	    case Attribute.RELATIONAL:
	      m_OutputToken = new Token(inst.instance(row).stringValue(index));
	      break;

	    default:
	      result = "Unhandled attribute type: " + inst.attribute(index).type();
	  }
	}
      }
      catch (Exception e) {
	result = handleException("Failed to obtain value from dataset:", e);
      }
    }

    return result;
  }
}
