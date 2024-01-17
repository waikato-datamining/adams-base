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
 * SpreadSheetInfo.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.flow.core.DataInfoActor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Outputs statistics of a SpreadSheet object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetInfo
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
 * &nbsp;&nbsp;&nbsp;If enabled, the info items get output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type &lt;NAME|COMMENTS|TIMEZONE|LOCALE|NUM_COLUMNS|NUM_ROWS|COLUMN_NAME|COLUMN_NAMES|COLUMN_TYPE|CELL_TYPES|CELL_VALUES|COLUMN_VALUES|SHEET_VALUES|FIELD_SPEC|FIELD_SPECS|FIELD_TYPE|FIELD_TYPES&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: NUM_ROWS
 * </pre>
 *
 * <pre>-column-index &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnIndex)
 * &nbsp;&nbsp;&nbsp;The column index to use for generating column-specific information. An index
 * &nbsp;&nbsp;&nbsp;is a number starting with 1; column names (case-sensitive) as well as the
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last; numeric indices can be enforced by preceding them with '#' (eg '#12'
 * &nbsp;&nbsp;&nbsp;); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-sort &lt;boolean&gt; (property: sort)
 * &nbsp;&nbsp;&nbsp;If enabled, lists (eg names, values) are sorted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetInfo
    extends AbstractArrayProvider
    implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum InfoType {
    /** the name. */
    NAME,
    /** the comments. */
    COMMENTS,
    /** the timezone (eg Pacific/Auckland). */
    TIMEZONE,
    /** the locale (eg en_NZ). */
    LOCALE,
    /** the number of columns. */
    NUM_COLUMNS,
    /** the number of rows. */
    NUM_ROWS,
    /** the name of the column (at specified index). */
    COLUMN_NAME,
    /** all column names. */
    COLUMN_NAMES,
    /** the overall column type (at specified index). */
    COLUMN_TYPE,
    /** all cell types (at specified index). */
    CELL_TYPES,
    /** all (unique) cell values (at specified index). */
    CELL_VALUES,
    /** values in specified column as they appear (unsorted, non-unique). */
    COLUMN_VALUES,
    /** all (unique) cell values. */
    SHEET_VALUES,
    /** field (at specified index). */
    FIELD_SPEC,
    /** fields (all). */
    FIELD_SPECS,
    /** field type (at specified index). */
    FIELD_TYPE,
    /** field types (all). */
    FIELD_TYPES,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /** the index of the column to get the information for. */
  protected SpreadSheetColumnIndex m_ColumnIndex;

  /** whether to sort lists. */
  protected boolean m_Sort;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs statistics of a SpreadSheet object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"type", "type",
	InfoType.NUM_ROWS);

    m_OptionManager.add(
	"column-index", "columnIndex",
	new SpreadSheetColumnIndex(Index.LAST));

    m_OptionManager.add(
	"sort", "sort",
	true);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ColumnIndex = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    HashSet<InfoType>	types;

    result = QuickInfoHelper.toString(this, "type", m_Type);

    types = new HashSet<>(
	Arrays.asList(
	    InfoType.NAME,
	    InfoType.COMMENTS,
	    InfoType.TIMEZONE,
	    InfoType.LOCALE,
	    InfoType.NUM_COLUMNS,
	    InfoType.NUM_ROWS,
	    InfoType.FIELD_SPEC,
	    InfoType.FIELD_TYPE
	));
    if (!types.contains(m_Type) || QuickInfoHelper.hasVariable(this, "type"))
      result += QuickInfoHelper.toString(this, "columnIndex", m_ColumnIndex, ", index: ");

    types = new HashSet<>(
	Arrays.asList(
	    InfoType.COLUMN_NAMES,
	    InfoType.CELL_VALUES,
	    InfoType.COLUMN_VALUES,
	    InfoType.SHEET_VALUES
	));
    if (types.contains(m_Type) || QuickInfoHelper.hasVariable(this, "type"))
      result += QuickInfoHelper.toString(this, "sort", m_Sort, (m_Sort ? "sorted" : "unsorted"), ", ");

    result += QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array", ", ");

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the info items get output as array rather than one-by-one.";
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Sets the column index to use for column specific information.
   *
   * @param value	the 1-based index
   */
  public void setColumnIndex(SpreadSheetColumnIndex value) {
    m_ColumnIndex = value;
    reset();
  }

  /**
   * Returns the column index to use for column specific information.
   *
   * @return		the 1-based index
   */
  public SpreadSheetColumnIndex getColumnIndex() {
    return m_ColumnIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnIndexTipText() {
    return "The column index to use for generating column-specific information. " + m_ColumnIndex.getExample();
  }

  /**
   * Sets whether to sort lists (eg names, values).
   *
   * @param value	true if to sort
   */
  public void setSort(boolean value) {
    m_Sort = value;
    reset();
  }

  /**
   * Returns whether lists (eg names, values) are sorted.
   *
   * @return		true if to sort
   */
  public boolean getSort() {
    return m_Sort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortTipText() {
    return "If enabled, lists (eg names, values) are sorted.";
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case NAME:
      case COMMENTS:
      case TIMEZONE:
      case LOCALE:
      case COLUMN_NAME:
      case COLUMN_NAMES:
      case COLUMN_TYPE:
      case CELL_TYPES:
      case CELL_VALUES:
      case COLUMN_VALUES:
      case SHEET_VALUES:
      case FIELD_SPEC:
      case FIELD_SPECS:
      case FIELD_TYPE:
      case FIELD_TYPES:
	return String.class;

      case NUM_COLUMNS:
      case NUM_ROWS:
	return Integer.class;

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
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
   * Turns the column into a field.
   *
   * @param sheet	the spreadsheet to use
   * @param col	the column index
   * @return		the field
   */
  protected Field toField(SpreadSheet sheet, int col) {
    Field 			result;
    Collection<ContentType>	types;

    result = null;
    types  = sheet.getContentTypes(col);
    types.remove(ContentType.MISSING);
    if (types.size() == 2) {
      if (types.contains(ContentType.DOUBLE) && types.contains(ContentType.LONG))
	result = new Field(sheet.getColumnName(col), DataType.NUMERIC);
    }
    else if (types.size() == 1) {
      if (types.contains(ContentType.DOUBLE) || types.contains(ContentType.LONG))
	result = new Field(sheet.getColumnName(col), DataType.NUMERIC);
      else if (types.contains(ContentType.BOOLEAN))
	result = new Field(sheet.getColumnName(col), DataType.BOOLEAN);
    }

    if (result == null)
      result = new Field(sheet.getColumnName(col), DataType.STRING);

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
    int				index;
    ContentType			type;
    Collection<ContentType>	types;
    int				i;

    result = null;

    m_Queue = new ArrayList();
    sheet   = (SpreadSheet) m_InputToken.getPayload();
    m_ColumnIndex.setSpreadSheet(sheet);
    index   = m_ColumnIndex.getIntIndex();

    switch (m_Type) {
      case NAME:
	m_Queue.add(sheet.getName());
	break;

      case COMMENTS:
	m_Queue.addAll(sheet.getComments());
	break;

      case TIMEZONE:
	m_Queue.add(sheet.getTimeZone().getID());
	break;

      case LOCALE:
	m_Queue.add(sheet.getLocale().toString());
	break;

      case COLUMN_NAME:
	if (index != -1)
	  m_Queue.add(sheet.getHeaderRow().getCell(index).getContent());
	break;

      case COLUMN_NAMES:
	for (Cell cell: sheet.getHeaderRow().cells())
	  m_Queue.add(cell.getContent());
	if (m_Sort)
	  Collections.sort(m_Queue);
	break;

      case NUM_COLUMNS:
	m_Queue.add(sheet.getColumnCount());
	break;

      case NUM_ROWS:
	m_Queue.add(sheet.getRowCount());
	break;

      case COLUMN_TYPE:
	if (index != -1) {
	  type = sheet.getContentType(index);
	  if (type == null)
	    type = ContentType.STRING;
	  m_Queue.add(type.toString());
	}
	break;

      case CELL_TYPES:
	if (index != -1) {
	  types = sheet.getContentTypes(index);
	  for (ContentType ct: types)
	    m_Queue.add(ct.toString());
	}
	break;

      case CELL_VALUES:
	if (index != -1)
	  m_Queue.addAll(Arrays.asList(SpreadSheetUtils.getColumn(sheet, index, true, m_Sort)));
	break;

      case COLUMN_VALUES:
	if (index != -1)
	  m_Queue.addAll(Arrays.asList(SpreadSheetUtils.getColumn(sheet, index, false, false)));
	break;

      case SHEET_VALUES:
	m_Queue.addAll(SpreadSheetUtils.uniqueValues(sheet));
	break;

      case FIELD_SPEC:
	if (index != -1)
	  m_Queue.add(toField(sheet, index).toParseableString());
	break;

      case FIELD_SPECS:
	for (i = 0; i < sheet.getColumnCount(); i++)
	  m_Queue.add(toField(sheet, i).toParseableString());
	break;

      case FIELD_TYPE:
	if (index != -1)
	  m_Queue.add(toField(sheet, index).getDataType().toDisplay());
	break;

      case FIELD_TYPES:
	for (i = 0; i < sheet.getColumnCount(); i++)
	  m_Queue.add(toField(sheet, i).getDataType().toDisplay());
	break;

      default:
	result = "Unhandled info type: " + m_Type;
    }

    m_ColumnIndex.setData(null);

    return result;
  }
}
