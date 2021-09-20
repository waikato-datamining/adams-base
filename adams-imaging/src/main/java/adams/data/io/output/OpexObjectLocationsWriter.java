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
 * OpexObjectLocationsWriter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.io.FileUtils;
import adams.core.io.PrettyPrintingSupporter;
import adams.data.io.input.OpexObjectLocationsReader;
import adams.data.io.input.ViaAnnotationsReportReader;
import adams.data.json.JsonHelper;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.Date;

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
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-score-key &lt;java.lang.String&gt; (property: scoreKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the sore, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: score
 * </pre>
 *
 * <pre>-meta-prefix &lt;java.lang.String&gt; (property: metaPrefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report for the meta-data.
 * &nbsp;&nbsp;&nbsp;default: Meta.
 * </pre>
 *
 * <pre>-pretty-printing &lt;boolean&gt; (property: prettyPrinting)
 * &nbsp;&nbsp;&nbsp;If enabled, the output is printed in a 'pretty' format.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpexObjectLocationsWriter
    extends AbstractReportWriter<Report>
    implements PrettyPrintingSupporter {

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the field with the timestamp. */
  protected Field m_Timestamp;

  /** the meta-data key with the ID. */
  protected Field m_ID;

  /** the meta-data key with the label. */
  protected String m_LabelKey;

  /** the meta-data key with the score. */
  protected String m_ScoreKey;

  /** the prefix for the meta-data. */
  protected String m_MetaPrefix;

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
	"id", "ID",
	new Field("ID", DataType.STRING));

    m_OptionManager.add(
	"timestamp", "timestamp",
	new Field("Timestamp", DataType.STRING));

    m_OptionManager.add(
	"label-key", "labelKey",
	"type");

    m_OptionManager.add(
	"score-key", "scoreKey",
	"score");

    m_OptionManager.add(
	"meta-prefix", "metaPrefix",
	"Meta.");

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
   * Sets the key in the meta-data containing the score.
   *
   * @param value	the key
   */
  public void setScoreKey(String value) {
    m_ScoreKey = value;
    reset();
  }

  /**
   * Returns the key in the meta-data containing the score.
   *
   * @return		the key
   */
  public String getScoreKey() {
    return m_ScoreKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scoreKeyTipText() {
    return "The key in the meta-data containing the sore, ignored if empty.";
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
    return new OpexObjectLocationsReader().getFormatDescription();
  }

  /**
   * Returns the extension of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new OpexObjectLocationsReader().getFormatExtensions();
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
    JSONObject 		all;
    JSONObject 		meta;
    JSONArray 		objects;
    JSONObject 		object;
    JSONObject 		bbox;
    JSONObject 		polygon;
    JSONArray		points;
    JSONArray		point;
    int[]		x;
    int[]		y;
    DateFormat		dformat;
    String		content;

    dformat = DateUtils.getTimestampFormatterMsecs();
    all     = new JSONObject();

    // ID
    if (data.hasField(m_ID))
      all.put("id", "" + data.getValue(m_ID));
    else
      all.put("id", dformat.format(new Date()));

    // timestamp
    if (data.hasField(m_Timestamp))
      all.put("timestamp", "" + data.getValue(m_Timestamp));

    // meta
    meta = new JSONObject();
    for (AbstractField field: data.getFields()) {
      if (field.getName().startsWith(m_MetaPrefix))
	meta.put(field.getName().substring(m_MetaPrefix.length()), "" + data.getValue(field));
    }
    if (meta.size() > 0)
      all.put("meta", meta);

    // objects
    objs    = m_Finder.findObjects(data);
    objects = new JSONArray();
    for (LocatedObject obj: objs) {
      object = new JSONObject();
      objects.add(object);
      // label
      if (!m_LabelKey.isEmpty() && obj.getMetaData().containsKey(m_LabelKey))
	object.put("label", "" + obj.getMetaData().get(m_LabelKey));
      else
	object.put("label", "-no-label-");
      // score
      if (!m_ScoreKey.isEmpty() && obj.getMetaData().containsKey(m_ScoreKey))
	object.put("score", obj.getMetaData().get(m_ScoreKey));
      // bbox
      bbox = new JSONObject();
      bbox.put("left", obj.getX());
      bbox.put("top", obj.getY());
      bbox.put("right", obj.getX() + obj.getWidth() - 1);
      bbox.put("bottom", obj.getY() + obj.getHeight() - 1);
      object.put("bbox", bbox);
      // polygon
      polygon = new JSONObject();
      points  = new JSONArray();
      if (obj.hasPolygon()) {
	x = obj.getPolygonX();
	y = obj.getPolygonY();
      }
      else {
	x = new int[]{obj.getX(), obj.getX() + obj.getWidth() - 1, obj.getX() + obj.getWidth() - 1, obj.getX()};
	y = new int[]{obj.getY(), obj.getY(), obj.getY() + obj.getHeight() - 1, obj.getY() + obj.getHeight() - 1};
      }
      for (n = 0; n < x.length; n++) {
	point = new JSONArray();
	point.add(x[n]);
	point.add(y[n]);
	points.add(point);
      }
      polygon.put("points", points);
      object.put("polygon", polygon);
    }
    all.put("objects", objects);

    if (m_PrettyPrinting)
      content = JsonHelper.prettyPrint(all.toString());
    else
      content = all.toString();

    return FileUtils.writeToFile(m_Output.getAbsolutePath(), content, false);
  }
}
