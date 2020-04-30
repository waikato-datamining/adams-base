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
 * AbstractEventLogger.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.interactionlogging;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for interaction logging filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInteractionLoggingFilter
  extends AbstractOptionHandler
  implements InteractionLoggingFilter {

  private static final long serialVersionUID = -7569705964600776466L;

  /** whether the logger is enabled. */
  protected boolean m_Enabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "enabled", "enabled",
      true);
  }

  /**
   * Sets whether to enable the filter.
   *
   * @param value 	true if enable
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether to enable the filter.
   *
   * @return 		true if to enable
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "Determines whether filtering is enabled.";
  }

  /**
   * Check method before logging the event.
   *
   * @param e		the event to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(InteractionEvent e) {
    if (e == null)
      return "No event provided!";
    return null;
  }

  /**
   * Filters the interaction logging.
   *
   * @param e		the interaction event
   */
  protected abstract void doFilterInteractionLog(InteractionEvent e);

  /**
   * Filters the interaction logging.
   *
   * @param e		the interaction event
   */
  public void filterInteractionLog(InteractionEvent e) {
    String	msg;

    if (!getEnabled())
      return;

    msg = check(e);
    if (msg != null)
      throw new IllegalStateException(msg);
    doFilterInteractionLog(e);
  }
}
