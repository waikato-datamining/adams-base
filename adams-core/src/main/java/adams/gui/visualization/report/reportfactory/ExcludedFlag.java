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
 * ExcludedFlag.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.db.ReportProvider;

import java.awt.event.ActionEvent;

/**
 * Sets the 'Excluded' flag and updates the database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ExcludedFlag
  extends AbstractTableActionWithDatabaseAccess {

  /** for serialization. */
  private static final long serialVersionUID = -7727702763234836816L;

  /**
   * Default constructor.
   */
  public ExcludedFlag() {
    super("Set '" + Report.FIELD_EXCLUDED + "' flag");
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ReportProvider 	provider;
    Report 		report;
    
    provider = getReportProvider();
    report   = getReport();
    report.setValue(new Field(Report.FIELD_EXCLUDED, DataType.BOOLEAN), true);
    provider.store(report.getDatabaseID(), report);
    setReport(report);
  }
}
