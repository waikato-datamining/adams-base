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
 * Cropping.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.CropAlgorithm;
import adams.data.image.transformer.crop.AbstractCropAlgorithm;
import adams.data.image.transformer.crop.NoCrop;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Crops the images passing through using the specified crop algorithm.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-algorithm &lt;adams.data.jai.transformer.crop.AbstractCropAlgorithm&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The crop algorithm to apply to the image.
 * &nbsp;&nbsp;&nbsp;default: adams.data.jai.transformer.crop.NoCrop
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Cropping
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the crop algorithm to use. */
  protected AbstractCropAlgorithm m_Algorithm;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Crops the images passing through using the specified crop algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "algorithm", "algorithm",
	    new NoCrop());
  }

  /**
   * Sets the crop algorithm.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractCropAlgorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the crop algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractCropAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The crop algorithm to apply to the image.";
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
    Report			report;
    
    result = (BufferedImageContainer) img.getHeader();
    result.setImage(m_Algorithm.crop(img.getImage()));
    
    report = result.getReport();
    if (report != null) {
      report.addField(new Field(CropAlgorithm.CROP_LEFT,   DataType.NUMERIC));
      report.addField(new Field(CropAlgorithm.CROP_TOP,    DataType.NUMERIC));
      report.addField(new Field(CropAlgorithm.CROP_RIGHT,  DataType.NUMERIC));
      report.addField(new Field(CropAlgorithm.CROP_BOTTOM, DataType.NUMERIC));
      
      report.setNumericValue(CropAlgorithm.CROP_LEFT,   m_Algorithm.getTopLeft().getX());
      report.setNumericValue(CropAlgorithm.CROP_TOP,    m_Algorithm.getTopLeft().getY());
      report.setNumericValue(CropAlgorithm.CROP_RIGHT,  m_Algorithm.getBottomRight().getX());
      report.setNumericValue(CropAlgorithm.CROP_BOTTOM, m_Algorithm.getBottomRight().getY());
    }

    return new BufferedImageContainer[]{result};
  }
}
