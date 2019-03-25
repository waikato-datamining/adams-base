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
 * ColumnSplitter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.datasetsplitter;

import adams.data.weka.columnfinder.ColumnFinder;
import adams.data.weka.columnfinder.NullFinder;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Splits a dataset in two based on the columns selected by a column-finder. Selected columns go in the first dataset, and the rest go in the second.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-column-finder &lt;adams.data.weka.columnfinder.ColumnFinder&gt; (property: columnFinder)
 * &nbsp;&nbsp;&nbsp;Column-finder defining which attributes go into which dataset.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.columnfinder.NullFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class ColumnSplitter extends AbstractSplitter {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 5451391004405077402L;

  /** Column-finder for selecting which attributes go in which dataset. */
  protected ColumnFinder m_ColumnFinder;

  /** Mapping from the split attributes to their source in the original dataset. */
  protected int[][] m_SourceLookup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits a dataset in two based on the columns selected by a column-finder. " +
      "Selected columns go in the first dataset, and the rest go in the second.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("column-finder", "columnFinder", new NullFinder());
  }

  /**
   * Gets the column finder.
   *
   * @return	The column finder.
   */
  public ColumnFinder getColumnFinder() {
    return m_ColumnFinder;
  }

  /**
   * Sets the column finder.
   *
   * @param value	The column finder.
   */
  public void setColumnFinder(ColumnFinder value) {
    m_ColumnFinder = value;
    reset();
  }

  /**
   * Gets the tip-text for the columnFinder option.
   *
   * @return	The tip-text as a string.
   */
  public String columnFinderTipText() {
    return "Column-finder defining which attributes go into which dataset.";
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
   * Creates an int[] which contains the unselected columns. i.e. all column
   * indices up to numColumns that aren't in selectedColumns.
   *
   * @param selectedColumns The columns to exclude from the array. Must be sorted.
   * @param numColumns  The total number of columns.
   * @return  The array of columns not in selectedColumns.
   */
  protected int[] getUnselectedColumns(int[] selectedColumns, int numColumns) {
    int nextSelectedColumnIndex = 0;
    int nextSelectedColumn = getSelectedColumn(selectedColumns, nextSelectedColumnIndex);
    int nextUnselectedColumnIndex = 0;

    // Initialise the output array
    int[] unselectedColumns = new int[numColumns - selectedColumns.length];

    // Go through each possible column number
    for (int i = 0; i < numColumns; i++) {
      // If it's in the selected columns, skip it
      if (i == nextSelectedColumn) {
        nextSelectedColumnIndex++;
        nextSelectedColumn = getSelectedColumn(selectedColumns, nextSelectedColumnIndex);
        continue;
      }

      // Otherwise add it to the unselected columns
      unselectedColumns[nextUnselectedColumnIndex] = i;
      nextUnselectedColumnIndex++;
    }

    return unselectedColumns;
  }

  /**
   * Gets the column number of the selected column at the given index.
   *
   * @param selectedColumns The array of selected columns.
   * @param index The index of the column to get.
   * @return  The number of the selected column, or -1 if index out of range.
   */
  protected int getSelectedColumn(int[] selectedColumns, int index) {
    if (index < selectedColumns.length)
      return selectedColumns[index];
    else
      return -1;
  }

  /**
   * Creates the attribute lists for the two datasets resulting from this split.
   *
   * @param dataset The dataset being split.
   * @return  Two lists, the first containing the selected attributes, the second containing the rest.
   */
  protected ArrayList<Attribute>[] splitAttributes(Instances dataset) {
    // Find the columns to put in split 0
    int[] selectedColumns = m_ColumnFinder.findColumns(dataset);

    // Sort the columns
    Arrays.sort(selectedColumns);

    // Generate the unselected columns set
    int[] unselectedColumns = getUnselectedColumns(selectedColumns, dataset.numAttributes());

    // Save the two column sets for later
    m_SourceLookup = new int[][] {selectedColumns, unselectedColumns};

    // Create the attribute lists for the new datasets
    ArrayList<Attribute>[] result = new ArrayList[2];
    result[0] = new ArrayList<>(selectedColumns.length);
    result[1] = new ArrayList<>(unselectedColumns.length);

    // Copy the attributes for the selected columns
    for (int selectedColumn : selectedColumns) {
      Attribute attribute = (Attribute) dataset.attribute(selectedColumn).copy();
      result[0].add(attribute);
    }

    // Copy the attributes for the unselected columns
    for (int unselectedColumn : unselectedColumns) {
      Attribute attribute = (Attribute) dataset.attribute(unselectedColumn).copy();
      result[1].add(attribute);
    }

    return result;
  }

  /**
   * Creates a new empty instance suited to the given dataset
   *
   * @param dataset The dataset to create the instance for.
   * @return  The created instance.
   */
  protected Instance newInstanceForDataset(Instances dataset) {
    Instance result = new DenseInstance(dataset.numAttributes());
    result.setDataset(dataset);
    return result;
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

    // Split the attributes between the two resultant datasets
    ArrayList<Attribute>[] attributeSplit = splitAttributes(dataset);

    // Create the two resultant datasets
    Instances[] result = new Instances[2];
    result[0] = new Instances(dataset.relationName(), attributeSplit[0], dataset.numInstances());
    result[1] = new Instances(dataset.relationName(), attributeSplit[1], dataset.numInstances());

    // Check for degenerate cases (either dataset is empty)
    if (attributeSplit[0].size() == 0) {
      return new Instances[] { result[0], dataset };
    } else if (attributeSplit[1].size() == 0) {
      return new Instances[] { dataset, result[1] };
    }

    // Copy the data into the resultant datasets
    for (int instanceIndex = 0; instanceIndex < dataset.numInstances(); instanceIndex++) {
      Instance sourceInstance = dataset.instance(instanceIndex);
      for (int resultIndex = 0; resultIndex < 2; resultIndex++) {
        Instances resultDataset = result[resultIndex];
        Instance resultInstance = newInstanceForDataset(resultDataset);
        for (int attributeIndex = 0; attributeIndex < resultDataset.numAttributes(); attributeIndex++) {
          Attribute attribute = resultInstance.attribute(attributeIndex);
          double sourceValue = sourceInstance.value(m_SourceLookup[resultIndex][attributeIndex]);
          resultInstance.setValue(attributeIndex, sourceValue);
        }
        resultInstance.setWeight(sourceInstance.weight());
        resultDataset.add(resultInstance);
      }
    }

    return result;
  }
}