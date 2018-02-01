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
 * ActorTemplateUser.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.template;

import adams.flow.core.Actor;
import adams.flow.core.InternalActorHandler;

/**
 * Interface for actors that make use for actor templates.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ActorTemplateUser
  extends Actor, InternalActorHandler {

  /**
   * Sets the name of the global actor to use.
   *
   * @param value 	the global name
   */
  public void setTemplate(AbstractActorTemplate value);

  /**
   * Returns the name of the global actor in use.
   *
   * @return 		the global name
   */
  public AbstractActorTemplate getTemplate();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String templateTipText();
}
