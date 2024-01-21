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
 * SpreadSheetFilter.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.filter.TrainableSpreadSheetFilter;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.core.Token;
import adams.flow.core.VariableMonitor;
import adams.flow.standalone.JobRunnerInstance;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunnerSupporter;

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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * <pre>-prefer-jobrunner &lt;boolean&gt; (property: preferJobRunner)
 * &nbsp;&nbsp;&nbsp;If enabled, tries to offload the processing onto a adams.flow.standalone.JobRunnerInstance;
 * &nbsp;&nbsp;&nbsp; applies only to training.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetFilter
  extends AbstractSpreadSheetTransformer
  implements VariableMonitor, JobRunnerSupporter {

  public static class FilterJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the filter to use. */
    protected adams.data.spreadsheet.filter.SpreadSheetFilter m_Filter;

    /** the data to filter. */
    protected SpreadSheet m_Data;

    /** the filtered data. */
    protected SpreadSheet m_Filtered;

    /**
     * Initializes the job.
     *
     * @param filter  	the filter to use
     * @param data 	the training data
     */
    public FilterJob(adams.data.spreadsheet.filter.SpreadSheetFilter filter, SpreadSheet data) {
      super();
      m_Filter   = filter;
      m_Data     = data;
      m_Filtered = null;
    }

    /**
     * Returns the filtered data.
     *
     * @return		the filtered data, null if not available
     */
    public SpreadSheet getFiltered() {
      return m_Filtered;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Filter == null)
	return "No filter to use!";
      if (m_Data == null)
	return "No data provided!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Filtered = m_Filter.filter(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      return null;
    }

    /**
     * Returns a string representation of this job.
     *
     * @return the job as string
     */
    @Override
    public String toString() {
      return OptionUtils.getCommandLine(m_Filter) + "\n" + m_Data.getName();
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Filter   = null;
      m_Data     = null;
      m_Filtered = null;
      super.cleanUp();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -4633161214275622241L;

  /** the filter to apply. */
  protected adams.data.spreadsheet.filter.SpreadSheetFilter m_Filter;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /** whether to offload training into a JobRunnerInstance. */
  protected boolean m_PreferJobRunner;

  /** the JobRunnerInstance to use. */
  protected transient JobRunnerInstance m_JobRunnerInstance;

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

    m_OptionManager.add(
      "prefer-jobrunner", "preferJobRunner",
      false);
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
   * Sets whether to offload processing to a JobRunner instance if available.
   *
   * @param value	if true try to find/use a JobRunner instance
   */
  public void setPreferJobRunner(boolean value) {
    m_PreferJobRunner = value;
    reset();
  }

  /**
   * Returns whether to offload processing to a JobRunner instance if available.
   *
   * @return		if true try to find/use a JobRunner instance
   */
  public boolean getPreferJobRunner() {
    return m_PreferJobRunner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String preferJobRunnerTipText() {
    return "If enabled, tries to offload the processing onto a " + Utils.classToString(JobRunnerInstance.class) + "; applies only to training.";
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
    result += QuickInfoHelper.toString(this, "preferJobRunner", m_PreferJobRunner, ", jobrunner");

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
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_PreferJobRunner)
	m_JobRunnerInstance = JobRunnerInstance.locate(this, true);
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
    SpreadSheet	sheet;
    FilterJob	job;

    result = null;
    sheet  = (SpreadSheet) m_InputToken.getPayload();

    try {
      if (m_JobRunnerInstance != null) {
	job    = new FilterJob(m_Filter, sheet);
	result = m_JobRunnerInstance.executeJob(job);
	sheet  = job.getFiltered();
	job.cleanUp();
	if (result != null)
	  throw new Exception(result);
      }
      else {
	sheet = m_Filter.filter(sheet);
      }
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
