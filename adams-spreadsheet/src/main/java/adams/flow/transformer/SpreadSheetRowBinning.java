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
 * SpreadSheetRowBinning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.algorithm.AbstractBinningAlgorithm;
import adams.data.binning.algorithm.ManualBinning;
import adams.data.binning.postprocessing.AbstractBinPostProcessing;
import adams.data.binning.postprocessing.PassThrough;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies a binning algorithm to the values from the specified binning column to filter the rows into specific bins.<br>
 * A new column is then added containing the corresponding bin index.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetRowBinning
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
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-binning-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: binningColumn)
 * &nbsp;&nbsp;&nbsp;The column to obtain the numeric values from to use for binning.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-algorithm &lt;adams.data.binning.algorithm.AbstractBinningAlgorithm&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The binning algorithm to apply.
 * &nbsp;&nbsp;&nbsp;default: adams.data.binning.algorithm.ManualBinning
 * </pre>
 *
 * <pre>-post-processing &lt;adams.data.binning.postprocessing.AbstractBinPostProcessing&gt; (property: postProcessing)
 * &nbsp;&nbsp;&nbsp;The post-processing algorithm to apply to the bins.
 * &nbsp;&nbsp;&nbsp;default: adams.data.binning.postprocessing.PassThrough
 * </pre>
 *
 * <pre>-position &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the column; An index is a number starting with
 * &nbsp;&nbsp;&nbsp;1; column names (case-sensitive) as well as the following placeholders can
 * &nbsp;&nbsp;&nbsp;be used: first, second, third, last_2, last_1, last; numeric indices can
 * &nbsp;&nbsp;&nbsp;be enforced by preceding them with '#' (eg '#12'); column names can be surrounded
 * &nbsp;&nbsp;&nbsp;by double quotes.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-after &lt;boolean&gt; (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the column is inserted after the position instead of at the
 * &nbsp;&nbsp;&nbsp;position.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-header &lt;java.lang.String&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The name of the new column.
 * &nbsp;&nbsp;&nbsp;default: Bin
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRowBinning
  extends AbstractInPlaceSpreadSheetTransformer {

  private static final long serialVersionUID = -4140425415663734153L;

  /** the numeric column to use for determining the bins. */
  protected SpreadSheetColumnIndex m_BinningColumn;

  /** the binning algorithm to use. */
  protected AbstractBinningAlgorithm m_Algorithm;

  /** for post-processing the bins. */
  protected AbstractBinPostProcessing m_PostProcessing;

  /** the position where to insert the column. */
  protected SpreadSheetColumnIndex m_Position;

  /** whether to insert after the position instead of at. */
  protected boolean m_After;

  /** the column header. */
  protected String m_Header;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a binning algorithm to the values from the specified "
      + "binning column to filter the rows into specific bins.\n"
      + "A new column is then added containing the corresponding bin index.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "binning-column", "binningColumn",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
      "algorithm", "algorithm",
      new ManualBinning());

    m_OptionManager.add(
      "post-processing", "postProcessing",
      new PassThrough());

    m_OptionManager.add(
      "position", "position",
      new SpreadSheetColumnIndex(Index.LAST));

    m_OptionManager.add(
      "after", "after",
      false);

    m_OptionManager.add(
      "header", "header",
      "Bin");
  }

  /**
   * Sets the numeric column to use for binning.
   *
   * @param value	the column
   */
  public void setBinningColumn(SpreadSheetColumnIndex value) {
    m_BinningColumn = value;
    reset();
  }

  /**
   * Returns the numeric column to use for binning.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getBinningColumn() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binningColumnTipText() {
    return "The column to obtain the numeric values from to use for binning.";
  }

  /**
   * Sets the binning algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(AbstractBinningAlgorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the binning algorithm to use.
   *
   * @return		the algorithm
   */
  public AbstractBinningAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The binning algorithm to apply.";
  }

  /**
   * Sets the post-processing algorithm to apply to the bins.
   *
   * @param value	the post-processing
   */
  public void setPostProcessing(AbstractBinPostProcessing value) {
    m_PostProcessing = value;
    reset();
  }

  /**
   * Returns the post-processing algorithm to apply to the bins.
   *
   * @return		the post-processing
   */
  public AbstractBinPostProcessing getPostProcessing() {
    return m_PostProcessing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessingTipText() {
    return "The post-processing algorithm to apply to the bins.";
  }

  /**
   * Sets the position where to insert the column.
   *
   * @param value	the position
   */
  public void setPosition(SpreadSheetColumnIndex value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the column.
   *
   * @return		the position
   */
  public SpreadSheetColumnIndex getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return
        "The position where to insert the column; " + m_Position.getExample();
  }

  /**
   * Sets whether to insert at or after the position.
   *
   * @param value	true if to add after
   */
  public void setAfter(boolean value) {
    m_After = value;
    reset();
  }

  /**
   * Returns whether to insert at or after the position.
   *
   * @return		true if to add after
   */
  public boolean getAfter() {
    return m_After;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String afterTipText() {
    return
        "If enabled, the column is inserted after the position instead of at "
	+ "the position.";
  }

  /**
   * Sets the name of the column.
   *
   * @param value	the name
   */
  public void setHeader(String value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the name of the column.
   *
   * @return		the name
   */
  public String getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The name of the new column.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> options;

    result = QuickInfoHelper.toString(this, "binningColumn", m_BinningColumn, "binning: ");
    result += QuickInfoHelper.toString(this, "algorithm", m_Algorithm, ", algorithm: ");
    result += QuickInfoHelper.toString(this, "postProcessing", m_PostProcessing, ", post: ");
    result += QuickInfoHelper.toString(this, "header", "'" + m_Header + "'", ", header: ");

    if (QuickInfoHelper.hasVariable(this, "after"))
      result += ", at/after: ";
    else if (m_After)
      result += ", after: ";
    else
      result += ", at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    SpreadSheet 			sheetOld;
    SpreadSheet				sheetNew;
    int					binCol;
    int					pos;
    double[]				values;
    List<Binnable<Integer>>	rows;
    int					i;
    List<Bin<Integer>>			bins;
    Row					row;

    result   = null;
    sheetOld = (SpreadSheet) m_InputToken.getPayload();
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();

    // determine columns
    binCol = 0;
    pos    = 0;
    if (sheetOld.getColumnCount() > 0) {
      m_BinningColumn.setData(sheetOld);
      binCol = m_BinningColumn.getIntIndex();
      m_Position.setSpreadSheet(sheetOld);
      pos = m_Position.getIntIndex();
      if (m_After)
        pos++;
    }

    // generate binnable data
    values = SpreadSheetUtils.getNumericColumn(sheetOld, binCol);
    rows   = null;
    try {
      rows = Binnable.wrap(values);
    }
    catch (Exception e) {
      result = handleException("Failed to generate binnable data!", e);
    }

    if (result == null) {
      // perform binning
      bins = m_Algorithm.generateBins(rows);
      bins = m_PostProcessing.postProcessBins(bins);

      // insert column
      sheetNew.insertColumn(pos, m_Header);

      // insert bins
      for (Bin<Integer> bin: bins) {
        for (Binnable<Integer> object: bin.get()) {
          i   = object.getPayload();
          row = sheetNew.getRow(i);
          row.addCell(pos).setContent(bin.getIndex());
	}
      }

      // generate output
      m_OutputToken = new Token(sheetNew);
    }

    return result;
  }
}
