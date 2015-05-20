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
 * JsonToSpreadSheet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a JSON object into a spreadsheet, essentially flattening it.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the generated columns.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-left-pad &lt;boolean&gt; (property: leftPad)
 * &nbsp;&nbsp;&nbsp;If enabled, the index in the arrays gets left-padded with zeroes.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 5430116668955307166L;

  /** the prefix to use for the column names. */
  protected String m_Prefix;

  /** whether to left-pad the index of arrays with 0s. */
  protected boolean m_LeftPad;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a JSON object into a spreadsheet, essentially flattening it.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "left-pad", "leftPad",
	    false);
  }

  /**
   * Sets the prefix to use for the columns.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the columns.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the generated columns.";
  }

  /**
   * Sets whether to left-pad the array indices with zeroes.
   *
   * @param value	true if to pad
   */
  public void setLeftPad(boolean value) {
    m_LeftPad = value;
    reset();
  }

  /**
   * Returns whether the array indices get left-padded with zeroes.
   *
   * @return		true if to pad
   */
  public boolean getLeftPad() {
    return m_LeftPad;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftPadTipText() {
    return "If enabled, the index in the arrays gets left-padded with zeroes.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "prefix", m_Prefix);
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return JSONAware.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Flattens the JSON object.
   * 
   * @param json	the array to flatten
   * @param path	the current path
   * @param sheet	the sheet to add it to
   * @param header	the header row
   */
  protected void flatten(String path, JSONArray json, SpreadSheet sheet, Row header) {
    int		i;
    String	newPath;
    Object	obj;
    int		digits;
    
    digits = Integer.toString(json.size() - 1).length();
    for (i = 0; i < json.size(); i++) {
      obj     = json.get(i);
      newPath = "[" + (m_LeftPad ? Utils.padLeft("" + i, '0', digits) : ("" + i)) + "]";
      if (path != null)
	newPath = path + newPath;
      else
	newPath = m_Prefix + newPath;

      if (obj instanceof JSONObject) {
	flatten(newPath, (JSONObject) obj, sheet, header);
      }
      else if (obj instanceof JSONArray) {
	flatten(newPath, (JSONArray) obj, sheet, header);
      }
      else {
	if (!header.hasCell(newPath))
	  header.addCell(newPath).setContent(newPath);
	sheet.getRow(0).addCell(newPath).setNative(obj);
      }
    }
  }

  /**
   * Flattens the JSON object.
   * 
   * @param json	the object to flatten
   * @param path	the current path
   * @param sheet	the sheet to add it to
   * @param header	the header row
   */
  protected void flatten(String path, JSONObject json, SpreadSheet sheet, Row header) {
    Object	obj;
    String	newPath;
    
    for (String key: json.keySet()) {
      obj = json.get(key);
      newPath = key;
      if (path != null)
	newPath = path + "." + newPath;
      else
	newPath = m_Prefix + newPath;
      
      if (obj instanceof JSONObject) {
	flatten(newPath, (JSONObject) obj, sheet, header);
      }
      else if (obj instanceof JSONArray) {
	flatten(newPath, (JSONArray) obj, sheet, header);
      }
      else {
	if (!header.hasCell(newPath))
	  header.addCell(newPath).setContent(newPath);
	sheet.getRow(0).addCell(newPath).setNative(obj);
      }
    }
  }
  
  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet	result;
    Row		header;
    
    result = new SpreadSheet();
    result.setDataRowClass(SparseDataRow.class);
    result.addRow();
    header = result.getHeaderRow();
    
    if (m_Input instanceof JSONObject)
      flatten(null, (JSONObject) m_Input, result, header);
    else if (m_Input instanceof JSONArray)
      flatten(null, (JSONArray) m_Input, result, header);
    else
      throw new IllegalStateException("Cannot handle: " + m_Input.getClass().getName());
    
    return result;
  }
}
