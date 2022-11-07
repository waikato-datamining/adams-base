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
 * IcePDF.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.logging.LoggingHelper;
import org.icepdf.core.pobjects.Document;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Helper class for IcePDF.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class IcePDF {

  /**
   * Saves the document to the specified file.
   *
   * @param file	the output file
   * @return		null if successful, otherwise error message
   */
  public static String saveTo(Document doc, File file) {
    String			result;
    BufferedOutputStream 	bos;
    FileOutputStream 		fos;

    if (doc == null)
      return null;

    result = null;
    fos    = null;
    bos    = null;
    try {
      fos = new FileOutputStream(file.getAbsoluteFile());
      bos = new BufferedOutputStream(fos);
      doc.saveToOutputStream(bos);
    }
    catch (Exception e) {
      result = "Failed to save PDF to: " + file + "\n" + LoggingHelper.throwableToString(e);
    }
    finally {
      FileUtils.closeQuietly(bos);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

}
