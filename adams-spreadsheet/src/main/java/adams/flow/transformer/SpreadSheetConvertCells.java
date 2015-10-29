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
 * SpreadSheetConvertCells.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.QuickInfoHelper;
import adams.core.Time;
import adams.data.conversion.Conversion;
import adams.data.conversion.ObjectToObject;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.HeaderRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.cellfinder.AbstractCellFinder;
import adams.data.spreadsheet.cellfinder.CellLocation;
import adams.data.spreadsheet.cellfinder.CellRange;
import adams.flow.core.Token;

import java.util.Date;
import java.util.Iterator;

/**
 <!-- globalinfo-start -->
 * Finds cells in a spreadsheet and converts them with a conversion scheme.<br>
 * If the conversion scheme generates a adams.data.spreadsheet.SpreadSheet object itself, this will get merged with the enclosing one: any additional columns get added and the content of the first row gets added to the row the converted cell belongs to.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetConvertCells
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-finder &lt;adams.data.spreadsheet.cellfinder.AbstractCellFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The cell finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.cellfinder.CellRange
 * </pre>
 * 
 * <pre>-skip-missing &lt;boolean&gt; (property: skipMissing)
 * &nbsp;&nbsp;&nbsp;If enabled, missing cells are skipped.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-missing-replacement-value &lt;java.lang.String&gt; (property: missingReplacementValue)
 * &nbsp;&nbsp;&nbsp;The string representation of the value to use for replacing missing values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-missing-replacement-type &lt;MISSING|STRING|BOOLEAN|LONG|DOUBLE|DATE|DATETIME|DATETIMEMSEC|TIME|OBJECT&gt; (property: missingReplacementType)
 * &nbsp;&nbsp;&nbsp;The data type to use for the replacement value for missing values.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.Conversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion to apply to the located cells.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.ObjectToObject
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetConvertCells
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4633161214275622241L;

  /** for locating the cells. */
  protected AbstractCellFinder m_Finder;
  
  /** whether to skip missing cells. */
  protected boolean m_SkipMissing;
  
  /** the value to use instead of missing. */
  protected String m_MissingReplacementValue;
  
  /** the data type of the replacement value. */
  protected ContentType m_MissingReplacementType;
  
  /** the conversion to apply to the cells. */
  protected Conversion m_Conversion;
  
  /** used for parsing missing value replacement strings. */
  protected Cell m_Cell;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Finds cells in a spreadsheet and converts them with a conversion scheme.\n"
	+ "If the conversion scheme generates a " + SpreadSheet.class.getName() + " "
	+ "object itself, this will get merged with the enclosing one: any "
	+ "additional columns get added and the content of the first row gets "
	+ "added to the row the converted cell belongs to.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "finder", "finder",
	    new CellRange());

    m_OptionManager.add(
	    "skip-missing", "skipMissing",
	    true);

    m_OptionManager.add(
	    "missing-replacement-value", "missingReplacementValue",
	    "");

    m_OptionManager.add(
	    "missing-replacement-type", "missingReplacementType",
	    ContentType.STRING);

    m_OptionManager.add(
	    "conversion", "conversion",
	    new ObjectToObject());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Cell = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");
    result += QuickInfoHelper.toString(this, "skipMissing", m_SkipMissing, "skip missing", ", ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");

    return result;
  }

  /**
   * Sets the cell finder to use.
   *
   * @param value	the finder
   */
  public void setFinder(AbstractCellFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the cell finder to use.
   *
   * @return		the finder
   */
  public AbstractCellFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The cell finder to use.";
  }

  /**
   * Sets whether to skip missing cells.
   *
   * @param value	true if to skip missing cells
   */
  public void setSkipMissing(boolean value) {
    m_SkipMissing = value;
    reset();
  }

  /**
   * Returns whether missing cells are skipped.
   *
   * @return		true if missing cells are skipped
   */
  public boolean getSkipMissing() {
    return m_SkipMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipMissingTipText() {
    return "If enabled, missing cells are skipped.";
  }

  /**
   * Sets the replacement value for missing values.
   *
   * @param value	the replacement value
   */
  public void setMissingReplacementValue(String value) {
    m_MissingReplacementValue = value;
    reset();
  }

  /**
   * Returns the replacement value for missing values.
   *
   * @return		the replacement value
   */
  public String getMissingReplacementValue() {
    return m_MissingReplacementValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingReplacementValueTipText() {
    return "The string representation of the value to use for replacing missing values.";
  }

  /**
   * Sets the data type of the replacement value.
   *
   * @param value	the replacement type
   */
  public void setMissingReplacementType(ContentType value) {
    m_MissingReplacementType = value;
    reset();
  }

  /**
   * Returns the data type of the replacement value.
   *
   * @return		the replacement type
   */
  public ContentType getMissingReplacementType() {
    return m_MissingReplacementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingReplacementTypeTipText() {
    return "The data type to use for the replacement value for missing values.";
  }

  /**
   * Sets the conversion scheme to apply to the located cells.
   *
   * @param value	the conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion scheme to apply to the located cells.
   *
   * @return		the conversion
   */
  public Conversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion to apply to the located cells.";
  }
  
  /**
   * Transfers the spreadsheet content as new columns to the spreadsheet the row belongs to.
   * 
   * @param source	the content to transfer
   * @param cell	the cell where the spreadsheet data originated from
   */
  protected void transfer(SpreadSheet source, Cell cell) {
    SpreadSheet	target;
    Row		targetRow;
    HeaderRow	targetHeader;
    Row		sourceRow;
    HeaderRow	sourceHeader;
    Cell	hc;
    int		i;
    int		col;
    
    if (source.getRowCount() < 1) {
      if (isLoggingEnabled())
	getLogger().warning("No data rows generated for cell: " + cell.getContent());
      return;
    }
    
    target       = cell.getSpreadSheet();
    targetRow    = cell.getOwner();
    targetHeader = target.getHeaderRow();
    sourceRow    = source.getRow(0);
    sourceHeader = source.getHeaderRow();
    
    for (i = 0; i < sourceHeader.getCellCount(); i++) {
      hc = sourceHeader.getCell(i);
      
      // extend sheet if necessary
      if (targetHeader.indexOfContent(hc.getContent()) == -1) {
	if (isLoggingEnabled())
	  getLogger().info("Adding column: " + hc.getContent());
	target.insertColumn(target.getColumnCount(), hc.getContent());
      }
      
      // transfer content
      if ((sourceRow.getCell(i) != null) && !sourceRow.getCell(i).isMissing()) {
	col = targetHeader.indexOfContent(hc.getContent());
	targetRow.addCell(col).assign(sourceRow.getCell(i));
      }
      
      if (isStopped())
	break;
    }
  }
  
  /**
   * Applies the conversion to the cell.
   * 
   * @param location	the cell location to convert
   * @param sheet	the sheet to process
   * @return		null if successful, otherwise error message
   */
  protected String convertCell(CellLocation location, SpreadSheet sheet) {
    String	result;
    Cell	cell;
    Class	classIn;
    Class	classOut;
    Object	input;
    Object	output;

    result = null;
    input  = null;
    output = null;
    
    if (m_Cell == null)
      m_Cell = sheet.newCell();

    if (!sheet.hasCell(location.getRow(), location.getColumn())) {
      if (m_SkipMissing)
	return null;
      else
	input = m_Cell.parseContent(m_MissingReplacementValue, m_MissingReplacementType);
    }
    
    cell = sheet.getCell(location.getRow(), location.getColumn());
    if (cell.isMissing()) {
      if (m_SkipMissing)
	return null;
      else
	input = m_Cell.parseContent(m_MissingReplacementValue, m_MissingReplacementType);
    }

    classIn  = m_Conversion.accepts();
    classOut = m_Conversion.generates();

    if (input == null) {
      if (classIn == Double.class)
	input = cell.toDouble();
      else if (classIn == Integer.class)
	input = cell.toLong().intValue();
      else if (classIn == Long.class)
	input = cell.toLong();
      else if (classIn == Date.class)
	input = cell.toDate();
      else if (classIn == DateTime.class)
	input = cell.toDateTime();
      else if (classIn == DateTimeMsec.class)
	input = cell.toDateTimeMsec();
      else if (classIn == Time.class)
	input = cell.toTime();
      else if (classIn == String.class)
	input = cell.getContent();
      else
	result = "Don't know how to get cell value for conversion input type: " + classIn.getName();
    }
    
    if (result == null) {
      m_Conversion.setInput(input);
      result = m_Conversion.convert();
    }
      
    if (result == null) {
      output = m_Conversion.getOutput();
      m_Conversion.cleanUp();
      
      if (classOut == Double.class)
	cell.setContent((Double) output);
      else if (classOut == Integer.class)
	cell.setContent((Integer) output);
      else if (classOut == Long.class)
	cell.setContent((Long) output);
      else if (classOut == Date.class)
	cell.setContent((Date) output);
      else if (classOut == DateTime.class)
	cell.setContent((DateTime) output);
      else if (classOut == DateTimeMsec.class)
	cell.setContent((DateTimeMsec) output);
      else if (classOut == Time.class)
	cell.setContent((Time) output);
      else if (classOut == String.class)
	cell.setContentAsString((String) output);
      else if (classOut == SpreadSheet.class)
	transfer((SpreadSheet) output, cell);
      else
	result = "Don't know how to set cell value for conversion output type: " + classOut.getName();
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
    SpreadSheet			sheetOld;
    SpreadSheet			sheetNew;
    Iterator<CellLocation>	cells;
    
    result = null;
    
    sheetOld = (SpreadSheet) m_InputToken.getPayload();
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();
    
    cells = m_Finder.findCells(sheetNew);
    while (cells.hasNext() && !isStopped()) {
      if (isStopped())
        return null;
      result = convertCell(cells.next(), sheetNew);
      if (result != null)
	break;
    }
    
    if (result == null)
      m_OutputToken = new Token(sheetNew);
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    m_Conversion.stopExecution();
  }
}
