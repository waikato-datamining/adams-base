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
 * PrincipalComponentsTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.Index;
import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Range;
import adams.core.base.BaseRegExp;
import adams.data.instancesanalysis.PCA;
import adams.data.weka.WekaAttributeRange;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay;
import adams.gui.visualization.stats.scatterplot.Coordinates;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;
import adams.gui.visualization.stats.scatterplot.action.ViewDataClickAction;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * Visualizes the PCA loadings and PCA space calculated from the selected
 * dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrincipalComponentsTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -4106630131554796889L;

  public static final String KEY_LEFTPANELWIDTH = "leftpanelwidth";

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_RANGE = "range";

  public static final String KEY_VARIANCE = "variance";

  public static final String KEY_MAXATTRIBUTES = "maxattributes";

  public static final String KEY_MAXATTRIBUTENAMES = "maxattributenames";

  public static final String KEY_SKIPNOMINAL = "skipnominal";

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
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the attribute range. */
  protected JTextField m_TextAttributeRange;

  /** the variance. */
  protected NumberTextField m_TextVariance;

  /** the maximum number of attributes. */
  protected NumberTextField m_TextMaxAttributes;

  /** the maximum number of attribute names. */
  protected NumberTextField m_TextMaxAttributeNames;

  /** whether to skip nominal attributes. */
  protected JCheckBox m_CheckBoxSkipNominal;

  /** the button to start PCA. */
  protected JButton m_ButtonStart;

  /** the button to stop PCA. */
  protected JButton m_ButtonStop;

  /** the tabbed pane for the plots. */
  protected BaseTabbedPane m_TabbedPanePlots;

  /** the loadings plot. */
  protected ScatterPlot m_PanelLoadings;

  /** the scores plot. */
  protected ScatterPlot m_PanelScores;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelDatasets   = new DefaultComboBoxModel<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 	props;
    JPanel	panelOptions;
    JPanel	panelButtons;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(props.getInteger("PrincipalComponents.LeftPanelWidth", 200));
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

    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    m_TextAttributeRange = new JTextField(20);
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

    m_TextVariance = new NumberTextField(Type.DOUBLE, 10);
    m_TextVariance.setValue(props.getDouble("PrincipalComponents.Variance", 0.95));
    m_PanelParameters.addParameter("Variance", m_TextVariance);

    m_TextMaxAttributes = new NumberTextField(Type.INTEGER, 10);
    m_TextMaxAttributes.setValue(props.getInteger("PrincipalComponents.MaxAttributes", -1));
    m_PanelParameters.addParameter("Max attributes", m_TextMaxAttributes);

    m_TextMaxAttributeNames = new NumberTextField(Type.INTEGER, 10);
    m_TextMaxAttributeNames.setValue(props.getInteger("PrincipalComponents.MaxAttributeNames", 5));
    m_PanelParameters.addParameter("Max attribute names", m_TextMaxAttributeNames);

    m_CheckBoxSkipNominal = new JCheckBox();
    m_CheckBoxSkipNominal.setSelected(props.getBoolean("PrincipalComponents.SkipNominal", false));
    m_PanelParameters.addParameter("Skip nominal attributes", m_CheckBoxSkipNominal);

    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelOptions.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonStart = new JButton("Start");
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    panelButtons.add(m_ButtonStart);

    m_ButtonStop = new JButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopExecution());
    panelButtons.add(m_ButtonStop);

    // the plots
    m_TabbedPanePlots = new BaseTabbedPane();
    m_PanelRight.add(m_TabbedPanePlots, BorderLayout.CENTER);

    m_PanelLoadings = new ScatterPlot();
    m_PanelLoadings.setXRegExp(new BaseRegExp("Loading-1"));
    m_PanelLoadings.setYRegExp(new BaseRegExp("Loading-2"));
    m_PanelLoadings.getPlot().getAxis(Axis.LEFT).setTopMargin(0.01);
    m_PanelLoadings.getPlot().getAxis(Axis.LEFT).setBottomMargin(0.01);
    m_PanelLoadings.getPlot().getAxis(Axis.BOTTOM).setTopMargin(0.01);
    m_PanelLoadings.getPlot().getAxis(Axis.BOTTOM).setBottomMargin(0.01);
    m_PanelLoadings.setMouseClickAction(new ViewDataClickAction());
    m_PanelLoadings.setOverlays(new AbstractScatterPlotOverlay[]{
      new Coordinates()
    });
    m_TabbedPanePlots.addTab("Loadings", m_PanelLoadings);

    m_PanelScores = new ScatterPlot();
    m_PanelScores.setXIndex(new Index("1"));
    m_PanelScores.setYIndex(new Index("2"));
    m_PanelScores.getPlot().getAxis(Axis.LEFT).setTopMargin(0.01);
    m_PanelScores.getPlot().getAxis(Axis.LEFT).setBottomMargin(0.01);
    m_PanelScores.getPlot().getAxis(Axis.BOTTOM).setTopMargin(0.01);
    m_PanelScores.getPlot().getAxis(Axis.BOTTOM).setBottomMargin(0.01);
    m_PanelScores.setMouseClickAction(new ViewDataClickAction());
    m_PanelScores.setOverlays(new AbstractScatterPlotOverlay[]{
      new Coordinates()
    });
    m_TabbedPanePlots.addTab("Scores", m_PanelScores);
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
    return "PCA";
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
    index    = DatasetHelper.indexOfDataset(getData(), (String) m_ComboBoxDatasets.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelDatasets)) {
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
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
    Instances	data;

    rangeStr = m_TextAttributeRange.getText();
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
   * Generates PCA visualization.
   */
  protected void startExecution() {
    startExecution(new InvestigatorTabJob(this, "PCA visualization") {
      @Override
      protected void doRun() {
        DataContainer cont = getData().get(m_ComboBoxDatasets.getSelectedIndex());
        PCA pca = new PCA();
        pca.setAttributeRange(new WekaAttributeRange(m_TextAttributeRange.getText()));
        pca.setVariance(m_TextVariance.getValue().doubleValue());
        pca.setMaxAttributes(m_TextMaxAttributes.getValue().intValue());
        pca.setMaxAttributeNames(m_TextMaxAttributeNames.getValue().intValue());
	pca.setSkipNominal(m_CheckBoxSkipNominal.isSelected());
        String result = pca.analyze(cont.getData());
        if (result != null) {
          logError(result, "PCA error");
        }
        else {
          m_PanelLoadings.setData(pca.getLoadings());
          m_PanelLoadings.reset();
          m_PanelScores.setData(pca.getScores());
          m_PanelScores.reset();
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
    logMessage("Stopped PCA visualization");
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
    result.put(KEY_VARIANCE, m_TextVariance.getValue().doubleValue());
    result.put(KEY_MAXATTRIBUTES, m_TextMaxAttributes.getValue().intValue());
    result.put(KEY_MAXATTRIBUTENAMES, m_TextMaxAttributeNames.getValue().intValue());
    result.put(KEY_SKIPNOMINAL, m_CheckBoxSkipNominal.isSelected());

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
    if (data.containsKey(KEY_VARIANCE))
      m_TextVariance.setValue((double) data.get(KEY_VARIANCE));
    if (data.containsKey(KEY_MAXATTRIBUTES))
      m_TextMaxAttributes.setValue((int) data.get(KEY_MAXATTRIBUTES));
    if (data.containsKey(KEY_MAXATTRIBUTENAMES))
      m_TextMaxAttributeNames.setValue((int) data.get(KEY_MAXATTRIBUTENAMES));
    if (data.containsKey(KEY_SKIPNOMINAL))
      m_CheckBoxSkipNominal.setSelected((boolean) data.get(KEY_MAXATTRIBUTENAMES));
  }
}
