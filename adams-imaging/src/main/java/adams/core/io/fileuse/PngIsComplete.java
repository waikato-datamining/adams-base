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
 * PngIsComplete.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import adams.core.Utils;
import adams.core.io.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Checks whether the PNG file ends with bytes IEND (EOF for PNGs).<br>
 * See also:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Portable_Network_Graphics#Critical_chunks<br>
 * http:&#47;&#47;www.libpng.org&#47;pub&#47;png&#47;spec&#47;1.2&#47;PNG-Structure.html#Chunk-layout<br>
 * http:&#47;&#47;www.libpng.org&#47;pub&#47;png&#47;spec&#47;1.2&#47;PNG-Chunks.html#C.IEND
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PngIsComplete
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Checks whether the PNG file ends with bytes IEND (EOF for PNGs).\n"
      + "See also:\n"
      + "https://en.wikipedia.org/wiki/Portable_Network_Graphics#Critical_chunks\n"
      + "http://www.libpng.org/pub/png/spec/1.2/PNG-Structure.html#Chunk-layout\n"
      + "http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html#C.IEND";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    boolean		result;
    RandomAccessFile	raf;
    byte[]		buffer;

    raf = null;
    try {
      buffer = new byte[8];
      raf    = new RandomAccessFile(file.getAbsolutePath(), "r");
      if (file.length() > 8) {
	raf.seek(file.length() - 8);
	raf.read(buffer, 0, 8);
	result = !((buffer[0] == 73) && (buffer[1] == 69) && (buffer[2] == 78) && (buffer[3] == 68));  // IEND
	if (isLoggingEnabled())
	  getLogger().info("First four bytes of the last eight byte block: "
	    + Utils.toHex(buffer[0]) + Utils.toHex(buffer[1])
	    + Utils.toHex(buffer[2]) + Utils.toHex(buffer[3]) + " -> " + result);
      }
      else {
	// too small, must currently being written
	result = true;
      }
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().log(Level.SEVERE, "Failed to extract bytes from: " + file, e);
      result = true;
    }
    finally {
      FileUtils.closeQuietly(raf);
    }

    return result;
  }
}
