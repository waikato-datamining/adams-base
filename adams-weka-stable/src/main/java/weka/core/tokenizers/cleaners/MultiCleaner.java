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
 * MultiCleaner.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers.cleaners;

import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Combines multiple cleaners, applies them sequentially.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiCleaner
  extends AbstractTokenCleaner {

  private static final long serialVersionUID = -1815343837519097597L;

  public static final String CLEANER = "cleaner";

  /** the cleaners to use. */
  protected TokenCleaner[] m_Cleaners = getDefaultCleaners();

  /**
   * Returns a string describing the cleaner.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "Combines multiple cleaners, applies them sequentially.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, cleanersTipText(), "none", CLEANER);
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
    setCleaners((TokenCleaner[]) WekaOptionUtils.parse(options, CLEANER, getDefaultCleaners(), TokenCleaner.class));
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
    WekaOptionUtils.add(result, CLEANER, getCleaners());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default token cleaners.
   *
   * @return		the default
   */
  protected TokenCleaner[] getDefaultCleaners() {
    return new TokenCleaner[0];
  }

  /**
   * Sets the cleaners to use.
   *
   * @param value	the cleaners
   */
  public void setCleaners(TokenCleaner[] value) {
    m_Cleaners = value;
    reset();
  }

  /**
   * Returns the cleaners to use.
   *
   * @return		the cleaners
   */
  public TokenCleaner[] getCleaners() {
    return m_Cleaners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanersTipText() {
    return "The cleaners to apply sequentially.";
  }

  /**
   * Determines whether a token is clean or not.
   *
   * @param token	the token to check
   * @return		the clean token or null to ignore
   */
  @Override
  public String clean(String token) {
    String	result;

    result = token;

    for (TokenCleaner cleaner: m_Cleaners) {
      result = cleaner.clean(result);
      if (result == null)
	break;
    }

    return result;
  }
}
