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
 * BufferedImageHelper.java
 * Copyright (C) 2011-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.data.Notes;
import adams.data.report.Report;
import adams.data.xml.DOMUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Helper class for BufferedImage objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BufferedImageHelper {

  /**
   * Returns a copy of a BufferedImage object.
   * <br><br>
   * Taken from
   * <a href="http://stackoverflow.com/a/19327237" target="_blank">here</a>
   * (CC BY-SA 3.0).
   * and from 
   * <a href="http://stackoverflow.com/a/3514297" target="_blank">here</a>
   * (CC BY-SA 3.0).
   *
   * @param source	the image to copy
   */
  public static BufferedImage deepCopy(BufferedImage source) {
    BufferedImage 	result;
    Graphics 		g;
    ColorModel 		cm;
    boolean 		isAlphaPremultiplied;
    WritableRaster 	raster;

    if (source.getType() == BufferedImage.TYPE_CUSTOM) {
      cm = source.getColorModel();
      isAlphaPremultiplied = cm.isAlphaPremultiplied();
      raster = source.copyData(null);
      result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    else {
      result = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
      g      = result.getGraphics();
      g.drawImage(source, 0, 0, null);
      g.dispose();
    }

    return result;
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
   * Returns all the pixels of the image as an int array (row-wise).
   *
   * @param img		the image to get the pixels from
   * @return		the pixel array
   * @see		BufferedImage#getRGB(int, int)
   */
  public static IntArrayMatrixView getPixelMatrix(BufferedImage img) {
    int[]	data;

    data = new int[img.getWidth() * img.getHeight()];
    img.getRGB(0, 0, img.getWidth(), img.getHeight(), data, 0, img.getWidth());

    return new IntArrayMatrixView(data, img.getWidth(), img.getHeight());
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
   * Returns the first image reader that handles the extension of the specified
   * file.
   *
   * @param file	the file to get the reader for
   * @return		the reader, null if none found
   */
  public static ImageReader getReaderForFile(File file) {
    ImageReader		result;
    String		suffix;

    suffix = FileUtils.getExtension(file);
    result = getReaderForExtension(suffix);

    return result;
  }

  /**
   * Returns the first image reader that handles the specified extension.
   *
   * @param ext		the extension to get the reader for
   * @return		the reader, null if none found
   */
  public static ImageReader getReaderForExtension(String ext) {
    ImageReader			result;
    Iterator<ImageReader>	iter;

    result = null;
    iter   = ImageIO.getImageReadersBySuffix(ext);
    if (iter.hasNext())
      result = iter.next();

    return result;
  }

  /**
   * Returns the first image writer that handles the extension of the specified
   * file.
   *
   * @param file	the file to get the writer for
   * @return		the writer, null if none found
   */
  public static ImageWriter getWriterForFile(File file) {
    ImageWriter			result;
    String			suffix;

    suffix = FileUtils.getExtension(file);
    result = getWriterForExtension(suffix);

    return result;
  }

  /**
   * Returns the first image writer that handles the extension of the specified
   * file.
   *
   * @param ext		the extension to get the writer for
   * @return		the writer, null if none found
   */
  public static ImageWriter getWriterForExtension(String ext) {
    ImageWriter			result;
    Iterator<ImageWriter>	iter;

    result = null;
    iter   = ImageIO.getImageWritersBySuffix(ext);
    if (iter.hasNext())
      result = iter.next();

    return result;
  }

  /**
   * Reads an image, also fills in meta-data.
   *
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  public static BufferedImageContainer read(File file) {
    return read(file, true);
  }

  /**
   * Reads an image, also fills in meta-data.
   *
   * @param file	the file to read
   * @param addMetaData whether to add the meta-data
   * @return		the image container, null if failed to read
   */
  public static BufferedImageContainer read(File file, boolean addMetaData) {
    BufferedImageContainer	result;
    FileInputStream		fis;
    ImageInputStream 		iis;
    Iterator 			it;
    ImageReader 		reader;
    IIOMetadata 		meta;
    BufferedImage 		image;
    String[]			formats;
    Properties			props;

    iis = null;
    fis = null;
    try {
      result = new BufferedImageContainer();
      fis    = new FileInputStream(file.getAbsoluteFile());
      iis    = ImageIO.createImageInputStream(fis);
      it     = ImageIO.getImageReaders(iis);
      if (!it.hasNext()) {
	System.err.println("No reader for this format: " + file);
	return null;
      }
      reader = (ImageReader) it.next();
      reader.setInput(iis);

      // meta
      if (addMetaData) {
	meta    = reader.getImageMetadata(0);
	formats = meta.getMetadataFormatNames();
	props   = new Properties();
	for (String format : formats)
	  props.add(DOMUtils.toProperties(".", false, true, true, meta.getAsTree(format)));
	result.setReport(Report.parseProperties(props));
      }

      // image
      image = reader.read(0);
      result.setImage(image);
      reader = null;

      return result;
    }
    catch (Exception e) {
      System.err.println("Failed to read image: " + file);
      e.printStackTrace();
      return null;
    }
    finally {
      if (iis != null) {
	try {
	  iis.close();
	  iis = null;
	}
	catch (Exception e) {
	  // ignored
	}
      }
      FileUtils.closeQuietly(fis);
    }
  }

  /**
   * Reads an image, also fills in meta-data.
   *
   * @param stream	the stream to read from
   * @param addMetaData whether to add the meta-data
   * @return		the image container, null if failed to read
   */
  public static BufferedImageContainer read(InputStream stream, boolean addMetaData) {
    BufferedImageContainer	result;
    ImageInputStream 		iis;
    Iterator 			it;
    ImageReader 		reader;
    IIOMetadata 		meta;
    BufferedImage 		image;
    String[]			formats;
    Properties			props;

    iis = null;
    try {
      result = new BufferedImageContainer();
      iis    = ImageIO.createImageInputStream(stream);
      it     = ImageIO.getImageReaders(iis);
      if (!it.hasNext()) {
	System.err.println("No reader for this format!");
	return null;
      }
      reader = (ImageReader) it.next();
      reader.setInput(iis);

      // meta
      if (addMetaData) {
	meta    = reader.getImageMetadata(0);
	formats = meta.getMetadataFormatNames();
	props   = new Properties();
	for (String format : formats)
	  props.add(DOMUtils.toProperties(".", false, true, true, meta.getAsTree(format)));
	result.setReport(Report.parseProperties(props));
      }

      // image
      image = reader.read(0);
      result.setImage(image);
      reader = null;

      return result;
    }
    catch (Exception e) {
      System.err.println("Failed to read image from stream!");
      e.printStackTrace();
      return null;
    }
    finally {
      if (iis != null) {
	try {
	  iis.close();
	  iis = null;
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
  }

  /**
   * Removes the alpha channel if present and turns it into RGB image.
   *
   * @param img		the image to convert
   * @return		the (potentially) converted image
   */
  public static BufferedImage removeAlphaChannel(BufferedImage img) {
    if (img.getColorModel().hasAlpha())
      return convert(img, BufferedImage.TYPE_INT_RGB);
    else
      return img;
  }

  /**
   * Writes the image to the specified file.
   * If the output file points to a JPG file (.jpg or .jpeg), then images
   * with alpha channel automatically get converted to RGB.
   *
   * @param img		the image to save
   * @param file	the file to write to
   * @return		null if successful, otherwise error message
   */
  public static String write(BufferedImage img, File file) {
    return write(img, null, file);
  }

  /**
   * Writes the image to the specified file. If format is null, uses the file extension as format.
   * If the format is JPG, then images with alpha channel automatically get converted to RGB.
   *
   * @param img		the image to save
   * @param format	the image format to use (eg jpg or png), if null or empty uses extension as format
   * @param file	the file to write to
   * @return		null if successful, otherwise error message
   */
  public static String write(BufferedImage img, String format, File file) {
    if ((format == null) || format.isEmpty())
      format = FileUtils.getExtension(file);

    // remove alpha channel?
    if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg"))
      img = removeAlphaChannel(img);

    try {
      if (!ImageIO.write(img, format, file.getAbsoluteFile()))
	return "Failed to write image to: " + file;
    }
    catch (Exception e) {
      return "Failed to write image to '" + file + "': " + LoggingHelper.throwableToString(e);
    }

    return null;
  }

  /**
   * Writes the image to the specified stream.
   *
   * @param img		the image to save
   * @param stream	the stream to write to
   * @return		null if successful, otherwise error message
   */
  public static String write(BufferedImage img, String format, OutputStream stream) {
    try {
      if (!ImageIO.write(img, format, stream))
	return "Failed to write image to stream as: " + format;
    }
    catch (Exception e) {
      return "Failed to write image to stream as: " + format + "\n" + LoggingHelper.throwableToString(e);
    }

    return null;
  }

  /**
   * Turns an image into a byte array.
   *
   * @param img		the image to convert
   * @param format	the format of the image, e.g., PNG or JPG
   * @return		the generated bytes, null if failed
   */
  public static byte[] toBytes(BufferedImage img, String format, MessageCollection errors) {
    byte[]			result;
    ByteArrayOutputStream	bytes;

    result = null;

    if (format.toLowerCase().equals("jpeg") || format.toLowerCase().equals("jpg"))
      img = removeAlphaChannel(img);

    bytes = new ByteArrayOutputStream();
    try {
      if (!ImageIO.write(img, format, bytes)) {
	errors.add("Failed to turn image into bytes of type " + format + "!");
      }
      else {
	result = bytes.toByteArray();
      }
    }
    catch (Exception e) {
      errors.add("Failed to turn image into bytes of type " + format + "!", e);
    }

    return result;
  }

  /**
   * Turns the image bytes (eg JPG or PNG) into a BufferedImage.
   *
   * @param bytes	the bytes to convert
   * @param errors	for collecting errors
   * @return		the read image, null if failed to read
   */
  public static BufferedImage fromBytes(byte[] bytes, MessageCollection errors) {
    BufferedImage		result;
    ByteArrayInputStream	stream;

    result = null;
    stream = new ByteArrayInputStream(bytes);
    try {
      result = ImageIO.read(stream);
    }
    catch (Exception e) {
      errors.add("Failed to read image from bytes array!", e);
    }

    return result;
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
   * @param gray	whether to use (A)RGB or grayscale
   * @return		the histogram, if ARGB then 0 = R, 1 = G, 2 = B, 3 = A
   * 			or 0 = gray in case of grayscale
   */
  public static int[][] histogram(BufferedImage img, boolean gray) {
    int[][]	result;
    int		width;
    int		height;
    int		x;
    int		y;
    int[]	split;
    int		i;

    if (gray) {
      result = new int[1][256];
      img    = convert(img, BufferedImage.TYPE_BYTE_GRAY);
      height = img.getHeight();
      width  = img.getWidth();
      split  = new int[1];

      for (y = 0; y < height; y++) {
	for (x = 0; x < width; x++) {
	  split[0] = (img.getRGB(x, y) >> 8) & 0xFF;
	  result[0][split[0]]++;
	}
      }
    }
    else {
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
    }

    return result;
  }

  /**
   * Returns the pixel depth in bits.
   *
   * @param img		the image to analyze
   * @return		the number of bits, -1 if unknown type
   */
  public static int getPixelDepth(BufferedImage img) {
    switch (img.getType()) {
      case BufferedImage.TYPE_3BYTE_BGR:
	return 24;
      case BufferedImage.TYPE_4BYTE_ABGR:
      case BufferedImage.TYPE_4BYTE_ABGR_PRE:
	return 32;
      case BufferedImage.TYPE_BYTE_BINARY:
      case BufferedImage.TYPE_BYTE_GRAY:
      case BufferedImage.TYPE_BYTE_INDEXED:
	return 8;
      case BufferedImage.TYPE_INT_ARGB:
      case BufferedImage.TYPE_INT_ARGB_PRE:
	return 32;
      case BufferedImage.TYPE_INT_BGR:
      case BufferedImage.TYPE_INT_RGB:
	return 24;
      case BufferedImage.TYPE_USHORT_555_RGB:
	return 15;
      case BufferedImage.TYPE_USHORT_565_RGB:
	return 16;
      case BufferedImage.TYPE_USHORT_GRAY:
	return 16;
      default:
	return -1;
    }
  }

  /**
   * Creates a {@link BufferedImageContainer} container if necessary, otherwise it just casts the object.
   *
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static BufferedImageContainer toBufferedImageContainer(AbstractImageContainer cont) {
    BufferedImageContainer	result;
    Report			report;
    Notes 			notes;

    if (cont instanceof BufferedImageContainer) {
      result = (BufferedImageContainer) cont;
    }
    else {
      report = cont.getReport().getClone();
      notes  = cont.getNotes().getClone();
      result = new BufferedImageContainer();
      result.setImage(cont.toBufferedImage());
      result.setReport(report);
      result.setNotes(notes);
    }
    return result;
  }

  /**
   * Creates a BufferedImage from the component.
   *
   * @param comp	the component to turn into an image
   * @param background 	the background
   * @return		the generated image
   */
  public static BufferedImage toBufferedImage(JComponent comp, Color background) {
    return toBufferedImage(comp, background, -1, -1);
  }

  /**
   * Creates a BufferedImage from the component.
   *
   * @param comp	the component to turn into an image
   * @param background 	the background
   * @param width 	the width to use, -1 to use component width
   * @param height 	the height to use, -1 to use component height
   * @return		the generated image
   */
  public static BufferedImage toBufferedImage(JComponent comp, Color background, int width, int height) {
    BufferedImage	result;
    Graphics2D		g;

    if (width == -1)
      width = comp.getWidth();
    if (height == -1)
      height = comp.getHeight();
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g      = result.createGraphics();
    g.setPaintMode();
    g.setColor(background);
    g.fillRect(0, 0, width, height);
    comp.printAll(g);
    g.dispose();
    return result;
  }
}
