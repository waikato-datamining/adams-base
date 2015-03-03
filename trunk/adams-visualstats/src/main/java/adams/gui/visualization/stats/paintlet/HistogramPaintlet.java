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
 * HistogramPaintlet.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.Color;
import java.awt.Graphics;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.histogram.Histogram;

/**
 <!-- globalinfo-start -->
 * Paints the histogram
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Stroke color for the paintlet
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-fill-bins (property: fillBins)
 * &nbsp;&nbsp;&nbsp;Fill the bins with color
 * </pre>
 *
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;color for filling the bins
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class HistogramPaintlet
extends AbstractColorPaintlet{

  /** for serialization */
  private static final long serialVersionUID = -3474738819482043957L;

  /** Whether to fill the bins with color */
  protected boolean m_Fill;

  /** Color to fill the bins with if fill is chosen */
  protected Color m_FillColor;

  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    //Fill bins
    m_OptionManager.add(
	"fill-bins", "fillBins", true);

    //color for fill
    m_OptionManager.add(
	"fill-color", "fillColor", Color.RED);
  }

  /**
   * Set whether the bins should be filled with color
   * @param val			True if bins filled
   */
  public void setFillBins(boolean val) {
    m_Fill = val;
    memberChanged();
  }

  /**
   * get whether the bins should be filled with color
   * @return			true if bins filled
   */
  public boolean getFillBins() {
    return m_Fill;
  }

  /**
   * Tip text for the fill bins property
   * @return				String describing the property
   */
  public String fillBinsTipText() {
    return "Fill the bins with color";
  }

  /**
   * Set the color for filling the bins
   * @param val			Color for fill
   */
  public void setFillColor(Color val) {
    m_FillColor = val;
    memberChanged();
  }

  /**
   * Get the color for filling the bins
   * @return			Color for fill
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Tip text for the color property
   * @return			Color describing the property
   */
  public String fillColorTipText() {
    return "color for filling the bins";
  }
  
  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    double[][] data = ((Histogram) getPanel()).getPlotdata();
    double binWidth = ((Histogram) getPanel()).getBinWidth();
    if ((data != null) && (data.length > 0)) {
      AxisPanel axisBottom = getPlot().getAxis(Axis.BOTTOM);
      AxisPanel axisLeft = getPlot().getAxis(Axis.LEFT);
      for(int i = 0; i< data.length; i++) {
	double val = data[i][1]/binWidth;
	//If bin is filled with color
	if(m_Fill) {
	  g.setColor(m_FillColor);
	  g.fillRect(axisBottom.valueToPos(data[i][0]),
	      axisLeft.valueToPos(val),
	      axisBottom.valueToPos(data[i][0]+ binWidth)-axisBottom.valueToPos(data[i][0]),
	      axisLeft.valueToPos(axisLeft.getMinimum())-axisLeft.valueToPos(val));
	}
	//Outline of bin
	g.setColor(m_Color);
	g.drawRect(axisBottom.valueToPos(data[i][0]),
	    axisLeft.valueToPos(val),
	    axisBottom.valueToPos(data[i][0]+ binWidth)-axisBottom.valueToPos(data[i][0]),
	    axisLeft.valueToPos(axisLeft.getMinimum())-axisLeft.valueToPos(val));
      }
    }
  }

  @Override
  public String globalInfo() {
    return "Paints the histogram";
  }
}