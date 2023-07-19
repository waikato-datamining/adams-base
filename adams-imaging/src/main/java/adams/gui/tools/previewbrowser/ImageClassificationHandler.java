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
 * ImageClassificationHandler.java
 * Copyright (C) 2022-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.ObjectCopyHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.ImageClassificationSpreadSheetReportReader;
import adams.data.io.input.ImageReader;
import adams.data.io.input.JAIImageReader;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.core.Fonts;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.MetaDataText;
import adams.gui.visualization.object.objectannotations.AnnotationUtils;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Overlays the annotations onto the image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-image-reader &lt;adams.data.io.input.AbstractImageReader&gt; (property: imageReader)
 * &nbsp;&nbsp;&nbsp;The image reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.JAIImageReader
 * </pre>
 *
 * <pre>-file-suffix &lt;java.lang.String&gt; (property: fileSuffix)
 * &nbsp;&nbsp;&nbsp;The forced suffix (incl ext) to append to the image name for generating
 * &nbsp;&nbsp;&nbsp;the meta-data file name.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader setup to use for reading the object locations from the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 *
 * <pre>-field-label &lt;adams.data.report.Field&gt; (property: fieldLabel)
 * &nbsp;&nbsp;&nbsp;The field to store the label under.
 * &nbsp;&nbsp;&nbsp;default: Classification[S]
 * </pre>
 *
 * <pre>-field-score &lt;adams.data.report.Field&gt; (property: fieldScore)
 * &nbsp;&nbsp;&nbsp;The field to store the score under.
 * &nbsp;&nbsp;&nbsp;default: Score[N]
 * </pre>
 *
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The label format string to use for the rectangles; '&#64;' for type and '$'
 * &nbsp;&nbsp;&nbsp;for short type, '{score}' gets replaced with the score (if present); for
 * &nbsp;&nbsp;&nbsp;instance: '&#64;' or '{score}'; in case of numeric values, use '|.X' to limit
 * &nbsp;&nbsp;&nbsp;the number of decimals, eg '{score|.2}' for a maximum of decimals after
 * &nbsp;&nbsp;&nbsp;the decimal point.
 * &nbsp;&nbsp;&nbsp;default: $
 * </pre>
 *
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the text.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 * <pre>-offset-x &lt;int&gt; (property: offsetX)
 * &nbsp;&nbsp;&nbsp;The X offset for the label; values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses left as anchor, -2 the center and -3 the right.
 * &nbsp;&nbsp;&nbsp;default: 20
 * </pre>
 *
 * <pre>-offset-y &lt;int&gt; (property: offsetY)
 * &nbsp;&nbsp;&nbsp;The Y offset for the label values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.
 * &nbsp;&nbsp;&nbsp;default: 20
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageClassificationHandler
  extends AbstractContentHandler {

  private static final long serialVersionUID = -6655562227841341465L;

  /** the file suffix to force (incl extension). */
  protected String m_FileSuffix;

  /** the image reader to use. */
  protected ImageReader m_ImageReader;

  /** the reader to use. */
  protected AbstractReportReader m_Reader;

  /** the report field to get the label from. */
  protected Field m_FieldLabel;

  /** the report file to get the score from. */
  protected Field m_FieldScore;

  /** the label for the label. */
  protected String m_Format;

  /** the label font. */
  protected Font m_Font;

  /** the color of the text. */
  protected Color m_Color;

  /** the x offset for the label. */
  protected int m_OffsetX;

  /** the y offset for the label. */
  protected int m_OffsetY;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays the annotations onto the image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-reader", "imageReader",
      getDefaultImageReader());

    m_OptionManager.add(
      "file-suffix", "fileSuffix",
      "");

    m_OptionManager.add(
      "reader", "reader",
      getDefaultReader());

    m_OptionManager.add(
      "field-label", "fieldLabel",
      new Field("Classification", DataType.STRING));

    m_OptionManager.add(
      "field-score", "fieldScore",
      new Field("Score", DataType.NUMERIC));

    m_OptionManager.add(
      "format", "format",
      "$");

    m_OptionManager.add(
      "font", "font",
      Fonts.getSansFont(14));

    m_OptionManager.add(
      "color", "color",
      Color.RED);

    m_OptionManager.add(
      "offset-x", "offsetX",
      20);

    m_OptionManager.add(
      "offset-y", "offsetY",
      40);
  }

  /**
   * Returns the default image reader.
   *
   * @return		the default
   */
  protected ImageReader getDefaultImageReader() {
    return new JAIImageReader();
  }

  /**
   * Sets the image reader to use.
   *
   * @param value	the reader
   */
  public void setImageReader(ImageReader value) {
    m_ImageReader = value;
    reset();
  }

  /**
   * Returns the image reader to use.
   *
   * @return		the reader
   */
  public ImageReader getImageReader() {
    return m_ImageReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageReaderTipText() {
    return "The image reader to use.";
  }

  /**
   * Returns the default reader.
   *
   * @return		the reader
   */
  protected AbstractReportReader getDefaultReader() {
    return new ImageClassificationSpreadSheetReportReader();
  }

  /**
   * Sets the forced suffix (incl ext) to append to the image name for generating the meta-data file name.
   *
   * @param value 	the suffix
   */
  public void setFileSuffix(String value) {
    m_FileSuffix = value;
    reset();
  }

  /**
   * Returns the forced suffix (incl ext) to append to the image name for generating the meta-data file name.
   *
   * @return 		the suffix
   */
  public String getFileSuffix() {
    return m_FileSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileSuffixTipText() {
    return "The forced suffix (incl ext) to append to the image name for generating the meta-data file name.";
  }

  /**
   * Sets the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @param value 	the reader
   */
  public void setReader(AbstractReportReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @return 		the reader
   */
  public AbstractReportReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader setup to use for reading the object locations from the spreadsheet.";
  }

  /**
   * Sets the field for the label.
   *
   * @param value	the field
   */
  public void setFieldLabel(Field value) {
    m_FieldLabel = value;
    reset();
  }

  /**
   * Returns the field for the label.
   *
   * @return		the field
   */
  public Field getFieldLabel() {
    return m_FieldLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldLabelTipText() {
    return "The field to store the label under.";
  }

  /**
   * Sets the field for the score.
   *
   * @param value	the field
   */
  public void setFieldScore(Field value) {
    m_FieldScore = value;
    reset();
  }

  /**
   * Returns the field for the score.
   *
   * @return		the field
   */
  public Field getFieldScore() {
    return m_FieldScore;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldScoreTipText() {
    return "The field to store the score under.";
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The label format string to use for the rectangles; "
      + "'@' for type and '$' for short type, '{score}' gets replaced with the "
      + "score (if present); "
      + "for instance: '@' or '{score}'; in case of numeric values, use '|.X' "
      + "to limit the number of decimals, eg '{score|.2}' for a maximum of decimals "
      + "after the decimal point.";
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the labels.";
  }

  /**
   * Sets the color of the text.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color of the text.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color of the text.";
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setOffsetX(int value) {
    m_OffsetX = value;
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getOffsetX() {
    return m_OffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetXTipText() {
    return "The X offset for the label; values of 0 or greater are interpreted as absolute pixels, -1 uses left as anchor, -2 the center and -3 the right.";
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setOffsetY(int value) {
    m_OffsetY = value;
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getOffsetY() {
    return m_OffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetYTipText() {
    return "The Y offset for the label values of 0 or greater are interpreted as absolute pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return getImageReader().getFormatExtensions();
  }

  /**
   * Applies the label format to the object to generate a display string.
   *
   * @param label	the label to use
   * @param score 	the score to use, ignored if null
   * @param labelFormat	the label format to use
   * @return		the generated label
   */
  protected String applyLabelFormat(String label, Double score, String labelFormat) {
    String 	result;
    String	type;
    String	key;
    String	value;
    int		start;
    int		end;
    String	format;

    result = labelFormat
      .replace("@", label)
      .replace("$", label.replaceAll(".*\\.", ""));

    // meta-data
    while (((start = result.indexOf("{")) > -1) && ((end = result.indexOf("}", start)) > -1)) {
      key    = result.substring(start + 1, end);
      format = "";
      if (key.contains("|")) {
	format = key.substring(key.indexOf("|") + 1);
	key    = key.substring(0, key.indexOf("|"));
      }
      if ((key.equals("score")) && (score != null))
	value = "" + score;
      else
	value = "";
      value = AnnotationUtils.applyFormatOptions(value, format);
      result = result.substring(0, start) + value + result.substring(end + 1);
    }

    return result;
  }

  /**
   * Loads the report associated with the image.
   *
   * @param panel 	the context panel
   * @param file	the image file
   * @return		the report, null if failed to load report data or none available
   */
  protected Report loadAnnotations(ImagePanel panel, File file) {
    Report 		result;
    File		baseFile;
    File 		locFile;
    List<Report> 	reports;
    Report		report;
    String		label;
    Double		score;
    String		text;

    result   = null;
    baseFile = file;
    if (m_FileSuffix.isEmpty())
      locFile = FileUtils.replaceExtension(baseFile, "." + m_Reader.getDefaultFormatExtension());
    else
      locFile = FileUtils.replaceExtension(baseFile, m_FileSuffix);
    if (locFile.exists() && locFile.isFile()) {
      m_Reader.setInput(new PlaceholderFile(locFile));
      reports = m_Reader.read();
      if (reports.size() > 0)
	result = reports.get(0);
    }

    // fabricate new report for overlay
    if (result != null) {
      report = new Report();
      label  = "???";
      score  = null;
      if (result.hasValue(m_FieldLabel))
	label = result.getStringValue(m_FieldLabel);
      if (result.hasValue(m_FieldScore))
	score = result.getDoubleValue(m_FieldScore);
      text = applyLabelFormat(label, score, m_Format);
      report.setStringValue("Text", text);
      result = report;
    }

    return result;
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    ImagePanel 		panel;
    Report 		report;
    MetaDataText 	overlay;

    overlay = new MetaDataText();
    overlay.setField(new Field("Text", DataType.STRING));
    overlay.setColor(m_Color);
    overlay.setFont(m_Font);
    overlay.setX(m_OffsetX);
    overlay.setY(m_OffsetY);
    panel  = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.addImageOverlay(overlay);
    report = loadAnnotations(panel, file);
    panel.load(file, ObjectCopyHelper.copyObject(getImageReader()), -1.0);
    panel.setAdditionalProperties(report);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel previewPanel) {
    ImagePanel 	panel;
    Report	report;

    panel  = (ImagePanel) previewPanel.getComponent();
    report = loadAnnotations(panel, file);
    panel.load(file, ObjectCopyHelper.copyObject(getImageReader()), panel.getScale());
    panel.setAdditionalProperties(report);

    return previewPanel;
  }
}
