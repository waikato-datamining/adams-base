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

/**
 * SanselanImageMetaDataHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.data.image.ImageMetaDataHelper;
import adams.data.io.input.SanselanImageReader;
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
 * Displays meta-data for the following image types: bmp,gif,ico,pbm,pgm,png,pnm,ppm,psd,tif,tiff
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
 * @version $Revision: 10619 $
 */
public class SanselanImageMetaDataHandler
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
    return "Displays meta-data for the following image types: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new SanselanImageReader().getFormatExtensions();
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    JPanel panel;
    final SpreadSheetTable 	table;
    SpreadSheet			sheet;
    SearchPanel search;

    try {
      panel = new JPanel(new BorderLayout(5, 5));
      sheet = ImageMetaDataHelper.getMetaData(file);
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
