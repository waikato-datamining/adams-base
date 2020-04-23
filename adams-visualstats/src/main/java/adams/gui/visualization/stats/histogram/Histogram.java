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
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.histogram;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.ArrayHistogram.BinCalculation;
import adams.data.statistics.StatUtils;
import adams.flow.sink.TextSupplier;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.histogram.HistogramOptions.BoxType;
import adams.gui.visualization.stats.paintlet.HistogramPaintlet;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Class that displays a histogram displaying the data provided.
 *
 * @author msf8
 */
public class Histogram
  extends PaintablePanel
  implements PopupMenuCustomizer, TextSupplier, SpreadSheetSupporter {

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

  /** the ranges. */
  protected String[] m_Ranges;

  /** the bin width. */
  protected double m_BinWidth;

  /** the name of the x-axis. */
  protected String m_Name;

  /** the file chooser for saving a specific sequence. */
  protected SpreadSheetFileChooser m_FileChooser;

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
    m_Ranges      = new String[0];
    m_FileChooser = null;
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
    m_Plot.setPopupMenuCustomizer(this);
    add(m_Plot, BorderLayout.CENTER);

    m_Val = new HistogramPaintlet();
    m_Val.setPanel(this);
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
    m_Data  = value;
    m_Array = null;
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
    m_Array = value;
    m_Data  = null;
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
      if (m_HistOptions.getAxisY().getLabel().isEmpty())
	m_Name  = "Data";
      else
	m_Name  = m_HistOptions.getAxisX().getLabel();
    }
    if (m_DataName.length() > 0)
      m_Name = m_DataName;
    // no data to plot?
    if (numData == null) {
      m_Plotdata = new double[0][2];
      m_Ranges   = new String[0];
      return;
    }
    aHist.add(numData);
    aHist.setBinWidth(m_HistOptions.m_WidthBin);
    aHist.setNumBins(m_HistOptions.m_NumBins);
    BinCalculation bc;
    //Set the bincalculation type
    if (m_HistOptions.m_BoxType == BoxType.DENSITY)
      bc = BinCalculation.DENSITY;
    else
      bc = BinCalculation.MANUAL;
    aHist.setBinCalculation(bc);
    aHist.setDisplayRanges(true);
    //calculates depending on bin calculation type, will use
    //binwidth or numbins depending on type
    cont = aHist.calculate();
    int numBins = (Integer) cont.getMetaData(ArrayHistogram.METADATA_NUMBINS);
    //Start of intervals for bins
    double[] binX = (double[])cont.getMetaData(ArrayHistogram.METADATA_BINX);
    m_BinWidth = (Double) cont.getMetaData(ArrayHistogram.METADATA_BINWIDTH);
    m_Plotdata = new double[numBins][2];
    m_Ranges   = new String[numBins];
    //fill 2d array with positions of bins and count for each
    for (int i = 0; i < m_Plotdata.length; i++) {
      m_Plotdata[i][1] = (Double) cont.getCell(0, i);
      m_Plotdata[i][0] = binX[i];
      m_Ranges[i] = cont.getHeader(i);
    }

    AxisPanel axisBottom = getPlot().getAxis(Axis.BOTTOM);
    AxisPanel axisLeft = getPlot().getAxis(Axis.LEFT);
    axisBottom.setMinimum(m_Plotdata[0][0]);
    axisBottom.setMaximum(m_Plotdata[m_Plotdata.length-1][0] + m_BinWidth);
    axisBottom.setAxisName(m_Name);
    axisLeft.setMinimum(0);

    //find the maximum frequency for a bin
    double max;
    if (numBins > 1) {
      max = m_Plotdata[1][0];
      for (int i = 1; i < m_Plotdata.length; i++) {
	if (m_Plotdata[i][1] > max) {
	  max = m_Plotdata[i][1];
	}
      }
    }
    else {
      max = (double) cont.getMetaData(ArrayHistogram.METADATA_MAXIMUM);
    }
    //y axis shows number in bin/ width of bin
    axisLeft.setMaximum(max);
    if (m_HistOptions.getAxisY().getLabel().isEmpty())
      axisLeft.setAxisName("Frequency");
    else
      axisLeft.setAxisName(m_HistOptions.getAxisY().getLabel());
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
   * Returns the ranges.
   *
   * @return		the ranges
   */
  public String[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the bin width.
   *
   * @return		the width
   */
  public double getBinWidth() {
    return m_BinWidth;
  }

  /**
   * Saves the data as spreadsheet.
   */
  protected void save() {
    int			retVal;
    SpreadSheetWriter 	writer;

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    writer = m_FileChooser.getWriter();
    if (!writer.write(toSpreadSheet(), m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(
	this, "Failed to save data to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Displays the data as spreadsheet.
   */
  protected void showData() {
    SpreadSheetDialog	dialog;

    if (getParentDialog() != null)
      dialog = new SpreadSheetDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Histogram" + (m_DataName.isEmpty() ? "" : " - " + m_DataName));
    dialog.setSpreadSheet(toSpreadSheet());
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		The mouse event
   * @param menu	The menu to customize.
   */
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem menuitem;

    menu.addSeparator();

    menuitem = new JMenuItem("Save data...", GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent ae) -> save());
    menu.add(menuitem);

    menuitem = new JMenuItem("Show data...", GUIHelper.getIcon("spreadsheet.png"));
    menuitem.addActionListener((ActionEvent ae) -> showData());
    menu.add(menuitem);
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet 	result;
    Row			row;
    int			i;

    result = new DefaultSpreadSheet();
    row   = result.getHeaderRow();
    row.addCell("B").setContentAsString(getPlot().getAxis(Axis.LEFT).getAxisName());
    row.addCell("R").setContentAsString("Range");
    row.addCell("V").setContentAsString(getPlot().getAxis(Axis.BOTTOM).getAxisName());
    for (i = 0; i < m_Plotdata.length; i++) {
      row = result.addRow();
      row.addCell("B").setContent(m_Plotdata[i][0]);
      row.addCell("R").setContentAsString(m_Ranges[i]);
      row.addCell("V").setContent(m_Plotdata[i][1]);
    }

    return result;
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    return "Save histogram as...";
  }

  /**
   * Returns a custom file filter for the file chooser.
   *
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("CSV files", "csv");
  }

  /**
   * Supplies the text. May get called even if actor hasn't been executed yet.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    return toSpreadSheet().toString();
  }
}