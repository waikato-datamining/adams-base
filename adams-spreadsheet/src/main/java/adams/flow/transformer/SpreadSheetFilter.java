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
 * SpreadSheetFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.filter.TrainableSpreadSheetFilter;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.core.Token;
import adams.flow.core.VariableMonitor;

/**
 <!-- globalinfo-start -->
 * Applies the specified spreadsheet filter to the data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetFilter
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
 * <pre>-filter &lt;adams.data.spreadsheet.filter.SpreadSheetFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to use for filtering the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.filter.PassThrough
 * </pre>
 * 
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to monitor for resetting trainable filters.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFilter
  extends AbstractSpreadSheetTransformer
  implements VariableMonitor {

  /** for serialization. */
  private static final long serialVersionUID = -4633161214275622241L;

  /** the filter to apply. */
  protected adams.data.spreadsheet.filter.SpreadSheetFilter m_Filter;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified spreadsheet filter to the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new adams.data.spreadsheet.filter.PassThrough());

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(adams.data.spreadsheet.filter.SpreadSheetFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public adams.data.spreadsheet.filter.SpreadSheetFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the spreadsheet.";
  }

  /**
   * Sets the name of the variable to monitor.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to monitor.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The variable to monitor for resetting trainable filters.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "filter", m_Filter);
    result += QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue(), ", monitor: ");

    return result;
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);
    if ((e.getType() == Type.MODIFIED) || (e.getType() == Type.ADDED)) {
      if (e.getName().equals(m_VariableName.getValue())) {
	if (m_Filter instanceof TrainableSpreadSheetFilter)
	  ((TrainableSpreadSheetFilter) m_Filter).resetFilter();
        if (isLoggingEnabled())
          getLogger().info("Reset 'trainable filter'");
      }
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    SpreadSheet	sheet;
    
    result = null;
    sheet  = (SpreadSheet) m_InputToken.getPayload();

    try {
      sheet = m_Filter.filter(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to filter spreadsheet!", e);
      sheet = null;
    }

    if (sheet != null)
      m_OutputToken = new Token(sheet);
    
    return result;
  }
}
