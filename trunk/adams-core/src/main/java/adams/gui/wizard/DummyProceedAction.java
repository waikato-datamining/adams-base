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
 * DummyProceedAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

/**
 * Dummy action, does nothing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DummyProceedAction
  implements ProceedAction<AbstractWizardPage, AbstractWizardPage> {

  /** for serialization. */
  private static final long serialVersionUID = -2709628845391871659L;

  /**
   * Does nothing.
   * 
   * @param currPage	the current page that is about to be left
   * @param nextPage	the next page that will get accessed
   */
  @Override
  public void onProceed(AbstractWizardPage currPage, AbstractWizardPage nextPage) {
  }
}
