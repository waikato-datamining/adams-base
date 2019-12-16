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
 * FlowContainerRenderer.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.core.MessageCollection;
import adams.data.conversion.ContainerToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.AbstractContainer;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Renders flow containers as tables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FlowContainerRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /** the last setup. */
  protected SpreadSheetTable m_LastTable;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(AbstractContainer.class, cls);
  }

  /**
   * Checks whether the renderer can use a cached setup to render an object.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  @Override
  public boolean canRenderCached(Object obj, JPanel panel) {
    return (m_LastTable != null);
  }

  /**
   * Converts the container into a spreadsheet.
   *
   * @param cont	the container to convert
   * @param errors	for collecting errors
   * @return		the spreadsheet, null if failed to convert
   */
  protected SpreadSheet containerToSheet(AbstractContainer cont, MessageCollection errors) {
    SpreadSheet 		result;
    ContainerToSpreadSheet	conv;
    String 			msg;

    result = null;
    conv  = new ContainerToSpreadSheet();
    conv.setInput(cont);
    msg = conv.convert();
    if (msg == null)
      result = (SpreadSheet) conv.getOutput();
    else
      errors.add(msg);
    conv.cleanUp();

    return result;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRenderCached(Object obj, JPanel panel) {
    String			result;
    AbstractContainer		cont;
    SpreadSheet			sheet;
    MessageCollection		errors;
    BaseScrollPane		scrollPane;

    result = null;
    cont   = (AbstractContainer) obj;
    errors = new MessageCollection();
    sheet  = containerToSheet(cont, errors);
    if (errors.isEmpty() && (sheet != null)) {
      m_LastTable.setModel(new SpreadSheetTableModel(sheet));
      scrollPane = new BaseScrollPane(m_LastTable);
      panel.add(scrollPane, BorderLayout.CENTER);
    }
    else {
      if (errors.isEmpty())
        result = "Failed to convert container!";
      else
        result = errors.toString();
    }

    return result;
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
    String 			result;
    AbstractContainer		cont;
    SpreadSheet			sheet;
    MessageCollection		errors;
    SpreadSheetTable 		table;
    BaseScrollPane		scrollPane;

    result = null;
    cont   = (AbstractContainer) obj;
    errors = new MessageCollection();
    sheet  = containerToSheet(cont, errors);
    if (errors.isEmpty() && (sheet != null)) {
      table = new SpreadSheetTable(sheet);
      scrollPane = new BaseScrollPane(table);
      panel.add(scrollPane, BorderLayout.CENTER);
      m_LastTable = table;
    }
    else {
      if (errors.isEmpty())
        result = "Failed to convert container!";
      else
        result = errors.toString();
    }

    return result;
  }
}
