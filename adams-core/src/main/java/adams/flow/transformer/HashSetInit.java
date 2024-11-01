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
 * HashSetInit.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.conversion.Conversion;
import adams.data.conversion.ObjectToObject;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;

import java.lang.reflect.Array;
import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Creates a hashset from a column in a spreadsheet or an array. The hashset itself gets stored in the internal storage under the specified name.<br>
 * In case of a spreadsheet, native values are obtained.<br>
 * The values can be converted using the specified conversion.<br>
 * <br>
 * See also:<br>
 * adams.flow.standalone.HashSetInit<br>
 * adams.flow.transformer.HashSetAdd<br>
 * adams.flow.source.HashSet<br>
 * adams.flow.condition.bool.HashSet
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: HashSetInit
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name for the hashset in the internal storage.
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
 * <pre>-conversion &lt;adams.data.conversion.Conversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The type of conversion to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.ObjectToObject
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HashSetInit
  extends AbstractTransformer
  implements StorageUpdater, ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = 4182914190162129217L;

  /** the name of the hashset in the internal storage. */
  protected StorageName m_StorageName;

  /** the index of the column which values to store in the hashset. */
  protected Index m_Column;

  /** the type of conversion. */
  protected Conversion m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Creates a hashset from a column in a spreadsheet or an array. The hashset itself "
	+ "gets stored in the internal storage under the specified name.\n"
	+ "In case of a spreadsheet, native values are obtained.\n"
	+ "The values can be converted using the specified conversion.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.standalone.HashSetInit.class, HashSetAdd.class, adams.flow.source.HashSet.class, adams.flow.condition.bool.HashSet.class};
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

    m_OptionManager.add(
	    "conversion", "conversion",
	    new ObjectToObject());
  }

  /**
   * Returns whether storage items are being updated.
   * 
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
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
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");

    return result;
  }

  /**
   * Sets the name for the hashset in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the hashset in the internal storage.
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
    return "The name for the hashset in the internal storage.";
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
   * Sets the type of conversion to perform.
   *
   * @param value	the type of conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
    m_Conversion.setOwner(this);
    reset();
  }

  /**
   * Returns the type of conversion to perform.
   *
   * @return		the type of conversion
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
    return "The type of conversion to perform.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class, Object[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class, Object[].class};
  }

  /**
   * Adds the value to the hashset.
   *
   * @param hashset	the hashset to add to
   * @param val		the value to add
   * @return		null if successfully added
   */
  protected String addValue(HashSet hashset, Object val) {
    String	result;

    m_Conversion.setInput(val);
    result = m_Conversion.convert();
    if (result != null)
      result = getFullName() + ": " + result;
    if ((result == null) && (m_Conversion.getOutput() != null))
      hashset.add(m_Conversion.getOutput());
    m_Conversion.cleanUp();

    if (isLoggingEnabled())
      getLogger().info("Adding: '" + val + "' - " + (result == null ? "success" : result));

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    int			valCol;
    HashSet		hashset;
    Object		val;
    Object		array;
    int			i;
    
    result = getOptionManager().ensureVariableForPropertyExists("storageName");

    if (result == null) {
      if (m_InputToken.getPayload() instanceof SpreadSheet) {
        sheet = (SpreadSheet) m_InputToken.getPayload();
        valCol = -1;

        if (sheet.getColumnCount() < 1)
          result = "Spreadsheet must have at least 1 column, available: " + sheet.getColumnCount();

        // value
        if (result == null) {
          m_Column.setMax(sheet.getColumnCount());
          valCol = m_Column.getIntIndex();
          if (valCol == -1)
            result = "Failed to locate column: " + m_Column.getIndex();
        }

        // create hashset
        if (result == null) {
          hashset = new HashSet();
          for (Row row : sheet.rows()) {
            if (!row.hasCell(valCol))
              continue;
            val = row.getCell(valCol).getNative();
            if (val != null) {
              result = addValue(hashset, val);
              if (result != null)
                break;
            }
          }
          if (result == null)
            getStorageHandler().getStorage().put(m_StorageName, hashset);
        }
      }
      else {
        array = m_InputToken.getPayload();
        hashset = new HashSet();
        for (i = 0; i < Array.getLength(array); i++) {
          val = Array.get(array, i);
          result = addValue(hashset, val);
          if (result != null)
            break;
        }
        if (result == null)
          getStorageHandler().getStorage().put(m_StorageName, hashset);
      }
    }
    
    if (result == null)
      m_OutputToken = m_InputToken;
    
    return result;
  }
}
