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
 * ByMetaDataStringValue.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 <!-- globalinfo-start -->
 * Outputs the indices of objects where the specified regular expression matches the value associated with the given meta-data key.
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
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The meta-data key to check.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the meta-data value must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ByMetaDataStringValue
  extends AbstractObjectFinder {

  private static final long serialVersionUID = 4793599743931691992L;

  /** the meta-data key to inspect. */
  protected String m_Key;

  /** the regular expression to match against the value. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs the indices of objects where the specified regular expression "
	+ "matches the value associated with the given meta-data key.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "");

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the meta-data key to check.
   *
   * @param value 	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the meta-data key to check.
   *
   * @return 		the key
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The meta-data key to check.";
  }

  /**
   * Sets the regular expression to match the meta-data value against.
   *
   * @param value 	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the meta-data value against.
   *
   * @return 		the expression
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
    return "The regular expression that the meta-data value must match.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "key", m_Key, ", key: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");

    return result;
  }

  /**
   * Performs the actual filtering of the objects in the report.
   *
   * @param report	the report to process
   * @return		the filtered image
   */
  @Override
  protected int[] doFind(Report report) {
    TIntList 		result;
    LocatedObjects 	objects;

    result  = new TIntArrayList();
    objects = LocatedObjects.fromReport(report, m_Prefix);
    for (LocatedObject obj: objects) {
      if (obj.getMetaData() != null) {
        if (obj.getMetaData().containsKey(m_Key)) {
          if (m_RegExp.isMatch("" + obj.getMetaData().get(m_Key)))
            result.add(obj.getIndex());
	}
      }
    }

    return result.toArray();
  }
}
