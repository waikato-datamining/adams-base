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
 * OpenStreetMapView.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.conversion.SpreadSheetJoinColumns;
import adams.data.conversion.SpreadSheetStringColumnToObject;
import adams.data.conversion.SpreadSheetToMapObjects;
import adams.data.conversion.mapobject.SimpleDotMarkerGenerator;
import adams.data.spreadsheet.AbstractObjectHandler;
import adams.data.spreadsheet.GPSDecimalDegrees;
import adams.data.spreadsheet.GPSObjectHandler;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.sink.OpenStreetMapViewer;
import adams.flow.sink.openstreetmapviewer.OpenStreetMapCachedLoader;
import adams.flow.sink.openstreetmapviewer.OpenStreetMapSource;
import adams.flow.sink.openstreetmapviewer.TableMapObjectHitListener;
import adams.flow.source.StorageValue;
import adams.flow.transformer.Convert;
import adams.gui.core.BasePanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.DescriptionPanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * Displays the GIS data in OpenStreetMap (OSM).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStreetMapView
  extends AbstractViewPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 3096161473182386994L;

  /** for key for the meta-data columns. */
  public static final String KEY_META_DATA_COLUMNS = "Meta-data columns";

  /** for key for the format. */
  public static final String KEY_FORMAT = "Format";

  /** for key for the GPS column. */
  public static final String KEY_GPS = "GPS";

  /** for key for the longitude column. */
  public static final String KEY_LONGITUDE = "Longitude";

  /** for key for the latitude column. */
  public static final String KEY_LATITUDE = "Latitude";

  /** the name for the spreadsheet in the flow's storage. */
  public final static String STORAGE_NAME = "sheet";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the GIS data in OpenStreetMap (OSM).";
  }

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  @Override
  public String getMenuText() {
    return "OpenStreetMap";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getMenuIcon() {
    return "osm.png";
  }
  
  /**
   * Returns whether the dialog requires the OK/Cancel buttons.
   * 
   * @return		true if the dialog requires the buttons
   */
  @Override
  public boolean requiresButtons() {
    return false;
  }

  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  @Override
  protected BasePanel doGenerate(final SpreadSheet sheet) {
    final DescriptionPanel		result;
    final PropertiesParameterPanel	propsPanel;
    JPanel				panel;
    List<String>			names;
    String[]				cols;
    Properties				props;
    String				key;
    JButton				buttonOK;
    JButton				buttonCancel;
    List<String>			order;
    
    result = new DescriptionPanel(new BorderLayout());
    result.setDescription("Select either the Latitude and Longitude columns or the column with the GPS data.", false);
    result.setDescriptionHeight(50);
    
    // paramaters
    order = new ArrayList<String>();
    names = sheet.getColumnNames();
    names.add(0, "");
    cols  = names.toArray(new String[0]);
    props = new Properties();
    propsPanel = new PropertiesParameterPanel();
    
    key = KEY_LATITUDE;
    propsPanel.addPropertyType(key, PropertyType.LIST);
    propsPanel.setList(key, cols);
    props.setProperty(key, cols[0]);
    order.add(key);

    key = KEY_LONGITUDE;
    propsPanel.addPropertyType(key, PropertyType.LIST);
    propsPanel.setList(key, cols);
    props.setProperty(key, cols[0]);
    order.add(key);
    
    key = KEY_GPS;
    propsPanel.addPropertyType(key, PropertyType.LIST);
    propsPanel.setList(key, cols);
    props.setProperty(key, cols[0]);
    order.add(key);
    
    key = KEY_FORMAT;
    propsPanel.addPropertyType(key, PropertyType.OBJECT_EDITOR);
    propsPanel.setChooser(key, new GenericObjectEditorPanel(AbstractObjectHandler.class, new GPSDecimalDegrees(), true));
    props.setProperty(key, OptionUtils.getCommandLine(new GPSDecimalDegrees()));
    order.add(key);
    
    key = KEY_META_DATA_COLUMNS;
    propsPanel.addPropertyType(key, PropertyType.STRING);
    props.setProperty(key, "");
    order.add(key);

    propsPanel.setPropertyOrder(order);
    propsPanel.setProperties(props);
    
    result.add(propsPanel, BorderLayout.CENTER);
    
    // button panel
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    result.add(panel, BorderLayout.SOUTH);
    
    buttonOK = new JButton("OK");
    buttonOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	display(propsPanel.getProperties(), sheet);
      }
    });
    panel.add(buttonOK);
    
    buttonCancel = new JButton("Cancel");
    buttonCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	result.closeParent();
      }
    });
    panel.add(buttonCancel);
    
    return result;
  }
  
  /**
   * Generates the flow from the setup and data.
   * 
   * @param name	the tab title
   * @param props	the setup
   * @param sheet	the data to display
   * @return		the generated flow, null if failed to generate
   * @throws Exception	if flow generation fails
   */
  protected Flow generateFlow(String name, Properties props, SpreadSheet sheet) throws Exception {
    Flow			result;
    String			lat;
    String			lon;
    String			gps;
    String			meta;
    SpreadSheetColumnIndex	index;
    int				latIndex;
    int				lonIndex;
    boolean			swapped;
    AbstractObjectHandler	handler;
    
    result = new Flow();
    if (sheet.getName() != null)
      result.setName(sheet.getName());
    
    StorageValue sv = new StorageValue();
    sv.setStorageName(new StorageName(STORAGE_NAME));
    result.add(sv);

    gps = props.getProperty(KEY_GPS, "").trim();
    if (gps.isEmpty()) {
      lat = props.getProperty(KEY_LATITUDE, "").trim();
      lon = props.getProperty(KEY_LONGITUDE, "").trim();
      if (lat.isEmpty() || lon.isEmpty()) {
	GUIHelper.showErrorMessage(
	    m_CurrentPanel, 
	    "If no GPS column is selected both column, latitude and longitude, must be provided");
	result.destroy();
	return null;
      }
      
      index = new SpreadSheetColumnIndex();
      index.setData(sheet);
      index.setIndex(lat);
      latIndex = index.getIntIndex();
      index.setIndex(lon);
      lonIndex = index.getIntIndex();
      
      if (latIndex == lonIndex) {
	GUIHelper.showErrorMessage(
	    m_CurrentPanel, 
	    "Latitude and longitude columns are the same!");
	result.destroy();
	return null;
      }
      
      Convert conv = new Convert();
      SpreadSheetJoinColumns join = new SpreadSheetJoinColumns();
      join.setColumns(new SpreadSheetColumnRange((latIndex+1) + "," + (lonIndex+1))); 
      join.setGlue(" ");
      join.setColumnName("gps");
      conv.setConversion(join);
      conv.setName("join " + lat + " and " + lon);
      result.add(conv);
      
      swapped = (latIndex > lonIndex);
    }
    else {
      swapped = false;
    }
    
    Convert conv = new Convert();
    SpreadSheetStringColumnToObject cto = new SpreadSheetStringColumnToObject();
    if (!gps.isEmpty())
      cto.setColumn(new SpreadSheetColumnIndex(gps));
    else
      cto.setColumn(new SpreadSheetColumnIndex("gps"));
    handler = (AbstractObjectHandler) OptionUtils.forAnyCommandLine(AbstractObjectHandler.class, props.getProperty(KEY_FORMAT, ""));
    if (!(handler instanceof GPSObjectHandler)) {
      GUIHelper.showErrorMessage(
	  m_CurrentPanel, 
	  "You have to select a format that converts GPS strings into objects!\n"
	  + "Selected: " + OptionUtils.getCommandLine(handler));
      result.destroy();
      return null;
    }
    ((GPSObjectHandler) handler).setSwapped(swapped);
    cto.setHandler(handler);
    conv.setConversion(cto);
    conv.setName("generate GPS objects");
    result.add(conv);

    conv = new Convert();
    SpreadSheetToMapObjects map = new SpreadSheetToMapObjects();
    SimpleDotMarkerGenerator generator = new SimpleDotMarkerGenerator();
    generator.setLayer(name);
    if (!gps.isEmpty())
      generator.setGPS(new SpreadSheetColumnIndex(gps));
    else
      generator.setGPS(new SpreadSheetColumnIndex("gps"));
    meta = props.getProperty(KEY_META_DATA_COLUMNS, "");
    if (!meta.isEmpty())
      generator.setAdditionalAttributes(new SpreadSheetColumnRange(meta));
    map.setGenerator(generator);
    conv.setConversion(map);
    conv.setName("generate map objects");
    result.add(conv);
    
    OpenStreetMapViewer osm = new OpenStreetMapViewer();
    osm.setName(name);
    osm.setShortTitle(true);
    osm.setTileSource(new OpenStreetMapSource());
    osm.setTileLoader(new OpenStreetMapCachedLoader());
    osm.setHitListener(new TableMapObjectHitListener());
    osm.setInitialZoom(1);
    osm.setZoomControls(true);
    osm.setLayerTree(true);
    osm.setLayers(new BaseString[]{new BaseString(name)});
    result.add(osm);
    
    result.getStorage().put(new StorageName(STORAGE_NAME), sheet);
    
    if (LoggingHelper.isAtLeast(getLoggingLevel().getLevel(), Level.FINE))
      getLogger().fine(result.toCommandLine());
    
    return result;
  }

  /**
   * Displays the data on a map.
   * 
   * @param props	the setup
   * @param sheet	the data to display
   */
  protected void display(Properties props, SpreadSheet sheet) {
    SwingWorker	worker;
    final Flow	flow;
    
    try {
      flow = generateFlow(FileUtils.replaceExtension(m_CurrentPanel.getTabTitle(), ""), props, sheet);
      if (flow == null)
	return;
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  m_CurrentPanel, 
	  "Failed to generate flow for GIS data display!\n" + Utils.throwableToString(e));
      return;
    }
    flow.setParentComponent(m_CurrentPanel);
    
    worker = new SwingWorker() {
      String msg = null;
      
      @Override
      protected Object doInBackground() throws Exception {
	msg = flow.setUp();
	if (msg != null)
	  msg = "Failed to setup flow for generating GIS display:\n" + msg;

	if (msg == null) {
	  msg = flow.execute();
	  if (msg != null)
	    msg = "Failed to execute flow for generating GIS display:\n" + msg;
	}

	if (msg == null) {
	  flow.wrapUp();
	  if (flow.hasStopMessage())
	    msg = "Flow execution for generating GIS display was stopped:\n" + flow.getStopMessage();
	}

        return msg;
      }
      
      @Override
      protected void done() {
        super.done();
        if (msg != null) {
          GUIHelper.showErrorMessage(m_CurrentPanel, msg);
          ConsolePanel.getSingleton().append(OutputType.ERROR, msg + "\n");
          ConsolePanel.getSingleton().append(OutputType.ERROR, flow.toCommandLine() + "\n");
          flow.destroy();
        }
        else {
          m_Panel.closeParent();
          m_CurrentPanel.addGeneratedFlow(flow);
        }
      }
    };
    
    worker.execute();
  }
}
