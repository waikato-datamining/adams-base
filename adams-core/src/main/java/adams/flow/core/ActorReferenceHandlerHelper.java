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
 * ActorReferenceHandlerHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.Utils;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Helper class for {@link ActorReferenceHandler} classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActorReferenceHandlerHelper {

  /**
   * Checks whether there are any other actors preceding this actor that
   * implement the {@link ActorReferenceHandler} interface, but aren't allowed.
   *
   * @return		null if OK, otherwise error message
   */
  public static String checkActorReferenceHandlers(ActorReferenceHandler handler) {
    MessageCollection	result;
    Class[] 		prohibited;
    ActorHandler	parent;
    int			i;
    int			index;
    int			n;

    result = new MessageCollection();

    if (handler.getParent() instanceof ActorHandler) {
      parent     = (ActorHandler) handler.getParent();
      index      = handler.index();
      prohibited = handler.getProhibitedPrecedingActorReferenceHandlers();
      if (prohibited.length > 0) {
	for (i = index - 1; i >= 0; i--) {
	  for (n = 0; n < prohibited.length; n++) {
	    if (ClassLocator.isSubclass(prohibited[n], parent.get(i).getClass()))
	      result.add(Utils.classToString(handler) + " cannot be preceded by " + Utils.classToString(parent.get(i)));
	  }
	}
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }
}
