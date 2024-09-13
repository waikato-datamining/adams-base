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
 * LabeledRegions.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import adams.core.QuickInfoHelper;
import adams.core.base.LabeledRectangle;
import adams.data.image.BufferedImageContainer;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Extracts the sub-images according to the region definitions.<br>
 * Additional report values:<br>
 * - Region: for the region<br>
 * - Coordinates: for the 1-based coordinates (x,y,w,h)
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-partial &lt;boolean&gt; (property: partial)
 * &nbsp;&nbsp;&nbsp;If enabled, partial hits are included as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-fix-invalid &lt;boolean&gt; (property: fixInvalid)
 * &nbsp;&nbsp;&nbsp;If enabled, objects that fall partially outside the image boundaries get
 * &nbsp;&nbsp;&nbsp;fixed (eg when allowing partial hits).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-region &lt;adams.core.base.LabeledRectangle&gt; [-region ...] (property: regions)
 * &nbsp;&nbsp;&nbsp;The regions to extract (x y w h label, 0 or 1-based x&#47;y).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-one-based-coords &lt;boolean&gt; (property: oneBasedCoords)
 * &nbsp;&nbsp;&nbsp;If enabled, the coordinates are consisdered starting at 1 rather than 0.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The key for the label in the meta-data.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LabeledRegions
  extends AbstractSubImagesGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2488185528644078539L;

  /** the key for the region. */
  public final static String KEY_REGION = "Region";

  /** the key for the coordinates. */
  public final static String KEY_COORDINATES = "Coordinates";

  /** the regions to use. */
  protected LabeledRectangle[] m_Regions;

  /** whether the regions are 1-based or 0-based. */
  protected boolean m_OneBasedCoords;

  /** the meta-data key for the label. */
  protected String m_LabelKey;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Extracts the sub-images according to the region definitions.\n"
	+ "Additional report values:\n"
	+ "- " + KEY_REGION + ": for the region\n"
	+ "- " + KEY_COORDINATES + ": for the 1-based coordinates (x,y,w,h)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "region", "regions",
      new LabeledRectangle[0]);

    m_OptionManager.add(
      "one-based-coords", "oneBasedCoords",
      true);

    m_OptionManager.add(
      "label-key", "labelKey",
      "type");
  }

  /**
   * Sets the regions to extract (x y w h, 0 or 1-based x/y).
   *
   * @param value	the regions
   */
  public void setRegions(LabeledRectangle[] value) {
    m_Regions = value;
    reset();
  }

  /**
   * Returns the regions to extract (x y w h, 0 or 1-based x/y).
   *
   * @return		the regions
   */
  public LabeledRectangle[] getRegions() {
    return m_Regions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String regionsTipText() {
    return "The regions to extract (x y w h label, 0 or 1-based x/y).";
  }

  /**
   * Sets whether the coordinates start at 1 or 0.
   *
   * @param value	true if 1-based coordinates
   */
  public void setOneBasedCoords(boolean value) {
    m_OneBasedCoords = value;
    reset();
  }

  /**
   * Returns whether the coordinates start at 1 or 0.
   *
   * @return		true if 1-based coordinates
   */
  public boolean getOneBasedCoords() {
    return m_OneBasedCoords;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oneBasedCoordsTipText() {
    return "If enabled, the coordinates are consisdered starting at 1 rather than 0.";
  }

  /**
   * Sets the key for the label in the meta-data.
   *
   * @param value	the key
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the key for the label in the meta-data.
   *
   * @return		the key
   */
  public String getLabelKey() {
    return m_LabelKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelKeyTipText() {
    return "The key for the label in the meta-data.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "regions", m_Regions, ", regions: ");
    result += QuickInfoHelper.toString(this, "oneBasedCoords", m_OneBasedCoords, "1-based", ", ");
    result += QuickInfoHelper.toString(this, "labelKey", m_LabelKey, ", label key: ");

    return result;
  }

  /**
   * Performs the actual generation of the subimages.
   *
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  @Override
  protected List<BufferedImageContainer> doProcess(BufferedImageContainer image) {
    List<BufferedImageContainer>	result;
    BufferedImageContainer		cont;
    BufferedImage			bimage;
    int					i;
    Rectangle				rect;
    int					dec;
    int					x;
    int					y;
    int					width;
    int					height;
    String				label;

    result = new ArrayList<>();
    bimage = image.getImage();
    if (m_OneBasedCoords)
      dec = 1;
    else
      dec = 0;

    for (i = 0; i < m_Regions.length; i++) {
      rect   = m_Regions[i].rectangleValue();
      x      = rect.x - dec;
      y      = rect.y - dec;
      width  = rect.width;
      height = rect.height;
      label  = m_Regions[i].labelValue();

      if (isLoggingEnabled()) {
	getLogger().info(
	  "region=" + i + ", x=" + x + ", y=" + y
	    + ", width=" + width + ", height=" + height
	    + ", label=" + label);
      }

      if ((x + width - 1 < image.getWidth()) && (y + height - 1 < image.getHeight())) {
	cont = (BufferedImageContainer) image.getHeader();
	cont.setReport(transferObjects(cont.getReport(), x, y, width, height, label, m_LabelKey));
	cont.setImage(bimage.getSubimage(x, y, width, height));
	cont.getReport().setNumericValue(KEY_REGION, i);
	cont.getReport().setStringValue(KEY_COORDINATES, (x + 1) + "," + (y + 1) + "," + width + "," + height);
	result.add(cont);
      }
      else {
	getLogger().warning("Region exceeds image dimensions: region: x=" + x + ", y=" + y + ", w=" + width + ", h=" + height
			      + "; image: w=" + image.getWidth() + ", h=" + image.getHeight());
      }
    }

    return result;
  }
}
