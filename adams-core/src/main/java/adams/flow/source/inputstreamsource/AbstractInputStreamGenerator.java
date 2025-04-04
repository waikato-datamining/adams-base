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
 * AbstractInputStreamGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.inputstreamsource;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import java.io.InputStream;

/**
 * Ancestor for classes that construct input streams.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInputStreamGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 3225457608826031698L;

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
   * Hook method for checks.
   *
   * @return		null if checks passed, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Generates the InputStream instance.
   *
   * @return		the stream
   * @throws Exception	if generation fails
   */
  protected abstract InputStream doGenerate() throws Exception;

  /**
   * Generates the InputStream instance.
   *
   * @return		the stream
   * @throws Exception	if generation fails
   */
  public InputStream generate() throws Exception {
    String	msg;

    msg = check();
    if (msg != null)
      throw new Exception(msg);

    return doGenerate();
  }
}
