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
 * DatasetRowFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.flow.core.Token;
import adams.ml.data.Dataset;
import adams.ml.preprocessing.StreamFilter;
import adams.ml.preprocessing.unsupervised.PassThrough;

/**
 <!-- globalinfo-start -->
 * Applies the stream filter to the rows.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.Row<br>
 * &nbsp;&nbsp;&nbsp;adams.ml.data.Dataset<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.Row<br>
 * &nbsp;&nbsp;&nbsp;adams.ml.data.Dataset<br>
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
 * &nbsp;&nbsp;&nbsp;default: DatasetRowFilter
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
 * <pre>-filter &lt;adams.ml.preprocessing.StreamFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to use for filtering the Dataset rows.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.preprocessing.unsupervised.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DatasetRowFilter
  extends AbstractTransformer {

  private static final long serialVersionUID = -2575293379884905336L;

  /** the batch filter to use. */
  protected StreamFilter m_Filter;

  /** the actual filter to use. */
  protected StreamFilter m_ActualFilter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the stream filter to the rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new PassThrough());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualFilter = null;
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(StreamFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public StreamFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the Dataset rows.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Row.class, Dataset.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Row.class, Dataset.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filter", m_Filter, "filter: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Row 	rowIn;
    Dataset	setIn;
    Row 	rowOut;
    Dataset	setOut;

    result = null;
    rowIn  = null;
    setIn  = null;
    if (m_InputToken.hasPayload(Row.class))
      rowIn = m_InputToken.getPayload(Row.class);
    else
      setIn = m_InputToken.getPayload(Dataset.class);

    try {
      if (m_ActualFilter == null)
        m_ActualFilter = ObjectCopyHelper.copyObject(m_Filter);
      if (rowIn != null) {
	rowOut = m_ActualFilter.filter(rowIn);
	m_OutputToken = new Token(rowOut);
      }
      else {
	setOut = m_ActualFilter.filter(setIn);
	m_OutputToken = new Token(setOut);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to filter " + (rowIn != null ? "row" : "dataset") + "!", e);
    }

    return result;
  }
}
