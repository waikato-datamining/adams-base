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
 * PreprocessTab.java
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Range;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseSplitButton;
import adams.gui.core.BaseTextField;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ImageManager;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabRunnableJob;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeSelectionPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.InstancesSummaryPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.MultiAttributeSummaryPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.MultiAttributeVisualizationPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction.AbstractSelectedAttributesAction;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction.Remove;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Preprocessing tab.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PreprocessTab
    extends AbstractInvestigatorTabWithEditableDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  public static final String KEY_FILTER = "filter";

  public static final String KEY_BATCHFILTER = "batchfilter";

  public static final String KEY_KEEPNAME = "keepname";

  public static final String KEY_REPLACE = "replace";

  public static final String KEY_SERIALIZE = "serialize";

  public static final String KEY_SERIALIZE_FILE = "serialize-file";

  /** the GOe with the filter. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the panel for the filter and the buttons. */
  protected JPanel m_PanelTop;

  /** the panel for the preprocess panels. */
  protected JPanel m_PanelMain;

  /** the checkbox to replace the datasets. */
  protected BaseCheckBox m_CheckBoxReplace;

  /** the checkbox for batch-filtering. */
  protected BaseCheckBox m_CheckBoxBatchFilter;

  /** the whether to keep the relation name. */
  protected BaseCheckBox m_CheckBoxKeepName;

  /** the whether to serialize the filter to a file. */
  protected BaseCheckBox m_CheckBoxSerialize;

  /** the file to serialize the trained filter to. */
  protected FileChooserPanel m_FileSerialize;

  /** the button for starting the filtering. */
  protected BaseButton m_ButtonStart;

  /** the button for stop the filtering. */
  protected BaseButton m_ButtonStop;

  /** the instances summary panel. */
  protected InstancesSummaryPanel m_PanelInstSummary;

  /** the attribute selection panel. */
  protected AttributeSelectionPanel m_PanelAttSelection;

  /** the attribute summary panel. */
  protected MultiAttributeSummaryPanel m_PanelAttSummary;

  /** the attribute visualization panel. */
  protected MultiAttributeVisualizationPanel m_PanelAttVisualization;

  /** the currently selected attributes. */
  protected BaseTextField m_TextSelectedAttributes;

  /** the button for copying the selected attributes range. */
  protected BaseFlatButton m_ButtonSelectedAttributes;

  /** button for removing checked attributes. */
  protected BaseSplitButton m_ButtonSelectedAttributesAction;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /** the current filter. */
  protected Filter m_CurrentFilter;

  /** the available actions. */
  protected List<AbstractSelectedAttributesAction> m_Actions;

  /** the maximum number of attributes to visualize (summary table/histogram). */
  protected int m_MaxAttributesToVisualize;

  /** the last indices that were visualized. */
  protected int[] m_LastAttributesToVisualize;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Class[]				classes;
    AbstractSelectedAttributesAction 	action;
    Properties 				props;

    super.initialize();

    m_Worker  = null;
    m_Actions = new ArrayList<>();
    classes   = AbstractSelectedAttributesAction.getActions();
    for (Class cls: classes) {
      try {
	action = (AbstractSelectedAttributesAction) cls.getDeclaredConstructor().newInstance();
	action.setOwner(this);
	m_Actions.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate action: " + cls.getName(), e);
      }
    }

    props = InvestigatorPanel.getProperties();

    m_MaxAttributesToVisualize  = props.getInteger("", 5);
    m_LastAttributesToVisualize = new int[0];
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
    m_PanelGOE.addChangeListener((ChangeEvent e) -> updateButtons());
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel.add(m_PanelGOE, BorderLayout.CENTER);
    m_PanelTop.add(panel, BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelTop.add(panel, BorderLayout.SOUTH);

    m_ButtonStart = new BaseButton("Start");
    m_ButtonStart.setEnabled(false);
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    panel.add(m_ButtonStart);

    m_ButtonStop  = new BaseButton("Stop");
    m_ButtonStop.setEnabled(false);
    m_ButtonStart.addActionListener((ActionEvent e) -> stopExecution());
    panel.add(m_ButtonStop);

    m_CheckBoxReplace = new BaseCheckBox("Replace datasets");
    m_CheckBoxReplace.setSelected(props.getBoolean("Preprocess.ReplaceDatasets", true));
    panel.add(m_CheckBoxReplace);

    m_CheckBoxKeepName = new BaseCheckBox("Keep name");
    m_CheckBoxKeepName.setSelected(props.getBoolean("Preprocess.KeepName", true));
    panel.add(m_CheckBoxKeepName);

    m_CheckBoxBatchFilter = new BaseCheckBox("Batch filter");
    m_CheckBoxBatchFilter.setSelected(props.getBoolean("Preprocess.BatchFilter", false));
    panel.add(m_CheckBoxBatchFilter);

    m_CheckBoxSerialize = new BaseCheckBox("Serialize");
    m_CheckBoxSerialize.setSelected(props.getBoolean("Preprocess.Serialize", false));
    panel.add(m_CheckBoxSerialize);

    m_FileSerialize = new FileChooserPanel(props.getPath("Preprocess.SerializeFile", "."));
    m_FileSerialize.setTextColumns(5);
    panel.add(m_FileSerialize);

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
    m_TextSelectedAttributes = new BaseTextField(15);
    m_TextSelectedAttributes.setEditable(false);
    m_ButtonSelectedAttributes = new BaseFlatButton(ImageManager.getIcon("copy.gif"));
    m_ButtonSelectedAttributes.setSize(m_TextSelectedAttributes.getHeight(), m_TextSelectedAttributes.getHeight());
    m_ButtonSelectedAttributes.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(m_TextSelectedAttributes.getText()));
    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel2.add(m_TextSelectedAttributes);
    panel2.add(m_ButtonSelectedAttributes);
    panelAtts.add(panel2, BorderLayout.WEST);

    panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_ButtonSelectedAttributesAction = new BaseSplitButton();
    m_ButtonSelectedAttributesAction.setText("Attribute action");
    m_ButtonSelectedAttributesAction.setButtonEnabled(true);
    m_ButtonSelectedAttributesAction.addChangeListener((ChangeEvent e) -> {
      for (AbstractSelectedAttributesAction action: m_Actions)
	action.update();
    });
    for (AbstractSelectedAttributesAction action: m_Actions) {
      if (action instanceof Remove)
	m_ButtonSelectedAttributesAction.setAction(action);
      else
	m_ButtonSelectedAttributesAction.add(action);
    }
    panel2.add(m_ButtonSelectedAttributesAction);
    panelAtts.add(panel2, BorderLayout.EAST);

    m_PanelAttSelection = new AttributeSelectionPanel();
    m_PanelAttSelection.setBorder(BorderFactory.createTitledBorder("Attributes"));
    m_PanelAttSelection.addSelectionListener((ListSelectionEvent e) -> {
      // update the text field with the indices
      updateAttributeSelection();
      // update actions
      for (AbstractSelectedAttributesAction action: m_Actions)
	action.update();
      // update other panels
      int[] indices = m_PanelAttSelection.getSelectedRows();
      if (indices.length > m_MaxAttributesToVisualize) {
	int[] newIndices = new int[m_MaxAttributesToVisualize];
	System.arraycopy(indices, 0, newIndices, 0, m_MaxAttributesToVisualize);
	indices = newIndices;
      }
      // did they change?
      boolean changed = (indices.length != m_LastAttributesToVisualize.length);
      if (!changed) {
        for (int i = 0; i < indices.length; i++) {
          if (indices[i] != m_LastAttributesToVisualize[i]) {
            changed = true;
            break;
	  }
	}
      }
      if (!changed)
        return;
      // update
      m_PanelAttSummary.setAttributes(indices);
      m_PanelAttVisualization.setAttributes(indices);
      m_LastAttributesToVisualize = indices;
    });
    panel.add(m_PanelAttSelection, BorderLayout.CENTER);

    // main-right
    panel = new JPanel(new GridLayout(2, 1));
    m_PanelMain.add(panel);

    m_PanelAttSummary = new MultiAttributeSummaryPanel();
    m_PanelAttSummary.setBorder(BorderFactory.createTitledBorder("Attribute summary"));
    panel.add(m_PanelAttSummary);

    m_PanelAttVisualization = new MultiAttributeVisualizationPanel();
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createTitledBorder("Attribute visualization"));
    panel2.add(m_PanelAttVisualization);
    panel.add(panel2);

    m_SplitPane.setBottomComponentHidden(false);
    m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
  }

  /**
   * For updating the state of the selected attributes text field and button.
   */
  protected void updateAttributeSelection() {
    int[] 	indices;
    Range 	range;

    indices = m_PanelAttSelection.getSelectedAttributes();
    if (indices == null)
      indices = new int[0];

    if (indices.length == 0) {
      m_TextSelectedAttributes.setText("");
    }
    else {
      range = new Range();
      range.setIndices(indices);
      m_TextSelectedAttributes.setText(range.getRange());
    }

    m_ButtonSelectedAttributes.setEnabled(indices.length > 0);
  }

  /**
   * Checks whether the selected datasets are compatible.
   *
   * @param indices	the indices of the data containers to check
   * @return		null if compatible, otherwise error message
   */
  protected String isCompatible(int[] indices) {
    MessageCollection	errors;
    DataContainer 	first;
    DataContainer 	other;
    String		msg;

    first  = getData().get(indices[0]);
    errors = new MessageCollection();
    for (int i = 1; i < indices.length; i++) {
      other = getData().get(indices[i]);
      msg   = first.getData().equalHeadersMsg(other.getData());
      if (msg != null)
	errors.add("Dataset " + other.getID() + " is not compatible:\n" + msg);
    }

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }

  /**
   * Starts the filtering.
   */
  protected void startExecution() {
    final int[] 		indices;
    final boolean		batch;
    final boolean 		replace;
    final boolean		keep;
    final File 			serialize;
    final InvestigatorPanel	owner;
    String			msg;

    if (!canStartExecution())
      return;

    owner           = getOwner();
    m_CurrentFilter = (Filter) m_PanelGOE.getCurrent();
    batch           = m_CheckBoxBatchFilter.isSelected();
    replace         = m_CheckBoxReplace.isSelected();
    keep            = m_CheckBoxKeepName.isSelected();
    serialize       = (m_CheckBoxSerialize.isSelected() && !m_FileSerialize.getCurrent().isDirectory() ? m_FileSerialize.getCurrent() : null);
    indices         = getSelectedRows();

    // ensure that datasets are compatible
    if (batch && (indices.length > 1)) {
      msg = isCompatible(indices);
      if (msg != null) {
	logError("The datasets are not compatible and cannot be batch-filtered:\n" + msg, "Batch-filtering");
	return;
      }
    }

    startExecution(new InvestigatorTabJob(this, "Filtering") {
      @Override
      protected void doRun() {
	for (int i = 0; i < indices.length; i++) {
	  DataContainer cont = getData().get(indices[i]);
	  logMessage("Starting filtering " + (i+1) + "/" + indices.length + " '" + cont.getID() + "/" + cont.getData().relationName() + "' using " + OptionUtils.getCommandLine(m_CurrentFilter));
	  try {
	    String oldName = cont.getData().relationName();
	    if ((batch && (i == 0)) || !batch)
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
	    if ((i == 0) && (serialize != null)) {
	      SerializationHelper.write(serialize.getAbsolutePath(), m_CurrentFilter);
	      logMessage("Serialized filter to: " + serialize);
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

    updateAttributeSelection();
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
    if (e.getType() == WekaInvestigatorDataEvent.ROWS_ADDED) {
      if (e.getRows().length == 1)
	m_Table.setSelectedRow(e.getRows()[0]);
    }
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
  public BaseCheckBox getCheckBoxReplace() {
    return m_CheckBoxReplace;
  }

  /**
   * Returns the batch filter checkbox.
   *
   * @return		the checkbox
   */
  public BaseCheckBox getCheckBoxBatchFilter() {
    return m_CheckBoxBatchFilter;
  }

  /**
   * Returns the keep name checkbox.
   *
   * @return		the checkbox
   */
  public BaseCheckBox getCheckBoxKeepName() {
    return m_CheckBoxKeepName;
  }

  /**
   * Returns the objects for serialization.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize(Set<SerializationOption> options) {
    Map<String,Object>	result;

    result = super.doSerialize(options);
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_FILTER, OptionUtils.getCommandLine(m_PanelGOE.getCurrent()));
      result.put(KEY_BATCHFILTER, m_CheckBoxBatchFilter.isSelected());
      result.put(KEY_KEEPNAME, m_CheckBoxKeepName.isSelected());
      result.put(KEY_REPLACE, m_CheckBoxReplace.isSelected());
      result.put(KEY_SERIALIZE, m_CheckBoxSerialize.isSelected());
      result.put(KEY_SERIALIZE_FILE, m_FileSerialize.getCurrent().getAbsolutePath());
    }

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
    if (data.containsKey(KEY_SERIALIZE))
      m_CheckBoxSerialize.setSelected((Boolean) data.get(KEY_SERIALIZE));
    if (data.containsKey(KEY_SERIALIZE_FILE))
      m_FileSerialize.setCurrent(new PlaceholderFile((String) data.get(KEY_SERIALIZE_FILE)));
  }
}
