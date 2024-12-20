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
 * ReportFieldRegExp.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.data.idextraction;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

import java.util.regex.Pattern;

/**
 * Returns the value of the specified field after passing it through the find/replace pair.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReportFieldRegExp
  extends ReportField {

  private static final long serialVersionUID = -4503545619469661243L;

  /** the string to find. */
  protected BaseRegExp m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /** whether to remove any file extension before applying the regexp. */
  protected boolean m_RemoveFileExt;

  /** the pattern to find. */
  protected transient Pattern m_FindPattern;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the value of the specified field after passing it through the find/replace pair.\n"
      + "By default, automatically removes any file extension before applying the regular expression (can be turned off).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "find", "find",
      new BaseRegExp("([\\s\\S]+)"));

    m_OptionManager.add(
      "replace", "replace",
      "$0");

    m_OptionManager.add(
      "remove-file-ext", "removeFileExt",
      true);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_FindPattern = null;
  }

  /**
   * Sets the string to find (regular expression).
   *
   * @param value	the string
   */
  public void setFind(BaseRegExp value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find (regular expression).
   *
   * @return		the string
   */
  public BaseRegExp getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The string to find (a regular expression).";
  }

  /**
   * Sets the string to replace the occurrences with.
   *
   * @param value	the string
   */
  public void setReplace(String value) {
    m_Replace = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to replace the occurences with.
   *
   * @return		the string
   */
  public String getReplace() {
    return Utils.backQuoteChars(m_Replace);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The string to replace the occurrences with.";
  }

  /**
   * Sets whether to remove any file extension before applying the regular expression.
   *
   * @param value	true if to remove
   */
  public void setRemoveFileExt(boolean value) {
    m_RemoveFileExt = value;
    reset();
  }

  /**
   * Returns whether to remove any file extension before applying the regular expression.
   *
   * @return		true if to remove
   */
  public boolean getRemoveFileExt() {
    return m_RemoveFileExt;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeFileExtTipText() {
    return "If enabled, any file extension gets removed from the string before applying the regular expression.";
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof Report)
      || (obj instanceof ReportHandler);
  }

  /**
   * Extracts the ID from a object.
   *
   * @param obj		the object to process
   * @return		the extracted ID, null if failed to extract or not handled
   */
  @Override
  protected String doExtractID(Object obj) {
    String	result;
    Report	report;
    String	str;

    result = null;

    report = null;
    if (obj instanceof ReportHandler)
      report = ((ReportHandler) obj).getReport();
    else if (obj instanceof Report)
      report = (Report) obj;
    else
      result = "Unhandled object: " + Utils.classToString(obj);

    if (result == null) {
      if (m_FindPattern == null)
        m_FindPattern = Pattern.compile(m_Find.getValue());

      if (report.hasValue(m_Field)) {
	str = "" + report.getValue(m_Field);
	if (m_RemoveFileExt)
	  str = FileUtils.replaceExtension(str, "");
	if (!m_FindPattern.matcher(str).matches())
	  getLogger().warning("String '" + str + "' does not match pattern '" + m_Find + "'!");
        result = str.replaceAll(m_Find.getValue(), m_Replace);
      }
      else {
	getLogger().warning("Failed to locate field: " + m_Field);
      }
    }

    return result;
  }
}
