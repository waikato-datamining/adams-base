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
 * PasswordPrompterTransfer.java
 * Copyright (C) 2026 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.PasswordPrompter;
import adams.core.option.OptionHandler;

/**
 * Transfers options between {@link PasswordPrompter} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PasswordPrompterTransfer
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
    return (source instanceof PasswordPrompter) && (target instanceof PasswordPrompter)
      && (source instanceof OptionHandler) && (target instanceof OptionHandler);
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
    PasswordPrompter 	asource;
    PasswordPrompter 	atarget;

    asource = (PasswordPrompter) source;
    atarget = (PasswordPrompter) target;

    atarget.setPromptForPassword(asource.getPromptForPassword());
    transferVariable((OptionHandler) asource, (OptionHandler) atarget, "promptForPassword");

    return null;
  }
}
