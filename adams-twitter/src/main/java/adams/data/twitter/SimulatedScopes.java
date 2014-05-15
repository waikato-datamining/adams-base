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
 * SimulatedScopes.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.twitter;

import twitter4j.Scopes;

/**
 * For simulating tweets without using Twitter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedScopes
  extends AbstractSimulatedTwitterResponse
  implements Scopes {

  /** for serialization. */
  private static final long serialVersionUID = 1645190500537197966L;
  
  /** the place IDs. */
  protected String[] m_PlaceIds;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PlaceIds = null;
  }

  /**
   * Sets the place IDs.
   * 
   * @param value	the IDs
   */
  public void setPlaceIds(String[] value) {
    m_PlaceIds = value;
  }
  
  /**
   * Returns the place_ids that identify the scope of the status.
   *
   * @return the place_ids that identify the scope of the status.
   */
  @Override
  public String[] getPlaceIds() {
    return m_PlaceIds;
  }

}
