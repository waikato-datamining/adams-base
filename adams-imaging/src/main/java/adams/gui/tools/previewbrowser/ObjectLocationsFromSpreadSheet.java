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
 * ObjectLocationsFromSpreadSheet.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.ObjectLocationsSpreadSheetReader;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.Fonts;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following image types with an overlay for the objects stored in the spreadsheet with the same name (using the spreadsheet reader's default extension) or with a '-rois' suffix to the name: tif,jpg,tiff,bmp,gif,png,wbmp,jpeg
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.ObjectLocationsSpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader setup to use for reading the object locations from the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.ObjectLocationsSpreadSheetReader -reader \"adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet\" -row-finder adams.data.spreadsheet.rowfinder.AllFinder -col-left x0 -col-top y0 -col-right x1 -col-bottom y1 -col-type label_str
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the objects.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 * <pre>-use-colors-per-type &lt;boolean&gt; (property: useColorsPerType)
 * &nbsp;&nbsp;&nbsp;If enabled, individual colors per type are used.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-type-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: typeColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for the various types.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix of fields in the report to identify the type.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 * <pre>-type-regexp &lt;adams.core.base.BaseRegExp&gt; (property: typeRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the types must match in order to get drawn (
 * &nbsp;&nbsp;&nbsp;eg only plotting a subset).
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-label-format &lt;java.lang.String&gt; (property: labelFormat)
 * &nbsp;&nbsp;&nbsp;The label format string to use for the rectangles; '#' for index, '&#64;' for
 * &nbsp;&nbsp;&nbsp;type and '$' for short type (type suffix must be defined for '&#64;' and '$'
 * &nbsp;&nbsp;&nbsp;); for instance: '# &#64;'.
 * &nbsp;&nbsp;&nbsp;default: #. $
 * </pre>
 *
 * <pre>-label-font &lt;java.awt.Font&gt; (property: labelFont)
 * &nbsp;&nbsp;&nbsp;The font to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectLocationsFromSpreadSheet
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /** the reader to use. */
  protected ObjectLocationsSpreadSheetReader m_Reader;

  /** the color for the objects. */
  protected Color m_Color;

  /** whether to use colors per type. */
  protected boolean m_UseColorsPerType;

  /** the color provider to use. */
  protected ColorProvider m_TypeColorProvider;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the regular expression for the types to draw. */
  protected BaseRegExp m_TypeRegExp;

  /** the label for the rectangles. */
  protected String m_LabelFormat;

  /** the label font. */
  protected Font m_LabelFont;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Displays the following image types with an overlay for the objects "
	+ "stored in the spreadsheet with the same name (using the spreadsheet "
	+ "reader's default extension) or with a '-rois' suffix to the name: "
	+ Utils.arrayToString(getExtensions());
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      getDefaultReader());

    m_OptionManager.add(
      "color", "color",
      Color.RED);

    m_OptionManager.add(
      "use-colors-per-type", "useColorsPerType",
      true);

    m_OptionManager.add(
      "type-color-provider", "typeColorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "type-suffix", "typeSuffix",
      ".type");

    m_OptionManager.add(
      "type-regexp", "typeRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "label-format", "labelFormat",
      "#. $");

    m_OptionManager.add(
      "label-font", "labelFont",
      Fonts.getSansFont(14));
  }

  /**
   * Returns the default reader.
   *
   * @return		the reader
   */
  protected ObjectLocationsSpreadSheetReader getDefaultReader() {
    ObjectLocationsSpreadSheetReader  result;

    result = new ObjectLocationsSpreadSheetReader();
    result.setColLeft(new SpreadSheetColumnIndex("x0"));
    result.setColTop(new SpreadSheetColumnIndex("y0"));
    result.setColRight(new SpreadSheetColumnIndex("x1"));
    result.setColBottom(new SpreadSheetColumnIndex("y1"));
    result.setColType(new SpreadSheetColumnIndex("label_str"));

    return result;
  }

  /**
   * Sets the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @param value 	the reader
   */
  public void setReader(ObjectLocationsSpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @return 		the reader
   */
  public ObjectLocationsSpreadSheetReader getReader() {
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
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
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
    return "The color to use for the objects.";
  }

  /**
   * Sets whether to use colors per type.
   *
   * @param value 	true if to use colors per type
   */
  public void setUseColorsPerType(boolean value) {
    m_UseColorsPerType = value;
    reset();
  }

  /**
   * Returns whether to use colors per type.
   *
   * @return 		true if to use colors per type
   */
  public boolean getUseColorsPerType() {
    return m_UseColorsPerType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColorsPerTypeTipText() {
    return "If enabled, individual colors per type are used.";
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setTypeColorProvider(ColorProvider value) {
    m_TypeColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the types.
   *
   * @return 		the provider
   */
  public ColorProvider getTypeColorProvider() {
    return m_TypeColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColorProviderTipText() {
    return "The color provider to use for the various types.";
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_TypeSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return "The suffix of fields in the report to identify the type.";
  }

  /**
   * Sets the regular expression that the types must match in order to get
   * drawn.
   *
   * @param value 	the expression
   */
  public void setTypeRegExp(BaseRegExp value) {
    m_TypeRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the types must match in order to get
   * drawn.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeRegExp() {
    return m_TypeRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeRegExpTipText() {
    return "The regular expression that the types must match in order to get drawn (eg only plotting a subset).";
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setLabelFormat(String value) {
    m_LabelFormat = value;
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getLabelFormat() {
    return m_LabelFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFormatTipText() {
    return "The label format string to use for the rectangles; '#' for index, '@' for type and '$' for short type (type suffix must be defined for '@' and '$'); for instance: '# @'.";
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setLabelFont(Font value) {
    m_LabelFont = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_LabelFont;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return "The font to use for the labels.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new JAIImageReader().getFormatExtensions();
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    ImagePanel 				panel;
    ObjectLocationsOverlayFromReport	overlay;
    File 				locFile;
    File 				locFile2;
    List<Report> 			reports;
    Report				report;

    panel    = new ImagePanel();
    overlay  = null;
    report   = null;
    locFile  = FileUtils.replaceExtension(file, "." + m_Reader.getReader().getDefaultFormatExtension());
    locFile2 = FileUtils.replaceExtension(file, "-rois." + m_Reader.getReader().getDefaultFormatExtension());
    if (locFile2.exists() && locFile2.isFile())
      locFile = locFile2;
    if (locFile.exists() && locFile.isFile()) {
      m_Reader.setInput(new PlaceholderFile(locFile));
      reports = m_Reader.read();
      if (reports.size() > 0) {
        report  = reports.get(0);
	overlay = new ObjectLocationsOverlayFromReport();
	overlay.setPrefix(m_Reader.getPrefix());
	overlay.setColor(m_Color);
	overlay.setUseColorsPerType(m_UseColorsPerType);
	overlay.setTypeColorProvider(m_TypeColorProvider.shallowCopy());
	overlay.setTypeSuffix(m_TypeSuffix);
	overlay.setTypeRegExp((BaseRegExp) m_TypeRegExp.getClone());
	overlay.setLabelFormat(m_LabelFormat);
	overlay.setLabelFont(m_LabelFont);
      }
    }
    if (overlay != null) {
      panel.addImageOverlay(overlay);
      panel.setAdditionalProperties(report);
    }
    panel.load(file, new JAIImageReader(), -1.0);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
