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
 * XzUtils.java
 * Copyright (C) 2018-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.MessageCollection;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import org.apache.commons.compress.utils.IOUtils;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

/**
 * Helper class for xz related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class XzUtils {

  /** the default extension. */
  public final static String EXTENSION = ".xz";

  /** for logging errors. */
  protected static Logger LOGGER = LoggingHelper.getLogger(XzUtils.class);

  /**
   * Decompresses the specified archive to a file without the ".xz"
   * extension.
   *
   * @param archiveFile	the archive file to decompress
   * @param buffer	the buffer size to use
   * @return		the error message, null if everything OK
   */
  public static String decompress(File archiveFile, int buffer) {
    return decompress(archiveFile, buffer, new PlaceholderFile(archiveFile.getAbsolutePath().replaceAll("\\" + EXTENSION + "$", "")));
  }

  /**
   * Decompresses the specified archive.
   *
   * @param archiveFile	the archive file to decompress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file
   * @return		the error message, null if everything OK
   */
  public static String decompress(File archiveFile, int buffer, File outputFile) {
    String			result;
    byte[] 			buf;
    int 			len;
    FileInputStream             fis;
    XZInputStream 		in;
    BufferedOutputStream 	out;
    String			msg;

    in     = null;
    out    = null;
    fis    = null;
    result = null;
    try {
      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      // create GZIP file
      buf = new byte[buffer];
      fis = new FileInputStream(archiveFile.getAbsolutePath());
      in  = new XZInputStream(new BufferedInputStream(fis));
      out = new BufferedOutputStream(new FileOutputStream(outputFile), buffer);

      // Transfer bytes from the file to the GZIP file
      while ((len = in.read(buf)) > 0)
	out.write(buf, 0, len);
    }
    catch (Exception e) {
      msg = "Failed to decompress '" + archiveFile + "': ";
      LOGGER.log(Level.SEVERE, msg, e);
      result = msg + e;
    }
    finally {
      FileUtils.closeQuietly(fis);
      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(out);
    }

    return result;
  }

  /**
   * Compresses the specified file to a file with the ".xz"
   * extension.
   *
   * @param inputFile	the file to compress
   * @param buffer	the buffer size to use
   * @return		the error message, null if everything OK
   */
  public static String compress(File inputFile, int buffer) {
    return compress(inputFile, buffer, new PlaceholderFile(inputFile.getAbsolutePath() + EXTENSION));
  }

  /**
   * Compresses the specified file. Does not remove the input file.
   *
   * @param inputFile	the file to compress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file (the archive)
   * @return		the error message, null if everything OK
   */
  public static String compress(File inputFile, int buffer, File outputFile) {
    return compress(inputFile, buffer, outputFile, false);
  }

  /**
   * Compresses the specified file.
   *
   * @param inputFile	the file to compress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file (the archive)
   * @param removeInput	whether to remove the input file
   * @return		the error message, null if everything OK
   */
  public static String compress(File inputFile, int buffer, File outputFile, boolean removeInput) {
    String			result;
    byte[] 			buf;
    int 			len;
    XZOutputStream		out;
    BufferedInputStream 	in;
    String			msg;
    FileInputStream             fis;
    FileOutputStream		fos;

    in     = null;
    fis    = null;
    out    = null;
    fos    = null;
    result = null;
    try {
      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      // create GZIP file
      buf = new byte[buffer];
      fos = new FileOutputStream(outputFile);
      out = new XZOutputStream(fos, new LZMA2Options());
      fis = new FileInputStream(inputFile.getAbsolutePath());
      in  = new BufferedInputStream(fis);

      // Transfer bytes from the file to the GZIP file
      while ((len = in.read(buf)) > 0)
	out.write(buf, 0, len);

      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(fis);
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
      in  = null;
      fis = null;
      out = null;
      fos = null;

      // remove input file?
      if (removeInput) {
	if (!inputFile.delete())
	  result = "Failed to delete input file '" + inputFile + "' after successful compression!";
      }
    }
    catch (Exception e) {
      msg = "Failed to compress '" + inputFile + "': ";
      LOGGER.log(Level.SEVERE, msg, e);
      result = msg + e;
    }
    finally {
      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(fis);
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

  /**
   * Compresses the specified bytes using xz.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input) {
    return compress(input, new MessageCollection());
  }

  /**
   * Compresses the specified bytes using xz.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input, MessageCollection errors) {
    ByteArrayInputStream 	bis;
    ByteArrayOutputStream 	bos;
    XZOutputStream 		cos;
    String			msg;

    bis = null;
    bos = null;
    cos = null;
    try {
      bis = new ByteArrayInputStream(input);
      bos = new ByteArrayOutputStream();
      cos = new XZOutputStream(bos, new LZMA2Options());
      IOUtils.copy(bis, cos);
      FileUtils.closeQuietly(cos);
      return bos.toByteArray();
    }
    catch (Exception e) {
      msg = "Failed to compress bytes!";
      LOGGER.log(Level.SEVERE, msg, e);
      errors.add(msg, e);
      return null;
    }
    finally {
      FileUtils.closeQuietly(cos);
      FileUtils.closeQuietly(bos);
      FileUtils.closeQuietly(bis);
    }
  }

  /**
   * Decompresses the specified xz compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer) {
    return decompress(input, buffer, new MessageCollection());
  }

  /**
   * Decompresses the specified xz compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @param errors 	for collecting errors
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer, MessageCollection errors) {
    XZInputStream 		cis;
    ByteArrayOutputStream	bos;
    String			msg;

    cis = null;
    bos = null;
    try {
      cis = new XZInputStream(new ByteArrayInputStream(input));
      bos = new ByteArrayOutputStream();
      IOUtils.copy(cis, bos, buffer);
      return bos.toByteArray();
    }
    catch (Exception e) {
      msg = "Failed to decompress bytes!";
      LOGGER.log(Level.SEVERE, msg, e);
      errors.add(msg, e);
      return null;
    }
    finally {
      FileUtils.closeQuietly(cis);
      FileUtils.closeQuietly(bos);
    }
  }

  /**
   * Checks whether the file is xz compressed.
   * See: https://tukaani.org/xz/xz-file-format.txt
   *
   * @param file	the file to inspect
   * @return		true if gzip
   */
  public static boolean isXzCompressed(File file) {
    byte[]	data;

    data = FileUtils.loadFromBinaryFile(file, 6);
    if (data != null)
      return isXzCompressed(data);
    else
      return false;
  }

  /**
   * Checks whether the array is xz compressed.
   * See: https://tukaani.org/xz/xz-file-format.txt
   *
   * @param data  	the data to inspect
   * @return		true if gzip
   */
  public static boolean isXzCompressed(byte[] data) {
    if (data.length >= 6)
      return (data[0] == (byte) 0xFD)
	&& (data[1] == (byte) 0x37)
	&& (data[2] == (byte) 0x7A)
	&& (data[3] == (byte) 0x58)
	&& (data[4] == (byte) 0x5A)
	&& (data[5] == (byte) 0x00);
    else
      return false;
  }
}
