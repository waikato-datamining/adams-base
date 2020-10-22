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
 * ActualVsPredictedHandler.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;
import adams.flow.sink.AbstractDisplayPanel;
import adams.flow.sink.ActualVsPredictedPlot;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;

import java.awt.BorderLayout;
import java.io.File;

/**
 * For displaying an actual vs predicted plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActualVsPredictedHandler
  extends AbstractContentHandler {

  private static final long serialVersionUID = -5721119391424306170L;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the column with the error values (optional). */
  protected SpreadSheetColumnIndex m_Error;

  /** the additional columns in the spreadsheet to add to the plot containers. */
  protected SpreadSheetColumnRange m_Additional;

  /** whether to swap the axes. */
  protected boolean m_SwapAxes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays an actual vs predicted plot from the following spreadsheet types:\n"
      + Utils.flatten(getExtensions(), ", ");
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

    m_OptionManager.add(
      "additional", "additional",
      new SpreadSheetColumnRange(""));

    m_OptionManager.add(
      "swap-axes", "swapAxes",
      false);
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
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new CsvSpreadSheetHandler().getExtensions();
  }

  /**
   * Creates the actual preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    BasePanel			result;
    CsvSpreadSheetReader	reader;
    SpreadSheet 		sheet;
    ActualVsPredictedPlot	plot;
    AbstractDisplayPanel	panel;

    result = new BasePanel(new BorderLayout());

    reader = new CsvSpreadSheetReader();
    sheet  = reader.read(file);

    plot = new ActualVsPredictedPlot();
    plot.setActual(ObjectCopyHelper.copyObject(m_Actual));
    plot.setPredicted(ObjectCopyHelper.copyObject(m_Predicted));
    plot.setError(ObjectCopyHelper.copyObject(m_Error));
    plot.setAdditional(ObjectCopyHelper.copyObject(m_Additional));
    plot.setSwapAxes(m_SwapAxes);
    panel = plot.createDisplayPanel(new Token(sheet));
    if (plot.displayPanelRequiresScrollPane())
      result.add(new BaseScrollPane(panel), BorderLayout.CENTER);
    else
      result.add(panel, BorderLayout.CENTER);

    return new PreviewPanel(result, panel);
  }
}
