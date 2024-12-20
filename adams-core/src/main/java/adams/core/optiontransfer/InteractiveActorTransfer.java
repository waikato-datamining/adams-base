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
 * InteractiveActorTransfer.java
 * Copyright (C) 2016-2023 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.core.InteractiveActor;

/**
 * Transfers options between {@link InteractiveActor} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InteractiveActorTransfer
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
    return (source instanceof InteractiveActor) && (target instanceof InteractiveActor);
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
    InteractiveActor 	asource;
    InteractiveActor 	atarget;

    asource = (InteractiveActor) source;
    atarget = (InteractiveActor) target;

    atarget.setStopFlowIfCanceled(asource.getStopFlowIfCanceled());
    atarget.setCustomStopMessage(asource.getCustomStopMessage());
    atarget.setStopMode(asource.getStopMode());

    transferVariable(asource, atarget, "stopFlowIfCanceled");
    transferVariable(asource, atarget, "customStopMessage");
    transferVariable(asource, atarget, "stopMode");

    return null;
  }
}
