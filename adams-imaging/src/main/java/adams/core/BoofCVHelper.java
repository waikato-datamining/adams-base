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
 * BoofCVHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.data.Notes;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.image.AbstractImage;
import adams.data.report.Report;
import boofcv.core.image.ConvertBufferedImage;

/**
 * Helper class for BoofCV operations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVHelper {
  
  /**
   * Creates a {@link BoofCVImageContainer} container if necessary, otherwise
   * it just casts the object.
   * 
   * @param cont	the cont to cast/convert
   * @param type	the type of image
   * @return		the casted/converted container
   */
  public static BoofCVImageContainer toBoofCVImageContainer(AbstractImage cont, BoofCVImageType type) {
    BoofCVImageContainer	result;
    Report			report;
    Notes			notes;
    
    if (cont instanceof BoofCVImageContainer)
      return (BoofCVImageContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = new BoofCVImageContainer();
    result.setImage(ConvertBufferedImage.convertFromSingle(cont.toBufferedImage(), null, type.getImageClass()));
    result.setReport(report);
    result.setNotes(notes);
    
    return result;
  }
}
