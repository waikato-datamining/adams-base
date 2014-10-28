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
 * ProceedAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import java.io.Serializable;

/**
 * Interface for action classes that get called when a user proceeds to the
 * next page.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8372 $
 * @param <C> the type of the current page
 * @param <N> the type of the next page
 */
public interface ProceedAction<C extends AbstractWizardPage, N extends AbstractWizardPage> 
  extends Serializable {

  /**
   * Performs the action associated with proceeding.
   * 
   * @param currPage	the current page that is about to be left
   * @param nextPage	the next page that will get accessed
   */
  public void performProceedAction(C currPage, N nextPage);
}
