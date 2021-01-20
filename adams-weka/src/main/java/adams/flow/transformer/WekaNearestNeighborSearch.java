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
 * WekaNearestNeighborhoodSearch.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.container.WekaNearestNeighborSearchContainer;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;
import adams.flow.core.VariableMonitor;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Outputs the specified number of nearest neighbors for the incoming Weka Instance.<br>
 * The data used for the nearest neighbor search is either obtained from storage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaNearestNeighborSearchContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaNearestNeighborSearchContainer: Instance, Neighborhood
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
 * &nbsp;&nbsp;&nbsp;default: WekaNearestNeighborSearch
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
 * <pre>-search &lt;weka.core.neighboursearch.NearestNeighbourSearch&gt; (property: search)
 * &nbsp;&nbsp;&nbsp;The search algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"
 * </pre>
 * 
 * <pre>-max-neighbors &lt;int&gt; (property: maxNeighbors)
 * &nbsp;&nbsp;&nbsp;The maximum number of neighbors to find.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-storage &lt;adams.flow.control.StorageName&gt; (property: storage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the data from.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to monitor.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaNearestNeighborSearch
  extends AbstractTransformer
  implements StorageUser, VariableMonitor {

  private static final long serialVersionUID = -5495087922726994088L;

  /** the key for storing the current initialized state in the backup. */
  public final static String BACKUP_SEARCH = "search";

  /** the neighboorhood search to use. */
  protected NearestNeighbourSearch m_Search;

  /** the actual neighboorhood search in use. */
  protected NearestNeighbourSearch m_ActualSearch;

  /** the maximum number of neighbors to return. */
  protected int m_MaxNeighbors;

  /** the storage item. */
  protected StorageName m_Storage;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs the specified number of nearest neighbors for the incoming Weka Instance.\n"
      + "The data used for the nearest neighbor search is either obtained from storage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "search", "search",
      new LinearNNSearch());

    m_OptionManager.add(
      "max-neighbors", "maxNeighbors",
      10, 1, null);

    m_OptionManager.add(
      "storage", "storage",
      new StorageName());

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualSearch = null;
  }

  /**
   * Sets the search algorithm.
   *
   * @param value	the algorithm
   */
  public void setSearch(NearestNeighbourSearch value) {
    m_Search = value;
    reset();
  }

  /**
   * Returns the search algorithm.
   *
   * @return		the algorithm
   */
  public NearestNeighbourSearch getSearch() {
    return m_Search;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String searchTipText() {
    return "The search algorithm to use.";
  }

  /**
   * Sets the maximum number of neighbors to find.
   *
   * @param value	the maximum
   */
  public void setMaxNeighbors(int value) {
    m_MaxNeighbors = value;
    reset();
  }

  /**
   * Returns the maximum number of neighbors to find.
   *
   * @return		the maximum
   */
  public int getMaxNeighbors() {
    return m_MaxNeighbors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxNeighborsTipText() {
    return "The maximum number of neighbors to find.";
  }

  /**
   * Sets the data storage item.
   *
   * @param value	the storage item
   */
  public void setStorage(StorageName value) {
    m_Storage = value;
    reset();
  }

  /**
   * Returns the data storage item.
   *
   * @return		the storage item
   */
  public StorageName getStorage() {
    return m_Storage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageTipText() {
    return "The storage item to obtain the data from.";
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
    return "The variable to monitor.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "search", m_Search.getClass(), "search: ");
    result += QuickInfoHelper.toString(this, "storage", m_Storage, ", storage: ");
    result += QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue(), ", variable: ");

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_SEARCH);
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

    if (m_ActualSearch != null)
      result.put(BACKUP_SEARCH, m_ActualSearch);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_SEARCH)) {
      m_ActualSearch = (NearestNeighbourSearch) state.get(BACKUP_SEARCH);
      state.remove(BACKUP_SEARCH);
    }

    super.restoreState(state);
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
        m_ActualSearch = null;
        if (isLoggingEnabled())
          getLogger().info("Reset search algorithm");
      }
    }
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  @Override
  public boolean isUsingStorage() {
    return !getSkip();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaNearestNeighborSearchContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 	result;
    Instances	data;
    Instance	inst;
    Instances	hood;
    double[]	distances;

    result = null;

    // setup search
    if (m_ActualSearch == null) {
      try {
	m_ActualSearch = ObjectCopyHelper.copyObject(m_Search);
      }
      catch (Exception e) {
	result = handleException("Failed to create copy of search algorithm!", e);
      }
      if (result == null) {
	data = (Instances) getStorageHandler().getStorage().get(m_Storage);
	if (data == null) {
	  result = "No data available: " + m_Storage;
	}
	else {
	  try {
	    m_ActualSearch.setInstances(data);
	  }
	  catch (Exception e) {
	    result = handleException("Failed to initialize search algorithm!", e);
	  }
	}
      }
    }

    // perform search
    if (result == null) {
      inst = (Instance) m_InputToken.getPayload();
      try {
	hood          = m_ActualSearch.kNearestNeighbours(inst, m_MaxNeighbors);
	distances     = m_ActualSearch.getDistances();
	m_OutputToken = new Token(new WekaNearestNeighborSearchContainer(inst, hood, distances));
      }
      catch (Exception e) {
	result = handleException("Failed to determine neighbors for: " + inst, e);
      }
    }

    return result;
  }
}
