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
 * ViaAnnotationsReportWriter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.io.PrettyPrintingSupporter;
import adams.data.io.input.ViaAnnotationsReportReader;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.awt.Polygon;

/**
 <!-- globalinfo-start -->
 * Writes polygon annotations in VGG Image Annotator JSON format.<br>
 * For more information, see:<br>
 * http:&#47;&#47;www.robots.ox.ac.uk&#47;~vgg&#47;software&#47;via&#47;
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the report to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.json
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-pretty-printing &lt;boolean&gt; (property: prettyPrinting)
 * &nbsp;&nbsp;&nbsp;If enabled, the output is printed in a 'pretty' format.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViaAnnotationsReportWriter
  extends AbstractReportWriter<Report>
  implements PrettyPrintingSupporter {

  private static final long serialVersionUID = -7250784020894287952L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the meta-data key with the label. */
  protected String m_LabelKey;

  /** whether to use pretty-printing. */
  protected boolean m_PrettyPrinting;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes polygon annotations in VGG Image Annotator JSON format.\n"
      + "For more information, see:\n"
      + "http://www.robots.ox.ac.uk/~vgg/software/via/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "label-key", "labelKey",
      "");

    m_OptionManager.add(
      "pretty-printing", "prettyPrinting",
      false);
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
   *
   * @return		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use.";
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
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, the output is printed in a 'pretty' format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new ViaAnnotationsReportReader().getFormatDescription();
  }

  /**
   * Returns the extension of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new ViaAnnotationsReportReader().getFormatExtensions();
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(Report data) {
    LocatedObjects 	objs;
    int			n;
    LocatedObject 	obj;
    JSONObject		all;
    JSONObject 		jrep;
    JSONObject		jregions;
    JSONObject		jregion;
    JSONObject		jshape;
    JSONArray 		jpoints;
    JSONObject		jatts;
    String		name;
    Polygon		polygon;
    int[]		x;
    int[]		y;
    int			i;
    String		content;
    Gson 		gson;
    JsonParser 		jp;
    JsonElement 	je;

    all = new JSONObject();
    jrep = new JSONObject();
    jrep.put("fileref", "");
    jrep.put("size", 0);
    jrep.put("base64_img_data", "");
    jrep.put("file_attributes", new JSONObject());
    jregions = new JSONObject();
    jrep.put("regions", jregions);

    // get filename
    name = m_Output.getName();  // fallback
    if (data.hasValue("Name"))
      name = data.getStringValue("Name");
    else if (data.hasValue("Filename"))
      name = data.getStringValue("Filename");
    jrep.put("filename", name);
    all.put(name, jrep);

    // iterate objects
    objs = m_Finder.findObjects(data);
    for (n = 0; n < objs.size(); n++) {
      obj     = objs.get(n);
      jregion = new JSONObject();
      jregions.put("" + n, jregion);
      jshape = new JSONObject();
      jregion.put("shape_attributes", jshape);
      jatts = new JSONObject();
      jregion.put("region_attributes", jatts);
      if (!m_LabelKey.isEmpty() && (obj.getMetaData().get(m_LabelKey) != null))
        jatts.put("name", obj.getMetaData().get(m_LabelKey));
      jshape.put("name", "polygon");
      if (obj.hasPolygon()) {
        polygon = obj.getPolygon();
	// x
	jpoints = new JSONArray();
	x       =  polygon.xpoints;
	for (i = 0; i < x.length; i++)
	  jpoints.add(x[i]);
	jshape.put("all_points_x", jpoints);
	// y
	jpoints = new JSONArray();
	y       = polygon.ypoints;
	for (i = 0; i < y.length; i++)
	  jpoints.add(y[i]);
	jshape.put("all_points_y", jpoints);
      }
      else {
	// x
	jpoints = new JSONArray();
	jpoints.add(obj.getX());
	jpoints.add(obj.getX() + obj.getWidth() - 1);
	jpoints.add(obj.getX() + obj.getWidth() - 1);
	jpoints.add(obj.getX());
	jshape.put("all_points_x", jpoints);
	// y
	jpoints = new JSONArray();
	jpoints.add(obj.getY());
	jpoints.add(obj.getY());
	jpoints.add(obj.getY() + obj.getHeight() - 1);
	jpoints.add(obj.getY() + obj.getHeight() - 1);
	jshape.put("all_points_y", jpoints);
      }
    }

    if (m_PrettyPrinting) {
      gson    = new GsonBuilder().setPrettyPrinting().create();
      jp      = new JsonParser();
      je      = jp.parse(all.toString());
      content = gson.toJson(je);
    }
    else {
      content = all.toString();
    }

    return FileUtils.writeToFile(m_Output.getAbsolutePath(), content, false);
  }
}
