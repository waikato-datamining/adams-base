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
 * ViaAnnotations.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates JSON annotations for the VGG Image Annotator (VIA) from annotations contained in reports.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report[]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;net.minidev.json.JSONAware<br>
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
 * &nbsp;&nbsp;&nbsp;default: ViaAnnotations
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
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViaAnnotations
  extends AbstractTransformer {

  private static final long serialVersionUID = -2028204629498855331L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates JSON annotations for the VGG Image Annotator (VIA) from annotations contained in reports.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, Report[].class, ReportHandler.class, ReportHandler[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{JSONAware.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<Report> 	reports;
    ReportHandler[]	handlers;
    int			i;
    LocatedObjects	objs;
    int			n;
    LocatedObject	obj;
    JSONObject		all;
    Report		rep;
    JSONObject 		jrep;
    JSONObject		jregions;
    JSONObject		jregion;
    JSONObject		jshape;
    JSONArray 		jpoints;
    String		name;

    result  = null;
    reports = new ArrayList<>();
    if (m_InputToken.hasPayload(Report.class)) {
      reports.add(m_InputToken.getPayload(Report.class));
    }
    else if (m_InputToken.hasPayload(Report[].class)) {
      reports.addAll(Arrays.asList(m_InputToken.getPayload(Report[].class)));
    }
    else if (m_InputToken.hasPayload(ReportHandler.class)) {
      reports.add(m_InputToken.getPayload(ReportHandler.class).getReport());
    }
    else if (m_InputToken.hasPayload(ReportHandler[].class)) {
      handlers = m_InputToken.getPayload(ReportHandler[].class);
      for (ReportHandler handler: handlers)
	reports.add(handler.getReport());
    }
    else {
      result = m_InputToken.unhandledData();
    }

    if (result == null) {
      all = new JSONObject();
      for (i = 0; i < reports.size(); i++) {
        rep  = reports.get(i);
        jrep = new JSONObject();
        jrep.put("fileref", "");
        jrep.put("size", 0);
        jrep.put("base64_img_data", "");
        jrep.put("file_attributes", new JSONObject());
        jregions = new JSONObject();
        jrep.put("regions", jregions);

        // get filename
        name = "" + i;  // fallback
        if (rep.hasValue("Name"))
          name = rep.getStringValue("Name");
        else if (rep.hasValue("Filename"))
          name = rep.getStringValue("Filename");
        jrep.put("filename", name);
        all.put(name + "-" + i, jrep);

        // iterate objects
        objs = m_Finder.findObjects(rep);
	for (n = 0; n < objs.size(); n++) {
	  obj     = objs.get(n);
	  jregion = new JSONObject();
	  jregions.put("" + n, jregion);
	  jshape = new JSONObject();
	  jregion.put("shape_attributes", jshape);
	  jregion.put("region_attributes", new JSONObject());
	  jshape.put("name", "polygon");
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
      m_OutputToken = new Token(all);
    }

    return result;
  }
}
