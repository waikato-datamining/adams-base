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
 * DetectronAnnotationsReportReader.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.annotation.DeprecatedClass;
import adams.core.io.FileUtils;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Interprets rectangle annotations present in Detectron annotations JSON file.
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
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Hisham (habdelqa at waikato dot ac dot nz)
 */
@DeprecatedClass(
  useInstead = {CocoAnnotationsReportReader.class}
)
public class DetectronAnnotationsReportReader
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
    return "Interprets rectangle annotations present in Detectron annotations JSON file.\n";
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
      "");
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
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report>	result;
    JSONParser 		parser;
    FileReader 		freader;
    BufferedReader 	breader;
    LocatedObject	lobj;
    LocatedObjects 	lobjs;
    JSONObject		obj;
    JSONObject		img;
    JSONArray 		categories;
    JSONArray 		images;
    JSONArray 		annotations;
    String		fname;
    JSONObject		region;
    JSONObject 		category;
    JSONArray 		bbox;
    String		label;
    String[] 		labels;
    double x;
    double y;
    double width;
    double height;

    result  = new ArrayList<>();
    freader = null;
    breader = null;
    try {
      freader = new FileReader(m_Input.getAbsolutePath());
      breader = new BufferedReader(freader);
      parser  = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      obj     = (JSONObject) parser.parse(breader);
      categories = (JSONArray) obj.get("categories");
      images = (JSONArray) obj.get("images");
      annotations = (JSONArray) obj.get("annotations");
      labels = new String[categories.size()];
      for (int ca = 0; ca < categories.size(); ca++) {
	category = (JSONObject) categories.get(ca);
	labels[ca] = category.getAsString("name");
      }
      for (int im = 0; im < images.size(); im++) {
	img     = (JSONObject) images.get(im);
	fname   = img.getAsString("file_name");
	lobjs   = new LocatedObjects();
	for (int an = 0; an < annotations.size(); an++) {
	  region     = (JSONObject) annotations.get(an);
	  if ( region.getAsNumber("image_id").intValue() == im+1) {
	    // add bbox
	    bbox = (JSONArray) region.get("bbox");
	    x = (double) bbox.get(0);
	    y = (double) bbox.get(1);
	    width = (double) bbox.get(2);
	    height = (double) bbox.get(3);
	    lobj = new LocatedObject((int) x, (int) y, (int) width, (int) height);
	    label = null;
	    // add label if available
	    if (region.getAsNumber("category_id") != null)
	      label = labels[region.getAsNumber("category_id").intValue() - 1];
	    lobj.getMetaData().put("filename", fname);
	    if (label != null) lobj.getMetaData().put(m_LabelKey, label);
	    lobjs.add(lobj);
	  }
	}
	result.add(lobjs.toReport(m_Prefix));
      }
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
