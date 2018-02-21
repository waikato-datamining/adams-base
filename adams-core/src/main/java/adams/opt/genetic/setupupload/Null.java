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
 * Null.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.setupupload;

import java.util.Map;

/**
 * Dummy, does not nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Null
  extends AbstractSetupUpload {

  private static final long serialVersionUID = 5071310866052238520L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does not nothing.";
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  public boolean requiresFlowContext() {
    return false;
  }

  /**
   * Uploads the setup.
   *
   * @param setup	the setup data to upload
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpload(Map<String, Object> setup) {
    if (isLoggingEnabled())
      getLogger().info(setup.toString());
    return null;
  }
}
