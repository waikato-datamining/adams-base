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
 * AbstractReportDbWriter.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Constants;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.db.ReportProvider;
import adams.flow.core.Token;
import adams.flow.transformer.report.AbstractReportPreProcessor;
import adams.flow.transformer.report.NoPreProcessing;

/**
 * Abstract ancestor for actors that write reports to the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to handle
 */
public abstract class AbstractReportDbWriter<T extends Report>
  extends AbstractDbTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5253006932367969870L;

  /** whether to remove existing reports (i.e., completely remove them from the DB). */
  protected boolean m_RemoveExisting;

  /** whether to merge the current and existing report. */
  protected boolean m_Merge;

  /** the fields to overwrite in "merge" mode. */
  protected Field[] m_OverwriteFields;

  /** whether to allow reports that are not associated with a data container. */
  protected boolean m_StandaloneReports;

  /** the pre-processor to apply to the data. */
  protected AbstractReportPreProcessor m_PreProcessor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "remove", "removeExisting",
	    false);

    m_OptionManager.add(
	    "merge", "merge",
	    false);

    m_OptionManager.add(
	    "overwrite", "overwriteFields",
	    new Field[]{});

    m_OptionManager.add(
	    "standalone-reports", "standaloneReports",
	    false);
    
    m_OptionManager.add(
	"pre-processor", "preProcessor",
	new NoPreProcessing());
  }

  /**
   * Sets whether existing reports are removed first.
   *
   * @param value 	if true then existing reports will be removed first
   */
  public void setRemoveExisting(boolean value) {
    m_RemoveExisting = value;
    reset();
  }

  /**
   * Returns whether existing reports are removed first.
   *
   * @return 		true if are removed first
   */
  public boolean getRemoveExisting() {
    return m_RemoveExisting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeExistingTipText() {
    return
        "If true then existing reports will be removed first completely "
      + "from the database before the current one is saved.";
  }

  /**
   * Sets whether the information of the current report is added to an existing
   * one.
   *
   * @param value 	if true then information is added
   */
  public void setMerge(boolean value) {
    m_Merge = value;
    reset();
  }

  /**
   * Returns whether the information of current report is added to an existing
   * one.
   *
   * @return 		true if information is added
   */
  public boolean getMerge() {
    return m_Merge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mergeTipText() {
    return
        "If true then the information in the current report is only added to "
      + "the existing one (but '" + Report.FIELD_DUMMYREPORT + "' "
      + "is always set to 'false').";
  }

  /**
   * Sets the fields to overwrite in "merge" mode.
   *
   * @param value 	the fields
   */
  public void setOverwriteFields(Field[] value) {
    m_OverwriteFields = value;
    reset();
  }

  /**
   * Returns the fields to overwrite in "merge" mode.
   *
   * @return 		the fields
   */
  public Field[] getOverwriteFields() {
    return m_OverwriteFields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overwriteFieldsTipText() {
    return "The fields to overwrite with the new data when in 'merge' mode.";
  }

  /**
   * Sets whether reports are allowed that are not associated with a
   * data container.
   *
   * @param value 	if true then reports don't have to be associated with
   * 			a data container
   */
  public void setStandaloneReports(boolean value) {
    m_StandaloneReports = value;
    reset();
  }

  /**
   * Returns whether reports don't have to be associated with a data container.
   *
   * @return 		true if reports don't have to be associated with a
   * 			data container
   */
  public boolean getStandaloneReports() {
    return m_StandaloneReports;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String standaloneReportsTipText() {
    return
        "If true then reports don't have to be associated with a data container (= 'standalone').";
  }

  /**
   * Sets the pre-processor to apply to the data.
   *
   * @param value 	the pre-processor
   */
  public void setPreProcessor(AbstractReportPreProcessor value) {
    m_PreProcessor = value;
    m_PreProcessor.setOwner(this);
    reset();
  }

  /**
   * Returns the pre-processor in use.
   *
   * @return 		the pre-processor
   */
  public AbstractReportPreProcessor getPreProcessor() {
    return m_PreProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preProcessorTipText() {
    return "The pre-processor to apply to the data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the report class
   */
  @Override
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Integer.class};
  }

  /**
   * Returns the report provider to use for writing the reports to the database.
   *
   * @return		the provider to use
   */
  protected abstract ReportProvider<T> getReportProvider();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String		result;
    ReportProvider<T>	provider;
    boolean		stored;
    T 			report;
    int			parentID;

    result = null;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report = (T) ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report = (T) m_InputToken.getPayload();

    if (report == null) {
      result = "No report attached: " + m_InputToken.getPayload();
    }
    else {
      report   = (T) m_PreProcessor.preProcess(report);
      parentID = report.getDatabaseID();
      stored   = false;

      if (m_StandaloneReports || (parentID != Constants.NO_ID)) {
	provider = getReportProvider();
	stored   = provider.store(parentID, report, m_RemoveExisting, m_Merge, m_OverwriteFields);
      }
      else {
	result = "No container with ID: " + parentID;
      }

      if ((parentID != Constants.NO_ID) && stored)
	m_OutputToken = new Token(parentID);
    }

    return result;
  }
}
