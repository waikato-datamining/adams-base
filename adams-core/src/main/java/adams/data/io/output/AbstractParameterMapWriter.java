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
 * AbstractParameterMapWriter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Ancestor for classes that write parameter maps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractParameterMapWriter
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = -2116714163958424681L;

  /**
   * Hook method for performing checks.
   *
   * @param output	the output file to read
   * @return		null if checks passed, otherwise error message
   */
  protected String check(PlaceholderFile output) {
    return null;
  }

  /**
   * Reads the parameters from the file.
   *
   * @param params	the parameters to write
   * @param output	the output file to write to
   * @throws Exception	if reading fails
   */
  protected abstract void doWrite(Map<String,Object> params, PlaceholderFile output) throws Exception;

  /**
   * Reads the parameters from the file.
   *
   * @param params	the parameters to write
   * @param output	the output file to write to
   * @throws Exception	if writing fails
   */
  public void write(Map<String,Object> params, PlaceholderFile output) throws Exception {
    String	msg;

    msg = check(output);
    if (msg != null)
      new Exception(msg);

    doWrite(params, output);
  }
}
