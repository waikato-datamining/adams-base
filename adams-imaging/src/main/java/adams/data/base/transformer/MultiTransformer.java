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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.base.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Applies the specified JAI transformers one after the other.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-transformer &lt;adams.data.jai.transformer.AbstractJAITransformer&gt; [-transformer ...] (property: transformers)
 * &nbsp;&nbsp;&nbsp;The transformers to apply one after the other.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public class MultiTransformer
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7793661256600883689L;

  /** the sub-transformers. */
  protected AbstractBufferedImageTransformer[] m_Transformers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified JAI transformers one after the other.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "transformer", "transformers",
	    new AbstractBufferedImageTransformer[0]);
  }

  /**
   * Sets the transformers to use.
   *
   * @param value	the transformers
   */
  public void setTransformers(AbstractBufferedImageTransformer[] value) {
    m_Transformers = value;
    reset();
  }

  /**
   * Returns the transformers in use.
   *
   * @return		the transformers
   */
  public AbstractBufferedImageTransformer[] getTransformers() {
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
   * Performs the transformation.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    List<BufferedImageContainer>	result;
    List<BufferedImageContainer>	input;
    int					i;
    
    result = new ArrayList<BufferedImageContainer>();
    result.add(img);
    input  = new ArrayList<BufferedImageContainer>();
    
    for (i = 0; i < m_Transformers.length; i++) {
      if (isLoggingEnabled())
	getLogger().info("Transformer #" + (i+1) + "...");
      
      input.clear();
      input.addAll(result);
      result.clear();
      
      for (BufferedImageContainer cont: input)
	result.addAll(Arrays.asList(m_Transformers[i].transform(cont)));
    }
    
    return result.toArray(new BufferedImageContainer[result.size()]);
  }
}
