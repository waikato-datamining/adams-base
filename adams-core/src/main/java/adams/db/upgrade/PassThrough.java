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
 * PassThrough.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.db.upgrade;

/**
 <!-- globalinfo-start -->
 * A dummy upgrader, does nothing.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 * 
 <!-- options-end -->
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractTableUpgrade {

  /** for serialization. */
  private static final long serialVersionUID = -7684218434080097460L;

  /**
   * Returns a string describing the object.
   * 
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A dummy upgrader, does nothing.";
  }

  /**
   * Does nothing.
   */
  protected void doUpgrade() {
    m_UpgradeInfo.append("Done nuffin' and succeeded brilliantly in doing that! ;-)\n");
  }
}
