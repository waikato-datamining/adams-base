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
 * RowSplitter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.datasetsplitter;

import adams.data.weka.rowfinder.NullFinder;
import adams.data.weka.rowfinder.RowFinder;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Splits a dataset in two based on the rows selected by the row-finder. Instances found by the row-finder go in the first dataset, and the rest go in the second.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-row-finder &lt;adams.data.weka.rowfinder.RowFinder&gt; (property: rowFinder)
 * &nbsp;&nbsp;&nbsp;The row-finder to use to select rows for the first dataset.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.rowfinder.NullFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class RowSplitter extends AbstractSplitter {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 4638233201875563585L;

  /** Sentinel for when there's no more selected rows. */
  public static final int NO_NEXT_SELECTED_INDEX = -1;

  /** The selector for splitting rows between the two datasets. */
  protected RowFinder m_RowFinder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits a dataset in two based on the rows selected by the row-finder. " +
      "Instances found by the row-finder go in the first dataset, " +
      "and the rest go in the second.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("row-finder", "rowFinder", new NullFinder());
  }

  /**
   * Gets the row-finder to use to select rows for the first dataset.
   *
   * @return The row-finder.
   */
  public RowFinder getRowFinder() {
    return m_RowFinder;
  }

  /**
   * Sets the row-finder to use to select rows for the first dataset.
   *
   * @param value The row-finder.
   */
  public void setRowFinder(RowFinder value) {
    m_RowFinder = value;
    reset();
  }

  /**
   * Gets the tip-text for the rowFinder option.
   *
   * @return  The tip-text as a string.
   */
  public String rowFinderTipText() {
    return "The row-finder to use to select rows for the first dataset.";
  }

  /**
   * Checks that the input data is correctly formatted for our purposes.
   *
   * @param dataset	The dataset to check.
   * @return	Null if all okay, or an error message if not.
   */
  public String check(Instances dataset) {
    // Must have a dataset
    if (dataset == null)
      return "Dataset not supplied.";

    // All okay
    return null;
  }

  /**
   * Gets the next index selected by the row-finder.
   *
   * @param selectedIndices The array of selected indices.
   * @param index The index to get from the array.
   * @return  The next selected row-index, or NO_NEXT_SELECTED_INDEX if there isn't one.
   */
  protected int getNextSelectedIndex(int[] selectedIndices, int index) {
    // Check there's another index to look for
    if (index >= selectedIndices.length) return NO_NEXT_SELECTED_INDEX;

    return selectedIndices[index];
  }

  /**
   * Splits the given dataset into a number of other datasets. Should be
   * implemented by sub-classes to perform actual splitting.
   *
   * @param dataset	The dataset to split.
   * @return	An array of datasets resulting from the split.
   */
  @Override
  public Instances[] split(Instances dataset) {
    // Validate first
    String error = check(dataset);
    if (error != null) throw new IllegalArgumentException(error);

    // Use the row-finder to select the desired rows
    int[] selectedIndices = m_RowFinder.findRows(dataset);

    // Check for degenerate cases (either split is empty)
    if (selectedIndices.length == 0) {
      return new Instances[] { new Instances(dataset, 0), dataset };
    } else if (selectedIndices.length == dataset.numInstances()) {
      return new Instances[] { dataset, new Instances(dataset, 0) };
    }

    // Ensure we encounter the selected rows in order
    Arrays.sort(selectedIndices);

    // Remember the next row we are looking for (and how
    // to find the one after that)
    int selectedIndicesIndex = 0;
    int nextSelectedIndex = getNextSelectedIndex(selectedIndices, selectedIndicesIndex);

    // Create the two resulting datasets
    Instances[] result = new Instances[2];
    result[0] = new Instances(dataset, 0);
    result[1] = new Instances(dataset, 0);

    // Process each instance from the dataset
    for (int i = 0; i < dataset.numInstances(); i++) {
      // Clone the instance
      Instance copy = (Instance) dataset.instance(i).copy();

      // Put the instance in one of the datasets
      if (i == nextSelectedIndex) {
        result[0].add(copy);
        selectedIndicesIndex++;
        nextSelectedIndex = getNextSelectedIndex(selectedIndices, selectedIndicesIndex);
      } else {
        result[1].add(copy);
      }
    }

    return result;
  }
}