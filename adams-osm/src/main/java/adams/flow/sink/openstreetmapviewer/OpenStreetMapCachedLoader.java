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
 * OpenStreetMapCachedLoader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

import adams.core.io.PlaceholderDirectory;

/**
 <!-- globalinfo-start -->
 * Returns the default OpenStreetMap file cache tile loader.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-cache-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: cacheDir)
 * &nbsp;&nbsp;&nbsp;The cache directory to use.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see OsmTileLoader
 */
public class OpenStreetMapCachedLoader
  extends AbstractTileLoaderProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3965568571771391365L;

  /** the cache directory. */
  protected PlaceholderDirectory m_CacheDir;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the default OpenStreetMap file cache tile loader.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cache-dir", "cacheDir",
	    new PlaceholderDirectory("${TMP}"));
  }

  /**
   * Sets the cache directory.
   *
   * @param value	the directory
   */
  public void setCacheDir(PlaceholderDirectory value) {
    m_CacheDir = value;
    reset();
  }

  /**
   * Returns the cache directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getCacheDir() {
    return m_CacheDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheDirTipText() {
    return "The cache directory to use.";
  }

  /**
   * Performs the actual instantiation of the tileloader.
   * 
   * @param listener	the listener to use for instantiating
   * @return		the instantiated tileloader
   * @throws Exception	if generation fails for some reason
   */
  @Override
  protected TileLoader doGenerate(TileLoaderListener listener) throws Exception {
    return new OsmFileCacheTileLoader(listener, m_CacheDir.getAbsoluteFile());
  }
}
