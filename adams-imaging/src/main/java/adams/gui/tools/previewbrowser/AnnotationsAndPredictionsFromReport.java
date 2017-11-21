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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.report.Report;
import adams.gui.core.ColorHelper;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.MultiImageOverlay;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.ReportObjectOverlay;

import java.awt.Color;
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
	annotations.setTypeSuffix(".type");
	annotations.setLabelFormat("#. $");
	annotations.setPrefix(PREFIX_ANNOTATIONS);
	annotations.setColor(COLOR_ANNOTATIONS);
	predictions = new ObjectLocationsOverlayFromReport();
	predictions.setTypeSuffix(".type");
	predictions.setLabelFormat("#. $");
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
