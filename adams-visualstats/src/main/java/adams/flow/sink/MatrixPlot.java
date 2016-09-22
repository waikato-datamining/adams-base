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
 * MatrixPlot.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay;
import adams.gui.visualization.stats.scatterplot.Matrix;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a matrix of scatter plots
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MatrixPlot
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
 * <pre>-plot-size &lt;int&gt; (property: plotSize)
 * &nbsp;&nbsp;&nbsp;Set the size for each plot in the matrix
 * &nbsp;&nbsp;&nbsp;default: 100
 * </pre>
 *
 * <pre>-overlay &lt;adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;Overlays to display on each scatterplot
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-paintlet &lt;adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;Paintlet to display the data
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.ScatterPaintletCircle
 * </pre>
 *
 * <pre>-percent &lt;int&gt; (property: percent)
 * &nbsp;&nbsp;&nbsp;percentage of sample for sub-sample
 * &nbsp;&nbsp;&nbsp;default: 100
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class MatrixPlot
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization */
  private static final long serialVersionUID = -679565614211767555L;

  /**matrix to display */
  protected Matrix m_Plot;

  /**Size of each scatterplot */
  protected int m_PlotSize;

  /**Paintlet for plotting the data on each scatter plot */
  protected AbstractScatterPlotPaintlet m_Paintlet;

  /**Array of overlays for each scatterplot */
  protected AbstractScatterPlotOverlay[] m_Overlays;

  /**Percentage of sample for subsample */
  protected int m_Percent;

  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    //size of each plot
    m_OptionManager.add(
      "plot-size", "plotSize",
      100);

    //overlays for each plot
    m_OptionManager.add(
      "overlay", "overlays",
      new AbstractScatterPlotOverlay[]{});

    //paintlet for drawing each plot
    m_OptionManager.add(
      "paintlet", "paintlet",
      new ScatterPaintletCircle());

    //percent of instances to work with
    m_OptionManager.add(
      "percent", "percent", 100);
  }

  @Override
  public void clearPanel() {
    if (m_Plot != null) {
      SpreadSheet temp = new DefaultSpreadSheet();
      m_Plot.setData(temp);
    }
  }

  @Override
  protected BasePanel newPanel() {
    m_Plot = new Matrix();
    return m_Plot;
  }

  @Override
  protected void display(Token token) {
    m_Plot.setPlotSize(m_PlotSize);
    m_Plot.setOverlays(m_Overlays);
    m_Plot.setPaintlet(m_Paintlet);
    m_Plot.setPercent(m_Percent);
    m_Plot.setData((SpreadSheet) token.getPayload());
    m_Plot.reset();
  }

  @Override
  public String globalInfo() {
    return "Actor for displaying a matrix of scatter plots";
  }

  @Override
  protected int getDefaultHeight() {
    return 800;
  }

  @Override
  protected int getDefaultWidth() {
    return 1600;
  }

  /**
   * Sets the paintlet for each scatter plot
   * @param val			Paintlet to use
   */
  public void setPaintlet(AbstractScatterPlotPaintlet val) {
    m_Paintlet = val;
    reset();
  }

  /**
   * Gets the paintlet to use for each scatter plot
   * @return				Paintlet to use
   */
  public AbstractScatterPlotPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * returns a string for the paintlet option
   * @return
   */
  public String paintletTipText() {
    return "Paintlet to display the data";
  }

  /**
   * Set the overlays for each scatterplot in the matrix
   * @param val			Array of overlays
   */
  public void setOverlays(AbstractScatterPlotOverlay[] val) {
    m_Overlays = val;
    reset();
  }

  /**
   * Get the overlays for each scatterplot
   * @return			Array of overlays
   */
  public AbstractScatterPlotOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * return a string for the overlays option
   * @return			String for the property
   */
  public String overlaysTipText() {
    return "Overlays to display on each scatterplot";
  }

  /**
   * Set the size of each scatter plot
   * @param val			Size of each plot in pixels
   */
  public void setPlotSize(int val) {
    m_PlotSize = val;
    reset();
  }

  /**
   * Get the size of each scatter plot
   * @return			Size in pixels
   */
  public int getPlotSize() {
    return m_PlotSize;
  }

  /**
   * return a string for the plot size option
   * @return			string for the property
   */
  public String plotSizeTipText() {
    return "Set the size for each plot in the matrix";
  }

  /**
   * Set the percent of the instances for each attribute to work with
   * @param val			int percent of instances
   */
  public void setPercent(int val) {
    m_Percent = val;
    reset();
  }

  /**
   * Get the percent of the the instances in each attribute to work with
   * @return			int percent of instances
   */
  public int getPercent() {
    return m_Percent;
  }

  /**
   * Tip text for the percent of instances property
   * @return			String describing the property
   */
  public String percentTipText() {
    return "percentage of sample for sub-sample";
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel("MatrixPlot") {
      private static final long serialVersionUID = 4360182045245637304L;
      protected Matrix m_Plot;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_Plot = new Matrix();
	add(m_Plot, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_Plot.setPlotSize(m_PlotSize);
	m_Plot.setOverlays(m_Overlays);
	m_Plot.setPaintlet(m_Paintlet);
	m_Plot.setPercent(m_Percent);
	m_Plot.setData((SpreadSheet) token.getPayload());
	m_Plot.reset();
      }
      @Override
      public void clearPanel() {
	SpreadSheet temp = new DefaultSpreadSheet();
	m_Plot.setData(temp);
      }
      @Override
      public JComponent supplyComponent() {
	return m_Plot;
      }
      @Override
      public void cleanUp() {
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
}