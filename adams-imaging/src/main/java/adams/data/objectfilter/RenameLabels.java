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
 * RenameLabels.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * For renaming labels in the meta-data, stored under a specific key.<br>
 * The rules for renaming are specified: old=new.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-rule &lt;adams.core.base.BaseKeyValuePair&gt; [-rule ...] (property: rules)
 * &nbsp;&nbsp;&nbsp;The renaming rules for the labels (old=new pairs).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenameLabels
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -2181381799680316619L;

  /** the key in the meta-data containing the labels. */
  protected String m_Key;

  /** the renaming rules (old=new). */
  protected BaseKeyValuePair[] m_Rules;

  /** the lookup table. */
  protected transient Map<String,String> m_Lookup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For renaming labels in the meta-data, stored under a specific key.\n"
      + "The rules for renaming are specified: old=new.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "type");

    m_OptionManager.add(
      "rule", "rules",
      new BaseKeyValuePair[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Lookup = null;
  }

  /**
   * Sets the key in the meta-data containing the label.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key in the meta-data containing the label.
   *
   * @return		the key
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
    return "The key in the meta-data containing the label.";
  }

  /**
   * Sets the rules for renaming the labels.
   *
   * @param value	the rules
   */
  public void setRules(BaseKeyValuePair[] value) {
    m_Rules = value;
    reset();
  }

  /**
   * Returns the rules for renaming the labels.
   *
   * @return		the rules
   */
  public BaseKeyValuePair[] getRules() {
    return m_Rules;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rulesTipText() {
    return "The renaming rules for the labels (old=new pairs).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result = QuickInfoHelper.toString(this, "key", m_Key, "key: ");
    result += QuickInfoHelper.toString(this, "rules", m_Rules, ", rules: ");

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the located objects
   * @return		the updated list of objects
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    LocatedObject	newObj;
    String		old;

    if (m_Lookup == null) {
      m_Lookup = new HashMap<>();
      for (BaseKeyValuePair rule: m_Rules)
        m_Lookup.put(rule.getPairKey(), rule.getPairValue());
    }

    result = new LocatedObjects();
    for (LocatedObject obj: objects) {
      newObj = obj.getClone();
      if (newObj.getMetaData().containsKey(m_Key)) {
        old = "" + newObj.getMetaData().get(m_Key);
        if (m_Lookup.containsKey(old))
          newObj.getMetaData().put(m_Key, m_Lookup.get(old));
      }
      result.add(newObj);
    }

    return result;
  }
}
