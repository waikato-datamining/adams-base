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
 * SimpleOptionParser.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.GlobalInfoSupporter;
import net.sourceforge.argparse4j.inf.ArgumentParser;

/**
 * Interface for classes that can consume command-line options.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface SimpleOptionParser
  extends GlobalInfoSupporter {

  /**
   * Configures and returns the commandline parser.
   *
   * @return		the parser
   */
  public ArgumentParser getParser();

  /**
   * Sets the commandline options.
   *
   * @param options	the options to use
   * @return		true if successful
   * @throws Exception	in case of an invalid option
   */
  public boolean setOptions(String[] options) throws Exception;
}
