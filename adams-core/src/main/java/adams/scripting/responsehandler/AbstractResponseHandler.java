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
 * AbstractResponseHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.responsehandler;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for response handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractResponseHandler
  extends AbstractOptionHandler
  implements ResponseHandler {

  private static final long serialVersionUID = -5933202929871166784L;
}
