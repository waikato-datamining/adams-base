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
 * IncludeExternalToExternalTransfer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.option.OptionHandler;
import adams.flow.sink.ExternalSink;
import adams.flow.sink.IncludeExternalSink;
import adams.flow.source.ExternalSource;
import adams.flow.source.IncludeExternalSource;
import adams.flow.standalone.ExternalStandalone;
import adams.flow.standalone.IncludeExternalStandalone;
import adams.flow.transformer.ExternalTransformer;
import adams.flow.transformer.IncludeExternalTransformer;

/**
 * Transfers options from 'include external' to 'external' actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IncludeExternalToExternalTransfer
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
      ((source instanceof IncludeExternalStandalone) && (target instanceof ExternalStandalone))
        || ((source instanceof IncludeExternalSource) && (target instanceof ExternalSource))
        || ((source instanceof IncludeExternalTransformer) && (target instanceof ExternalTransformer))
        || ((source instanceof IncludeExternalSink) && (target instanceof ExternalSink));
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
    if ((source instanceof IncludeExternalStandalone) && (target instanceof ExternalStandalone)) {
      ((ExternalStandalone) target).setActorFile(((IncludeExternalStandalone) source).getActorFile());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }
    if ((source instanceof IncludeExternalSource) && (target instanceof ExternalSource)) {
      ((ExternalSource) target).setActorFile(((IncludeExternalSource) source).getActorFile());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }
    if ((source instanceof IncludeExternalTransformer) && (target instanceof ExternalTransformer)) {
      ((ExternalTransformer) target).setActorFile(((IncludeExternalTransformer) source).getActorFile());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }
    if ((source instanceof IncludeExternalSink) && (target instanceof ExternalSink)) {
      ((ExternalSink) target).setActorFile(((IncludeExternalSink) source).getActorFile());
      transferVariable((OptionHandler) source, (OptionHandler) target, "actorFile");
    }

    return null;
  }
}
