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
 * DiagonalPaintlet.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;

/**
 <!-- globalinfo-start -->
 * Paintlet for displaying the diagonal overlay on the scatter plot.
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
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class DiagonalPaintlet
  extends AbstractOverlayPaintlet
  implements AntiAliasingSupporter {

  /** For serialization */
  private static final long serialVersionUID = 2136293814736622480L;

  /** Whether an indicator should be shown on the side
   * indicating where the diagonal is and at what gradient
   */
  protected boolean m_Indicator;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for displaying the diagonal overlay on the scatter plot.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    memberChanged();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing lines.";
  }

  /**
   * Draws the data on the graphics object
   * @param g		Graphics object to display data on
   */
  @Override
  protected void drawData(Graphics g) {
    if(m_Calculated) {
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

      g.setColor(m_Color);
      Graphics2D g2d = (Graphics2D)g;
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      //draw the diagonal
      g2d.drawLine(0,
	  m_AxisLeft.valueToPos(m_AxisBottom.posToValue(0)),
	  m_AxisBottom.getWidth(),
	  m_AxisLeft.valueToPos(m_AxisBottom.posToValue(m_AxisBottom.getWidth())));


      if(m_Indicator) {
	//see if diagonal goes through the plot
	boolean through = false;
	int pos = Integer.MIN_VALUE;
	int oldPos;
	for(int i = 0; i< m_AxisBottom.getWidth(); i++) {
	  oldPos = pos;
	  pos = m_AxisLeft.valueToPos(m_AxisBottom.posToValue(i));
	  //if the position of the line is on the graph or above the graph
	  //with the previous point being below, then the diagonal is visible
	  if((pos>0 && pos< m_AxisLeft.getHeight())|| (oldPos > m_AxisLeft.getHeight() && pos < 0)) {
	    through = true;
	    break;
	  }
	}
	if(!through) {
	  double gradient;
	  //gradient of the diagonal in pixels
	  gradient = -(((double)(m_AxisLeft.valueToPos(m_AxisBottom.posToValue(100))
	      - m_AxisLeft.valueToPos(m_AxisBottom.posToValue(0))))/100.0);
	  //left hand side of plot
	  if(m_AxisLeft.valueToPos(m_AxisBottom.posToValue(0))<0) {
	    //if gradient > 1 then draw the line along entire left side of plot
	    if(gradient>1) {
	      int lengthY = m_AxisLeft.getHeight();
	      int lengthX = (int) Math.round((m_AxisLeft.getHeight()/gradient));
	      //If the length of the x line fits
	      if(lengthX < m_AxisBottom.getWidth()) {
		g2d.drawLine(1, 0, 1, lengthY);
		g2d.drawLine(0, 1, lengthX+1, 1);
	      }
	      //otherwise draw the x line the width of the plot and rescale the y line
	      else {
		g2d.drawLine(0, 1, m_AxisBottom.getWidth(), 1);
		g2d.drawLine(1, 0, 1, (int)Math.round((m_AxisBottom.getWidth()*gradient)));
	      }
	    }
	    //If gradient < 1, dra the x line the width of the plot
	    else {
	      int lengthX = m_AxisBottom.getWidth();
	      int lengthY = (int)Math.round((m_AxisBottom.getWidth() *gradient));
	      //If the y line fits
	      if(lengthY < m_AxisLeft.getHeight()) {
		g2d.drawLine(0, 1, lengthX, 1);
		g2d.drawLine(1, 0, 1, lengthY+1);
	      }
	      //otherwise draw the y line the height of the plot and rescale the x line
	      else {
		g2d.drawLine(1, 0, 1, m_AxisLeft.getHeight());
		g2d.drawLine(0, 1, (int)Math.round((m_AxisLeft.getHeight()/gradient)), 1);
	      }
	    }
	  }
	  //Diagonal on the right and side of the plot
	  else {
	    //If gradient >1, draw the y line the height of the plot
	    if(gradient>1) {
	      int lengthY = m_AxisLeft.getHeight();
	      int lengthX = (int)Math.round((m_AxisLeft.getHeight()/gradient));
	      //if the x line fits
	      if(lengthX < m_AxisBottom.getWidth()) {
		g2d.drawLine(m_AxisBottom.getWidth()-1, 0, m_AxisBottom.getWidth()-1, lengthY);
		g2d.drawLine(m_AxisBottom.getWidth()-(lengthX+1), m_AxisLeft.getHeight()-1, m_AxisBottom.getWidth(), m_AxisLeft.getHeight()-1);
	      }
	      //otherwise draw the x line the width of the plot and rescale the y line
	      else {
		g2d.drawLine(0, m_AxisLeft.getHeight()-1, m_AxisBottom.getWidth(), m_AxisLeft.getHeight()-1);
		g2d.drawLine(m_AxisBottom.getWidth()-1, m_AxisLeft.getHeight()-(int)Math.round((m_AxisBottom.getWidth()*gradient)), m_AxisBottom.getWidth()-1, m_AxisLeft.getHeight());
	      }
	    }
	    //if gradient <1, draw the x line the width of the plot
	    else {
	      int lengthX = m_AxisBottom.getWidth();
	      int lengthY = (int)Math.round((m_AxisBottom.getWidth() *gradient));
	      //if y line fits
	      if(lengthY < m_AxisLeft.getHeight()) {
		g2d.drawLine(0, m_AxisLeft.getHeight()-1, lengthX, m_AxisLeft.getHeight()-1);
		g2d.drawLine(m_AxisBottom.getWidth()-1, m_AxisLeft.getHeight()-(lengthY+1), m_AxisBottom.getWidth()-1, m_AxisLeft.getHeight());
	      }
	      //otherwise draw the y line the height of the plot and rescale the x line
	      else {
		g2d.drawLine(m_AxisBottom.getWidth()-1, 0, m_AxisBottom.getWidth()-1, m_AxisLeft.getHeight());
		g2d.drawLine(m_AxisBottom.getWidth()-(int)Math.round((m_AxisLeft.getHeight()/gradient)), m_AxisLeft.getHeight()-1, m_AxisBottom.getWidth(), m_AxisLeft.getHeight()-1);
	      }
	    }

	  }

	}
      }
    }
  }

  @Override
  public void calculate() {
    super.calculate();
    m_Calculated = true;
  }

  @Override
  public void setIndicator(boolean val) {
    m_Indicator = val;
    memberChanged();
  }
}