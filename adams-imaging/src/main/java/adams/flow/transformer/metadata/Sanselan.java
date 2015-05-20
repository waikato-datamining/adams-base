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
 * Sanselan.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.metadata;

import java.io.File;

import adams.data.image.ImageMetaDataHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Uses the Sanselan library to extract the meta-data.<br>
 * For more information see:<br>
 * https:&#47;&#47;commons.apache.org&#47;proper&#47;commons-imaging&#47;
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Sanselan
  extends AbstractMetaDataExtractor {

  /** for serialization. */
  private static final long serialVersionUID = 3185245918519979059L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses the Sanselan library to extract the meta-data.\n"
	+ "For more information see:\n"
	+ "https://commons.apache.org/proper/commons-imaging/";
  }

  /**
   * Performs the actual meta-data extraction.
   * 
   * @param file	the file to process
   * @return		the meta-data
   * @throws Exception	if extraction fails
   */
  @Override
  protected SpreadSheet doExtract(File file) throws Exception {
    return ImageMetaDataHelper.getMetaData(file);
  }
}
