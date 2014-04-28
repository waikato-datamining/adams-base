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
 * AbstractTileSourceProvider.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for tilesource providers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTileSourceProvider
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3871234169648533791L;

  /**
   * Performs the actual instantiation of the tilesource.
   * 
   * @return		the instantiated tilesource
   * @throws Exception	if generation fails for some reason
   */
  protected abstract TileSource doGenerate() throws Exception;
  
  /**
   * Returns the tilesource to use.
   * 
   * @return		the instantiated tilesource
   * @throws Exception	if generation fails for some reason
   */
  public TileSource generate() throws Exception {
    return doGenerate();
  }
}
