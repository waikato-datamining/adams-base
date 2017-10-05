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
 * StreamingProcessOwner.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.streamingprocess;

/**
 * Interface for classes that can utilize the {@link StreamingProcessOutput}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface StreamingProcessOwner {

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public OutputType getOutputType();

  /**
   * Adds the line from the output to the internal list of lines to output.
   *
   * @param line	the line to add
   * @param stdout	whether stdout or stderr
   */
  public void add(String line, boolean stdout);
}
