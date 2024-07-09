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
 * SamplePlot.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.repeatedCV;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.sink.sequenceplotter.AbstractErrorPaintlet;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.SimpleErrorPaintlet;
import adams.flow.sink.sequenceplotter.SimpleErrorPaintlet.PlotType;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.DiagonalOverlayPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JComponent;
import java.util.HashMap;

/**
 * Generates a plot with statistics derived for each sample across the cross-validation runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SamplePlot
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the statistic to generate. */
  protected CenterStatistic m_Statistic;

  /** the lower value to compute. */
  protected LowerStatistic m_Lower;

  /** the upper value to compute. */
  protected UpperStatistic m_Upper;

  /** the paintlet to use. */
  protected XYSequencePaintlet m_Paintlet;

  /** the paintlet to use for the lower/upper statistic. */
  protected AbstractErrorPaintlet m_RangePaintlet;

  /** the options for the X axis. */
  protected AxisPanelOptions m_AxisX;

  /** the options for the Y axis. */
  protected AxisPanelOptions m_AxisY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a plot with statistics derived for each sample across the cross-validation runs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statistic",
      CenterStatistic.MEDIAN);

    m_OptionManager.add(
      "lower", "lower",
      LowerStatistic.QUARTILE25);

    m_OptionManager.add(
      "upper", "upper",
      UpperStatistic.QUARTILE75);

    m_OptionManager.add(
      "paintlet", "paintlet",
      getDefaultPaintlet());

    m_OptionManager.add(
      "range-paintlet", "rangePaintlet",
      getDefaultRangePaintlet());

    m_OptionManager.add(
      "axis-x", "axisX",
      getDefaultAxisX());

    m_OptionManager.add(
      "axis-y", "axisY",
      getDefaultAxisY());
  }

  /**
   * Sets the statistic to output.
   *
   * @param value	the statistic
   */
  public void setStatistic(CenterStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the statistic to output.
   *
   * @return		the statistic
   */
  public CenterStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The statistic to output.";
  }

  /**
   * Sets the lower value to output.
   *
   * @param value	the lower value
   */
  public void setLower(LowerStatistic value) {
    m_Lower = value;
    reset();
  }

  /**
   * Returns the lower value to output.
   *
   * @return		the lower value
   */
  public LowerStatistic getLower() {
    return m_Lower;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lowerTipText() {
    return "The lower value to output.";
  }

  /**
   * Sets the upper value to output.
   *
   * @param value	the upper value
   */
  public void setUpper(UpperStatistic value) {
    m_Upper = value;
    reset();
  }

  /**
   * Returns the upper value to output.
   *
   * @return		the upper value
   */
  public UpperStatistic getUpper() {
    return m_Upper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String upperTipText() {
    return "The upper value to output.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Sample plot (RCV)";
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the default
   */
  protected XYSequencePaintlet getDefaultPaintlet() {
    CrossPaintlet	result;

    result = new CrossPaintlet();

    return result;
  }

  /**
   * Sets the paintlet to use.
   *
   * @param value 	the paintlet
   */
  public void setPaintlet(XYSequencePaintlet value) {
    m_Paintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use.
   *
   * @return 		the paintlet
   */
  public XYSequencePaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The paintlet to use for painting the data.";
  }

  /**
   * Returns the default paintlet to use for the lower/upper statistics.
   *
   * @return		the default
   */
  protected AbstractErrorPaintlet getDefaultRangePaintlet() {
    SimpleErrorPaintlet result;

    result = new SimpleErrorPaintlet();
    result.setPlotType(PlotType.LINE);

    return result;
  }

  /**
   * Sets the paintlet to use for the lower/upper statistics.
   *
   * @param value 	the paintlet
   */
  public void setRangePaintlet(AbstractErrorPaintlet value) {
    m_RangePaintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use for the lower/upper statistics.
   *
   * @return 		the paintlet
   */
  public AbstractErrorPaintlet getRangePaintlet() {
    return m_RangePaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangePaintletTipText() {
    return "The paintlet to use for painting the lower/upper statistic overlay.";
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions	result;

    result = new AxisPanelOptions();
    result.setType(Type.DEFAULT);
    result.setLabel("actual");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(40);
    result.setTopMargin(0.05);
    result.setBottomMargin(0.05);
    result.setCustomFormat(new DecimalFormatString("0.00"));
    result.setTickGenerator(new FancyTickGenerator());

    return result;
  }

  /**
   * Sets the setup for the X axis.
   *
   * @param value 	the setup
   */
  public void setAxisX(AxisPanelOptions value) {
    m_AxisX = value;
    reset();
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  public AxisPanelOptions getAxisX() {
    return m_AxisX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String axisXTipText() {
    return "The setup for the X axis.";
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisY() {
    AxisPanelOptions	result;

    result = new AxisPanelOptions();
    result.setType(Type.DEFAULT);
    result.setLabel("predicted");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(60);
    result.setTopMargin(0.05);
    result.setBottomMargin(0.05);
    result.setCustomFormat(new DecimalFormatString("0.00"));
    result.setTickGenerator(new FancyTickGenerator());

    return result;
  }

  /**
   * Sets the setup for the Y axis.
   *
   * @param value 	the setup
   */
  public void setAxisY(AxisPanelOptions value) {
    m_AxisY = value;
    reset();
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  public AxisPanelOptions getAxisY() {
    return m_AxisY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String axisYTipText() {
    return "The setup for the Y axis.";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasRunEvaluations()
	     && item.hasRunOriginalIndices()
	     && (item.getHeader().classIndex() > -1)
	     && item.getHeader().classAttribute().isNumeric();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    SequencePlotterPanel	plot;
    SpreadSheet			stats;
    Row				row;
    int				r;
    TIntList			cols;
    SequencePlotSequence 	seq;
    SequencePlotPoint		point;
    XYSequenceContainerManager 	manager;
    XYSequenceContainer 	cont;
    HashMap<String,Object>	meta;
    int				i;

    cols  = new TIntArrayList();
    stats = PredictionUtils.calcStats(item, errors, m_Statistic, m_Lower, m_Upper, -1, getLogger(), cols, true);

    plot = new SequencePlotterPanel(getTitle());
    plot.setDataPaintlet(ObjectCopyHelper.copyObject(m_Paintlet));
    plot.setErrorPaintlet(ObjectCopyHelper.copyObject(m_RangePaintlet));
    plot.setOverlayPaintlet(new DiagonalOverlayPaintlet());
    plot.setMouseClickAction(new ViewDataClickAction());
    m_AxisX.configure(plot.getPlot(), Axis.BOTTOM);
    m_AxisY.configure(plot.getPlot(), Axis.LEFT);
    plot.setSidePanelVisible(true);

    // create plot data
    seq = new SequencePlotSequence();
    seq.setComparison(Comparison.X_AND_Y);
    seq.setID(item.getHeader().relationName());
    for (r = 0; r < stats.getRowCount(); r++) {
      row = stats.getRow(r);

      point = new SequencePlotPoint(
	"" + (r + 1),
	row.getCell(cols.get(3)).toDouble(),
	row.getCell(cols.get(0)).toDouble(),
	null,
	new Double[]{row.getCell(cols.get(1)).toDouble(), row.getCell(cols.get(2)).toDouble()});

      // meta-data?
      if (cols.get(0) > 0) {
	meta = new HashMap<>();
	for (i = 0; i < cols.get(0); i++)
	  meta.put(stats.getHeaderRow().getCell(i).getContent(), row.getCell(i).getNative());
	point.setMetaData(meta);
      }

      // add to sequence
      seq.add(point);
    }

    // display data
    manager = plot.getContainerManager();
    manager.startUpdate();
    cont = manager.newContainer(seq);
    manager.add(cont);
    manager.finishUpdate();

    return new ComponentContentPanel(plot, false);
  }
}
