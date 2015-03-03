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
 * AbstractAdamsHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.webserver;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for handlers that return a configured jetty 
 * {@link org.eclipse.jetty.server.Handler}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHandler
  extends AbstractOptionHandler
  implements Handler {

  /** for serialization. */
  private static final long serialVersionUID = 402744357786035068L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Configures the handler.
   * 
   * @return		the configured handler
   */
  @Override
  public abstract org.eclipse.jetty.server.Handler configureHandler();
}
