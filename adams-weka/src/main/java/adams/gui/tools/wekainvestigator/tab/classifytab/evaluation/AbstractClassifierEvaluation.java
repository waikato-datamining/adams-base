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
 * AbstractClassifierEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.ClassLister;
import adams.core.GlobalInfoSupporter;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.logging.LoggingObject;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetView;
import adams.flow.core.Token;
import adams.flow.transformer.SpreadSheetInsertColumn;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.ml.data.InstancesView;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.apache.commons.lang.time.StopWatch;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.ComboBoxModel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for classifier evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierEvaluation
  extends LoggingObject
  implements StatusMessageHandler, GlobalInfoSupporter {

  private static final long serialVersionUID = -5847790432092994409L;

  /** the owner. */
  protected ClassifyTab m_Owner;

  /** the panel with the options. */
  protected JPanel m_PanelOptions;

  /**
   * Constructor.
   */
  protected AbstractClassifierEvaluation() {
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
  public void setOwner(ClassifyTab value) {
    m_Owner = value;
    update();
  }

  /**
   * Returns the owner.
   *
   * @return		the owning tab, null if none set
   */
  public ClassifyTab getOwner() {
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
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(Classifier classifier);

  /**
   * Evaluates the classifier and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  protected abstract ResultItem doEvaluate(Classifier classifier, AbstractNamedHistoryPanel<ResultItem> history) throws Exception;

  /**
   * Evaluates the classifier and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  public ResultItem evaluate(Classifier classifier, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ResultItem	result;
    StopWatch 	watch;

    watch = new StopWatch();
    watch.start();
    result = doEvaluate(classifier, history);
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
   * Fills the panel for selection options with the attributes from the
   * specified data container.
   *
   * @param select	the panel to fill
   * @param index	the index, ignored if -1
   */
  protected void fillWithAttributeNames(SelectOptionPanel select, int index) {
    Instances		data;
    List<String>	atts;
    int			i;

    atts = new ArrayList<>();
    if (index > -1) {
      data = getOwner().getData().get(index).getData();
      for (i = 0; i < data.numAttributes(); i++)
	atts.add(data.attribute(i).name());
      Collections.sort(atts);
    }
    select.setOptions(atts.toArray(new String[atts.size()]));
  }

  /**
   * Transfers the additional attributes into a spreadsheet.
   *
   * @param select	the selected attributes
   * @param data	the data to transfer
   * @return		the spreadsheet, null if not attributes selected
   */
  protected SpreadSheet transferAdditionalAttributes(SelectOptionPanel select, Instances data) {
    SpreadSheet			result;
    InstancesView 		iview;
    SpreadSheetView		sview;
    String[]			atts;
    TIntList			indices;
    SpreadSheetInsertColumn	insert;
    Token			token;
    String			msg;
    int				i;

    if (select.getCurrent().length == 0)
      return null;

    atts    = select.getCurrent();
    indices = new TIntArrayList();
    for (String att: atts) {
      if (data.attribute(att) != null)
	indices.add(data.attribute(att).index());
    }
    iview = new InstancesView(data);
    sview = new SpreadSheetView(iview, null, indices.toArray());
    result = new DefaultSpreadSheet();
    result.getHeaderRow().assign(sview.getHeaderRow());
    for (Row row: sview.rows())
      result.addRow().assign(row);
    insert = new SpreadSheetInsertColumn();
    insert.setHeader("Instance Index");
    insert.setNoCopy(false);
    insert.setPosition(new SpreadSheetColumnIndex("1"));
    insert.setAfter(false);
    insert.input(new Token(result));
    msg = insert.execute();
    if (msg != null) {
      getLogger().severe("Failed to transfer additional attributes!\n" + msg);
      return null;
    }
    token = insert.output();
    result = (SpreadSheet) token.getPayload();
    for (i = 0; i < result.getRowCount(); i++)
      result.getRow(i).getCell(0).setContent(i+1);

    return result;
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
    return ClassLister.getSingleton().getClasses(AbstractClassifierEvaluation.class);
  }
}
