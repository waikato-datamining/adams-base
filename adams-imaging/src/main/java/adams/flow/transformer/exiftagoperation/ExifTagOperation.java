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
 * ExifTagOperation.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.exiftagoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;

/**
 * Interface for EXIF tag operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <I> the input type
 * @param <O> the output type
 */
public interface ExifTagOperation<I, O>
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Returns the type of data that we can process.
   *
   * @return		the type of data
   */
  public Class[] accepts();

  /**
   * Returns the type of data that we generate.
   *
   * @return		the type of data
   */
  public Class[] generates();

  /**
   * Processes the incoming data.
   *
   * @param input	the input to process
   * @param errors	for storing errors
   * @return		the generated output
   */
  public O process(I input, MessageCollection errors);
}
