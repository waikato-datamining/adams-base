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
 * WekaInstancesRenderer.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseScrollPane;
import adams.gui.visualization.instances.InstancesTable;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Renders Weka Instances/Instance objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Instances.class, cls)
      || ClassLocator.hasInterface(Instance.class, cls);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel) {
    Instance 		inst;
    Instances 		data;
    InstancesTable 	table;
    BaseScrollPane	scrollPane;
    PlainTextRenderer	plain;
    SpreadSheet 	sheet;
    Row			row;
    int			i;
    SpreadSheetRenderer	sprenderer;

    if (obj instanceof Instances) {
      data = (Instances) obj;
      if (data.numInstances() == 0) {
	sheet = new DefaultSpreadSheet();
	row = sheet.getHeaderRow();
	row.addCell("I").setContentAsString("Index");
	row.addCell("N").setContentAsString("Name");
	row.addCell("T").setContentAsString("Type");
	row.addCell("C").setContentAsString("Class");
	for (i = 0; i < data.numAttributes(); i++) {
	  row = sheet.addRow();
	  row.addCell("I").setContent(i + 1);
	  row.addCell("N").setContentAsString(data.attribute(i).name());
	  row.addCell("T").setContentAsString(Attribute.typeToString(data.attribute(i)));
	  row.addCell("C").setContent((i == data.classIndex()) ? "true" : "");
	}
	sprenderer = new SpreadSheetRenderer();
	sprenderer.render(sheet, panel);
      }
      else {
        table = new InstancesTable(data);
        scrollPane = new BaseScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
      }
    }
    else {
      inst = (Instance) obj;
      if (inst.dataset() != null) {
	data = new Instances(inst.dataset(), 0);
	data.add((Instance) inst.copy());
	table      = new InstancesTable(data);
	scrollPane = new BaseScrollPane(table);
	panel.add(scrollPane, BorderLayout.CENTER);
      }
      else {
	plain = new PlainTextRenderer();
	plain.render(obj, panel);
      }
    }

    return null;
  }
}
