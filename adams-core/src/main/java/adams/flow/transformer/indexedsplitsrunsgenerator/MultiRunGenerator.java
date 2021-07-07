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
 * MultiRunGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.Randomizable;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;

/**
 * Generates the specified number of runs using the configured base generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiRunGenerator
  extends AbstractIndexedSplitsRunsGenerator {

  private static final long serialVersionUID = -4745486074603563061L;

  /** the number of runs to generate. */
  protected int m_NumRuns;

  /** the base generator. */
  protected IndexedSplitsRunsGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the specified number of runs using the configured base generator.\n"
      + "If the base generator can be randomized, then the 1-based run number is used as seed value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-runs", "numRuns",
      10, 1, null);

    m_OptionManager.add(
      "generator", "generator",
      new ManualSplitGenerator());
  }

  /**
   * Sets the number of runs to generate.
   *
   * @param value	the runs
   */
  public void setNumRuns(int value) {
    m_NumRuns = value;
    reset();
  }

  /**
   * Returns the number of runs to generate.
   *
   * @return		the runs
   */
  public int getNumRuns() {
    return m_NumRuns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runRunsTipText() {
    return "The number of runs to generate.";
  }

  /**
   * Sets the generator to use for the runs.
   *
   * @param value	the generator
   */
  public void setGenerator(IndexedSplitsRunsGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use for the runs.
   *
   * @return		the generator
   */
  public IndexedSplitsRunsGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The base generator to use for each run.";
  }

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return the classes
   */
  @Override
  public Class[] accepts() {
    return m_Generator.accepts();
  }

  /**
   * Generates the indexed splits.
   *
   * @param data   the data to use for generating the splits
   * @param errors for storing any errors occurring during processing
   * @return the splits or null in case of error
   */
  @Override
  protected IndexedSplitsRuns doGenerate(Object data, MessageCollection errors) {
    IndexedSplitsRuns	result;
    IndexedSplitsRuns	current;
    int			i;

    result = new IndexedSplitsRuns();

    m_Generator.setFlowContext(getFlowContext());

    for (i = 0; i < m_NumRuns; i++) {
      if (m_Generator instanceof Randomizable)
	((Randomizable) m_Generator).setSeed(i + 1);
      current = m_Generator.generate(data, errors);
      if (current == null) {
        errors.add("Failed to generate output in run #" + (i+1) + "!");
        return null;
      }
      for (IndexedSplitsRun run: current) {
        run.setID(result.size());
        result.add(run);
      }
    }

    return result;
  }
}
