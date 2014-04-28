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
 * PeriodicityPaintlet.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Graphics;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import adams.data.timeseries.PeriodicityHelper;
import adams.data.timeseries.PeriodicityType;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paintlet for painting the spectral graph.
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
 * <pre>-markers-extent &lt;int&gt; (property: markerExtent)
 * &nbsp;&nbsp;&nbsp;The size of the markers in pixels.
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-markers-disabled (property: markersDisabled)
 * &nbsp;&nbsp;&nbsp;If set to true, the markers are disabled.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PeriodicityPaintlet
  extends AbstractTimeseriesPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -6475036298238205843L;

  /** the periodicity type. */
  protected PeriodicityType m_Periodicity;
  
  /** the color provider in use. */
  protected AbstractColorProvider m_ColorProvider;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for painting the periodicity background.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "periodicity", "periodicity",
	    PeriodicityType.valueOf(GUIHelper.getString(getClass(), "periodicity", PeriodicityType.NONE.toString())));

    m_OptionManager.add(
	    "color-provider", "colorProvider",
	    (AbstractColorProvider) GUIHelper.getOptionHandler(getClass(), "colorProvider", new PeriodicityColorProvider()));
  }

  /**
   * Checks whether the paintlet is supposed to paint for this 
   * {@link PaintMoment}. Does not paint anything if {@link PeriodicityType#NONE}.
   * 
   * @return		true if painting should occur
   */
  @Override
  public boolean canPaint(PaintMoment moment) {
    if (m_Periodicity == PeriodicityType.NONE)
      return false;
    else
      return super.canPaint(moment);
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.BACKGROUND;
  }

  /**
   * Sets the type of periodicity to use.
   *
   * @param value	the type
   */
  public void setPeriodicity(PeriodicityType value) {
    m_Periodicity = value;
    memberChanged();
  }

  /**
   * Returns the type of periodicity to use.
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
    return "The type of periodicity to use.";
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(AbstractColorProvider value) {
    m_ColorProvider = value;
    memberChanged();
  }

  /**
   * Returns the color provider in use.
   *
   * @return		the color provider
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
    return "The color provider to use.";
  }

  /**
   * Calculates the positions for the periodicity.
   * 
   * @param axisX	the X axis
   * @return		the positions
   */
  protected TIntArrayList calculatePositions(AxisPanel axisX) {
    TIntArrayList	result;
    TDoubleArrayList	val;
    double		left;
    double		right;
    Date		leftDate;
    Date		rightDate;
    Calendar		leftCal;
    Calendar		rightCal;
    int			last;
    int			i;
    int			current;

    left      = axisX.getActualMinimum();
    leftDate  = new Date((long) left);
    leftCal   = new GregorianCalendar();
    leftCal.setTime(leftDate);
    right     = axisX.getActualMaximum();
    rightDate = new Date((long) right);
    rightCal  = new GregorianCalendar();
    rightCal.setTime(rightDate);
    
    // turn raw values into pixel positions, drop duplicate ones
    val     = PeriodicityHelper.calculate(m_Periodicity, left, leftDate, leftCal, right, rightDate, rightCal);
    result  = new TIntArrayList();
    current = -1;
    for (i = 0; i < val.size(); i++) {
      last    = current;
      current = axisX.valueToPos(val.get(i));
      if (current != last)
	result.add(current);
    }
    
    return result;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel		axisX;
    TIntArrayList	pos;
    int			i;
    int			pHeight;
    int			x;
    int			width;
    
    axisX   = getPanel().getPlot().getAxis(Axis.BOTTOM);
    pos     = calculatePositions(axisX);
    pHeight = getPanel().getHeight();
    m_ColorProvider.resetColors();
    
    for (i = 0; i < pos.size() - 1; i++) {
      g.setColor(m_ColorProvider.next());
      x     = pos.get(i);
      width = pos.get(i + 1) - pos.get(i);
      g.fillRect(x, 0, width, pHeight);
    }
  }
}
