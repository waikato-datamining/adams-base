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
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;
import adams.gui.visualization.stats.scatterplot.action.MouseClickAction;
import adams.gui.visualization.stats.scatterplot.action.NullClickAction;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a scatter plot of one attribute vs another.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ScatterDisplay
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
 * <pre>-color-attribute-name &lt;adams.core.base.BaseRegExp&gt; (property: colorAttributeName)
 * &nbsp;&nbsp;&nbsp;Attribute for the colors using regular expression used if set,otherwise 
 * &nbsp;&nbsp;&nbsp;the index is used
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
 * <pre>-color-attribute &lt;java.lang.String&gt; (property: colorAttribute)
 * &nbsp;&nbsp;&nbsp;index of optional attribute to use for coloring, used only ifregular expression 
 * &nbsp;&nbsp;&nbsp;not set, ignored if empty
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-overlay &lt;adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;add overlays to the scatterplot
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-paintlet &lt;adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;Paintlet for plotting data
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.ScatterPaintletCircle -color-provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 * <pre>-mouse-click-action &lt;adams.gui.visualization.stats.scatterplot.action.MouseClickAction&gt; (property: mouseClickAction)
 * &nbsp;&nbsp;&nbsp;How to process mouse clicks in the plot.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.scatterplot.action.NullClickAction
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author msf8
 * @author FracPete (fracpete at waikato dot ac dot nz)
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

  /**String for setting color attribute using regular expression */
  protected BaseRegExp m_ColorAttributeName;

  /** the 0-based index of the X attribute. */
  protected String m_XAttribute;

  /** the 1-based index of the Y attribute. */
  protected String m_YAttribute;

  /** the 1-based index of the color attribute. */
  protected String m_ColorAttribute;

  /** scatter panel to display using the actor */
  protected ScatterPlot m_ScatterPlot;

  /**Array containing scatter plot overlays */
  protected AbstractScatterPlotOverlay[] m_Overlays;

  /**Paintlet to draw original data with */
  protected AbstractScatterPlotPaintlet m_Paintlet;

  /** the mouse click action. */
  protected MouseClickAction m_MouseClickAction;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying a scatter plot of one attribute vs another.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x-attribute-name", "xAttributeName",
      new BaseRegExp(""));

    m_OptionManager.add(
      "y-attribute-name", "yAttributeName",
      new BaseRegExp(""));

    m_OptionManager.add(
      "color-attribute-name", "colorAttributeName",
      new BaseRegExp(""));

    m_OptionManager.add(
      "x-attribute", "xAttribute",
      "1");

    m_OptionManager.add(
      "y-attribute", "yAttribute",
      "1");

    m_OptionManager.add(
      "color-attribute", "colorAttribute",
      "");

    m_OptionManager.add(
      "overlay", "overlays",
      new AbstractScatterPlotOverlay[]{});

    m_OptionManager.add(
      "paintlet", "paintlet",
      new ScatterPaintletCircle());

    m_OptionManager.add(
      "mouse-click-action", "mouseClickAction",
      new NullClickAction());
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
    return 600;
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
   * Set the color attribute on the scatter plot using a regular expression.
   *
   * @param val		regular expression for color attribute
   */
  public void setColorAttributeName(BaseRegExp val) {
    m_ColorAttributeName = val;
    reset();
  }
  /**
   * Returns the attribute to be uses for determining the colors.
   *
   * @return		regular expression for choosing attribute
   */
  public BaseRegExp getColorAttributeName() {
    return m_ColorAttributeName;
  }

  /**
   * Tip text to display for y attribute regular expression.
   *
   * @return		String to display
   */
  public String colorAttributeNameTipText() {
    return "Attribute for the colors using regular expression used if set," +
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

  /**
   * Set the attribute to use for coloring the plot (optional).
   *
   * @param val		1-based index of attribute
   */
  public void setColorAttribute(String val) {
    m_ColorAttribute = val;
    reset();
  }

  /**
   * Get the index of the attribute displayed on the y axis
   * @return		The 1-based index of the attribute on the y axis
   */
  public String getColorAttribute() {
    return m_ColorAttribute;
  }

  /**
   * Returns the tip text setting the color attribute using an index.
   *
   * @return         1-based index for attribute
   */
  public String colorAttributeTipText() {
    return "index of optional attribute to use for coloring, used only if" +
      "regular expression not set, ignored if empty";
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
   * Returns the mouse click action in use.
   *
   * @return		the action, null if non set
   */
  public MouseClickAction getMouseClickAction() {
    return m_MouseClickAction;
  }

  /**
   * Returns the tip text setting the y attribute using an index
   *
   * @return         1-based index for attribute to display on y axis
   */
  public String mouseClickActionTipText() {
    return "How to process mouse clicks in the plot.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_ScatterPlot != null) {
      SpreadSheet temp = new DefaultSpreadSheet();
      m_ScatterPlot.setData(temp);
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_ScatterPlot = new ScatterPlot();
    return m_ScatterPlot;
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_ScatterPlot.setXRegExp(m_XAttributeName);
    m_ScatterPlot.setXIndex(new Index(m_XAttribute));
    m_ScatterPlot.setYRegExp(m_YAttributeName);
    m_ScatterPlot.setColorRegExp(m_ColorAttributeName);
    m_ScatterPlot.setYIndex(new Index(m_YAttribute));
    m_ScatterPlot.setColorIndex(new Index(m_ColorAttribute));
    m_ScatterPlot.setData((SpreadSheet) token.getPayload());
    m_ScatterPlot.setOverlays(m_Overlays);
    m_ScatterPlot.setPaintlet(m_Paintlet);
    m_ScatterPlot.setMouseClickAction((MouseClickAction) OptionUtils.shallowCopy(m_MouseClickAction));
    m_ScatterPlot.reset();
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
      protected ScatterPlot m_ScatterPlot;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_ScatterPlot = new ScatterPlot();
	add(m_ScatterPlot, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_ScatterPlot.setXRegExp(m_XAttributeName);
	m_ScatterPlot.setXIndex(new Index(m_XAttribute));
	m_ScatterPlot.setYRegExp(m_YAttributeName);
	m_ScatterPlot.setColorRegExp(m_ColorAttributeName);
	m_ScatterPlot.setYIndex(new Index(m_YAttribute));
	m_ScatterPlot.setColorIndex(new Index(m_ColorAttribute));
	m_ScatterPlot.setData((SpreadSheet) token.getPayload());
	m_ScatterPlot.setOverlays(m_Overlays);
	m_ScatterPlot.setPaintlet(m_Paintlet);
        m_ScatterPlot.setMouseClickAction((MouseClickAction) OptionUtils.shallowCopy(m_MouseClickAction));
	m_ScatterPlot.reset();
      }
      @Override
      public void clearPanel() {
	SpreadSheet temp = new DefaultSpreadSheet();
	m_ScatterPlot.setData(temp);
      }
      @Override
      public JComponent supplyComponent() {
	return m_ScatterPlot;
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
