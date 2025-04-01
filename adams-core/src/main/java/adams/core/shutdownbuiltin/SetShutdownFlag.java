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
 * SetShutdownFlag.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.shutdownbuiltin;

import adams.core.logging.LoggingLevel;
import adams.core.management.ProcessUtils;

/**
 * Sets the global shutdown flag.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SetShutdownFlag
  extends AbstractBuiltInShutdownHook {

  private static final long serialVersionUID = 6958473060495258418L;

  /**
   * Default constructor.
   */
  public SetShutdownFlag() {
    super();
    setLoggingLevel(LoggingLevel.INFO);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets the global shutdown flag.";
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return the runnable
   */
  @Override
  public Runnable configure() {
    return () -> {
      getLogger().info("Setting shutdown flag");
      ProcessUtils.SHUTTING_DOWN = true;
    };
  }
}
