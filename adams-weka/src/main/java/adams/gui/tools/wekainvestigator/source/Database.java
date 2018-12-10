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
 * Database.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.source;

import adams.db.AbstractDatabaseConnection;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SqlQueryDialog;
import adams.gui.tools.wekainvestigator.data.DatabaseContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorJob;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * For loading data from a database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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
    SqlQueryDialog 	dialog;
    InvestigatorJob 	job;

    if (getOwner().getParentDialog() != null)
      dialog = new SqlQueryDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new SqlQueryDialog(getOwner().getParentFrame(), true);
    dialog.setDefaultCloseOperation(SqlQueryDialog.DISPOSE_ON_CLOSE);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
    if (dialog.getOption() != SqlQueryDialog.APPROVE_OPTION)
      return;

    job = new InvestigatorJob(getOwner(), "Loading data from database") {
      @Override
      protected void doRun() {
	AbstractDatabaseConnection conn = dialog.getDatabaseConnection();
	addData(new DatabaseContainer(conn.getURL(), conn.getUser(), conn.getPassword().getValue(), dialog.getQuery()));
      }
    };
    getOwner().startExecution(job);
  }
}
