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
 * NullEvaluation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsevaluation;

import adams.core.MessageCollection;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.flow.core.Unknown;

/**
 * Dummy, does nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NullEvaluation
  extends AbstractIndexedSplitsRunsEvaluation<Object, String> {

  private static final long serialVersionUID = -2999867662233949084L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does nothing.";
  }

  /**
   * The accepted classes.
   *
   * @return		the array of accepted types
   */
  public Class accepts() {
    return Unknown.class;
  }

  /**
   * The generated classes.
   *
   * @return		the array of generated types
   */
  public Class generates() {
    return String.class;
  }

  /**
   * Returns whether flow context is actually required.
   *
   * @return true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return false;
  }

  /**
   * Performs an evaluation by applying the indexed splits runs to the data.
   *
   * @param data   the data to use for evaluation
   * @param runs   the indexed splits to use
   * @param errors for collecting errors
   * @return the generated evaluations, null in case of error
   */
  @Override
  protected String doEvaluate(Object data, IndexedSplitsRuns runs, MessageCollection errors) {
    return "";
  }
}
