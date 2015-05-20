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
 * Gamma.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;

import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paints the transformed gamma distribution
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
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;Size of the data points
 * &nbsp;&nbsp;&nbsp;default: 5
 * </pre>
 *
 * <pre>-fill-point (property: fillPoint)
 * &nbsp;&nbsp;&nbsp;Whether to fill the data point with solid color
 * </pre>
 *
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;Color for filling data point
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 * <pre>-shape &lt;double&gt; (property: shape)
 * &nbsp;&nbsp;&nbsp;Shape paramter for gamma distribution
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-scale &lt;double&gt; (property: scale)
 * &nbsp;&nbsp;&nbsp;Scale parameter for gamma distribution
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class Gamma
extends AbstractProbabilityPaintlet{

  /** for serialization */
  private static final long serialVersionUID = 3439914415669101587L;

  /**Shape parameter for the gamma distribution */
  protected double m_Shape;

  /**Scale parameter for the gamma distribution */
  protected double m_Scale;

  @Override
  protected void initialize() {
    super.initialize();
    m_Shape = -1.0;
    m_Scale = -1.0;
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    //shape parameter
    m_OptionManager.add(
	"shape", "shape", 1.0);

    //scale parameter
    m_OptionManager.add(
	"scale", "scale", 1.0);
  }

  /**
   * Set the scale parameter for the gamma distribution
   * @param val			Scale parameter
   */
  public void setScale(double val) {
    m_Scale = val;
    memberChanged();
  }

  /**
   * Get the scale parameter for the gamma distribution
   * @return			Scale parameter
   */
  public double getScale() {
    return m_Scale;
  }

  /**
   * Tip text for the scale property
   * @return			String describing the property
   */
  public String scaleTipText() {
    return "Scale parameter for gamma distribution";
  }

  /**
   * Set the shape parameter for the gamma distribution
   * @param val			Shape parameter
   */
  public void setShape(double val) {
    m_Shape = val;
    memberChanged();
  }

  /**
   * Get the shape parameter for the gamma distribution
   * @return			Shape parameter
   */
  public double getShape() {
    return m_Shape;
  }

  /**
   * Tip text for the shape property
   * @return			String describing the property
   */
  public String shapeTipText() {
    return "Shape paramter for gamma distribution";
  }

  @Override
  public void configureAxes() {
    m_AxisBottom = getPanel().getPlot().getAxis(Axis.BOTTOM);
    m_AxisLeft = getPanel().getPlot().getAxis(Axis.LEFT);

    m_AxisBottom.setType(Type.LOG_ABSOLUTE);
    m_AxisBottom.setNumberFormat("#.##");
    m_AxisLeft.setType(Type.LOG_ABSOLUTE);
    m_AxisLeft.setNumberFormat("#.##");
  }

  /**
   * For calculating the dimensions of the plot area.
   */
  @Override
  public void calculateDimensions() {
    double median;
    GammaDistributionImpl gam = new GammaDistributionImpl(m_Shape, m_Scale);
    m_Data = m_Instances.attributeToDoubleArray(m_Index);
    m_TransformedY = new double[m_Data.length];
    Arrays.sort(m_Data);
    for(int i = 0; i< m_Data.length; i++) {
      median = ((i+1) -0.3)/(m_Data.length + 0.4);
      try {
	m_TransformedY[i] = gam.inverseCumulativeProbability(median);
      }
      catch (MathException e) {
	e.printStackTrace();
      }
    }
    //If axis can handle the data
    if(m_AxisBottom.getType().canHandle(m_Data[0], m_Data[m_Data.length-1])) {
      m_AxisBottom.setMinimum(m_Data[0]);
      m_AxisBottom.setMaximum(m_Data[m_Data.length-1]);
    }
    else {
      getLogger().severe("errors in plotting");
    }
    if(m_AxisLeft.getType().canHandle(m_TransformedY[0], m_TransformedY[m_TransformedY.length -1])) {
      m_AxisLeft.setMinimum(m_TransformedY[0]);
      m_AxisLeft.setMaximum(m_TransformedY[m_TransformedY.length -1]);
    }
    else {
      getLogger().severe("errors in plotting");
    }
    m_AxisBottom.setAxisName(m_Instances.attribute(m_Index).name() + ")");
    m_AxisLeft.setAxisName("Inverse Gamma");
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    if ((m_Instances != null) && (m_Data != null) && m_Shape != -1.0) {
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

      for(int i = 0; i< m_Data.length; i++) {
	Graphics2D g2d = (Graphics2D)g;
	//If data points are to be filled
	if(m_Fill) {
	  g2d.setColor(m_FillColor);
	  g2d.setStroke(new BasicStroke(0));
	  g2d.fillOval(m_AxisBottom.valueToPos(m_Data[i])-m_Size/2, m_AxisLeft.valueToPos(m_TransformedY[i])-m_Size/2, m_Size, m_Size);
	}
	//outline of data point
	g2d.setStroke(new BasicStroke(m_StrokeThickness));
	g2d.setColor(m_Color);
	g2d.drawOval(m_AxisBottom.valueToPos(m_Data[i])-m_Size/2, m_AxisLeft.valueToPos(m_TransformedY[i])-m_Size/2, m_Size, m_Size);
      }

      //If drawing regression fit diagonal
      if(m_RegressionLine) {
	g.setColor(Color.BLACK);
	double[] newData = new double[m_Data.length];
	for(int i = 0; i < m_Data.length; i++) {
	  newData[i] = Math.log(m_Data[i]);
	}
	GammaDistributionImpl gd = new GammaDistributionImpl(m_Shape, m_Scale);
	//draw the expected diagonal line using the gamma distribution
	for(int i = 0; i< m_Data.length-1; i++) {
	  double p1;
	  try {
	    p1 = gd.cumulativeProbability(newData[i]);
	  } catch (MathException e) {
	    p1 = 0;
	  }
	  double p2;
	  try {
	    p2 = gd.cumulativeProbability(newData[i+1]);
	  } catch (MathException e) {
	    p2 = 0;
	  }
	  g.drawLine(m_AxisBottom.valueToPos(m_Data[i]), m_AxisLeft.valueToPos(p1), m_AxisBottom.valueToPos(m_Data[i+1]), m_AxisLeft.valueToPos(p2));
	}
      }
    }
  }

  @Override
  public String globalInfo() {
    return "Paints the transformed gamma distribution";
  }

  @Override
  public boolean hasFitLine() {
    return false;
  }
}