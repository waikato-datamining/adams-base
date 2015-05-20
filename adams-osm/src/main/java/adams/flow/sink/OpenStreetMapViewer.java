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
 * OpenStreetMapViewer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.openstreetmap.gui.jmapviewer.AbstractLayer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.LayerGroup;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.checkBoxTree.CheckBoxNodeData;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik;

import adams.core.AdditionalInformationHandler;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.gps.GPSDecimalDegrees;
import adams.data.mapobject.HitIndicator;
import adams.flow.core.Token;
import adams.flow.sink.openstreetmapviewer.AbstractMapClickListener;
import adams.flow.sink.openstreetmapviewer.AbstractMapObjectHitListener;
import adams.flow.sink.openstreetmapviewer.AbstractMapObjectPruner;
import adams.flow.sink.openstreetmapviewer.AbstractMapOverlay;
import adams.flow.sink.openstreetmapviewer.AbstractTileLoaderProvider;
import adams.flow.sink.openstreetmapviewer.AbstractTileSourceProvider;
import adams.flow.sink.openstreetmapviewer.NullMapClickListener;
import adams.flow.sink.openstreetmapviewer.NullMapObjectHitListener;
import adams.flow.sink.openstreetmapviewer.NullMapOverlay;
import adams.flow.sink.openstreetmapviewer.NullPruner;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.MouseUtils;
import adams.gui.core.dotnotationtree.DotNotationNode;
import adams.gui.core.dotnotationtree.DotNotationTree;
import adams.gui.event.MapClickEvent;
import adams.gui.event.MapObjectHitEvent;
import adams.gui.visualization.osm.OpenStreetMapViewerTree;

