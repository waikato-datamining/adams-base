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
 * Simple.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekadatasetsmerge;

import weka.core.Instances;

import java.util.Enumeration;

/**
 <!-- globalinfo-start -->
 * Just merges the datasets side by side. Requires the datasets to have the same number of rows.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-class-finder &lt;adams.data.weka.columnfinder.ColumnFinder&gt; (property: classFinder)
 * &nbsp;&nbsp;&nbsp;The column finder to use to find class attributes in the datasets.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.columnfinder.Class
 * </pre>
 *
 * <pre>-dataset-names &lt;adams.core.base.BaseString&gt; [-dataset-names ...] (property: datasetNames)
 * &nbsp;&nbsp;&nbsp;The list of dataset names to use in attribute renaming.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-attr-renames-exp &lt;adams.core.base.BaseRegExp&gt; [-attr-renames-exp ...] (property: attributeRenamesExp)
 * &nbsp;&nbsp;&nbsp;The expressions to use to select attribute names for renaming (one per dataset
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-attr-renames-format &lt;adams.core.base.BaseString&gt; [-attr-renames-format ...] (property: attributeRenamesFormat)
 * &nbsp;&nbsp;&nbsp;One format string for each renaming expression to specify how to rename
 * &nbsp;&nbsp;&nbsp;the attribute. Can contain the {DATASET} keyword which will be replaced
 * &nbsp;&nbsp;&nbsp;by the dataset name, and also group identifiers which will be replaced by
 * &nbsp;&nbsp;&nbsp;groups from the renaming regex.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-output-name &lt;java.lang.String&gt; (property: outputName)
 * &nbsp;&nbsp;&nbsp;The name to use for the merged dataset.
 * &nbsp;&nbsp;&nbsp;default: output
 * </pre>
 *
 * <pre>-ensure-equal-values &lt;boolean&gt; (property: ensureEqualValues)
 * &nbsp;&nbsp;&nbsp;Whether multiple attributes being merged into a single attribute require
 * &nbsp;&nbsp;&nbsp;equal values from all sources.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 * <p>
 * Performs a merge over a set of datasets of equal size by joining
 * rows of equal index in a simple concatenation.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Simple
  extends AbstractMerge {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 6417253231850358108L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just merges the datasets side by side. Requires the datasets to have the same number of rows.";
  }

  /**
   * Hook method for performing checks before attempting the merge.
   *
   * @param datasets the datasets to merge
   * @return null if successfully checked, otherwise error message
   */
  @Override
  protected String check(Instances[] datasets) {
    String result;
    int i;

    result = super.check(datasets);

    if (result == null) {
      for (i = 1; i < datasets.length; i++) {
	if (datasets[0].numInstances() != datasets[i].numInstances())
	  return "Datasets #" + (i + 1) + "has " + datasets[i].numInstances() + " rows instead of " + datasets[0].numInstances();
      }
    }

    return result;
  }

  /**
   * Allows specific merge methods to specify the order in which rows are placed
   * into the merged dataset, and which rows from the source datasets are used
   * for the source data.
   *
   * @return An enumeration of the source rows, one row for each dataset.
   */
  @Override
  protected Enumeration<int[]> getRowSetEnumeration() {
    return new SimpleRowSetIterator(m_Datasets);
  }

  /**
   * Enumeration class which just returns the concatenation of the source
   * data rows in order.
   */
  public static class SimpleRowSetIterator implements Enumeration<int[]> {

    /** The internal row counter. */
    private int m_NextRow = 0;

    /** The number of rows to output. */
    private int m_RowCount;

    /** The number of datasets. */
    private int m_DatasetCount;

    /**
     * Constructs an enumeration over the rows in the source datasets.
     *
     * @param datasets The source datasets to enumerate.
     */
    private SimpleRowSetIterator(Instances[] datasets) {
      m_RowCount = datasets[0].numInstances();
      m_DatasetCount = datasets.length;
    }

    @Override
    public boolean hasMoreElements() {
      return m_NextRow < m_RowCount;
    }

    @Override
    public int[] nextElement() {
      // Create the return array
      int[] nextElement = new int[m_DatasetCount];

      // Add the row from each source dataset
      for (int i = 0; i < nextElement.length; i++) {
	nextElement[i] = m_NextRow;
      }

      // Move to the next row
      m_NextRow++;

      // Return the row-set
      return nextElement;
    }
  }
}