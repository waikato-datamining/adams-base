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
 * AbstractClassifierSetupProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaclassifiersetupprocessor;

import adams.core.option.AbstractOptionHandler;
import weka.classifiers.Classifier;

/**
 * Ancestor for schemes that preprocess classifier arrays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractClassifierSetupProcessor
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -5938243697011035109L;

  /**
   * Hook method for performing checks on the classifiers.
   *
   * @param classifiers	the classifiers to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Classifier[] classifiers) {
    if (classifiers == null)
      return "No classifiers provided!";
    return null;
  }

  /**
   * Processes the classifier array.
   *
   * @param classifiers	the classifiers to process
   * @return		the processed classifiers
   */
  protected abstract Classifier[] doProcess(Classifier[] classifiers);

  /**
   * Processes the classifier array.
   *
   * @param classifiers	the classifiers to process
   * @return		the processed classifiers
   */
  public Classifier[] process(Classifier[] classifiers) {
    String	msg;

    msg = check(classifiers);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doProcess(classifiers);
  }
}
