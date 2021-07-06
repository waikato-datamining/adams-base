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
 * IndexedSplitsRunsCompatibility.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsevaluation;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.StoppableWithFeedback;
import adams.core.option.OptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for performing evaluations on indexed splits runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <I> the accepted input data
 * @param <O> the generated output data
 */
public interface IndexedSplitsRunsEvaluation<I, O>
  extends OptionHandler, FlowContextHandler, QuickInfoSupporter, StoppableWithFeedback {

  /**
   * The accepted classes.
   *
   * @return		the array of accepted types
   */
  public Class accepts();

  /**
   * The generated classes.
   *
   * @return		the array of generated types
   */
  public Class generates();

  /**
   * Performs an evaluation by applying the indexed splits runs to the data.
   *
   * @param data	the data to use for evaluation
   * @param runs 	the indexed splits to use
   * @param errors 	for collecting errors
   * @return		the generated evaluations, null in case of error
   */
  public O evaluate(I data, IndexedSplitsRuns runs, MessageCollection errors);
}
