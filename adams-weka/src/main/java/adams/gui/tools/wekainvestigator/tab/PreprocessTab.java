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
 * PreprocessTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Range;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabRunnableJob;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeSelectionPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeSummaryPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeVisualizationPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.InstancesSummaryPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction.AbstractSelectedAttributesAction;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction.RemoveChecked;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSplitButton;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Preprocessing tab.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreprocessTab
  extends AbstractInvestigatorTabWithEditableDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  public static final String KEY_FILTER = "filter";

  public static final String KEY_BATCHFILTER = "batchfilter";

  public static final String KEY_KEEPNAME = "keepname";

  public static final String KEY_REPLACE = "replace";

  /** the GOe with the filter. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the panel for the filter and the buttons. */
  protected JPanel m_PanelTop;

  /** the panel for the preprocess panels. */
  protected JPanel m_PanelMain;

  /** the checkbox to replace the datasets. */
  protected JCheckBox m_CheckBoxReplace;

  /** the checkbox for batch-filtering. */
  protected JCheckBox m_CheckBoxBatchFilter;

  /** the whether to keep the relation name. */
  protected JCheckBox m_CheckBoxKeepName;

  /** the button for starting the filtering. */
  protected JButton m_ButtonStart;

  /** the button for stop the filtering. */
  protected JButton m_ButtonStop;

  /** the instances summary panel. */
  protected InstancesSummaryPanel m_PanelInstSummary;

  /** the attribute selection panel. */
  protected AttributeSelectionPanel m_PanelAttSelection;

  /** the attribute summary panel. */
  protected AttributeSummaryPanel m_PanelAttSummary;

  /** the attribute visualization panel. */
  protected AttributeVisualizationPanel m_PanelAttVisualization;

  /** the currently selected attributes. */
  protected JTextField m_TextSelectedAttributes;

  /** the button for copying the selected attributes range. */
  protected JideButton m_ButtonSelectedAttributes;

  /** button for removing checked attributes. */
  protected JideSplitButton m_ButtonSelectedAttributesAction;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /** the current filter. */
  protected Filter m_CurrentFilter;

  /** the available actions. */
  protected List<AbstractSelectedAttributesAction> m_Actions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Class[]				classes;
    AbstractSelectedAttributesAction 	action;

    super.initialize();

    m_Worker  = null;
    m_Actions = new ArrayList<>();
    classes   = AbstractSelectedAttributesAction.getActions();
    for (Class cls: classes) {
      try {
	action = (AbstractSelectedAttributesAction) cls.newInstance();
	action.setOwner(this);
	m_Actions.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate action: " + cls.getName(), e);
      }
    }
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 		props;
    Filter		filter;
    JPanel		panel;
    JPanel		panel2;
    JPanel		panelAtts;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    // top
    m_PanelTop = new JPanel(new BorderLayout());
    m_PanelTop.setBorder(BorderFactory.createTitledBorder("Filter"));
    m_PanelData.add(m_PanelTop, BorderLayout.NORTH);

    try {
      filter = (Filter) OptionUtils.forAnyCommandLine(
	Filter.class,
	InvestigatorPanel.getProperties().getProperty(
	  "Preprocess.Filter", AllFilter.class.getName()));
    }
    catch (Exception e) {
      filter = new AllFilter();
    }
    m_PanelGOE = new GenericObjectEditorPanel(Filter.class, filter, true);
    m_PanelGOE.moveChooseButton(true);
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel.add(m_PanelGOE, BorderLayout.CENTER);
    m_PanelTop.add(panel, BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelTop.add(panel, BorderLayout.SOUTH);

    m_ButtonStart = new JButton("Start");
    m_ButtonStart.setEnabled(false);
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    panel.add(m_ButtonStart);

    m_ButtonStop  = new JButton("Stop");
    m_ButtonStop.setEnabled(false);
    m_ButtonStart.addActionListener((ActionEvent e) -> stopExecution());
    panel.add(m_ButtonStop);

    m_CheckBoxReplace = new JCheckBox("Replace datasets");
    m_CheckBoxReplace.setSelected(props.getBoolean("Preprocess.ReplaceDatasets", true));
    panel.add(m_CheckBoxReplace);

    m_CheckBoxKeepName = new JCheckBox("Keep name");
    m_CheckBoxKeepName.setSelected(props.getBoolean("Preprocess.KeepName", true));
    panel.add(m_CheckBoxKeepName);

    m_CheckBoxBatchFilter = new JCheckBox("Batch filter");
    m_CheckBoxBatchFilter.setSelected(props.getBoolean("Preprocess.BatchFilter", false));
    panel.add(m_CheckBoxBatchFilter);

    // main
    m_PanelMain = new JPanel(new GridLayout(1, 2));
    m_PanelMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelData.add(m_PanelMain, BorderLayout.CENTER);

    // main-left
    panel = new JPanel(new BorderLayout());
    m_PanelMain.add(panel);

    m_PanelInstSummary = new InstancesSummaryPanel();
    m_PanelInstSummary.setBorder(BorderFactory.createTitledBorder("Dataset summary"));
    panel.add(m_PanelInstSummary, BorderLayout.NORTH);

    panelAtts = new JPanel(new BorderLayout());
    panel.add(panelAtts, BorderLayout.SOUTH);
    m_TextSelectedAttributes = new JTextField(15);
    m_TextSelectedAttributes.setEditable(false);
    m_ButtonSelectedAttributes = new JideButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonSelectedAttributes.setButtonStyle(JideSplitButton.TOOLBOX_STYLE);
    m_ButtonSelectedAttributes.setSize(m_TextSelectedAttributes.getHeight(), m_TextSelectedAttributes.getHeight());
    m_ButtonSelectedAttributes.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(m_TextSelectedAttributes.getText()));
    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel2.add(m_TextSelectedAttributes);
    panel2.add(m_ButtonSelectedAttributes);
    panelAtts.add(panel2, BorderLayout.WEST);

    panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_ButtonSelectedAttributesAction = new JideSplitButton();
    m_ButtonSelectedAttributesAction.setAlwaysDropdown(false);
    m_ButtonSelectedAttributesAction.setButtonEnabled(true);
    m_ButtonSelectedAttributesAction.setButtonStyle(JideSplitButton.TOOLBOX_STYLE);
    for (AbstractSelectedAttributesAction action: m_Actions) {
      if (action instanceof RemoveChecked)
	m_ButtonSelectedAttributesAction.setAction(action);
      else
	m_ButtonSelectedAttributesAction.add(action);
    }
    panel2.add(m_ButtonSelectedAttributesAction);
    panelAtts.add(panel2, BorderLayout.EAST);

    m_PanelAttSelection = new AttributeSelectionPanel();
    m_PanelAttSelection.setBorder(BorderFactory.createTitledBorder("Attributes"));
    m_PanelAttSelection.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
      // update other panels
      int[] indices = m_PanelAttSelection.getSelectedRows();
      if (indices.length == 1) {
	m_PanelAttSummary.setAttribute(indices[0]);
	m_PanelAttVisualization.setAttribute(indices[0]);
      }
      else {
	// TODO unset?
      }
      // range
      indices = m_PanelAttSelection.getSelectedAttributes();
      if (indices.length == 0) {
	m_TextSelectedAttributes.setText("");
      }
      else {
	Range range = new Range();
	range.setIndices(indices);
	m_TextSelectedAttributes.setText(range.getRange());
      }
      m_ButtonSelectedAttributes.setEnabled(indices.length > 0);
    });
    panel.add(m_PanelAttSelection, BorderLayout.CENTER);

    // main-right
    panel = new JPanel(new GridLayout(2, 1));
    m_PanelMain.add(panel);

    m_PanelAttSummary = new AttributeSummaryPanel();
    m_PanelAttSummary.setBorder(BorderFactory.createTitledBorder("Attribute summary"));
    panel.add(m_PanelAttSummary);

    m_PanelAttVisualization = new AttributeVisualizationPanel();
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createTitledBorder("Attribute visualization"));
    panel2.add(m_PanelAttVisualization);
    panel.add(panel2);

    m_SplitPane.setBottomComponentHidden(false);
    m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
  }

  /**
   * Starts the filtering.
   */
  protected void startExecution() {
    final int[] 		indices;
    final boolean		batch;
    final boolean 		replace;
    final boolean		keep;
    final InvestigatorPanel	owner;

    if (!canStartExecution())
      return;

    owner           = getOwner();
    m_CurrentFilter = (Filter) m_PanelGOE.getCurrent();
    batch           = m_CheckBoxBatchFilter.isSelected();
    replace         = m_CheckBoxReplace.isSelected();
    keep            = m_CheckBoxKeepName.isSelected();
    indices         = getSelectedRows();

    startExecution(new InvestigatorTabJob(this, "Filtering") {
      @Override
      protected void doRun() {
	for (int i = 0; i < indices.length; i++) {
	  DataContainer cont = getData().get(indices[i]);
	  logMessage("Starting filtering " + (i+1) + "/" + indices.length + " '" + cont.getID() + "/" + cont.getData().relationName() + "' using " + OptionUtils.getCommandLine(m_CurrentFilter));
	  try {
	    String oldName = cont.getData().relationName();
	    if ((!batch && (i == 0)) || batch)
	      m_CurrentFilter.setInputFormat(cont.getData());
	    Instances filtered = Filter.useFilter(cont.getData(), m_CurrentFilter);
	    if (keep)
	      filtered.setRelationName(oldName);
	    logMessage("Finished filtering " + (i+1) + "/" + indices.length + " '" + cont.getID() + "/" + cont.getData().relationName() + "' using " + OptionUtils.getCommandLine(m_CurrentFilter));
	    if (replace) {
	      cont.setData(filtered);
	      fireDataChange(new WekaInvestigatorDataEvent(owner, WekaInvestigatorDataEvent.ROWS_MODIFIED, indices[i]));
	    }
	    else {
	      cont = new MemoryContainer(filtered);
	      getData().add(cont);
	      fireDataChange(new WekaInvestigatorDataEvent(owner, WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
	    }
	  }
	  catch (Exception e) {
	    logError("Failed to filter data" + (i+1) + "/" + indices.length, e, "Filter");
	    break;
	  }
	}
      }
    });
  }

  /**
   * Hook method that gets called after successfully starting a job.
   *
   * @param job		the job that got started
   */
  @Override
  protected void postStartExecution(InvestigatorTabJob job) {
    super.postStartExecution(job);
    updateButtons();
  }

  /**
   * Hook method that gets called after stopping a job.
   */
  @Override
  protected void postStopExecution() {
    super.postStopExecution();
    logMessage("Stopped filtering using " + OptionUtils.getCommandLine(m_CurrentFilter));
    updateButtons();
  }

  /**
   * Hook method that gets called after finishing a job.
   */
  @Override
  protected void postExecutionFinished() {
    super.postExecutionFinished();
    updateButtons();
  }

  /**
   * Submits the job.
   *
   * @param run		the job to submit
   * @return		true if successfully submitted
   */
  public synchronized boolean submitJob(Runnable run) {
    return canStartExecution() && startExecution(new InvestigatorTabRunnableJob(this, run));
  }

  /**
   * Checks whether data can be filtered.
   *
   * @return		null if data can be filtered, otherwise error message
   */
  protected String canFilter() {
    if (isBusy())
      return "Currently busy...";

    if (getSelectedRows().length == 0)
      return "No dataset selected!";

    return null;
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    String	msg;

    super.updateButtons();

    msg = canFilter();
    m_ButtonStart.setEnabled(msg == null);
    m_ButtonStart.setToolTipText(msg);
    m_ButtonStop.setEnabled(isBusy());
    m_ButtonSelectedAttributesAction.setEnabled(!isBusy() && (getSelectedRows().length == 1));
    m_ButtonSelectedAttributes.setEnabled(m_TextSelectedAttributes.getText().length() > 0);
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Preprocess";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "filter.png";
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  }

  /**
   * Notifies all the tabs that the data has changed.
   *
   * @param e		the event to send
   */
  public void fireDataChange(WekaInvestigatorDataEvent e) {
    super.fireDataChange(e);
    displayData();
  }

  /**
   * Gets called when the user changes the selection.
   */
  protected void dataTableSelectionChanged() {
    super.dataTableSelectionChanged();
    displayData();
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    super.dataChanged(e);
    displayData();
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    DataContainer	cont;

    if ((getData().size() > 0) && (getSelectedRows().length == 1)) {
      cont = getData().get(getSelectedRows()[0]);
      m_PanelInstSummary.setInstances(cont.getData());
      m_PanelAttSelection.setInstances(cont.getData());
      m_PanelAttSummary.setInstances(cont.getData());
      m_PanelAttVisualization.setInstances(cont.getData());
      m_ButtonSelectedAttributesAction.setEnabled(true);
    }
    else {
      m_PanelInstSummary.setInstances(null);
      m_PanelAttSelection.setInstances(null);
      m_PanelAttSummary.setInstances(null);
      m_PanelAttVisualization.setInstances(null);
      m_ButtonSelectedAttributesAction.setEnabled(false);
    }

    repaint();
  }

  /**
   * Returns the attribute selection panel.
   *
   * @return		the panel
   */
  public AttributeSelectionPanel getAttributeSelectionPanel() {
    return m_PanelAttSelection;
  }

  /**
   * Returns the replace checkbox.
   *
   * @return		the checkbox
   */
  public JCheckBox getCheckBoxReplace() {
    return m_CheckBoxReplace;
  }

  /**
   * Returns the batch filter checkbox.
   *
   * @return		the checkbox
   */
  public JCheckBox getCheckBoxBatchFilter() {
    return m_CheckBoxBatchFilter;
  }

  /**
   * Returns the keep name checkbox.
   *
   * @return		the checkbox
   */
  public JCheckBox getCheckBoxKeepName() {
    return m_CheckBoxKeepName;
  }

  /**
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize() {
    Map<String,Object>	result;

    result = super.doSerialize();
    result.put(KEY_FILTER, OptionUtils.getCommandLine(m_PanelGOE.getCurrent()));
    result.put(KEY_BATCHFILTER, m_CheckBoxBatchFilter.isSelected());
    result.put(KEY_KEEPNAME, m_CheckBoxKeepName.isSelected());
    result.put(KEY_REPLACE, m_CheckBoxReplace.isSelected());

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_FILTER)) {
      try {
        m_CurrentFilter = (Filter) OptionUtils.forAnyCommandLine(Filter.class, (String) data.get(KEY_FILTER));
        m_PanelGOE.setCurrent(m_CurrentFilter);
      }
      catch (Exception e) {
        errors.add("Failed to restore filter: " + data.get(KEY_FILTER), e);
      }
    }
    if (data.containsKey(KEY_BATCHFILTER))
      m_CheckBoxBatchFilter.setSelected((Boolean) data.get(KEY_BATCHFILTER));
    if (data.containsKey(KEY_KEEPNAME))
      m_CheckBoxKeepName.setSelected((Boolean) data.get(KEY_KEEPNAME));
    if (data.containsKey(KEY_REPLACE))
      m_CheckBoxReplace.setSelected((Boolean) data.get(KEY_REPLACE));
  }
}
