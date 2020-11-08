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
 * IDFilter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.interactionlogging;

import adams.core.base.BaseRegExp;

/**
 * Logs all events.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IDFilter
  extends AbstractInteractionLoggingFilter {

  private static final long serialVersionUID = -7834618782093662555L;

  /** the regular expression to use for matching the IDs. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Logs events with matching ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert", "invert",
      false);
  }

  /**
   * Sets the regular expression to apply to the ID.
   *
   * @param value 	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the ID.
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
  public String regexpTipText() {
    return "The regular expression to apply to the ID of the events.";
  }

  /**
   * Sets whether to invert the matching.
   *
   * @param value 	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching.
   *
   * @return 		true if to invert
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
    return "If enabled, the matching sense is inverted.";
  }

  /**
   * Logs the interaction.
   *
   * @param e		the interaction event
   */
  @Override
  protected void doFilterInteractionLog(InteractionEvent e) {
    boolean	match;

    match = m_RegExp.isMatch(e.getID());
    if (m_Invert)
      match = !match;
    if (match)
      e.getLogManager().addInteractionLog(e);
  }
}
