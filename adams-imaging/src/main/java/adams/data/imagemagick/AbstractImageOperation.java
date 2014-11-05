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

/**
 * AbstractImageOperation.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick;

import adams.core.QuickInfoSupporter;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for image operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageOperation
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5786884290710210384L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }
  
  /**
   * Hook method for performing checks before applying the operation.
   * 
   * @param input	the input file
   * @param output	the output file
   * @return		null if successfull, otherwise error message
   */
  protected String check(PlaceholderFile input, PlaceholderFile output) {
    if (!input.exists())
      return "Input file '" + input + "' does not exist!";
    if (!output.getParentFile().exists())
      return "Output directory '" + output.getParentFile() + "' does not exist!";
    if (!output.getParentFile().isDirectory())
      return "Output '" + output.getParentFile() + "' is not a directory!";
    return null;
  }

  /**
   * Applies the actual operation to the input file and stores the result in the 
   * output file.
   * 
   * @param input	the input file
   * @param output	the output file
   * @return		null if successfull, otherwise error message
   */
  protected abstract String doApply(PlaceholderFile input, PlaceholderFile output);
  
  /**
   * Applies the operation to the input file and stores the result in the 
   * output file.
   * 
   * @param input	the input file
   * @param output	the output file
   * @return		null if successfull, otherwise error message
   */
  public String apply(PlaceholderFile input, PlaceholderFile output) {
    String	result;
    
    result = check(input, output);
    if (result == null)
      result = doApply(input, output);
    
    return result;
  }
}
