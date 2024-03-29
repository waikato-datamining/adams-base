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
 * HusMoments.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.moments.AbstractBufferedImageMoment;
import adams.data.image.moments.MomentHelper;
import adams.data.report.DataType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Calculates Hu's moments.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
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
 * <pre>-background-value &lt;java.awt.Color&gt; (property: backgroundValue)
 * &nbsp;&nbsp;&nbsp;The background color.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author sjb90
 * @version $Revision$
 */
public class HusMoments
  extends AbstractBufferedImageFeatureGenerator {

  private static final long serialVersionUID = 495282625574301886L;

  /** the value of the background colour **/
  protected Color m_BackgroundValue;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates Hu's moments.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("background-value", "backgroundValue", Color.WHITE);
  }

  /**
   * Sets the background color.
   *
   * @param value	the color
   */
  public void setBackgroundValue(Color value) {
    m_BackgroundValue = value;
    reset();
  }

  /**
   * Returns the background color.
   *
   * @return		the color
   */
  public Color getBackgroundValue() {
    return m_BackgroundValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundValueTipText() {
    return "The background color.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img the image to act as a template
   * @return the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition result = new HeaderDefinition();

    result.add("H_1", DataType.NUMERIC);
    result.add("H_2", DataType.NUMERIC);
    result.add("H_3", DataType.NUMERIC);
    result.add("H_4", DataType.NUMERIC);
    result.add("H_5", DataType.NUMERIC);
    result.add("H_6", DataType.NUMERIC);
    result.add("H_7", DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature genration.
   *
   * @param img the image to process
   * @return the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[] result = new List[1];

    result[0] = new ArrayList<>();
    result[0].addAll(MomentHelper.husMoments(AbstractBufferedImageMoment.imageToMatrix(img, m_BackgroundValue)));

    return result;
  }
}
