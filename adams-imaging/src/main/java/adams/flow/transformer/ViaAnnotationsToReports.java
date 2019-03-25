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
 * ViaAnnotationsToReports.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts the JSON object passing through to Reports.
 * <br><br>
 * <!-- globalinfo-end -->
 * <p>
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;net.minidev.json.JSONObject<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * <br><br>
 <!-- flow-summary-end -->
 * <p>
 * <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ViaAnnotationsToReports
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the Reports as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-default-label &lt;java.lang.String&gt; (property: defaultLabel)
 * &nbsp;&nbsp;&nbsp;Replaces empty labels with provided label. No default label if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-label-mapping &lt;adams.core.base.BaseKeyValuePair&gt; [-label-mapping ...] (property: labelMapping)
 * &nbsp;&nbsp;&nbsp;Label mapping, in the form old=new\n. No label mapping if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Hisham Abdel Qader (habdelqa at waikato dot ac dot nz)
 */
public class ViaAnnotationsToReports
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6162307113533583549L;

  /** the default label to replace empty labels. */
  protected String m_DefaultLabel;

  /** the label mappings to use, in the format old=new\n. */
  protected BaseKeyValuePair[] m_LabelMapping;

  /** the label mappings converted into a Map of (key,value) pairs. */
  protected Map<String,String> m_Mappings;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the JSON object passing through to Reports.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "default-label", "defaultLabel",
      "");

    m_OptionManager.add(
      "label-mapping", "labelMapping",
      new BaseKeyValuePair[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Mappings = null;
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Report.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the Reports as array or one-by-one.";
  }

  /**
   * Sets the default label.
   *
   * @param value 	the default label
   */
  public void setDefaultLabel(String value) {
    m_DefaultLabel = value;
    reset();
  }

  /**
   * Returns the default label.
   *
   * @return 		the default label
   */
  public String getDefaultLabel() {
    return m_DefaultLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultLabelTipText() {
    return "Replaces empty labels with provided label. No default label if empty.";
  }

  /**
   * Sets the label mappings.
   *
   * @param value 	the label mappings
   */
  public void setLabelMapping(BaseKeyValuePair[] value) {
    m_LabelMapping = value;
    reset();
  }

  /**
   * Returns the label mappings.
   *
   * @return 		the label mappings
   */
  public BaseKeyValuePair[] getLabelMapping() {
    return m_LabelMapping;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelMappingTipText() {
    return "Label mapping, in the form old=new\\n. No label mapping if empty.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    result  = QuickInfoHelper.toString(this, "defaultLabel", m_DefaultLabel, "default-label: ");
    if (result != null)
      result += ", ";
    else
      result = "";
    result += QuickInfoHelper.toString(this, "labelMapping", m_LabelMapping, "label-mapping: ");
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "output array"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JSONObject.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String result;
    JSONObject json;
    Report report;
    JSONObject metadata;
    JSONObject image;
    JSONArray regions;
    JSONObject region;
    JSONObject regionAtts;
    JSONObject shape;
    LocatedObjects objects;
    LocatedObject object;
    JSONArray pointsX;
    JSONArray pointsY;
    Polygon polygon;
    int[] x;
    int[] y;
    String label;

    result = null;
    json = m_InputToken.getPayload(JSONObject.class);

    // generate lookup table
    if (m_Mappings == null) {
      m_Mappings = new HashMap<>();
      for (BaseKeyValuePair pair: m_LabelMapping)
        m_Mappings.put(pair.getPairKey(), pair.getPairValue());
    }

    try {
      metadata = (JSONObject) json.get("_via_img_metadata");

      // generate a report per image
      for (String key : metadata.keySet()) {
	image = (JSONObject) metadata.get(key);
	regions = (JSONArray) image.get("regions");
	objects = new LocatedObjects();

	// add all annotated regions as objects in the report
	for (int i = 0; i < regions.size(); i++) {
	  region = (JSONObject) regions.get(i);
	  shape = (JSONObject) region.get("shape_attributes");
	  regionAtts = (JSONObject) region.get("region_attributes");

	  // add as polygon if available
	  if (shape.containsKey("all_points_x")) {
	    pointsX = (JSONArray) shape.get("all_points_x");
	    pointsY = (JSONArray) shape.get("all_points_y");
	    x = new int[pointsX.size()];
	    y = new int[pointsY.size()];
	    for (int p = 0; p < pointsX.size(); p++) {
	      x[p] = ((Number) pointsX.get(p)).intValue();
	      y[p] = ((Number) pointsY.get(p)).intValue();
	    }
	    polygon = new Polygon(x, y, x.length);
	    object = new LocatedObject(polygon);
	  }
	  // add as bbox only otherwise
	  else
	    object = new LocatedObject(shape.getAsNumber("x").intValue(), shape.getAsNumber("y").intValue(),
	      shape.getAsNumber("width").intValue(), shape.getAsNumber("height").intValue());

	  label = null;
	  // add label if available, or default label if provided to replace empty label
	  if ((regionAtts != null) && (regionAtts.get("name") != null))
	    label = "" + regionAtts.get("name");
	  else if (!m_DefaultLabel.isEmpty())
	    label = m_DefaultLabel;

	  // update label from mappings
	  if ((m_Mappings.size() > 0) && (label != null))
	    label = m_Mappings.getOrDefault(label, label);

	  if (label != null) object.getMetaData().put("type", label);
	  objects.add(object);
	}
	report = objects.toReport("Object.");
	report.setStringValue("Filename", image.getAsString("filename"));

	m_Queue.add(report);
      }
    } catch (Exception e) {
      result = handleException("Failed to parse VIA json object", e);
    }

    return result;
  }
}
