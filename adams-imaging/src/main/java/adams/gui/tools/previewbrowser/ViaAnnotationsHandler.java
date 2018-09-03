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
 * ViaAnnotationsHandler.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.ViaAnnotationsReportReader;
import adams.data.objectfinder.ByMetaDataStringValue;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViaAnnotationsHandler
  extends AbstractContentHandler {

  private static final long serialVersionUID = -1671414346233382229L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Displays the following image types with an overlay for the objects "
	+ "stored in the JSON file with the same name (using object prefix '" + ObjectLocationsOverlayFromReport.PREFIX_DEFAULT + "'): "
	+ Utils.arrayToString(getExtensions());
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
    ViaAnnotationsReportReader		reportReader;
    List<Report> 			reports;
    Report 				report;
    ByMetaDataStringValue		filter;
    LocatedObjects			lobjs;

    panel      = new ImagePanel();
    overlay    = null;
    report     = null;
    reportFile = FileUtils.replaceExtension(file, ".json");
    if (reportFile.exists() && reportFile.isFile()) {
      reportReader = new ViaAnnotationsReportReader();
      reportReader.setLabelKey("type");
      reportReader.setInput(new PlaceholderFile(reportFile));
      reports = reportReader.read();
      if (reports.size() > 0) {
        report  = reports.get(0);
        filter  = new ByMetaDataStringValue();
        filter.setKey("filename");
        filter.setRegExp(new BaseRegExp(file.getName()));
        lobjs = filter.findObjects(report);
        if (lobjs.size() > 0)
	  report = lobjs.toReport(ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);
	overlay = new ObjectLocationsOverlayFromReport();
	overlay.setPrefix(ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);
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
