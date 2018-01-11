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
 * Hermione.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.opt.genetic.AbstractClassifierBasedGeneticAlgorithm;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.wizard.StartPage;

/**
 * For optimizing datasets (parameter selection) using genetic algorithm.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11103 $
 */
public class Hermione
  extends AbstractClassifierBasedGeneticAlgorithmWizard {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;

  /**
   * Initializes the menu item with no owner.
   */
  public Hermione() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public Hermione(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Hermione";
  }

  /**
   * Returns the start page for the wizard.
   *
   * @return		the page
   */
  protected StartPage getStartPage() {
    StartPage	result;

    result = new StartPage();
    result.setLogo(null);
    result.setDescription(
      "<html>The <b>" + getTitle() + "</b> genetic optimization algorithm allows you "
	+ "to optimize classifier/filter parameters on a range of datasets, using "
	+ "a user-supplied initial classifier setup.</html>");

    return result;
  }

  /**
   * Returns the genetic algorithm setup to use.
   *
   * @return		the setup
   */
  protected AbstractClassifierBasedGeneticAlgorithm getSetup() {
    return new adams.opt.genetic.Hermione();
  }
}