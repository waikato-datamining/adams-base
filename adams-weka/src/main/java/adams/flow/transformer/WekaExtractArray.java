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
 * WekaExtractArray.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import weka.core.Instances;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Extracts a column or row of data from a weka.core.Instances or SpreadSheet object.<br>
 * Only numeric columns can be returned. In case of row-retrieval, the value of the internal format of the weka.core.Instance object is returned; for SpreadSheet object it is attempted to convert the cell content to double (null values might get returned!).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;adams.core.io.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: ExtractArray
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
 * <pre>-type &lt;COLUMN|ROW&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of extraction to perform, row or column.
 * &nbsp;&nbsp;&nbsp;default: COLUMN
 * </pre>
 *
 * <pre>-index &lt;java.lang.String&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the row&#47;column to extract.
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExtractArray
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3989993009528522476L;

  /**
   * The type of extraction to perform.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ExtractionType {
    /** column. */
    COLUMN,
    /** row. */
    ROW
  }

  /** the type of extraction. */
  protected ExtractionType m_Type;

  /** the index of the row/column to extract. */
  protected Index m_Index;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Extracts a column or row of data from a weka.core.Instances or "
      + "SpreadSheet object.\n"
      + "Only numeric columns can be returned. In case of row-retrieval, "
      + "the value of the internal format of the weka.core.Instance object "
      + "is returned; for SpreadSheet object it is attempted to convert the "
      + "cell content to double (null values might get returned!).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    ExtractionType.COLUMN);

    m_OptionManager.add(
	    "index", "index",
	    new Index(Index.FIRST));
  }

  /**
   * Initializes the member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Index = new Index();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type);
    result += QuickInfoHelper.toString(this, "index", m_Index, ": ");

    return result;
  }

  /**
   * Sets the type of extraction to perform.
   *
   * @param value	the type
   */
  public void setType(ExtractionType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of extraction to perform.
   *
   * @return		the type
   */
  public ExtractionType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of extraction to perform, row or column.";
  }

  /**
   * Sets the type of extraction to perform.
   *
   * @param value	the type
   */
  public void setIndex(Index value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the type of extraction to perform.
   *
   * @return		the type
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
    return "The index of the row/column to extract.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, adams.core.io.SpreadSheet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Double[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Double[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Double[]		array;
    Instances		inst;
    SpreadSheet		sheet;
    int			i;
    int			index;
    Cell		cell;

    result = null;

    array = null;
    if (m_InputToken.getPayload() instanceof Instances) {
      inst = (Instances) m_InputToken.getPayload();

      if (m_Type == ExtractionType.COLUMN)
	m_Index.setMax(inst.numAttributes());
      else
	m_Index.setMax(inst.numInstances());
      index = m_Index.getIntIndex();

      if (index == -1)
	result = "Invalid index: " + m_Index + " (max=" + m_Index.getMax() + ")";
      else if ((m_Type == ExtractionType.COLUMN) && !inst.attribute(index).isNumeric())
	result = "Column " + m_Index + " is not numeric!";

      if (result == null) {
	if (m_Type == ExtractionType.COLUMN) {
	  array = new Double[inst.numInstances()];
	  for (i = 0; i < array.length; i++)
	    array[i] = inst.instance(i).value(index);
	}
	else {
	  array = new Double[inst.numAttributes()];
	  for (i = 0; i < array.length; i++)
	    array[i] = inst.instance(index).value(i);
	}
      }
    }
    else {
      sheet = (SpreadSheet) m_InputToken.getPayload();

      if (m_Type == ExtractionType.COLUMN)
	m_Index.setMax(sheet.getRowCount());
      else
	m_Index.setMax(sheet.getColumnCount());
      index = m_Index.getIntIndex();

      if (index == -1)
	result = "Invalid index: " + m_Index + " (max=" + m_Index.getMax() + ")";
      else if ((m_Type == ExtractionType.COLUMN) && !sheet.isNumeric(index, true))
	result = "Column " + m_Index + " is not numeric!";

      if (result == null) {
	if (m_Type == ExtractionType.COLUMN) {
	  array = new Double[sheet.getRowCount()];
	  for (i = 0; i < array.length; i++) {
	    cell = sheet.getCell(i, index);
	    if ((cell != null) && !cell.isMissing())
	      array[i] = cell.toDouble();
	  }
	}
	else {
	  array = new Double[sheet.getColumnCount()];
	  for (i = 0; i < array.length; i++) {
	    cell = sheet.getCell(index, i);
	    if ((cell != null) && !cell.isMissing())
	      array[i] = cell.toDouble();
	  }
	}
      }
    }

    if (array != null)
      m_OutputToken = new Token(array);

    return result;
  }
}
