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
 * OutputProducer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Interface for flow actors that generate output.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface OutputProducer 
  extends Actor {

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates();

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output();

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <p/>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput();
}
