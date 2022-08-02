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
 * PascalVOCObjectLocationsReader.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.conversion.XMLToDOM;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads object locations in Pascal VOC format.<br>
 * <br>
 * See more:<br>
 * https:&#47;&#47;github.com&#47;WaikatoLink2020&#47;objdet-predictions-exchange-format
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
 * <pre>-id &lt;adams.data.report.Field&gt; (property: ID)
 * &nbsp;&nbsp;&nbsp;The field to use for storing the ID.
 * &nbsp;&nbsp;&nbsp;default: ID[S]
 * </pre>
 *
 * <pre>-timestamp &lt;adams.data.report.Field&gt; (property: timestamp)
 * &nbsp;&nbsp;&nbsp;The field to use for storing the timestamp.
 * &nbsp;&nbsp;&nbsp;default: Timestamp[S]
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report for the objects.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-label-suffix &lt;java.lang.String&gt; (property: labelSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix to use in the report for labels.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-meta-prefix &lt;java.lang.String&gt; (property: metaPrefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report for the meta-data.
 * &nbsp;&nbsp;&nbsp;default: Meta.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PascalVOCObjectLocationsReader
    extends AbstractReportReader<Report>
    implements ObjectPrefixHandler {

  private static final long serialVersionUID = -7100893374030214070L;

  /** the field to use for the ID. */
  protected Field m_ID;

  /** the field to use for the timestamp. */
  protected Field m_Timestamp;

  /** the prefix to use for objects. */
  protected String m_Prefix;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

  /** the prefix for the meta-data. */
  protected String m_MetaPrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads object locations in Pascal VOC format.\n\n"
	+ "See more:\n"
	+ "https://github.com/WaikatoLink2020/objdet-predictions-exchange-format";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"id", "ID",
	new Field("ID", DataType.STRING));

    m_OptionManager.add(
	"timestamp", "timestamp",
	new Field("Timestamp", DataType.STRING));

    m_OptionManager.add(
	"prefix", "prefix",
	"Object.");

    m_OptionManager.add(
	"label-suffix", "labelSuffix",
	"type");

    m_OptionManager.add(
	"meta-prefix", "metaPrefix",
	"Meta.");
  }

  /**
   * Sets the field to use for the ID.
   *
   * @param value 	the field
   */
  public void setID(Field value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the field to use for the ID.
   *
   * @return 		the field
   */
  public Field getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The field to use for storing the ID.";
  }

  /**
   * Sets the field to use for the timestamp.
   *
   * @param value 	the field
   */
  public void setTimestamp(Field value) {
    m_Timestamp = value;
    reset();
  }

  /**
   * Returns the field to use for the timestamp.
   *
   * @return 		the field
   */
  public Field getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timestampTipText() {
    return "The field to use for storing the timestamp.";
  }

  /**
   * Sets the field prefix used in the report for the objects.
   *
   * @param value 	the field prefix
   */
  @Override
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report for the objects.
   *
   * @return 		the field prefix
   */
  @Override
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String prefixTipText() {
    return "The report field prefix used in the report for the objects.";
  }

  /**
   * Sets the field suffix used in the report for labels.
   *
   * @param value 	the field suffix
   */
  public void setLabelSuffix(String value) {
    m_LabelSuffix = value;
    reset();
  }

  /**
   * Returns the field suffix used in the report for labels.
   *
   * @return 		the field suffix
   */
  public String getLabelSuffix() {
    return m_LabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelSuffixTipText() {
    return "The suffix to use in the report for labels.";
  }

  /**
   * Sets the field prefix used in the report for the meta-data.
   *
   * @param value 	the field prefix
   */
  public void setMetaPrefix(String value) {
    m_MetaPrefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report for the meta-data.
   *
   * @return 		the field prefix
   */
  public String getMetaPrefix() {
    return m_MetaPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaPrefixTipText() {
    return "The report field prefix used in the report for the meta-data.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Pascal VOC annotations";
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
   * Returns the (first) node.
   *
   * @param dom		the document to look for tag (picks first)
   * @param tag		the tag name
   * @return		the node, null if not found
   */
  protected Element getNode(Document dom, String tag) {
    Element	result;
    NodeList 	nodes;

    result = null;
    nodes = dom.getElementsByTagName(tag);
    if ((nodes.getLength() > 0) && (nodes.item(0) instanceof Element))
      result = (Element) nodes.item(0);

    return result;
  }

  /**
   * Returns the node's text.
   *
   * @param dom		the document to look for tag (picks first)
   * @param tag		the tag name
   * @return		the text, null if not found
   */
  protected String getNodeText(Document dom, String tag) {
    String 	result;
    Element	node;

    result = null;
    node   = getNode(dom, tag);
    if (node != null)
      result = node.getTextContent();

    return result;
  }

  /**
   * Returns the node from the specified sub-node.
   *
   * @param parent	the node to start from
   * @param tag		the sub-node tag to look for
   * @return		the sub-node, null if not found
   */
  protected Element getSubNode(Element parent, String tag) {
    Element	sub;
    int		i;

    for (i = 0; i < parent.getChildNodes().getLength(); i++) {
      if (parent.getChildNodes().item(i) instanceof Element) {
	sub = (Element) parent.getChildNodes().item(i);
	if (sub.getTagName().equals(tag))
	  return sub;
      }
    }

    return null;
  }

  /**
   * Returns the node text from the specified sub-node.
   *
   * @param parent	the node to start from
   * @param tag		the sub-node tag to look for
   * @return		the text, null if not found
   */
  protected String getSubNodeText(Element parent, String tag) {
    String	result;
    Element	sub;
    int		i;

    result = null;

    for (i = 0; i < parent.getChildNodes().getLength(); i++) {
      if (parent.getChildNodes().item(i) instanceof Element) {
	sub = (Element) parent.getChildNodes().item(i);
	if (sub.getTagName().equals(tag))
	  result = sub.getTextContent();
      }
    }

    return result;
  }

  /**
   * Adds the string value to the report.
   *
   * @param report	the report to update
   * @param name	the name of the field
   * @param value	the string value
   * @return		whether successfully added
   */
  protected boolean addString(Report report, String name, String value) {
    Field	field;

    if (value == null)
      return false;

    field = new Field(name, DataType.STRING);
    report.addField(field);
    report.setStringValue(field.getName(), value);
    return true;
  }

  /**
   * Adds the numeric value to the report.
   *
   * @param report	the report to update
   * @param name	the name of the field
   * @param value	the numeric value
   * @return		whether successfully added
   */
  protected boolean addNumeric(Report report, String name, String value) {
    Field	field;

    if (value == null)
      return false;

    field = new Field(name, DataType.NUMERIC);
    try {
      report.addField(field);
      report.setNumericValue(field.getName(), Double.parseDouble(value));
      return true;
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().warning("Failed to parse numeric value for field '" + name + "': " + value);
    }

    return false;
  }

  /**
   * Adds the boolean value to the report.
   *
   * @param report	the report to update
   * @param name	the name of the field
   * @param value	the numeric value
   * @return		whether successfully added
   */
  protected boolean addBoolean(Report report, String name, Boolean value) {
    Field	field;

    if (value == null)
      return false;

    field = new Field(name, DataType.BOOLEAN);
    report.addField(field);
    report.setBooleanValue(field.getName(), value);
    return true;
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report> 	result;
    String		xml;
    XMLToDOM		conv;
    String		msg;
    Document		dom;
    NodeList 		nodes;
    Element	 	node;
    Element		bbox;
    int			i;
    Report		report;
    LocatedObject	lobj;
    LocatedObjects	lobjs;
    int			xmin;
    int			xmax;
    int			ymin;
    int			ymax;
    String		id;
    String		segmented;
    String		height;
    String		width;
    String		depth;
    String		value;

    result = new ArrayList<>();
    xml    = Utils.flatten(FileUtils.loadFromFile(m_Input), "\n");
    conv   = new XMLToDOM();
    conv.setInput(xml);
    msg = conv.convert();
    if (msg == null) {
      lobjs = new LocatedObjects();
      dom = (Document) conv.getOutput();
      // ID
      id = getNodeText(dom, "filename");
      // meta
      node = getNode(dom, "size");
      height = getSubNodeText(node, "height");
      width = getSubNodeText(node, "width");
      depth = getSubNodeText(node, "depth");
      segmented = getNodeText(dom, "segmented");

      // objects
      nodes = dom.getElementsByTagName("object");
      for (i = 0; i < nodes.getLength(); i++) {
	if (!(nodes.item(i) instanceof Element))
	  continue;
	node = (Element) nodes.item(i);
	bbox = getSubNode(node, "bndbox");
	if (bbox == null)
	  continue;
	lobj = null;
	try {
	  xmin = Integer.parseInt(getSubNodeText(bbox, "xmin"));
	  xmax = Integer.parseInt(getSubNodeText(bbox, "xmax"));
	  ymin = Integer.parseInt(getSubNodeText(bbox, "ymin"));
	  ymax = Integer.parseInt(getSubNodeText(bbox, "ymax"));
	  lobj = new LocatedObject(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);
	  lobjs.add(lobj);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to parse coordinates!", e);
	  continue;
	}

	// meta
	value = getSubNodeText(node, "name");
	if (value != null)
	  lobj.getMetaData().put(m_LabelSuffix, value);
	value = getSubNodeText(node, "pose");
	if (value != null)
	  lobj.getMetaData().put("pose", value);
	value = getSubNodeText(node, "truncated");
	if (value != null)
	  lobj.getMetaData().put("truncated", value.equalsIgnoreCase("1"));
	value = getSubNodeText(node, "difficult");
	if (value != null)
	  lobj.getMetaData().put("difficult", value.equalsIgnoreCase("1"));
	value = getSubNodeText(node, "occluded");
	if (value != null)
	  lobj.getMetaData().put("occluded", value.equalsIgnoreCase("1"));
      }

      report = lobjs.toReport(m_Prefix);
      report.setValue(m_ID, id);
      addNumeric(report, m_MetaPrefix + "width", width);
      addNumeric(report, m_MetaPrefix + "height", height);
      addNumeric(report, m_MetaPrefix + "depth", depth);
      if (segmented != null)
        addBoolean(report, m_MetaPrefix + "segmented", segmented.equalsIgnoreCase("1"));
      result.add(report);
    }

    return result;
  }
}
