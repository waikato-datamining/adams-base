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
 * VersusOrderOptions.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.fourinone;

import adams.data.DecimalFormatString;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.stats.core.AbstractPlotOptionGroup;
import adams.gui.visualization.stats.paintlet.VsOrderPaintlet;

/**
 <!-- globalinfo-start -->
 * Class containing the options for the versus order plot
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
 * <pre>-paintlet &lt;adams.gui.visualization.stats.paintlet.VsOrderPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;painlet for plotting the vs fit plot
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.VsOrderPaintlet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class VersusOrderOptions
  extends AbstractPlotOptionGroup {

  /** for serialization */
  private static final long serialVersionUID = -4737656972085433346L;

  /** Paintlet for plotting the data */
  protected VsOrderPaintlet m_Val;

  /**
   * Returns the group name.
   * 
   * @return		the name
   */
  @Override
  protected String getGroupName() {
    return "Versus Order plot";
  }


  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"paintlet", "paintlet", 
	new VsOrderPaintlet());
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
    result.setType(Type.DEFAULT);
    result.setLabel("Order");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("#"));
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
    result.setType(Type.DEFAULT);
    result.setLabel("Residuals");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(1);
    result.setWidth(60);
    result.setTopMargin(0.05);
    result.setBottomMargin(0.05);
    result.setCustomFormat(new DecimalFormatString("#.#"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Set the paintlet for the versus order plot
   * @param val			Paintlet for plotting the data
   */
  public void setPaintlet(VsOrderPaintlet val) {
    m_Val = val;
    reset();
  }

  /**
   * Get the paintlet for the versus order plot
   * @return			Paintlet for plotting the data
   */
  public VsOrderPaintlet getPaintlet() {
    return m_Val;
  }

  /**
   * Tip Text for the paintlet property
   * @return			String to describe this property
   */
  public String paintletTipText() {
    return "painlet for plotting the vs fit plot";
  }
}