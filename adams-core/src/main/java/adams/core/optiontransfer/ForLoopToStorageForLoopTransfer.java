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
 * ForLoopToStorageForLoop.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.source.ForLoop;
import adams.flow.source.StorageForLoop;

/**
 * Transfers options between {@link ForLoop}
 * and {@link StorageForLoop} objects as vice versa.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ForLoopToStorageForLoopTransfer
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
    return ((source instanceof ForLoop) && (target instanceof StorageForLoop))
      || ((source instanceof StorageForLoop) && (target instanceof ForLoop));
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
    ForLoop 		forloop;
    StorageForLoop 	sforloop;

    if (source instanceof ForLoop) {
      forloop = (ForLoop) source;
      sforloop = (StorageForLoop) target;

      sforloop.setLoopLower(forloop.getLoopLower());
      transferVariable(forloop, sforloop, "loopLower");
      sforloop.setLoopUpper(forloop.getLoopUpper());
      transferVariable(forloop, sforloop, "loopUpper");
      sforloop.setLoopStep(forloop.getLoopStep());
      transferVariable(forloop, sforloop, "loopStep");
    }
    else {
      sforloop = (StorageForLoop) source;
      forloop = (ForLoop) target;

      forloop.setLoopLower(sforloop.getLoopLower());
      transferVariable(sforloop, forloop, "loopLower");
      forloop.setLoopUpper(sforloop.getLoopUpper());
      transferVariable(sforloop, forloop, "loopUpper");
      forloop.setLoopStep(sforloop.getLoopStep());
      transferVariable(sforloop, forloop, "loopStep");
    }

    return null;
  }
}
