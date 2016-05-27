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
 * ReportFieldSubtraction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVImageContainer;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * For each of the specified report fields, the difference between the value from the first report and the second is calculated. The updated report is output with the first image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to create the difference for.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12325 $
 */
public class ReportFieldSubtraction
  extends AbstractBoofCVMultiImageOperation {

  private static final long serialVersionUID = 7381673951864996785L;

  /** the report fields to subtract. */
  protected Field[] m_Fields;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "For each of the specified report fields, the difference between the value "
        + "from the first report and the second is calculated. The updated report "
        + "is output with the first image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "fields",
      new Field[0]);
  }

  /**
   * Sets the fields.
   *
   * @param value	the fields
   */
  public void setFields(Field[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the fields.
   *
   * @return		the fields
   */
  public Field[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fieldsTipText() {
    return "The fields to create the difference for.";
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumImagesRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no upper limit
   */
  public int maxNumImagesRequired() {
    return 2;
  }

  /**
   * Checks the images.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(BoofCVImageContainer[] images) {
    String	msg;
    int		i;

    super.check(images);

    msg = null;

    for (Field field: m_Fields) {
      for (i = 0; i < images.length; i++) {
	if (!images[i].hasReport()) {
	  msg = "Image #" + (i+1) + " has no report attached!";
	  break;
	}
	if (!images[i].getReport().hasValue(field)) {
	  msg = "Image #" + (i+1) + " does not have field '" + field + "'!";
	  break;
	}
	if (images[i].getReport().getDoubleValue(field) == null) {
	  msg = "Image #" + (i+1) + " does not have a numeric value for field '" + field + "'!";
	  break;
	}
      }
      if (msg != null)
	break;
    }

    if (msg != null)
      throw new IllegalStateException(msg);
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doProcess(BoofCVImageContainer[] images) {
    BoofCVImageContainer[]	result;
    double			diff;

    result    = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) images[0].getClone();
    for (Field field: m_Fields) {
      diff = images[0].getReport().getDoubleValue(field) - images[1].getReport().getDoubleValue(field);
      result[0].getReport().setValue(field, diff);
    }

    return result;
  }
}
