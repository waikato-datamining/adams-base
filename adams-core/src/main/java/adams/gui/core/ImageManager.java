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
 * ImageManager.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages images and icons.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageManager {

  /** the empty icon name. */
  public final static String EMPTY_ICON = "empty.gif";

  /** the default path for images/icons. */
  public static final String DEFAULT_IMAGE_DIR = "adams/gui/images/";

  /** maps icon name to filename. */
  protected static Map<String,String> m_ImageCache = new HashMap<>();

  /**
   * Checks whether the image is available.
   *
   * @param name	the name of the image (filename without path but with
   * 			extension)
   * @return		true if image exists
   */
  public static boolean hasImageFile(String name) {
    return (getImageFilename(name) != null);
  }

  /**
   * Adds the path of the images directory to the name of the image.
   * Automatically tests for .gif and .png if the name does not have an
   * extension.
   *
   * @param name	the name of the image to add the path to
   * @return		the full path of the image
   */
  public static String getImageFilename(String name) {
    String	result;
    String[]	dirs;
    int		i;
    URL url;

    result = null;

    // cached?
    if (m_ImageCache.containsKey(name))
      return m_ImageCache.get(name);

    // no extension?
    if (!(name.toLowerCase().endsWith(".gif") || name.toLowerCase().endsWith(".png"))) {
      result = getImageFilename(name + ".gif");
      if (result == null)
	result = getImageFilename(name + ".png");
      if (result != null) {
        m_ImageCache.put(name, result);
        return result;
      }
    }

    dirs = GUIHelper.getString("ImagesDirectory", DEFAULT_IMAGE_DIR).split(",");
    for (i = 0; i < dirs.length; i++) {
      if (!dirs[i].endsWith("/"))
	dirs[i] += "/";
      try {
	url = ClassLoader.getSystemClassLoader().getResource(dirs[i] + name);
	if (url != null) {
	  result = dirs[i] + name;
          m_ImageCache.put(name, result);
	  break;
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
  }

  /**
   * Returns an ImageIcon for the given class.
   *
   * @param cls		the class to get the icon for (gif, png or jpg)
   * @return		the ImageIcon or null if none found
   */
  public static ImageIcon getIcon(Class cls) {
    if (hasImageFile(cls.getName() + ".gif"))
      return getIcon(cls.getName() + ".gif");
    else if (hasImageFile(cls.getName() + ".png"))
      return getIcon(cls.getName() + ".png");
    else if (hasImageFile(cls.getName() + ".jpg"))
      return getIcon(cls.getName() + ".jpg");
    else
      return null;
  }

  /**
   * Returns an ImageIcon from the given name.
   *
   * @param name	the filename without path
   * @return		the ImageIcon or null if not available
   */
  public static ImageIcon getIcon(String name) {
    String	filename;

    filename = getImageFilename(name);
    if (filename != null)
      return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(filename));
    else
      return null;
  }

  /**
   * Returns an ImageIcon from the given name.
   *
   * @param filename	the filename
   * @return		the ImageIcon or null if not available
   */
  public static ImageIcon getExternalIcon(String filename) {
    ImageIcon	result;

    try {
      result = new ImageIcon(ClassLoader.getSystemClassLoader().getResource(filename));
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Returns the ImageIcon for the empty icon.
   *
   * @return		the ImageIcon
   */
  public static ImageIcon getEmptyIcon() {
    return getIcon(EMPTY_ICON);
  }

  /**
   * Returns a BufferedImage from the given name.
   *
   * @param name	the filename without path
   * @return		the BufferedImage or null if not available/failed to load
   */
  public static BufferedImage getImage(String name) {
    String	filename;

    filename = getImageFilename(name);
    if (filename != null) {
      try {
	return ImageIO.read(ClassLoader.getSystemClassLoader().getResource(filename));
      }
      catch (Exception e) {
	System.err.println("Failed to load image: " + filename);
	e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Returns a BufferedImage from the given filename.
   *
   * @param filename	the full filename
   * @return		the BufferedImage or null if not available/failed to load
   */
  public static BufferedImage getExternalImage(String filename) {
    if (filename != null) {
      try {
	return ImageIO.read(ClassLoader.getSystemClassLoader().getResource(filename));
      }
      catch (Exception e) {
	System.err.println("Failed to load image: " + filename);
	e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Returns an ImageIcon of the logo (large image).
   *
   * @return		the logo or null if none available
   */
  public static ImageIcon getLogoImage() {
    ImageIcon	result;
    String	filename;

    result = null;

    filename = GUIHelper.getString("LogoImage", "");
    if (filename.length() != 0)
      result = getIcon(filename);

    return result;
  }

  /**
   * Returns an ImageIcon of the logo (icon sized image).
   *
   * @return		the logo or null if none available
   */
  public static ImageIcon getLogoIcon() {
    ImageIcon	result;
    String	filename;

    result = null;

    filename = GUIHelper.getString("LogoIcon", "");
    if (filename.length() != 0)
      result = getIcon(filename);

    return result;
  }
}
