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
 * ActorTransfer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.core.Actor;

/**
 * Transfers options between {@link Actor} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorTransfer
  extends AbstractOptionTransfer {

  /**
   * Returns whether it can handle the transfer.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		true if options can be transferred by this class
   */
  @Override
  public boolean handles(Object source, Object target) {
    return (source instanceof Actor) && (target instanceof Actor);
  }

  /**
   * Does the actual transfer of options.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransfer(Object source, Object target) {
    Actor 	asource;
    Actor 	atarget;

    asource = (Actor) source;
    atarget = (Actor) target;

    if (!asource.getName().equals(asource.getDefaultName()))
      atarget.setName(asource.getName());
    atarget.setSkip(asource.getSkip());
    atarget.setAnnotations(asource.getAnnotations());
    atarget.setSilent(asource.getSilent());
    atarget.setStopFlowOnError(asource.getStopFlowOnError());

    transferVariable(asource, atarget, "name");
    transferVariable(asource, atarget, "skip");
    transferVariable(asource, atarget, "annotations");
    transferVariable(asource, atarget, "silet");
    transferVariable(asource, atarget, "stopFlowOnError");

    return null;
  }
}
