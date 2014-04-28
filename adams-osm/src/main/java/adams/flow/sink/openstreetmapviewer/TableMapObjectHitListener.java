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
 * TableMapObjectHitListener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Dialog.ModalityType;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.data.mapobject.MetaDataSupporter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;

/**
 <!-- globalinfo-start -->
 * Displays all the hits in a table format.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Hits
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TableMapObjectHitListener
  extends AbstractMapObjectHitListenerWithDialog<SpreadSheetDialog> {

  /** for serialization. */
  private static final long serialVersionUID = -613241778857988225L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays all the hits in a table format.";
  }
  
  /**
   * Performs the processing of the hits.
   * 
   * @param viewer	the associated viewer
   * @param hits	the objects that were "hit"
   */
  @Override
  protected SpreadSheetDialog doProcessHits(JMapViewer viewer, List<MapObject> hits) {
    SpreadSheetDialog	result;
    SpreadSheet		sheet;
    Row			row;
    MetaDataSupporter	meta;
    List<String>	keys;
    
    sheet = new SpreadSheet();
    sheet.setDataRowClass(SparseDataRow.class);
    row   = sheet.getHeaderRow();
    row.addCell("name").setContent("Name");
    row.addCell("layer").setContent("Layer");
    keys = getMetaDataKeys(hits);
    for (String key: keys)
      row.addCell("meta-" + key).setContentAsString(key);
    for (MapObject hit: hits) {
      row = sheet.addRow();
      row.addCell("name").setContentAsString(hit.getName());
      row.addCell("layer").setContentAsString(hit.getLayer().getName());
      if (hit instanceof MetaDataSupporter) {
	meta = (MetaDataSupporter) hit;
	for (String key: keys)
	  row.addCell("meta-" + key).setContent(meta.getMetaData(key).toString());
      }
    }
    
    if (GUIHelper.getParentDialog(viewer) != null)
      result = new SpreadSheetDialog(GUIHelper.getParentDialog(viewer), ModalityType.MODELESS);
    else
      result = new SpreadSheetDialog(GUIHelper.getParentFrame(viewer), false);
    result.setTitle(getTitle());
    result.setShowSearch(true);
    result.setSpreadSheet(sheet);
    
    return result;
  }
}
