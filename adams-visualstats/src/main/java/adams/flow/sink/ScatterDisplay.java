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
 * ScatterDisplay.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a scatter plot
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
 * &nbsp;&nbsp;&nbsp;default: ScatterDisplay
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
 * &nbsp;&nbsp;&nbsp;default: 700
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 500
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
 * <pre>-x-attribute-name &lt;adams.core.base.BaseRegExp&gt; (property: xAttributeName)
 * &nbsp;&nbsp;&nbsp;Attribute for x axis using regular expression used if set, otherwise the
 * &nbsp;&nbsp;&nbsp;index is used
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-y-attribute-name &lt;adams.core.base.BaseRegExp&gt; (property: yAttributeName)
 * &nbsp;&nbsp;&nbsp;Attribute for y axis using regular expression used if set,otherwise the
 * &nbsp;&nbsp;&nbsp;index is used
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-x-attribute &lt;java.lang.String&gt; (property: xAttribute)
 * &nbsp;&nbsp;&nbsp;Index of attribute to display on x axis, used onlyif regular expression
 * &nbsp;&nbsp;&nbsp;not set
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-y-attribute &lt;java.lang.String&gt; (property: yAttribute)
 * &nbsp;&nbsp;&nbsp;index of attribute to display on y axis, used only ifregular expression
 * &nbsp;&nbsp;&nbsp;not set
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-overlay &lt;adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;add overlays to the scatterplot
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-paintlet &lt;adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;Paintlet for plotting data
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.ScatterPaintletCircle
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterDisplay
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization */
  private static final long serialVersionUID = -1985415728904099274L;

  /**String for setting x attribute using regular expression */
  protected BaseRegExp m_XAttributeName;

  /**String for setting y attribute using regular expression */
  protected BaseRegExp m_YAttributeName;

  /** the 0-based index of the X attribute. */
  protected String m_XAttribute;

  /** the 0-based index of the Y attribute. */
  protected String m_YAttribute;

  /** scatter panel to display using the actor */
  protected ScatterPlot m_ScatPlot;

  /**Array containing scatter plot overlays */
  protected AbstractScatterPlotOverlay[] m_Overlays;

  /**Paintlet to draw original data with */
  protected AbstractScatterPlotPaintlet m_Paintlet;

  @Override
  public String globalInfo() {
    return "Actor for displaying a scatter plot of one attribute vs another.";
  }

  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    //x attribute to display, chosen using a regular expression
    m_OptionManager.add(
      "x-attribute-name", "xAttributeName",
      new BaseRegExp(""));

    //y attribute to display, chosen using a regular expression
    m_OptionManager.add(
      "y-attribute-name", "yAttributeName",
      new BaseRegExp(""));

    //x attribute to display, chosen by stating an index
    m_OptionManager.add(
      "x-attribute", "xAttribute",
      "1");

    //y attribte to display, chosen by stating an index
    m_OptionManager.add(
      "y-attribute", "yAttribute",
      "1");

    //Overlays to display
    m_OptionManager.add(
      "overlay", "overlays",
      new AbstractScatterPlotOverlay[]{});

    //paintlet to use for plotting
    m_OptionManager.add(
      "paintlet", "paintlet",
      new ScatterPaintletCircle());
  }

  /**
   * Set the overlays to be drawn on the scatter plot
   * @param over		Array containing overlays
   */
  public void setOverlays(AbstractScatterPlotOverlay[] over) {
    m_Overlays = over;
    reset();
  }

  /**
   * Get the overlays to be drawn on the scatter plot
   * @return		Array containing the overlays
   */
  public AbstractScatterPlotOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for the overlays property.
   *
   * @return         tip text for this property
   */
  public String overlaysTipText() {
    return "add overlays to the scatterplot";
  }

  /**
   * Set the paintlet to draw the data points with
   * @param pain		paintlet used
   */
  public void setPaintlet(AbstractScatterPlotPaintlet pain) {
    m_Paintlet = pain;
    reset();
  }

  /**
   * get the paintlet used to draw the data with
   * @return		Paintlet used
   */
  public AbstractScatterPlotPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for the paintlet property
   * @return		tip text for this property
   */
  public String paintletTipText() {
    return "Paintlet for plotting data";
  }

  @Override
  protected int getDefaultWidth() {
    return 1200;
  }

  @Override
  protected int getDefaultHeight() {
    return 500;
  }

  /**
   * Set the x attribute on the scatter plot using a regular expression
   * @param val		name of attribute for axis
   */
  public void setXAttributeName(BaseRegExp val) {
    m_XAttributeName = val;
    reset();
  }
  /**
   * Returns the regular expression for the attribute to be displayed on the x axis
   * @return		name of attribute
   */
  public BaseRegExp getXAttributeName() {
    return m_XAttributeName;
  }

  /**
   * Tip text to display for x attribute regular expression
   * @return		String to display
   */
  public String xAttributeNameTipText() {
    return "Attribute for x axis using regular expression used if set, " +
      "otherwise the index is used";
  }

  /**
   * Set the y attribute on the scatter plot using a regular expression
   * @param val		regular expression for attribute on y axis
   */
  public void setYAttributeName(BaseRegExp val) {
    m_YAttributeName = val;
    reset();
  }
  /**
   * Returns the attribute to be displayed on the y axis
   * @return		regular expression for choosing attribute
   */
  public BaseRegExp getYAttributeName() {
    return m_YAttributeName;
  }

  /**
   * Tip text to display for y attribute regular expression
   * @return		String to display
   */
  public String yAttributeNameTipText() {
    return "Attribute for y axis using regular expression used if set," +
      "otherwise the index is used";
  }

  /**
   * Sets the index of the attribute to display on x axis
   * @param val		1-based index of attribute for x axis
   */
  public void setXAttribute(String val) {
    m_XAttribute = val;
    reset();
  }

  /**
   * Get the index of the attribute displayed on the x axis
   * @return		1-based index of attribute to be displayed
   */
  public String getXAttribute() {
    return m_XAttribute;
  }

  /**
   * Returns the tip text for x attribute set using index.
   *
   * @return         Set the attribute to be used for the x axis using a 1-based index
   */
  public String xAttributeTipText() {
    return "Index of attribute to display on x axis, used only" +
      "if regular expression not set";
  }

  /**
   * Set the attribute to be displayed on the y axis using an index
   * @param val		1-based index of attribute to be displayed on y axis
   */
  public void setYAttribute(String val) {
    m_YAttribute = val;
    reset();
  }

  /**
   * Get the index of the attribute displayed on the y axis
   * @return		The 1-based index of the attribute on the y axis
   */
  public String getYAttribute() {
    return m_YAttribute;
  }

  /**
   * Returns the tip text setting the y attribute using an index
   *
   * @return         1-based index for attribute to display on y axis
   */
  public String yAttributeTipText() {
    return "index of attribute to display on y axis, used only if" +
      "regular expression not set";
  }

  @Override
  public void clearPanel() {
    if (m_ScatPlot != null) {
      SpreadSheet temp = new DefaultSpreadSheet();
      m_ScatPlot.setData(temp);
    }
  }

  @Override
  protected BasePanel newPanel() {
    m_ScatPlot = new ScatterPlot();
    return m_ScatPlot;
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_ScatPlot.setX_IndexReg(m_XAttributeName);
    m_ScatPlot.setX_Ind(new Index(m_XAttribute));
    m_ScatPlot.setY_IndexReg(m_YAttributeName);
    m_ScatPlot.setY_Ind(new Index(m_YAttribute));
    m_ScatPlot.setData((SpreadSheet) token.getPayload());
    m_ScatPlot.setOverlays(m_Overlays);
    m_ScatPlot.setPaintlet(m_Paintlet);
    m_ScatPlot.reset();
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

    result = new AbstractComponentDisplayPanel("Histogram") {
      private static final long serialVersionUID = 4360182045245637304L;
      protected ScatterPlot m_ScatPlot;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_ScatPlot = new ScatterPlot();
	add(m_ScatPlot, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_ScatPlot.setX_IndexReg(m_XAttributeName);
	m_ScatPlot.setX_Ind(new Index(m_XAttribute));
	m_ScatPlot.setY_IndexReg(m_YAttributeName);
	m_ScatPlot.setY_Ind(new Index(m_YAttribute));
	m_ScatPlot.setData((SpreadSheet) token.getPayload());
	m_ScatPlot.setOverlays(m_Overlays);
	m_ScatPlot.setPaintlet(m_Paintlet);
	m_ScatPlot.reset();
      }
      @Override
      public void clearPanel() {
	SpreadSheet temp = new DefaultSpreadSheet();
	m_ScatPlot.setData(temp);
      }
      @Override
      public JComponent supplyComponent() {
	return m_ScatPlot;
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