/**
 <!-- globalinfo-start -->
 * Displays data layers (markers, rectangles and polygons) on top of a map provided by OpenStreetMap.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.openstreetmap.gui.jmapviewer.interfaces.MapMarker<br>
 * &nbsp;&nbsp;&nbsp;org.openstreetmap.gui.jmapviewer.interfaces.MapMarker[]<br>
 * &nbsp;&nbsp;&nbsp;org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon<br>
 * &nbsp;&nbsp;&nbsp;org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon[]<br>
 * &nbsp;&nbsp;&nbsp;org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle<br>
 * &nbsp;&nbsp;&nbsp;org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: OpenStreetMapViewer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-tile-source &lt;adams.flow.sink.openstreetmapviewer.AbstractTileSourceProvider&gt; (property: tileSource)
 * &nbsp;&nbsp;&nbsp;The provider for generating the tile source.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.openstreetmapviewer.OpenStreetMapSource
 * </pre>
 * 
 * <pre>-tile-loader &lt;adams.flow.sink.openstreetmapviewer.AbstractTileLoaderProvider&gt; (property: tileLoader)
 * &nbsp;&nbsp;&nbsp;The provider for generating the tile loader.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.openstreetmapviewer.OpenStreetMapLoader
 * </pre>
 * 
 * <pre>-hit-listener &lt;adams.flow.sink.openstreetmapviewer.AbstractMapObjectHitListener&gt; (property: hitListener)
 * &nbsp;&nbsp;&nbsp;The listener for hits when the user left-clicks on map objects.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.openstreetmapviewer.NullMapObjectHitListener
 * </pre>
 * 
 * <pre>-click-listener &lt;adams.flow.sink.openstreetmapviewer.AbstractMapClickListener&gt; (property: clickListener)
 * &nbsp;&nbsp;&nbsp;The listener for clicks on the map.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.openstreetmapviewer.NullMapClickListener
 * </pre>
 * 
 * <pre>-mapobject-pruner &lt;adams.flow.sink.openstreetmapviewer.AbstractMapObjectPruner&gt; (property: mapObjectPruner)
 * &nbsp;&nbsp;&nbsp;The scheme for pruning the map objects.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.openstreetmapviewer.NullPruner
 * </pre>
 * 
 * <pre>-map-overlay &lt;adams.flow.sink.openstreetmapviewer.AbstractMapOverlay&gt; (property: mapOverlay)
 * &nbsp;&nbsp;&nbsp;The overlay for the map.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.openstreetmapviewer.NullMapOverlay
 * </pre>
 * 
 * <pre>-initial-coordinates &lt;adams.data.gps.GPSDecimalDegrees&gt; (property: initialCoordinates)
 * &nbsp;&nbsp;&nbsp;The initial coordinates to use.
 * &nbsp;&nbsp;&nbsp;default: N0.0 W0.0
 * </pre>
 * 
 * <pre>-initial-zoom &lt;int&gt; (property: initialZoom)
 * &nbsp;&nbsp;&nbsp;The initial zoom to use.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-zoom-controls &lt;boolean&gt; (property: zoomControls)
 * &nbsp;&nbsp;&nbsp;If enabled the zoom controls will get displayed.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-tile-grid &lt;boolean&gt; (property: tileGrid)
 * &nbsp;&nbsp;&nbsp;If enabled, the tile grid gets displayed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-divider-location &lt;int&gt; (property: dividerLocation)
 * &nbsp;&nbsp;&nbsp;The location in pixels for the divider between layer tree and map.
 * &nbsp;&nbsp;&nbsp;default: 150
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-layer-tree &lt;boolean&gt; (property: layerTree)
 * &nbsp;&nbsp;&nbsp;If enabled, the layer tree is displayed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-layer-tree-root &lt;java.lang.String&gt; (property: layerTreeRoot)
 * &nbsp;&nbsp;&nbsp;The label for the root of the layer tree.
 * &nbsp;&nbsp;&nbsp;default: Layers
 * </pre>
 * 
 * <pre>-layer &lt;adams.core.base.BaseString&gt; [-layer ...] (property: layers)
 * &nbsp;&nbsp;&nbsp;The layer names; use '.' to separate levels, eg the three layers 'A A.B 
 * &nbsp;&nbsp;&nbsp;A.C' will create layers B and C below A.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStreetMapViewer
  extends AbstractGraphicalDisplay
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6066131046841201616L;

  /** the map viewer. */
  protected OpenStreetMapViewerTree m_Viewer;
  
  /** the layer dot-notation to layer relation. */
  protected HashMap<String,Layer> m_LayerLookup;
  
  /** the tilesource provider to use. */
  protected AbstractTileSourceProvider m_TileSource;
  
  /** the tileloader provider to use. */
  protected AbstractTileLoaderProvider m_TileLoader;
  
  /** the listener for hits. */
  protected AbstractMapObjectHitListener m_HitListener;
  
  /** the listener for clicks. */
  protected AbstractMapClickListener m_ClickListener;
  
  /** for pruning the map objects. */
  protected AbstractMapObjectPruner m_MapObjectPruner;
  
  /** the map overlay. */
  protected AbstractMapOverlay m_MapOverlay;
  
  /** the initial coordinates to use. */
  protected GPSDecimalDegrees m_InitialCoordinates;
  
  /** the initial zoom to use. */
  protected int m_InitialZoom;
  
  /** whether the zoom controls are visible. */
  protected boolean m_ZoomControls;
  
  /** whether the tile grid is visible. */
  protected boolean m_TileGrid;
  
  /** whether the layer tree is shown. */
  protected boolean m_LayerTree;
  
  /** the location for divider between tree and map. */
  protected int m_DividerLocation;
  
  /** the label of the layer tree root. */
  protected String m_LayerTreeRoot;
  
  /** the layers. */
  protected BaseString[] m_Layers;

  /** allows us to paint the overlays. */
  protected JPanel m_MapOverlayPlaceholderPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Displays data layers (markers, rectangles and polygons) on top of a "
	+ "map provided by OpenStreetMap.";
  }
  
  /**
   * Returns the additional information.
   * 
   * @return		the additional information, null or 0-length string for no information
   */
  @Override
  public String getAdditionalInformation() {
    return 
	super.getAdditionalInformation()
	+ "\n\n"
	+ "Uses the JMapViewer component:\n"
	+ "https://wiki.openstreetmap.org/wiki/JMapViewer";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tile-source", "tileSource",
	    new adams.flow.sink.openstreetmapviewer.OpenStreetMapSource());

    m_OptionManager.add(
	    "tile-loader", "tileLoader",
	    new adams.flow.sink.openstreetmapviewer.OpenStreetMapLoader());

    m_OptionManager.add(
	    "hit-listener", "hitListener",
	    new NullMapObjectHitListener());

    m_OptionManager.add(
	    "click-listener", "clickListener",
	    new NullMapClickListener());

    m_OptionManager.add(
	    "mapobject-pruner", "mapObjectPruner",
	    new NullPruner());

    m_OptionManager.add(
	    "map-overlay", "mapOverlay",
	    new NullMapOverlay());

    m_OptionManager.add(
	    "initial-coordinates", "initialCoordinates",
	    new GPSDecimalDegrees());

    m_OptionManager.add(
	    "initial-zoom", "initialZoom",
	    0, 0, null);

    m_OptionManager.add(
	    "zoom-controls", "zoomControls",
	    true);

    m_OptionManager.add(
	    "tile-grid", "tileGrid",
	    false);

    m_OptionManager.add(
	    "divider-location", "dividerLocation",
	    150, 0, null);

    m_OptionManager.add(
	    "layer-tree", "layerTree",
	    false);

    m_OptionManager.add(
	    "layer-tree-root", "layerTreeRoot",
	    "Layers");

    m_OptionManager.add(
	    "layer", "layers",
	    new BaseString[]{new BaseString("Default")});
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "tileSource", m_TileSource, "source: ");
    result += QuickInfoHelper.toString(this, "tileLoader", m_TileLoader, ", loader: ");
    result += QuickInfoHelper.toString(this, "hitListener", m_HitListener, ", hits: ");
    result += QuickInfoHelper.toString(this, "clickListener", m_ClickListener, ", clicks: ");
    result += QuickInfoHelper.toString(this, "mapObjectPruner", m_MapObjectPruner, ", pruner: ");
    result += QuickInfoHelper.toString(this, "mapOverlay", m_MapObjectPruner, ", overlay: ");
    result += QuickInfoHelper.toString(this, "layers", (m_Layers.length == 0 ? "none" : Utils.arrayToString(m_Layers)), ", layers: ");

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "zoomControls", m_ZoomControls, "zoom ctrls"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "tileGrid", m_TileGrid, "grid"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "layerTree", m_LayerTree, "layer tree"));
    result += ", " + QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the tile source provider.
   *
   * @param value	the provider
   */
  public void setTileSource(AbstractTileSourceProvider value) {
    m_TileSource = value;
    reset();
  }

  /**
   * Returns the current tile source provider.
   *
   * @return		the provider
   */
  public AbstractTileSourceProvider getTileSource() {
    return m_TileSource;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tileSourceTipText() {
    return "The provider for generating the tile source.";
  }

  /**
   * Sets the tile loader provider.
   *
   * @param value	the provider
   */
  public void setTileLoader(AbstractTileLoaderProvider value) {
    m_TileLoader = value;
    reset();
  }

  /**
   * Returns the current tile loader provider.
   *
   * @return		the provider
   */
  public AbstractTileLoaderProvider getTileLoader() {
    return m_TileLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tileLoaderTipText() {
    return "The provider for generating the tile loader.";
  }

  /**
   * Sets the hit listener for {@link MapObject}s.
   *
   * @param value	the listener
   */
  public void setHitListener(AbstractMapObjectHitListener value) {
    m_HitListener = value;
    reset();
  }

  /**
   * Returns the current {@link MapObject} hit listener.
   *
   * @return		the listener
   */
  public AbstractMapObjectHitListener getHitListener() {
    return m_HitListener;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hitListenerTipText() {
    return "The listener for hits when the user left-clicks on map objects.";
  }

  /**
   * Sets the click listener.
   *
   * @param value	the listener
   */
  public void setClickListener(AbstractMapClickListener value) {
    m_ClickListener = value;
    reset();
  }

  /**
   * Returns the current click listener.
   *
   * @return		the listener
   */
  public AbstractMapClickListener getClickListener() {
    return m_ClickListener;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clickListenerTipText() {
    return "The listener for clicks on the map.";
  }

  /**
   * Sets the pruner for {@link MapObject}s.
   *
   * @param value	the pruner
   */
  public void setMapObjectPruner(AbstractMapObjectPruner value) {
    m_MapObjectPruner = value;
    reset();
  }

  /**
   * Returns the current {@link MapObject} pruner.
   *
   * @return		the pruner
   */
  public AbstractMapObjectPruner getMapObjectPruner() {
    return m_MapObjectPruner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mapObjectPrunerTipText() {
    return "The scheme for pruning the map objects.";
  }

  /**
   * Sets the overlay for the map.
   *
   * @param value	the overlay
   */
  public void setMapOverlay(AbstractMapOverlay value) {
    m_MapOverlay = value;
    reset();
  }

  /**
   * Returns the current map overlay.
   *
   * @return		the overlay
   */
  public AbstractMapOverlay getMapOverlay() {
    return m_MapOverlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mapOverlayTipText() {
    return "The overlay for the map.";
  }

  /**
   * Sets the initial coordinates.
   *
   * @param value	the coordinates
   */
  public void setInitialCoordinates(GPSDecimalDegrees value) {
    m_InitialCoordinates = value;
    reset();
  }

  /**
   * Returns the initial coordinates.
   *
   * @return		the coordinates
   */
  public GPSDecimalDegrees getInitialCoordinates() {
    return m_InitialCoordinates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialCoordinatesTipText() {
    return "The initial coordinates to use.";
  }

  /**
   * Sets the initial zoom.
   *
   * @param value	the zoom level
   */
  public void setInitialZoom(int value) {
    if (value >= 0) {
      m_InitialZoom = value;
      reset();
    }
    else {
      getLogger().warning("Initial zoom level must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the initial zoom.
   *
   * @return		the zoom level
   */
  public int getInitialZoom() {
    return m_InitialZoom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialZoomTipText() {
    return "The initial zoom to use.";
  }

  /**
   * Sets whether to display the zoom controls.
   *
   * @param value	true if to display zoom controls
   */
  public void setZoomControls(boolean value) {
    m_ZoomControls = value;
    reset();
  }

  /**
   * Returns whether to display the zoom controls.
   *
   * @return		true if zoom controls are displayed
   */
  public boolean getZoomControls() {
    return m_ZoomControls;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomControlsTipText() {
    return "If enabled the zoom controls will get displayed.";
  }

  /**
   * Sets whether to display the tile grid.
   *
   * @param value	if true the tile grid gets displayed
   */
  public void setTileGrid(boolean value) {
    m_TileGrid = value;
    reset();
  }

  /**
   * Returns whether the tile grid gets displayed.
   *
   * @return		true if the tile grid gets displayed
   */
  public boolean getTileGrid() {
    return m_TileGrid;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tileGridTipText() {
    return "If enabled, the tile grid gets displayed.";
  }

  /**
   * Sets the location for the divider between layer tree and map.
   *
   * @param value	the location in pixels
   */
  public void setDividerLocation(int value) {
    m_DividerLocation = value;
    reset();
  }

  /**
   * Returns the location of the divider between layer tree and map.
   *
   * @return		the location in pixels
   */
  public int getDividerLocation() {
    return m_DividerLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dividerLocationTipText() {
    return "The location in pixels for the divider between layer tree and map.";
  }

  /**
   * Sets whether to display the layer tree.
   *
   * @param value	if true the layer tree gets displayed
   */
  public void setLayerTree(boolean value) {
    m_LayerTree = value;
    reset();
  }

  /**
   * Returns whether the layer tree gets displayed.
   *
   * @return		true if the layer tree gets displayed
   */
  public boolean getLayerTree() {
    return m_LayerTree;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerTreeTipText() {
    return "If enabled, the layer tree is displayed.";
  }

  /**
   * Sets the root label of the layer tree.
   *
   * @param value	the root label
   */
  public void setLayerTreeRoot(String value) {
    m_LayerTreeRoot = value;
    reset();
  }

  /**
   * Returns the root label of the layer tree.
   *
   * @return		the root label
   */
  public String getLayerTreeRoot() {
    return m_LayerTreeRoot;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerTreeRootTipText() {
    return "The label for the root of the layer tree.";
  }

  /**
   * Sets the layer names.
   *
   * @param value	the layer names
   */
  public void setLayers(BaseString[] value) {
    m_Layers = value;
    reset();
  }

  /**
   * Returns the layer names.
   *
   * @return		the layer names
   */
  public BaseString[] getLayers() {
    return m_Layers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layersTipText() {
    return "The layer names; use '.' to separate levels, eg the three layers 'A A.B A.C' will create layers B and C below A.";
  }

  /**
   * Returns the underlying viewer.
   * 
   * @return		the viewer, null if not initialized
   */
  public JMapViewerTree getViewer() {
    return m_Viewer;
  }
  
  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{MapMarker.class, MapMarker[].class, MapPolygon.class, MapPolygon[].class, MapRectangle.class, MapRectangle[].class};
  }

  /**
   * Updates the layer of the MapObject with the one used in the tree.
   * 
   * @param object	the MapObject to update
   * @return		true if layer was found
   */
  protected boolean updateLayer(MapObject object) {
    String	name;
    
    name = object.getLayer().getName();
    if (m_LayerLookup.containsKey(name)) {
      object.setLayer(m_LayerLookup.get(name));
      return true;
    }

    return false;
  }
  
  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    MapMarker[]		markers;
    MapPolygon[] 	polygons;
    MapRectangle[]	rectangles;
    
    if (token.getPayload() instanceof MapMarker) {
      MapMarker marker = (MapMarker) token.getPayload();
      if (updateLayer(marker))
	m_Viewer.getViewer().addMapMarker(marker);
    }
    else if (token.getPayload() instanceof MapMarker[]) {
      markers = (MapMarker[]) token.getPayload();
      for (MapMarker marker: markers) {
	if (updateLayer(marker))
	  m_Viewer.getViewer().addMapMarker(marker);
      }
    }
    else if (token.getPayload() instanceof MapPolygon) {
      MapPolygon polygon = (MapPolygon) token.getPayload();
      if (updateLayer(polygon))
	m_Viewer.getViewer().addMapPolygon(polygon);
    }
    else if (token.getPayload() instanceof MapPolygon[]) {
      polygons = (MapPolygon[]) token.getPayload();
      for (MapPolygon polygon: polygons) {
	if (updateLayer(polygon))
	  m_Viewer.getViewer().addMapPolygon(polygon);
      }
    }
    else if (token.getPayload() instanceof MapRectangle) {
      MapRectangle rectangle = (MapRectangle) token.getPayload();
      if (updateLayer(rectangle))
	m_Viewer.getViewer().addMapRectangle(rectangle);
    }
    else if (token.getPayload() instanceof MapRectangle[]) {
      rectangles = (MapRectangle[]) token.getPayload();
      for (MapRectangle rectangle: rectangles) {
	if (updateLayer(rectangle))
	  m_Viewer.getViewer().addMapRectangle(rectangle);
      }
    }
    
    if (!(m_MapObjectPruner instanceof NullPruner))
      m_MapObjectPruner.prune(m_Viewer);
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Viewer != null) {
      m_Viewer.getViewer().removeAllMapMarkers();
      m_Viewer.getViewer().removeAllMapPolygons();
      m_Viewer.getViewer().removeAllMapRectangles();
      m_Viewer.getTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode(new CheckBoxNodeData(m_LayerTreeRoot))));
      // TODO more?
    }
  }
  
  /**
   * Adds the children of the parent node to the layer tree.
   * 
   * @param parent	the parent node
   * @param group	the optional parent layer group, can be null
   */
  protected void addLayers(DotNotationNode parent, LayerGroup group) {
    LayerGroup	groupNested;
    Layer	layer;
    
    if (parent.getChildCount() == 0) {
      if (group == null) {
	layer = m_Viewer.getTree().addLayer(parent.getLabel());
      }
      else {
	layer = group.addLayer(parent.getLabel());
	m_Viewer.getTree().addLayer(layer);
      }
      m_LayerLookup.put(parent.getItem(), layer);
    }
    else {
      if (group == null)
	groupNested = new LayerGroup(parent.getLabel());
      else
	groupNested = new LayerGroup(group, parent.getLabel());
      m_Viewer.getTree().addLayer(groupNested);
      for (BaseTreeNode node: parent.getChildren())
	addLayers((DotNotationNode) node, groupNested);
    }
  }
  
  /**
   * Creates the layer tree from the labels.
   */
  protected void createLayerTree() {
    DotNotationTree	tree;
    DotNotationNode	root;

    tree = new DotNotationTree<DotNotationNode>();
    tree.setCompress(false);
    tree.setItems(m_Layers);
    m_LayerLookup = new HashMap<String,Layer>();

    root = (DotNotationNode) tree.getModel().getRoot();
    if (root != null) {
      for (BaseTreeNode node: root.getChildren())
	addLayers((DotNotationNode) node, null);
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel		result;
    
    result = new BasePanel();
    result.setLayout(new BorderLayout());
    
    m_Viewer = new OpenStreetMapViewerTree(m_LayerTreeRoot);
    m_Viewer.setTreeVisible(m_LayerTree);
    m_Viewer.setDividerLocation(m_DividerLocation);
    createLayerTree();
    m_Viewer.getViewer().setZoomContolsVisible(m_ZoomControls);
    m_Viewer.getViewer().setTileGridVisible(m_TileGrid);
    try {
      m_Viewer.getViewer().setTileSource(m_TileSource.generate());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to generate tile source: " + OptionUtils.getCommandLine(m_TileSource), e);
      m_Viewer.getViewer().setTileSource(new Mapnik());
    }
    try {
      m_Viewer.getViewer().setTileLoader(m_TileLoader.generate(m_Viewer.getViewer()));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to generate tile loader: " + OptionUtils.getCommandLine(m_TileLoader), e);
      m_Viewer.getViewer().setTileLoader(new OsmTileLoader(m_Viewer.getViewer()));
    }
    m_Viewer.getViewer().setDisplayPositionByLatLon(m_InitialCoordinates.getLatitude().toDecimal(), m_InitialCoordinates.getLongitude().toDecimal(), m_InitialZoom);
    m_Viewer.getViewer().setZoom(m_InitialZoom);
    
    // hit listener?
    if (!(m_HitListener instanceof NullMapObjectHitListener)) {
      m_Viewer.getViewer().addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isLeftClick(e)) {
	    List<MapObject> hits = new ArrayList<MapObject>();
	    Coordinate coord = m_Viewer.getViewer().getPosition(e.getPoint());
	    HashSet<AbstractLayer> visible = new HashSet<AbstractLayer>(m_Viewer.getVisibleLayers());
	    
	    // markers
	    for (MapMarker obj: m_Viewer.getViewer().getMapMarkerList()) {
	      if (!visible.contains(obj.getLayer()))
		continue;
	      if (obj instanceof HitIndicator) {
		if (((HitIndicator) obj).isHit(m_Viewer.getViewer(), coord))
		  hits.add(obj);
	      }
	    }
	    
	    // rectangles
	    for (MapRectangle obj: m_Viewer.getViewer().getMapRectangleList()) {
	      if (!visible.contains(obj.getLayer()))
		continue;
	      if (obj instanceof HitIndicator) {
		if (((HitIndicator) obj).isHit(m_Viewer.getViewer(), coord))
		  hits.add(obj);
	      }
	    }
	    
	    // polygons
	    for (MapPolygon obj: m_Viewer.getViewer().getMapPolygonList()) {
	      if (!visible.contains(obj.getLayer()))
		continue;
	      if (obj instanceof HitIndicator) {
		if (((HitIndicator) obj).isHit(m_Viewer.getViewer(), coord))
		  hits.add(obj);
	      }
	    }

	    // any hits?
	    if (hits.size() > 0) {
	      m_HitListener.mapObjectsHit(new MapObjectHitEvent(m_Viewer.getViewer(), hits));
	      e.consume();
	    }
	  }
	  
	  if (!e.isConsumed()) {
	    super.mouseClicked(e);
	  }
	}
      });
    }
    
    // click listener?
    if (!(m_ClickListener instanceof NullMapClickListener)) {
      if (m_ClickListener.requiresDatabaseConnection())
	m_ClickListener.updateDatabaseConnection(this);
      
      m_Viewer.getViewer().addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isLeftClick(e)) {
	    if (!e.isConsumed())
	      m_ClickListener.mapClicked(new MapClickEvent(m_Viewer.getViewer(), e));
	  }
	  
	  if (!e.isConsumed()) {
	    super.mouseClicked(e);
	  }
	}
      });
    }

    result.add(m_Viewer, BorderLayout.CENTER);
    
    m_MapOverlayPlaceholderPanel = new JPanel() {
      private static final long serialVersionUID = 8792227302889651948L;
      @Override
      public void validate() {
        super.validate();
        // resize itself to parent's dimensions
        Rectangle rect = getParent().getBounds();
        setBounds(0, 0, rect.width, rect.height);
      }
      @Override
      protected void paintComponent(Graphics g) {
	// pain the overlay
        m_MapOverlay.paintOverlay(OpenStreetMapViewer.this, g);
      }
    };
    m_MapOverlayPlaceholderPanel.setOpaque(false);
    m_Viewer.getViewer().add(m_MapOverlayPlaceholderPanel);
    
    return result;
  }
}
