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
 * AbstractMapObjectPruner.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import org.openstreetmap.gui.jmapviewer.JMapViewerTree;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that prune the map objects of a 
 * {@link JMapViewerTree}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapObjectPruner
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5527808078324909954L;

  /**
   * Prunes the map objects.
   * 
   * @param tree	the tree to prune
   */
  protected abstract void doPrune(JMapViewerTree tree);

  /**
   * Prunes the map objects.
   * 
   * @param tree	the tree to prune
   */
  public void prune(JMapViewerTree tree) {
    doPrune(tree);
  }
}
