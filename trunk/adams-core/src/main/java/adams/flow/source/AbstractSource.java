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
 * AbstractSource.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.core.AbstractActor;
import adams.flow.core.OutputProducer;

/**
 * Ancestor of all flow items that act as sources of tokens.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSource
  extends AbstractActor
  implements OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -5375239839428426386L;
}
