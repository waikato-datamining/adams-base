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
 * SizeFilter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.locateobjects;

import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Allows filtering the located objects based on the min&#47;max width&#47;height.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-locator &lt;adams.flow.transformer.locateobjects.AbstractObjectLocator&gt; (property: locator)
 * &nbsp;&nbsp;&nbsp;The base locator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.locateobjects.PassThrough
 * </pre>
 *
 * <pre>-min-width &lt;int&gt; (property: minWidth)
 * &nbsp;&nbsp;&nbsp;The minimum width; ignored if &lt;= 0.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-max-width &lt;int&gt; (property: maxWidth)
 * &nbsp;&nbsp;&nbsp;The maximum width; ignored if &lt;= 0.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-min-height &lt;int&gt; (property: minHeight)
 * &nbsp;&nbsp;&nbsp;The minimum height; ignored if &lt;= 0.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-max-height &lt;int&gt; (property: maxHeight)
 * &nbsp;&nbsp;&nbsp;The maximum height; ignored if &lt;= 0.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SizeFilter
  extends AbstractMetaObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 7548064590836834398L;

  /** the minimum width. */
  protected int m_MinWidth;

  /** the maximum width. */
  protected int m_MaxWidth;

  /** the minimum height. */
  protected int m_MinHeight;

  /** the maximum height. */
  protected int m_MaxHeight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows filtering the located objects based on the min/max width/height.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-width", "minWidth",
	    -1, -1, null);

    m_OptionManager.add(
	    "max-width", "maxWidth",
	    -1, -1, null);

    m_OptionManager.add(
	    "min-height", "minHeight",
	    -1, -1, null);

    m_OptionManager.add(
	    "max-height", "maxHeight",
	    -1, -1, null);
  }

  /**
   * Sets the minimum width.
   *
   * @param value	the width
   */
  public void setMinWidth(int value) {
    if (value >= -1) {
      m_MinWidth = value;
      reset();
    }
    else {
      getLogger().warning("Minimum width must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the minimum width.
   *
   * @return		the width
   */
  public int getMinWidth() {
    return m_MinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minWidthTipText() {
    return "The minimum width; ignored if <= 0.";
  }

  /**
   * Sets the maximum width.
   *
   * @param value	the width
   */
  public void setMaxWidth(int value) {
    if (value >= -1) {
      m_MaxWidth = value;
      reset();
    }
    else {
      getLogger().warning("Maximum width must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the maximum width.
   *
   * @return		the width
   */
  public int getMaxWidth() {
    return m_MaxWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxWidthTipText() {
    return "The maximum width; ignored if <= 0.";
  }

  /**
   * Sets the minimum height.
   *
   * @param value	the height
   */
  public void setMinHeight(int value) {
    if (value >= -1) {
      m_MinHeight = value;
      reset();
    }
    else {
      getLogger().warning("Minimum height must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the minimum height.
   *
   * @return		the height
   */
  public int getMinHeight() {
    return m_MinHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minHeightTipText() {
    return "The minimum height; ignored if <= 0.";
  }

  /**
   * Sets the maximum height.
   *
   * @param value	the height
   */
  public void setMaxHeight(int value) {
    if (value >= -1) {
      m_MaxHeight = value;
      reset();
    }
    else {
      getLogger().warning("Maximum height must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the maximum height.
   *
   * @return		the height
   */
  public int getMaxHeight() {
    return m_MaxHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxHeightTipText() {
    return "The maximum height; ignored if <= 0.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "minWidth", (m_MinWidth < 1 ? "NA" : "" + m_MinWidth), "minW: ");
    result += QuickInfoHelper.toString(this, "maxWidth", (m_MaxWidth < 1 ? "NA" : "" + m_MaxWidth), ", maxW: ");
    result += QuickInfoHelper.toString(this, "minHeight", (m_MinHeight < 1 ? "NA" : "" + m_MinHeight), "minH: ");
    result += QuickInfoHelper.toString(this, "maxHeight", (m_MaxHeight < 1 ? "NA" : "" + m_MaxHeight), ", maxH: ");
    
    return result;
  }

  /**
   * Performs the actual locating of the objects.
   *
   * @param imp	        the image to process
   * @return		the containers of located objects
   */
  @Override
  protected LocatedObjects doLocate(BufferedImage image) {
    LocatedObjects	result;
    LocatedObjects	base;

    result = new LocatedObjects();
    base   = m_Locator.locate(image);

    for (LocatedObject obj: base) {
      if (m_MinWidth > 0) {
	if (obj.getWidth() < m_MinWidth)
	  continue;
      }
      if (m_MaxWidth > 0) {
	if (obj.getWidth() > m_MaxWidth)
	  continue;
      }
      if (m_MinHeight > 0) {
	if (obj.getHeight() < m_MinHeight)
	  continue;
      }
      if (m_MaxHeight > 0) {
	if (obj.getHeight() > m_MaxHeight)
	  continue;
      }
      result.add(obj);
    }

    return result;
  }
}
