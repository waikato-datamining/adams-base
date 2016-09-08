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
 * AbstractAttributeSelectionEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.attseltab.evaluation;

import adams.core.ClassLister;
import adams.core.GlobalInfoSupporter;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.logging.LoggingObject;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.tab.AttributeSelectionTab;
import adams.gui.tools.wekainvestigator.tab.attseltab.ResultItem;
import org.apache.commons.lang.time.StopWatch;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;

import javax.swing.ComboBoxModel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for attribute selection evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAttributeSelectionEvaluation
  extends LoggingObject
  implements StatusMessageHandler, GlobalInfoSupporter {

  private static final long serialVersionUID = -5847790432092994409L;

  /** the owner. */
  protected AttributeSelectionTab m_Owner;

  /** the panel with the options. */
  protected JPanel m_PanelOptions;

  /**
   * Constructor.
   */
  protected AbstractAttributeSelectionEvaluation() {
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
  public void setOwner(AttributeSelectionTab value) {
    m_Owner = value;
    update();
  }

  /**
   * Returns the owner.
   *
   * @return		the owning tab, null if none set
   */
  public AttributeSelectionTab getOwner() {
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
   * Tests whether attribute selection can be performed.
   *
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(ASEvaluation evaluator, ASSearch search);

  /**
   * Performs attribute selections and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  protected abstract ResultItem doEvaluate(ASEvaluation evaluator, ASSearch search, AbstractNamedHistoryPanel<ResultItem> history) throws Exception;

  /**
   * Performs attribute selections and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  public ResultItem evaluate(ASEvaluation evaluator, ASSearch search, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ResultItem 	result;
    StopWatch 	watch;

    watch = new StopWatch();
    watch.start();
    result = doEvaluate(evaluator, search, history);
    watch.stop();
    if (result.hasRunInformation())
      result.getRunInformation().add("Total time", (watch.getTime() / 1000.0) + "s");

    return result;
  }

  /**
   * Adds the item to the history and selects it.
   *
   * @param item	the item to add
   * @return		the item
   */
  protected ResultItem addToHistory(AbstractNamedHistoryPanel<ResultItem> history, ResultItem item) {
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
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_Owner.showStatus(msg);
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
      result.add((i + 1) + ": " + data.getData().relationName());
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

    result = -1;

    if (oldDataset != null)
      oldDataset = oldDataset.replaceAll("^[0-9]+: ", "");
    for (i = 0; i < getOwner().getData().size(); i++) {
      data = getOwner().getData().get(i);
      if ((oldDataset != null) && data.getData().relationName().equals(oldDataset)) {
	result = i;
	break;
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
    int		i;
    Set<String>	setDatasets;
    Set<String>	setModel;

    setDatasets = new HashSet<>(newDatasets);
    setModel    = new HashSet<>();
    for (i = 0; i < currentModel.getSize(); i++)
      setModel.add(currentModel.getElementAt(i));

    return (setDatasets.size() != setModel.size())
      || !(setDatasets.containsAll(setModel) && setModel.containsAll(setDatasets));
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static Properties getProperties() {
    return InvestigatorPanel.getProperties();
  }

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getEvaluations() {
    return ClassLister.getSingleton().getClasses(AbstractAttributeSelectionEvaluation.class);
  }
}
