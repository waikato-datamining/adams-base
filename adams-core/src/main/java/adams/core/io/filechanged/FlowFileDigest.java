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
 * FlowFileDigest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filechanged;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.NestedProducer;

import java.io.File;
import java.util.List;

/**
 * Generates a message digest for a flow file and uses that for comparison.
 * Skips the comments at the start.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FlowFileDigest
  extends AbstractMessageDigestBasedMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a message digest for a flow file and uses that for comparison.\n"
      + "Skips the comments at the start.";
  }

  /**
   * Generates the message digest, if possible.
   *
   * @param file	the file to generate the digest for
   * @param errors	for collecting any errors
   * @return		the digest
   */
  @Override
  protected String computeDigest(File file, MessageCollection errors) {
    List<String> 	lines;

    lines = FileUtils.loadFromFile(file);
    Utils.removeComments(lines, NestedProducer.COMMENT);

    return m_Type.digest(lines.toArray(new String[lines.size()]), errors);
  }
}
