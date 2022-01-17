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
 * YoloAnnotationsHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.ImageAnchor;
import adams.data.image.ImageMetaDataHelper;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.YoloAnnotationsReportReader;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.Fonts;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.leftclick.ViewObjects;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following image types with an overlay for the objects stored in the .txt file with the same name (using object prefix 'Object.'): tif,jpg,tiff,bmp,gif,png,jpeg,wbmp
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix of fields in the report to identify as object location, eg 'Object.
 * &nbsp;&nbsp;&nbsp;'.
 * &nbsp;&nbsp;&nbsp;default: Object.
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
 * <pre>-label-format &lt;java.lang.String&gt; (property: labelFormat)
 * &nbsp;&nbsp;&nbsp;The label format string to use for the rectangles; '#' for index, '&#64;' for
 * &nbsp;&nbsp;&nbsp;type and '$' for short type (type suffix must be defined for '&#64;' and '$'
 * &nbsp;&nbsp;&nbsp;), '{BLAH}' gets replaced with the value associated with the meta-data key
 * &nbsp;&nbsp;&nbsp;'BLAH'; for instance: '# &#64;' or '# {BLAH}'; in case of numeric values, use
 * &nbsp;&nbsp;&nbsp;'|.X' to limit the number of decimals, eg '{BLAH|.2}' for a maximum of decimals
 * &nbsp;&nbsp;&nbsp;after the decimal point.
 * &nbsp;&nbsp;&nbsp;default: #. $
 * </pre>
 *
 * <pre>-label-font &lt;java.awt.Font&gt; (property: labelFont)
 * &nbsp;&nbsp;&nbsp;The font to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 * <pre>-label-anchor &lt;TOP_LEFT|TOP_CENTER|TOP_RIGHT|MIDDLE_LEFT|MIDDLE_CENTER|MIDDLE_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT&gt; (property: labelAnchor)
 * &nbsp;&nbsp;&nbsp;The anchor for the label.
 * &nbsp;&nbsp;&nbsp;default: TOP_RIGHT
 * </pre>
 *
 * <pre>-label-offset-x &lt;int&gt; (property: labelOffsetX)
 * &nbsp;&nbsp;&nbsp;The X offset for the label; values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses left as anchor, -2 the center and -3 the right.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-label-offset-y &lt;int&gt; (property: labelOffsetY)
 * &nbsp;&nbsp;&nbsp;The Y offset for the label values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class YoloAnnotationsHandler
    extends AbstractContentHandler {

  private static final long serialVersionUID = -1671414346233382229L;

  /** the prefix for the objects in the report. */
  protected String m_Prefix;

  /** the color for the objects. */
  protected Color m_Color;

  /** whether to use colors per type. */
  protected boolean m_UseColorsPerType;

  /** the color provider to use. */
  protected ColorProvider m_TypeColorProvider;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the label for the rectangles. */
  protected String m_LabelFormat;

  /** the label font. */
  protected Font m_LabelFont;

  /** the label anchor. */
  protected ImageAnchor m_LabelAnchor;

  /** the x offset for the label. */
  protected int m_LabelOffsetX;

  /** the y offset for the label. */
  protected int m_LabelOffsetY;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Displays the following image types with an overlay for the objects "
	    + "stored in the .txt file with the same name (using object prefix '" + ObjectLocationsOverlayFromReport.PREFIX_DEFAULT + "'): "
	    + Utils.arrayToString(getExtensions());
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
	"color", "color",
	Color.RED);

    m_OptionManager.add(
	"use-colors-per-type", "useColorsPerType",
	getDefaultUseColorsPerType());

    m_OptionManager.add(
	"type-color-provider", "typeColorProvider",
	new DefaultColorProvider());

    m_OptionManager.add(
	"type-suffix", "typeSuffix",
	".type");

    m_OptionManager.add(
	"label-format", "labelFormat",
	getDefaultLabelFormat());

    m_OptionManager.add(
	"label-font", "labelFont",
	Fonts.getSansFont(14));

    m_OptionManager.add(
	"label-anchor", "labelAnchor",
	getDefaultLabelAnchor());

    m_OptionManager.add(
	"label-offset-x", "labelOffsetX",
	getDefaultLabelOffsetX());

    m_OptionManager.add(
	"label-offset-y", "labelOffsetY",
	getDefaultLabelOffsetY());
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects in the report.
   *
   * @return 		the prefix
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
    return "The prefix of fields in the report to identify as object location, eg 'Object.'.";
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
   * Returns the default for using colors per type.
   *
   * @return		the default
   */
  protected boolean getDefaultUseColorsPerType() {
    return true;
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
   * Returns the default label format.
   *
   * @return		the default
   */
  protected String getDefaultLabelFormat() {
    return "#. $";
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
    return "The label format string to use for the rectangles; "
	+ "'#' for index, '@' for type and '$' for short type (type suffix "
	+ "must be defined for '@' and '$'), '{BLAH}' gets replaced with the "
	+ "value associated with the meta-data key 'BLAH'; "
	+ "for instance: '# @' or '# {BLAH}'; in case of numeric values, use '|.X' "
	+ "to limit the number of decimals, eg '{BLAH|.2}' for a maximum of decimals "
	+ "after the decimal point.";
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
   * Returns the default label anchor.
   *
   * @return		the default
   */
  protected ImageAnchor getDefaultLabelAnchor() {
    return ImageAnchor.TOP_RIGHT;
  }

  /**
   * Sets the anchor for the label.
   *
   * @param value 	the anchor
   */
  public void setLabelAnchor(ImageAnchor value) {
    m_LabelAnchor = value;
    reset();
  }

  /**
   * Returns the anchor for the label.
   *
   * @return 		the anchor
   */
  public ImageAnchor getLabelAnchor() {
    return m_LabelAnchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelAnchorTipText() {
    return "The anchor for the label.";
  }

  /**
   * Returns the default label offset for X.
   *
   * @return		the default
   */
  protected int getDefaultLabelOffsetX() {
    return 0;
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setLabelOffsetX(int value) {
    m_LabelOffsetX = value;
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getLabelOffsetX() {
    return m_LabelOffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetXTipText() {
    return "The X offset for the label; values of 0 or greater are interpreted as absolute pixels, -1 uses left as anchor, -2 the center and -3 the right.";
  }

  /**
   * Returns the default label offset for Y.
   *
   * @return		the default
   */
  protected int getDefaultLabelOffsetY() {
    return 0;
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setLabelOffsetY(int value) {
    m_LabelOffsetY = value;
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getLabelOffsetY() {
    return m_LabelOffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetYTipText() {
    return "The Y offset for the label values of 0 or greater are interpreted as absolute pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.";
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
    File				reportFile;
    YoloAnnotationsReportReader		reportReader;
    List<Report> 			reports;
    Report 				report;
    SpreadSheet				sheet;
    int					width;
    int					height;

    panel      = new ImagePanel();
    report     = null;
    reportFile = FileUtils.replaceExtension(file, ".txt");
    if (reportFile.exists() && reportFile.isFile()) {
      width  = -1;
      height = -1;
      try {
	sheet = ImageMetaDataHelper.commons(file);
	for (Row row: sheet.rows()) {
	  if (row.getCell(0).getContent().equals("Width"))
	    width = Integer.parseInt(row.getCell(1).getContent());
	  if (row.getCell(0).getContent().equals("Height"))
	    height = Integer.parseInt(row.getCell(1).getContent());
	}
      }
      catch (Exception e) {
	width  = -1;
	height = -1;
      }
      reportReader = new YoloAnnotationsReportReader();
      reportReader.setInput(new PlaceholderFile(reportFile));
      if ((width > -1) && (height > -1)) {
	reportReader.setWidth(width);
	reportReader.setHeight(height);
      }
      reports = reportReader.read();
      if (reports.size() > 0) {
	report  = reports.get(0);
	overlay = new ObjectLocationsOverlayFromReport();
	overlay.setPrefix(ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);
	overlay.setColor(m_Color);
	overlay.setUseColorsPerType(m_UseColorsPerType);
	overlay.setTypeColorProvider(m_TypeColorProvider.shallowCopy());
	overlay.setTypeSuffix(m_TypeSuffix);
	overlay.setLabelFormat(m_LabelFormat);
	overlay.setLabelFont(m_LabelFont);
	overlay.setLabelAnchor(m_LabelAnchor);
	overlay.setLabelOffsetX(m_LabelOffsetX);
	overlay.setLabelOffsetY(m_LabelOffsetY);
	panel.addImageOverlay(overlay);
      }
    }
    panel.load(file, new JAIImageReader(), -1.0);
    panel.setAdditionalProperties(report);
    panel.addLeftClickListener(new ViewObjects());

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
