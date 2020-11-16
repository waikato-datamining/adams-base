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
 * AddMetaData.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.mouseclick;

import adams.core.Properties;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.dialog.SpreadSheetPanel;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows the user to add meta-data to the selected objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AddMetaData
  extends AbstractMouseClickProcessor {

  private static final long serialVersionUID = -5747047661002140048L;

  /** the last field name. */
  protected transient String m_LastField;

  /** the last field value. */
  protected transient String m_LastValue;

  /** the last field type. */
  protected transient String m_LastType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to add meta-data to the selected objects.";
  }

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  @Override
  protected void doProcess(ObjectAnnotationPanel panel, MouseEvent e) {
    LocatedObjects		objects;
    LocatedObjects		hits;
    SpreadSheet			sheet;
    SpreadSheet			sheetHit;
    ApprovalDialog 		dialog;
    SpreadSheetPanel 		sheetPanel;
    PropertiesParameterPanel 	propsPanel;
    List<String> 		order;
    Properties 			props;
    JPanel			contentPanel;
    Object			value;

    objects  = new LocatedObjects(panel.getObjects());
    hits     = determineHits(panel, e);

    if (hits.size() > 0) {
      // hits
      sheet = null;
      for (LocatedObject hit: hits) {
	sheetHit = hit.toSpreadSheet();
	if (sheet == null)
	  sheet = sheetHit;
	else
	  sheet.addRow().assign(sheetHit.getRow(0));
      }
      sheetPanel = new SpreadSheetPanel();
      sheetPanel.setSpreadSheet(sheet);
      sheetPanel.setShowSearch(true);

      // field
      propsPanel = new PropertiesParameterPanel();
      order = new ArrayList<>();

      propsPanel.addPropertyType("name", PropertyType.STRING);
      propsPanel.setLabel("name", "_Name");
      propsPanel.setHelp("name", "The name of the field");
      order.add("name");

      propsPanel.addPropertyType("type", PropertyType.LIST);
      propsPanel.setList("type", new String[]{"N", "S", "B", "U"});
      propsPanel.setLabel("type", "Type");
      propsPanel.setHelp("type", "The name of the field");
      if (m_LastType != null)
        propsPanel.setListDefault("type", m_LastType);
      order.add("type");

      propsPanel.addPropertyType("value", PropertyType.STRING);
      propsPanel.setLabel("value", "_Value");
      propsPanel.setHelp("value", "The value for the field");
      order.add("value");

      props = new Properties();
      props.setProperty("name", m_LastField == null ? "field" : m_LastField);
      props.setProperty("type", m_LastType == null ? "N" : m_LastType);
      if ((hits.size() == 1) && hits.get(0).getMetaData().containsKey(m_LastField))
        props.setProperty("value", "" + hits.get(0).getMetaData().get(m_LastField));
      else
        props.setProperty("value", m_LastValue == null ? "0.0" : m_LastValue);

      propsPanel.setPropertyOrder(order);
      propsPanel.setProperties(props);

      contentPanel = new JPanel(new BorderLayout());
      contentPanel.add(sheetPanel, BorderLayout.CENTER);
      contentPanel.add(propsPanel, BorderLayout.SOUTH);

      if (panel.getParentDialog() != null)
	dialog = new ApprovalDialog(panel.getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	dialog = new ApprovalDialog(panel.getParentFrame(), true);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Add meta-data");
      dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
      dialog.setLocationRelativeTo(panel);
      dialog.setVisible(true);
      if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
        return;

      // transfer data
      props = propsPanel.getProperties();
      m_LastField = props.getProperty("name");
      m_LastType  = props.getProperty("type");
      m_LastValue = props.getProperty("value");
      for (LocatedObject hit: hits) {
	switch (m_LastType) {
	  case "N":
	    value = props.getDouble("value");
	    break;
	  case "B":
	    value = props.getBoolean("value");
	    break;
	  default:
	    value = props.getProperty("value");
	    break;
	}
	if (value != null) {
	  objects.remove(hit);
          hit.getMetaData().put(m_LastField, value);
          objects.add(hit);
        }
      }

      // set new meta-data
      panel.addUndoPoint("Adding meta-data: " + m_LastField + "=" + m_LastValue);
      panel.setObjects(objects);
      panel.annotationsChanged(this);
    }
  }
}
