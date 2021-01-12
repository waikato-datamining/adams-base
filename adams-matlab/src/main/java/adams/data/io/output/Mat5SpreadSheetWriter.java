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
 * Mat5SpreadSheetWriter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.data.conversion.SpreadSheetToMatlabArray;
import adams.data.io.input.Mat5SpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Sinks;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Writes Matlab .mat files (format 5)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-sheet-prefix &lt;java.lang.String&gt; (property: sheetPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix for sheet names.
 * &nbsp;&nbsp;&nbsp;default: Sheet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mat5SpreadSheetWriter
  extends AbstractMultiSheetSpreadSheetWriter {

  private static final long serialVersionUID = 6981291532332284718L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes Matlab .mat files (format 5)";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new Mat5SpreadSheetReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new Mat5SpreadSheetReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return the reader, null if none available
   */
  @Override
  public SpreadSheetReader getCorrespondingReader() {
    return new Mat5SpreadSheetReader();
  }

  /**
   * Returns how the data is written.
   *
   * @return the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.FILE;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet[] content, String filename) {
    String[]    		names;
    Set<String> 		unique;
    int				i;
    int				n;
    Mat5File			mat5;
    SpreadSheetToMatlabArray	conv;
    Array			array;
    String			msg;

    // ensure names are unique
    names  = new String[content.length];
    unique = new HashSet<>();
    for (i = 0; i < content.length; i++) {
      names[i] = content[i].getName();
      n        = 0;
      while (unique.contains(names[i])) {
        n++;
        names[i] = content[i].getName() + "-" + n;
      }
      unique.add(names[i]);
    }

    mat5 = Mat5.newMatFile();
    conv = new SpreadSheetToMatlabArray();
    for (i = 0; i < content.length; i++) {
      conv.setInput(content[i]);
      msg = conv.convert();
      if (msg != null) {
        getLogger().severe("Failed to convert spreadsheet #" + (i+1) + " into Matlab array:\n" + msg);
        return false;
      }
      array = (Array) conv.getOutput();
      mat5.addArray(names[i], array);
    }

    try {
      mat5.writeTo(Sinks.newStreamingFile(new PlaceholderFile(filename).getAbsoluteFile()));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write to file: " + filename, e);
      return false;
    }

    return true;
  }
}
