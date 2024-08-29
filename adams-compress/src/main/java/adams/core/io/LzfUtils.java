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
 * LzfUtils.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.MessageCollection;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import com.ning.compress.lzf.LZFInputStream;
import com.ning.compress.lzf.LZFOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Helper class for LZF related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LzfUtils {

  /** the default extension. */
  public final static String EXTENSION = ".lzf";

  /** for logging errors. */
  protected static Logger LOGGER = LoggingHelper.getLogger(LzfUtils.class);

  /**
   * Decompresses the specified lzma archive to a file without the ".lzf"
   * extension.
   *
   * @param archiveFile	the gzip file to decompress
   * @param buffer	the buffer size to use
   * @return		the error message, null if everything OK
   */
  public static String decompress(File archiveFile, int buffer) {
    return decompress(archiveFile, buffer, new PlaceholderFile(archiveFile.getAbsolutePath().replaceAll("\\" + EXTENSION + "$", "")));
  }

  /**
   * Decompresses the specified lzma archive.
   *
   * @param archiveFile	the lzma file to decompress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file
   * @return		the error message, null if everything OK
   */
  public static String decompress(File archiveFile, int buffer, File outputFile) {
    String		result;
    LZFInputStream	in;
    OutputStream 	out;
    FileInputStream     fis;
    FileOutputStream	fos;
    String		msg;

    in     = null;
    fis    = null;
    out    = null;
    fos    = null;
    result = null;
    try {

      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      fis = new FileInputStream(archiveFile.getAbsolutePath());
      in  = new LZFInputStream(fis);
      fos = new FileOutputStream(outputFile.getAbsolutePath());
      out = new BufferedOutputStream(fos);

      IOUtils.copy(in, out, buffer);
    }
    catch (Exception e) {
      msg = "Failed to decompress '" + archiveFile + "': ";
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
   * Compresses the specified lzma archive to a file with the ".lzf"
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
   * Compresses the specified lzma archive. Does not remove the input file.
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
   * Compresses the specified lzma archive.
   *
   * @param inputFile	the file to compress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file (the archive)
   * @param removeInput	whether to remove the input file
   * @return		the error message, null if everything OK
   */
  public static String compress(File inputFile, int buffer, File outputFile, boolean removeInput) {
    String			result;
    LZFOutputStream 		out;
    BufferedInputStream 	in;
    FileInputStream		fis;
    FileOutputStream		fos;
    String			msg;

    in     = null;
    fis    = null;
    out    = null;
    fos    = null;
    result = null;
    try {
      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      fis = new FileInputStream(inputFile.getAbsolutePath());
      in  = new BufferedInputStream(fis);
      fos = new FileOutputStream(outputFile.getAbsolutePath());
      out = new LZFOutputStream(fos);
      
      IOUtils.copy(in, out, buffer);

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
   * Compresses the specified bytes using lzf.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input) {
    return compress(input, new MessageCollection());
  }

  /**
   * Compresses the specified bytes using lzf.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input, MessageCollection errors) {
    ByteArrayInputStream 	bis;
    ByteArrayOutputStream 	bos;
    LZFOutputStream 		cos;
    String			msg;

    bis = null;
    bos = null;
    cos = null;
    try {
      bis = new ByteArrayInputStream(input);
      bos = new ByteArrayOutputStream();
      cos = new LZFOutputStream(bos);
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
   * Decompresses the specified lzf compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer) {
    return decompress(input, buffer, new MessageCollection());
  }

  /**
   * Decompresses the specified lzf compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @param errors 	for collecting errors
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer, MessageCollection errors) {
    LZFInputStream 		cis;
    ByteArrayOutputStream	bos;
    String			msg;

    cis = null;
    bos = null;
    try {
      cis = new LZFInputStream(new ByteArrayInputStream(input));
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
}
