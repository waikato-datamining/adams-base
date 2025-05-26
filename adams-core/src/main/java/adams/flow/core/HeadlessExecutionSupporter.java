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
 * HeadlessExecutionSupporter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Interface for actors that indicate whether they support headless execution.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface HeadlessExecutionSupporter
  extends Actor {

  /**
   * Returns whether headless execution is supported.
   *
   * @return		true if supported
   */
  public boolean supportsHeadlessExecution();
}
