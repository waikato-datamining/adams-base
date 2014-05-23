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
 * BufferedImageHelper.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import adams.core.Properties;
import adams.data.conversion.DOMToProperties;
import adams.data.report.Report;

/**
 * Helper class for BufferedImage objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageHelper {

  /**
   * Returns a copy of a BufferedImage object.
   * <p/>
   * Taken from
   * <a href="http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage/3514297#3514297" target="_blank">here</a>
   * (CC BY-SA 3.0).
   *
   * @param img		the image to copy
   */
  public static BufferedImage deepCopy(BufferedImage img) {
    ColorModel 		cm;
    boolean 		isAlphaPremultiplied;
    WritableRaster 	raster;

    cm = img.getColorModel();
    isAlphaPremultiplied = cm.isAlphaPremultiplied();
    raster = img.copyData(null);

    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  }

  /**
   * Returns all the pixels of the image as an int array (row-wise).
   *
   * @param img		the image to get the pixels from
   * @return		the pixel array
   * @see		BufferedImage#getRGB(int, int)
   */
  public static int[] getPixels(BufferedImage img) {
    int[]	result;
    int		y;
    int		x;
    int		i;

    result = new int[img.getWidth() * img.getHeight()];
    i      = 0;
    for (y = 0; y < img.getHeight(); y++) {
      for (x = 0; x < img.getWidth(); x++) {
	result[i] = img.getRGB(x, y);
	i++;
      }
    }

    return result;
  }

  /**
   * Splits the RGBA value into R,G,B,A.
   * 
   * @param pixel	the RGB value to split
   * @return		the array with R,G,B,A
   */
  public static int[] split(int pixel) {
    int[]	result;
    
    result    = new int[4];
    result[0] = (pixel >> 16) & 0xFF;  // R
    result[1] = (pixel >>  8) & 0xFF;  // G
    result[2] = (pixel >>  0) & 0xFF;  // B
    result[3] = (pixel >> 24) & 0xFF;  // A
    
    return result;
  }

  /**
   * Combines the R,G,B,A values back into single integer.
   * 
   * @param rgba	the RGBA values
   * @return		the combined integer
   */
  public static int combine(int[] rgba) {
    return combine(rgba[0], rgba[1], rgba[2], rgba[3]);
  }

  /**
   * Combines the R,G,B,A values back into single integer.
   * 
   * @param r		the red value
   * @param g		the green value
   * @param b		the blue value
   * @param a		the alpha value
   * @return		the combined integer
   */
  public static int combine(int r, int g, int b, int a) {
    return (r << 16) + (g << 8) + (b << 0) + (a << 24);
  }
  
  /**
   * Returns all the pixels of the image as an int array (row-wise) with the 
   * RGB(A) components as second dimension.
   *
   * @param img		the image to get the pixels from
   * @return		the pixel array
   * @see		BufferedImage#getRGB(int, int)
   */
  public static int[][] getRGBPixels(BufferedImage img) {
    int[][]	result;
    int		y;
    int		x;
    int		i;
    int		pixel;

    result = new int[img.getWidth() * img.getHeight()][4];
    i      = 0;
    for (y = 0; y < img.getHeight(); y++) {
      for (x = 0; x < img.getWidth(); x++) {
	pixel = img.getRGB(x, y);
	result[i][0] = (pixel >> 16) & 0xFF;  // R
	result[i][1] = (pixel >>  8) & 0xFF;  // G
	result[i][2] = (pixel >>  0) & 0xFF;  // B
	result[i][3] = (pixel >> 24) & 0xFF;  // A
	i++;
      }
    }

    return result;
  }

  /**
   * Returns the pixels of the image as an 2-D int array (row-wise).
   *
   * @param img		the image to get the pixels from
   * @return		the pixel array
   * @see		BufferedImage#getRGB(int, int)
   */
  public static int[][] getPixelRaster(BufferedImage img) {
    int[][]	result;
    int		y;
    int		x;

    result = new int[img.getHeight()][img.getWidth()];
    for (y = 0; y < img.getHeight(); y++) {
      for (x = 0; x < img.getWidth(); x++)
	result[y][x] = img.getRGB(x, y);
    }

    return result;
  }
  
  /**
   * Performs flood fill on the image.
   * Based on the <a href="http://en.wikipedia.org/wiki/Flood_fill" target="_blank">2nd alternative implementation</a>.
   * 
   * @param img		the image to perform the floodfill on
   * @param startX	the starting point, x coordinate
   * @param startY	the starting point, y coordinate
   * @param targetColor	the target color (what we want to fill)
   * @param replacementColor	the replacement color (the color fill with)
   * @param extent	for recording the bounding box for the flood fill, all -1 if failed to fill
   * @return		true if successfully filled
   */
  public static boolean floodFill(BufferedImage img, int startX, int startY, Color targetColor, Color replacementColor) {
    return floodFill(img, startX, startY, targetColor.getRGB(), replacementColor.getRGB());
  }
  
  /**
   * Performs flood fill on the image.
   * Based on the <a href="http://en.wikipedia.org/wiki/Flood_fill" target="_blank">2nd alternative implementation</a>.
   * 
   * @param img		the image to perform the floodfill on
   * @param startX	the starting point, x coordinate
   * @param startY	the starting point, y coordinate
   * @param targetColor	the target color (what we want to fill)
   * @param replacementColor	the replacement color (the color fill with)
   * @param extent	for recording the bounding box for the flood fill, all -1 if failed to fill
   * @return		true if successfully filled
   */
  public static boolean floodFill(BufferedImage img, int startX, int startY, int targetColor, int replacementColor) {
    return floodFill(img, startX, startY, targetColor, replacementColor, new int[4]);
  }
  
  /**
   * Performs flood fill on the image. Records the extent of the fill 
   * (bounding box). 
   * Based on the <a href="http://en.wikipedia.org/wiki/Flood_fill" target="_blank">2nd alternative implementation</a>.
   * 
   * @param img		the image to perform the floodfill on
   * @param startX	the starting point, x coordinate
   * @param startY	the starting point, y coordinate
   * @param targetColor	the target color (what we want to fill)
   * @param replacementColor	the replacement color (the color fill with)
   * @param extent	for recording the bounding box for the flood fill, all -1 if failed to fill
   * @return		true if successfully filled
   */
  public static boolean floodFill(BufferedImage img, int startX, int startY, Color targetColor, Color replacementColor, int[] extent) {
    return floodFill(img, startX, startY, targetColor.getRGB(), replacementColor.getRGB(), extent);
  }
  
  /**
   * Performs flood fill on the image. Records the extent of the fill 
   * (bounding box). 
   * Based on the <a href="http://en.wikipedia.org/wiki/Flood_fill" target="_blank">2nd alternative implementation</a>.
   * 
   * @param img		the image to perform the floodfill on
   * @param startX	the starting point, x coordinate
   * @param startY	the starting point, y coordinate
   * @param targetColor	the target color (what we want to fill)
   * @param replacementColor	the replacement color (the color fill with)
   * @param extent	for recording the bounding box for the flood fill, all -1 if failed to fill
   * @return		true if successfully filled
   */
  public static boolean floodFill(BufferedImage img, int startX, int startY, int targetColor, int replacementColor, int[] extent) {
    LinkedList<int[]>	queue;
    LinkedList<int[]>	queueNew;
    int			west;
    int			east;
    int			width;
    int			height;
    int			i;
    
    // can't fill?
    if (img.getRGB(startX, startY) != targetColor) {
      extent[0] = -1;
      extent[1] = -1;
      extent[2] = -1;
      extent[3] = -1;
      return false;
    }

    // init extent
    extent[0] = startX;
    extent[1] = startY;
    extent[2] = startX;
    extent[3] = startY;
    
    // init queue
    queue = new LinkedList<int[]>();
    queue.add(new int[]{startX, startY});

    width  = img.getWidth();
    height = img.getHeight();
    while (!queue.isEmpty()) {
      queueNew = new LinkedList<int[]>();
      for (int[] pos: queue) {
	if (img.getRGB(pos[0], pos[1]) == targetColor) {
	  west = pos[0];
	  east = west;
	  
	  // go west
	  while ((west > 0) && img.getRGB(west - 1, pos[1]) == targetColor)
	    west--;
	  // go east
	  while ((east < width - 1) && img.getRGB(east + 1, pos[1]) == targetColor)
	    east++;
	  
	  // check pixels (north/south)
	  for (i = west; i <= east; i++) {
	    img.setRGB(i, pos[1], replacementColor);
	    // north?
	    if ((pos[1] > 0) && (img.getRGB(i, pos[1] - 1) == targetColor))
	      queueNew.add(new int[]{i, pos[1] - 1});
	    // south?
	    if ((pos[1] < height - 1) && (img.getRGB(i, pos[1] + 1) == targetColor))
	      queueNew.add(new int[]{i, pos[1] + 1});
	  }
	  
	  // update bounding box
	  if (extent[0] > west)
	    extent[0] = west;
	  if (extent[2] < east)
	    extent[2] = east;
	  if (extent[1] > pos[1])
	    extent[1] = pos[1];
	  if (extent[3] < pos[1])
	    extent[3] = pos[1];
	}
      }
      queue = queueNew;
    }
    
    return true;
  }
  
  /**
   * Reads an image, also fills in meta-data.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  public static BufferedImageContainer read(File file) {
    BufferedImageContainer	result;
    ImageInputStream 		iis;
    Iterator 			it;
    ImageReader 		reader;
    IIOMetadata 		meta;
    BufferedImage 		image;
    String[]			formats;
    DOMToProperties		convert;
    Properties			props;
    
    try {
      result = new BufferedImageContainer();
      iis    = ImageIO.createImageInputStream(new FileInputStream(file.getAbsoluteFile()));
      it     = ImageIO.getImageReaders(iis);
      if (!it.hasNext()) {
	System.err.println("No reader for this format: " + file);
	return null;
      }
      reader = (ImageReader) it.next();
      reader.setInput(iis);
      
      // meta
      meta = reader.getImageMetadata(0);
      convert = new DOMToProperties();
      convert.setStoreAttributes(true);
      convert.setSkipRoot(true);
      formats = meta.getMetadataFormatNames();
      props   = new Properties();
      for (String format: formats) {
	convert.setInput(meta.getAsTree(format));
	if (convert.convert() == null)
	  props.add((Properties) convert.getOutput());
      }
      convert.cleanUp();
      result.setReport(Report.parseProperties(props));

      // image
      image = reader.read(0);
      result.setImage(image);

      return result;
    }
    catch (Exception e) {
      System.err.println("Failed to read image: " + file);
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Converts the image, if necessary to the specified type.
   * 
   * @param img		the image to convert
   * @param type	the required type
   * @return		the (potentially) converted image
   */
  public static BufferedImage convert(BufferedImage img, int type) {
    BufferedImage	result;
    Graphics2D		g2d;
    
    if (img.getType() != type) {
      result = new BufferedImage(img.getWidth(), img.getHeight(), type);
      g2d   = result.createGraphics();
      g2d.drawImage(img, 0, 0, null);
      g2d.dispose();
    }
    else {
      result = img;
    }
    
    return result;
  }
  
  /**
   * Generates a histogram for each of the R, G and B channels.
   * 
   * @param img		the image to analyze
   * @return		the histogram, 0 = R, 1 = G, 2 = B, 3 = A
   */
  public static int[][] histogram(BufferedImage img) {
    int[][]	result;
    int		width;
    int		height;
    int		x;
    int		y;
    int[]	split;
    int		i;
    
    result = new int[4][256];
    img    = convert(img, BufferedImage.TYPE_4BYTE_ABGR);
    height = img.getHeight();
    width  = img.getWidth();
    
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	split = split(img.getRGB(x, y));
	for (i = 0; i < split.length; i++)
	  result[i][split[i]]++;
      }
    }
    
    
    return result;
  }
}
