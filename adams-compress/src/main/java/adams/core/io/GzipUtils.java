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
 * GzipUtils.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache compress commons
 */
package adams.core.io;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class for gzip related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GzipUtils {

  /** the default extension. */
  public final static String EXTENSION = ".gz";

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
      System.err.println(msg);
      e.printStackTrace();
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
      System.err.println(msg);
      e.printStackTrace();
      result = msg + e;
    }
    finally {
      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
