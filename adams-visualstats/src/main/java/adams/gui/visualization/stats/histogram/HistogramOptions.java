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
 * HistogramOptions.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.histogram;

import adams.data.DecimalFormatString;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.stats.core.AbstractPlotOptionGroup;
import adams.gui.visualization.stats.paintlet.HistogramPaintlet;

/**
 <!-- globalinfo-start -->
 * Class containing options for the histogram plot
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
 * <pre>-number-bins &lt;int&gt; (property: numberBins)
 * &nbsp;&nbsp;&nbsp;Number of bins, used only if frequency axis type chosen
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 *
 * <pre>-width-bin &lt;double&gt; (property: widthBin)
 * &nbsp;&nbsp;&nbsp;Width of each bin, used only if denisty axis type chosen
 * &nbsp;&nbsp;&nbsp;default: 0.5
 * </pre>
 *
 * <pre>-y-axis-type &lt;MANUAL|DENSITY&gt; (property: axisType)
 * &nbsp;&nbsp;&nbsp;Axis type for y axis of the histogram
 * &nbsp;&nbsp;&nbsp;default: DENSITY
 * </pre>
 *
 * <pre>-paintlet &lt;adams.gui.visualization.stats.paintlet.HistogramPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;painlet for plotting the histogram plot
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.HistogramPaintlet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class HistogramOptions
  extends AbstractPlotOptionGroup {

  /** for serialization */
  private static final long serialVersionUID = -4737656972085433346L;

  /** Number of bins in the histogram used if manual bin calculation type is chosen */
  protected int m_NumBins;

  /** Width of each bin in the histogram, used if density
   * bin calculation type is chosen */
  protected double m_WidthBin;

  /**bin calculation type */
  protected BoxType m_BoxType;

  /**Paintlet for plotting the histogram */
  protected HistogramPaintlet m_Val;

  /**
   * Returns the group name.
   * 
   * @return		the name
   */
  @Override
  protected String getGroupName() {
    return "Histogram plot";
  }

  /**
   * Enum for bincalculation type without some of the options
   * @author msf8
   *
   */
  public enum BoxType {
    MANUAL, DENSITY
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"number-bins", "numberBins", 
	10);

    m_OptionManager.add(
	"width-bin", "widthBin", 
	0.5);

    m_OptionManager.add(
	"y-axis-type", "axisType", 
	BoxType.DENSITY);

    m_OptionManager.add(
	"paintlet", "paintlet", 
	new HistogramPaintlet());
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
    result.setTopMargin(0.05);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("#.#"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Set the paintlet for plotting the histogram
   * @param val			Paintlet for plotting
   */
  public void setPaintlet(HistogramPaintlet val) {
    m_Val = val;
    reset();
  }

  /**
   * Get the paintlet for plotting the histogram
   * @return				Paintlet for plotting
   */
  public HistogramPaintlet getPaintlet() {
    return m_Val;
  }

  /**
   * Tip Text for the paintlet property
   * @return			String to describe the property
   */
  public String paintletTipText() {
    return "painlet for plotting the histogram plot";
  }

  /**
   * Set the axis type for bin calculation
   * @param val			Method of bin calculation
   */
  public void setAxisType(BoxType val) {
    m_BoxType = val;
    reset();
  }

  /**
   * Get the axis type used in bin calculation
   * @return			method of bin calculation
   */
  public BoxType getAxisType() {
    return m_BoxType;
  }

  /**
   * Tip Text for the axis Type property
   * @return			String to describe the property
   */
  public String axisTypeTipText() {
    return "Axis type for y axis of the histogram";
  }

  /**
   * Set the width of each bin in the histogram
   * @param val			Width of bin
   */
  public void setWidthBin(double val) {
    m_WidthBin = val;
    reset();
  }

  /**
   * Get the width of each bin in histogram
   * @return			Width of each bin
   */
  public double getWidthBin() {
    return m_WidthBin;
  }

  /**
   * Tip text for the bin width property
   * @return			String describing the property
   */
  public String widthBinTipText() {
    return "Width of each bin, used only if denisty axis type chosen";
  }

  /**
   * Set the number of bins in the histogram
   * @param val			Number of bins
   */
  public void setNumberBins(int val) {
    m_NumBins = val;
    reset();
  }

  /**
   * get the number of bins in the histogram
   * @return			Number of bins
   */
  public int getNumberBins() {
    return m_NumBins;
  }

  /**
   * Tip text for the number of bins property
   * @return			String describing the property
   */
  public String numberBinsTipText() {
    return "Number of bins, used only if frequency axis type chosen";
  }
}