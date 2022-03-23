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
 * DetectronAnnotationsHandler.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.annotation.DeprecatedClass;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.DetectronAnnotationsReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.objectfinder.ByMetaDataStringValue;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
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
 * Displays the following image types with an overlay for the objects stored in the Detectron annotations JSON file (using object prefix 'Object.'): jpg,tif,tiff,bmp,gif,png,jpeg,wbmp
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Hisham (habdelqa at waikato dot ac dot nz)
 */
@DeprecatedClass(
  useInstead = {CocoAnnotationsHandler.class}
)
public class DetectronAnnotationsHandler
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
      "Displays the following image types with an overlay for the objects stored in the "
	+ "Detectron annotations JSON file (using object prefix '" + ObjectLocationsOverlayFromReport.PREFIX_DEFAULT + "'): "
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
    File				reportFile;
    DetectronAnnotationsReportReader    reportReader;
    List<Report> 			reports;
    Report 				report;
    ByMetaDataStringValue		filter;
    LocatedObjects			lobjs;
    boolean                             found;

    panel      = new ImagePanel();
    panel.getUndo().setEnabled(false);
    report     = null;
    reportFile = new File(file.getParent() + "/annotations.json");
    found = false;

    if (reportFile.exists() && reportFile.isFile()) {
      reportReader = new DetectronAnnotationsReportReader();
      reportReader.setLabelKey("type");
      reportReader.setInput(new PlaceholderFile(reportFile));
      reports = reportReader.read();
      for (int i=0; i < reports.size(); i++) {
        report  = reports.get(i);
        filter  = new ByMetaDataStringValue();
        filter.setKey("filename");
        filter.setRegExp(new BaseRegExp(file.getName()));
        lobjs = filter.findObjects(report);
        if (lobjs.size() > 0) {
          report = lobjs.toReport(ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);
          found = true;
          break;
        }
      }
      if (found) {
        overlay = new ObjectLocationsOverlayFromReport();
        overlay.setPrefix(ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);
        overlay.setColor(m_Color);
        overlay.setUseColorsPerType(m_UseColorsPerType);
        overlay.setTypeColorProvider(m_TypeColorProvider.shallowCopy());
        overlay.setTypeSuffix(m_TypeSuffix);
        overlay.setTypeRegExp((BaseRegExp) m_TypeRegExp.getClone());
        overlay.setLabelFormat(m_LabelFormat);
        overlay.setLabelFont(m_LabelFont);
        panel.addImageOverlay(overlay);
      }
    }
    panel.load(file, new JAIImageReader(), -1.0);
    panel.setAdditionalProperties(report);
    panel.addLeftClickListener(new ViewObjects());

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
