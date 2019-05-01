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
 * StoreValueInReport.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel.containerlistpopup;

import adams.core.Properties;
import adams.core.option.AbstractOption;
import adams.data.container.DataContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ColorContainerManager;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import com.github.fracpete.javautils.struct.Struct2;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * For storing a value in the report.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StoreValueInReport<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractContainerListPopupCustomizer<T,M,C> {

  private static final long serialVersionUID = 4973341996386365675L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Value-Store in report";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "1-report";
  }

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  @Override
  public boolean handles(DataContainerPanelWithContainerList<T, M, C> panel) {
    return (panel.getContainerManager() instanceof ColorContainerManager) && panel.supportsStoreColorInReport();
  }

  /**
   * Prompts the user to enter a field and value.
   *
   * @param context	the context
   * @return		the value, null if dialog canceled
   */
  protected Struct2<Field,Object> enterValue(final Context<T,M,C> context) {
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

    panel.setPropertyOrder(order);
    panel.setProperties(props);

    dialog = new ApprovalDialog(null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Enter field");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(context.panel.getParent()));
    dialog.setVisible(true);

    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;

    props  = panel.getProperties();
    field  = new Field(props.getProperty("name"), DataType.valueOf((AbstractOption) null, props.getProperty("type")));
    report = new Report();
    report.addField(field);
    report.setValue(field, props.getProperty("value"));  // to parse value

    return new Struct2<>(field, report.getValue(field));
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param context	the context
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final Context<T,M,C> context, JPopupMenu menu) {
    JMenuItem		item;
    final int[] 	indices;

    indices = context.actualSelectedContainerIndices;
    item    = new JMenuItem("Store value in report...");
    item.addActionListener((ActionEvent e) -> {
      Struct2<Field,Object> data = enterValue(context);
      if (data == null)
        return;
      context.panel.storeValueInReport(indices, data.value1, data.value2);
    });
    menu.add(item);
  }
}
