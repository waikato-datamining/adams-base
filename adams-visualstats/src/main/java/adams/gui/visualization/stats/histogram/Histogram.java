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
 * Histogram.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.histogram;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.ArrayHistogram.BinCalculation;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.histogram.HistogramOptions.BoxType;
import adams.gui.visualization.stats.paintlet.HistogramPaintlet;

import java.awt.BorderLayout;
import java.awt.Graphics;

/**
 * Class that displays a histogram displaying the data provided.
 *
 * @author msf8
 * @version $Revision$
 */
public class Histogram
  extends PaintablePanel{

  /** for serialization */
  private static final long serialVersionUID = -4366437103496819542L;

  /**Data to be plotted */
  protected SpreadSheet m_Data;

  /** double array to plot. */
  protected Double[] m_Array;
  
  /** Panel for displaying the histogram */
  protected HistogramPanel m_Plot;

  /**Paintlet for plotting the data */
  protected HistogramPaintlet m_Val;

  /**Options for the histogram */
  protected HistogramOptions m_HistOptions;

  /** Position of the residuals attribute within the data */
  protected int m_Index;
  
  /** the name to use for the x-axis. */
  protected String m_DataName;

  /** the data to plot. */
  protected double[][] m_Plotdata;
  
  /** the bin width. */
  protected double m_BinWidth;

  /** the name of the x-axis. */
  protected String m_Name;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_HistOptions = new HistogramOptions();
    m_Data = null;
    m_Array       = null;
    m_DataName    = "";
    m_Index       = 0;
    m_Plotdata    = new double[0][2];
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());
    m_Plot = new HistogramPanel();
    m_Plot.addPaintListener(this);
    m_Val = new HistogramPaintlet();
    m_Val.setPanel(this);
    add(m_Plot, BorderLayout.CENTER);
  }
  
  /**
   * Set the options for this histogram
   * @param val			Histogramoptions object containing all the options
   */
  public void setOptions(HistogramOptions val) {
    m_HistOptions = val;
    m_HistOptions.getAxisX().configure(m_Plot, Axis.BOTTOM);
    m_HistOptions.getAxisY().configure(m_Plot, Axis.LEFT);
    removePaintlet(m_Val);
    m_Val = (HistogramPaintlet) m_HistOptions.getPaintlet().shallowCopy(true);
    m_Val.setPanel(this);
  }
  
  /**
   * Returns the option for this histogram.
   * 
   * @return		the options
   */
  public HistogramOptions getOptions() {
    return m_HistOptions;
  }

  /**
   * Set the data for the histogram
   * @param value			Data for the histogram plot
   */
  public void setData(SpreadSheet value) {
    m_Data = value;
    m_Array     = null;
    update();
  }
  
  /**
   * Returns the instanecs for the histogram.
   * 
   * @return		the data, null if not set
   */
  public SpreadSheet getData() {
    return m_Data;
  }
  
  /**
   * Sets the array for this histogram.
   * 
   * @param value	the array
   */
  public void setArray(Double[] value) {
    m_Array     = value;
    m_Data = null;
    update();
  }
  
  /**
   * Returns the current array.
   * 
   * @return		the array, null if not set
   */
  public Double[] getArray() {
    return m_Array;
  }

  /**
   * Sets the name for the x-axis.
   * 
   * @param value	the name
   */
  public void setDataName(String value) {
    m_DataName = value;
  }
  
  /**
   * Returns the name for the x-axis.
   * 
   * @return		the name
   */
  public String getDataName() {
    return m_DataName;
  }
  
  /**
   * Returns the plot panel of the panel, null if no panel present.
   *
   * @return		the plot panel
   */
  @Override
  public PlotPanel getPlot() {
    return m_Plot;
  }

  /**
   * Prepares the update, i.e., calculations etc.
   */
  @Override
  public void prepareUpdate() {
    //Calculate bin size and frequency
    StatisticContainer cont;
    ArrayHistogram<Number> aHist = new ArrayHistogram<>();
    Number[] numData;
    if (m_Data != null) {
      numData = StatUtils.toNumberArray(SpreadSheetUtils.getNumericColumn(m_Data, m_Index));
      m_Name  = m_Data.getColumnName(m_Index);
    }
    else {
      numData = m_Array;
      m_Name  = "Data";
    }
    if (m_DataName.length() > 0)
      m_Name = m_DataName;
    // no data to plot?
    if (numData == null) {
      m_Plotdata = new double[0][2];
      return;
    }
    aHist.add(numData);
    aHist.setBinWidth(m_HistOptions.m_WidthBin);
    aHist.setNumBins(m_HistOptions.m_NumBins);
    BinCalculation bc;
    //Set the bincalculation type
    if(m_HistOptions.m_BoxType == BoxType.DENSITY)
      bc = BinCalculation.DENSITY;
    else
      bc = BinCalculation.MANUAL;
    aHist.setBinCalculation(bc);
    //calculates depending on bin calculation type, will use
    //binwidth or numbins depending on type
    cont = aHist.calculate();
    int numBins = (Integer) cont.getMetaData(ArrayHistogram.METADATA_NUMBINS);
    //Start of intervals for bins
    double[] binX = (double[])cont.getMetaData(ArrayHistogram.METADATA_BINX);
    m_BinWidth = (Double) cont.getMetaData(ArrayHistogram.METADATA_BINWIDTH);
    m_Plotdata = new double[numBins][2];
    //fill 2d array with positions of bins and count for each
    for (int i = 0; i < m_Plotdata.length; i++) {
      m_Plotdata[i][1] = (Double) cont.getCell(0, i);
      m_Plotdata[i][0] = binX[i];
    }
    
    AxisPanel axisBottom = getPlot().getAxis(Axis.BOTTOM);
    AxisPanel axisLeft = getPlot().getAxis(Axis.LEFT);
    axisBottom.setMinimum(m_Plotdata[0][0]);
    axisBottom.setMaximum(m_Plotdata[m_Plotdata.length-1][0] + m_BinWidth);
    axisBottom.setAxisName(m_Name);
    axisLeft.setMinimum(0);
    
    //find the maximum frequency for a bin
    double max = m_Plotdata[1][0];
    for(int i = 1; i< m_Plotdata.length; i++) {
      if(m_Plotdata[i][1]> max) {
	max = m_Plotdata[i][1];
      }
    }
    //y axis shows number in bin/ width of bin
    axisLeft.setMaximum(max/m_BinWidth);
    axisLeft.setAxisName("Frequency");
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return(m_Plot != null);
  }

  /**
   * Set the index of residuals attribute within the data
   * @param val			Position of residuals attribute
   */
  public void setIndex(int val) {
    m_Index = val;
    update();
  }
  
  /**
   * Returns the currently set index.
   * 
   * @return		the index
   */
  public int getIndex() {
    return m_Index;
  }
  
  /**
   * Returns the plot data.
   * 
   * @return		the data
   */
  public double[][] getPlotdata() {
    return m_Plotdata;
  }
  
  /**
   * Returns the bin width.
   * 
   * @return		the width
   */
  public double getBinWidth() {
    return m_BinWidth;
  }
}