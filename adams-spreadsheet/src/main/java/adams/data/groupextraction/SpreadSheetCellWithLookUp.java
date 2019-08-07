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
 * SpreadSheetCellWithLookUp.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.groupextraction;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;

import java.util.HashMap;
import java.util.Map;

/**
 * Returns the cell value of the spreadsheet row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetCellWithLookUp
  extends SpreadSheetCell {

  private static final long serialVersionUID = 6130414784797102811L;

  /**
   * Describes the behaviors if a lookup key is not found.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum MissingLookUpKey {
    /** output the missing value. */
    OUTPUT_MISSING_VALUE,
    /** output the key. */
    OUTPUT_KEY,
  }

  /** the lookup values. */
  protected BaseKeyValuePair[] m_LookUps;

  /** the behavior for missing keys. */
  protected MissingLookUpKey m_MissingKey;

  /** the missing value. */
  protected String m_MissingValue;

  /** whether to suppress warnings when key is not present. */
  protected boolean m_SuppressMissingKeyWarnings;

  /** the lookup table. */
  protected transient Map<String,String> m_Table;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the cell value of the spreadsheet row.\n"
      + "If lookups are supplied, then the cell value is checked against the "
      + "keys in the lookup table for replacement.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lookup", "lookUps",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "missing-key", "missingKey",
      MissingLookUpKey.OUTPUT_KEY);

    m_OptionManager.add(
      "missing-value", "missingValue",
      "???");

    m_OptionManager.add(
      "suppress-missing-key-warnings", "suppressMissingKeyWarnings",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Table = null;
  }

  /**
   * Sets the lookup pairs.
   *
   * @param value	the lookups
   */
  public void setLookUps(BaseKeyValuePair[] value) {
    m_LookUps = value;
    reset();
  }

  /**
   * Returns the lookup pairs.
   *
   * @return		the lookups
   */
  public BaseKeyValuePair[] getLookUps() {
    return m_LookUps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lookUpsTipText() {
    return "The key-value pairs to generate the lookup table from.";
  }

  /**
   * Sets the behavior for missing keys.
   *
   * @param value	the behavior
   */
  public void setMissingKey(MissingLookUpKey value) {
    m_MissingKey = value;
    reset();
  }

  /**
   * Returns the behavior for missing keys.
   *
   * @return		the behavior
   */
  public MissingLookUpKey getMissingKey() {
    return m_MissingKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingKeyTipText() {
    return "The behavior in case a lookup key is missing (ie not found in the lookup table).";
  }

  /**
   * Sets the value to be used if behavior is {@link MissingLookUpKey#OUTPUT_MISSING_VALUE}.
   *
   * @param value	the value to use
   * @see		MissingLookUpKey#OUTPUT_MISSING_VALUE
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the value used if behavior is {@link MissingLookUpKey#OUTPUT_MISSING_VALUE}.
   *
   * @return		the value in use
   * @see		MissingLookUpKey#OUTPUT_MISSING_VALUE
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The value to forward if the missing key behavior is " + MissingLookUpKey.OUTPUT_MISSING_VALUE + ".";
  }

  /**
   * Sets whether to suppress warnings about missing keys.
   *
   * @param value	true if to suppress warnings
   */
  public void setSuppressMissingKeyWarnings(boolean value) {
    m_SuppressMissingKeyWarnings = value;
    reset();
  }

  /**
   * Returns whether to suppress warnings about missing keys.
   *
   * @return		true if warnings suppressed
   */
  public boolean getSuppressMissingKeyWarnings() {
    return m_SuppressMissingKeyWarnings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppressMissingKeyWarningsTipText() {
    return "If enabled, warnings about missing keys are suppressed.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "lookUps", m_LookUps, ", lookups: ");
    result += QuickInfoHelper.toString(this, "missingKey", m_MissingKey, ", missing: ");
    result += QuickInfoHelper.toString(this, "missingValue", m_MissingValue, ", value: ");
    value = QuickInfoHelper.toString(this, "suppressMissingKeyWarnings", m_SuppressMissingKeyWarnings, ", no missing key warnings");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Extracts the group from the object.
   *
   * @param obj		the object to process
   * @return		the extracted group, null if failed to extract or not handled
   */
  @Override
  protected String doExtractGroup(Object obj) {
    String	result;
    String	group;

    result = super.doExtractGroup(obj);

    if ((result != null) && (m_LookUps.length > 0)) {
      group = result;
      if (m_Table == null) {
	m_Table = new HashMap<>();
	for (BaseKeyValuePair lookup: m_LookUps)
	  m_Table.put(lookup.getPairKey(), lookup.getPairValue());
      }

      if (m_Table.containsKey(result)) {
	result = m_Table.get(group);
      }
      else {
	switch (m_MissingKey) {
	  case OUTPUT_KEY:
	    result = group;
	    if (!m_SuppressMissingKeyWarnings)
	      getLogger().warning("Key '" + group + "' not available from lookup table!");
	    break;
	  case OUTPUT_MISSING_VALUE:
	    result = m_MissingValue;
	    if (!m_SuppressMissingKeyWarnings)
	      getLogger().warning("Key '" + group + "' not available from lookup table, using missing value: " + m_MissingValue);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled missing key behavior: " + m_MissingKey);
	}
      }
    }

    return result;
  }
}
