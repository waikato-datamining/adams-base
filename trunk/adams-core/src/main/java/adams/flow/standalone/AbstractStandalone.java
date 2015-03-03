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
 * AbstractStandalone.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.flow.core.AbstractActor;

/**
 * Ancestor for all flow items that neither generate nor process tokens.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractStandalone
  extends AbstractActor {

  /** for serialization. */
  private static final long serialVersionUID = 2925242664771318860L;
}
