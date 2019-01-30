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
 * StorageNameTransfer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.option.OptionHandler;

/**
 * Transfers the storageName property between objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StorageNameTransfer
  extends AbstractOptionTransfer {

  public static final String STORAGE_NAME = "storageName";

  /**
   * Returns whether it can handle the transfer.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		true if options can be transferred by this class
   */
  @Override
  public boolean handles(Object source, Object target) {
    return (source instanceof OptionHandler)
      && (((OptionHandler) source).getOptionManager().findByProperty(STORAGE_NAME) != null)
      && (target instanceof OptionHandler)
      && (((OptionHandler) target).getOptionManager().findByProperty(STORAGE_NAME) != null);
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
    OptionHandler 	osource;
    OptionHandler 	otarget;

    osource = (OptionHandler) source;
    otarget = (OptionHandler) target;

    otarget.getOptionManager().findByProperty(STORAGE_NAME).setCurrentValue(
      osource.getOptionManager().findByProperty(STORAGE_NAME).getCurrentValue());
    transferVariable(osource, otarget, STORAGE_NAME);

    return null;
  }
}
