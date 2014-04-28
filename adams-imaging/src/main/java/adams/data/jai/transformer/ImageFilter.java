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
 * ImageFilter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.imagefilter.AbstractImageFilterProvider;
import adams.data.imagefilter.GrayFilterProvider;

/**
 <!-- globalinfo-start -->
 * Applies an ImageFilter to the buffered image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-filter &lt;adams.data.imagefilter.AbstractImageFilterProvider&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The provider of the image filter to apply to the image.
 * &nbsp;&nbsp;&nbsp;default: adams.data.imagefilter.GrayFilterProvider
 * </pre>
 * 
 * <pre>-copy &lt;boolean&gt; (property: copy)
 * &nbsp;&nbsp;&nbsp;If enabled, a copy of the image is created first before applying the filter.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageFilter
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;
  
  /** the filter to apply. */
  protected AbstractImageFilterProvider m_Filter;

  /** whether to create a copy of the image first. */
  protected boolean m_Copy;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies an ImageFilter to the buffered image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new GrayFilterProvider());

    m_OptionManager.add(
	    "copy", "copy",
	    false);
  }

  /**
   * Sets the filter.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractImageFilterProvider value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter.
   *
   * @return		the filter
   */
  public AbstractImageFilterProvider getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String filterTipText() {
    return "The provider of the image filter to apply to the image.";
  }

  /**
   * Sets whether to create a copy of the image first.
   *
   * @param value	true if to create copy
   */
  public void setCopy(boolean value) {
    m_Copy = value;
    reset();
  }

  /**
   * Returns whether a copy is created first before applying the filter.
   *
   * @return		true if a copy is created first
   */
  public boolean getCopy() {
    return m_Copy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String copyTipText() {
    return "If enabled, a copy of the image is created first before applying the filter.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "filter", m_Filter, "filter: ");
    result += QuickInfoHelper.toString(this, "copy", m_Copy, "copy", ", ");
    
    return result;
  }

  /**
   * Generates the subimages.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the subimages
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer		result;
    BufferedImage			im;
    java.awt.image.ImageFilter		filter;
    ImageProducer 			ip;

    im = img.toBufferedImage();
    if (m_Copy)
      im = BufferedImageHelper.deepCopy(im);
    filter = m_Filter.generate(im);
    ip     = new FilteredImageSource(im.getSource(), filter);  
    result = (BufferedImageContainer) img.getHeader();
    result.setImage(AbstractImageFilterProvider.imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(ip), BufferedImage.TYPE_INT_ARGB));

    return new BufferedImageContainer[]{result};
  }
}
