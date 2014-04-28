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
 * BackgroundImagePaintlet.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import adams.core.io.PlaceholderFile;
import adams.gui.event.PaintEvent.PaintMoment;

/**
 * Paintlet for painting a background image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BackgroundImagePaintlet
  extends AbstractPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 7533482742917264993L;

  /** the image file to paint. */
  protected PlaceholderFile m_ImageFile;
  
  /** the image. */
  protected transient Image m_Image;
  
  /** whether to use fixed size or resize. */
  protected boolean m_UseFixedSize;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints an image located in a file as background.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "image-file", "imageFile",
	    new PlaceholderFile());

    m_OptionManager.add(
	    "use-fixed-size", "useFixedSize",
	    false);
  }

  /**
   * Sets the image file to load.
   *
   * @param value 	the file
   */
  public void setImageFile(PlaceholderFile value) {
    m_ImageFile = value;
    reset();
  }

  /**
   * Returns the image file to load.
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
    return "The image file to load and use as background.";
  }

  /**
   * Sets the whether to use a fixed size image or resize according to canvas.
   *
   * @param value 	true if to use fixed size
   */
  public void setUseFixedSize(boolean value) {
    m_UseFixedSize = value;
    reset();
  }

  /**
   * Returns the image file to load.
   *
   * @return 		the file
   */
  public boolean getUseFixedSize() {
    return m_UseFixedSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFixedSizeTipText() {
    return "Whether to use a fixed size image or to resize according to canvas.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.BACKGROUND;
  }
  
  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    if (m_Image == null) {
      if (m_ImageFile.isDirectory() || !m_ImageFile.exists())
	return;
      
      try {
	m_Image = ImageIO.read(new File(m_ImageFile.getAbsolutePath()));
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, 
	    "Failed to load background image from '" + m_ImageFile + "'!", e);
	return;
      }
    }
    
    if (m_UseFixedSize)
      g.drawImage(m_Image, 0, 0, null);
    else
      g.drawImage(m_Image, 0, 0, getPlot().getWidth(), getPlot().getHeight(), null);
  }
}
