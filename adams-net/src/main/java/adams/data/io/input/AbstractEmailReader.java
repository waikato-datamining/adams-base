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
 * AbstractEmailReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.net.Email;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that read emails.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEmailReader
  extends AbstractOptionHandler 
  implements EmailReader {

  /** for serialization. */
  private static final long serialVersionUID = -9737701248226890L;

  /**
   * Hook method for performing checks before reading the emails. Throws
   * {@link IllegalStateException} in case something is wrong.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Performs the actual reading.
   * 
   * @return		the email that was read, null in case of error
   */
  protected abstract Email doRead();
  
  /**
   * Reads the email.
   * 
   * @return		the email, null in case of an error
   */
  public Email read() {
    check();
    return doRead();
  }
}
