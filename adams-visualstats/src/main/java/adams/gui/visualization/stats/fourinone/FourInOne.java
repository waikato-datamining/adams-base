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
 * FourInOne.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.fourinone;

import adams.core.Index;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.histogram.Histogram;
import adams.gui.visualization.stats.histogram.HistogramOptions;
import adams.gui.visualization.stats.probabilityplot.NormalPlot;
import adams.gui.visualization.stats.probabilityplot.NormalPlotOptions;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Class that displays a four-in-1 plot containing a histogram, vs order plot,
 * vs fit plot and a normal probability plot.
 *
 * @author msf8
 * @version $Revision$
 */
public class FourInOne
extends BasePanel{

  /** for serialization */
  private static final long serialVersionUID = -993210228989776486L;

  /**Instances containing the data */
  protected SpreadSheet m_Data;

  /**Panel for displaying the four plots */
  protected JPanel m_Centre;

  /** Index for the position of the residuals attribute */
  protected int m_Index;

  /**Options for the normal probability plot */
  protected NormalPlotOptions m_NormOptions;

  /**Options for the histogram plot */
  protected HistogramOptions m_HistogramOptions;

  /**options for the vs fit plot */
  protected VersusFitOptions m_VsFitOptions;

  /**options for the vs order plot */
  protected VersusOrderOptions m_VsOrderOptions;

  /**Index for setting the position of the actual attribute */
  protected Index m_Act;

  /**index for setting the position of the predicted attribute */
  protected Index m_Pred;

  /**Position of the actual attribute */
  protected int m_ActInd;

  /**Position of the predicted attribute */
  protected int m_PredInd;

  /**
   * Set the instances used in the four-in-plot
   * @param val		Instances for the plot
   */
  public void setData(SpreadSheet val) {
    m_Data = val;
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());
    m_Centre = new JPanel();
    GridLayout gridLay = new GridLayout(2,0);
    gridLay.setHgap(10);
    gridLay.setVgap(10);
    m_Centre.setLayout(gridLay);
    add(m_Centre, BorderLayout.CENTER);
  }

  /**
   * Called by the class that creates this four-in-one class once the fields
   * have been set
   */
  public void reset() {
    m_Act.setMax(m_Data.getColumnCount());
    m_Pred.setMax(m_Data.getColumnCount());
    //set the position of the actual attribute using the index provided
    try {
      m_ActInd = m_Act.getIntIndex();
      if(m_ActInd == -1) {
	System.err.println("Error: default attribute used");
	m_ActInd = 0;
      }
    }
    catch(Exception e) {
      m_ActInd = 0;
      System.err.println("Error: default attribute used");
    }

    //set the position of the predicted attribute using the index provided
    try {
      m_PredInd = m_Pred.getIntIndex();
      if(m_PredInd == -1) {
	System.err.println("Error: default attribute used");
	m_PredInd = 0;
      }
    }
    catch(Exception e) {
      m_PredInd = 0;
      System.err.println("Error: default attribute used");
    }

    JPanel top;
    //panel for normal probability plot
    JPanel normPanel = new JPanel(new BorderLayout());
    m_Centre.add(normPanel);
    //panel for vs fit plot
    JPanel vsFitPanel = new JPanel(new BorderLayout());
    m_Centre.add(vsFitPanel);
    //panel for histogram plot
    JPanel histogramPanel = new JPanel(new BorderLayout());
    m_Centre.add(histogramPanel);
    //panel for vs order plot
    JPanel vsOrderPanel = new JPanel(new BorderLayout());
    m_Centre.add(vsOrderPanel);

    //new set of instances for the graphs, contains a residuals attribute
    SpreadSheet sheet = m_Data.getClone();
    sheet.insertColumn(sheet.getColumnCount(), "residuals");
    for (int i = 0; i < sheet.getRowCount(); i++) {
      Row row = sheet.getRow(i);
      if (row.hasCell(m_ActInd) && row.hasCell(m_PredInd)) {
	Cell act = row.getCell(m_ActInd);
	Cell pred = row.getCell(m_PredInd);
	if (!act.isMissing() && !pred.isMissing() && act.isNumeric() && pred.isNumeric())
	  row.getCell(sheet.getColumnCount() - 1).setContent(act.toDouble() - pred.toDouble());
      }
    }
    m_Index = sheet.getColumnCount() - 1;

    //Normal plot
    NormalPlot norm = new NormalPlot();
    norm.setData(sheet);
    norm.setIndex(m_Index);
    norm.setOptions(m_NormOptions);
    normPanel.add(norm, BorderLayout.CENTER);
    JLabel normTitle = new JLabel("Normal Probability Plot");
    top = new JPanel(new FlowLayout());
    normPanel.add(top, BorderLayout.NORTH);
    top.add(normTitle);
    normPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    //vs fit plot
    VersusFit vsFit = new VersusFit();
    vsFit.setData(sheet);
    vsFit.setIndices(m_Index, m_PredInd);
    vsFit.setOptions(m_VsFitOptions);
    vsFitPanel.add(vsFit, BorderLayout.CENTER);
    JLabel vsFitTitle = new JLabel("Versus fit");
    top = new JPanel(new FlowLayout());
    vsFitPanel.add(top, BorderLayout.NORTH);
    top.add(vsFitTitle);
    vsFitPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    //Histogram plot
    Histogram hist = new Histogram();
    hist.setData(sheet);
    hist.setOptions(m_HistogramOptions);
    hist.setIndex(m_Index);
    histogramPanel.add(hist, BorderLayout.CENTER);
    JLabel histogramTitle = new JLabel("Histogram");
    top = new JPanel(new FlowLayout());
    histogramPanel.add(top, BorderLayout.NORTH);
    top.add(histogramTitle);
    histogramPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    //Vs order plot
    VersusOrder VsOrder = new VersusOrder();
    VsOrder.setData(sheet);
    VsOrder.setIndex(m_Index);
    VsOrder.setOptions(m_VsOrderOptions);
    vsOrderPanel.add(VsOrder, BorderLayout.CENTER);
    JLabel vsOrderTitle = new JLabel("Versus Order");
    top = new JPanel(new FlowLayout());
    vsOrderPanel.add(top, BorderLayout.NORTH);
    top.add(vsOrderTitle, BorderLayout.CENTER);
    vsOrderPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    m_Centre.repaint();
    m_Centre.revalidate();
    repaint();
  }

  /**
   * Set the options for each of the plots within the four-in-one
   * @param hist		Options for the histogram
   * @param vsFit		Options for the vs fit
   * @param vsOrder	options for the vs order
   * @param norm		options for the normal
   */
  public void setOptions(HistogramOptions hist, VersusFitOptions vsFit, VersusOrderOptions vsOrder, NormalPlotOptions norm) {
    m_HistogramOptions = hist;
    m_VsFitOptions = vsFit;
    m_VsOrderOptions = vsOrder;
    m_NormOptions = norm;
  }

  /**
   * Set the index for the position of the actual attribute
   * @param val		Index for position
   */
  public void setAct(Index val) {
    m_Act = val;
  }

  /**
   * Set the index for the position of the predicted attribute
   * @param val
   */
  public void setPred(Index val) {
    m_Pred = val;
  }
}