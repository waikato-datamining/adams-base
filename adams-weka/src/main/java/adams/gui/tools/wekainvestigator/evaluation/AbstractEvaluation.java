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
 * AbstractEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.evaluation;

import adams.core.GlobalInfoSupporter;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.logging.LoggingObject;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.output.AbstractResultItem;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ancestor for evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEvaluation<T extends AbstractInvestigatorTab, R extends AbstractResultItem>
  extends LoggingObject
  implements StatusMessageHandler, GlobalInfoSupporter {

  private static final long serialVersionUID = -5847790432092994409L;

  /** the owner. */
  protected T m_Owner;

  /** the panel with the options. */
  protected JPanel m_PanelOptions;

  /**
   * Constructor.
   */
  protected AbstractEvaluation() {
    initialize();
    initGUI();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
  }

  /**
   * Initializes the panel.
   */
  protected void initGUI() {
    m_PanelOptions = new JPanel(new BorderLayout());
  }

  /**
   * Sets the owner.
   *
   * @param value	the owning tab
   */
  public void setOwner(T value) {
    m_Owner = value;
    update();
  }

  /**
   * Returns the owner.
   *
   * @return		the owning tab, null if none set
   */
  public T getOwner() {
    return m_Owner;
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * Returns a panel with options to display.
   *
   * @return		the panel
   */
  public JPanel getPanel() {
    return m_PanelOptions;
  }

  /**
   * Returns the interval to use for outputting progress info during testing.
   *
   * @return		the interval
   */
  protected int getTestingUpdateInterval() {
    return getProperties().getInteger("General.TestingUpdateInterval", 1000);
  }

  /**
   * Adds the item to the history and selects it.
   *
   * @param item	the item to add
   * @return		the item
   */
  protected R addToHistory(AbstractNamedHistoryPanel<R> history, R item) {
    history.addEntry(item.getName(), item);
    history.setSelectedIndex(history.count() - 1);
    return item;
  }

  /**
   * Returns just the name of the evaluation.
   *
   * @return		the evaluation
   */
  public String toString() {
    return getName();
  }

  /**
   * Updates the settings panel.
   */
  public abstract void update();

  /**
   * Activates the specified dataset.
   *
   * @param index	the index of the dataset
   */
  public abstract void activate(int index);

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_Owner.logMessage(msg);
  }

  /**
   * Generates the list of datasets for a combobox.
   *
   * @return		the list
   */
  protected List<String> generateDatasetList() {
    List<String> 	result;
    int			i;
    DataContainer 	data;

    result = new ArrayList<>();
    for (i = 0; i < getOwner().getData().size(); i++) {
      data = getOwner().getData().get(i);
      result.add(data.getID() + ": " + data.getData().relationName());
    }

    return result;
  }

  /**
   * Determines the index of the old dataset name in the current dataset list.
   *
   * @param oldDataset	the old dataset to look for
   * @return		the index, -1 if not found
   */
  protected int indexOfDataset(String oldDataset) {
    int 		result;
    int			i;
    DataContainer	data;
    String		idStr;
    int			id;

    result = -1;

    if (oldDataset != null) {
      // get ID
      idStr = oldDataset.replaceAll(":.*", "");
      try {
	id = Integer.parseInt(idStr);
      }
      catch (Exception e) {
	id = -1;
      }

      // try ID
      for (i = 0; i < getOwner().getData().size(); i++) {
	data = getOwner().getData().get(i);
	if (data.getID() == id) {
	  result = i;
	  break;
	}
      }

      // try relationname
      if (result == -1) {
	oldDataset = oldDataset.replaceAll("^[0-9]+: ", "");
	for (i = 0; i < getOwner().getData().size(); i++) {
	  data = getOwner().getData().get(i);
	  if (data.getData().relationName().equals(oldDataset)) {
	    result = i;
	    break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Checks whether the data has changed and the model needs updating.
   *
   * @param newDatasets		the new list of datasets
   * @param currentModel	the current model
   * @return			true if changed
   */
  protected boolean hasDataChanged(List<String> newDatasets, ComboBoxModel<String> currentModel) {
    boolean	result;
    int		i;
    Set<String>	setDatasets;
    Set<String>	setModel;

    setDatasets = new HashSet<>(newDatasets);
    setModel    = new HashSet<>();
    for (i = 0; i < currentModel.getSize(); i++)
      setModel.add(currentModel.getElementAt(i));

    // different datasets?
    result = (setDatasets.size() != setModel.size())
      || !(setDatasets.containsAll(setModel) && setModel.containsAll(setDatasets));

    // different order?
    if (!result) {
      for (i = 0; i < newDatasets.size(); i++) {
	if (!newDatasets.get(i).equals(currentModel.getElementAt(i))) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Checks whether the combobox selection index is a valid dataset index.
   *
   * @param combobox	the combobox to check
   * @return		true if valid index
   */
  protected boolean isValidDataIndex(JComboBox combobox) {
    if (combobox.getSelectedIndex() == -1)
      return false;
    if (combobox.getSelectedIndex() >= getOwner().getData().size())
      return false;
    return true;
  }

  /**
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  public Map<String,Object> serialize() {
    Map<String,Object>	result;

    result = new HashMap<>();

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  public void deserialize(Map<String,Object> data, MessageCollection errors) {
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static Properties getProperties() {
    return InvestigatorPanel.getProperties();
  }
}
