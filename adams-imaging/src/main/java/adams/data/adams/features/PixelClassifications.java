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
 * PixelClassifications.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.adams.features;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.data.adams.features.Pixels.PixelType;
import adams.data.adams.transformer.Crop;
import adams.data.adams.transformer.Crop.Anchor;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.transformer.PixelSelector;
import adams.flow.transformer.pixelselector.AddClassification;

/**
 <!-- globalinfo-start -->
 * Generates a feature vector for each pixel classification that is stored in the image's report.<br/>
 * Pixel classifications are generated with the adams.flow.transformer.PixelSelector transformer, using the adams.flow.transformer.pixelselector.AddClassification action.<br/>
 * Automatically adds the classification label associated with a classification position in the report as a separate attribute.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the crop rectangle.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the crop rectangle.
 * &nbsp;&nbsp;&nbsp;default: 75
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-anchor &lt;TOP_LEFT|TOP_RIGHT|CENTER|BOTTOM_LEFT|BOTTOM_RIGHT&gt; (property: anchor)
 * &nbsp;&nbsp;&nbsp;Defines where to anchor the position on the crop rectangle.
 * &nbsp;&nbsp;&nbsp;default: TOP_LEFT
 * </pre>
 * 
 * <pre>-pixel-type &lt;RGB_SINGLE|RGB_SEPARATE|HSB_SEPARATE&gt; (property: pixelType)
 * &nbsp;&nbsp;&nbsp;The pixel type to use.
 * &nbsp;&nbsp;&nbsp;default: RGB_SINGLE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class PixelClassifications
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -511904410456677378L;

  /** the width of the crop rectangle. */
  protected int m_Width;
  
  /** the height of the crop rectangle. */
  protected int m_Height;
  
  /** where to anchor the position on the rectangle. */
  protected Anchor m_Anchor;
  
  /** how to output the pixels. */
  protected PixelType m_PixelType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a feature vector for each pixel classification that is stored "
	+ "in the image's report.\n"
	+ "Pixel classifications are generated with the " 
	+ PixelSelector.class.getName() + " transformer, using the "
	+ AddClassification.class.getName() + " action.\n"
	+ "Automatically adds the classification label associated with a "
	+ "classification position in the report as a separate attribute.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"width", "width",
	100, 1, null);

    m_OptionManager.add(
	"height", "height",
	75, 1, null);

    m_OptionManager.add(
	"anchor", "anchor",
	Anchor.TOP_LEFT);

    m_OptionManager.add(
	    "pixel-type", "pixelType",
	    PixelType.RGB_SINGLE);
  }

  /**
   * Sets the width of the crop rectangle.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if (value > 0) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().severe("Width has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the width of the crop rectangle.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String widthTipText() {
    return "The width of the crop rectangle.";
  }

  /**
   * Sets the height of the crop rectangle.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    if (value > 0) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().severe("Height has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the height of the crop rectangle.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String heightTipText() {
    return "The height of the crop rectangle.";
  }

  /**
   * Sets where to anchor the position on the rectangle.
   *
   * @param value	the anchor
   */
  public void setAnchor(Anchor value) {
    m_Anchor = value;
    reset();
  }

  /**
   * Returns where to anchor the position on the rectangle.
   *
   * @return		the anchor
   */
  public Anchor getAnchor() {
    return m_Anchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String anchorTipText() {
    return "Defines where to anchor the position on the crop rectangle.";
  }

  /**
   * Sets the type of pixel to output.
   *
   * @param value	the type
   */
  public void setPixelType(PixelType value) {
    m_PixelType = value;
    reset();
  }

  /**
   * Returns the type of pixel to output.
   *
   * @return		the type
   */
  public PixelType getPixelType() {
    return m_PixelType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String pixelTypeTipText() {
    return "The pixel type to use.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;
    Crop		crop;
    Pixels		pixels;

    // 1st: crop
    crop = new Crop();
    crop.setWidth(m_Width);
    crop.setHeight(m_Height);
    crop.setAnchor(Anchor.TOP_LEFT);
    crop.setX(1);
    crop.setY(1);
    img = crop.transform(img)[0];
    crop.destroy();
    
    // 2nd: turn into pixels
    pixels = new Pixels();
    pixels.setPixelType(m_PixelType);
    result = pixels.createHeader(img);
    pixels.destroy();

    return result;
  }

  /**
   * Returns the classification indices.
   * 
   * @param img		the current image to process
   * @return		the indices
   */
  protected Integer[] getClassificationIndices(BufferedImageContainer img) {
    ArrayList<Integer>	result;
    List<AbstractField>	fields;
    
    result = new ArrayList<Integer>();
    fields = img.getReport().getFields();
    for (AbstractField field: fields) {
      if (field.getName().startsWith(AddClassification.CLASSIFICATION))
	result.add(Integer.parseInt(field.getName().substring(AddClassification.CLASSIFICATION.length())));
    }
    
    return result.toArray(new Integer[result.size()]);
  }
  
  /**
   * Returns the pixel location to paint.
   * 
   * @param img		the current image to process
   * @param index	the pixel location
   * @return		the location, null if none found
   */
  protected Point getPixelLocation(BufferedImageContainer img, int index) {
    Point	result;
    Report	report;
    
    result = null;
    
    if ((img != null) && (img.hasReport())) {
      report = img.getReport();
      if (report.hasValue(AddClassification.PIXEL_X + index) && report.hasValue(AddClassification.PIXEL_Y + index)) {
	result = new Point(
	    report.getDoubleValue(AddClassification.PIXEL_X + index).intValue(),
	    report.getDoubleValue(AddClassification.PIXEL_Y + index).intValue());
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
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<List<Object>>		result;
    Integer[]			indices;
    Crop			crop;
    Pixels			pixels;
    Point			loc;
    List<Object>[]		data;
    BufferedImageContainer	cropped;

    result  = new ArrayList<List<Object>>();
    indices = getClassificationIndices(img);
    for (Integer index: indices) {
      loc = getPixelLocation(img, index);
      
      // 1st: crop
      crop = new Crop();
      crop.setWidth(m_Width);
      crop.setHeight(m_Height);
      crop.setAnchor(m_Anchor);
      crop.setX((int) (loc.getX() + 1));
      crop.setY((int) (loc.getY() + 1));
      cropped = crop.transform(img)[0];
      crop.destroy();
      
      // 2nd: turn into pixels
      pixels = new Pixels();
      pixels.setPixelType(m_PixelType);
      pixels.setFields(new Field[]{
	  new Field(AddClassification.CLASSIFICATION + index, DataType.STRING)
      });
      data = pixels.postProcessRows(img, pixels.generateRows(cropped));
      pixels.destroy();
      if (data.length == 0)
	continue;
      result.addAll(Arrays.asList(data));
    }

    return result.toArray(new List[result.size()]);
  }
}
