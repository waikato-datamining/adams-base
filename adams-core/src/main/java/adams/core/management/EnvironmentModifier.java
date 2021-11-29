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
 * EnvironmentModifier.java
 * Copyright (C) 2018-2021 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.core.option.SimpleOptionParser;

import java.util.List;

/**
 * Interface for classes that modify the environment used by the {@link Launcher}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface EnvironmentModifier
  extends SimpleOptionParser {

  /**
   * Updates the environment variables that the {@link Launcher} uses for
   * launching the ADAMS process.
   *
   * @param env		the current key=value pairs
   * @return		if the environment got updated
   */
  public boolean updateEnvironment(List<String> env);
}
