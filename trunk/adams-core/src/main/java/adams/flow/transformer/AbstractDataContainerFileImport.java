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
 * AbstractDataContainerFileImport.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.io.PlaceholderFile;
import adams.data.container.DataContainer;
import adams.data.io.input.AbstractDataContainerReader;
import adams.db.DataProvider;
import adams.db.DatabaseConnectionHandler;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 * Abstract ancestor for actors that import data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data that is imported
 */
public abstract class AbstractDataContainerFileImport<T extends DataContainer>
  extends AbstractDbDataProcessor
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3449734957975707303L;

  /** the key for storing the current ids in the backup. */
  public final static String BACKUP_IDS = "ids";

  /** the key for storing the current containers in the backup. */
  public final static String BACKUP_CONTAINERS = "containers";

  /** the reader to use for processing the containers. */
  protected AbstractDataContainerReader<T> m_Reader;

  /** the IDs of the containers that have been imported. */
  protected List<Integer> m_IDs;

  /** the containers that have been read. */
  protected List<T> m_Containers;

  /** whether to import the containers into the database. */
  protected boolean m_Import;

  /** whether to forward the containers instead of the IDs. */
  protected boolean m_Forward;

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
	    "forward", "forward",
	    false);

    m_OptionManager.add(
	    "import", "import",
	    true);
  }

  /**
   * Returns the default reader for loading the data.
   *
   * @return		the default reader
   */
  protected abstract AbstractDataContainerReader<T> getDefaultReader();

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(AbstractDataContainerReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader
   */
  public AbstractDataContainerReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for importing the containers.";
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
        "If set to true then the containers are forwarded instead of the IDs.";
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
        "If set to true then the containers are imported into the database.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_IDS);
    pruneBackup(BACKUP_CONTAINERS);
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
    result.put(BACKUP_CONTAINERS, m_Containers);

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

    if (state.containsKey(BACKUP_CONTAINERS)) {
      m_Containers = (List<T>) state.get(BACKUP_CONTAINERS);
      state.remove(BACKUP_CONTAINERS);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IDs        = new ArrayList<Integer>();
    m_Containers = new ArrayList<T>();
  }

  /**
   * Returns the report provider to use for writing the reports to the database.
   *
   * @return		the provider to use
   */
  protected abstract DataProvider<T> getDataProvider();

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
    List<T> 		conts;
    int			i;
    int			id;
    DataProvider<T>	provider;

    result = false;

    // setup reader
    m_Reader.setInput(new PlaceholderFile(file));
    if (isLoggingEnabled())
      getLogger().info("Attempting to load '" + file + "'");

    // read data
    try {
      conts = m_Reader.read();
      if (isLoggingEnabled())
	getLogger().info(conts.size() + " containers read");
      m_Reader.cleanUp();
    }
    catch (Exception e) {
      m_ProcessError = handleException("Error reading '" + file + "': ", e);
      return result;
    }

    // import data into database
    if (conts.size() > 0) {
      result   = true;
      provider = null;
      if (m_Import)
	provider = getDataProvider();
      for (i = 0; i < conts.size(); i++) {
	try {
	  if (m_Import) {
	    id = provider.add(conts.get(i));
	    if (isLoggingEnabled())
	      getLogger().info("Container (" + conts.get(i) + ") imported: " + id);
	    if (m_Forward)
	      m_Containers.add(conts.get(i));
	    else
	      m_IDs.add(id);
	  }
	  else if (m_Forward) {
	    m_Containers.add(conts.get(i));
	  }
	}
	catch (Exception e) {
	  result = false;
	  handleException("Error importing container (" + conts.get(i) + "): ", e);
	}
      }
    }

    return result;
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the class
   */
  protected abstract Class getDataContainerClass();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    if (m_Forward)
      return new Class[]{getDataContainerClass()};
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
      result = new Token(m_Containers.get(0));
      m_Containers.remove(0);
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
      return (m_Containers.size() > 0);
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
