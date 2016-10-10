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
 * InvestigatorPanelHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.workspace;

import adams.core.ClassLister;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;
import adams.gui.workspace.SerializablePanelHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializes/deserializes a Weka Investigator panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorPanelHandler
  extends SerializablePanelHandler<InvestigatorPanel> {

  private static final long serialVersionUID = 1928413024837299838L;

  public static final String KEY_DATA = "data";

  public static final String KEY_TABS = "tabs";

  /** the handlers. */
  protected AbstractInvestigatorTabHandler[] m_Handlers;

  /**
   * Initializes the handler.
   */
  public InvestigatorPanelHandler() {
    Class[]					classes;
    AbstractInvestigatorTabHandler		handler;
    List<AbstractInvestigatorTabHandler>	handlers;

    classes  = ClassLister.getSingleton().getClasses(AbstractInvestigatorTabHandler.class);
    handlers = new ArrayList<>();
    for (Class cls: classes) {
      try {
	handler = (AbstractInvestigatorTabHandler) cls.newInstance();
	if (handler instanceof DummyTabHandler)
	  continue;
	handlers.add(handler);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate tab handler: " + cls.getName());
	e.printStackTrace();
      }
    }
    handlers.add(new DummyTabHandler());
    m_Handlers = handlers.toArray(new AbstractInvestigatorTabHandler[handlers.size()]);
  }

  /**
   * Checks whether this handler can process the given panel.
   *
   * @param panel	the panel to check
   * @return		true if it can be processed
   */
  @Override
  public boolean handles(InvestigatorPanel panel) {
    return true;
  }

  /**
   * Generates a view of the panel that can be serialized.
   *
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  @Override
  public Object serialize(InvestigatorPanel panel) {
    Map<String,Object> 		result;
    List<Object> 		list;
    int				i;
    AbstractInvestigatorTab 	tab;

    result = new HashMap<>();

    // data
    list = new ArrayList<>();
    list.addAll(panel.getData());
    result.put(KEY_DATA, list);

    // tabs
    list = new ArrayList<>();
    for (i = 0; i < panel.getTabbedPane().getTabCount(); i++) {
      tab = (AbstractInvestigatorTab) panel.getTabbedPane().getComponentAt(i);
      for (AbstractInvestigatorTabHandler handler: m_Handlers) {
	if (handler.handles(tab)) {
	  list.add(tab.getClass().getName());
	  list.add(handler.getClass().getName());
	  list.add(handler.serialize(tab));
	  break;
	}
      }
    }
    result.put(KEY_TABS, list);

    return result;
  }

  /**
   * Deserializes the data and configures the panel.
   *
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   */
  @Override
  public void deserialize(InvestigatorPanel panel, Object data) {
    Map<String,Object>			items;
    List<Object>			list;
    int					i;
    Class				cls;
    AbstractInvestigatorTab		tab;
    AbstractInvestigatorTabHandler	handler;

    items = (Map<String,Object>) data;

    // data
    list = (List<Object>) items.get(KEY_DATA);
    for (Object obj: list)
      panel.getData().add((DataContainer) obj);

    // tabs
    list = (List<Object>) items.get(KEY_TABS);
    i = 0;
    while (i < list.size()) {
      try {
	cls = Class.forName((String) list.get(i));
	tab = (AbstractInvestigatorTab) cls.newInstance();
	panel.getTabbedPane().addTab(tab, false);

	cls     = Class.forName((String) list.get(i + 1));
	handler = (AbstractInvestigatorTabHandler) cls.newInstance();

	handler.deserialize(tab, list.get(i + 2));
      }
      catch (Exception e) {
	System.err.println("Failed to deserialize data (" + (i) + "-" + (i+2) + ")!");
	e.printStackTrace();
      }
      i += 3;
    }
  }
}
