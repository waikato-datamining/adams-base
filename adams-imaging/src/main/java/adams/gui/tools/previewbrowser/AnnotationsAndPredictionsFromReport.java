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
 * AnnotationsAndPredictionsFromReport.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.report.Report;
import adams.gui.core.ColorHelper;
import adams.gui.core.Fonts;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.MultiImageOverlay;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.ReportObjectOverlay;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following image types with an overlay for the annotations and predictions stored in the report with the same name: jpg,bmp,gif,png,wbmp,jpeg<br>
 * Annotations have to have the prefix 'Object.' and predictions 'Prediction.'.<br>
 * The color for annotations is red and for predictions green.
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AnnotationsAndPredictionsFromReport
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /** the prefix for the annoations. */
  public final static String PREFIX_ANNOTATIONS = ReportObjectOverlay.PREFIX_DEFAULT;

  /** the prefix for the predictions. */
  public final static String PREFIX_PREDICTIONS = "Prediction.";

  /** the color for the annotations. */
  public final static Color COLOR_ANNOTATIONS = Color.RED;

  /** the color for the predictions. */
  public final static Color COLOR_PREDICTIONS = Color.GREEN;

  /** the prefix for the objects in the report. */
  protected String m_Prefix;

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
      "Displays the following image types with an overlay for the annotations "
	+ "and predictions stored in the report with the same name: "
	+ Utils.arrayToString(getExtensions()) + "\n"
	+ "Annotations have to have the prefix '" + PREFIX_ANNOTATIONS + "' and "
	+ "predictions '" + PREFIX_PREDICTIONS + "'.\n"
	+ "The color for annotations is " + toString(COLOR_ANNOTATIONS) + " and "
	+ "for predictions " + toString(COLOR_PREDICTIONS) + ".";
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

  protected String toString(Color color) {
    if (ColorHelper.toName(color) == null)
      return ColorHelper.toHex(color);
    else
      return ColorHelper.toName(color);
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
    MultiImageOverlay			multi;
    ObjectLocationsOverlayFromReport 	annotations;
    ObjectLocationsOverlayFromReport 	predictions;
    File				reportFile;
    DefaultSimpleReportReader		reportReader;
    List<Report> 			reports;
    Report				report;

    panel  = new ImagePanel();
    multi  = null;
    report = null;
    reportFile = FileUtils.replaceExtension(file, ".report");
    if (reportFile.exists() && reportFile.isFile()) {
      reportReader = new DefaultSimpleReportReader();
      reportReader.setInput(new PlaceholderFile(reportFile));
      reports = reportReader.read();
      if (reports.size() > 0) {
	report  = reports.get(0);
	annotations = new ObjectLocationsOverlayFromReport();
	annotations.setPrefix(m_Prefix);
	annotations.setTypeSuffix(m_TypeSuffix);
	annotations.setTypeRegExp((BaseRegExp) m_TypeRegExp.getClone());
	annotations.setLabelFormat(m_LabelFormat);
	annotations.setLabelFont(m_LabelFont);
	annotations.setPrefix(PREFIX_ANNOTATIONS);
	annotations.setColor(COLOR_ANNOTATIONS);
	predictions = new ObjectLocationsOverlayFromReport();
	predictions.setPrefix(m_Prefix);
	predictions.setTypeSuffix(m_TypeSuffix);
	predictions.setTypeRegExp((BaseRegExp) m_TypeRegExp.getClone());
	predictions.setLabelFormat(m_LabelFormat);
	predictions.setLabelFont(m_LabelFont);
	predictions.setPrefix(PREFIX_PREDICTIONS);
	predictions.setColor(COLOR_PREDICTIONS);
	multi = new MultiImageOverlay();
	multi.setOverlays(new ImageOverlay[]{
	  annotations,
	  predictions
	});
      }
    }
    if (multi != null) {
      panel.addImageOverlay(multi);
      panel.setAdditionalProperties(report);
    }
    panel.load(file, new JAIImageReader(), -1.0);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
