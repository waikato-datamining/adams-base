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
 * SimplePlot.java
 * Copyright (C) 2015-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.ClassCrossReference;
import adams.core.NamedCounter;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.DecimalFormatString;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;
import adams.flow.core.ActorUtils;
import adams.flow.core.DataPlotUpdaterSupporter;
import adams.flow.core.Token;
import adams.flow.sink.sequenceplotter.AbstractPlotUpdater;
import adams.flow.sink.sequenceplotter.MouseClickAction;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderHandler;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CirclePaintlet;
import adams.gui.visualization.sequence.NullPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.io.StringWriter;
import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Actor for generating simple plots.<br>
 * <br>
 * See also:<br>
 * adams.flow.sink.SequencePlotter
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer[]<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, Content type, Error X, Error Y, MetaData
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
 * &nbsp;&nbsp;&nbsp;default: SimplePlot
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
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
 * <pre>-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the data.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.CirclePaintlet -meta-data-color adams.gui.visualization.sequence.metadatacolor.Dummy
 * </pre>
 *
 * <pre>-overlay-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: overlayPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the overlay data (if any).
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.NullPaintlet
 * </pre>
 *
 * <pre>-mouse-click-action &lt;adams.flow.sink.sequenceplotter.MouseClickAction&gt; (property: mouseClickAction)
 * &nbsp;&nbsp;&nbsp;The action to use for mouse clicks on the canvas.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.NullClickAction
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider in use for generating the colors for the various plots.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title for the border around the plot.
 * &nbsp;&nbsp;&nbsp;default: Plot
 * </pre>
 *
 * <pre>-axis-x &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisX)
 * &nbsp;&nbsp;&nbsp;The setup for the X axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label x -tick-generator \"adams.gui.visualization.core.axis.FancyTickGenerator -num-ticks 20\" -nth-value 2 -width 40 -custom-format 0.0
 * </pre>
 *
 * <pre>-axis-y &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisY)
 * &nbsp;&nbsp;&nbsp;The setup for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label y -tick-generator adams.gui.visualization.core.axis.FancyTickGenerator -nth-value 2 -width 60 -custom-format 0.0
 * </pre>
 *
 * <pre>-show-side-panel &lt;boolean&gt; (property: showSidePanel)
 * &nbsp;&nbsp;&nbsp;If enabled, the side panel with the plot names is visible.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-no-tool-tips &lt;boolean&gt; (property: noToolTips)
 * &nbsp;&nbsp;&nbsp;If enabled, the tool tips of the plot get suppressed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to write the plot containers to (in CSV format); does not store
 * &nbsp;&nbsp;&nbsp;the meta-data, as it can change from container to container; ignored if
 * &nbsp;&nbsp;&nbsp;pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimplePlot
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider, FileWriter, ClassCrossReference,
             TextSupplier, DataPlotUpdaterSupporter<AbstractPlotUpdater>,
             ColorProviderHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3238389451500168650L;

  /** the paintlet to use for painting the XY data. */
  protected XYSequencePaintlet m_Paintlet;

  /** the overlay paintlet to use for painting the overlays. */
  protected XYSequencePaintlet m_OverlayPaintlet;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

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

  /** whether to show the side panel. */
  protected boolean m_ShowSidePanel;

  /** whether to suppress the tooltips. */
  protected boolean m_NoToolTips;

  /** the file to save the plot containers to. */
  protected PlaceholderFile m_OutputFile;

  /** whether to use an output file. */
  protected Boolean m_UseOutputFile;

  /** the plot updater to use. */
  protected AbstractPlotUpdater m_PlotUpdater;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for generating simple plots.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "paintlet", "paintlet",
      new CirclePaintlet());

    m_OptionManager.add(
      "overlay-paintlet", "overlayPaintlet",
      new NullPaintlet());

    m_OptionManager.add(
      "mouse-click-action", "mouseClickAction",
      new NullClickAction());

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "title", "title",
      "Plot");

    m_OptionManager.add(
      "axis-x", "axisX",
      getDefaultAxisX());

    m_OptionManager.add(
      "axis-y", "axisY",
      getDefaultAxisY());

    m_OptionManager.add(
      "show-side-panel", "showSidePanel",
      true);

    m_OptionManager.add(
      "no-tool-tips", "noToolTips",
      false);

    m_OptionManager.add(
      "output", "outputFile",
      getDefaultOutputFile());
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{SequencePlotter.class};
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Counter = new NamedCounter();
    m_PlotUpdater = new SimplePlotUpdater();
    ((SimplePlotUpdater) m_PlotUpdater).setUpdateInterval(-1);
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Counter.clear();
    m_UseOutputFile = null;
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
   * Sets the overlay paintlet to use.
   *
   * @param value 	the paintlet
   */
  public void setOverlayPaintlet(XYSequencePaintlet value) {
    m_OverlayPaintlet = value;
    reset();
  }

  /**
   * Returns the overlay paintlet to use.
   *
   * @return 		the paintlet
   */
  public XYSequencePaintlet getOverlayPaintlet() {
    return m_OverlayPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayPaintletTipText() {
    return "The paintlet to use for painting the overlay data (if any).";
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
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider in use.
   *
   * @return 		the color provider
   */
  public ColorProvider getColorProvider() {
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
    result.setType(Type.DEFAULT);
    result.setLabel("x");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("0.0"));
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
    result.setType(Type.DEFAULT);
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
   * Sets whether to show the side panel with the plot names.
   *
   * @param value	true if to show
   */
  public void setShowSidePanel(boolean value) {
    m_ShowSidePanel = value;
    reset();
  }

  /**
   * Returns whether to show the side panel with the plot names.
   *
   * @return	true if to show
   */
  public boolean getShowSidePanel() {
    return m_ShowSidePanel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showSidePanelTipText() {
    return "If enabled, the side panel with the plot names is visible.";
  }

  /**
   * Sets whether to suppress the plot tool tips.
   *
   * @param value	true if to suppress
   */
  public void setNoToolTips(boolean value) {
    m_NoToolTips = value;
    reset();
  }

  /**
   * Returns whether to suppress the plot tool tips.
   *
   * @return		true if to suppress
   */
  public boolean getNoToolTips() {
    return m_NoToolTips;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noToolTipsTipText() {
    return "If enabled, the tool tips of the plot get suppressed.";
  }

  /**
   * Returns the default output file.
   *
   * @return		the file
   */
  protected PlaceholderFile getDefaultOutputFile() {
    return new PlaceholderFile(".");
  }

  /**
   * Sets the output file.
   *
   * @param value	file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return	file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return
      "The file to write the plot containers to (in CSV format); does not "
	+ "store the meta-data, as it can change from container to container; "
	+ "ignored if pointing to a directory.";
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
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

    result = new SequencePlotterPanel(getTitle());
    result.setDataPaintlet(getPaintlet());
    ActorUtils.updateFlowAwarePaintlet(result.getDataPaintlet(), this);
    result.setOverlayPaintlet(getOverlayPaintlet());
    ActorUtils.updateFlowAwarePaintlet(result.getOverlayPaintlet(), this);
    result.setMouseClickAction(m_MouseClickAction);
    m_AxisX.configure(result.getPlot(), Axis.BOTTOM);
    m_AxisY.configure(result.getPlot(), Axis.LEFT);
    result.setColorProvider(getColorProvider().shallowCopy());
    result.setSidePanelVisible(m_ShowSidePanel);
    if (m_NoToolTips) {
      result.getPlot().clearToolTipAxes();
      result.getPlot().setTipTextCustomizer(null);
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.SequencePlotterContainer.class, adams.flow.container.SequencePlotterContainer[].class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SequencePlotterContainer.class, SequencePlotterContainer[].class};
  }

  /**
   * Writes the plot container to the output file.
   *
   * @param name	the name of the plot
   * @param type	the type of plot
   * @param x		the X value
   * @param y		the Y value
   * @param dX		the numerix X value
   * @param dY		the numeric Y value
   * @param errorX	the X error(s)
   * @param errorY	the Y error(s)
   * @return		true if successfully written
   */
  protected boolean writePlotContainer(String name, ContentType type, Comparable x, Comparable y, double dX, double dY, Double[] errorX, Double[] errorY) {
    boolean		result;
    StringBuilder	line;
    int			decimals;

    result = true;

    // header?
    if (!m_OutputFile.exists()) {
      line = new StringBuilder();
      line.append("# " + toCommandLine());
      line.append("\n");
      line.append("Plot");
      line.append(",");
      line.append("Type");
      line.append(",");
      line.append("X");
      line.append(",");
      line.append("Y");
      line.append(",");
      line.append("X-numeric");
      line.append(",");
      line.append("Y-numeric");
      line.append(",");
      line.append("X-error-low");
      line.append(",");
      line.append("X-error-high");
      line.append(",");
      line.append("Y-error-low");
      line.append(",");
      line.append("Y-error-high");
      result = FileUtils.writeToFile(m_OutputFile.getAbsolutePath(), line.toString(), false);
    }

    // data
    if (result) {
      decimals = 12;
      line     = new StringBuilder();
      line.append(Utils.doubleQuote(name));
      line.append(",");
      line.append(Utils.doubleQuote(type.toString()));
      line.append(",");
      line.append(x);
      line.append(",");
      line.append(y);
      line.append(",");
      line.append(Utils.doubleToString(dX, decimals));
      line.append(",");
      line.append(Utils.doubleToString(dY, decimals));
      line.append(",");
      if ((errorX != null) && ((errorX.length == 1) || (errorX.length == 2))) {
	if (errorX.length == 1) {
	  line.append(Utils.doubleToString(errorX[0], decimals));
	  line.append(",");
	  line.append(",");
	}
	else {
	  line.append(Utils.doubleToString(errorX[0], decimals));
	  line.append(",");
	  line.append(Utils.doubleToString(errorX[1], decimals));
	  line.append(",");
	}
      }
      else {
	line.append(",");
	line.append(",");
      }
      if ((errorY != null) && ((errorY.length == 1) || (errorY.length == 2))) {
	if (errorY.length == 1) {
	  line.append(Utils.doubleToString(errorY[0], decimals));
	  line.append(",");
	  //line.append(",");
	}
	else {
	  line.append(Utils.doubleToString(errorY[0], decimals));
	  line.append(",");
	  line.append(Utils.doubleToString(errorY[1], decimals));
	  //line.append(",");
	}
      }
      else {
	line.append(",");
	//line.append(",");
      }
      result = FileUtils.writeToFile(m_OutputFile.getAbsolutePath(), line.toString(), true);
    }

    return result;
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
    SequencePlotterContainer[]	plotConts;
    SequencePlotterContainer	plotCont;
    String			plotName;
    Comparable			x;
    Comparable			y;
    double			dX;
    double			dY;
    Double[]			errorX;
    Double[]			errorY;
    ContentType			type;
    HashMap<String,Object>	meta;
    boolean			started;
    int				i;

    if (token.hasPayload(SequencePlotterContainer.class))
      plotConts = new SequencePlotterContainer[]{token.getPayload(SequencePlotterContainer.class)};
    else
      plotConts = token.getPayload(SequencePlotterContainer[].class);
    started = false;

    for (i = 0; i < plotConts.length; i++) {
      plotCont = plotConts[i];
      plotName = (String) plotCont.getValue(SequencePlotterContainer.VALUE_PLOTNAME);
      x        = (Comparable) plotCont.getValue(SequencePlotterContainer.VALUE_X);
      y        = (Comparable) plotCont.getValue(SequencePlotterContainer.VALUE_Y);
      errorX   = (Double[]) plotCont.getValue(SequencePlotterContainer.VALUE_ERROR_X);
      errorY   = (Double[]) plotCont.getValue(SequencePlotterContainer.VALUE_ERROR_Y);
      type     = (ContentType) plotCont.getValue(SequencePlotterContainer.VALUE_CONTENTTYPE);
      meta     = plotCont.getMetaData();

      switch (type) {
	case PLOT:
	  manager = ((SequencePlotterPanel) m_Panel).getContainerManager();
	  break;
	case MARKER:
	case OVERLAY:
	  continue;
	case UPDATE:
	  m_PlotUpdater.update((SequencePlotterPanel) getPanel());
	  continue;
	default:
	  throw new IllegalStateException("Unhandled plot container content type: " + type);
      }

      if (!started) {
	manager.startUpdate();
	started = true;
      }

      // find or create new plot
      if (manager.indexOf(plotName) == -1) {
	seq = new SequencePlotSequence();
	seq.setComparison(Comparison.X_AND_Y);
	seq.setID(plotName);
	cont = manager.newContainer(seq);
	manager.add(cont);
      }
      else {
	cont = manager.get(manager.indexOf(plotName));
	seq = cont.getData();
      }

      // create and add new point
      if (x == null)
	x = m_Counter.next(plotName);
      if (x instanceof Number)
	dX = ((Number) x).doubleValue();
      else
	dX = seq.putMappingX(x.toString());
      if (y instanceof Number)
	dY = ((Number) y).doubleValue();
      else
	dY = seq.putMappingY(y.toString());
      point = new SequencePlotPoint("" + seq.size(), dX, dY, errorX, errorY);
      if (meta != null)
	point.setMetaData(meta);
      seq.add(point);

      // save container?
      if (m_UseOutputFile == null)
	m_UseOutputFile = !m_OutputFile.isDirectory();
      if (m_UseOutputFile)
	writePlotContainer(plotName, type, x, y, dX, dY, errorX, errorY);

      m_PlotUpdater.update((SequencePlotterPanel) getPanel(), plotCont);
    }
  }

  /**
   * Updates the panel regardless, notifying the listeners.
   */
  @Override
  public void updatePlot() {
    if (getPanel() != null)
      m_PlotUpdater.update((SequencePlotterPanel) getPanel());
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    return "Save graph as...";
  }

  /**
   * Returns a custom file filter for the file chooser.
   *
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("CSV file", "csv");
  }

  /**
   * Supplies the text. May get called even if actor hasn't been executed yet.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    XYSequenceContainerManager	manager;
    SequencePlotSequence	seq;
    SpreadSheet 		sheet;
    CsvSpreadSheetWriter 	writer;
    StringWriter 		swriter;

    if (m_Panel == null)
      return null;

    manager = ((SequencePlotterPanel) m_Panel).getContainerManager();
    if (manager.countVisible() == 0)
      return null;

    seq   = (SequencePlotSequence) manager.getVisible(0).getData();
    sheet = seq.toSpreadSheet();
    swriter = new StringWriter();
    writer = new CsvSpreadSheetWriter();
    writer.write(sheet, swriter);

    return swriter.toString();
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractTextAndComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 4356468458332186521L;
      protected SequencePlotterPanel m_Panel;
      protected AbstractPlotUpdater m_PlotUpdater;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_PlotUpdater = new SimplePlotUpdater();
	((SimplePlotUpdater) m_PlotUpdater).setUpdateInterval(-1);
	m_Panel = new SequencePlotterPanel(getTitle());
	m_Panel.setDataPaintlet((Paintlet) OptionUtils.shallowCopy(getPaintlet()));
	ActorUtils.updateFlowAwarePaintlet(m_Panel.getDataPaintlet(), SimplePlot.this);
	m_Panel.setMouseClickAction((MouseClickAction) OptionUtils.shallowCopy(m_MouseClickAction));
	m_Panel.setOverlayPaintlet((XYSequencePaintlet) OptionUtils.shallowCopy(getOverlayPaintlet()));
	ActorUtils.updateFlowAwarePaintlet(m_Panel.getOverlayPaintlet(), SimplePlot.this);
	m_AxisX.configure(m_Panel.getPlot(), Axis.BOTTOM);
	m_AxisY.configure(m_Panel.getPlot(), Axis.LEFT);
	m_Panel.setColorProvider((ColorProvider) OptionUtils.shallowCopy(m_ColorProvider));
	m_Panel.setSidePanelVisible(m_ShowSidePanel);
	if (m_NoToolTips) {
	  m_Panel.getPlot().clearToolTipAxes();
	  m_Panel.getPlot().setTipTextCustomizer(null);
	}
	add(m_Panel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	XYSequenceContainer		cont;
	XYSequenceContainerManager	manager;
	XYSequence			seq;
	XYSequencePoint			point;
	SequencePlotterContainer	plotCont;
	SequencePlotterContainer[]	plotConts;
	String				plotName;
	Comparable			x;
	Comparable			y;
	double				dX;
	double				dY;
	Double[]			errorX;
	Double[]			errorY;
	ContentType			type;
	HashMap<String,Object>		meta;
	boolean			started;
	int				i;

	if (token.hasPayload(SequencePlotterContainer.class))
	  plotConts = new SequencePlotterContainer[]{token.getPayload(SequencePlotterContainer.class)};
	else
	  plotConts = token.getPayload(SequencePlotterContainer[].class);
	started = false;

	for (i = 0; i < plotConts.length; i++) {
	  plotCont = plotConts[i];
	  plotName = (String) plotCont.getValue(SequencePlotterContainer.VALUE_PLOTNAME);
	  x        = (Comparable) plotCont.getValue(SequencePlotterContainer.VALUE_X);
	  y        = (Comparable) plotCont.getValue(SequencePlotterContainer.VALUE_Y);
	  errorX   = (Double[]) plotCont.getValue(SequencePlotterContainer.VALUE_ERROR_X);
	  errorY   = (Double[]) plotCont.getValue(SequencePlotterContainer.VALUE_ERROR_Y);
	  type     = (ContentType) plotCont.getValue(SequencePlotterContainer.VALUE_CONTENTTYPE);
	  meta     = plotCont.getMetaData();

	  switch (type) {
	    case PLOT:
	      manager = m_Panel.getContainerManager();
	      break;
	    case OVERLAY:
	    case MARKER:
	      return;
	    case UPDATE:
	      m_PlotUpdater.update(m_Panel);
	      return;
	    default:
	      throw new IllegalStateException("Unhandled plot container content type: " + type);
	  }

	  if (!started) {
	    started = true;
	    manager.startUpdate();
	  }

	  // find or create new plot
	  if (manager.indexOf(plotName) == -1) {
	    seq = new XYSequence();
	    seq.setComparison(Comparison.X_AND_Y);
	    seq.setID(plotName);
	    cont = manager.newContainer(seq);
	    manager.add(cont);
	  }
	  else {
	    cont = manager.get(manager.indexOf(plotName));
	    seq = cont.getData();
	  }

	  // create and add new point
	  if (x == null)
	    x = m_Counter.next(plotName);
	  if (x instanceof Number)
	    dX = ((Number) x).doubleValue();
	  else
	    dX = seq.putMappingX(x.toString());
	  if (y instanceof Number)
	    dY = ((Number) y).doubleValue();
	  else
	    dY = seq.putMappingY(y.toString());
	  point = new SequencePlotPoint("" + seq.size(), dX, dY, errorX, errorY);
	  if (meta != null)
	    point.setMetaData(meta);
	  seq.add(point);

	  m_PlotUpdater.update(m_Panel, plotCont);
	}
      }
      @Override
      public void clearPanel() {
	m_Panel.getContainerManager().clear();
      }
      @Override
      public void wrapUp() {
        super.wrapUp();
        m_PlotUpdater.update(m_Panel);
      }
      @Override
      public void cleanUp() {
	m_Panel.getContainerManager().clear();
      }
      @Override
      public JComponent supplyComponent() {
	return m_Panel;
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
	return new ExtensionFileFilter("CSV file", "csv");
      }
      @Override
      public String supplyText() {
	XYSequenceContainerManager manager;
	SequencePlotSequence seq;
	SpreadSheet sheet;
	CsvSpreadSheetWriter writer;
	StringWriter swriter;

	if (m_Panel == null)
	  return null;

	manager = m_Panel.getContainerManager();
	if (manager.countVisible() == 0)
	  return null;

	seq   = (SequencePlotSequence) manager.getVisible(0).getData();
	sheet = seq.toSpreadSheet();
	swriter = new StringWriter();
	writer = new CsvSpreadSheetWriter();
	writer.write(sheet, swriter);

	return swriter.toString();
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  @Override
  public boolean displayPanelRequiresScrollPane() {
    return false;
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
