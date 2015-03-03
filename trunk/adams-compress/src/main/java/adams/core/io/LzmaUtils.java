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
 * LzmaUtils.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Julien Ponge
 */
package adams.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import lzma.streams.LzmaOutputStream;

import org.apache.commons.compress.utils.IOUtils;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Helper class for lzma related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LzmaUtils {

  /** the default extension. */
  public final static String EXTENSION = ".7z";

  /**
   * Decompresses the specified lzma archive to a file without the ".7z"
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
  @MixedCopyright(
      copyright = "Julien Ponge",
      license = License.APACHE2,
      url = "https://github.com/jponge/lzma-java/blob/master/README.md"
  )
  public static String decompress(File archiveFile, int buffer, File outputFile) {
    String		result;
    LzmaInputStream	in;
    OutputStream 	out;
    String		msg;

    in     = null;
    out    = null;
    result = null;
    try {

      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      in = new LzmaInputStream(
	  new BufferedInputStream(new FileInputStream(archiveFile.getAbsolutePath())),
	  new Decoder());
      out = new BufferedOutputStream(new FileOutputStream(outputFile.getAbsolutePath()));

      IOUtils.copy(in, out, buffer);
      out.close();      
      out = null;
      in.close();
      in = null;
    }
    catch (Exception e) {
      msg = "Failed to decompress '" + archiveFile + "': ";
      System.err.println(msg);
      e.printStackTrace();
      result = msg + e;
    }
    finally {
      if (in != null) {
	try {
	  in.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
      if (out != null) {
	try {
	  out.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Compresses the specified lzma archive to a file with the ".7z"
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
  @MixedCopyright(
      copyright = "Julien Ponge",
      license = License.APACHE2,
      url = "https://github.com/jponge/lzma-java/blob/master/README.md"
  )
  public static String compress(File inputFile, int buffer, File outputFile, boolean removeInput) {
    String			result;
    LzmaOutputStream 		out;
    BufferedInputStream 	in;
    String			msg;

    in     = null;
    out    = null;
    result = null;
    try {
      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      in  = new BufferedInputStream(new FileInputStream(inputFile.getAbsolutePath()));
      out = new LzmaOutputStream.Builder(new BufferedOutputStream(new FileOutputStream(outputFile.getAbsolutePath()))).build();
      
      IOUtils.copy(in, out, buffer);
      in.close();
      in = null;
      out.close();
      out = null;

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
      if (in != null) {
	try {
	  in.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
      if (out != null) {
	try {
	  out.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }
}
