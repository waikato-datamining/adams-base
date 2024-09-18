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
 * LocatedObjectsToReport.java
 * Copyright (C) 2022-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

/**
 <!-- globalinfo-start -->
 * Converts the array of LocatedObject instances to a report.
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
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocatedObjectsToReport
  extends AbstractConversion
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = 2077817492673201749L;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the offset to use. */
  protected int m_Offset;

  /** whether to update the index in the meta-data. */
  protected boolean m_UpdateIndex;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the array of LocatedObject instances to a report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      LocatedObjects.DEFAULT_PREFIX);

    m_OptionManager.add(
      "offset", "offset",
      0);

    m_OptionManager.add(
      "update-index", "updateIndex",
      false);
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
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
    return "The report field prefix used in the report.";
  }

  /**
   * Sets the offset to use for the index.
   *
   * @param value 	the offset
   */
  public void setOffset(int value) {
    if (getOptionManager().isValid("offset", value)) {
      m_Offset = value;
      reset();
    }
  }

  /**
   * Returns the offset in use for the index.
   *
   * @return 		the offset
   */
  public int getOffset() {
    return m_Offset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetTipText() {
    return "The offset to use for the index.";
  }

  /**
   * Sets whether to update the index in the meta-data.
   *
   * @param value 	true if to update
   */
  public void setUpdateIndex(boolean value) {
    m_UpdateIndex = value;
    reset();
  }

  /**
   * Returns whether to update the index in the meta-data.
   *
   * @return 		true if to update
   */
  public boolean getUpdateIndex() {
    return m_UpdateIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateIndexTipText() {
    return "If enabled, the index in the meta-data will get updated.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
    result += QuickInfoHelper.toString(this, "offset", m_Offset, ", offset: ");
    result += QuickInfoHelper.toString(this, "updateIndex", m_UpdateIndex, "update index", ", ");

    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return LocatedObject[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Report.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Report		result;
    LocatedObjects	objects;
    LocatedObject[]	input;

    input   = (LocatedObject[]) m_Input;
    objects = new LocatedObjects(input);
    result  = objects.toReport(m_Prefix, m_Offset, m_UpdateIndex);

    return result;
  }
}
