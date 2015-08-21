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
 * AbstractDataContainerFileReader.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Stoppable;
import adams.core.io.PlaceholderFile;
import adams.data.container.DataContainer;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.IncrementalDataContainerReader;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Abstract ancestor for actors that read data containers from disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data that is read from disk
 */
public abstract class AbstractDataContainerFileReader<T extends DataContainer>
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 2136481673137019370L;

  /** the key for storing the current containers in the backup. */
  public final static String BACKUP_CONTAINERS = "containers";

  /** the reader to use. */
  protected AbstractDataContainerReader<T> m_Reader;

  /** the chromatograms that were read. */
  protected List m_Containers;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    getDefaultReader());
  }

  /**
   * Returns the default reader to use.
   *
   * @return		the default reader
   */
  protected abstract AbstractDataContainerReader getDefaultReader();

  /**
   * Sets the reader to use.
   *
   * @param value	the filter
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
    return "The reader to use for importing the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "reader", m_Reader);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the data type
   */
  public abstract Class[] generates();

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

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
    if (state.containsKey(BACKUP_CONTAINERS)) {
      m_Containers = (List) state.get(BACKUP_CONTAINERS);
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

    m_Containers = new ArrayList();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;

    result = null;

    file = new PlaceholderFile((String) m_InputToken.getPayload());

    // setup reader
    m_Reader.setInput(file);
    if (isLoggingEnabled())
      getLogger().info("Attempting to load '" + file + "'");

    // read data
    try {
      m_Containers = m_Reader.read();
      if (isLoggingEnabled())
	getLogger().info(m_Containers.size() + " containers read");
      if (!(m_Reader instanceof IncrementalDataContainerReader))
	m_Reader.cleanUp();
    }
    catch (Exception e) {
      result = handleException("Error reading '" + file + "': ", e);
      m_Containers.clear();
      return result;
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_Reader instanceof Stoppable)
      ((Stoppable) m_Reader).stopExecution();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    // read more data?
    if (m_Reader instanceof IncrementalDataContainerReader) {
      if (m_Containers.size() == 0)
	m_Containers = m_Reader.read();
    }

    result = new Token(m_Containers.get(0));
    m_Containers.remove(0);

    updateProvenance(result);

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
    if (m_Reader instanceof IncrementalDataContainerReader)
      return (m_Containers.size() > 0) || ((IncrementalDataContainerReader) m_Reader).hasMoreData();
    else
      return (m_Containers.size() > 0);
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Reader.cleanUp();
  }
}
