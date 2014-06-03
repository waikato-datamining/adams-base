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
 * BoxPlotGraph.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.boxplot;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import adams.data.statistics.Percentile;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;

/**
 * Class that displays a single box plot graph.
 * 
 * @author msf8
 * @version $Revision$
 */
public class BoxPlotGraph
  extends PlotPanel {

  /** for serializing */
  private static final long serialVersionUID = 3012367720278639818L;

  /** median for attribute */
  protected double m_Median;

  /**lower quartile for attribute */
  protected double m_Lower;

  /**upper quartile for attribute */
  protected double m_Upper;

  /**Minimum value for attribute */
  protected double m_Min;

  /**maximum value for attribute */
  protected double m_Max;

  /**array for each value within an attribute */
  protected double[] m_Data;

  /**Fill each of the boxes with color */
  protected boolean m_Fill;

  /** Color to fill the boxes */
  protected Color m_Color;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
    m_AxisLeft.setType(Type.ABSOLUTE);
    m_AxisLeft.setTickGenerator(new FancyTickGenerator());
    m_AxisLeft.setNumberFormat("#.##");
    m_AxisLeft.setNthValueToShow(2);
    m_AxisLeft.setBottomMargin(0.05);
    m_AxisLeft.setTopMargin(0.05);
    m_AxisLeft.setAxisName(null);
  }
  
  /**
   * Passes in the data to construct the box plot graph
   * @param i		Instance data
   * @param att		Attribute being graphed in this box plot
   */
  public void pass(Instances i, Attribute att) {
    //position of attribute in instance data
    int pos = -1;
    //finds position of attribute
    for(int t = 0; t<i.numAttributes(); t++) {
      if(i.attribute(t) == att)
	pos = t;
    }
    //gets all data for the specific attribute
    m_Data = i.attributeToDoubleArray(pos);
    //finding statistics for drawing box plot
    m_Median = StatUtils.median(m_Data);
    m_Min = StatUtils.min(m_Data);
    m_Max = StatUtils.max(m_Data);
    
    
    //version 1
//    Percentile<Double> percent= new Percentile<Double>();
//    List<Double> vec = new ArrayList<Double>();
//    for(double d: m_Data) {
//      vec.add(d);
//    }
//    percent.addAll(vec);
//    m_Lower = percent.getPercentile(0.25);
//    m_Upper = percent.getPercentile(0.75);
    
    //version 2
    Double[] copyArray = new Double[m_Data.length];
    for(int j = 0; j < m_Data.length; j++) {
    	copyArray[j] = m_Data[j];
    }
    Arrays.sort(copyArray);
    
    m_Lower = copyArray[(int)Math.round(((double)(copyArray.length-1)) * .25)];
    m_Upper = copyArray[(int)Math.round(((double)(copyArray.length-1)) * .75)];
    
    
    m_AxisLeft.setMinimum(m_Min);
    m_AxisLeft.setMaximum(m_Max);
  }

  /**
   * Performs the painting on the graphics area
   * @param g		Graphics object for painting on
   */
  public void paintPlot(Graphics g) {
    //positions to plot values in relation to the axis
    int maxPos = m_AxisLeft.valueToPos(m_Max);
    int minPos = m_AxisLeft.valueToPos(m_Min);
    int medianPos = m_AxisLeft.valueToPos(m_Median);
    int lowerPos = m_AxisLeft.valueToPos(m_Lower);
    int upperPos = m_AxisLeft.valueToPos(m_Upper);

    //width of graph, used to set width of box drawing
    int wid = m_PanelContent.getWidth();
    //box 60% of width of content panel
    int widthUse = (int)(0.6*wid);
    int start = (int)(wid*.2);
    int midleUse = (int)(.5*wid);
    g.setColor(Color.BLACK);
    ///line from maximum to minimum value, with horizontal ends
    g.drawLine(midleUse, maxPos, midleUse, minPos);
    g.drawLine((int)(wid*0.4), maxPos, (int)(wid*0.6), maxPos);
    g.drawLine((int)(wid*0.4), minPos, (int)(wid*0.6), minPos);
    g.setColor(Color.GRAY);
    if(m_Fill)
      g.setColor(m_Color);
    else
      g.setColor(Color.WHITE);

    g.fillRect(start, upperPos, widthUse, lowerPos-upperPos);
    g.setColor(Color.BLACK);
    g.drawRect(start, upperPos, widthUse, lowerPos-upperPos);
    //line for median
    g.drawLine(start, medianPos, start + widthUse, medianPos);
  }

  /**
   * Axis are the same for each graph, this sets boundary values
   * @param max			Maximum y value
   * @param min			Minimum y value
   */
  public void axisSame(double max, double min) {
    m_AxisLeft.setMaximum(max);
    m_AxisLeft.setMinimum(min);
    m_AxisLeft.setBottomMargin(0.0);
    m_AxisLeft.setTopMargin(0.0);
  }

  /**
   * Set whether the box plots should be filled with color
   * @param val			True if boxes filled
   */
  public void setFill(boolean val) {
    m_Fill = val;
  }

  /**
   * Set the color used to fill the plots
   * @param val			color of box plots
   */
  public void setColor(Color val) {
    m_Color = val;
  }
}