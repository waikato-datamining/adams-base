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
 * Histogram.java
 * Copyright (C) 2022-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.opencv.OpenCVImageContainer;
import adams.data.report.DataType;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.split;
import static org.bytedeco.opencv.global.opencv_imgproc.calcHist;

/**
 <!-- globalinfo-start -->
 * Computes a histogram from the incoming image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for the feature names.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to generate.
 * &nbsp;&nbsp;&nbsp;default: 256
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 256
 * </pre>
 *
 * <pre>-min-value &lt;float&gt; (property: minValue)
 * &nbsp;&nbsp;&nbsp;The min value to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-max-value &lt;float&gt; (property: maxValue)
 * &nbsp;&nbsp;&nbsp;The max value to use.
 * &nbsp;&nbsp;&nbsp;default: 255.0
 * </pre>
 *
 * <pre>-group-channels &lt;boolean&gt; (property: groupChannels)
 * &nbsp;&nbsp;&nbsp;If enabled, grouping is by channel rather than by bin.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Histogram
  extends AbstractOpenCVFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the number of bins. */
  protected int m_NumBins;

  /** the minimum value. */
  protected float m_MinValue;

  /** the maximum value. */
  protected float m_MaxValue;

  /** whether to group the channels. */
  protected boolean m_GroupChannels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes a histogram from the incoming image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-bins", "numBins",
      256, 1, 256);

    m_OptionManager.add(
      "min-value", "minValue",
      0.0f);

    m_OptionManager.add(
      "max-value", "maxValue",
      255.0f);

    m_OptionManager.add(
      "group-channels", "groupChannels",
      false);
  }

  /**
   * Sets the number of bins to use.
   *
   * @param value	the bins
   */
  public void setNumBins(int value) {
    if (getOptionManager().isValid("numBins", value)) {
      m_NumBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to use.
   *
   * @return		the bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numBinsTipText() {
    return "The number of bins to generate.";
  }

  /**
   * Sets the min value.
   *
   * @param value	the min value
   */
  public void setMinValue(float value) {
    if (getOptionManager().isValid("minValue", value)) {
      m_MinValue = value;
      reset();
    }
  }

  /**
   * Returns the min value.
   *
   * @return		the min value
   */
  public float getMinValue() {
    return m_MinValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minValueTipText() {
    return "The min value to use.";
  }

  /**
   * Sets the max value.
   *
   * @param value	the max value
   */
  public void setMaxValue(float value) {
    if (getOptionManager().isValid("maxValue", value)) {
      m_MaxValue = value;
      reset();
    }
  }

  /**
   * Returns the max value.
   *
   * @return		the max value
   */
  public float getMaxValue() {
    return m_MaxValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxValueTipText() {
    return "The max value to use.";
  }

  /**
   * Sets the whether to group by channels.
   *
   * @param value 	true if to group by channels
   */
  public void setGroupChannels(boolean value) {
    m_GroupChannels = value;
    reset();
  }

  /**
   * Returns whether to group by channels.
   *
   * @return 		true if to group by channels
   */
  public boolean getGroupChannels() {
    return m_GroupChannels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupChannelsTipText() {
    return "If enabled, grouping is by channel rather than by bin.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(OpenCVImageContainer img) {
    HeaderDefinition		result;
    int				n;
    int				i;

    result = new HeaderDefinition();
    if (m_GroupChannels) {
      for (i = 0; i < m_NumBins; i++) {
	for (n = 0; n < img.getContent().channels(); n++)
	  result.add("channel-" + n + "-" + i, DataType.NUMERIC);
      }
    }
    else {
      for (n = 0; n < img.getContent().channels(); n++) {
	for (i = 0; i < m_NumBins; i++)
	  result.add("channel-" + n + "-" + i, DataType.NUMERIC);
      }
    }

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(OpenCVImageContainer img) {
    List<Object>[]	result;
    MatVector 		channelImgs;
    IntPointer		channelIdxs;
    IntPointer 		histSize;
    FloatPointer 	ranges;
    Mat[] 		hist;
    double[][] 		dest;
    Indexer 		indexer;
    int 		i;
    int			n;
    int[]		idxs;

    result    = new List[1];
    result[0] = new ArrayList<>();

    channelImgs = new MatVector();
    split(img.getContent(), channelImgs);

    idxs = new int[img.getContent().channels()];
    for (i = 0; i < idxs.length; i++)
      idxs[i] = i;
    channelIdxs = new IntPointer(idxs);

    hist = new Mat[img.getContent().channels()];
    for (n = 0; n < img.getContent().channels(); n++) {
      histSize = new IntPointer(1);
      histSize.put(0, m_NumBins);
      ranges = new FloatPointer(m_MinValue, m_MaxValue);
      hist[n] = new Mat();
      calcHist(channelImgs.get(n), 1, channelIdxs, new Mat(), hist[n], 1, histSize, ranges);
    }

    dest = new double[img.getContent().channels()][m_NumBins];
    for (n = 0; n < img.getContent().channels(); n++) {
      indexer = hist[n].createIndexer();
      for (i = 0; i < m_NumBins; i++)
	dest[n][i] = indexer.getDouble(i);
    }

    if (m_GroupChannels) {
      for (i = 0; i < m_NumBins; i++) {
	for (n = 0; n < img.getContent().channels(); n++)
	  result[0].add(dest[n][i]);
      }
    }
    else {
      for (n = 0; n < img.getContent().channels(); n++) {
	for (i = 0; i < m_NumBins; i++)
	  result[0].add(dest[n][i]);
      }
    }

    return result;
  }
}
