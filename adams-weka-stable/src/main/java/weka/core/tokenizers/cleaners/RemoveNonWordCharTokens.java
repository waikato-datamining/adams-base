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
 * RemoveNonWordCharTokens.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Removes tokens that contain non-word characters.
 * Matching sense can be inverted, i.e., only tokens with non-word characters get returned.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #PATTERN
 */
public class RemoveNonWordCharTokens
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = -1815343837519097597L;

  /** the pattern to use. */
  public final static String PATTERN = ".*\\W.*";

  public static final String INVERT = "invert";

  /** whether to invert the matching sense. */
  protected boolean m_Invert = false;

  /** the pattern in use. */
  protected transient Pattern m_Pattern;

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Removes tokens that contain non-word characters: " + PATTERN + "\n"
      + "Matching sense can be inverted, i.e., only tokens with non-word characters get returned.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, invertTipText(), "no", INVERT);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setInvert(Utils.getFlag(INVERT, options));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, INVERT, getInvert());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets whether to invert the matching sense, ie keep only the emoticons
   * rather than removing them.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense, ie keep only the emoticons
   * rather than removing them.
   *
   * @return		true if to invert
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
    return "If enabled, the emoticons are the only tokens not removed.";
  }

  /**
   * Resets the cleaner.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Pattern = null;
  }

  /**
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    if (m_Pattern == null)
      m_Pattern = Pattern.compile(PATTERN);

    if (m_Invert && !m_Pattern.matcher(token).matches())
      return null;
    else if (!m_Invert && m_Pattern.matcher(token).matches())
      return null;
    else
      return token;
  }
}
