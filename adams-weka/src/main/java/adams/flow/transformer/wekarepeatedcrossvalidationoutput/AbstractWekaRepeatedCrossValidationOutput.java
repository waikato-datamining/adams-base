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
 * AbstractWekaRepeatedCrossValidationOutput.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekarepeatedcrossvalidationoutput;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.WekaEvaluationContainer;

/**
 * Ancestor for classes that generate output from repeated cross-validations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the output that is being generated
 */
public abstract class AbstractWekaRepeatedCrossValidationOutput<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -5772893067952940054L;

  /**
   * Returns the class that it generates.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks whether the cross-validation results can be processed.
   *
   * @param conts	the containers to check
   * @return		null if the data can be processed, otherwise an error message
   */
  public abstract String handles(WekaEvaluationContainer[] conts);

  /**
   * Generates the data.
   *
   * @param conts	the containers to process
   * @return		the generated data
   */
  protected abstract T doGenerateOutput(WekaEvaluationContainer[] conts);

  /**
   * Generates the data.
   *
   * @param conts	the containers to process
   * @return		the generated data
   */
  public T generateOutput(WekaEvaluationContainer[] conts) {
    return doGenerateOutput(conts);
  }
}
