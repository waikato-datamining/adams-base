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
 * ActualVsPredictedPlot.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.data.DecimalFormatString;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.sink.sequenceplotter.ErrorCrossPaintlet;
import adams.flow.sink.sequenceplotter.PolygonSelectionPaintlet;
import adams.flow.sink.sequenceplotter.SequencePlotContainer;
import adams.flow.sink.sequenceplotter.SequencePlotContainerManager;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.MetaDataColorPaintlet;
import adams.gui.visualization.sequence.MetaDataValuePaintlet;
import adams.gui.visualization.sequence.MultiPaintlet;
import adams.gui.visualization.sequence.PaintletWithFixedXYRange;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import adams.gui.visualization.sequence.metadatacolor.AbstractMetaDataColor;
import adams.gui.visualization.sequence.metadatacolor.Dummy;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
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
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
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
 * <pre>-additional &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: additional)
 * &nbsp;&nbsp;&nbsp;The additional columns to add to the plot containers.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-use-column-names-as-titles &lt;boolean&gt; (property: useColumnNamesAsTitles)
 * &nbsp;&nbsp;&nbsp;If enabled, the column titles in the spreadsheet are used as axes titles.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-swap-axes &lt;boolean&gt; (property: swapAxes)
 * &nbsp;&nbsp;&nbsp;If enabled, the axes get swapped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The (optional) title of the plot.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-plot-name &lt;java.lang.String&gt; (property: plotName)
 * &nbsp;&nbsp;&nbsp;The (optional) name for the plot.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-limit &lt;NONE|ACTUAL|SPECIFIED&gt; (property: limit)
 * &nbsp;&nbsp;&nbsp;The type of limit to impose on the axes; NONE just uses the range determined
 * &nbsp;&nbsp;&nbsp;from the data; ACTUAL uses the min&#47;max from the actual column for both axes;
 * &nbsp;&nbsp;&nbsp; SPECIFIED uses the specified limits or, if a value is 'infinity' then the
 * &nbsp;&nbsp;&nbsp;corresponding value from the determine range.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 *
 * <pre>-show-side-panel &lt;boolean&gt; (property: showSidePanel)
 * &nbsp;&nbsp;&nbsp;If enabled, the side panel gets displayed which allows access to the underlying
 * &nbsp;&nbsp;&nbsp;data for the plot.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-diameter &lt;int&gt; (property: diameter)
 * &nbsp;&nbsp;&nbsp;The diameter of the cross in pixels (if no error data supplied).
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-meta-data-color &lt;adams.gui.visualization.sequence.metadatacolor.AbstractMetaDataColor&gt; (property: metaDataColor)
 * &nbsp;&nbsp;&nbsp;The scheme to use for extracting the color from the meta-data; ignored if
 * &nbsp;&nbsp;&nbsp;adams.gui.visualization.sequence.metadatacolor.Dummy.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.metadatacolor.Dummy
 * </pre>
 *
 * <pre>-use-custom-paintlet &lt;boolean&gt; (property: useCustomPaintlet)
 * &nbsp;&nbsp;&nbsp;If enabled, the custom paintlet is used instead of cross&#47;error paintlet,
 * &nbsp;&nbsp;&nbsp;anti-aliasing and meta-data color scheme.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-paintlet &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; (property: customPaintlet)
 * &nbsp;&nbsp;&nbsp;The custom paintlet to use instead of cross&#47;error paintlet, anti-aliasing
 * &nbsp;&nbsp;&nbsp;and meta-data color scheme.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.MetaDataValuePaintlet -meta-data-color adams.gui.visualization.sequence.metadatacolor.Dummy
 * </pre>
 *
 * <pre>-overlay &lt;adams.gui.visualization.sequence.XYSequencePaintlet&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;The overlays to use in the plot.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.StraightLineOverlayPaintlet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActualVsPredictedPlot
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider, AntiAliasingSupporter {

  private static final long serialVersionUID = -278662766780196125L;

  /**
   * Determines what limits to use on the axes.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum LimitType {
    NONE,
    ACTUAL,
    SPECIFIED
  }

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** whether to use the column names as axes titles. */
  protected boolean m_UseColumnNamesAsTitles;

  /** whether to swap the axes. */
  protected boolean m_SwapAxes;

  /** the column with the error values (optional). */
  protected SpreadSheetColumnIndex m_Error;

  /** the title. */
  protected String m_Title;

  /** the (optional) plot name. */
  protected String m_PlotName;

  /** the limit type. */
  protected LimitType m_Limit;

  /** the minimum to use for the actual values (neg inf = no restriction). */
  protected double m_ActualMin;

  /** the maximum to use for the actual values (pos inf = no restriction). */
  protected double m_ActualMax;

  /** the minimum to use for the predicted values (neg inf = no restriction). */
  protected double m_PredictedMin;

  /** the maximum to use for the predicted values (pos inf = no restriction). */
  protected double m_PredictedMax;

  /** the additional columns in the spreadsheet to add to the plot containers. */
  protected SpreadSheetColumnRange m_Additional;

  /** whether to show the side panel. */
  protected boolean m_ShowSidePanel;

  /** the diameter of the cross. */
  protected int m_Diameter;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /** for obtaining the color from the meta-data. */
  protected AbstractMetaDataColor m_MetaDataColor;

  /** whether to use a custom paintlet. */
  protected boolean m_UseCustomPaintlet;

  /** the custom paintlet. */
  protected XYSequencePaintlet m_CustomPaintlet;

  /** the overlays to use. */
  protected XYSequencePaintlet[] m_Overlays;

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

    m_OptionManager.add(
      "additional", "additional",
      new SpreadSheetColumnRange(""));

    m_OptionManager.add(
      "use-column-names-as-titles", "useColumnNamesAsTitles",
      false);

    m_OptionManager.add(
      "swap-axes", "swapAxes",
      false);

    m_OptionManager.add(
      "title", "title",
      "");

    m_OptionManager.add(
      "plot-name", "plotName",
      "");

    m_OptionManager.add(
      "limit", "limit",
      LimitType.NONE);

    m_OptionManager.add(
      "show-side-panel", "showSidePanel",
      true);

    m_OptionManager.add(
      "diameter", "diameter",
      7, 1, null);

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));

    m_OptionManager.add(
      "meta-data-color", "metaDataColor",
      new Dummy());

    m_OptionManager.add(
      "use-custom-paintlet", "useCustomPaintlet",
      false);

    m_OptionManager.add(
      "custom-paintlet", "customPaintlet",
      new MetaDataValuePaintlet());

    m_OptionManager.add(
      "overlay", "overlays",
      new XYSequencePaintlet[]{new StraightLineOverlayPaintlet()});
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
   * @return		the column
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
   * Sets the additional columns to add to the plot containers.
   *
   * @param value	the columns
   */
  public void setAdditional(SpreadSheetColumnRange value) {
    m_Additional = value;
    reset();
  }

  /**
   * Returns the additional columns to add to the plot containers.
   *
   * @return		the columns
   */
  public SpreadSheetColumnRange getAdditional() {
    return m_Additional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalTipText() {
    return "The additional columns to add to the plot containers.";
  }

  /**
   * Sets whether to use the column names as titles for the axes.
   *
   * @param value	true if to use
   */
  public void setUseColumnNamesAsTitles(boolean value) {
    m_UseColumnNamesAsTitles = value;
    reset();
  }

  /**
   * Returns whether to use the column names as titles for the axes.
   *
   * @return		true if to use
   */
  public boolean getUseColumnNamesAsTitles() {
    return m_UseColumnNamesAsTitles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColumnNamesAsTitlesTipText() {
    return "If enabled, the column titles in the spreadsheet are used as axes titles.";
  }

  /**
   * Sets whether to swap the axes.
   *
   * @param value	true if to swap
   */
  public void setSwapAxes(boolean value) {
    m_SwapAxes = value;
    reset();
  }

  /**
   * Returns whether to swap the axes.
   *
   * @return		true if to swap
   */
  public boolean getSwapAxes() {
    return m_SwapAxes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swapAxesTipText() {
    return "If enabled, the axes get swapped.";
  }

  /**
   * Sets the (optional) title of the plot.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the (optional) title of the plot.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The (optional) title of the plot.";
  }

  /**
   * Sets the (optional) name for the plot.
   *
   * @param value	the name
   */
  public void setPlotName(String value) {
    m_PlotName = value;
    reset();
  }

  /**
   * Returns the (optional) name for the plot.
   *
   * @return		the name
   */
  public String getPlotName() {
    return m_PlotName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNameTipText() {
    return "The (optional) name for the plot.";
  }

  /**
   * Sets the limit to impose on the axes.
   *
   * @param value	the limit type
   */
  public void setLimit(LimitType value) {
    m_Limit = value;
    reset();
  }

  /**
   * Returns the limit to impose on the axes.
   *
   * @return		the limit type
   */
  public LimitType getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return
      "The type of limit to impose on the axes; NONE just uses the range "
	+ "determined from the data; ACTUAL uses the min/max from the actual "
	+ "column for both axes; SPECIFIED uses the specified limits or, if "
	+ "a value is 'infinity' then the corresponding value from the "
	+ "determine range.";
  }

  /**
   * Sets whether to show the side panel.
   *
   * @param value	true if to show
   */
  public void setShowSidePanel(boolean value) {
    m_ShowSidePanel = value;
    reset();
  }

  /**
   * Returns whether to show the side panel.
   *
   * @return		the limit type
   */
  public boolean getShowSidePanel() {
    return m_ShowSidePanel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showSidePanelTipText() {
    return
      "If enabled, the side panel gets displayed which allows access to the "
	+ "underlying data for the plot.";
  }

  /**
   * Sets the cross diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    if (getOptionManager().isValid("diameter", value)) {
      m_Diameter = value;
      reset();
    }
  }

  /**
   * Returns the diameter of the cross.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the cross in pixels (if no error data supplied).";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    reset();
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
    return "If enabled, uses anti-aliasing for drawing.";
  }

  /**
   * Sets the scheme for extracting the color from the meta-data.
   *
   * @param value	the scheme
   */
  public void setMetaDataColor(AbstractMetaDataColor value) {
    m_MetaDataColor = value;
    reset();
  }

  /**
   * Returns the scheme for extracting the color from the meta-data.
   *
   * @return		the scheme
   */
  public AbstractMetaDataColor getMetaDataColor() {
    return m_MetaDataColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataColorTipText() {
    return "The scheme to use for extracting the color from the meta-data; ignored if " + Dummy.class.getName() + ".";
  }

  /**
   * Sets whether to use the custom paintlet.
   *
   * @param value	true if custom
   */
  public void setUseCustomPaintlet(boolean value) {
    m_UseCustomPaintlet = value;
    reset();
  }

  /**
   * Returns whether to use the custom paintlet.
   *
   * @return		true if custom
   */
  public boolean getUseCustomPaintlet() {
    return m_UseCustomPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomPaintletTipText() {
    return "If enabled, the custom paintlet is used instead of cross/error paintlet, anti-aliasing and meta-data color scheme.";
  }

  /**
   * Sets the custom paintlet.
   *
   * @param value	the paintlet
   */
  public void setCustomPaintlet(XYSequencePaintlet value) {
    m_CustomPaintlet = value;
    reset();
  }

  /**
   * Returns the custom paintlet.
   *
   * @return		the paintlet
   */
  public XYSequencePaintlet getCustomPaintlet() {
    return m_CustomPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customPaintletTipText() {
    return "The custom paintlet to use instead of cross/error paintlet, anti-aliasing and meta-data color scheme.";
  }

  /**
   * Sets the overlays to use in the plot.
   *
   * @param value	the overlays
   */
  public void setOverlays(XYSequencePaintlet[] value) {
    m_Overlays = value;
    reset();
  }

  /**
   * Returns the overlays to use in the plot.
   *
   * @return		the overlays
   */
  public XYSequencePaintlet[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlaysTipText() {
    return "The overlays to use in the plot.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "actual", m_Actual, ", actual: ");
    result += QuickInfoHelper.toString(this, "predicted", m_Predicted, ", predicted: ");
    result += QuickInfoHelper.toString(this, "error", (m_Error.isEmpty() ? "-none-" : m_Error), ", error: ");
    result += QuickInfoHelper.toString(this, "additional", (m_Additional.isEmpty() ? "-none-" : m_Additional), ", additional: ");
    result += QuickInfoHelper.toString(this, "plotName", (m_PlotName.isEmpty() ? "-default-" : m_PlotName), ", name: ");
    result += QuickInfoHelper.toString(this, "limit", m_Limit, ", limit: ");
    result += QuickInfoHelper.toString(this, "diameter", m_Diameter, ", diameter: ");
    if (m_UseCustomPaintlet)
      result += QuickInfoHelper.toString(this, "customPaintlet", m_CustomPaintlet, ", paintlet: ");
    else
      result += QuickInfoHelper.toString(this, "metaDataColor", m_MetaDataColor, ", meta-color: ");

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
   * Returns the setup for the X axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisX() {
    AxisPanelOptions	result;
    FancyTickGenerator tick;

    result = new AxisPanelOptions();
    result.setType(Type.DEFAULT);
    result.setLabel(m_SwapAxes ? "Predicted" : "Actual");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(40);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("0.0"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(20);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Returns the setup for the Y axis.
   *
   * @return 		the setup
   */
  protected AxisPanelOptions getDefaultAxisY() {
    AxisPanelOptions	result;
    FancyTickGenerator	tick;

    result = new AxisPanelOptions();
    result.setType(Type.DEFAULT);
    result.setLabel(m_SwapAxes ? "Actual" : "Predicted");
    result.setShowGridLines(true);
    result.setLengthTicks(4);
    result.setNthValueToShow(2);
    result.setWidth(60);
    result.setTopMargin(0.0);
    result.setBottomMargin(0.0);
    result.setCustomFormat(new DecimalFormatString("0.0"));
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.setTickGenerator(tick);

    return result;
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    SequencePlotterPanel 	result;
    XYSequencePaintlet 		paintlet;
    PaintletWithFixedXYRange 	fixedPaintlet;
    MultiPaintlet 		overlays;
    List<XYSequencePaintlet> 	subPaintlets;

    result = new SequencePlotterPanel(m_SwapAxes ? "pred vs act" : "act vs pred");
    if (m_UseCustomPaintlet) {
      paintlet = ObjectCopyHelper.copyObject(m_CustomPaintlet);
    }
    else {
      if (m_Error.isEmpty()) {
	paintlet = new CrossPaintlet();
	((CrossPaintlet) paintlet).setDiameter(m_Diameter);
      }
      else {
	paintlet = new ErrorCrossPaintlet();
	((ErrorCrossPaintlet) paintlet).setDiameter(m_Diameter);
      }
      if (paintlet instanceof AntiAliasingSupporter)
	((AntiAliasingSupporter) paintlet).setAntiAliasingEnabled(m_AntiAliasingEnabled);
      if (paintlet instanceof MetaDataColorPaintlet)
	((MetaDataColorPaintlet) paintlet).setMetaDataColor((AbstractMetaDataColor) OptionUtils.shallowCopy(m_MetaDataColor));
    }
    fixedPaintlet = new PaintletWithFixedXYRange();
    fixedPaintlet.setPaintlet(paintlet);
    result.setDataPaintlet(fixedPaintlet);
    ActorUtils.updateFlowAwarePaintlet(result.getDataPaintlet(), this);
    subPaintlets = new ArrayList<>(Arrays.asList((XYSequencePaintlet[]) OptionUtils.shallowCopy(m_Overlays)));
    subPaintlets.add(new PolygonSelectionPaintlet());
    overlays = new MultiPaintlet();
    overlays.setSubPaintlets(subPaintlets.toArray(new XYSequencePaintlet[0]));
    result.setOverlayPaintlet(overlays);
    ActorUtils.updateFlowAwarePaintlet(result.getOverlayPaintlet(), this);
    getDefaultAxisX().configure(result.getPlot(), Axis.BOTTOM);
    getDefaultAxisY().configure(result.getPlot(), Axis.LEFT);
    result.setColorProvider(new DefaultColorProvider());
    result.setSidePanelVisible(m_ShowSidePanel);
    result.setMouseClickAction(new ViewDataClickAction());
    result.getPlot().clearToolTipAxes();
    result.getPlot().setTipTextCustomizer(null);

    return result;
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((SequencePlotterPanel) m_Panel).getContainerManager().clear();
  }

  /**
   * Adds the spreadsheet to the given panel.
   *
   * @param panel	the panel to the add the spreadsheet data to
   * @param sheet	the data to add
   */
  protected void addData(SequencePlotterPanel panel, SpreadSheet sheet) {
    PaintletWithFixedXYRange		paintlet;
    double[]				act;
    double[]				pred;
    double[]				error;
    double				actMin;
    double				actMax;
    double				predMin;
    double				predMax;
    SequencePlotContainerManager	manager;
    SequencePlotContainer 		cont;
    SequencePlotSequence		seq;
    SequencePlotPoint			point;
    int					i;
    String				id;
    TIntList				indices;
    int[] 				additional;
    Cell 				cell;
    HashMap<String,Object>		meta;

    paintlet = (PaintletWithFixedXYRange) panel.getDataPaintlet();
    manager  = (SequencePlotContainerManager) panel.getContainerManager();

    m_Actual.setData(sheet);
    if (m_Actual.getIntIndex() == -1)
      throw new IllegalStateException("'Actual' column not found: " + m_Actual);
    m_Predicted.setData(sheet);
    if (m_Predicted.getIntIndex() == -1)
      throw new IllegalStateException("'Predicted' column not found: " + m_Predicted);
    error = null;
    if (!m_Error.isEmpty()) {
      m_Error.setData(sheet);
      if (m_Error.getIntIndex() == -1)
	throw new IllegalStateException("'Error' column not found: " + m_Error);
    }

    if (m_UseColumnNamesAsTitles) {
      if (m_SwapAxes) {
        panel.getPlot().getAxis(Axis.BOTTOM).setAxisName(sheet.getColumnName(m_Predicted.getIntIndex()));
        panel.getPlot().getAxis(Axis.LEFT).setAxisName(sheet.getColumnName(m_Actual.getIntIndex()));
      }
      else {
        panel.getPlot().getAxis(Axis.BOTTOM).setAxisName(sheet.getColumnName(m_Actual.getIntIndex()));
        panel.getPlot().getAxis(Axis.LEFT).setAxisName(sheet.getColumnName(m_Predicted.getIntIndex()));
      }
    }

    // additional columns
    additional = new int[0];
    if (!m_Additional.isEmpty()) {
      m_Additional.setData(sheet);
      indices = new TIntArrayList(m_Additional.getIntIndices());
      indices.remove(m_Actual.getIntIndex());
      indices.remove(m_Predicted.getIntIndex());
      indices.remove(m_Error.getIntIndex());
      additional = indices.toArray();
    }

    // create plot data
    if (!m_PlotName.isEmpty())
      id = m_PlotName;
    else if (sheet.hasName())
      id = sheet.getName();
    else
      id = m_SwapAxes ? "pred vs act" : "act vs pred";
    act     = SpreadSheetHelper.getNumericColumn(sheet, m_Actual.getIntIndex());
    pred    = SpreadSheetHelper.getNumericColumn(sheet, m_Predicted.getIntIndex());
    if (!m_Error.isEmpty())
      error = SpreadSheetHelper.getNumericColumn(sheet, m_Error.getIntIndex());
    seq     = new SequencePlotSequence();
    seq.setComparison(Comparison.X_AND_Y);
    actMin  = Double.POSITIVE_INFINITY;
    actMax  = Double.NEGATIVE_INFINITY;
    predMin = Double.POSITIVE_INFINITY;
    predMax = Double.NEGATIVE_INFINITY;
    for (i = 0; i < act.length; i++) {
      if (Double.isNaN(act[i]) || Double.isNaN(pred[i]))
	continue;
      if ((error != null) && (Double.isNaN(error[i])))
	continue;
      actMin  = Math.min(actMin,  act[i]);
      actMax  = Math.max(actMax,  act[i]);
      predMin = Math.min(predMin, pred[i]);
      predMax = Math.max(predMax, pred[i]);
      if (error == null)
	point = new SequencePlotPoint(id, m_SwapAxes ? pred[i] : act[i], m_SwapAxes ? act[i] : pred[i]);
      else
	point = new SequencePlotPoint(id, m_SwapAxes ? pred[i] : act[i], m_SwapAxes ? act[i] : pred[i], null, new Double[]{error[i]});
      // meta-data
      if (additional.length > 0) {
	meta = new HashMap<>();
	for (int index: additional) {
	  if (sheet.hasCell(i, index)) {
	    cell = sheet.getCell(i, index);
	    if (!cell.isMissing())
	      meta.put(sheet.getColumnName(index), cell.getNative());
	  }
	}
	if (meta.size() > 0)
	  point.setMetaData(meta);
      }
      seq.add(point);
    }

    // actual min/max
    if (m_SwapAxes) {
      switch (m_Limit) {
	case NONE:
	  paintlet.setMinX(predMin);
	  paintlet.setMaxX(predMax);
	  paintlet.setMinY(actMin);
	  paintlet.setMaxY(actMax);
	  break;

	case ACTUAL:
	  paintlet.setMinX(predMin);
	  paintlet.setMaxX(predMax);
	  paintlet.setMinY(actMin);
	  paintlet.setMaxY(actMax);
	  break;

	case SPECIFIED:
	  paintlet.setMinX(Double.isInfinite(m_PredictedMin) ? predMin : m_PredictedMin);
	  paintlet.setMaxX(Double.isInfinite(m_PredictedMax) ? predMax : m_PredictedMax);
	  paintlet.setMinY(Double.isInfinite(m_ActualMin) ? actMin : m_ActualMin);
	  paintlet.setMaxY(Double.isInfinite(m_ActualMax) ? actMax : m_ActualMax);
	  break;

	default:
	  throw new IllegalStateException("Unhandled limit type: " + m_Limit);
      }
    }
    else {
      switch (m_Limit) {
	case NONE:
	  paintlet.setMinX(actMin);
	  paintlet.setMaxX(actMax);
	  paintlet.setMinY(predMin);
	  paintlet.setMaxY(predMax);
	  break;

	case ACTUAL:
	  paintlet.setMinX(actMin);
	  paintlet.setMaxX(actMax);
	  paintlet.setMinY(actMin);
	  paintlet.setMaxY(actMax);
	  break;

	case SPECIFIED:
	  paintlet.setMinX(Double.isInfinite(m_ActualMin) ? actMin : m_ActualMin);
	  paintlet.setMaxX(Double.isInfinite(m_ActualMax) ? actMax : m_ActualMax);
	  paintlet.setMinY(Double.isInfinite(m_PredictedMin) ? predMin : m_PredictedMin);
	  paintlet.setMaxY(Double.isInfinite(m_PredictedMax) ? predMax : m_PredictedMax);
	  break;

	default:
	  throw new IllegalStateException("Unhandled limit type: " + m_Limit);
      }
    }

    // add sequence
    cont = manager.newContainer(seq);
    cont.setID(id);
    panel.getContainerManager().add(cont);
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    if (!m_Title.isEmpty()) {
      if (!((SequencePlotterPanel) m_Panel).getTitle().equals(m_Title))
        ((SequencePlotterPanel) m_Panel).setTitle(m_Title);
    }
    addData((SequencePlotterPanel) m_Panel, (SpreadSheet) token.getPayload());
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 4356468458332186521L;
      protected SequencePlotterPanel m_Panel;
      @Override
      protected void initGUI() {
	super.initGUI();
	m_Panel = new SequencePlotterPanel(m_SwapAxes ? "pred vs act" : "act vs pred");
	XYSequencePaintlet paintlet;
	PaintletWithFixedXYRange fixedPaintlet;
	if (m_UseCustomPaintlet) {
	  paintlet = ObjectCopyHelper.copyObject(m_CustomPaintlet);
	}
	else {
	  if (m_Error.isEmpty()) {
	    paintlet = new CrossPaintlet();
	    ((CrossPaintlet) paintlet).setDiameter(m_Diameter);
	  }
	  else {
	    paintlet = new ErrorCrossPaintlet();
	    ((ErrorCrossPaintlet) paintlet).setDiameter(m_Diameter);
	  }
	  if (paintlet instanceof AntiAliasingSupporter)
	    ((AntiAliasingSupporter) paintlet).setAntiAliasingEnabled(m_AntiAliasingEnabled);
	  if (paintlet instanceof MetaDataColorPaintlet)
	    ((MetaDataColorPaintlet) paintlet).setMetaDataColor(ObjectCopyHelper.copyObject(m_MetaDataColor));
	}
	fixedPaintlet = new PaintletWithFixedXYRange();
	fixedPaintlet.setPaintlet(paintlet);
	m_Panel.setDataPaintlet(fixedPaintlet);
	ActorUtils.updateFlowAwarePaintlet(m_Panel.getDataPaintlet(), ActualVsPredictedPlot.this);
	List<XYSequencePaintlet> subPaintlets = new ArrayList<>(Arrays.asList((XYSequencePaintlet[]) OptionUtils.shallowCopy(m_Overlays)));
	subPaintlets.add(new PolygonSelectionPaintlet());
        MultiPaintlet overlays = new MultiPaintlet();
	overlays.setSubPaintlets(subPaintlets.toArray(new XYSequencePaintlet[0]));
        m_Panel.setOverlayPaintlet(overlays);
	ActorUtils.updateFlowAwarePaintlet(m_Panel.getOverlayPaintlet(), ActualVsPredictedPlot.this);
	getDefaultAxisX().configure(m_Panel.getPlot(), Axis.BOTTOM);
	getDefaultAxisY().configure(m_Panel.getPlot(), Axis.LEFT);
	m_Panel.setColorProvider(new DefaultColorProvider());
	m_Panel.setSidePanelVisible(m_ShowSidePanel);
	m_Panel.setMouseClickAction(new ViewDataClickAction());
	m_Panel.getPlot().clearToolTipAxes();
	m_Panel.getPlot().setTipTextCustomizer(null);
	add(m_Panel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	if (!m_Title.isEmpty()) {
	  if (!m_Panel.getTitle().equals(m_Title))
	    m_Panel.setTitle(m_Title);
	}
	addData(m_Panel, (SpreadSheet) token.getPayload());
      }
      @Override
      public void clearPanel() {
	m_Panel.getContainerManager().clear();
      }
      @Override
      public void cleanUp() {
	m_Panel.getContainerManager().clear();
      }
      @Override
      public JComponent supplyComponent() {
	return m_Panel;
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  @Override
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }
}
