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
 * Bzip2Utils.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Helper class for bzip2 related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Bzip2Utils {

  /** the default extension. */
  public final static String EXTENSION = ".bz2";

  /**
   * Decompresses the specified archive to a file without the ".bz2"
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
   * <p/>
   * See <a href="https://commons.apache.org/compress/examples.html" target="_blank">Apache commons/compress</a>.
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
    BZip2CompressorInputStream 	in;
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
      in  = new BZip2CompressorInputStream(new BufferedInputStream(fis));
      out = new BufferedOutputStream(new FileOutputStream(outputFile), buffer);

      // Transfer bytes from the file to the GZIP file
      while ((len = in.read(buf)) > 0)
	out.write(buf, 0, len);
    }
    catch (Exception e) {
      msg = "Failed to decompress '" + archiveFile + "': ";
      System.err.println(msg);
      e.printStackTrace();
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
   * Compresses the specified file to a file with the ".bz2"
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
   * <p/>
   * See <a href="https://commons.apache.org/compress/examples.html" target="_blank">Apache commons/compress</a>.
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
    BZip2CompressorOutputStream	out;
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
      out = new BZip2CompressorOutputStream(fos);
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
      System.err.println(msg);
      e.printStackTrace();
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
}
