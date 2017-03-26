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
 * PassThrough.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import adams.flow.container.WekaEvaluationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Does nothing, just passes through the input data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractWekaEvaluationPostProcessor {

  private static final long serialVersionUID = -6221671630652296700L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does nothing, just passes through the input data.";
  }

  /**
   * Post-processes the evaluation container.
   *
   * @param cont	the container to post-process
   * @return		the generated evaluation containers
   */
  @Override
  protected List<WekaEvaluationContainer> doPostProcess(WekaEvaluationContainer cont) {
    List<WekaEvaluationContainer>	result;

    result = new ArrayList<>();
    result.add(cont);

    return result;
  }
}
