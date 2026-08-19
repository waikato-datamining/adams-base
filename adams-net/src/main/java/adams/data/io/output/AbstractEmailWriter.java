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
 * AbstractEmailWriter.java
 * Copyright (C) 2013-2026 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that write emails.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of email to write
 */
public abstract class AbstractEmailWriter<T>
  extends AbstractOptionHandler 
  implements EmailWriter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 4854099676125904439L;

  /**
   * Hook method for performing checks before writing the emails.
   * <br><br>
   * Default implementation checks only whether an email has been provided.
   * 
   * @return		the error message, null if everything OK
   */
  protected String check(T email) {
    if (email == null)
      return "No email provided!";
    return null;
  }
  
  /**
   * Performs the actual writing.
   * 
   * @param email	the email to write
   * @return		the error message, null if everything OK
   */
  protected abstract String doWrite(T email);
  
  /**
   * Writes the email.
   * 
   * @param email	the email to write
   * @return		the error message, null if everything OK
   */
  public String write(T email) {
    String	result;
    
    result = check(email);
    if (result == null)
      result = doWrite(email);
    
    return result;
  }

}
