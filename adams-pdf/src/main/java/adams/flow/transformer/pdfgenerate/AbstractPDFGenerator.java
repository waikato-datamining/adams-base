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
 * AbstractPDFGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfgenerate;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import java.io.File;

/**
 * Ancestor for schemes to generate PDFs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPDFGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 3661670333249792042L;

  /**
   * The type of data the generator accepts.
   *
   * @return		the classes
   */
  public abstract Class[] accepts();

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
   * Hook method for checking the objects before processing them.
   * <br>
   * Default implementation does nothing.
   *
   * @param objects	the objects to check
   * @return		null if successful, otherwise error message
   */
  protected String check(Object[] objects) {
    return null;
  }

  /**
   * Processes the objects to generate the PDF.
   *
   * @param objects	the objects to process
   * @param outputFile 	the output file to generate
   * @return		null if successful, otherwise error message
   */
  protected abstract String doProcess(Object[] objects, File outputFile);

  /**
   * Processes the objects to generate the PDF.
   *
   * @param objects	the objects to process
   * @param outputFile 	the output file to generate
   * @return		null if successful, otherwise error message
   */
  public String process(Object[] objects, File outputFile) {
    String	result;

    result = check(objects);
    if (result == null)
      result = doProcess(objects, outputFile);

    return result;
  }
}
