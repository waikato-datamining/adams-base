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
 * SubImages.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.jai.transformer.subimages.AbstractSubImagesGenerator;
import adams.data.jai.transformer.subimages.PassThrough;

/**
 <!-- globalinfo-start -->
 * Produces subimages from the input image using the specified generator.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-generator &lt;adams.data.jai.transformer.subimages.AbstractSubImagesGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the subimages.
 * &nbsp;&nbsp;&nbsp;default: adams.data.jai.transformer.subimages.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SubImages
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;
  
  /** the subimages generator. */
  protected AbstractSubImagesGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Produces subimages from the input image using the specified generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new PassThrough());
  }

  /**
   * Sets the generator.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractSubImagesGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator.
   *
   * @return		the generator
   */
  public AbstractSubImagesGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String generatorTipText() {
    return "The generator to use for generating the subimages.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator);
  }

  /**
   * Generates the subimages.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the subimages
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    List<BufferedImageContainer>	result;

    result = m_Generator.process(img);

    return result.toArray(new BufferedImageContainer[result.size()]);
  }
}
