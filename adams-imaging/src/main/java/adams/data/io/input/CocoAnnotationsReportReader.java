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
 * CocoAnnotationsReportReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileUtils;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Loads COCO annotations from the JSON file, with one report per image.<br>
 * Handles only segmentations with polygons (not RLE) and only one polygon per annotation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a report.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Hisham (habdelqa at waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CocoAnnotationsReportReader
  extends AbstractReportReader<Report> {

  private static final long serialVersionUID = 5716807404370681434L;

  /** the prefix of the objects in the report. */
  protected String m_Prefix;

  /** the meta-data key with the label. */
  protected String m_LabelKey;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads COCO annotations from the JSON file, with one report per image.\n"
      + "Handles only segmentations with polygons (not RLE) and only one polygon per annotation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);

    m_OptionManager.add(
      "label-key", "labelKey",
      "type");
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Sets the key in the meta-data containing the label.
   *
   * @param value	the key
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the key in the meta-data containing the label.
   *
   * @return		the key
   */
  public String getLabelKey() {
    return m_LabelKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelKeyTipText() {
    return "The key in the meta-data containing the label, ignored if empty.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Detectron Annotations JSON";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"json"};
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return -1;
  }

  /**
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  @Override
  public Report newInstance() {
    return new Report();
  }

  /**
   * Turns the categories JSON array into a lookup table for category ID -> label.
   *
   * @param categories	the array to process
   * @return		the mapping
   */
  protected Map<Integer,String> loadCategories(JSONArray categories) {
    Map<Integer,String>	result;
    int 		i;
    JSONObject		category;

    result = new HashMap<>();
    for (i = 0; i < categories.size(); i++) {
      category = (JSONObject) categories.get(i);
      result.put(
        category.getAsNumber("id").intValue(),
	category.getAsString("name"));
    }

    return result;
  }

  /**
   * Adds the specified value to the report.
   *
   * @param report	the report to update
   * @param key		the field name
   * @param value	the value (integer, double, boolean, string)
   */
  protected void addValue(Report report, String key, Object value) {
    Field	field;

    if (value instanceof Number)
      field = new Field(key, DataType.NUMERIC);
    else if (value instanceof Boolean)
      field = new Field(key, DataType.BOOLEAN);
    else if (value instanceof String)
      field = new Field(key, DataType.STRING);
    else
      field = new Field(key, DataType.UNKNOWN);

    // skip empty strings
    if (value instanceof String) {
      if (((String) value).isEmpty())
        return;
    }

    report.addField(field);
    report.setValue(field, value);
  }

  /**
   * Turns the images JSON array into a lookup table for image ID -> Report.
   *
   * @param images	the array to process
   * @return		the mapping
   */
  protected Map<Integer,Report> loadImages(JSONArray images) {
    Map<Integer,Report>	result;
    int 		i;
    JSONObject 		image;
    Report		report;

    result = new HashMap<>();
    for (i = 0; i < images.size(); i++) {
      image  = (JSONObject) images.get(i);
      report = new Report();
      addValue(report, "Width", image.getAsNumber("width").intValue());
      addValue(report, "Height", image.getAsNumber("height").intValue());
      addValue(report, "Filename", image.getAsString("file_name"));
      addValue(report, "Date captured", image.getAsString("date_captured"));
      result.put(image.getAsNumber("id").intValue(), report);
    }

    return result;
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report>			result;
    JSONParser 				parser;
    FileReader 				freader;
    BufferedReader 			breader;
    JSONObject				obj;
    Map<Integer,String> 		categories;
    Map<Integer,Report>			images;
    Map<Integer,LocatedObjects>		annotations;
    JSONArray 				annotationsArray;
    JSONObject				region;
    JSONArray 				bbox;
    double 				x;
    double 				y;
    double 				width;
    double 				height;
    int					i;
    LocatedObject			object;
    int					imageID;
    int					labelID;
    TIntList				polyX;
    TIntList				polyY;
    JSONArray				poly;
    int					n;
    List<Integer>			ids;

    result  = new ArrayList<>();
    freader = null;
    breader = null;
    try {
      freader          = new FileReader(m_Input.getAbsolutePath());
      breader          = new BufferedReader(freader);
      parser           = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      obj              = (JSONObject) parser.parse(breader);
      categories       = loadCategories((JSONArray) obj.get("categories"));
      images           = loadImages((JSONArray) obj.get("images"));
      annotationsArray = (JSONArray) obj.get("annotations");
      annotations      = new HashMap<>();
      for (i = 0; i < annotationsArray.size(); i++) {
	region  = (JSONObject) annotationsArray.get(i);
	imageID = region.getAsNumber("image_id").intValue();
	labelID = region.getAsNumber("category_id").intValue();

	// bounding box
	bbox   = (JSONArray) region.get("bbox");
	x      = (double) bbox.get(0);
	y      = (double) bbox.get(1);
	width  = (double) bbox.get(2);
	height = (double) bbox.get(3);

	// create object
	object = new LocatedObject((int) x, (int) y, (int) width, (int) height);
	if (!annotations.containsKey(imageID))
	  annotations.put(imageID, new LocatedObjects());
	annotations.get(imageID).add(object);

	// label
	if (categories.containsKey(labelID))
	  object.getMetaData().put(m_LabelKey, categories.get(labelID));

	// polygon (iscrowd=0)
	if (region.containsKey("segmentation") && region.containsKey("iscrowd") && (region.getAsNumber("iscrowd").intValue() == 0)) {
	  poly = (JSONArray) region.get("segmentation");
	  // we can only handle one polygon per annotation
	  if (poly.size() >= 1) {
	    poly  = (JSONArray) poly.get(0);
	    polyX = new TIntArrayList();
	    polyY = new TIntArrayList();
	    for (n = 0; n < poly.size(); n += 2) {
	      polyX.add(((Double) poly.get(n)).intValue());
	      polyY.add(((Double) poly.get(n+1)).intValue());
	    }
	    object.setPolygon(new Polygon(polyX.toArray(), polyY.toArray(), polyX.size()));
	  }
	}
      }

      // add annotations to reports
      for (Integer id: images.keySet()) {
        if (annotations.containsKey(id))
          images.get(id).mergeWith(annotations.get(id).toReport(m_Prefix));
      }

      // generate output
      ids = new ArrayList<>(images.keySet());
      Collections.sort(ids);
      for (Integer id: ids)
        result.add(images.get(id));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read JSON file: " + m_Input, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }

    return result;
  }
}
