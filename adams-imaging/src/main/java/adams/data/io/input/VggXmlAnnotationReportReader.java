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
 * VggXmlAnnotationReportReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.conversion.XMLToDOM;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads XML image annotation files, like used in the pets dataset:<br>
 * http:&#47;&#47;www.robots.ox.ac.uk&#47;~vgg&#47;data&#47;pets&#47;
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class VggXmlAnnotationReportReader
  extends AbstractReportReader<Report> {

  private static final long serialVersionUID = -4823768127617381877L;

  /** the prefix of the objects in the report. */
  protected String m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads XML image annotation files, like used in the pets dataset:\n"
      + "http://www.robots.ox.ac.uk/~vgg/data/pets/";
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "VGG XML Annotation";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"xml"};
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
    String		xml;
    XMLToDOM		conv;
    String		msg;
    Document		dom;
    NodeList 		objects;
    Element		object;
    int			i;
    LocatedObject	lobj;
    LocatedObjects	lobjs;
    String		xmin;
    String		xmax;
    String		ymin;
    String		ymax;
    String		name;
    String		pose;
    String		truncated;
    String		occluded;

    result = new ArrayList<>();
    xml    = Utils.flatten(FileUtils.loadFromFile(m_Input), "\n");
    conv   = new XMLToDOM();
    conv.setInput(xml);
    msg = conv.convert();
    if (msg == null) {
      dom     = (Document) conv.getOutput();
      objects = dom.getElementsByTagName("object");
      lobjs   = new LocatedObjects();
      for (i = 0; i < objects.getLength(); i++) {
        try {
	  object    = (Element) objects.item(i);
	  xmin      = object.getElementsByTagName("xmin").item(0).getTextContent();
	  xmax      = object.getElementsByTagName("xmax").item(0).getTextContent();
	  ymin      = object.getElementsByTagName("ymin").item(0).getTextContent();
	  ymax      = object.getElementsByTagName("ymax").item(0).getTextContent();
	  name      = object.getElementsByTagName("name").item(0).getTextContent();
	  pose      = object.getElementsByTagName("pose").item(0).getTextContent();
	  truncated = object.getElementsByTagName("truncated").item(0).getTextContent();
	  occluded  = object.getElementsByTagName("occluded").item(0).getTextContent();
	  lobj = new LocatedObject(
	    null,
	    Integer.parseInt(xmin),
	    Integer.parseInt(ymin),
	    Integer.parseInt(xmax) - Integer.parseInt(xmin) + 1,
	    Integer.parseInt(ymax) - Integer.parseInt(ymin) + 1);
	  lobj.getMetaData().put("type", name);
	  lobj.getMetaData().put("pose", pose);
	  lobj.getMetaData().put("occluded", !occluded.equals("0"));
	  lobj.getMetaData().put("truncated", !truncated.equals("0"));
	  lobjs.add(lobj);
	}
	catch (Exception e) {
          getLogger().log(Level.SEVERE, "Failed to parse object #" + (i+1) + "!", e);
	}
      }
      result.add(lobjs.toReport(m_Prefix));
    }
    else {
      getLogger().severe("Failed to read " + m_Input + ":\n" + msg);
    }

    return result;
  }
}
