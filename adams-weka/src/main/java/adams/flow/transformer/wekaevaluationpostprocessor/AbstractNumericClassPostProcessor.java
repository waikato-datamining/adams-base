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
 * AbstractNumericClassPostProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import weka.classifiers.Evaluation;

/**
 * Ancestor for numeric class post-processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNumericClassPostProcessor
  extends AbstractWekaEvaluationPostProcessor {

  private static final long serialVersionUID = -8126062783012759418L;

  /**
   * Checks the container whether it can be processed.
   *
   * @param eval	the container to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(Evaluation eval) {
    String	result;

    result = super.check(eval);

    if (result == null) {
      if (!eval.getHeader().classAttribute().isNumeric())
	result = "Class attribute is not numeric!";
    }

    return result;
  }
}
