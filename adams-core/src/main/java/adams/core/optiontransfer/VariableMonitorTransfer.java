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
 * VariableMonitorTransfer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.option.OptionHandler;
import adams.flow.core.VariableMonitor;

/**
 * Transfers options between {@link VariableMonitor} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableMonitorTransfer
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
    return (source instanceof VariableMonitor) && (target instanceof VariableMonitor);
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
    VariableMonitor 	asource;
    VariableMonitor 	atarget;

    asource = (VariableMonitor) source;
    atarget = (VariableMonitor) target;

    atarget.setVariableName(asource.getVariableName());
    if ((asource instanceof OptionHandler) && (atarget instanceof OptionHandler))
      transferVariable((OptionHandler) asource, (OptionHandler) atarget, "variableName");

    return null;
  }
}
