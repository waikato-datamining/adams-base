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
 * AbstractWekaEnsembleGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaensemblegenerator;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for schemes that generate ensembles.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWekaEnsembleGenerator
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -5750322265718618569L;

  /**
   * Returns the input data the generator processes.
   *
   * @return		the accepted classes
   */
  public abstract Class[] accepts();

  /**
   * Returns the output data the generator generates.
   *
   * @return		the generated classes
   */
  public abstract Class[] generates();

  /**
   * Check method before generating the ensemble.
   *
   * @param input	the input to use
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Object input) {
    if (input == null)
      return "No input supplied!";

    return null;
  }

  /**
   * Generates the ensemble from the input.
   *
   * @param input	the input to use
   * @return		the generated ensemble
   */
  protected abstract Object doGenerate(Object input);

  /**
   * Generates the ensemble from the input.
   *
   * @param input	the input to use
   * @return		the generated ensemble
   */
  public Object generate(Object input) {
    String	msg;

    msg = check(input);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate(input);
  }
}
