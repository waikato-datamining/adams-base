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
 * ControlChartPlot.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spc.Limits;
import adams.data.statistics.StatUtils;
import adams.flow.container.ControlChartContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.sink.controlchartplot.AbstractControlChartPaintlet;
import adams.flow.sink.controlchartplot.ChartPaintlet;
import adams.flow.sink.controlchartplot.LimitPaintlet;
import adams.flow.sink.sequenceplotter.MouseClickAction;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.AbstractLimitedTickGenerator;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.TickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.MultiPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ControlChartPlot
  extends AbstractGraphicalDisplay {

  /** for serialization. */
  private static final long serialVersionUID = 3238389451500168650L;

  /** the paintlet to use for painting the chart data. */
  protected AbstractControlChartPaintlet m_Paintlet;

  /** the paintlet to use for painting the limits. */
  protected AbstractControlChartPaintlet m_LimitPaintlet;

  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;

  /** the mouse click action. */
  protected MouseClickAction m_MouseClickAction;

  /** the title. */
  protected String m_Title;

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
    return "Actor for generating control chart plots.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "paintlet", "paintlet",
      new ChartPaintlet());

    m_OptionManager.add(
      "limit-paintlet", "limitPaintlet",
      new LimitPaintlet());

    m_OptionManager.add(
      "mouse-click-action", "mouseClickAction",
      new NullClickAction());

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "title", "title",
      "");

    m_OptionManager.add(
      "axis-x", "axisX",
      getDefaultAxisX());

    m_OptionManager.add(
      "axis-y", "axisY",
      getDefaultAxisY());
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 350;
  }

  /**
   * Sets the paintlet to use.
   *
   * @param value 	the paintlet
   */
  public void setPaintlet(AbstractControlChartPaintlet value) {
    m_Paintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use.
   *
   * @return 		the paintlet
   */
  public AbstractControlChartPaintlet getPaintlet() {
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
   * Sets the paintlet to use for painting the limits.
   *
   * @param value 	the paintlet
   */
  public void setLimitPaintlet(AbstractControlChartPaintlet value) {
    m_LimitPaintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use for painting the limits.
   *
   * @return 		the paintlet
   */
  public AbstractControlChartPaintlet getLimitPaintlet() {
    return m_LimitPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitPaintletTipText() {
    return "The paintlet to use for painting the limits.";
  }

  /**
   * Sets the mouse click action to use.
   * 
   * @param value	the action
   */
  public void setMouseClickAction(MouseClickAction value) {
    m_MouseClickAction = value;
    reset();
  }
  
  /**
   * Returns the current mouse click action in use.
   * 
   * @return		the action
   */
  public MouseClickAction getMouseClickAction() {
    return m_MouseClickAction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mouseClickActionTipText() {
    return "The action to use for mouse clicks on the canvas.";
  }

  /**
   * Sets the color provider to use.
   *
   * @param value 	the color provider
   */
  public void setColorProvider(AbstractColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider in use.
   *
   * @return 		the color provider
   */
  public AbstractColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider in use for generating the colors for the various plots.";
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions	result;
    FancyTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("sample");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(1);
    result.setWidth(40);
    result.setTopMargin(0.05);
    result.setBottomMargin(0.05);
    result.setCustomFormat(new DecimalFormatString("0"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(20);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisY() {
    AxisPanelOptions	result;
    FancyTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("y");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(60);
    result.setTopMargin(0.05);
    result.setBottomMargin(0.05);
    result.setCustomFormat(new DecimalFormatString("0.0"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.setTickGenerator(tick);

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
   * Sets the title for border around the plot.
   *
   * @param value 	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the title for border around the plot.
   *
   * @return 		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title for the border around the plot.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((SequencePlotterPanel) m_Panel).getContainerManager().clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    SequencePlotterPanel	result;
    MultiPaintlet		multi;

    multi = new MultiPaintlet();
    multi.setSubPaintlets(new XYSequencePaintlet[]{
      getPaintlet(),
      getLimitPaintlet()
    });

    result = new SequencePlotterPanel(getTitle());
    result.setPaintlet(multi);
    ActorUtils.updateFlowAwarePaintlet(result.getPaintlet(), this);
    result.setMouseClickAction(m_MouseClickAction);
    m_AxisX.configure(result.getPlot(), Axis.BOTTOM);
    m_AxisY.configure(result.getPlot(), Axis.LEFT);
    result.setColorProvider(getColorProvider().shallowCopy());
    result.getContainerList().setAllowSearch(false);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ControlChartContainer.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    XYSequenceContainer		cont;
    XYSequenceContainerManager	manager;
    XYSequence			seq;
    SequencePlotPoint		point;
    ControlChartContainer 	chartCont;
    String 			chartName;
    double[]			prepared;
    Limits[]			limits;
    int				i;
    TIntHashSet			violations;
    HashMap<String,Object>	meta;
    double			min;
    double			max;
    AxisPanel 			axisY;
    AxisPanel 			axisX;
    int				numTicks;
    TickGenerator		tick;

    manager = ((SequencePlotterPanel) m_Panel).getContainerManager();
    manager.startUpdate();

    // extract data from container
    chartCont  = (ControlChartContainer) token.getPayload();
    chartName  = (String) chartCont.getValue(ControlChartContainer.VALUE_CHART);
    prepared   = (double[]) chartCont.getValue(ControlChartContainer.VALUE_PREPARED);
    limits     = (Limits[]) chartCont.getValue(ControlChartContainer.VALUE_LIMITS);
    if (chartCont.hasValue(ControlChartContainer.VALUE_VIOLATIONS))
      violations = new TIntHashSet((int[]) chartCont.getValue(ControlChartContainer.VALUE_VIOLATIONS));
    else
      violations = new TIntHashSet();

    // find or create new plot
    if (manager.indexOf(chartName) == -1) {
      seq  = new SequencePlotSequence();
      seq.setComparison(Comparison.X_AND_Y);
      seq.setID(chartName);
      cont = manager.newContainer(seq);
      manager.add(cont);
    }
    else {
      cont = manager.get(manager.indexOf(chartName));
      seq  = cont.getData();
    }

    // # ticks
    axisX = ((SequencePlotterPanel) m_Panel).getPlot().getAxis(Axis.BOTTOM);
    numTicks = 0;
    for (i = 0; i < manager.countVisible(); i++)
      numTicks = Math.max(numTicks, manager.getVisible(i).getData().size());
    tick = axisX.getTickGenerator();
    if (tick instanceof AbstractLimitedTickGenerator) {
      if (((AbstractLimitedTickGenerator) tick).getNumTicks() != numTicks) {
	tick = tick.shallowCopy();
	((AbstractLimitedTickGenerator) tick).setNumTicks(numTicks);
	axisX.setTickGenerator(tick);
      }
    }

    // create sequence
    min = Double.MAX_VALUE;
    max = Double.MIN_VALUE;
    for (i = 0; i < prepared.length; i++) {
      meta  = new HashMap<>();
      if (limits.length == prepared.length) {
	meta.put("lower", limits[i].getLower());
	meta.put("center", limits[i].getCenter());
	meta.put("upper", limits[i].getUpper());
	// min/max
	min = StatUtils.min(new double[]{min, limits[i].getLower(), limits[i].getCenter(), limits[i].getUpper(), prepared[i]});
	max = StatUtils.max(new double[]{max, limits[i].getLower(), limits[i].getCenter(), limits[i].getUpper(), prepared[i]});
      }
      else {
	meta.put("lower", limits[0].getLower());
	meta.put("center", limits[0].getCenter());
	meta.put("upper", limits[0].getUpper());
	// min/max
	min = StatUtils.min(new double[]{min, limits[0].getLower(), limits[0].getCenter(), limits[0].getUpper(), prepared[i]});
	max = StatUtils.max(new double[]{max, limits[0].getLower(), limits[0].getCenter(), limits[0].getUpper(), prepared[i]});
      }
      meta.put("violation", violations.contains(i));
      point = new SequencePlotPoint("" + (seq.size() + 1), seq.size() + 1, prepared[i]);
      point.setMetaData(meta);
      seq.add(point);
    }

    // update min/max
    axisY = ((SequencePlotterPanel) m_Panel).getPlot().getAxis(Axis.LEFT);
    axisY.setManualMinimum(min);
    axisY.setManualMaximum(max);

    manager.finishUpdate();
  }
}
