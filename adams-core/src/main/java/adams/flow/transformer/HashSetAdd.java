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
 * HashSetAdd.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;

import java.lang.reflect.Array;
import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Adds a value to the specified hashset. <br>
 * The input for the actor can be any object, the user has to ensure that the correct type is stored.<br>
 * In case of arrays, elements get stored one by one.<br>
 * SpreadSheet cells with actual data get added as strings.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object[]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
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
 * &nbsp;&nbsp;&nbsp;default: HashSetAdd
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the hashset in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: hashset
 * </pre>
 * 
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet which values to store in the 
 * &nbsp;&nbsp;&nbsp;hashset.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HashSetAdd
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7602201480653131469L;
  
  /** the name of the hashset in the internal storage. */
  protected StorageName m_StorageName;

  /** the index of the column which values to store in the hashset. */
  protected Index m_Column;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Adds a value to the specified hashset. \n"
	+ "The input for the actor can be any object, the user has to ensure "
	+ "that the correct type is stored.\n"
	+ "In case of arrays, elements get stored one by one.\n"
	+ "SpreadSheet cells with actual data get added as strings.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("hashset"));

    m_OptionManager.add(
      "column", "column",
      new Index(Index.FIRST));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "column", m_Column, ", col: ");

    return result;
  }

  /**
   * Sets the name for the lookup table in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the lookup table in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the hashset in the internal storage.";
  }

  /**
   * Sets the index of the column to store in the hashset.
   *
   * @param value	the index
   */
  public void setColumn(Index value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the index of the column to store in the hashset.
   *
   * @return		the index
   */
  public Index getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The index of the column in the spreadsheet which values to store in the hashset.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class, Object[].class, SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Object.class};
  }

  /**
   * Adds the value to the hashset.
   *
   * @param hashset	the hashset to update
   * @param value	the value to store
   */
  protected void addValue(HashSet hashset, Object value) {
    if (isLoggingEnabled()) {
      if (hashset.contains(value))
        getLogger().info("Replacing: '" + value + "'");
      else
        getLogger().info("Adding: '" + value + "'");
    }
    hashset.add(value);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    HashSet		hashset;
    Object		value;
    int			i;
    int			n;
    SpreadSheet		sheet;
    int			col;
    Cell 		cell;
    
    result = null;
    
    if (!getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Hashset '" + m_StorageName + "' not available! Not initialized with " + HashSetInit.class.getName() + "?";
    }
    else {
      hashset = (HashSet) getStorageHandler().getStorage().get(m_StorageName);
      value  = m_InputToken.getPayload();
      if (value.getClass().isArray()) {
	for (i = 0; i < Array.getLength(value); i++)
	  addValue(hashset, Array.get(value, i));
      }
      else if (value instanceof SpreadSheet) {
	sheet = (SpreadSheet) value;
	m_Column.setMax(sheet.getColumnCount());
	col = m_Column.getIntIndex();
	for (i = 0; i < sheet.getRowCount(); i++) {
	  if (sheet.hasCell(i, col)) {
	    cell = sheet.getCell(i, col);
	    if (!cell.isMissing())
	      addValue(hashset, cell.getContent());
	  }
	}
      }
      else {
	addValue(hashset, value);
      }

      m_OutputToken = m_InputToken;
    }
    
    return result;
  }
}
