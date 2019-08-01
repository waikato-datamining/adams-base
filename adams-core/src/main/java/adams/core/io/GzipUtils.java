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
 * GzipUtils.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache compress commons
 */
package adams.core.io;

import adams.core.License;
import adams.core.MessageCollection;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Helper class for gzip related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GzipUtils {

  /** the default extension. */
  public final static String EXTENSION = ".gz";

  /** for logging errors. */
  protected static Logger LOGGER = LoggingHelper.getLogger(GzipUtils.class);

  /**
   * Decompresses the specified gzip archive to a file without the ".gz"
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
   * Decompresses the specified gzip archive.
   *
   * @param archiveFile	the gzip file to decompress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file
   * @return		the error message, null if everything OK
   */
  @MixedCopyright(
      copyright = "Apache compress commons",
      license = License.APACHE2,
      url = "http://commons.apache.org/compress/apidocs/org/apache/commons/compress/compressors/CompressorStreamFactory.html"
  )
  public static String decompress(File archiveFile, int buffer, File outputFile) {
    String			result;
    OutputStream 		out;
    InputStream			fis;
    CompressorInputStream 	in;
    String			msg;

    in     = null;
    fis    = null;
    out    = null;
    result = null;
    try {

      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      fis = new FileInputStream(archiveFile.getAbsolutePath());
      out = new FileOutputStream(outputFile.getAbsolutePath());
      in  = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP, fis);
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
    }

    return result;
  }

  /**
   * Compresses the specified gzip archive to a file with the ".gz"
   * extension.
   *
   * @param inputFile	the gzip file to compress
   * @param buffer	the buffer size to use
   * @return		the error message, null if everything OK
   */
  public static String compress(File inputFile, int buffer) {
    return compress(inputFile, buffer, new PlaceholderFile(inputFile.getAbsolutePath() + EXTENSION));
  }

  /**
   * Compresses the specified gzip archive. Does not remove the input file.
   *
   * @param inputFile	the gzip file to compress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file (the archive)
   * @return		the error message, null if everything OK
   */
  public static String compress(File inputFile, int buffer, File outputFile) {
    return compress(inputFile, buffer, outputFile, false);
  }

  /**
   * Compresses the specified gzip archive.
   *
   * @param inputFile	the gzip file to compress
   * @param buffer	the buffer size to use
   * @param outputFile	the destination file (the archive)
   * @param removeInput	whether to remove the input file
   * @return		the error message, null if everything OK
   */
  @MixedCopyright(
      copyright = "Apache compress commons",
      license = License.APACHE2,
      url = "http://commons.apache.org/compress/apidocs/org/apache/commons/compress/compressors/CompressorStreamFactory.html"
  )
  public static String compress(File inputFile, int buffer, File outputFile, boolean removeInput) {
    String			result;
    FileInputStream		in;
    FileOutputStream 		fos;
    CompressorOutputStream 	out;
    String			msg;

    in     = null;
    out    = null;
    fos    = null;
    result = null;
    try {
      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      in  = new FileInputStream(inputFile.getAbsolutePath()); 
      fos = new FileOutputStream(outputFile.getAbsolutePath());
      out = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);
      IOUtils.copy(in, out, buffer);

      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
      in  = null;
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
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

  /**
   * Compresses the specified bytes using gzip.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input) {
    return compress(input, new MessageCollection());
  }

  /**
   * Compresses the specified bytes using gzip.
   *
   * @param input	the bytes to compress
   * @return		the compressed bytes, null in case of error
   */
  public static byte[] compress(byte[] input, MessageCollection errors) {
    ByteArrayInputStream	bis;
    ByteArrayOutputStream	bos;
    GZIPOutputStream 		cos;
    String			msg;

    bis = null;
    bos = null;
    cos = null;
    try {
      bis = new ByteArrayInputStream(input);
      bos = new ByteArrayOutputStream();
      cos = new GZIPOutputStream(bos);
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
   * Decompresses the specified gzip compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @return		the decompressed bytes, null in case of error
   */
  public static byte[] decompress(byte[] input, int buffer) {
    return decompress(input, buffer, new MessageCollection());
  }

  /**
   * Decompresses the specified gzip compressed bytes.
   *
   * @param input	the compressed bytes
   * @param buffer	the buffer size to use
   * @param errors 	for collecting errors
   * @return		the decompressed bytes, null in case of error
   */
  @MixedCopyright(
      copyright = "Apache compress commons",
      license = License.APACHE2,
      url = "http://commons.apache.org/compress/apidocs/org/apache/commons/compress/compressors/CompressorStreamFactory.html"
  )
  public static byte[] decompress(byte[] input, int buffer, MessageCollection errors) {
    GZIPInputStream 		cis;
    ByteArrayOutputStream	bos;
    String			msg;

    cis = null;
    bos = null;
    try {
      cis = new GZIPInputStream(new ByteArrayInputStream(input));
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
