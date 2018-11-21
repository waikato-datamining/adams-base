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
 * IndependentComponentsTab.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.Index;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Range;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaAttributeRange;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay;
import adams.gui.visualization.stats.scatterplot.Coordinates;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;
import adams.gui.visualization.stats.scatterplot.action.ViewDataClickAction;
import com.github.waikatodatamining.matrix.algorithm.ica.FastICA;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * Visualizes the ICA components/sources and ICA space calculated from the selected
 * dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndependentComponentsTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -4106630131554796889L;

  public static final String KEY_LEFTPANELWIDTH = "leftpanelwidth";

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_RANGE = "range";

  public static final String KEY_ICA = "ica";

  public static final String KEY_ADDITIONALATTRIBUTES = "additional attributes";

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the left panel. */
  protected JPanel m_PanelLeft;

  /** the right panel. */
  protected JPanel m_PanelRight;

  /** the parameter panel. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the datasets. */
  protected BaseComboBox<String> m_ComboBoxDatasets;

  /** the attribute range. */
  protected BaseTextField m_TextAttributeRange;

  /** the ICA setup. */
  protected GenericObjectEditorPanel m_PanelICA;

  /** the additional attribute range. */
  protected BaseTextField m_TextAdditionalAttributes;

  /** the button to start PCA. */
  protected BaseButton m_ButtonStart;

  /** the button to stop PCA. */
  protected BaseButton m_ButtonStop;

  /** the tabbed pane for the plots. */
  protected BaseTabbedPane m_TabbedPanePlots;

  /** the components plot. */
  protected ScatterPlot m_PanelComponents;

  /** the sources plot. */
  protected ScatterPlot m_PanelSources;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelDatasets = new DefaultComboBoxModel<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 	props;
    JPanel	panelOptions;
    JPanel	panelButtons;
    FastICA	fastica;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(props.getInteger("IndependentComponents.LeftPanelWidth", 200));
    m_SplitPane.setOneTouchExpandable(true);
    m_ContentPanel.add(m_SplitPane, BorderLayout.CENTER);

    m_PanelLeft = new JPanel(new BorderLayout());
    m_PanelRight = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_PanelLeft);
    m_SplitPane.setRightComponent(m_PanelRight);

    panelOptions = new JPanel(new BorderLayout());
    m_PanelLeft.add(panelOptions, BorderLayout.NORTH);

    m_PanelParameters = new ParameterPanel();
    panelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    m_ComboBoxDatasets = new BaseComboBox<>(m_ModelDatasets);
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    m_TextAttributeRange = new BaseTextField(20);
    m_TextAttributeRange.setText(Range.ALL);
    m_TextAttributeRange.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
    });
    m_PanelParameters.addParameter("Range", m_TextAttributeRange);

    try {
      fastica = (FastICA) OptionUtils.forAnyCommandLine(
        FastICA.class,
	props.getProperty("IndependentComponents.FastICA", OptionUtils.getCommandLine(new FastICA())));
    }
    catch (Exception e) {
      fastica = new FastICA();
    }
    m_PanelICA = new GenericObjectEditorPanel(FastICA.class, fastica);
    m_PanelICA.addChangeListener((ChangeEvent e) -> updateButtons());
    m_PanelParameters.addParameter("ICA", m_PanelICA);

    m_TextAdditionalAttributes = new BaseTextField(20);
    m_TextAdditionalAttributes.setText("");
    m_TextAdditionalAttributes.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
    });
    m_PanelParameters.addParameter("Additional attributes", m_TextAdditionalAttributes);

    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelOptions.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonStart = new BaseButton("Start");
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    panelButtons.add(m_ButtonStart);

    m_ButtonStop = new BaseButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopExecution());
    panelButtons.add(m_ButtonStop);

    // the plots
    m_TabbedPanePlots = new BaseTabbedPane();
    m_PanelRight.add(m_TabbedPanePlots, BorderLayout.CENTER);

    m_PanelComponents = new ScatterPlot();
    m_PanelComponents.setXIndex(new Index("1"));
    m_PanelComponents.setYIndex(new Index("2"));
    m_PanelComponents.getPlot().getAxis(Axis.LEFT).setTopMargin(0.01);
    m_PanelComponents.getPlot().getAxis(Axis.LEFT).setBottomMargin(0.01);
    m_PanelComponents.getPlot().getAxis(Axis.BOTTOM).setTopMargin(0.01);
    m_PanelComponents.getPlot().getAxis(Axis.BOTTOM).setBottomMargin(0.01);
    m_PanelComponents.setMouseClickAction(new ViewDataClickAction());
    m_PanelComponents.setOverlays(new AbstractScatterPlotOverlay[]{
      new Coordinates()
    });
    m_TabbedPanePlots.addTab("Components", m_PanelComponents);

    m_PanelSources = new ScatterPlot();
    m_PanelSources.setXIndex(new Index("1"));
    m_PanelSources.setYIndex(new Index("2"));
    m_PanelSources.getPlot().getAxis(Axis.LEFT).setTopMargin(0.01);
    m_PanelSources.getPlot().getAxis(Axis.LEFT).setBottomMargin(0.01);
    m_PanelSources.getPlot().getAxis(Axis.BOTTOM).setTopMargin(0.01);
    m_PanelSources.getPlot().getAxis(Axis.BOTTOM).setBottomMargin(0.01);
    m_PanelSources.setMouseClickAction(new ViewDataClickAction());
    m_PanelSources.setOverlays(new AbstractScatterPlotOverlay[]{
      new Coordinates()
    });
    m_TabbedPanePlots.addTab("Sources", m_PanelSources);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "ICA";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "scatterplot.gif";
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  @Override
  public void dataChanged(WekaInvestigatorDataEvent e) {
    List<String>	datasets;
    int			index;

    if (e.getType() == WekaInvestigatorDataEvent.ROW_ACTIVATED) {
      m_ComboBoxDatasets.setSelectedIndex(e.getRows()[0]);
      return;
    }

    datasets = DatasetHelper.generateDatasetList(getData());
    index    = DatasetHelper.indexOfDataset(getData(), m_ComboBoxDatasets.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelDatasets)) {
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[0]));
      m_ComboBoxDatasets.setModel(m_ModelDatasets);
      if ((index == -1) && (m_ModelDatasets.getSize() > 0))
	m_ComboBoxDatasets.setSelectedIndex(0);
      else if (index > -1)
	m_ComboBoxDatasets.setSelectedIndex(index);
    }
    updateButtons();
  }

  /**
   * Returns whether the tab is busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return (m_Worker != null);
  }

  /**
   * Returns whether data can be visualized.
   *
   * @return		null if can visualize, otherwise error message
   */
  protected String canVisualize() {
    String	rangeStr;
    String	additionalStr;
    Instances	data;

    rangeStr      = m_TextAttributeRange.getText();
    additionalStr = m_TextAdditionalAttributes.getText();
    if (m_ComboBoxDatasets.getSelectedIndex() > -1)
      data = getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    else
      data = null;

    if (isBusy())
      return "Currently busy...";

    if (data == null)
      return "No data selected!";

    if (rangeStr.isEmpty())
      return "No attribute range provided!";

    if (!Range.isValid(rangeStr, data.numAttributes()))
      return "Invalid attribute range!";

    if (!additionalStr.isEmpty() && !Range.isValid(rangeStr, data.numAttributes()))
      return "Invalid additional attributes range!";

    return null;
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    String	msg;

    msg = canVisualize();
    m_ButtonStart.setEnabled(msg == null);
    m_ButtonStart.setToolTipText(msg);
    m_ButtonStop.setEnabled(isBusy());
  }

  /**
   * Returns the additional attribute indices.
   *
   * @param data	the data to use
   * @return		the indices
   */
  protected int[] getAdditionalAttributeIndices(Instances data) {
    int[] 		result;
    WekaAttributeRange 	addRange;

    if (!m_TextAdditionalAttributes.getText().isEmpty()) {
      addRange = new WekaAttributeRange(m_TextAdditionalAttributes.getText());
      addRange.setData(data);
      result = addRange.getIntIndices();
    }
    else {
      result = new int[0];
    }

    return result;
  }

  /**
   * Adds the additional columns from the Instances to the spreadsheet.
   *
   * @param sheet	the spreadsheet to extend
   * @param data	the data to use
   * @param additional	the additional attribute indices to add
   */
  protected void addAdditionalAttributes(SpreadSheet sheet, Instances data, int[] additional) {
    int			r;
    int			a;
    int[]		newIndices;

    if (additional.length == 0)
      return;

    if (sheet.getRowCount() != data.numInstances()) {
      logError(
        "Failed to transfer additional attributes, number of rows in data differ: "
	  + sheet.getRowCount() + " != " + data.numInstances() + "\n"
	  + "Dataset: " + data.relationName(),
	getTitle() + " - Additional attributes transfer");
      return;
    }

    newIndices = new int[additional.length];
    for (a = 0; a < additional.length; a++) {
      newIndices[a] = sheet.getColumnCount();
      sheet.insertColumn(sheet.getColumnCount(), data.attribute(additional[a]).name());
    }

    for (r = 0; r < sheet.getRowCount(); r++) {
      for (a = 0; a < additional.length; a++) {
	if (data.attribute(additional[a]).isNumeric())
	  sheet.getCell(r, newIndices[a]).setContent(data.instance(r).value(additional[a]));
	else if (data.attribute(additional[a]).isNominal() || data.attribute(additional[a]).isString())
	  sheet.getCell(r, newIndices[a]).setContent(data.instance(r).stringValue(additional[a]));
      }
    }
  }

  /**
   * Generates PCA visualization.
   */
  protected void startExecution() {
    startExecution(new InvestigatorTabJob(this, "ICA visualization") {
      @Override
      protected void doRun() {
        DataContainer cont = getData().get(m_ComboBoxDatasets.getSelectedIndex());
	Instances data = cont.getData();
	adams.data.instancesanalysis.FastICA fastica = new adams.data.instancesanalysis.FastICA();
	fastica.setICA((com.github.waikatodatamining.matrix.algorithm.ica.FastICA) m_PanelICA.getCurrent());
	fastica.setAttributeRange(new WekaAttributeRange(m_TextAttributeRange.getText()));
	String msg = fastica.analyze(data);
	if (msg != null) {
	  logError(msg, "ICA error");
	}
	else {
	  int[] additional = getAdditionalAttributeIndices(data);
	  // components
	  SpreadSheet components = fastica.getComponents();
	  addAdditionalAttributes(components, data, additional);
	  m_PanelComponents.setData(components);
	  m_PanelComponents.reset();
	  // sources
	  SpreadSheet sources = fastica.getSources();
	  addAdditionalAttributes(sources, data, additional);
	  m_PanelSources.setData(sources);
	  m_PanelSources.reset();
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
    logMessage("Stopped ICA visualization");
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
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize() {
    Map<String,Object>	result;

    result = super.doSerialize();
    result.put(KEY_LEFTPANELWIDTH, m_SplitPane.getDividerLocation());
    result.put(KEY_DATASET, m_ComboBoxDatasets.getSelectedIndex());
    result.put(KEY_RANGE, m_TextAttributeRange.getText());
    result.put(KEY_ICA, m_PanelICA.getCurrent());
    result.put(KEY_ADDITIONALATTRIBUTES, m_TextAdditionalAttributes.getText());

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
    if (data.containsKey(KEY_LEFTPANELWIDTH))
      m_SplitPane.setDividerLocation((int) data.get(KEY_LEFTPANELWIDTH));
    if (data.containsKey(KEY_DATASET))
      m_ComboBoxDatasets.setSelectedIndex((int) data.get(KEY_DATASET));
    if (data.containsKey(KEY_RANGE))
      m_TextAttributeRange.setText((String) data.get(KEY_RANGE));
    if (data.containsKey(KEY_ICA))
      m_PanelICA.setCurrent(data.get(KEY_ICA));
    if (data.containsKey(KEY_ADDITIONALATTRIBUTES))
      m_TextAdditionalAttributes.setText((String) data.get(KEY_ADDITIONALATTRIBUTES));
  }
}
