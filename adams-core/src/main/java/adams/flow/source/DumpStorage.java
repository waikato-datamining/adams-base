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
 * DumpStorage.java
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Outputs the storage names and the string representation of their associated values in the specified format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: DumpStorage
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
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to retrieve the storage item names; uses the regular 
 * &nbsp;&nbsp;&nbsp;storage if left empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the storage names.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-invert &lt;boolean&gt; (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-type &lt;SPREADSHEET|PROPERTIES|MAP&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;The output format to use.
 * &nbsp;&nbsp;&nbsp;default: SPREADSHEET
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DumpStorage
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -6626384935427295809L;

  /**
   * The output type.
   */
  public enum OutputType {
    SPREADSHEET,
    PROPERTIES,
    MAP,
  }

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /** the output format. */
  protected OutputType m_OutputType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the storage names and the string representation of their associated values in the specified format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "cache", "cache",
      "");

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert", "invert",
      false);

    m_OptionManager.add(
      "output-type", "outputType",
      OutputType.SPREADSHEET);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "regExp", m_RegExp, (m_Invert ? "! " : ""));
    result += QuickInfoHelper.toString(this, "cache", (m_Cache.isEmpty() ? "-none-" : m_Cache), ", cache: ");
    result += QuickInfoHelper.toString(this, "outputType", m_OutputType, ", output: ");

    return result;
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to retrieve the storage item names; uses the regular storage if left empty.";
  }

  /**
   * Sets the regular expression to match the storage names against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the storage names against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the storage names.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Sets the format to output the storage items in.
   *
   * @param value	the format
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns the format to output the storage items in.
   *
   * @return		the format
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "The output format to use.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_OutputType) {
      case SPREADSHEET:
	return new Class[]{SpreadSheet.class};
      case PROPERTIES:
	return new Class[]{Properties.class};
      case MAP:
	return new Class[]{Map.class};
      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    ArrayList<StorageName> 	names;
    SpreadSheet		        sheet;
    adams.core.Properties	props;
    Map<String,String>		map;
    Row 		        row;
    Storage 			storage;
    Set<StorageName>            set;

    names   = new ArrayList<>();
    storage = getStorageHandler().getStorage();
    if (m_Cache.isEmpty())
      set = getStorageHandler().getStorage().keySet();
    else
      set = getStorageHandler().getStorage().keySet(m_Cache);
    if (m_RegExp.isMatchAll()) {
      if (!m_Invert)
	names.addAll(set);
    }
    else {
      for (StorageName name: set) {
	if (m_Invert && !m_RegExp.isMatch(name.getValue()))
	  names.add(name);
	else if (!m_Invert && m_RegExp.isMatch(name.getValue()))
	  names.add(name);
      }
    }

    Collections.sort(names);

    switch (m_OutputType) {
      case SPREADSHEET:
        sheet = new DefaultSpreadSheet();
        sheet.setName("Storage");
        row   = sheet.getHeaderRow();
        row.addCell("K").setContent("Name");
        row.addCell("V").setContent("Value");
        for (StorageName name: names) {
          row = sheet.addRow();
          row.addCell("K").setContentAsString(name.getValue());
          if (m_Cache.isEmpty())
            row.addCell("V").setContentAsString("" + storage.get(name));
          else
            row.addCell("V").setContentAsString("" + storage.get(m_Cache, name));
        }
        m_OutputToken = new Token(sheet);
	break;

      case PROPERTIES:
	props = new adams.core.Properties();
        for (StorageName name: names)
	  props.setProperty(name.getValue(), "" + storage.get(name));
	m_OutputToken = new Token(props);
	break;

      case MAP:
	map = new HashMap<>();
        for (StorageName name: names)
	  map.put(name.getValue(), "" + storage.get(name));
	m_OutputToken = new Token(map);
	break;

      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
    }

    return null;
  }
}
