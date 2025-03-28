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
 * SpreadSheetHistogramRanges.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.ArrayHistogram.BinCalculation;

/**
 <!-- globalinfo-start -->
 * Outputs the ranges generated by adams.data.statistics.ArrayHistogram using the incoming adams.data.spreadsheet.SpreadSheet object.<br>
 * If cells aren't numeric or missing, a default value of zero is used.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetHistogramRanges
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the ranges are output as array rather than one by one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;ROW_BY_INDEX|COLUMN_BY_INDEX|COLUMN_BY_REGEXP&gt; (property: dataType)
 * &nbsp;&nbsp;&nbsp;Whether to retrieve rows or columns from the Instances object.
 * &nbsp;&nbsp;&nbsp;default: COLUMN_BY_INDEX
 * </pre>
 * 
 * <pre>-location &lt;adams.core.base.BaseString&gt; [-location ...] (property: locations)
 * &nbsp;&nbsp;&nbsp;The locations of the data, depending on the chosen data type that can be 
 * &nbsp;&nbsp;&nbsp;either indices, column names or regular expressions on the column names.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-bin-calc &lt;MANUAL|FREQUENCY|DENSITY|STURGES|SCOTT|SQRT&gt; (property: binCalculation)
 * &nbsp;&nbsp;&nbsp;Defines how the number of bins are calculated.
 * &nbsp;&nbsp;&nbsp;default: MANUAL
 * </pre>
 * 
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use in case of manual bin calculation.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-bin-width &lt;double&gt; (property: binWidth)
 * &nbsp;&nbsp;&nbsp;The bin width to use for some of the calculations.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-5
 * </pre>
 * 
 * <pre>-normalize &lt;boolean&gt; (property: normalize)
 * &nbsp;&nbsp;&nbsp;If set to true the data gets normalized first before the histogram is calculated.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-use-fixed-min-max &lt;boolean&gt; (property: useFixedMinMax)
 * &nbsp;&nbsp;&nbsp;If enabled, then the user-specified min&#47;max values are used for the bin 
 * &nbsp;&nbsp;&nbsp;calculation rather than the min&#47;max from the data (allows comparison of 
 * &nbsp;&nbsp;&nbsp;histograms when generating histograms over a range of arrays).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-manual-min &lt;double&gt; (property: manualMin)
 * &nbsp;&nbsp;&nbsp;The minimum to use when using manual binning with user-supplied min&#47;max 
 * &nbsp;&nbsp;&nbsp;enabled.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-manual-max &lt;double&gt; (property: manualMax)
 * &nbsp;&nbsp;&nbsp;The maximum to use when using manual binning with user-supplied max&#47;max 
 * &nbsp;&nbsp;&nbsp;enabled.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals to show in the bin descriptions.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetHistogramRanges
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -540187402790189753L;

  /** the type of data to get from the Instances object (rows or columns). */
  protected SpreadSheetStatisticDataType m_DataType;

  /** the array of indices/regular expressions. */
  protected BaseString[] m_Locations;

  /** how to calculate the number of bins. */
  protected BinCalculation m_BinCalculation;

  /** the number of bins in case of manual bin calculation. */
  protected int m_NumBins;

  /** the bin width - used for some calculations. */
  protected double m_BinWidth;

  /** whether to normalize the data. */
  protected boolean m_Normalize;

  /** whether to use fixed min/max for manual bin calculation. */
  protected boolean m_UseFixedMinMax;

  /** the manual minimum. */
  protected double m_ManualMin;

  /** the manual maximum. */
  protected double m_ManualMax;

  /** the number of decimals to show. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs the ranges generated by " + ArrayHistogram.class.getName() + " using the incoming " + SpreadSheet.class.getName() + " object.\n"
      + "If cells aren't numeric or missing, a default value of zero is used.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "dataType",
	    SpreadSheetStatisticDataType.COLUMN_BY_INDEX);

    m_OptionManager.add(
	    "location", "locations",
	    new BaseString[0]);

    m_OptionManager.add(
	    "bin-calc", "binCalculation",
	    BinCalculation.MANUAL);

    m_OptionManager.add(
	    "num-bins", "numBins",
	    50, 1, null);

    m_OptionManager.add(
	    "bin-width", "binWidth",
	    1.0, 0.00001, null);

    m_OptionManager.add(
	    "normalize", "normalize",
	    false);

    m_OptionManager.add(
	    "use-fixed-min-max", "useFixedMinMax",
	    false);

    m_OptionManager.add(
	    "manual-min", "manualMin",
	    0.0);

    m_OptionManager.add(
	    "manual-max", "manualMax",
	    1.0);

    m_OptionManager.add(
	    "num-decimals", "numDecimals",
	    3, 0, null);
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the ranges are output as array rather than one by one.";
  }

  /**
   * Sets what type of data to retrieve from the Instances object.
   *
   * @param value	the type of conversion
   */
  public void setDataType(SpreadSheetStatisticDataType value) {
    m_DataType = value;
    reset();
  }

  /**
   * Returns what type of data to retrieve from the Instances object.
   *
   * @return		the type of conversion
   */
  public SpreadSheetStatisticDataType getDataType() {
    return m_DataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataTypeTipText() {
    return "Whether to retrieve rows or columns from the Instances object.";
  }

  /**
   * Sets the locations of the data (indices/regular expressions on attribute name).
   *
   * @param value	the locations of the data
   */
  public void setLocations(BaseString[] value) {
    m_Locations = value;
    reset();
  }

  /**
   * Returns the locations of the data (indices/regular expressions on attribute name).
   *
   * @return		the locations of the data
   */
  public BaseString[] getLocations() {
    return m_Locations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationsTipText() {
    return
        "The locations of the data, depending on the chosen data type that "
      + "can be either indices, column names or regular expressions on the column names.";
  }

  /**
   * Sets how the number of bins is calculated.
   *
   * @param value 	the bin calculation
   */
  public void setBinCalculation(BinCalculation value) {
    m_BinCalculation = value;
    reset();
  }

  /**
   * Returns how the number of bins is calculated.
   *
   * @return 		the bin calculation
   */
  public BinCalculation getBinCalculation() {
    return m_BinCalculation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binCalculationTipText() {
    return "Defines how the number of bins are calculated.";
  }

  /**
   * Sets the number of bins to use in manual calculation.
   *
   * @param value 	the number of bins
   */
  public void setNumBins(int value) {
    m_NumBins = value;
    reset();
  }

  /**
   * Returns the number of bins to use in manual calculation.
   *
   * @return 		the number of bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use in case of manual bin calculation.";
  }

  /**
   * Sets the bin width to use (for some calculations).
   *
   * @param value 	the bin width
   */
  public void setBinWidth(double value) {
    m_BinWidth = value;
    reset();
  }

  /**
   * Returns the bin width in use (for some calculations).
   *
   * @return 		the bin width
   */
  public double getBinWidth() {
    return m_BinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binWidthTipText() {
    return "The bin width to use for some of the calculations.";
  }

  /**
   * Sets whether to normalize the data before generating the histogram.
   *
   * @param value 	if true the data gets normalized first
   */
  public void setNormalize(boolean value) {
    m_Normalize = value;
    reset();
  }

  /**
   * Returns whether to normalize the data before generating the histogram.
   *
   * @return 		true if the data gets normalized first
   */
  public boolean getNormalize() {
    return m_Normalize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizeTipText() {
    return "If set to true the data gets normalized first before the histogram is calculated.";
  }

  /**
   * Sets whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @param value 	true if to use user-supplied min/max
   */
  public void setUseFixedMinMax(boolean value) {
    m_UseFixedMinMax = value;
    reset();
  }

  /**
   * Returns whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @return 		true if to use user-supplied min/max
   */
  public boolean getUseFixedMinMax() {
    return m_UseFixedMinMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFixedMinMaxTipText() {
    return
	"If enabled, then the user-specified min/max values are used for the "
	+ "bin calculation rather than the min/max from the data (allows "
	+ "comparison of histograms when generating histograms over a range "
	+ "of arrays).";
  }

  /**
   * Sets the minimum to use when using manual binning with user-supplied
   * min/max enabled.
   *
   * @param value 	the minimum
   */
  public void setManualMin(double value) {
    m_ManualMin = value;
    reset();
  }

  /**
   * Returns the minimum to use when using manual binning with user-supplied
   * min/max enabled.
   *
   * @return 		the minimum
   */
  public double getManualMin() {
    return m_ManualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMinTipText() {
    return "The minimum to use when using manual binning with user-supplied min/max enabled.";
  }

  /**
   * Sets the maximum to use when using manual binning with user-supplied
   * max/max enabled.
   *
   * @param value 	the maximum
   */
  public void setManualMax(double value) {
    m_ManualMax = value;
    reset();
  }

  /**
   * Returns the maximum to use when using manual binning with user-supplied
   * max/max enabled.
   *
   * @return 		the maximum
   */
  public double getManualMax() {
    return m_ManualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMaxTipText() {
    return "The maximum to use when using manual binning with user-supplied max/max enabled.";
  }

  /**
   * Sets the number of decimals to show in the bin description.
   *
   * @param value 	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_NumDecimals = value;
    reset();
  }

  /**
   * Returns the number of decimals to show in the bin description.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals to show in the bin descriptions.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the row as Double array. Non-numeric/missing values are defaulted to 0.
   *
   * @param sheet	the sheet to work on
   * @param index	the 0-based row index
   * @return		the numeric values
   */
  protected Double[] getRow(SpreadSheet sheet, int index) {
    Double[]	result;
    Row		row;
    Cell	cell;
    int		i;

    row    = sheet.getRow(index);
    result = new Double[sheet.getColumnCount()];

    for (i = 0; i < result.length; i++) {
      cell = row.getCell(i);
      if ((cell == null) || cell.isMissing() || !cell.isNumeric())
	result[i] = 0.0;
      else
	result[i] = Double.parseDouble(cell.getContent());
    }

    return result;
  }

  /**
   * Returns the column as Double array. Non-numeric/missing values are defaulted to 0.
   *
   * @param sheet	the sheet to work on
   * @param index	the 0-based column index
   * @return		the numeric values
   */
  protected Double[] getColumn(SpreadSheet sheet, int index) {
    Double[]	result;
    Row		row;
    Cell	cell;
    int		i;

    result = new Double[sheet.getRowCount()];

    for (i = 0; i < result.length; i++) {
      row  = sheet.getRow(i);
      cell = row.getCell(index);
      if ((cell == null) || cell.isMissing() || !cell.isNumeric())
	result[i] = 0.0;
      else
	result[i] = Double.parseDouble(cell.getContent());
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
    String			result;
    SpreadSheet			sheet;
    SpreadSheet			data;
    int				i;
    int				n;
    Index			index;
    ArrayHistogram 		stat;

    result = null;
    m_Queue.clear();

    try {
      sheet = null;
      data  = (SpreadSheet) m_InputToken.getPayload();
      stat  = new ArrayHistogram();
      stat.setBinCalculation(m_BinCalculation);
      stat.setNumBins(m_NumBins);
      stat.setBinWidth(m_BinWidth);
      stat.setNormalize(m_Normalize);
      stat.setUseFixedMinMax(m_UseFixedMinMax);
      stat.setManualMin(m_ManualMin);
      stat.setManualMax(m_ManualMax);
      stat.setDisplayRanges(true);
      stat.setNumDecimals(m_NumDecimals);

      for (i = 0; i < m_Locations.length; i++) {
	switch (m_DataType) {
	  case ROW_BY_INDEX:
	    index = new Index(m_Locations[i].stringValue());
	    index.setMax(data.getRowCount());
	    stat.add(getRow(data, index.getIntIndex()));
	    break;

	  case COLUMN_BY_INDEX:
	    index = new SpreadSheetColumnIndex(m_Locations[i].stringValue());
	    ((SpreadSheetColumnIndex) index).setData(data);
	    stat.add(getColumn(data, index.getIntIndex()));
	    break;

	  case COLUMN_BY_REGEXP:
	    for (n = 0; n < data.getColumnCount(); n++) {
	      if (data.getHeaderRow().getCell(n).getContent().matches(m_Locations[i].stringValue())) {
		stat.add(getColumn(data, n));
		break;
	      }
	    }
	    break;

	  default:
	    throw new IllegalStateException("Unhandled data type: " + m_DataType);
	}
      }

      sheet = stat.calculate().toSpreadSheet();
    }
    catch (Exception e) {
      result = handleException("Error generating the ranges: ", e);
      sheet = null;
    }

    if (sheet != null) {
      for (i = 0; i < sheet.getColumnCount(); i++)
	m_Queue.add(sheet.getColumnName(i));
    }

    return result;
  }
}
