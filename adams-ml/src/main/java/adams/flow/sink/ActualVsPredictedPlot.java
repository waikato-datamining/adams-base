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
 * Plots actual vs predicted columns obtained from a spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ActualVsPredictedPlot
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 350
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-actual &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: actual)
 * &nbsp;&nbsp;&nbsp;The column with the actual values.
 * &nbsp;&nbsp;&nbsp;default: Actual
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-actual-min &lt;double&gt; (property: actualMin)
 * &nbsp;&nbsp;&nbsp;The minimum to use for the display of the actual axis; use NaN for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -Infinity
 * </pre>
 * 
 * <pre>-actual-max &lt;double&gt; (property: actualMax)
 * &nbsp;&nbsp;&nbsp;The maximum to use for the display of the actual axis; use NaN for unlimited.
 * &nbsp;&nbsp;&nbsp;default: Infinity
 * </pre>
 * 
 * <pre>-predicted &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: predicted)
 * &nbsp;&nbsp;&nbsp;The column with the predicted values.
 * &nbsp;&nbsp;&nbsp;default: Predicted
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-predicted-min &lt;double&gt; (property: predictedMin)
 * &nbsp;&nbsp;&nbsp;The minimum to use for the display of the predicted axis; use NaN for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -Infinity
 * </pre>
 * 
 * <pre>-predicted-max &lt;double&gt; (property: predictedMax)
 * &nbsp;&nbsp;&nbsp;The maximum to use for the display of the predicted axis; use NaN for unlimited.
 * &nbsp;&nbsp;&nbsp;default: Infinity
 * </pre>
 * 
 * <pre>-error &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: error)
 * &nbsp;&nbsp;&nbsp;The column with the error values.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActualVsPredictedPlot
  extends AbstractSink
  implements FlowStructureModifier {

  private static final long serialVersionUID = -1277441640187943194L;

  /** whether to use just the actor name or the full name as title. */
  protected boolean m_ShortTitle;

  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /** the X position of the dialog. */
  protected int m_X;

  /** the Y position of the dialog. */
  protected int m_Y;

  /** whether to display the panel in the editor rather than in a separate frame. */
  protected boolean m_DisplayInEditor;

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
      "short-title", "shortTitle",
      getDefaultShortTitle());

    m_OptionManager.add(
      "display-in-editor", "displayInEditor",
      getDefaultDisplayInEditor());

    m_OptionManager.add(
      "width", "width",
      getDefaultWidth(), -1, null);

    m_OptionManager.add(
      "height", "height",
      getDefaultHeight(), -1, null);

    m_OptionManager.add(
      "x", "x",
      getDefaultX(), -3, null);

    m_OptionManager.add(
      "y", "y",
      getDefaultY(), -3, null);

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
   * Returns the default value for short title.
   *
   * @return		the default
   */
  protected boolean getDefaultShortTitle() {
    return false;
  }

  /**
   * Returns the default value for displaying the panel in the editor
   * rather than in a separate frame.
   *
   * @return		the default
   */
  protected boolean getDefaultDisplayInEditor() {
    return false;
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  protected int getDefaultX() {
    return -1;
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  protected int getDefaultY() {
    return -1;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 350;
  }

  /**
   * Sets whether to use just the name of the actor or the full name.
   *
   * @param value 	if true just the name will get used, otherwise the full name
   */
  public void setShortTitle(boolean value) {
    m_ShortTitle = value;
    reset();
  }

  /**
   * Returns whether to use just the name of the actor or the full name.
   *
   * @return 		true if just the name used, otherwise full name
   */
  public boolean getShortTitle() {
    return m_ShortTitle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shortTitleTipText() {
    return "If enabled uses just the name for the title instead of the actor's full name.";
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the dialog.";
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the dialog.";
  }

  /**
   * Sets the X position of the dialog.
   *
   * @param value 	the X position
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the currently set X position of the dialog.
   *
   * @return 		the X position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String xTipText() {
    return "The X position of the dialog (>=0: absolute, -1: left, -2: center, -3: right).";
  }

  /**
   * Sets the Y position of the dialog.
   *
   * @param value 	the Y position
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the currently set Y position of the dialog.
   *
   * @return 		the Y position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String yTipText() {
    return "The Y position of the dialog (>=0: absolute, -1: top, -2: center, -3: bottom).";
  }

  /**
   * Sets whether to display the panel in the flow editor rather than
   * in a separate frame.
   *
   * @param value 	true if to display in editor
   */
  public void setDisplayInEditor(boolean value) {
    m_DisplayInEditor = value;
    reset();
  }

  /**
   * Returns whether to display the panel in the flow editor rather than
   * in a separate frame.
   *
   * @return 		true if to display in editor
   */
  public boolean getDisplayInEditor() {
    return m_DisplayInEditor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayInEditorTipText() {
    return
	"If enabled displays the panel in a tab in the flow editor rather "
	+ "than in a separate frame.";
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
    String	value;

    if (m_X == -1)
      value = "left";
    else if (m_X == -2)
      value = "center";
    else if (m_X == -3)
      value = "right";
    else
      value = "" + m_X;
    result = QuickInfoHelper.toString(this, "x", value, "X:");

    if (m_Y == -1)
      value = "top";
    else if (m_Y == -2)
      value = "center";
    else if (m_Y == -3)
      value = "bottom";
    else
      value = "" + m_Y;
    result += QuickInfoHelper.toString(this, "y", value, ", Y:");

    result += QuickInfoHelper.toString(this, "width", m_Width, ", W:");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", H:");
    result += QuickInfoHelper.toString(this, "shortTitle", m_ShortTitle, "short title", ", ");

    result += QuickInfoHelper.toString(this, "actual", m_Actual, ", actual: ");
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
    Tee				tee;
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
    PaintletWithFixedXYRange fixedPaintlet;

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
	svName1.setName("relation name (default)");
	svName1.setVariableName(new VariableName("__relname__"));
	svName1.setVariableValue(new BaseText("act vs pred"));
	teeName.add(svName1);

	info = new SpreadSheetInfo();
	info.setType(InfoType.NAME);
	teeName.add(info);

	svName2 = new SetVariable();
	svName2.setName("relation name");
	svName2.setVariableName(new VariableName("__relname__"));
	teeName.add(svName2);
      }

      // actual
      teeAct = new Tee();
      teeAct.setName("actual");
      local.add(teeAct);
      {
	if (Double.isInfinite(m_ActualMin) || Double.isInfinite(m_ActualMax)) {
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
	}

	// min
	if (Double.isInfinite(m_ActualMin)) {
	  tee = new Tee();
	  tee.setName("min");
	  teeAct.add(tee);

	  min = new Min();
	  tee.add(min);

	  svMin = new SetVariable();
	  svMin.setName("actual min");
	  svMin.setVariableName(new VariableName("__actmin__"));
	  tee.add(svMin);
	}
	else {
	  svMin = new SetVariable();
	  svMin.setName("actual min");
	  svMin.setVariableName(new VariableName("__actmin__"));
	  svMin.setVariableValue(new BaseText(Double.toString(m_ActualMin)));
	  teeAct.add(svMin);
	}

	// max
	if (Double.isInfinite(m_ActualMax)) {
	  tee = new Tee();
	  tee.setName("max");
	  teeAct.add(tee);

	  max = new Max();
	  tee.add(max);

	  svMax = new SetVariable();
	  svMax.setName("actual max");
	  svMax.setVariableName(new VariableName("__actmax__"));
	  tee.add(svMax);
	}
	else {
	  svMax = new SetVariable();
	  svMax.setName("actual max");
	  svMax.setVariableName(new VariableName("__actmax__"));
	  svMax.setVariableValue(new BaseText(Double.toString(m_ActualMax)));
	  teeAct.add(svMax);
	}
      }

      // predicted
      teePred = new Tee();
      teePred.setName("predicted");
      local.add(teePred);
      {
	if (Double.isInfinite(m_PredictedMin) || Double.isInfinite(m_PredictedMax)) {
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
	}

	// min
	if (Double.isInfinite(m_PredictedMin)) {
	  tee = new Tee();
	  tee.setName("min");
	  teePred.add(tee);

	  min = new Min();
	  tee.add(min);

	  svMin = new SetVariable();
	  svMin.setName("predicted min");
	  svMin.setVariableName(new VariableName("__predmin__"));
	  tee.add(svMin);
	}
	else {
	  svMin = new SetVariable();
	  svMin.setName("predicted min");
	  svMin.setVariableName(new VariableName("__predmin__"));
	  svMin.setVariableValue(new BaseText(Double.toString(m_PredictedMin)));
	  teePred.add(svMin);
	}

	// max
	if (Double.isInfinite(m_PredictedMax)) {
	  tee = new Tee();
	  tee.setName("max");
	  teePred.add(tee);

	  max = new Max();
	  tee.add(max);

	  svMax = new SetVariable();
	  svMax.setName("predicted max");
	  svMax.setVariableName(new VariableName("__predmax__"));
	  tee.add(svMax);
	}
	else {
	  svMax = new SetVariable();
	  svMax.setName("predicted max");
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
      plot.setName(getName());
      plot.setShortTitle(getShortTitle());
      plot.setDisplayInEditor(getDisplayInEditor());
      plot.setX(getX());
      plot.setY(getY());
      plot.setWidth(getWidth());
      plot.setHeight(getHeight());
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
