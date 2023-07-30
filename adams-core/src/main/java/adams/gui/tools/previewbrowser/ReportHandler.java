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
 * ReportHandler.java
 * Copyright (C) 2015-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.report.Report;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.report.ReportFactory;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays report files (depends on selected reader): *
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
 * <pre>-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportHandler
  extends AbstractObjectContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8930638838922218410L;

  /** the reader to use. */
  protected AbstractReportReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays report files (depends on selected reader): " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"*"};
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
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  protected AbstractReportReader getDefaultReader() {
    return new DefaultSimpleReportReader();
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(AbstractReportReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader
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
    return "The reader to use.";
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel createPreview(File file) {
    List<Report> 		reports;

    m_Reader.setInput(new PlaceholderFile(file));
    reports = m_Reader.read();
    if (reports.size() == 0)
      return new PreviewPanel(new NoPreviewAvailablePanel());

    return createPreview(reports.get(0));
  }

  /**
   * Checks whether the class is handled by this content handler.
   *
   * @param cls the class to check
   * @return true if handled
   */
  @Override
  public boolean canHandle(Class cls) {
    return ClassLocator.isSubclass(Report.class, cls);
  }

  /**
   * Creates the actual preview.
   *
   * @param obj the object to create the view for
   * @return the preview
   */
  @Override
  public PreviewPanel createPreview(Object obj) {
    BasePanel			result;
    ReportFactory.Table		table;

    result = ReportFactory.getPanel((Report) obj, true);
    table  = (ReportFactory.Table) GUIHelper.findFirstComponent(result, ReportFactory.Table.class, true, true);

    return new PreviewPanel(result, table);
  }
}
