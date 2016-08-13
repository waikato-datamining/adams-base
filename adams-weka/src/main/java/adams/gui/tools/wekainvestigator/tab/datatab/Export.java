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
 * Export.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.datatab;

import adams.core.io.PlaceholderFile;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.FileContainer;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils.DataSink;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Exports the selected data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Export
  extends AbstractDataTabAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /** the file chooser for exporting. */
  protected WekaFileChooser m_FileChooser;

  /**
   * Instantiates the action.
   */
  public Export() {
    super();
    setName("Export");
    setIcon("save.gif");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new WekaFileChooser();
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer[]	conts;
    int[]		rows;
    int			i;
    DataContainer 	data;
    FileContainer 	cont;
    int			retVal;
    AbstractFileSaver 	saver;

    conts = getSelectedData();
    rows  = getSelectedRows();
    for (i = 0; i < conts.length; i++) {
      data   = conts[i];
      m_FileChooser.setDialogTitle("Exporting " + (i+1) + "/" + (rows.length) + ": " + data.getData().relationName());
      m_FileChooser.setSelectedFile(new PlaceholderFile(m_FileChooser.getCurrentDirectory().getAbsolutePath() + File.separator + data.getSourceShort()));
      retVal = m_FileChooser.showSaveDialog(getOwner());
      if (retVal != WekaFileChooser.APPROVE_OPTION)
	break;
      try {
	logMessage("Exporting: " + data.getSourceFull());
	saver = m_FileChooser.getWriter();
	saver.setFile(m_FileChooser.getSelectedFile());
	DataSink.write(saver, data.getData());
	logMessage("Exported: " + m_FileChooser.getSelectedFile());
	cont = new FileContainer(m_FileChooser.getReaderForFile(m_FileChooser.getSelectedFile()), m_FileChooser.getSelectedFile());
	getData().set(rows[i], cont);
        fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, rows[i]));
      }
      catch (Exception ex) {
	logError("Failed to export: " + m_FileChooser.getSelectedFile() + "\n", ex, "Export");
	break;
      }
    }
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(getTable().getSelectedRowCount() > 0);
  }
}
