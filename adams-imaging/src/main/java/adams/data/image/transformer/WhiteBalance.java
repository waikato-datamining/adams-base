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
 * WhiteBalance.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.transformer.whitebalance.AbstractWhiteBalanceAlgorithm;
import adams.data.image.transformer.whitebalance.NoBalance;

/**
 <!-- globalinfo-start -->
 * Processes the images passing through using the specified white balance algorithm.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-algorithm &lt;adams.data.jai.transformer.whitebalance.AbstractWhiteBalanceAlgorithm&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The white balance algorithm to apply to the image.
 * &nbsp;&nbsp;&nbsp;default: adams.data.jai.transformer.whitebalance.NoBalance
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8951 $
 */
public class WhiteBalance
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the crop algorithm to use. */
  protected AbstractWhiteBalanceAlgorithm m_Algorithm;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Processes the images passing through using the specified white balance algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "algorithm", "algorithm",
	    new NoBalance());
  }

  /**
   * Sets the white balance algorithm.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractWhiteBalanceAlgorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the white balance algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractWhiteBalanceAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The white balance algorithm to apply to the image.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "algorithm", m_Algorithm);
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer	result;
    
    result = (BufferedImageContainer) img.getHeader();
    result.setImage(m_Algorithm.balance(img.getImage()));
    
    return new BufferedImageContainer[]{result};
  }
}
