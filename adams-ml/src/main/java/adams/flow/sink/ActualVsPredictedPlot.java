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
import adams.data.conversion.StringToDouble;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.control.ArrayProcess;
import adams.flow.control.FlowStructureModifier;
import adams.flow.control.LocalScopeTransformer;
import adams.flow.control.PlotContainerUpdater.PlotContainerValue;
import adams.flow.control.Sequence;
import adams.flow.control.Tee;
import adams.flow.core.ActorHandler;
import adams.flow.sink.sequenceplotter.ErrorCrossPaintlet;
import adams.flow.transformer.Convert;
import adams.flow.transformer.Max;
import adams.flow.transformer.Min;
import adams.flow.transformer.SetPlotContainerValue;
import adams.flow.transformer.SetVariable;
import adams.flow.transformer.Sort;
import adams.flow.transformer.SpreadSheetInfo;
import adams.flow.transformer.SpreadSheetInfo.InfoType;
import adams.flow.transformer.SpreadSheetPlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;
import adams.flow.transformer.plotgenerator.XYWithErrorsPlotGenerator;
import adams.gui.visualization.sequence.AbstractXYSequencePaintlet;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.PaintletWithFixedXYRange;
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
  extends AbstractSink
  implements FlowStructureModifier {

  private static final long serialVersionUID = -1277441640187943194L;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the column with the error values (optional). */
  protected SpreadSheetColumnIndex m_Error;

  /** the minimum to use for the actual values (neg inf = restriction). */
  protected double m_ActualMin;

  /** the maximum to use for the actual values (pos inf = restriction). */
  protected double m_ActualMax;

  /** the minimum to use for the predicted values (neg inf = restriction). */
  protected double m_PredictedMin;

  /** the maximum to use for the predicted values (pos inf = restriction). */
  protected double m_PredictedMax;

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
      "actual-min", "actualMin",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "actual-max", "actualMax",
      Double.POSITIVE_INFINITY);

    m_OptionManager.add(
      "predicted", "predicted",
      new SpreadSheetColumnIndex("Predicted"));

    m_OptionManager.add(
      "predicted-min", "predictedMin",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "predicted-max", "predictedMax",
      Double.POSITIVE_INFINITY);

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
   * Sets the lower limit in use for the actual values.
   *
   * @param value	the limit, neg inf if unlimited
   */
  public void setActualMin(double value) {
    m_ActualMin = value;
    reset();
  }

  /**
   * Returns the lower limit in use for the actual values.
   *
   * @return		the limit, neg inf if unlimited
   */
  public double getActualMin() {
    return m_ActualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualMinTipText() {
    return "The minimum to use for the display of the actual axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the upper limit in use for the actual values.
   *
   * @param value	the limit, pos inf if unlimited
   */
  public void setActualMax(double value) {
    m_ActualMax = value;
    reset();
  }

  /**
   * Returns the upper limit in use for the actual values.
   *
   * @return		the limit, pos inf if unlimited
   */
  public double getActualMax() {
    return m_ActualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualMaxTipText() {
    return "The maximum to use for the display of the actual axis; use " + Double.NaN + " for unlimited.";
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
   * Sets the lower limit in use for the predicted values.
   *
   * @param value	the limit, neg inf if unlimited
   */
  public void setPredictedMin(double value) {
    m_PredictedMin = value;
    reset();
  }

  /**
   * Returns the lower limit in use for the predicted values.
   *
   * @return		the limit, neg inf if unlimited
   */
  public double getPredictedMin() {
    return m_PredictedMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedMinTipText() {
    return "The minimum to use for the display of the predicted axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the upper limit in use for the predicted values.
   *
   * @param value	the limit, pos inf if unlimited
   */
  public void setPredictedMax(double value) {
    m_PredictedMax = value;
    reset();
  }

  /**
   * Returns the upper limit in use for the predicted values.
   *
   * @return		the limit, pos inf if unlimited
   */
  public double getPredictedMax() {
    return m_PredictedMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedMaxTipText() {
    return "The maximum to use for the display of the predicted axis; use " + Double.NaN + " for unlimited.";
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
   * Returns whether the actor is modifying the structure.
   *
   * @return		true if the actor is modifying the structure
   */
  public boolean isModifyingStructure() {
    return !getSkip();
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
    Sequence			seq;
    LocalScopeTransformer 	local;
    Tee				teeName;
    Tee 			teeAct;
    Tee				teePred;
    SetVariable 		svName1;
    SetVariable			svName2;
    SetVariable			svMin;
    SetVariable			svMax;
    Sort			sort;
    ArrayProcess		arrayProc;
    Convert			conv;
    Min				min;
    Max 			max;
    SpreadSheetInfo		info;
    SpreadSheetPlotGenerator	plotgen;
    XYPlotGenerator		xy;
    XYWithErrorsPlotGenerator 	xye;
    SetPlotContainerValue	setplot;
    Tee				teePlot;
    SimplePlot			plot;
    int				index;
    AbstractXYSequencePaintlet	paintlet;
    PaintletWithFixedXYRange	fixedPaintlet;

    result = super.setUp();

    if (result == null) {
      seq = new Sequence();
      seq.setName(getName());

      local = new LocalScopeTransformer();
      seq.add(local);

      // spreadsheet name
      teeName = new Tee();
      teeName.setName("spreadsheet name");
      local.add(teeName);
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

      // actual - min
      teeAct = new Tee();
      teeAct.setName("actual - min");
      local.add(teeAct);
      {
	if (Double.isInfinite(m_ActualMin)) {
	  info = new SpreadSheetInfo();
	  info.setColumnIndex(m_Actual.getClone());
	  info.setType(InfoType.CELL_VALUES);
	  info.setOutputArray(true);
	  teeAct.add(info);

	  arrayProc = new ArrayProcess();
	  conv = new Convert();
	  conv.setConversion(new StringToDouble());
	  arrayProc.add(conv);
	  teeAct.add(arrayProc);

	  sort = new Sort();
	  teeAct.add(sort);

	  min = new Min();
	  teeAct.add(min);

	  svMin = new SetVariable();
	  svMin.setVariableName(new VariableName("__actmin__"));
	  teeAct.add(svMin);
	}
	else {
	  svMin = new SetVariable();
	  svMin.setVariableName(new VariableName("__actmin__"));
	  svMin.setVariableValue(new BaseText(Double.toString(m_ActualMin)));
	  teeAct.add(svMin);
	}
      }

      // actual - max
      teeAct = new Tee();
      teeAct.setName("actual - max");
      local.add(teeAct);
      {
	if (Double.isInfinite(m_ActualMax)) {
	  info = new SpreadSheetInfo();
	  info.setColumnIndex(m_Actual.getClone());
	  info.setType(InfoType.CELL_VALUES);
	  info.setOutputArray(true);
	  teeAct.add(info);

	  arrayProc = new ArrayProcess();
	  conv = new Convert();
	  conv.setConversion(new StringToDouble());
	  arrayProc.add(conv);
	  teeAct.add(arrayProc);

	  sort = new Sort();
	  teeAct.add(sort);

	  max = new Max();
	  teeAct.add(max);

	  svMax = new SetVariable();
	  svMax.setVariableName(new VariableName("__actmax__"));
	  teeAct.add(svMax);
	}
	else {
	  svMax = new SetVariable();
	  svMax.setVariableName(new VariableName("__actmax__"));
	  svMax.setVariableValue(new BaseText(Double.toString(m_ActualMax)));
	  teeAct.add(svMax);
	}
      }

      // predicted - min
      teePred = new Tee();
      teePred.setName("predicted - min");
      local.add(teePred);
      {
	if (Double.isInfinite(m_PredictedMin)) {
	  info = new SpreadSheetInfo();
	  info.setColumnIndex(m_Predicted.getClone());
	  info.setType(InfoType.CELL_VALUES);
	  info.setOutputArray(true);
	  teePred.add(info);

	  arrayProc = new ArrayProcess();
	  conv = new Convert();
	  conv.setConversion(new StringToDouble());
	  arrayProc.add(conv);
	  teePred.add(arrayProc);

	  sort = new Sort();
	  teePred.add(sort);

	  min = new Min();
	  teePred.add(min);

	  svMin = new SetVariable();
	  svMin.setVariableName(new VariableName("__predmin__"));
	  teePred.add(svMin);
	}
	else {
	  svMin = new SetVariable();
	  svMin.setVariableName(new VariableName("__predmin__"));
	  svMin.setVariableValue(new BaseText(Double.toString(m_PredictedMin)));
	  teePred.add(svMin);
	}
      }

      // predicted - max
      teePred = new Tee();
      teePred.setName("predicted - max");
      local.add(teePred);
      {
	if (Double.isInfinite(m_PredictedMax)) {
	  info = new SpreadSheetInfo();
	  info.setColumnIndex(m_Predicted.getClone());
	  info.setType(InfoType.CELL_VALUES);
	  info.setOutputArray(true);
	  teePred.add(info);

	  arrayProc = new ArrayProcess();
	  conv = new Convert();
	  conv.setConversion(new StringToDouble());
	  arrayProc.add(conv);
	  teePred.add(arrayProc);

	  sort = new Sort();
	  teePred.add(sort);

	  max = new Max();
	  teePred.add(max);

	  svMax = new SetVariable();
	  svMax.setVariableName(new VariableName("__predmax__"));
	  teePred.add(svMax);
	}
	else {
	  svMax = new SetVariable();
	  svMax.setVariableName(new VariableName("__predmax__"));
	  svMax.setVariableValue(new BaseText(Double.toString(m_PredictedMax)));
	  teePred.add(svMax);
	}
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
      local.add(plotgen);

      // plot name
      setplot = new SetPlotContainerValue();
      setplot.setContainerValue(PlotContainerValue.PLOT_NAME);
      setplot.getOptionManager().setVariableForProperty("value", "__relname__");
      local.add(setplot);

      // plot
      plot = new SimplePlot();
      plot.setName("Plot");
      plot.setOverlayPaintlet(new StraightLineOverlayPaintlet());
      plot.getAxisX().setLabel("Actual");
      plot.getAxisY().setLabel("Predicted");
      if (m_Error.isEmpty())
        paintlet = new CrossPaintlet();
      else
        paintlet = new ErrorCrossPaintlet();
      fixedPaintlet = new PaintletWithFixedXYRange();
      fixedPaintlet.getOptionManager().setVariableForProperty("minX", "__actmin__");
      fixedPaintlet.getOptionManager().setVariableForProperty("maxX", "__actmax__");
      fixedPaintlet.getOptionManager().setVariableForProperty("minY", "__predmin__");
      fixedPaintlet.getOptionManager().setVariableForProperty("maxY", "__predmax__");
      fixedPaintlet.setPaintlet(paintlet);
      plot.setPaintlet(fixedPaintlet);

      teePlot = new Tee();
      teePlot.setName("plot");
      teePlot.add(plot);
      local.add(teePlot);

      // add to flow
      index = index();
      ((ActorHandler) getParent()).set(index, seq);
      seq.setVariables(getParent().getVariables());
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
