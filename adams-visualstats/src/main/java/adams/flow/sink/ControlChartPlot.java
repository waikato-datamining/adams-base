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
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spc.ControlChart;
import adams.data.spc.IndividualsControlChart;
import adams.data.spc.Limits;
import adams.data.spc.MatrixControlChart;
import adams.data.spc.NullViolations;
import adams.data.spc.ViolationFinder;
import adams.data.statistics.StatUtils;
import adams.flow.container.ControlChartContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;
import adams.flow.sink.controlchartplot.AbstractControlChartPaintlet;
import adams.flow.sink.controlchartplot.ChartPaintlet;
import adams.flow.sink.controlchartplot.LimitPaintlet;
import adams.flow.sink.sequenceplotter.AbstractSequencePostProcessor;
import adams.flow.sink.sequenceplotter.MarkerPaintlet;
import adams.flow.sink.sequenceplotter.MouseClickAction;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.PassThrough;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.VerticalMarkers;
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
 * Actor for generating control chart plots.<br>
 * The plot needs to be initialized with a class adams.flow.container.ControlChartContainer. After that, individual numbers or arrays, depending on the control chart algorithm used, can be plotted. The last limits encountered (lower&#47;center&#47;upper) are used for all subsequent values. A vertical indicator is used to separate the data that was used for determining the limits and all subsequent data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ControlChartContainer<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Float<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br>
 * &nbsp;&nbsp;&nbsp;double[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Float[]<br>
 * &nbsp;&nbsp;&nbsp;float[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer[]<br>
 * &nbsp;&nbsp;&nbsp;int[]<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.ControlChartContainer: Algor, Chart, Data, Prepared, Limits
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ControlChartPlot
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 350
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-violation-finder &lt;adams.data.spc.ViolationFinder&gt; (property: violationFinder)
 * &nbsp;&nbsp;&nbsp;The algorithm for locating violations.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spc.NullViolations
 * </pre>
 * 
 * <pre>-paintlet &lt;adams.flow.sink.controlchartplot.AbstractControlChartPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.controlchartplot.ChartPaintlet
 * </pre>
 * 
 * <pre>-limit-paintlet &lt;adams.flow.sink.controlchartplot.AbstractControlChartPaintlet&gt; (property: limitPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the limits.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.controlchartplot.LimitPaintlet
 * </pre>
 * 
 * <pre>-separator-paintlet &lt;adams.flow.sink.sequenceplotter.MarkerPaintlet&gt; (property: separatorPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for separating data used for initializing the limits 
 * &nbsp;&nbsp;&nbsp;and subsequent data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.VerticalMarkers
 * </pre>
 * 
 * <pre>-mouse-click-action &lt;adams.flow.sink.sequenceplotter.MouseClickAction&gt; (property: mouseClickAction)
 * &nbsp;&nbsp;&nbsp;The action to use for mouse clicks on the canvas.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.NullClickAction
 * </pre>
 * 
 * <pre>-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider in use for generating the colors for the various plots.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title for the border around the plot.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-axis-x &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisX)
 * &nbsp;&nbsp;&nbsp;The setup for the X axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label sample -tick-generator \"adams.gui.visualization.core.axis.FancyTickGenerator -num-ticks 20\" -nth-value 1 -width 40 -top-margin 0.05 -bottom-margin 0.05 -custom-format 0
 * </pre>
 * 
 * <pre>-axis-y &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisY)
 * &nbsp;&nbsp;&nbsp;The setup for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label y -tick-generator adams.gui.visualization.core.axis.FancyTickGenerator -nth-value 2 -width 60 -top-margin 0.05 -bottom-margin 0.05 -custom-format 0.0
 * </pre>
 * 
 * <pre>-post-processor &lt;adams.flow.sink.sequenceplotter.AbstractSequencePostProcessor&gt; (property: postProcessor)
 * &nbsp;&nbsp;&nbsp;The post-processor to use on the sequences after a token has been added.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ControlChartPlot
  extends AbstractGraphicalDisplay {

  /** for serialization. */
  private static final long serialVersionUID = 3238389451500168650L;

  /** for locating violations. */
  protected ViolationFinder m_ViolationFinder;

  /** the paintlet to use for painting the chart data. */
  protected AbstractControlChartPaintlet m_Paintlet;

  /** the paintlet to use for painting the limits. */
  protected AbstractControlChartPaintlet m_LimitPaintlet;

  /** the paintlet to use for separating initialization data and subsequent data. */
  protected MarkerPaintlet m_SeparatorPaintlet;

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

  /** the post-processor for the sequences. */
  protected AbstractSequencePostProcessor m_PostProcessor;

  /** the chart algorithm to use. */
  protected ControlChart m_Chart;

  /** the chart name. */
  protected String m_ChartName;

  /** the limits to use. */
  protected Limits m_Limits;

  /** for keeping track of the tokens. */
  protected NamedCounter m_Counter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Actor for generating control chart plots.\n"
	+ "The plot needs to be initialized with a " + ControlChartContainer.class + ". "
	+ "After that, individual numbers or arrays, depending on the control chart "
	+ "algorithm used, can be plotted. The last limits encountered "
	+ "(lower/center/upper) are used for all subsequent values. A vertical "
        + "indicator is used to separate the data that was used for determining "
	+ "the limits and all subsequent data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "violation-finder", "violationFinder",
      new NullViolations());

    m_OptionManager.add(
      "paintlet", "paintlet",
      new ChartPaintlet());

    m_OptionManager.add(
      "limit-paintlet", "limitPaintlet",
      new LimitPaintlet());

    m_OptionManager.add(
      "separator-paintlet", "separatorPaintlet",
      new VerticalMarkers());

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

    m_OptionManager.add(
      "post-processor", "postProcessor",
      new PassThrough());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Counter = new NamedCounter();
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
   * Sets the algorithm for locating violations.
   *
   * @param value	the algorithm
   */
  public void setViolationFinder(ViolationFinder value) {
    m_ViolationFinder = value;
    reset();
  }

  /**
   * Returns the algorithm for locating violations.
   *
   * @return		the algorithm
   */
  public ViolationFinder getViolationFinder() {
    return m_ViolationFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String violationFinderTipText() {
    return "The algorithm for locating violations.";
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
   * Sets the paintlet to use for separating intialization data and
   * subsequent data.
   *
   * @param value 	the paintlet
   */
  public void setSeparatorPaintlet(MarkerPaintlet value) {
    m_SeparatorPaintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use for separating intialization data and
   * subsequent data.
   *
   * @return 		the paintlet
   */
  public MarkerPaintlet getSeparatorPaintlet() {
    return m_SeparatorPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorPaintletTipText() {
    return "The paintlet to use for separating data used for initializing the limits and subsequent data.";
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
   * Sets the post-processor for the sequences.
   *
   * @param value 	the post-processor
   */
  public void setPostProcessor(AbstractSequencePostProcessor value) {
    m_PostProcessor = value;
    reset();
  }

  /**
   * Returns the limit on the number of data points per sequence.
   *
   * @return 		the limit
   */
  public AbstractSequencePostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to use on the sequences after a token has been added.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "violoationFinder", m_ViolationFinder, "violations: ");

    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((SequencePlotterPanel) m_Panel).getContainerManager().clear();
    m_Chart  = null;
    m_Limits = null;
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
    result.setMarkerPaintlet((MarkerPaintlet) m_SeparatorPaintlet.shallowCopy());
    result.setMouseClickAction(m_MouseClickAction);
    m_AxisX.configure(result.getPlot(), Axis.BOTTOM);
    m_AxisY.configure(result.getPlot(), Axis.LEFT);
    result.setColorProvider(getColorProvider().shallowCopy());
    result.getContainerList().setAllowSearch(false);

    ActorUtils.updateFlowAwarePaintlet(result.getPaintlet(), this);
    ActorUtils.updateFlowAwarePaintlet(result.getMarkerPaintlet(), this);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{
      ControlChartContainer.class,
      Double.class, Float.class, Integer.class,
      Double[].class, double[].class, Float[].class, float[].class, Integer[].class, int[].class
    };
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
    XYSequenceContainerManager	markerManager;
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
    Compatibility		comp;
    Number[]			numberArray;
    double			value;
    int				x;

    manager = ((SequencePlotterPanel) m_Panel).getContainerManager();
    manager.startUpdate();

    if (token.getPayload() instanceof ControlChartContainer) {
      // extract data from container
      chartCont   = (ControlChartContainer) token.getPayload();
      chartName   = (String) chartCont.getValue(ControlChartContainer.VALUE_CHART);
      prepared    = (double[]) chartCont.getValue(ControlChartContainer.VALUE_PREPARED);
      limits      = (Limits[]) chartCont.getValue(ControlChartContainer.VALUE_LIMITS);
      violations  = new TIntHashSet(m_ViolationFinder.find(prepared, limits));
      m_Chart     = (ControlChart) chartCont.getValue(ControlChartContainer.VALUE_ALGORITHM);
      if (chartName == null)
	chartName = m_Chart.getName();
      m_ChartName = chartName;
      m_Limits    = limits[limits.length - 1];

      // find or create new plot
      if (manager.indexOf(m_ChartName) == -1) {
	seq = new SequencePlotSequence();
	seq.setComparison(Comparison.X_AND_Y);
	seq.setID(m_ChartName);
	cont = manager.newContainer(seq);
	manager.add(cont);
      }
      else {
	cont = manager.get(manager.indexOf(m_ChartName));
	seq = cont.getData();
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
	meta = new HashMap<>();
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
	x     = m_Counter.next(m_ChartName);
	point = new SequencePlotPoint("" + x, x, prepared[i]);
	point.setMetaData(meta);
	seq.add(point);
      }

      // add marker
      markerManager = ((SequencePlotterPanel) m_Panel).getMarkerContainerManager();
      markerManager.startUpdate();
      if (markerManager.indexOf(m_ChartName) == -1) {
	seq = new SequencePlotSequence();
	seq.setComparison(Comparison.X_AND_Y);
	seq.setID(m_ChartName);
	cont = markerManager.newContainer(seq);
	markerManager.add(cont);
      }
      else {
	cont = markerManager.get(manager.indexOf(m_ChartName));
	seq = cont.getData();
      }
      x     = m_Counter.current(m_ChartName);
      point = new SequencePlotPoint("" + x, x, 0);
      seq.add(point);
      markerManager.finishUpdate();

      // update min/max
      axisY = ((SequencePlotterPanel) m_Panel).getPlot().getAxis(Axis.LEFT);
      axisY.setManualMinimum(min);
      axisY.setManualMaximum(max);

      // post-process
      m_PostProcessor.postProcess(manager, m_ChartName);
    }
    else if (m_Chart instanceof IndividualsControlChart) {
      comp = new Compatibility();
      if (comp.isCompatible(
	new Class[]{token.getPayload().getClass()},
	new Class[]{Double.class, Float.class, Integer.class})) {

	// determine violations
	violations = new TIntHashSet(m_ViolationFinder.find(new double[]{((Number) token.getPayload()).doubleValue()}, new Limits[]{m_Limits}));

	// add data
	cont = manager.get(manager.indexOf(m_ChartName));
	seq = cont.getData();
	meta = new HashMap<>();
	meta.put("lower", m_Limits.getLower());
	meta.put("center", m_Limits.getCenter());
	meta.put("upper", m_Limits.getUpper());
	meta.put("violation", violations.size() > 0);
	value    = ((Number) token.getPayload()).doubleValue();
	prepared = ((IndividualsControlChart) m_Chart).prepare(new Double[]{value});
	x        = m_Counter.next(m_ChartName);
	point    = new SequencePlotPoint("" + x, x, prepared[0]);
	point.setMetaData(meta);
	seq.add(point);

	// post-process
	m_PostProcessor.postProcess(manager, m_ChartName);
      }
      else {
	throw new IllegalArgumentException(m_Chart.getName() + " cannot process class: " + Utils.classToString(token.getPayload().getClass()));
      }
    }
    else if (m_Chart instanceof MatrixControlChart) {
      comp = new Compatibility();
      if (comp.isCompatible(
	new Class[]{token.getPayload().getClass()},
	new Class[]{Double[].class, double[].class, Float[].class, float[].class, Integer[].class, int[].class})) {

	if (token.getPayload() instanceof Double[])
	  numberArray = (Double[]) token.getPayload();
	else if (token.getPayload() instanceof double[])
	  numberArray = StatUtils.toNumberArray((double[]) token.getPayload());
	else if (token.getPayload() instanceof Float[])
	  numberArray = (Float[]) token.getPayload();
	else if (token.getPayload() instanceof float[])
	  numberArray = StatUtils.toNumberArray((float[]) token.getPayload());
	else if (token.getPayload() instanceof Integer[])
	  numberArray = (Integer[]) token.getPayload();
	else if (token.getPayload() instanceof int[])
	  numberArray = StatUtils.toNumberArray((int[]) token.getPayload());
	else
	  throw new IllegalStateException("Unhandled token.getPayload() type: " + Utils.classToString(token.getPayload().getClass()));

	// determine violations
	limits = new Limits[numberArray.length];
	for (i = 0; i < limits.length; i++)
	  limits[i] = m_Limits;
	violations = new TIntHashSet(m_ViolationFinder.find(StatUtils.toDoubleArray(numberArray), limits));

	// add data
	cont     = manager.get(manager.indexOf(m_ChartName));
	seq      = cont.getData();
	prepared = ((MatrixControlChart) m_Chart).prepare(new Number[][]{numberArray});
	meta     = new HashMap<>();
	meta.put("lower", m_Limits.getLower());
	meta.put("center", m_Limits.getCenter());
	meta.put("upper", m_Limits.getUpper());
	meta.put("violation", violations.contains(i));
	x     = m_Counter.next(m_ChartName);
	point = new SequencePlotPoint("" + x, x, prepared[0]);
	point.setMetaData(meta);
	seq.add(point);

	// post-process
	m_PostProcessor.postProcess(manager, m_ChartName);
      }
      else {
	throw new IllegalArgumentException(m_Chart.getName() + " cannot process class: " + Utils.classToString(token.getPayload().getClass()));
      }
    }
    else {
      throw new IllegalArgumentException("Unhandled data: " + Utils.classToString(token.getPayload().getClass()));
    }

    manager.finishUpdate();
  }
}
