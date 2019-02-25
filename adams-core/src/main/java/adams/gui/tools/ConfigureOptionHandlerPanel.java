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
 * ConfigureOptionHandlerPanel.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.ClassLister;
import adams.core.NewInstance;
import adams.core.option.OptionHandler;
import adams.flow.control.Flow;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.DelayedActionRunnable;
import adams.gui.core.DelayedActionRunnable.AbstractAction;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchableBaseList;
import adams.gui.goe.GenericObjectEditorPanel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Allows configuring any class implementing {@link OptionHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConfigureOptionHandlerPanel
  extends BasePanel {

  private static final long serialVersionUID = 1459345403620296387L;

  /** the search panel. */
  protected BaseTextField m_TextSearch;

  /** the flow editor for displaying flows. */
  protected SearchableBaseList m_ListClasses;

  /** for configuring the class. */
  protected GenericObjectEditorPanel m_PanelObject;

  /** for updating the search etc. */
  protected DelayedActionRunnable m_DelayedAction;

  /**
   * Initializes the listeners.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DelayedAction = new DelayedActionRunnable(500, 50);
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel 	panel;

    super.initGUI();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    m_TextSearch = new BaseTextField();
    m_TextSearch.getDocument().addDocumentListener(new DocumentListener() {
      protected void update() {
	m_ListClasses.search(m_TextSearch.getText().length() == 0 ? null : m_TextSearch.getText(), false);
	m_DelayedAction.queue(new AbstractAction(m_DelayedAction) {
	  @Override
	  public String execute() {
	    SwingUtilities.invokeLater(() -> {
	      if (m_ListClasses.getModel().getSize() > 0) {
		int index = m_ListClasses.getSelectedIndex();
		if (index == -1)
		  index = 0;
		m_ListClasses.ensureIndexIsVisible(index);
	      }
	    });
	    return null;
	  }
	});
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	update();
      }
    });
    add(m_TextSearch, BorderLayout.NORTH);

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);

    m_ListClasses = new SearchableBaseList();
    m_ListClasses.search(null, false);
    m_ListClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListClasses.addListSelectionListener((ListSelectionEvent e) -> {
      String clsName = (String) m_ListClasses.getSelectedValue();
      if (clsName != null) {
        try {
          m_PanelObject.setCurrent(NewInstance.getSingleton().newObject(clsName));
	}
	catch (Exception ex) {
          // ignored
	}
      }
    });
    panel.add(new BaseScrollPane(m_ListClasses), BorderLayout.CENTER);

    m_PanelObject = new GenericObjectEditorPanel(OptionHandler.class, new Flow());
    panel.add(m_PanelObject, BorderLayout.SOUTH);
  }

  /**
   * Refreshes the class list.
   */
  public void refresh() {
    SwingWorker		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	MouseUtils.setWaitCursor(ConfigureOptionHandlerPanel.this);
	List<String> classes = ClassLister.getSingleton().getAllClassnames(true, new Class[]{OptionHandler.class});
	DefaultListModel model = new DefaultListModel();
	for (String cls: classes)
	  model.addElement(cls);
	m_ListClasses.setModel(model);
	return null;
      }
      @Override
      protected void done() {
	super.done();
	MouseUtils.setDefaultCursor(ConfigureOptionHandlerPanel.this);
      }
    };
    worker.execute();
  }
}
