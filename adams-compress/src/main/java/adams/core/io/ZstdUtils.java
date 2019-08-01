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
 * ZstdUtils.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.MessageCollection;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

/**
 * Helper class for zstd related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ZstdUtils {

  /** the default extension. */
  public final static String EXTENSION = ".zst";

  /** for logging errors. */
  protected static Logger LOGGER = LoggingHelper.getLogger(ZstdUtils.class);

  /**
   * Decompresses the specified archive to a file without the ".zst"
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
    ZstdInputStream 		in;
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
      in  = new ZstdInputStream(new BufferedInputStream(fis));
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
   * Compresses the specified file to a file with the ".zst"
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
    ZstdOutputStream 		out;
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

      // create zstd file
      buf = new byte[buffer];
      fos = new FileOutputStream(outputFile);
      out = new ZstdOutputStream(fos);
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
   * Compresses the specified bytes using zstd.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input) {
    return compress(input, new MessageCollection());
  }

  /**
   * Compresses the specified bytes using zstd.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input, MessageCollection errors) {
    ByteArrayInputStream 	bis;
    ByteArrayOutputStream 	bos;
    ZstdOutputStream 		cos;
    String			msg;

    bis = null;
    bos = null;
    cos = null;
    try {
      bis = new ByteArrayInputStream(input);
      bos = new ByteArrayOutputStream();
      cos = new ZstdOutputStream(bos);
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
   * Decompresses the specified zstd compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer) {
    return decompress(input, buffer, new MessageCollection());
  }

  /**
   * Decompresses the specified zstd compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @param errors 	for collecting errors
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer, MessageCollection errors) {
    ZstdInputStream 		cis;
    ByteArrayOutputStream	bos;
    String			msg;

    cis = null;
    bos = null;
    try {
      cis = new ZstdInputStream(new ByteArrayInputStream(input));
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
