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
 * Database.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.source;

import adams.gui.tools.wekainvestigator.data.DatabaseContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorJob;
import weka.gui.sql.SqlViewerDialog;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

/**
 * For loading data from a database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Database
  extends AbstractSource {

  private static final long serialVersionUID = 5646388990155938153L;

  /**
   * Instantiates the action.
   */
  public Database() {
    super();
    setName("Database...");
    setIcon("database.gif");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    SqlViewerDialog	dialog;
    InvestigatorJob 	job;

    dialog = new SqlViewerDialog(null);
    dialog.setDefaultCloseOperation(SqlViewerDialog.DISPOSE_ON_CLOSE);
    dialog.pack();
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
    if (dialog.getReturnValue() != JOptionPane.OK_OPTION)
      return;

    job = new InvestigatorJob(getOwner(), "Loading data from database") {
      @Override
      protected void doRun() {
	addData(new DatabaseContainer(dialog.getURL(), dialog.getUser(), dialog.getPassword(), dialog.getQuery()));
      }
    };
    getOwner().startExecution(job);
  }
}
