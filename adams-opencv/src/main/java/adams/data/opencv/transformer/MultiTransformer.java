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
 * MultiTransformer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.transformer;

import adams.core.QuickInfoHelper;
import adams.data.opencv.OpenCVImageContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies the specified OpenCV transformers one after the other.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-transformer &lt;AbstractOpenCVTransformer&gt; [-transformer ...] (property: transformers)
 * &nbsp;&nbsp;&nbsp;The transformers to apply one after the other.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiTransformer
    extends AbstractOpenCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7793661256600883689L;

  /** the sub-transformers. */
  protected AbstractOpenCVTransformer[] m_Transformers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified OpenCV transformers one after the other.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "transformer", "transformers",
        new AbstractOpenCVTransformer[0]);
  }

  /**
   * Sets the transformers to use.
   *
   * @param value	the transformers
   */
  public void setTransformers(AbstractOpenCVTransformer[] value) {
    m_Transformers = value;
    reset();
  }

  /**
   * Returns the transformers in use.
   *
   * @return		the transformers
   */
  public AbstractOpenCVTransformer[] getTransformers() {
    return m_Transformers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String transformersTipText() {
    return "The transformers to apply one after the other.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "transformers", m_Transformers, "transformers: ");
  }

  /**
   * Performs the transformation.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected OpenCVImageContainer[] doTransform(OpenCVImageContainer img) {
    List<OpenCVImageContainer>	result;
    List<OpenCVImageContainer>	input;
    int					i;

    result = new ArrayList<>();
    result.add(img);
    input  = new ArrayList<>();

    for (i = 0; i < m_Transformers.length; i++) {
      if (isLoggingEnabled())
        getLogger().info("Transformer #" + (i+1) + "...");

      input.clear();
      input.addAll(result);
      result.clear();

      for (OpenCVImageContainer cont: input)
        result.addAll(Arrays.asList(m_Transformers[i].transform(cont)));
    }

    return result.toArray(new OpenCVImageContainer[result.size()]);
  }
}
