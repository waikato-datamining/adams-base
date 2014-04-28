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

/**
 * ImageMapOverlay.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import adams.core.io.PlaceholderFile;
import adams.flow.sink.OpenStreetMapViewer;

/**
 <!-- globalinfo-start -->
 * Overlays the map with an icon obtained from a file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the overlay gets painted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the overlay (&gt;=0: absolute, -1: left, -2: center, -3: 
 * &nbsp;&nbsp;&nbsp;right).
 * &nbsp;&nbsp;&nbsp;default: -3
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the overlay (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-image-file &lt;adams.core.io.PlaceholderFile&gt; (property: imageFile)
 * &nbsp;&nbsp;&nbsp;The image file to overlay.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-scale &lt;double&gt; (property: scale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the image; 1.0 = actual size of image.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMapOverlay
  extends AbstractPositionableMapOverlay {

  /** for serialization. */
  private static final long serialVersionUID = 41349852232785589L;

  /** the image file to load. */
  protected PlaceholderFile m_ImageFile;
  
  /** the image to paint. */
  protected transient ImageIcon m_Image;
  
  /** the scaling factor. */
  protected double m_Scale;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays the map with an icon obtained from a file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "image-file", "imageFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "scale", "scale",
	    1.0, 0.0, null);
  }

  /**
   * Sets the image file to overlay.
   *
   * @param value 	the file
   */
  public void setImageFile(PlaceholderFile value) {
    m_ImageFile = value;
    reset();
  }

  /**
   * Returns the image file to overlay.
   *
   * @return 		the file
   */
  public PlaceholderFile getImageFile() {
    return m_ImageFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageFileTipText() {
    return "The image file to overlay.";
  }

  /**
   * Sets the scaling factor (1.0 = actual image size).
   *
   * @param value 	the scaling factor
   */
  public void setScale(double value) {
    m_Scale = value;
    reset();
  }

  /**
   * Returns the scaling factor (1.0 = actual image size).
   *
   * @return 		the scaling factor
   */
  public double getScale() {
    return m_Scale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleTipText() {
    return "The scaling factor for the image; 1.0 = actual size of image.";
  }
  
  /**
   * Returns the height of the image.
   *
   * @return 		the height
   */
  @Override
  protected int getHeight() {
    if (m_Image != null)
      return (int) (m_Image.getIconHeight() * m_Scale);
    else
      return 0;
  }
  
  /**
   * Returns the width of the image.
   *
   * @return 		the width
   */
  @Override
  protected int getWidth() {
    if (m_Image != null)
      return (int) (m_Image.getIconWidth() * m_Scale);
    else
      return 0;
  }

  /**
   * Gets executed before the actual painting.
   * <p/>
   * Loads the image.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   */
  @Override
  protected void prePaintOverlay(OpenStreetMapViewer viewer, Graphics g) {
    super.prePaintOverlay(viewer, g);
    
    if (m_Image == null) {
      if (!m_ImageFile.exists()) {
	getLogger().severe("Image file does not exist: " + m_ImageFile);
	return;
      }
      if (m_ImageFile.isDirectory()) {
	getLogger().severe("Image file points to a directory: " + m_ImageFile);
	return;
      }
      if (isLoggingEnabled())
	getLogger().fine("Loading: " + m_ImageFile);

      m_Image = new ImageIcon(m_ImageFile.getAbsolutePath());
    }
  }
  
  /**
   * Performs the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   * @param x		the actual x coordinate
   * @param y		the actual y coordinate
   */
  @Override
  protected void doPaintOverlay(OpenStreetMapViewer viewer, Graphics g, int x, int y) {
    g.drawImage(m_Image.getImage(), x, y - getHeight(), getWidth(), getHeight(), null);
  }
}
