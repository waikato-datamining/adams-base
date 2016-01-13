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
 * AbstractReportFileImport.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.Constants;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.db.DatabaseConnectionHandler;
import adams.db.ReportProviderByDBID;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 * Abstract ancestor for report import actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of reports to import
 */
public abstract class AbstractReportFileImport<T extends Report>
  extends AbstractDbDataProcessor
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4427045123505865448L;

  /** the key for storing the current ids in the backup. */
  public final static String BACKUP_IDS = "ids";

  /** the key for storing the current reports in the backup. */
  public final static String BACKUP_REPORTS = "reports";

  /** the IDs of the chromatograms that have been imported. */
  protected List<Integer> m_IDs;

  /** the reports that have been read. */
  protected List<T> m_Reports;

  /** the reader used for reading the reports. */
  protected AbstractReportReader<T> m_Reader;

  /** whether to remove existing reports (i.e., completely remove them from the DB). */
  protected boolean m_RemoveExisting;

  /** whether to import the containers into the database. */
  protected boolean m_Import;

  /** whether to forward the containers instead of the IDs. */
  protected boolean m_Forward;

  /** whether to merge the current and existing report. */
  protected boolean m_Merge;

  /** the fields to overwrite in "merge" mode. */
  protected Field[] m_OverwriteFields;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Imports quantitation reports into the database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    getDefaultReader());

    m_OptionManager.add(
	    "remove", "removeExisting",
	    false);

    m_OptionManager.add(
	    "forward", "forward",
	    false);

    m_OptionManager.add(
	    "import", "import",
	    true);

    m_OptionManager.add(
	    "merge", "merge",
	    false);

    m_OptionManager.add(
	    "overwrite", "overwriteFields",
	    new Field[]{});
  }

  /**
   * Returns the default reader for loading the reports.
   *
   * @return		the default reader
   */
  protected abstract AbstractReportReader<T> getDefaultReader();

  /**
   * Sets the reader to use.
   *
   * @param value	the filter
   */
  public void setReader(AbstractReportReader value) {
    m_Reader = value;
    if (m_Reader instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Reader).setDatabaseConnection(m_DatabaseConnection);
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader
   */
  public AbstractReportReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader used for loading the reports.";
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
   * Sets whether to forward the containers instead of the IDs.
   *
   * @param value	if true then the containers are forwarded
   */
  public void setForward(boolean value) {
    m_Forward = value;
    reset();
  }

  /**
   * Returns whether to forward the containers or the IDs.
   *
   * @return		true if the containers are forwarded
   */
  public boolean getForward() {
    return m_Forward;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forwardTipText() {
    return
        "If set to true then the reports are forwarded instead of the IDs.";
  }

  /**
   * Sets whether to import the containers into the database.
   *
   * @param value	if true then the containers are imported
   */
  public void setImport(boolean value) {
    m_Import = value;
    reset();
  }

  /**
   * Returns whether to import the containers into the database.
   *
   * @return		true if the containers are imported
   */
  public boolean getImport() {
    return m_Import;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String importTipText() {
    return
        "If set to true then the reports are imported into the database.";
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
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_IDS);
    pruneBackup(BACKUP_REPORTS);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_IDS, m_IDs);
    result.put(BACKUP_REPORTS, m_Reports);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_IDS)) {
      m_IDs = (List<Integer>) state.get(BACKUP_IDS);
      state.remove(BACKUP_IDS);
    }

    if (state.containsKey(BACKUP_REPORTS)) {
      m_Reports = (List<T>) state.get(BACKUP_REPORTS);
      state.remove(BACKUP_REPORTS);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IDs     = new ArrayList<Integer>();
    m_Reports = new ArrayList<T>();
  }

  /**
   * Returns the report provider to use for writing the reports to the database.
   *
   * @return		the provider to use
   */
  protected abstract ReportProviderByDBID<T> getReportProvider();

  /**
   * Configures the database connection if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String setUpDatabaseConnection() {
    String	result;

    result = null;

    if (m_DatabaseConnection == null) {
      result = super.setUpDatabaseConnection();
      if (result == null) {
	if (m_Import || (m_Reader instanceof DatabaseConnectionHandler)) {
	  m_DatabaseConnection = getDatabaseConnection();
	  if (m_Reader instanceof DatabaseConnectionHandler)
	    ((DatabaseConnectionHandler) m_Reader).setDatabaseConnection(m_DatabaseConnection);
	}
      }
    }

    return result;
  }

  /**
   * Processes the given data.
   *
   * @param file	the file/dir to process
   * @return		true if everything went alright
   */
  @Override
  protected boolean processData(File file) {
    boolean		result;
    ReportProviderByDBID<T> provider;
    List<T>		reports;

    result = false;

    // setup reader
    m_Reader.setInput(new PlaceholderFile(file));
    if (isLoggingEnabled())
      getLogger().info("Attempting to load '" + file + "'");

    // load report
    reports = m_Reader.read();

    for (T report: reports) {
      if (m_Import) {
	provider = getReportProvider();
	result   = provider.store(report.getDatabaseID(), report, m_RemoveExisting, m_Merge, m_OverwriteFields);
	if (m_Forward) {
	  m_Reports.add(report);
	}
	else {
	  if (result)
	    m_IDs.add(report.getDatabaseID());
	  else
	    m_IDs.add(Constants.NO_ID);
	}
      }
      else if (m_Forward) {
	m_Reports.add(report);
	result = true;
      }
    }

    return result;
  }

  /**
   * Returns the report class in use.
   *
   * @return		the report class
   */
  protected abstract Class getReportClass();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    if (m_Forward)
      return new Class[]{getReportClass()};
    else if (m_Import)
      return new Class[]{Integer.class};
    else
      return new Class[]{};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = null;

    if (m_Forward) {
      result = new Token(m_Reports.get(0));
      m_Reports.remove(0);
      updateProvenance(result);
    }
    else if (m_Import) {
      result = new Token(m_IDs.get(0));
      m_IDs.remove(0);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    if (m_Forward)
      return (m_Reports.size() > 0);
    else if (m_Import)
      return (m_IDs.size() > 0);
    else
      return false;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  @Override
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }
}
