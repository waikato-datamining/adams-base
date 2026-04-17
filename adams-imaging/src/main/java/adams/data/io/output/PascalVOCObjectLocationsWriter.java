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
 * PascalVOCObjectLocationsWriter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.PascalVOCObjectLocationsReader;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Writes the annotations in Pascal VOC format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the report to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.xml
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use for selecting a subset of objects before generating
 * &nbsp;&nbsp;&nbsp;the output.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the image to use when reading normalized coordinates&#47;dimensions.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the image to use when reading normalized coordinates&#47;dimensions.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-image &lt;adams.core.io.PlaceholderFile&gt; (property: image)
 * &nbsp;&nbsp;&nbsp;The corresponding image. If pointing to a directory, path and name will
 * &nbsp;&nbsp;&nbsp;be guessed.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PascalVOCObjectLocationsWriter
  extends AbstractReportWriter<Report> {

  private static final long serialVersionUID = -6320320740259427005L;

  /** the row finder to apply before extracting the objects. */
  protected ObjectFinder m_Finder;

  /** the image width to use as basis for normalized coordinates/dimensions. */
  protected int m_Width;

  /** the image height to use as basis for normalized coordinates/dimensions. */
  protected int m_Height;

  /** the corresponding image. */
  protected PlaceholderFile m_Image;

  /** the meta-data key with the label. */
  protected String m_LabelKey;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes the annotations in Pascal VOC format.";
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
      "width", "width",
      640, 1, null);

    m_OptionManager.add(
      "height", "height",
      480, 1, null);

    m_OptionManager.add(
      "image", "image",
      new PlaceholderFile());

    m_OptionManager.add(
      "label-key", "labelKey",
      "type");
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new PascalVOCObjectLocationsReader().getFormatDescription();
  }

  /**
   * Returns the extension of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new PascalVOCObjectLocationsReader().getFormatExtensions();
  }

  /**
   * Sets the object finder to use.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the object finder to use.
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
    return "The object finder to use for selecting a subset of objects before generating the output.";
  }

  /**
   * Sets the width of the image to use when reading normalized coordinates/dimensions.
   *
   * @param value	the image width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width of the image to use when reading normalized coordinates/dimensions.
   *
   * @return		the image width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the image to use when reading normalized coordinates/dimensions.";
  }

  /**
   * Sets the height of the image to use when reading normalized coordinates/dimensions.
   *
   * @param value	the image height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height of the image to use when reading normalized coordinates/dimensions.
   *
   * @return		the image height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the image to use when reading normalized coordinates/dimensions.";
  }

  /**
   * Sets the corresponding image.
   *
   * @param value	the corresponding image.
   */
  public void setImage(PlaceholderFile value) {
    m_Image = value;
    reset();
  }

  /**
   * Returns the corresponding image.
   *
   * @return		the corresponding image
   */
  public PlaceholderFile getImage() {
    return m_Image;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTipText() {
    return "The corresponding image. If pointing to a directory, path and name will be guessed.";
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
   * Performs the actual writing.
   *
   * @param data the data to write
   * @return true if successfully written
   */
  @Override
  protected boolean writeData(Report data) {
    String		path;
    String 		image;
    StringBuilder	xml;
    LocatedObjects	objs;
    String		msg;

    if (m_Image.isDirectory()) {
      path  = m_Output.getParentFile().getAbsolutePath();
      image = FileUtils.replaceExtension(m_Output, ".jpg").getName();
    }
    else {
      path  = m_Image.getParentFile().getAbsolutePath();
      image = m_Image.getName();
    }

    objs = m_Finder.findObjects(data);

    xml = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n");
    xml.append("<annotation>\n");
    xml.append("  <folder>").append(path).append("</folder>\n");
    xml.append("  <filename>").append(image).append("</filename>\n");
    xml.append("  <path>").append(path).append("/").append(image).append("</path>\n");
    xml.append("  <source>\n");
    xml.append("    <database>Unknown</database>\n");
    xml.append("  </source>\n");
    xml.append("  <size>\n");
    xml.append("    <width>").append(m_Width).append("</width>\n");
    xml.append("    <height>").append(m_Height).append("</height>\n");
    xml.append("    <depth>3</depth>\n");
    xml.append("  </size>\n");
    xml.append("  <segmented>0</segmented>\n");
    for (LocatedObject obj: objs) {
      xml.append("  <object>\n");
      xml.append("    <name>").append(obj.getMetaData().getOrDefault(m_LabelKey, "object")).append("</name>\n");
      xml.append("    <pose>Unspecified</pose>\n");
      xml.append("    <truncated>0</truncated>\n");
      xml.append("    <difficult>0</difficult>\n");
      xml.append("    <bndbox>\n");
      xml.append("      <xmin>").append(obj.getX()).append("</xmin>\n");
      xml.append("      <ymin>").append(obj.getY()).append("</ymin>\n");
      xml.append("      <xmax>").append(obj.getX() + obj.getWidth() - 1).append("</xmax>\n");
      xml.append("      <ymax>").append(obj.getY() + obj.getHeight() - 1).append("</ymax>\n");
      xml.append("    </bndbox>\n");
      xml.append("  </object>\n");
    }
    xml.append("</annotation>\n");

    msg = FileUtils.writeToFileMsg(m_Output.getAbsolutePath(), xml, false, null);
    if (msg != null)
      getLogger().warning(msg);

    return (msg == null);
  }
}
