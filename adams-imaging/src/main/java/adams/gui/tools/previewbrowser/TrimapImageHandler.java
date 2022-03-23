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
 * TrimapImageHandler.java
 * Copyright (C) 2018-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.transformer.TrimapColorizer;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.VggXmlAnnotationReportReader;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.leftclick.ViewObjects;

import java.awt.Color;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following image types as trimaps: jpg,tif,tiff,bmp,gif,png,wbmp,jpeg<br>
 * If VGG XML annotation file with the same name exists, then an object overlay is added.
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TrimapImageHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the following image types as trimaps: " + Utils.arrayToString(getExtensions()) + "\n"
      + "If VGG XML annotation file with the same name exists, then an object overlay is added.";
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
    ImagePanel				panel;
    JAIImageReader			reader;
    BufferedImageContainer		cont;
    TrimapColorizer			colorizer;
    ObjectLocationsOverlayFromReport	overlay;
    File				reportFile;
    VggXmlAnnotationReportReader	reportReader;
    List<Report> 			reports;
    Report 				report;

    reader = new JAIImageReader();
    cont   = reader.read(new PlaceholderFile(file));
    if (cont == null)
      return new NoPreviewAvailablePanel();

    colorizer = new TrimapColorizer();
    cont      = colorizer.transform(cont)[0];

    panel = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.setCurrentImage(cont);
    report     = null;
    reportFile = FileUtils.replaceExtension(file, ".xml");
    if (reportFile.exists() && reportFile.isFile()) {
      reportReader = new VggXmlAnnotationReportReader();
      reportReader.setInput(new PlaceholderFile(reportFile));
      reports = reportReader.read();
      if (reports.size() > 0) {
        report  = reports.get(0);
	overlay = new ObjectLocationsOverlayFromReport();
	overlay.setPrefix(ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);
	overlay.setColor(Color.GREEN);
        panel.addImageOverlay(overlay);
      }
    }
    panel.setAdditionalProperties(report);
    panel.addLeftClickListener(new ViewObjects());

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
