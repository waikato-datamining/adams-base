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
 * SequencePlotter.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.ClassCrossReference;
import adams.core.NamedCounter;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;
import adams.flow.core.ActorUtils;
import adams.flow.core.DataPlotUpdaterHandler;
import adams.flow.core.Token;
import adams.flow.sink.sequenceplotter.AbstractErrorPaintlet;
import adams.flow.sink.sequenceplotter.AbstractPlotUpdater;
import adams.flow.sink.sequenceplotter.AbstractSequencePostProcessor;
import adams.flow.sink.sequenceplotter.MarkerPaintlet;
import adams.flow.sink.sequenceplotter.MouseClickAction;
import adams.flow.sink.sequenceplotter.NoErrorPaintlet;
import adams.flow.sink.sequenceplotter.NoMarkers;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.PassThrough;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.SimpleTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CirclePaintlet;
import adams.gui.visualization.sequence.NullPaintlet;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Actor that plots sequences over time.<br>
 * <br>
 * See also:<br>
 * adams.flow.sink.SimplePlot
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: SequencePlotter
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
 * <pre>-comparison &lt;X|Y|X_AND_Y&gt; (property: comparisonType)
 * &nbsp;&nbsp;&nbsp;The type of comparison to use for the data points of the sequence.
 * &nbsp;&nbsp;&nbsp;default: X
 * </pre>
 * 
 * <pre>-meta-data-key &lt;java.lang.String&gt; (property: metaDataKey)
 * &nbsp;&nbsp;&nbsp;The optional meta-data key to use for comparing data points (apart from 
 * &nbsp;&nbsp;&nbsp;X&#47;Y).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the data.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.CirclePaintlet
 * </pre>
 * 
 * <pre>-overlay-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: overlayPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the overlay data (if any).
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.NullPaintlet
 * </pre>
 * 
 * <pre>-marker-paintlet &lt;adams.flow.sink.sequenceplotter.MarkerPaintlet&gt; (property: markerPaintlet)
 * &nbsp;&nbsp;&nbsp;The marker paintlet to use for painting marker overlays.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.NoMarkers
 * </pre>
 * 
 * <pre>-error-paintlet &lt;adams.flow.sink.sequenceplotter.AbstractErrorPaintlet&gt; (property: errorPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting error overlays.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.NoErrorPaintlet
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
 * <pre>-overlay-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: overlayColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider in use for generating the colors for the overlay plots.
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
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label x -tick-generator \"adams.gui.visualization.core.axis.SimpleTickGenerator -num-ticks 20\" -width 40
 * </pre>
 * 
 * <pre>-axis-y &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisY)
 * &nbsp;&nbsp;&nbsp;The setup for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label y -tick-generator adams.gui.visualization.core.axis.SimpleTickGenerator -width 60
 * </pre>
 * 
 * <pre>-show-side-panel &lt;boolean&gt; (property: showSidePanel)
 * &nbsp;&nbsp;&nbsp;If enabled, the side panel with the plot names is visible.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-plot-updater &lt;adams.flow.core.AbstractDataPlotUpdater&gt; (property: plotUpdater)
 * &nbsp;&nbsp;&nbsp;The updating strategy for the plot.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.SimplePlotUpdater
 * </pre>
 * 
 * <pre>-post-processor &lt;adams.flow.sink.sequenceplotter.AbstractSequencePostProcessor&gt; (property: postProcessor)
 * &nbsp;&nbsp;&nbsp;The post-processor to use on the sequences after a token has been added.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sequenceplotter.PassThrough
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
 * @version $Revision$
 */
public class SequencePlotter
  extends AbstractGraphicalDisplay 
  implements DisplayPanelProvider, FileWriter, ClassCrossReference, DataPlotUpdaterHandler<AbstractPlotUpdater> {

  /** for serialization. */
  private static final long serialVersionUID = 3238389451500168650L;

  /** the comparison to use for the X/Y points. */
  protected Comparison m_ComparisonType;

  /** an optional meta-data key to use in the comparison of the data points. */
  protected String m_MetaDataKey;

  /** the paintlet to use for painting the XY data. */
  protected XYSequencePaintlet m_Paintlet;

  /** the overlay paintlet to use for painting the overlays. */
  protected XYSequencePaintlet m_OverlayPaintlet;

  /** the paintlet to use for painting markers. */
  protected MarkerPaintlet m_MarkerPaintlet;

  /** the paintlet to use for painting errors. */
  protected AbstractErrorPaintlet m_ErrorPaintlet;

  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;

  /** the color provider to use for the overlays. */
  protected AbstractColorProvider m_OverlayColorProvider;

  /** the mouse click action. */
  protected MouseClickAction m_MouseClickAction;

  /** the title. */
  protected String m_Title;

  /** the options for the X axis. */
  protected AxisPanelOptions m_AxisX;

  /** the options for the Y axis. */
  protected AxisPanelOptions m_AxisY;

  /** whether to show the side panel. */
  protected boolean m_ShowSidePanel;

  /** the plot updater to use. */
  protected AbstractPlotUpdater m_PlotUpdater;

  /** the post-processor for the sequences. */
  protected AbstractSequencePostProcessor m_PostProcessor;

  /** for keeping track of the tokens. */
  protected NamedCounter m_Counter;
  
  /** the file to save the plot containers to. */
  protected PlaceholderFile m_OutputFile;
  
  /** whether to use an output file. */
  protected Boolean m_UseOutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor that plots sequences over time.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "comparison", "comparisonType",
      Comparison.X);

    m_OptionManager.add(
      "meta-data-key", "metaDataKey",
      "");

    m_OptionManager.add(
      "paintlet", "paintlet",
      new CirclePaintlet());

    m_OptionManager.add(
      "overlay-paintlet", "overlayPaintlet",
      new NullPaintlet());

    m_OptionManager.add(
      "marker-paintlet", "markerPaintlet",
      new NoMarkers());

    m_OptionManager.add(
      "error-paintlet", "errorPaintlet",
      new NoErrorPaintlet());

    m_OptionManager.add(
      "mouse-click-action", "mouseClickAction",
      new NullClickAction());

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "overlay-color-provider", "overlayColorProvider",
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
      "plot-updater", "plotUpdater",
      new SimplePlotUpdater());

    m_OptionManager.add(
      "post-processor", "postProcessor",
      new PassThrough());

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
    return new Class[]{SimplePlot.class};
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
   * Sets the type of comparison to use for the X/Y points.
   *
   * @param value	the type of comparison to use
   */
  public void setComparisonType(Comparison value) {
    m_ComparisonType = value;
    reset();
  }

  /**
   * Returns the type of comparison currently in use for the X/Y points.
   *
   * @return		the type of comparison
   */
  public Comparison getComparisonType() {
    return m_ComparisonType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparisonTypeTipText() {
    return "The type of comparison to use for the data points of the sequence.";
  }

  /**
   * Sets the optional meta-data key to use for comparing data points.
   *
   * @param value	the key, ignored in comparison if empty
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    reset();
  }

  /**
   * Returns the optional meta-data key to use for comparing data points.
   *
   * @return		the key, ignored in comparison if empty
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeyTipText() {
    return "The optional meta-data key to use for comparing data points (apart from X/Y).";
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
   * Sets the marker paintlet to use.
   *
   * @param value 	the marker paintlet
   */
  public void setMarkerPaintlet(MarkerPaintlet value) {
    m_MarkerPaintlet = value;
    reset();
  }

  /**
   * Returns the marker paintlet to use.
   *
   * @return 		the marker paintlet
   */
  public MarkerPaintlet getMarkerPaintlet() {
    return m_MarkerPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markerPaintletTipText() {
    return "The marker paintlet to use for painting marker overlays.";
  }

  /**
   * Sets the error paintlet to use.
   *
   * @param value 	the error paintlet
   */
  public void setErrorPaintlet(AbstractErrorPaintlet value) {
    m_ErrorPaintlet = value;
    reset();
  }

  /**
   * Returns the error paintlet to use.
   *
   * @return 		the error paintlet
   */
  public AbstractErrorPaintlet getErrorPaintlet() {
    return m_ErrorPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorPaintletTipText() {
    return "The paintlet to use for painting error overlays.";
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
   * Sets the color provider to use for the overlays.
   *
   * @param value 	the color provider
   */
  public void setOverlayColorProvider(AbstractColorProvider value) {
    m_OverlayColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider in use for the overlays.
   *
   * @return 		the color provider
   */
  public AbstractColorProvider getOverlayColorProvider() {
    return m_OverlayColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayColorProviderTipText() {
    return "The color provider in use for generating the colors for the overlay plots.";
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions	result;
    SimpleTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("x");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    tick = new SimpleTickGenerator();
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
    SimpleTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("y");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setWidth(60);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    tick = new SimpleTickGenerator();
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
   * Sets the plot updater to use.
   *
   * @param value 	the updater
   */
  public void setPlotUpdater(AbstractPlotUpdater value) {
    m_PlotUpdater = value;
    reset();
  }

  /**
   * Returns the plot updater in use.
   *
   * @return 		the updater
   */
  public AbstractPlotUpdater getPlotUpdater() {
    return m_PlotUpdater;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotUpdaterTipText() {
    return "The updating strategy for the plot.";
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
    result.setPaintlet(getPaintlet());
    result.setOverlayPaintlet((XYSequencePaintlet) getOverlayPaintlet().shallowCopy());
    result.setMarkerPaintlet((MarkerPaintlet) getMarkerPaintlet().shallowCopy());
    result.setErrorPaintlet((AbstractErrorPaintlet) getErrorPaintlet().shallowCopy());
    result.setMouseClickAction(m_MouseClickAction);
    m_AxisX.configure(result.getPlot(), Axis.BOTTOM);
    m_AxisY.configure(result.getPlot(), Axis.LEFT);
    result.setColorProvider(getColorProvider().shallowCopy());
    result.setOverlayColorProvider(getOverlayColorProvider().shallowCopy());
    result.setSidePanelVisible(m_ShowSidePanel);

    ActorUtils.updateFlowAwarePaintlet(result.getPaintlet(), this);
    ActorUtils.updateFlowAwarePaintlet(result.getOverlayPaintlet(), this);
    ActorUtils.updateFlowAwarePaintlet(result.getMarkerPaintlet(), this);
    ActorUtils.updateFlowAwarePaintlet(result.getErrorPaintlet(), this);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SequencePlotterContainer.class};
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
    SequencePlotSequence	seq;
    SequencePlotPoint		point;
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
    

    plotCont = (SequencePlotterContainer) token.getPayload();
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
	manager = ((SequencePlotterPanel) m_Panel).getMarkerContainerManager();
	break;
      case OVERLAY:
	manager = ((SequencePlotterPanel) m_Panel).getOverlayContainerManager();
	break;
      case UPDATE:
	m_PlotUpdater.update((SequencePlotterPanel) getPanel());
	return;
      default:
	throw new IllegalStateException("Unhandled plot container content type: " + type);
    }
    manager.startUpdate();
    
    // find or create new plot
    if (manager.indexOf(plotName) == -1) {
      seq  = new SequencePlotSequence();
      seq.setComparison(m_ComparisonType);
      seq.setMetaDataKey((m_MetaDataKey.isEmpty()) ? null : m_MetaDataKey);
      seq.setID(plotName);
      cont = manager.newContainer(seq);
      manager.add(cont);
    }
    else {
      cont = manager.get(manager.indexOf(plotName));
      seq  = (SequencePlotSequence) cont.getData();
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

    // post-process sequence?
    if (type != ContentType.MARKER) {
      if (manager.indexOf(plotName) > -1)
        m_PostProcessor.postProcess(manager, plotName);
    }
  }

  /**
   * Updates the panel regardless, notifying the listeners.
   */
  @Override
  public void updatePlot() {
    m_PlotUpdater.update((SequencePlotterPanel) getPanel());
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
    
    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 4356468458332186521L;
      protected SequencePlotterPanel m_Panel;
      @Override
      protected void initGUI() {
        super.initGUI();
        m_Panel = new SequencePlotterPanel(getTitle());
        m_Panel.setPaintlet(getPaintlet());
        m_Panel.setOverlayPaintlet(m_OverlayPaintlet);
        m_Panel.setMarkerPaintlet(getMarkerPaintlet());
        m_Panel.setErrorPaintlet(m_ErrorPaintlet);
        m_Panel.setMouseClickAction(m_MouseClickAction);
        m_AxisX.configure(m_Panel.getPlot(), Axis.BOTTOM);
        m_AxisY.configure(m_Panel.getPlot(), Axis.LEFT);
	m_Panel.setColorProvider(m_ColorProvider);
        m_Panel.setOverlayColorProvider(m_OverlayColorProvider);
	m_Panel.setSidePanelVisible(m_ShowSidePanel);
        add(m_Panel, BorderLayout.CENTER);
        ActorUtils.updateFlowAwarePaintlet(m_Panel.getPaintlet(), SequencePlotter.this);
        ActorUtils.updateFlowAwarePaintlet(m_Panel.getOverlayPaintlet(), SequencePlotter.this);
        ActorUtils.updateFlowAwarePaintlet(m_Panel.getMarkerPaintlet(), SequencePlotter.this);
        ActorUtils.updateFlowAwarePaintlet(m_Panel.getErrorPaintlet(), SequencePlotter.this);
      }
      @Override
      public void display(Token token) {
	XYSequenceContainer		cont;
	XYSequenceContainerManager	manager;
	XYSequence			seq;
	XYSequencePoint			point;
	SequencePlotterContainer	plotCont;
	String				plotName;
	Double				x;
	Double				y;
	ContentType			type;

	plotCont = (SequencePlotterContainer) token.getPayload();
	plotName = (String) plotCont.getValue(SequencePlotterContainer.VALUE_PLOTNAME);
	x        = (Double) plotCont.getValue(SequencePlotterContainer.VALUE_X);
	y        = (Double) plotCont.getValue(SequencePlotterContainer.VALUE_Y);
	type     = (ContentType) plotCont.getValue(SequencePlotterContainer.VALUE_CONTENTTYPE);

	switch (type) {
	  case PLOT:
	    manager = m_Panel.getContainerManager();
	    break;
	  case MARKER:
	    manager = m_Panel.getMarkerContainerManager();
	    break;
	  case OVERLAY:
	    manager = m_Panel.getOverlayContainerManager();
	    break;
	  default:
	    throw new IllegalStateException("Unhandled plot container content type: " + type);
	}
	manager.startUpdate();

	// find or create new plot
	if (manager.indexOf(plotName) == -1) {
	  seq  = new XYSequence();
	  seq.setComparison(m_ComparisonType);
	  seq.setID(plotName);
	  cont = manager.newContainer(seq);
	  manager.add(cont);
	}
	else {
	  cont = manager.get(manager.indexOf(plotName));
	  seq  = cont.getData();
	}

	// create and add new point
	if (x == null)
	  x = new Double(m_Counter.next(plotName));
	point = new XYSequencePoint("" + seq.size(), x, y);
	seq.add(point);

	// limit size of sequence?
	if (type != ContentType.MARKER)
	  m_PostProcessor.postProcess(manager, plotName);

	m_PlotUpdater.update(m_Panel, plotCont);
      }
      @Override
      public void clearPanel() {
	m_Panel.getContainerManager().clear();
      }
      @Override
      public void cleanUp() {
	m_Panel.getContainerManager().clear();
      }
      @Override
      public JComponent supplyComponent() {
	return m_Panel;
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
