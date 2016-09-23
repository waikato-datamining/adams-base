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
 * BoxPlot.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.boxplot.BoxPlotManager;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * <!-- globalinfo-start -->
 * * Actor for displaying box plots.<br>
 * * <br>
 * * For more information, see:<br>
 * * http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Box_plot
 * * <br><br>
 * <!-- globalinfo-end -->
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
 * &nbsp;&nbsp;&nbsp;default: BoxPlot
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
 * <pre>-short-title (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
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
 * &nbsp;&nbsp;&nbsp;default: 700
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
 * <pre>-width-plot &lt;int&gt; (property: widthPlot)
 * &nbsp;&nbsp;&nbsp;Width of each box plot
 * &nbsp;&nbsp;&nbsp;default: 200
 * &nbsp;&nbsp;&nbsp;minimum: 50
 * &nbsp;&nbsp;&nbsp;maximum: 500
 * </pre>
 * 
 * <pre>-height-plot &lt;int&gt; (property: heightPlot)
 * &nbsp;&nbsp;&nbsp;Height of each box plot
 * &nbsp;&nbsp;&nbsp;default: 200
 * &nbsp;&nbsp;&nbsp;minimum: 50
 * &nbsp;&nbsp;&nbsp;maximum: 500
 * </pre>
 * 
 * <pre>-width-ax &lt;int&gt; (property: widthAx)
 * &nbsp;&nbsp;&nbsp;Width of box plot y axis
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 20
 * &nbsp;&nbsp;&nbsp;maximum: 100
 * </pre>
 * 
 * <pre>-num-horizontal &lt;int&gt; (property: numHorizontal)
 * &nbsp;&nbsp;&nbsp;Number of box plots to display on each row, -1 and 0 aredefault and will 
 * &nbsp;&nbsp;&nbsp;display all the box plots in one row
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-fill-box (property: fillBox)
 * &nbsp;&nbsp;&nbsp;Fill the box plots with color
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color to fill box plots
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 * <pre>-same-axis (property: sameAxis)
 * &nbsp;&nbsp;&nbsp;Box plots have same axis
 * </pre>
 * 
 * <pre>-attributes &lt;adams.core.Range&gt; (property: attributes)
 * &nbsp;&nbsp;&nbsp;Attributes to display in box plots; A range is a comma-separated list of 
 * &nbsp;&nbsp;&nbsp;single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)'
 * &nbsp;&nbsp;&nbsp; inverts the range '...'; the following placeholders can be used as well:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class BoxPlot
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization */
  private static final long serialVersionUID = -8553869138965368551L;

  /** panel to display on the actor */
  protected BoxPlotManager m_BoxPlot;

  /** same axis for each box plot */
  protected boolean m_SameAxis;

  /**range of box plots to display, in string form */
  protected Range m_AttString;

  /** width of plots to be drawn */
  protected int m_WidthPlot;

  /** height of plots to be drawn */
  protected int m_HeightPlot;

  /** same axis for each box plot */
  protected int m_WidthAx;

  /**Number of plots on each row */
  protected int m_NumHorizontal;

  /**Fill the box with color*/
  protected boolean m_Fill;

  /**Color to fill the box */
  protected Color m_Color;

  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  @Override
  public void clearPanel() {
    if (m_BoxPlot != null) {
      SpreadSheet temp = new DefaultSpreadSheet();
      m_BoxPlot.setData(temp);
      m_BoxPlot.reset();
    }
  }

  @Override
  protected BasePanel newPanel() {
    m_BoxPlot = new BoxPlotManager();
    return m_BoxPlot;
  }

  @Override
  protected void display(Token token) {
    //set data for the box plot manager
	  
	  //should possibly clear the entire panel
    m_BoxPlot.setBoxWidth(m_WidthPlot);
    m_BoxPlot.setBoxHeight(m_HeightPlot);
    m_BoxPlot.setAxisWidth(m_WidthAx);
    m_BoxPlot.setNumHorizontal(m_NumHorizontal);
    m_BoxPlot.setSameAxis(m_SameAxis);
    m_BoxPlot.setRange(m_AttString);
    m_BoxPlot.setData((SpreadSheet) token.getPayload());
    m_BoxPlot.setFill(m_Fill);
    m_BoxPlot.setColor(m_Color);
    m_BoxPlot.reset();
  }

  /**
   * Adds options to the internal list of operations
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"width-plot", "widthPlot",
	200, 50,500);

    m_OptionManager.add(
	"height-plot", "heightPlot",
	200, 50, 500);

    m_OptionManager.add(
	"width-ax", "widthAx",
	50, 20, 100);

    m_OptionManager.add(
	"num-horizontal", "numHorizontal",
	3,-1,null);

    m_OptionManager.add(
	"fill-box", "fillBox", true);

    m_OptionManager.add(
	"color", "color", Color.RED);

    m_OptionManager.add(
	"same-axis", "sameAxis",
	false);

    m_OptionManager.add(
	"attributes", "attributes",
	new Range(Range.ALL));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "attributes", m_AttString.getRange(), ", atts: ");
    result += QuickInfoHelper.toString(this, "numHorizontal", m_NumHorizontal, ", #horizontal: ");
    result += QuickInfoHelper.toString(this, "sameAxis", m_SameAxis, "same axis", ", ");
    
    return result;
  }

  /**
   * Set whether the box plots should be filled with color
   * @param val			True if box should be filled in
   */
  public void setFillBox(boolean val) {
    m_Fill = val;
    reset();
  }

  /**
   * get whether the box should be filled with color
   * @return
   */
  public boolean getFillBox() {
    return m_Fill;
  }

  /**
   * Tip Text for the fill box property
   * @return			String to explain the property
   */
  public String fillBoxTipText() {
    return "Fill the box plots with color";
  }

  /**
   * Set the color to fill the box plots
   * @param val			Color to fill plots
   */
  public void setColor(Color val) {
    m_Color = val;
    reset();
  }

  /**
   * Get the color to fill the box plots
   * @return		Color used to fill
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Tip text for the color property
   * @return			String to explain the property
   */
  public String colorTipText() {
    return "Color to fill box plots";
  }

  /**
   * Set the width of each box plot
   * @param val		Width in pixels
   */
  public void setWidthPlot(int val) {
    m_WidthPlot = val;
    reset();
  }

  /**
   * Get the width of each box plot
   * @return		Width in pixels
   */
  public int getWidthPlot() {
    return m_WidthPlot;
  }

  /**
   * Tip text to display for the option
   * @return		Message string
   */
  public String widthPlotTipText() {
    return "Width of each box plot";
  }

  /**
   * Set the height of each box plot
   * @param val		Height in pixels
   */
  public void setHeightPlot(int val) {
    m_HeightPlot = val;
    reset();
  }

  /**
   * get the height of each box plot
   * @return		Height in pixels
   */
  public int getHeightPlot() {
    return m_HeightPlot;
  }

  /**
   * Tip text to display for the option
   * @return		Message string
   */
  public String heightPlotTipText() {
    return "Height of each box plot";
  }

  /**
   * Set the width of the left axis for each box plot
   * @param val		Width in pixels
   */
  public void setWidthAx(int val) {
    m_WidthAx = val;
    reset();
  }

  /**
   * Get the width of the left axis of each box plot
   * @return		Width in pixels
   */
  public int getWidthAx() {
    return m_WidthAx;
  }

  /**
   * Tip text to display for the option
   * @return		Message string
   */
  public String widthAxTipText() {
    return "Width of box plot y axis";
  }

  /**
   * Set the number of box plots to display on each row
   * @param val			Number on each row
   */
  public void setNumHorizontal(int val) {
    m_NumHorizontal = val;
    reset();
  }

  /**
   * Get the number of box plots to display on each row
   * @return			number on each row
   */
  public int getNumHorizontal() {
    return m_NumHorizontal;
  }

  /**
   * Return a tip text for the number of box plots on each row property
   * @return			tip text for the property
   */
  public String numHorizontalTipText() {
    return "Number of box plots to display on each row, -1 and 0 are" +
    "default and will display all the box plots in one row";
  }


  @Override
  public String globalInfo() {
    return 
	"Actor for displaying box plots.\n\n"
	+ "For more information, see:\n"
	+ "http://en.wikipedia.org/wiki/Box_plot";
  }

  @Override
  protected int getDefaultWidth() {
    return 1000;
  }

  @Override
  protected int getDefaultHeight() {
    return 700;
  }

  /**
   * Set the attributes to be displayed initially
   * @param val		String to create the range object
   */
  public void setAttributes(Range val) {
    m_AttString = val;
    reset();

  }

  /**
   * get the attributes to be displayed initially
   * @return		String to create the range object
   */
  public Range getAttributes() {
    return m_AttString;
  }

  /**
   * Tip text to display for the option
   * @return		String to display
   */
  public String attributesTipText() {
    return "Attributes to display in box plots.";
  }

  /**
   * Set whether the box plots use the same axis scale
   * @param val		True if box plots use same axis
   */
  public void setSameAxis(boolean val) {
    m_SameAxis = val;
    reset();
  }

  /**
   * gets whether the box plots are drawn using the same axis
   * @return		true if box plots use the same axis scale
   */
  public boolean getSameAxis() {
    return m_SameAxis;
  }

  /**
   * Returns the tip text for this property
   * @return		Tip text for this property
   */
  public String sameAxisTipText() {
    return "Box plots have same axis";
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

    result = new AbstractComponentDisplayPanel("BoxPlot") {
      private static final long serialVersionUID = -5112946659280550587L;
      protected BoxPlotManager m_BoxPlot;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_BoxPlot = new BoxPlotManager();
	add(m_BoxPlot, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_BoxPlot.setBoxWidth(m_WidthPlot);
	m_BoxPlot.setBoxHeight(m_HeightPlot);
	m_BoxPlot.setAxisWidth(m_WidthAx);
	m_BoxPlot.setNumHorizontal(m_NumHorizontal);
	m_BoxPlot.setSameAxis(m_SameAxis);
	m_BoxPlot.setRange(m_AttString);
	m_BoxPlot.setData((SpreadSheet) token.getPayload());
	m_BoxPlot.setFill(m_Fill);
	m_BoxPlot.setColor(m_Color);
	m_BoxPlot.reset();
      }
      @Override
      public void clearPanel() {
	SpreadSheet temp = new DefaultSpreadSheet();
	m_BoxPlot.setData(temp);
	m_BoxPlot.reset();
      }
      @Override
      public JComponent supplyComponent() {
	return m_BoxPlot;
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
