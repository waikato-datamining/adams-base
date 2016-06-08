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
 * LocalScopeHandlerTransfer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.control.LocalScopeHandler;

/**
 * Transfers options between {@link LocalScopeHandler} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocalScopeHandlerTransfer
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
    return (source instanceof LocalScopeHandler) && (target instanceof LocalScopeHandler);
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
    LocalScopeHandler 	asource;
    LocalScopeHandler 	atarget;

    asource = (LocalScopeHandler) source;
    atarget = (LocalScopeHandler) target;

    atarget.setScopeHandlingVariables(asource.getScopeHandlingVariables());
    atarget.setVariablesFilter(asource.getVariablesFilter());
    atarget.setPropagateVariables(asource.getPropagateVariables());
    atarget.setVariablesRegExp(asource.getVariablesRegExp());
    atarget.setScopeHandlingStorage(asource.getScopeHandlingStorage());
    atarget.setStorageFilter(asource.getStorageFilter());
    atarget.setPropagateStorage(asource.getPropagateStorage());
    atarget.setStorageRegExp(asource.getStorageRegExp());

    transferVariable(asource, atarget, "scopeHandlingVariables");
    transferVariable(asource, atarget, "variablesFilter");
    transferVariable(asource, atarget, "propagateVariables");
    transferVariable(asource, atarget, "variablesRegExp");
    transferVariable(asource, atarget, "scopeHandlingStorage");
    transferVariable(asource, atarget, "storageFilter");
    transferVariable(asource, atarget, "propagateStorage");
    transferVariable(asource, atarget, "storageRegExp");

    return null;
  }
}
