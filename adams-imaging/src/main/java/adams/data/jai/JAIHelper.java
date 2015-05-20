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
 * JAIHelper.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai;

import java.io.File;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;

/**
 * Helper class for JAI operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JAIHelper {

  /**
   * Disables the "java.lang.NoClassDefFoundError: com/sun/medialib/mlib/Image"
   * exception.
   * <br><br>
   * Taken from <a href="http://www.java.net/node/666373" target="_blank">here</a>
   */
  public static void disableMediaLib() {
    System.setProperty("com.sun.media.jai.disableMediaLib", "true");
  }

  /**
   * Loads the specified file.
   *
   * @param file	the file to load
   * @return		the image
   */
  public static RenderedOp read(File file) {
    return read(file.getAbsolutePath());
  }

  /**
   * Loads the specified file.
   *
   * @param file	the file to load
   * @return		the image
   */
  public static RenderedOp read(String file) {
    RenderedOp	result;

    result =  JAI.create("fileload", file);

    return result;
  }
  
  /**
   * Creates a {@link BufferedImageContainer} container if necessary, otherwise
   * it just casts the object.
   * 
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static BufferedImageContainer toBufferedImageContainer(AbstractImageContainer cont) {
    BufferedImageContainer	result;
    Report			report;
    Notes			notes;
    
    if (cont instanceof BufferedImageContainer)
      return (BufferedImageContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = new BufferedImageContainer();
    result.setImage(cont.toBufferedImage());
    result.setReport(report);
    result.setNotes(notes);
    
    return result;
  }
}
