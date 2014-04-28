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
 * CustomMapnikSource.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import adams.core.base.BaseURL;

/**
 <!-- globalinfo-start -->
 * Returns a tile source for a custom Mapnik server.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-url &lt;adams.core.base.BaseURL&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL of the custom Mapnik server to use.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;tile.openstreetmap.org
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CustomMapnikSource
extends AbstractTileSourceProvider {

  /** for serialization. */
  private static final long serialVersionUID = 2395465367677155756L;

  /**
   * The custom "Mapnik" OSM tile source.
   */
  public static class Mapnik extends AbstractOsmTileSource {

    /**
     * Constructs a new tile source.
     * 
     * @param url	the server URL to use
     */
    public Mapnik(String url) {
      super("Mapnik", url);
    }

    /**
     * Returns the tile update mechanism.
     * 
     * @return 		The update mechanism
     * @see 		TileUpdate
     */
    public TileUpdate getTileUpdate() {
      return TileUpdate.IfNoneMatch;
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns a tile source for a custom Mapnik server.";
  }

  /** the server URL. */
  protected BaseURL m_URL;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "url", "URL",
	    new BaseURL(OsmTileSource.MAP_MAPNIK));
  }

  /**
   * Sets the Mapnik server URL.
   *
   * @param value	the URL
   */
  public void setURL(BaseURL value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the Mapnik server URL.
   *
   * @return		the URL
   */
  public BaseURL getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The URL of the custom Mapnik server to use.";
  }

  /**
   * Performs the actual instantiation of the tilesource.
   * 
   * @return		the instantiated tilesource
   * @throws Exception	if generation fails for some reason
   */
  @Override
  protected TileSource doGenerate() throws Exception {
    return new OsmTileSource.Mapnik();
  }
}
