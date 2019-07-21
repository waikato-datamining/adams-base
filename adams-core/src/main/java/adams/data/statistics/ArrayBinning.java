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
 * ArrayBinning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import adams.data.binning.Bin;
import adams.data.binning.algorithm.BinningAlgorithm;
import adams.data.binning.algorithm.ManualBinning;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.postprocessing.AbstractBinPostProcessing;
import adams.data.binning.postprocessing.PassThrough;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Bins the data from given array using the specified algorithm and post-processing approach.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-normalize &lt;boolean&gt; (property: normalize)
 * &nbsp;&nbsp;&nbsp;If set to true the data gets normalized first before getting binned.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-algorithm &lt;adams.data.binning.algorithm.BinningAlgorithm&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The binning algorithm to apply to the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.binning.algorithm.ManualBinning
 * </pre>
 *
 * <pre>-post-processing &lt;adams.data.binning.postprocessing.AbstractBinPostProcessing&gt; (property: postProcessing)
 * &nbsp;&nbsp;&nbsp;The post-processing to apply to the generated bins.
 * &nbsp;&nbsp;&nbsp;default: adams.data.binning.postprocessing.PassThrough
 * </pre>
 *
 * <pre>-display-ranges &lt;boolean&gt; (property: displayRanges)
 * &nbsp;&nbsp;&nbsp;If enabled, the bins get description according to their range, rather than
 * &nbsp;&nbsp;&nbsp;a simple index.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the data to process
 */
public class ArrayBinning<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 3595293227007460735L;

  /** whether to normalize the data. */
  protected boolean m_Normalize;

  /** the binning algorithm. */
  protected BinningAlgorithm m_Algorithm;

  /** the post-processing scheme. */
  protected AbstractBinPostProcessing m_PostProcessing;

  /** whether to use the ranges as bin description. */
  protected boolean m_DisplayRanges;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Bins the data from given array using the specified algorithm and "
          + "post-processing approach.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "normalize", "normalize",
      false);

    m_OptionManager.add(
      "algorithm", "algorithm",
      new ManualBinning());

    m_OptionManager.add(
      "post-processing", "postProcessing",
      new PassThrough());

    m_OptionManager.add(
      "display-ranges", "displayRanges",
      false);
  }

  /**
   * Sets whether to normalize the data before generating the histogram.
   *
   * @param value 	if true the data gets normalized first
   */
  public void setNormalize(boolean value) {
    m_Normalize = value;
    reset();
  }

  /**
   * Returns whether to normalize the data before generating the histogram.
   *
   * @return 		true if the data gets normalized first
   */
  public boolean getNormalize() {
    return m_Normalize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizeTipText() {
    return "If set to true the data gets normalized first before getting binned.";
  }

  /**
   * Sets the binning algorithm.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(BinningAlgorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the binning algorithm.
   *
   * @return 		the algorithm
   */
  public BinningAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The binning algorithm to apply to the data.";
  }

  /**
   * Sets the post-processing scheme to apply to the bins.
   *
   * @param value 	the post-processing
   */
  public void setPostProcessing(AbstractBinPostProcessing value) {
    m_PostProcessing = value;
    reset();
  }

  /**
   * Returns the post-processing scheme to apply to the bins.
   *
   * @return 		the post-processing
   */
  public AbstractBinPostProcessing getPostProcessing() {
    return m_PostProcessing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessingTipText() {
    return "The post-processing to apply to the generated bins.";
  }

  /**
   * Sets whether to use the bin ranges as their description rather than a
   * simple index.
   *
   * @param value 	true if to display the ranges
   */
  public void setDisplayRanges(boolean value) {
    m_DisplayRanges = value;
    reset();
  }

  /**
   * Returns whether to use the bin ranges as their description rather than a
   * simple index.
   *
   * @return 		true if to display the ranges
   */
  public boolean getDisplayRanges() {
    return m_DisplayRanges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayRangesTipText() {
    return "If enabled, the bins get description according to their range, rather than a simple index.";
  }

  /**
   * Returns the length of the stored arrays.
   *
   * @return		the length of the arrays, -1 if none stored
   */
  public int getLength() {
    if (size() > 0)
      return get(0).length;
    else
      return -1;
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  @Override
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  @Override
  public int getMax() {
    return 1;
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  @Override
  protected StatisticContainer doCalculate() {
    StatisticContainer<Double>	result;
    int 			i;
    List<Bin<Integer>> 		bins;
    Number[]			array;
    String			prefix;

    array = get(0);
    if (m_Normalize)
      array = StatUtils.normalize(array);

    try {
      bins = m_Algorithm.generateBins(Wrapping.wrap(array));
      bins = m_PostProcessing.postProcessBins(bins);
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }

    result = new StatisticContainer<>(size(), bins.size());

    // header
    prefix = "bin";
    for (i = 0; i < bins.size(); i++) {
      if (m_DisplayRanges)
	result.setHeader(i, bins.get(i).getInterval().getValue());
      else
	result.setHeader(i, prefix + " " + (i +1));
    }

    // fill spreadsheet
    for (i = 0; i < bins.size(); i++)
      result.setCell(0, i, (double) bins.get(i).size());

    return result;
  }
}
