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
 * LzfUtils.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.utils.IOUtils;

import com.ning.compress.lzf.LZFInputStream;
import com.ning.compress.lzf.LZFOutputStream;

/**
 * Helper class for LZF related operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LzfUtils {

  /** the default extension. */
  public final static String EXTENSION = ".lzf";

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
    String		msg;

    in     = null;
    out    = null;
    result = null;
    try {

      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      in  = new LZFInputStream(new FileInputStream(archiveFile.getAbsolutePath()));
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
    String			msg;

    in     = null;
    out    = null;
    result = null;
    try {
      // does file already exist?
      if (outputFile.exists())
	System.err.println("WARNING: overwriting '" + outputFile + "'!");

      in  = new BufferedInputStream(new FileInputStream(inputFile.getAbsolutePath()));
      out = new LZFOutputStream(new FileOutputStream(outputFile.getAbsolutePath()));
      
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
