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
 * AbstractParameterMapReader.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Ancestor for classes that read parameter maps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractParameterMapReader
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = -2116714163958424681L;

  /**
   * Hook method for performing checks.
   *
   * @param input	the input file to read
   * @return		null if checks passed, otherwise error message
   */
  protected String check(PlaceholderFile input) {
    if (!input.exists())
      return "Parameter file does not exist: " + input;
    return null;
  }

  /**
   * Reads the parameters from the file.
   *
   * @param input	the input file to read
   * @return		the parameters that were read
   * @throws Exception	if reading fails
   */
  protected abstract Map<String,Object> doRead(PlaceholderFile input) throws Exception;

  /**
   * Reads the parameters from the file.
   *
   * @param input	the input file to read
   * @return		the parameters that were read
   * @throws Exception	if reading fails
   */
  public Map<String,Object> read(PlaceholderFile input) throws Exception {
    String	msg;

    msg = check(input);
    if (msg != null)
      throw new Exception(msg);

    return doRead(input);
  }
}
