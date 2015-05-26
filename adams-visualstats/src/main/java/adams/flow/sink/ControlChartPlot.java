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

import adams.core.NamedCounter;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.flow.container.ControlChartContainer;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.sink.controlchartplot.controlchartplot.AbstractControlChartPaintlet;
import adams.flow.sink.controlchartplot.controlchartplot.LinePaintlet;
import adams.flow.sink.sequenceplotter.AbstractPlotUpdater;
import adams.flow.sink.sequenceplotter.MouseClickAction;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.AbstractLimitedTickGenerator;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.MultiPaintlet;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;

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

  /** the paintlet to use for painting the XY data. */
  protected AbstractControlChartPaintlet m_Paintlet;

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

  /** for keeping track of the tokens. */
  protected NamedCounter m_Counter;

  /** the plot updater to use. */
  protected AbstractPlotUpdater m_PlotUpdater;

  /** the center paintlet. */
  protected StraightLineOverlayPaintlet m_CenterPaintlet;

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
      new LinePaintlet());

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Counter     = new NamedCounter();
    m_PlotUpdater = new SimplePlotUpdater();
    ((SimplePlotUpdater) m_PlotUpdater).setUpdateInterval(-1);
    m_CenterPaintlet = new StraightLineOverlayPaintlet();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Counter.clear();
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
    result.setLabel("observation");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(1);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
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
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
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
    if (m_Panel != null) {
      ((SequencePlotterPanel) m_Panel).getContainerManager().clear();
      ((SequencePlotterPanel) m_Panel).getMarkerContainerManager().clear();
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    SequencePlotterPanel	result;
    MultiPaintlet multi;

    result = new SequencePlotterPanel(getTitle());
    result.setPaintlet(getPaintlet());
    ActorUtils.updateFlowAwarePaintlet(result.getPaintlet(), this);
    // TODO
    multi = new MultiPaintlet();
    multi.setSubPaintlets(new XYSequencePaintlet[]{m_CenterPaintlet});
    result.setOverlayPaintlet(multi);
    ActorUtils.updateFlowAwarePaintlet(result.getOverlayPaintlet(), this);
    result.setMouseClickAction(m_MouseClickAction);
    m_AxisX.configure(result.getPlot(), Axis.BOTTOM);
    m_AxisY.configure(result.getPlot(), Axis.LEFT);
    result.setColorProvider(getColorProvider().shallowCopy());

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
    double			lower;
    double			center;
    double			upper;
    double			diff;
    String			format;
    int				i;
    SequencePlotterContainer	seqCont;
    PlotPanel			plot;
    AxisPanel 			axis;
    AbstractLimitedTickGenerator	tick;

    chartCont = (ControlChartContainer) token.getPayload();
    chartName = (String) chartCont.getValue(ControlChartContainer.VALUE_CHART);
    prepared  = (double[]) chartCont.getValue(ControlChartContainer.VALUE_PREPARED);
    lower     = (Double) chartCont.getValue(ControlChartContainer.VALUE_LOWER);
    center    = (Double) chartCont.getValue(ControlChartContainer.VALUE_CENTER);
    upper     = (Double) chartCont.getValue(ControlChartContainer.VALUE_UPPER);

    m_CenterPaintlet.setXFactor(0.0);
    m_CenterPaintlet.setYOffset(center);

    manager = ((SequencePlotterPanel) m_Panel).getContainerManager();
    manager.startUpdate();
    
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

    // be a bit intelligent about format for axes
    plot = ((SequencePlotterPanel) m_Panel).getPlot();
    axis = plot.getAxis(Axis.LEFT);
    diff = Math.abs(upper - lower);
    if (diff >= 1)
      format = "0.0";
    else
      format = ("" + diff).replaceAll("[1-9]*", "") + "0";
    axis.setNumberFormat(format);

    axis = plot.getAxis(Axis.BOTTOM);
    axis.setNumberFormat("0");
    if (axis.getTickGenerator() instanceof AbstractLimitedTickGenerator) {
      tick = (AbstractLimitedTickGenerator) axis.getTickGenerator();
      tick.setNumTicks(seq.size());
    }

    for (i = 0; i < prepared.length; i++) {
      point = new SequencePlotPoint("" + seq.size(), seq.size(), prepared[i]);
      seq.add(point);
      // update
      seqCont = new SequencePlotterContainer(chartName, prepared[i]);
      m_PlotUpdater.update((SequencePlotterPanel) getPanel(), seqCont);
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_Panel != null)
      m_PlotUpdater.update((SequencePlotterPanel) m_Panel);

    super.wrapUp();
  }
}
