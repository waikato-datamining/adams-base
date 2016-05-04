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

/**
 * ActualVsPredictedPlot.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.control.PlotContainerUpdater.PlotContainerValue;
import adams.flow.control.Sequence;
import adams.flow.control.Tee;
import adams.flow.core.ActorHandler;
import adams.flow.sink.sequenceplotter.ErrorCrossPaintlet;
import adams.flow.transformer.SetPlotContainerValue;
import adams.flow.transformer.SetVariable;
import adams.flow.transformer.SpreadSheetInfo;
import adams.flow.transformer.SpreadSheetInfo.InfoType;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;
import adams.flow.transformer.plotgenerator.XYWithErrorsPlotGenerator;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActualVsPredictedPlot
  extends AbstractSink {

  private static final long serialVersionUID = -1277441640187943194L;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the column with the error values (optional). */
  protected SpreadSheetColumnIndex m_Error;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots actual vs predicted columns obtained from a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "actual", "actual",
	    new SpreadSheetColumnIndex("Actual"));

    m_OptionManager.add(
	    "predicted", "predicted",
	    new SpreadSheetColumnIndex("Predicted"));

    m_OptionManager.add(
	    "error", "error",
	    new SpreadSheetColumnIndex(""));
  }

  /**
   * Sets the column with the actual values.
   *
   * @param value	the column
   */
  public void setActual(SpreadSheetColumnIndex value) {
    m_Actual = value;
    reset();
  }

  /**
   * Returns the column with the actual values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getActual() {
    return m_Actual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualTipText() {
    return "The column with the actual values.";
  }

  /**
   * Sets the column with the predicted values.
   *
   * @param value	the column
   */
  public void setPredicted(SpreadSheetColumnIndex value) {
    m_Predicted = value;
    reset();
  }

  /**
   * Returns the column with the predicted values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getPredicted() {
    return m_Predicted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedTipText() {
    return "The column with the predicted values.";
  }

  /**
   * Sets the column with the error values.
   *
   * @param value	the column
   */
  public void setError(SpreadSheetColumnIndex value) {
    m_Error = value;
    reset();
  }

  /**
   * Returns the column with the error values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getError() {
    return m_Error;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorTipText() {
    return "The column with the error values.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "actual", m_Actual, "actual: ");
    result += QuickInfoHelper.toString(this, "predicted", m_Predicted, ", predicted: ");
    result += QuickInfoHelper.toString(this, "error", (m_Error.isEmpty() ? "-none-" : m_Error), ", error: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String			result;
    Sequence 			seq;
    Tee				teeName;
    SetVariable 		svName1;
    SetVariable			svName2;
    SpreadSheetInfo		info;
    SpreadSheetPlotGenerator	plotgen;
    XYPlotGenerator		xy;
    XYWithErrorsPlotGenerator 	xye;
    SetPlotContainerValue	setplot;
    SimplePlot			plot;
    int				index;

    result = super.setUp();

    if (result == null) {
      seq = new Sequence();
      seq.setName(getName());

      // spreadsheet name
      teeName = new Tee();
      teeName.setName("spreadsheet name");
      seq.add(teeName);
      {
	svName1 = new SetVariable();
	svName1.setVariableName(new VariableName("__relname__"));
	svName1.setVariableValue(new BaseText("act vs pred"));
	teeName.add(svName1);

	info = new SpreadSheetInfo();
	info.setType(InfoType.NAME);
	teeName.add(info);

	svName2 = new SetVariable();
	svName2.setVariableName(new VariableName("__relname__"));
	teeName.add(svName2);
      }

      // plot generator
      plotgen = new SpreadSheetPlotGenerator();
      if (m_Error.isEmpty()) {
	xy = new XYPlotGenerator();
	xy.setXColumn(m_Actual.getIndex());
	xy.setPlotColumns(m_Predicted.getIndex());
	plotgen.setGenerator(xy);
      }
      else {
	xye = new XYWithErrorsPlotGenerator();
	xye.setXColumn(m_Actual);
	xye.setYColumn(m_Predicted);
	xye.setYErrorColumns(new SpreadSheetColumnRange(m_Error.getIndex()));
	plotgen.setGenerator(xye);
      }
      seq.add(plotgen);

      // plot name
      setplot = new SetPlotContainerValue();
      setplot.setContainerValue(PlotContainerValue.PLOT_NAME);
      setplot.getOptionManager().setVariableForProperty("value", "__relname__");
      seq.add(setplot);

      // plot
      plot = new SimplePlot();
      plot.setName("Plot");
      plot.setOverlayPaintlet(new StraightLineOverlayPaintlet());
      plot.getAxisX().setLabel("Actual");
      plot.getAxisY().setLabel("Predicted");
      if (m_Error.isEmpty())
	plot.setPaintlet(new CrossPaintlet());
      else
	plot.setPaintlet(new ErrorCrossPaintlet());
      seq.add(plot);

      // add to flow
      index = index();
      ((ActorHandler) getParent()).set(index, seq);
      result = getParent().setUp();
      setParent(null);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
