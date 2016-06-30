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
 * CopyCallableToCallableTransfer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.option.OptionHandler;
import adams.flow.sink.CallableSink;
import adams.flow.sink.CopyCallableSink;
import adams.flow.source.CallableSource;
import adams.flow.source.CopyCallableSource;
import adams.flow.transformer.CallableTransformer;
import adams.flow.transformer.CopyCallableTransformer;

/**
 * Transfers options from 'copy callable' to 'callable' actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopyCallableToCallableTransfer
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
    return
      ((source instanceof CopyCallableSource) && (target instanceof CallableSource))
        || ((source instanceof CopyCallableTransformer) && (target instanceof CallableTransformer))
        || ((source instanceof CopyCallableSink) && (target instanceof CallableSink));
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
    if ((source instanceof CopyCallableSource) && (target instanceof CallableSource)) {
      ((CallableSource) target).setCallableName(((CopyCallableSource) source).getCallableName());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }
    if ((source instanceof CopyCallableTransformer) && (target instanceof CallableTransformer)) {
      ((CallableTransformer) target).setCallableName(((CopyCallableTransformer) source).getCallableName());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }
    if ((source instanceof CopyCallableSink) && (target instanceof CallableSink)) {
      ((CallableSink) target).setCallableName(((CopyCallableSink) source).getCallableName());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }

    return null;
  }
}
