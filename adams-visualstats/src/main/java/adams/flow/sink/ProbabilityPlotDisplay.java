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
 * ProbabilityPlotDisplay.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.paintlet.AbstractProbabilityPaintlet;
import adams.gui.visualization.stats.paintlet.Normal;
import adams.gui.visualization.stats.probabilityplot.ProbabilityPlot;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a probability plot
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
 * &nbsp;&nbsp;&nbsp;default: ProbabilityPlotDisplay
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
 * &nbsp;&nbsp;&nbsp;default: 900
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
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
 * <pre>-regression &lt;adams.gui.visualization.stats.paintlet.AbstractProbabilityPaintlet&gt; (property: regression)
 * &nbsp;&nbsp;&nbsp;Regression to display
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.Normal
 * </pre>
 *
 * <pre>-grid (property: grid)
 * &nbsp;&nbsp;&nbsp;Display a grid overlay
 * </pre>
 *
 * <pre>-regression-line (property: regressionLine)
 * &nbsp;&nbsp;&nbsp;Display a best fit line overlay
 * </pre>
 *
 * <pre>-attribute-name &lt;adams.core.base.BaseRegExp&gt; (property: attributeName)
 * &nbsp;&nbsp;&nbsp;Name of attribute to display, used if set,otherwise the index is used
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-attribute &lt;java.lang.String&gt; (property: attribute)
 * &nbsp;&nbsp;&nbsp;Set the attribute to display using an index, used only if regular expression
 * &nbsp;&nbsp;&nbsp;not set
 * &nbsp;&nbsp;&nbsp;default: last
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class ProbabilityPlotDisplay
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization */
  private static final long serialVersionUID = -1500480091045045663L;

  /** Probability plot to display */
  protected ProbabilityPlot m_Plot;

  /**Paintlet for plotting the data */
  protected AbstractProbabilityPaintlet m_Val;

  /**Whether a grid overlay is drawn */
  protected boolean m_Grid;

  /** Whether a best regression line is drawn */
  protected boolean m_Regression;

  /**name of attribute to display */
  protected BaseRegExp m_Att;

  /**Index of attribute to display */
  protected String m_AttIndex;

  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
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
    m_Plot = new ProbabilityPlot();
    return m_Plot;
  }

  @Override
  protected void display(Token token) {
    m_Plot.setData((SpreadSheet) token.getPayload());
    m_Plot.setRegression(m_Val);
    m_Plot.setGrid(m_Grid);
    m_Plot.setRegressionLine(m_Regression);
    m_Plot.setAttRegExp(m_Att);
    m_Plot.setAttIndex(new Index(m_AttIndex));
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    //paintlet to use for regression
    m_OptionManager.add(
      "regression", "regression",
      new Normal());

    //Display grid overlay
    m_OptionManager.add(
      "grid", "grid", false);

    //display a regression line overlay
    m_OptionManager.add(
      "regression-line", "regressionLine", false);

    //Name of attribute
    m_OptionManager.add(
      "attribute-name", "attributeName", new BaseRegExp(""));

    //Index of attribute
    m_OptionManager.add(
      "attribute", "attribute", "last");
  }

  /**
   * Set the string to use for setting the attribute using
   * an index
   * @param val			String for the attribute index
   */
  public void setAttribute(String val) {
    m_AttIndex = val;
    reset();
  }

  /**
   * Get the string used to set the attribute using an index
   * @return			String for the index
   */
  public String getAttribute() {
    return m_AttIndex;
  }

  /**
   * return a tip text for the attribute index property
   * @return			tip text for the property
   */
  public String attributeTipText() {
    return "Set the attribute to display using an index, used only if " +
      "regular expression not set";
  }

  /**
   * Set the attribute to use with a regular expression
   * @param val		String for regular expression
   */
  public void setAttributeName(BaseRegExp val) {
    m_Att = val;
    reset();
  }

  /**
   * Get the string to set the attribute using a regular expression
   * @return				String for regular expression
   */
  public BaseRegExp getAttributeName() {
    return m_Att;
  }

  /**
   * Return a tip text for the attribute name property
   * @return			tip text for the property
   */
  public String attributeNameTipText() {
    return "Name of attribute to display, used if set," +
      "otherwise the index is used";
  }


  /**
   * Set whether a grid overlay is added
   * @param val		true if grid overlay drawn
   */
  public void setGrid(boolean val) {
    m_Grid = val;
    reset();
  }

  /**
   * Get whether a grid overlay should be added
   * @return			True if grid is to be added
   */
  public boolean getGrid() {
    return m_Grid;
  }

  /**
   * Tip text for the grid property
   * @return			String describing the property
   */
  public String gridTipText() {
    return "Display a grid overlay";
  }

  /**
   * Set whether a regression line overlay should be added
   * @param val			True if regression line added
   */
  public void setRegressionLine(boolean val) {
    m_Regression = val;
    reset();
  }

  /**
   * Get whether a regression line should be added
   * @return		true if regression line should be added
   */
  public boolean getRegressionLine() {
    return m_Regression;
  }

  /**
   * Tip text for the regression line property
   * @return			String describing the property
   */
  public String regressionLineTipText() {
    return "Display a best fit line overlay, will only display if regression chosen supports the line";
  }

  /**
   * Set the regression used to transform the data
   * @param val			Paintlet for the regression
   */
  public void setRegression(AbstractProbabilityPaintlet val) {
    m_Val = (AbstractProbabilityPaintlet) val.shallowCopy();
    reset();
  }

  /**
   * get the regression used to transform the data
   * @return			Paintlet used for the regression
   */
  public AbstractProbabilityPaintlet getRegression() {
    return m_Val;
  }

  /**
   * Tip text for the regression property
   * @return			String describing the property
   */
  public String regressionTipText() {
    return "Regression to display";
  }

  @Override
  public String globalInfo() {
    return "Actor for displaying a probability plot";
  }

  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  @Override
  protected int getDefaultWidth() {
    return 1400;
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

    result = new AbstractComponentDisplayPanel("Probability plot") {
      private static final long serialVersionUID = 4360182045245637304L;
      protected ProbabilityPlot m_Plot;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_Plot = new ProbabilityPlot();
	add(m_Plot, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_Plot.setData((SpreadSheet) token.getPayload());
	m_Plot.setRegression(m_Val);
	m_Plot.setGrid(m_Grid);
	m_Plot.setRegressionLine(m_Regression);
	m_Plot.setAttRegExp(m_Att);
	m_Plot.setAttIndex(new Index(m_AttIndex));
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