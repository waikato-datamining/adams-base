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
 * ColorDistributionHandler.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.image.features.ColorCounts;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.ApacheCommonsImageReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.event.SearchEvent;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Displays the color distribution for images.
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
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the images.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.ApacheCommonsImageReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColorDistributionHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /** the image reader to use. */
  protected AbstractImageReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the color distribution for images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new ApacheCommonsImageReader());
  }

  /**
   * Sets the reader to use for loading the image.
   *
   * @param value 	the reader
   */
  public void setReader(AbstractImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use for loading the image.
   *
   * @return 		the reader
   */
  public AbstractImageReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for loading the images.";
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
    JPanel 			panel;
    final SpreadSheetTable 	table;
    SpreadSheet 		sheet;
    SearchPanel 		search;
    AbstractImageContainer	cont;
    BufferedImageContainer	bcont;
    ColorCounts			counts;
    Object[]			rows;
    Row				srow;

    try {
      cont  = m_Reader.read(new PlaceholderFile(file));
      if (cont == null)
	return new NoPreviewAvailablePanel();
      bcont = new BufferedImageContainer();
      bcont.setImage(cont.toBufferedImage());
      counts = new ColorCounts();
      counts.setConverter(new adams.data.featureconverter.SpreadSheet());
      rows = counts.generate(bcont);
      sheet = null;
      for (Object row: rows) {
        srow = (Row) row;
        if (sheet == null)
          sheet = srow.getOwner().getHeader();
        sheet.addRow().assign(srow);
      }
      counts.cleanUp();
      if (sheet == null)
	return new NoPreviewAvailablePanel();
      panel = new JPanel(new BorderLayout(5, 5));
      table = new SpreadSheetTable(sheet);
      panel.add(new BaseScrollPane(table));

      search = new SearchPanel(LayoutType.HORIZONTAL, true);
      search.addSearchListener((SearchEvent e) -> table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
      panel.add(search, BorderLayout.SOUTH);

      return new PreviewPanel(panel, table);
    }
    catch (Exception e) {
      return new NoPreviewAvailablePanel();
    }
  }
}
