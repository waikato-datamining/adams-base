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
 * ConfusionMatrixHandler.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.spreadsheettable.ConfusionMatrixCellRenderingCustomizer;
import adams.gui.visualization.core.BiColorGenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

/**
 * For displaying a confusion matrix.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConfusionMatrixHandler
  extends AbstractContentHandler {

  private static final long serialVersionUID = -5721119391424306170L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays a confusion matrix from the following spreadsheet types:\n"
      + Utils.flatten(getExtensions(), ", ");
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new CsvSpreadSheetHandler().getExtensions();
  }

  /**
   * Creates the actual preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    BasePanel					result;
    SpreadSheetTable				table;
    CsvSpreadSheetReader			reader;
    SpreadSheet 				sheet;
    ConfusionMatrixCellRenderingCustomizer	renderer;
    BiColorGenerator				generator;

    reader = new CsvSpreadSheetReader();
    sheet  = reader.read(file);
    result = new BasePanel(new BorderLayout());
    table  = new SpreadSheetTable(sheet);
    table.setNumDecimals(3);
    renderer = new ConfusionMatrixCellRenderingCustomizer();
    renderer.setValueBasedBackground(true);
    generator = new BiColorGenerator();
    generator.setAlpha(128);
    generator.setFirstColor(Color.WHITE);
    generator.setSecondColor(Color.RED);
    renderer.setBackgroundColorGenerator(generator);
    table.setCellRenderingCustomizer(renderer);
    result.add(new BaseScrollPane(table), BorderLayout.CENTER);
    return new PreviewPanel(result, table);
  }
}
