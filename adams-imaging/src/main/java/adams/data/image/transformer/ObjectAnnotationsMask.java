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
 * ObjectAnnotationsMask.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Only leaves pixels in the image that are covered by the object annotations (= masking).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotationsMask
  extends AbstractBufferedImageTransformer {

  private static final long serialVersionUID = -7828174332731436229L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Only leaves pixels in the image that are covered by the object annotations (= masking).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
   *
   * @return		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "finder", m_Finder);
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    BufferedImage		imageOld;
    BufferedImage 		imageNew;
    BufferedImage		mask;
    LocatedObjects 		objects;
    Graphics2D 			g2d;
    int				x;
    int				y;
    int				m;

    imageOld = img.getContent();

    // create mask
    objects = m_Finder.findObjects(img.getReport());
    mask    = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    g2d     = mask.createGraphics();
    g2d.setColor(Color.BLACK);
    g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
    g2d.setColor(Color.WHITE);
    for (LocatedObject obj : objects) {
      if (obj.hasPolygon())
	g2d.fillPolygon(obj.getPolygon());
      else
	g2d.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
    }
    g2d.dispose();

    // apply mask
    imageNew = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (y = 0; y < mask.getHeight(); y++) {
      for (x = 0; x < mask.getWidth(); x++) {
	m = mask.getRGB(x, y) & 0xFFFFFF;
	if (m == 0)
	  imageNew.setRGB(x, y, 0);
	else
	  imageNew.setRGB(x, y, imageOld.getRGB(x, y));
      }
    }

    // assemble output
    result    = new BufferedImageContainer[1];
    result[0] = new BufferedImageContainer();
    result[0].setReport(img.getReport().getClone());
    result[0].setImage(imageNew);

    return result;
  }
}
