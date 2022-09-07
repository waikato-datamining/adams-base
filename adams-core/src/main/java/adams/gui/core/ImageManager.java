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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

  /** the property in the GUIHelper.props with the default images dir. */
  public static final String KEY_IMAGES_DIRECTORY = "ImagesDirectory";

  /** the property in the GUIHelper.props with the theme images dir. */
  public static final String KEY_THEME_IMAGES_DIRECTORY = "ThemeImagesDirectory";

  /** maps icon name to filename. */
  protected static Map<String,String> m_NameCache = new HashMap<>();

  /** maps filename to icon. */
  protected static Map<String,ImageIcon> m_IconCache = new HashMap<>();

  /** maps filename to buffered image. */
  protected static Map<String,BufferedImage> m_BufferedImageCache = new HashMap<>();

  /** the directories to look for files. */
  protected static List<String> m_ImageDirs;

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
   * Checks whether an image with the given name is present in the specified directory.
   *
   * @param dir		the directory to look for image
   * @param name	the name of the image to look for
   * @return		the full path of the image if present, otherwise null
   */
  public static String checkImageFilename(String dir, String name) {
    String	result;
    URL 	url;

    result = null;

    try {
      url = ClassLoader.getSystemClassLoader().getResource(dir + name);
      if (url != null)
	result = dir + name;
    }
    catch (Exception e) {
      // ignored
    }

    return result;
  }

  /**
   * Removes the gif/png/jpg extension from the name, if any.
   *
   * @param name	the name to process
   * @return		the processed name
   */
  protected static String removeExtension(String name) {
    int		pos;
    int		len;
    String	tmp;

    len = name.length();
    pos = name.lastIndexOf('.');

    // possible image extension?
    if (pos == len - 4) {
      tmp = name.toLowerCase();
      if (tmp.endsWith(".gif") || tmp.endsWith(".png") || tmp.endsWith(".jpg"))
	name = name.substring(0, len - 4);
    }

    return name;
  }

  /**
   * Initializes the directories to look for images.
   */
  protected static void initImageDirs() {
    String[]	dirs;
    int		i;

    m_ImageDirs = new ArrayList<>();

    // theme
    dirs = GUIHelper.getString(KEY_THEME_IMAGES_DIRECTORY, "").split(",");
    for (i = 0; i < dirs.length; i++) {
      if (!dirs[i].trim().isEmpty()) {
	if (!dirs[i].endsWith("/"))
	  dirs[i] += "/";
	m_ImageDirs.add(dirs[i]);
      }
    }

    // default
    dirs = GUIHelper.getString(KEY_IMAGES_DIRECTORY, DEFAULT_IMAGE_DIR).split(",");
    for (i = 0; i < dirs.length; i++) {
      if (!dirs[i].endsWith("/"))
	dirs[i] += "/";
      m_ImageDirs.add(dirs[i]);
    }
  }

  /**
   * Tries to find an image with the specified name in one of the defined
   * image directories. Automatically checks for .gif/.png/.jpg, therefore
   * does not require an extension in the name.
   *
   * @param name	the name of the image to add the path to
   * @return		the full path of the image
   */
  public static String getImageFilename(String name) {
    String	result;
    String[]	dirs;
    int		i;
    URL 	url;

    name = removeExtension(name);

    // cached?
    if (m_NameCache.containsKey(name))
      return m_NameCache.get(name);

    // determine dirs?
    synchronized (m_NameCache) {
      if (m_ImageDirs == null)
	initImageDirs();
    }

    // locate image
    result = null;
    for (i = 0; i < m_ImageDirs.size(); i++) {
      result = checkImageFilename(m_ImageDirs.get(i), name + ".gif");
      if (result == null)
	result = checkImageFilename(m_ImageDirs.get(i), name + ".png");
      if (result == null)
	result = checkImageFilename(m_ImageDirs.get(i), name + ".jpg");
      if (result != null) {
	m_NameCache.put(name, result);
	break;
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
    if (hasImageFile(cls.getName()))
      return getIcon(cls.getName());
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
    ImageIcon	result;
    String	filename;

    result   = null;
    filename = getImageFilename(name);

    if (filename != null) {
      if (m_IconCache.containsKey(filename))
	return m_IconCache.get(filename);
      result = new ImageIcon(ClassLoader.getSystemClassLoader().getResource(filename));
      m_IconCache.put(filename, result);
    }

    return result;
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
      if (m_IconCache.containsKey(filename))
	return m_IconCache.get(filename);
      result = new ImageIcon(ClassLoader.getSystemClassLoader().getResource(filename));
      m_IconCache.put(filename, result);
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
    BufferedImage	result;
    String		filename;

    result   = null;
    filename = getImageFilename(name);
    if (filename != null) {
      if (m_BufferedImageCache.containsKey(filename))
        return m_BufferedImageCache.get(filename);
      try {
	result = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(filename));
	m_BufferedImageCache.put(filename, result);
      }
      catch (Exception e) {
	System.err.println("Failed to load image: " + filename);
	e.printStackTrace();
      }
    }
    return result;
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
