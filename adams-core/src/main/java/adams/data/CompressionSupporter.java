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
 * CompressableDataContainerWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import adams.core.option.OptionHandler;
import adams.data.io.output.AbstractDataContainerWriter;


/**
 * Interface for {@link AbstractDataContainerWriter} classes that support
 * compression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CompressionSupporter 
  extends OptionHandler {

  /**
   * Sets whether to use compression.
   *
   * @param value	true if to use compression
   */
  public void setUseCompression(boolean value);

  /**
   * Returns whether compression is in use.
   *
   * @return 		true if compression is used
   */
  public boolean getUseCompression();

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String useCompressionTipText();
}
