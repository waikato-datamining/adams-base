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
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.VggXmlAnnotationReportReader;
import adams.data.report.Report;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CustomColorProvider;
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
 * <pre>-reader &lt;adams.data.io.input.AbstractImageReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The image reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.JAIImageReader
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for coloring in the grayscale&#47;indexed image.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.CustomColorProvider -color #ffff00 -color #0000ff -color #ff0000
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

  /** the image reader to use. */
  protected AbstractImageReader m_Reader;

  /** the color provider for generating the colors. */
  protected ColorProvider m_ColorProvider;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "reader", "reader",
        getDefaultReader());

    m_OptionManager.add(
        "color-provider", "colorProvider",
        getDefaultColorProvider());
  }

  /**
   * Returns the default image reader.
   *
   * @return		the default
   */
  protected AbstractImageReader getDefaultReader() {
    return new JAIImageReader();
  }

  /**
   * Sets the image reader to use.
   *
   * @param value	the image reader
   */
  public void setReader(AbstractImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the imag reader to use.
   *
   * @return		the image reader
   */
  public AbstractImageReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String readerTipText() {
    return "The image reader to use.";
  }

  /**
   * Returns the default color provider.
   *
   * @return		the default
   */
  protected ColorProvider getDefaultColorProvider() {
    CustomColorProvider result;
    result = new CustomColorProvider();
    result.setColors(new Color[]{Color.YELLOW, Color.BLUE, Color.RED});
    return result;
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for coloring in the grayscale/indexed image.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return m_Reader.getFormatExtensions();
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
    BufferedImageContainer		cont;
    TrimapColorizer			colorizer;
    ObjectLocationsOverlayFromReport	overlay;
    File				reportFile;
    VggXmlAnnotationReportReader	reportReader;
    List<Report> 			reports;
    Report 				report;

    cont = (BufferedImageContainer) m_Reader.read(new PlaceholderFile(file));
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
