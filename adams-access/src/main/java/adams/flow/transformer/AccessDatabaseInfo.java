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
 * AccessDatabaseInfo.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.DataInfoActor;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Outputs information on a MS Access database.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: AccessDatabaseInfo
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Outputs the information as an array instead of one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;FILE|FILE_FORMAT|TABLES|COLUMN_ORDER|COLUMN_NAMES&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: TABLES
 * </pre>
 * 
 * <pre>-table &lt;java.lang.String&gt; (property: table)
 * &nbsp;&nbsp;&nbsp;The table to read from the database.
 * &nbsp;&nbsp;&nbsp;default: MyTable
 * </pre>
 * 
 * <pre>-sort &lt;boolean&gt; (property: sort)
 * &nbsp;&nbsp;&nbsp;If enabled, lists (eg names, values) are sorted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AccessDatabaseInfo
  extends AbstractArrayProvider
  implements DataInfoActor {

  private static final long serialVersionUID = -8104727186480376733L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** the file. */
    FILE,
    /** the file format. */
    FILE_FORMAT,
    /** the tables. */
    TABLES,
    /** the column order. */
    COLUMN_ORDER,
    /** the column names. */
    COLUMN_NAMES,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /** the table to use. */
  protected String m_Table;

  /** whether to sort lists. */
  protected boolean m_Sort;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information on a MS Access database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.TABLES);

    m_OptionManager.add(
      "table", "table",
      "MyTable");

    m_OptionManager.add(
      "sort", "sort",
      true);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	      result;
    HashSet<InfoType> types;

    result = QuickInfoHelper.toString(this, "type", m_Type);

    types = new HashSet<InfoType>(
	Arrays.asList(
	    new InfoType[]{
		InfoType.TABLES,
	    }));
    if (types.contains(m_Type) || QuickInfoHelper.hasVariable(this, "type"))
      result += QuickInfoHelper.toString(this, "sort", m_Sort, (m_Sort ? "sorted" : "unsorted"), ", ");

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
    return "Outputs the information as an array instead of one-by-one.";
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
   * Sets the table to read from.
   *
   * @param value	the table
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the table to read from.
   *
   * @return		the table
   */
  public String getTable() {
    return m_Table;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableTipText() {
    return "The table to read from the database.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case FILE:
      case FILE_FORMAT:
      case TABLES:
      case COLUMN_ORDER:
      case COLUMN_NAMES:
	return String.class;
      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		file;
    Database		db;
    Table               table;

    result = null;

    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((File) m_InputToken.getPayload());

    db = null;
    try {
      db = DatabaseBuilder.open(file.getAbsoluteFile());
      switch (m_Type) {
	case FILE:
	  m_Queue.add(db.getFile().toString());
	  break;
	case FILE_FORMAT:
	  m_Queue.add(db.getFileFormat().toString());
	  break;
	case TABLES:
	  m_Queue.addAll(db.getTableNames());
	  if (m_Sort)
	    Collections.sort(m_Queue);
	  break;
	case COLUMN_ORDER:
	  m_Queue.add(db.getColumnOrder().toString());
	  break;
        case COLUMN_NAMES:
          table = db.getTable(m_Table);
          if (table != null) {
            for (Column col : table.getColumns())
              m_Queue.add(col.getName());
          }
          else {
            result = "Table '" + m_Table + "' not found?";
          }
          break;
	default:
	  throw new IllegalStateException("Unhandled info type: " + m_Type);
      }
    }
    catch (Exception e) {
      result = handleException("Failed process database: " + file, e);
    }
    finally {
      if (db != null) {
	try {
	  db.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }
}
