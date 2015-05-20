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
 * NormalPlotOptions.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.probabilityplot;

import adams.data.DecimalFormatString;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.stats.core.AbstractPlotOptionGroup;

/**
 <!-- globalinfo-start -->
 * Class for setting the options for the normal probability plot
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
 * <pre>-regression-line (property: regLine)
 * &nbsp;&nbsp;&nbsp;Display a linear regression line overlay
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 *
 */
public class NormalPlotOptions
  extends AbstractPlotOptionGroup {

  /** for serialization */
  private static final long serialVersionUID = -4737656972085433346L;

  /**Whether to display a regression diagonal line */
  protected boolean m_RegLine;

  /**
   * Returns the group name.
   * 
   * @return		the name
   */
  @Override
  protected String getGroupName() {
    return "Normal probability plot";
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"regression-line", "regLine", 
	true);
  }

  /**
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  @Override
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions	result;
    FancyTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("#.#"));
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
  @Override
  protected AxisPanelOptions getDefaultAxisY() {
    AxisPanelOptions	result;
    FancyTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.ABSOLUTE);
    result.setLabel("");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(1);
    result.setWidth(60);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("#.#"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Set whether a regression line should be drawn
   * @param val		True if regression line drawn
   */
  public void setRegLine(boolean val) {
    m_RegLine = val;
    reset();
  }

  /**
   * Get whether a regression line should be drawn
   * @return
   */
  public boolean getRegLine() {
    return m_RegLine;
  }

  /**
   * Tip text for the regression line property
   * @return			String describing the property
   */
  public String regLineTipText() {
    return "Display a linear regression line overlay";
  }
}