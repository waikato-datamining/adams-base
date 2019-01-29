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
 * AbstractExifTagOperation.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.exiftagoperation;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for operations on EXIF data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractExifTagOperation<I, O>
  extends AbstractOptionHandler
  implements ExifTagOperation<I, O> {

  private static final long serialVersionUID = -4311927144082965123L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the type of data that we can process.
   *
   * @return		the type of data
   */
  public abstract Class[] accepts();

  /**
   * Returns the type of data that we generate.
   *
   * @return		the type of data
   */
  public abstract Class[] generates();

  /**
   * Hook method for performing checks before processing the data.
   *
   * @param input	the input to process
   * @return		null if successful, otherwise error message
   */
  protected String check(I input) {
    if (input == null)
      return "No input data provided!";
    return null;
  }

  /**
   * Processes the incoming data.
   *
   * @param input	the input to process
   * @param errors	for storing errors
   * @return		the generated output
   */
  protected abstract O doProcess(I input, MessageCollection errors);

  /**
   * Processes the incoming data.
   *
   * @param input	the input to process
   * @param errors	for storing errors
   * @return		the generated output
   */
  public O process(I input, MessageCollection errors) {
    String	msg;

    msg = check(input);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    return doProcess(input, errors);
  }
}
