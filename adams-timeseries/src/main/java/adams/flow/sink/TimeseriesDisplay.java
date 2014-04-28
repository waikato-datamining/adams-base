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
 * TimeseriesDisplay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JComponent;

import adams.data.timeseries.PeriodicityType;
import adams.data.timeseries.Timeseries;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.PeriodicityTickGenerator;
import adams.gui.visualization.core.axis.SimpleTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.timeseries.TimeseriesContainer;
import adams.gui.visualization.timeseries.TimeseriesContainerManager;
import adams.gui.visualization.timeseries.TimeseriesExplorer;

/**
 <!-- globalinfo-start -->
 * Actor that displays timeseries.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: TimeseriesDisplay
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
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
 * <pre>-axis-x &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisX)
 * &nbsp;&nbsp;&nbsp;The setup for the X axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label time -tick-generator adams.gui.visualization.core.axis.PeriodicityTickGenerator -width 40
 * </pre>
 * 
 * <pre>-axis-y &lt;adams.gui.visualization.core.AxisPanelOptions&gt; (property: axisY)
 * &nbsp;&nbsp;&nbsp;The setup for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.AxisPanelOptions -label value -tick-generator adams.gui.visualization.core.axis.SimpleTickGenerator -width 60
 * </pre>
 * 
 * <pre>-periodicity &lt;NONE|YEARLY|QUARTERLY|MONTHLY|WEEKLY|DAILY|HALF_DAILY|HOURLY|HALF_HOURLY|PER_MINUTE&gt; (property: periodicity)
 * &nbsp;&nbsp;&nbsp;The type of periodicity to use for the background.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 * 
 * <pre>-color-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider in use for generating the colors for the various plots.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 * <pre>-show-side-panel &lt;boolean&gt; (property: showSidePanel)
 * &nbsp;&nbsp;&nbsp;If enabled, the side panel with the list of loaded spectra gets displayed.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-zoom-overview &lt;boolean&gt; (property: zoomOverview)
 * &nbsp;&nbsp;&nbsp;If enabled, a zoom overview panel gets displayed as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesDisplay
  extends AbstractGraphicalDisplay 
  implements DisplayPanelProvider {

  /**
   * Panel to be used in {@link DisplayPanelManager} sink.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  protected class DisplayPanel
    extends AbstractComponentDisplayPanel
    implements MergeableDisplayPanel<DisplayPanel> {

    private static final long serialVersionUID = 7384093089760722339L;

    protected TimeseriesExplorer m_Panel;

    protected DisplayPanel(String name) {
      super(name);
    }

    @Override
    protected void initGUI() {
      super.initGUI();
      setLayout(new BorderLayout());
      m_Panel = new TimeseriesExplorer();
      ((TimeseriesContainerManager) m_Panel.getContainerManager()).setAllowRemoval(false);
      ((TimeseriesContainerManager) m_Panel.getContainerManager()).setReloadable(false);
      ((TimeseriesContainerManager) m_Panel.getContainerManager()).setColorProvider(m_ColorProvider.shallowCopy());
      m_Panel.getTimeseriesPanel().setSidePanelVisible(m_ShowSidePanel);
      m_Panel.setZoomOverviewPanelVisible(m_ZoomOverview);
      m_AxisX.configure(m_Panel.getTimeseriesPanel().getPlot(), Axis.BOTTOM);
      m_AxisY.configure(m_Panel.getTimeseriesPanel().getPlot(), Axis.LEFT);
      m_Panel.getTimeseriesPanel().getPeriodicityPaintlet().setPeriodicity(m_Periodicity);
      if (m_Panel.getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).getTickGenerator() instanceof PeriodicityTickGenerator)
	((PeriodicityTickGenerator) m_Panel.getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).getTickGenerator()).setPeriodicity(m_Periodicity);
      add(m_Panel, BorderLayout.CENTER);
    }

    @Override
    public void display(Token token) {
      Timeseries		ts;
      TimeseriesContainer	cont;

      ts   = (Timeseries) token.getPayload();
      cont = ((TimeseriesContainerManager) m_Panel.getContainerManager()).newContainer(ts);
      m_Panel.getContainerManager().add(cont);
    }

    @Override
    public void cleanUp() {
      m_Panel.getContainerManager().clear();
    }

    @Override
    public void clearPanel() {
      m_Panel.getContainerManager().clear();
    }

    @Override
    public JComponent supplyComponent() {
      return m_Panel;
    }

    @Override
    public void mergeWith(DisplayPanel other) {
      List<TimeseriesContainer>		list;
      
      m_Panel.getContainerManager().startUpdate();
      list = other.m_Panel.getContainerManager().getAll();
      for (TimeseriesContainer cont: list) {
	cont.setColor(Color.WHITE);
	m_Panel.getContainerManager().add(cont);
      }
      m_Panel.getContainerManager().finishUpdate();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = 2505818295695863125L;

  /** the options for the X axis. */
  protected AxisPanelOptions m_AxisX;

  /** the options for the Y axis. */
  protected AxisPanelOptions m_AxisY;

  /** the periodicity to use. */
  protected PeriodicityType m_Periodicity;

  /** the color provider to use. */
  protected AbstractColorProvider m_ColorProvider;
  
  /** whether to show the side panel or not. */
  protected boolean m_ShowSidePanel;

  /** whether to display the zoom overview. */
  protected boolean m_ZoomOverview;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor that displays timeseries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "axis-x", "axisX",
	    getDefaultAxisX());

    m_OptionManager.add(
	    "axis-y", "axisY",
	    getDefaultAxisY());

    m_OptionManager.add(
	    "periodicity", "periodicity",
	    PeriodicityType.NONE);

    m_OptionManager.add(
	    "color-provider", "colorProvider",
	    new DefaultColorProvider());

    m_OptionManager.add(
	    "show-side-panel", "showSidePanel",
	    true);

    m_OptionManager.add(
	    "zoom-overview", "zoomOverview",
	    false);
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 1000;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions		result;
    PeriodicityTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("time");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    tick = new PeriodicityTickGenerator();
    tick.setPeriodicity(PeriodicityType.NONE);
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
    result.setLabel("value");
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
   * Sets the type of periodicity to use for the background.
   *
   * @param value	the type
   */
  public void setPeriodicity(PeriodicityType value) {
    m_Periodicity = value;
    reset();
  }

  /**
   * Returns the type of periodicity to use for the background.
   *
   * @return		the type
   */
  public PeriodicityType getPeriodicity() {
    return m_Periodicity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String periodicityTipText() {
    return "The type of periodicity to use for the background.";
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
   * Sets whether to show the side panel or not.
   *
   * @param value 	if true the side panel gets displayed
   */
  public void setShowSidePanel(boolean value) {
    m_ShowSidePanel = value;
    reset();
  }

  /**
   * Returns whether to show the side panel or not.
   *
   * @return 		true if the side panel gets displayed
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
    return "If enabled, the side panel with the list of loaded spectra gets displayed.";
  }

  /**
   * Sets whether to display the zoom overview.
   *
   * @param value 	if true then the zoom overview will get displayed
   */
  public void setZoomOverview(boolean value) {
    m_ZoomOverview = value;
    reset();
  }

  /**
   * Returns whether the zoom overview gets displayed.
   *
   * @return 		true if the zoom overview gets displayed
   */
  public boolean getZoomOverview() {
    return m_ZoomOverview;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomOverviewTipText() {
    return "If enabled, a zoom overview panel gets displayed as well.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((TimeseriesExplorer) m_Panel).getContainerManager().clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    TimeseriesExplorer		result;

    result = new TimeseriesExplorer();
    ((TimeseriesContainerManager) result.getContainerManager()).setAllowRemoval(false);
    ((TimeseriesContainerManager) result.getContainerManager()).setReloadable(false);
    ((TimeseriesContainerManager) result.getContainerManager()).setColorProvider(m_ColorProvider.shallowCopy());
    result.getTimeseriesPanel().setSidePanelVisible(m_ShowSidePanel);
    result.setZoomOverviewPanelVisible(m_ZoomOverview);
    m_AxisX.configure(result.getTimeseriesPanel().getPlot(), Axis.BOTTOM);
    m_AxisY.configure(result.getTimeseriesPanel().getPlot(), Axis.LEFT);
    result.getTimeseriesPanel().getPeriodicityPaintlet().setPeriodicity(m_Periodicity);
    if (result.getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).getTickGenerator() instanceof PeriodicityTickGenerator)
      ((PeriodicityTickGenerator) result.getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).getTickGenerator()).setPeriodicity(m_Periodicity);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.timeseries.Timeseries.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Timeseries.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    TimeseriesContainer	cont;

    cont = ((TimeseriesContainerManager) ((TimeseriesExplorer) m_Panel).getContainerManager()).newContainer((Timeseries) token.getPayload());
    ((TimeseriesExplorer) m_Panel).getContainerManager().add(cont);
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public DisplayPanel createDisplayPanel(Token token) {
    DisplayPanel	result;

    result = new DisplayPanel(getClass().getSimpleName());
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
}
