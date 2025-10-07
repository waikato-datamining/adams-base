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
 * ImageOverlayPaintlet.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.gui.core.ImageManager;
import adams.gui.event.PaintEvent.PaintMoment;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Paints the image at the specified location.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageOverlayPaintlet
  extends AbstractPaintlet {

  private static final long serialVersionUID = 7923819857566247771L;

  /** where to find the image. */
  public enum Source {
    FILE,
    RESOURCE,
  }

  /** where to place the image. */
  public enum Location {
    ABSOLUTE,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
  }

  /** the source of the image. */
  protected Source m_Source;

  /** the image file. */
  protected PlaceholderFile m_ImageFile;

  /** the image resource. */
  protected String m_ImageResource;

  /** the location. */
  protected Location m_Location;

  /** the X position. */
  protected int m_X;

  /** the Y position. */
  protected int m_Y;

  /** the scale to use (1 = 100%). */
  protected double m_Scale;

  /** the image to paint. */
  protected transient Image m_Image;

  /** whether the image was initialized. */
  protected transient boolean m_Initialized;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints the image at the specified location.";
  }
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      Source.FILE);

    m_OptionManager.add(
      "image-file", "imageFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "image-resource", "imageResource",
      "");

    m_OptionManager.add(
      "location", "location",
      Location.TOP_RIGHT);

    m_OptionManager.add(
      "x", "X",
      1, 1, null);

    m_OptionManager.add(
      "y", "Y",
      1, 1, null);

    m_OptionManager.add(
      "scale", "scale",
      1.0, 0.001, null);
  }

  /**
   * Executes a repaints only if the changes to members are not ignored.
   *
   * @see		#getRepaintOnChange()
   * @see		#isInitializing()
   * @see		#repaint()
   */
  @Override
  public void memberChanged() {
    super.memberChanged();

    m_Image       = null;
    m_Initialized = false;
  }

  /**
   * Sets the source of the image.
   *
   * @param value	the source
   */
  public void setSource(Source value) {
    m_Source = value;
    memberChanged();
  }

  /**
   * Returns the source of the image.
   *
   * @return		the source
   */
  public Source getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "Where to obtain the image from, either from a file or from a resource (ie from the classpath).";
  }

  /**
   * Sets the image to load, ignored if pointing to a directory.
   *
   * @param value	the file
   */
  public void setImageFile(PlaceholderFile value) {
    m_ImageFile = value;
    memberChanged();
  }

  /**
   * Returns the image to load, ignored if pointing to a directory.
   *
   * @return		the file
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
    return "The image to load, ignored if pointing to a directory.";
  }

  /**
   * Sets the classpath resource to load the image from, ignored if empty.
   *
   * @param value	the resource path
   */
  public void setImageResource(String value) {
    m_ImageResource = value;
    memberChanged();
  }

  /**
   * Returns the classpath resource to load the image from, ignored if empty.
   *
   * @return		the resource path
   */
  public String getImageResource() {
    return m_ImageResource;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageResourceTipText() {
    return "The classpath resource to load the image from, ignored if empty.";
  }

  /**
   * Sets the location of the image.
   *
   * @param value	the location
   */
  public void setLocation(Location value) {
    m_Location = value;
    memberChanged();
  }

  /**
   * Returns the location of the image.
   *
   * @return		the location
   */
  public Location getLocation() {
    return m_Location;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationTipText() {
    return "Where to place the image.";
  }

  /**
   * Sets the X position in pixels.
   *
   * @param value	the position
   */
  public void setX(int value) {
    m_X = value;
    memberChanged();
  }

  /**
   * Returns the X position in pixels.
   *
   * @return		the position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position in pixels.";
  }

  /**
   * Sets the Y position in pixels.
   *
   * @param value	the position
   */
  public void setY(int value) {
    m_Y = value;
    memberChanged();
  }

  /**
   * Returns the Y position in pixels.
   *
   * @return		the position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position in pixels.";
  }

  /**
   * Sets the scale factor to apply.
   *
   * @param value	the scale factor
   */
  public void setScale(double value) {
    m_Scale = value;
    memberChanged();
  }

  /**
   * Returns the scale factor to apply.
   *
   * @return		the scale factor
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
    return "The scale factor to apply to the image (1 = 100%).";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  /**
   * Loads the image if necessary.
   */
  protected void initializeImage() {
    BufferedImageContainer	cont;

    // load image if necessary
    if (!m_Initialized) {
      switch (m_Source) {
	case FILE:
	  if (m_ImageFile.exists() && !m_ImageFile.isDirectory()) {
	    cont = BufferedImageHelper.read(m_ImageFile);
	    if (cont != null)
	      m_Image = cont.toBufferedImage();
	  }
	  break;

	case RESOURCE:
	  m_Image = ImageManager.getExternalImage(m_ImageResource);
	  break;

	default:
	  throw new IllegalStateException("Unhandled source: " + m_Source);
      }

      if ((m_Image != null) && (m_Scale != 1.0)) {
	m_Image = m_Image.getScaledInstance(
	  (int) (m_Image.getWidth(null) * m_Scale),
	  (int) (m_Image.getHeight(null) * m_Scale),
	  Image.SCALE_SMOOTH);
      }

      m_Initialized = true;
    }
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    int		x;
    int		y;
    JPanel	panel;

    initializeImage();
    if (m_Image == null)
      return;

    panel = getPanel().getPlot().getContent();

    switch (m_Location) {
      case ABSOLUTE:
	x = m_X - 1;
	y = m_Y - 1;
	break;

      case TOP_LEFT:
	x = 0;
	y = 0;
	break;

      case TOP_RIGHT:
	x = panel.getWidth() - m_Image.getWidth(null);
	y = 0;
	break;

      case BOTTOM_LEFT:
	x = 0;
	y = panel.getHeight() - m_Image.getHeight(null);
	break;

      case BOTTOM_RIGHT:
	x = panel.getWidth()  - m_Image.getWidth(null);
	y = panel.getHeight() - m_Image.getHeight(null);
	break;

      default:
	throw new IllegalStateException("Unhandled location: " + m_Location);
    }

    g.drawImage(m_Image, x, y, null);
  }
}
