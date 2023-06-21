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
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.container.DataContainer;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.IncrementalDataContainerReader;
import adams.flow.core.ArrayProvider;
import adams.flow.core.Token;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

/**
 * Abstract ancestor for actors that read data containers from disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data that is read from disk
 */
public abstract class AbstractDataContainerFileReader<T extends DataContainer>
  extends AbstractTransformer
  implements ArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 2136481673137019370L;

  /** the key for storing the current containers in the backup. */
  public final static String BACKUP_CONTAINERS = "containers";

  /** the reader to use. */
  protected AbstractDataContainerReader<T> m_Reader;

  /** whether to output an array instead of single items. */
  protected boolean m_OutputArray;

  /** the containers that were read. */
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

    m_OptionManager.add(
      "output-array", "outputArray",
      false);
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
   * Sets whether to output the items as array or as single strings.
   *
   * @param value	true if output is an array
   */
  public void setOutputArray(boolean value) {
    m_OutputArray = value;
    reset();
  }

  /**
   * Returns whether to output the items as array or as single strings.
   *
   * @return		true if output is an array
   */
  public boolean getOutputArray() {
    return m_OutputArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputArrayTipText() {
    return "Whether to output the containers as an array or one-by-one.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "reader", m_Reader);
    result += QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array", ", ");

    return result;
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
   * Returns the base class of the items.
   *
   * @return		the class
   */
  protected abstract Class getItemClass();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    Class[]	result;
    Object	array;

    if (m_OutputArray) {
      array  = Array.newInstance(getItemClass(), 0);
      result = new Class[]{array.getClass()};
    }
    else {
      result = new Class[]{getItemClass()};
    }

    return result;
  }

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
      if ((m_Reader instanceof IncrementalDataContainerReader) && m_OutputArray) {
	while (((IncrementalDataContainerReader) m_Reader).hasMoreData())
	  m_Containers.addAll(m_Reader.read());
      }
      if (isLoggingEnabled())
	getLogger().info(m_Containers.size() + " containers read");
      if (m_OutputArray || !(m_Reader instanceof IncrementalDataContainerReader))
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
    Object	array;
    int		i;
    boolean	error;

    if (m_OutputArray) {
      error = false;
      array = Array.newInstance(getItemClass(), m_Containers.size());
      for (i = 0; i < m_Containers.size(); i++) {
        try {
          Array.set(array, i, m_Containers.get(i));
        }
        catch (Exception e) {
          error = true;
          getLogger().log(
            Level.SEVERE,
            "Failed to set array element #" + (i+1) + " '" + m_Containers.get(i) + "' "
              + "(" + Utils.classToString(m_Containers.get(i)) + ") "
              + "as " + Utils.classToString(getItemClass()), e);
        }
        if (isStopped() || error)
          break;
      }
      result = new Token(array);
      m_Containers.clear();
    }
    else {
      // read more data?
      if ((m_Reader instanceof IncrementalDataContainerReader) && !m_OutputArray) {
        if (m_Containers.size() == 0)
          m_Containers = m_Reader.read();
      }

      result = new Token(m_Containers.get(0));
      m_Containers.remove(0);
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
    if (m_Reader instanceof IncrementalDataContainerReader)
      return (m_Containers.size() > 0) || ((IncrementalDataContainerReader) m_Reader).hasMoreData();
    else
      return (m_Containers.size() > 0);
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
