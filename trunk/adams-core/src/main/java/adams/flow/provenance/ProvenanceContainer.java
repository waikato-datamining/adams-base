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
 * ProvenanceContainer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.provenance;

import adams.gui.flow.provenance.ProvenanceNode;

/**
 * Interface for containers that store provenance information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ProvenanceContainer {

  /**
   * Returns whether provenance is available.
   *
   * @return		true if provenance is available
   */
  public boolean hasProvenance();

  /**
   * Sets the provenance to use.
   *
   * @param value	the provenance
   */
  public void setProvenance(ProvenanceNode value);

  /**
   * Returns the current provenance.
   *
   * @return		the provenance, null if none available
   */
  public ProvenanceNode getProvenance();

  /**
   * Adds the provenance information to the internal structure.
   *
   * @param info	the info to add
   */
  public void addProvenance(ProvenanceInformation info);

  /**
   * Sets the provenance information. The "info" provenance information will
   * be the result of the parent nodes in the provenance tree.
   *
   * @param info	the provenance information to add (the new root node)
   * @param parents	the parent provenance information of the new node
   */
  public void mergeProvenance(ProvenanceInformation info, ProvenanceNode[] parents);
}
