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
 * Save.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.FileContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils.DataSink;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Saves the selected data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Save
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /** the file chooser for exporting. */
  protected WekaFileChooser m_FileChooser;

  /**
   * Instantiates the action.
   */
  public Save() {
    super();
    setName("Save");
    setIcon("save.gif");
    setAsynchronous(true);
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
    int			retVal;
    PlaceholderFile 	suggested;

    conts = getSelectedData();
    rows  = getSelectedRows();
    // TODO requires queuing to work properly!
    for (i = 0; i < conts.length; i++) {
      final int index = i;
      final DataContainer data = conts[i];
      if (data instanceof FileContainer)
        suggested = new PlaceholderFile(data.getSource());
      else
        suggested = new PlaceholderFile(m_FileChooser.getCurrentDirectory().getAbsolutePath() + File.separator + FileUtils.createFilename(data.getData().relationName(), "_"));
      m_FileChooser.setDialogTitle("Saving " + (i+1) + "/" + (rows.length) + ": " + data.getData().relationName());
      m_FileChooser.setCurrentDirectory(suggested.getParentFile());
      m_FileChooser.setSelectedFile(suggested);
      retVal = m_FileChooser.showSaveDialog(getOwner());
      if (retVal != WekaFileChooser.APPROVE_OPTION)
        break;
      final File file = m_FileChooser.getSelectedFile();
      getOwner().startExecution(new InvestigatorTabJob(getOwner(), m_FileChooser.getDialogTitle()) {
        @Override
        protected void doRun() {
          try {
            if (file.exists()) {
              if (!file.delete())
                logMessage("Failed to delete existing file: " + file);
            }
            AbstractFileSaver saver = m_FileChooser.getWriter();
            saver.setFile(file);
            DataSink.write(saver, data.getData());
            showStatus("Saved " + data.getID() + "/" + data.getSource() + " to " + file);
            FileContainer cont = new FileContainer(m_FileChooser.getReaderForFile(file), file, data.getData());
            getData().set(rows[index], cont);
            String msg = getOwner().getOwner().addRecentFile(file, null);
            if (msg != null)
              showStatus(msg);
            fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, rows[index]));
          }
          catch (Exception ex) {
            logError("Failed to save: " + file + "\n", ex, "Save");
          }
        }
      });
    }
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && getTable().getSelectedRowCount() == 1);
  }
}
