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
 * DatabaseAddField.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import adams.core.Properties;
import adams.core.option.AbstractOption;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.db.ReportProvider;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.ApprovalDialog;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows the user to add a new field to the report. The updated report gets 
 * saved to the database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseAddField
  extends AbstractTableActionWithDatabaseAccess {

  /** for serialization. */
  private static final long serialVersionUID = 2839250960387657274L;

  /**
   * Default constructor.
   */
  public DatabaseAddField() {
    super("Add new field...");
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ReportProvider 		provider;
    Report 			report;
    Field 			field;
    PropertiesParameterPanel 	panel;
    List<String> 		order;
    Properties 			props;
    ApprovalDialog 		dialog;

    panel = new PropertiesParameterPanel();
    order = new ArrayList<>();

    panel.addPropertyType("name", PropertyType.STRING);
    panel.setLabel("name", "Name");
    panel.setHelp("name", "The name of the field");
    order.add("name");

    panel.addPropertyType("type", PropertyType.BLANK_SEPARATED_LIST_FIXED);
    panel.setLabel("type", "Type");
    panel.setHelp("type", "The name of the field");
    order.add("type");

    panel.addPropertyType("value", PropertyType.STRING);
    panel.setLabel("value", "Value");
    panel.setHelp("value", "The value for the field");
    order.add("value");

    props = new Properties();
    props.setProperty("name", "field");
    props.setProperty("type", "N S B U");
    props.setProperty("value", "0.0");

    panel.setProperties(props);

    dialog = new ApprovalDialog(null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle(getName());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getTable()));
    dialog.setVisible(true);

    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    props  = panel.getProperties();
    field  = new Field(props.getProperty("name"), DataType.valueOf((AbstractOption) null, props.getProperty("type")));
    provider = getReportProvider();
    report = getReport();
    report.setValue(field, props.getProperty("value"));
    provider.store(report.getDatabaseID(), report);
    setReport(report);
  }
}
