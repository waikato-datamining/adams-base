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
 * ClassHelpPanel.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.ClassLister;
import adams.core.option.UserMode;
import adams.gui.core.BaseHtmlEditorPane;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTextField;
import adams.gui.core.DelayedActionRunnable;
import adams.gui.core.DelayedActionRunnable.AbstractAction;
import adams.gui.core.Fonts;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchableBaseList;
import adams.gui.event.DoubleClickEvent;
import adams.gui.event.DoubleClickListener;
import adams.gui.help.AbstractHelpGenerator;
import adams.gui.help.HelpContainer;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple panel for lookup of help information on classes (if available).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClassHelpPanel
  extends BasePanel {

  private static final long serialVersionUID = 1459345403620296387L;

  /** the search panel. */
  protected BaseTextField m_TextSearch;

  /** the flow editor for displaying flows. */
  protected SearchableBaseList m_ListClasses;

  /** for displaying the help. */
  protected BaseHtmlEditorPane m_TextPaneHelp;

  /** the change listeners. */
  protected Set<ChangeListener> m_ChangeListeners;

  /** for updating the search etc. */
  protected DelayedActionRunnable m_DelayedAction;

  /** the double click listener. */
  protected DoubleClickListener m_DoubleClickListener;

  /**
   * Initializes the listeners.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ChangeListeners     = new HashSet<>();
    m_DelayedAction       = new DelayedActionRunnable(500, 50);
    m_DoubleClickListener = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    BaseSplitPane 	split;

    super.initGUI();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    m_TextSearch = new BaseTextField();
    m_TextSearch.getDocument().addDocumentListener(new DocumentListener() {
      protected void update() {
	m_ListClasses.search(m_TextSearch.getText().isEmpty() ? null : m_TextSearch.getText(), false);
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
	      notifyChangeListeners();
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

    split = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    split.setUISettingsParameters(getClass(), "Divider");
    split.setResizeWeight(0.0);
    add(split, BorderLayout.CENTER);

    m_ListClasses = new SearchableBaseList();
    m_ListClasses.search(null, false);
    m_ListClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListClasses.addListSelectionListener((ListSelectionEvent e) -> {
      String clsName = (String) m_ListClasses.getSelectedValue();
      if (clsName != null)
	displayHelp(clsName);
      else
        clearHelp();
      notifyChangeListeners();
    });
    m_ListClasses.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_DoubleClickListener == null)
	  return;
	if (MouseUtils.isDoubleClick(e)) {
	  String clsName = (String) m_ListClasses.getSelectedValue();
	  if (clsName != null)
	    m_DoubleClickListener.doubleClickOccurred(new DoubleClickEvent(ClassHelpPanel.this));
	}
      }
    });
    split.setTopComponent(new BaseScrollPane(m_ListClasses));

    m_TextPaneHelp = new BaseHtmlEditorPane();
    m_TextPaneHelp.setFont(Fonts.getMonospacedFont());
    m_TextPaneHelp.addDefaultHyperlinkListener();
    split.setBottomComponent(new BaseScrollPane(m_TextPaneHelp));
  }

  /**
   * Removes all text.
   */
  protected void clearHelp() {
    m_TextPaneHelp.setText("");
  }

  /**
   * Displays the help for the specified class.
   *
   * @param clsName	the class to display the help for
   */
  protected void displayHelp(String clsName) {
    HelpContainer	cont;

    cont = AbstractHelpGenerator.generateHelp(clsName, UserMode.HIGHEST);
    if (cont != null) {
      if (cont.isHtml())
	m_TextPaneHelp.setContentType("text/html");
      else
	m_TextPaneHelp.setContentType("text/plain");
      m_TextPaneHelp.setText(cont.getHelp());
      m_TextPaneHelp.setCaretPosition(0);
    }
  }

  /**
   * Updates the listed classes.
   *
   * @param managed	whether to list only managed classes or all
   * @see		#setListedClassNames(List)
   */
  public void listAllClassNames(boolean managed) {
    setListedClassNames(ClassLister.getSingleton().getAllClassnames(managed));
  }

  /**
   * Sets the class names to list.
   *
   * @param classes 	the class names
   */
  public void setListedClassNames(String[] classes) {
    setListedClassNames(Arrays.asList(classes));
  }

  /**
   * Sets the class names to list.
   *
   * @param classes 	the class names
   */
  public void setListedClassNames(List<String> classes) {
    DefaultListModel	model;

    model = new DefaultListModel();
    for (String cls: classes)
      model.addElement(cls);

    m_ListClasses.setModel(model);
  }

  /**
   * Returns the currently listed class names.
   *
   * @return		the class names
   */
  public List<String> getListedClassNames() {
    List<String>	result;
    ListModel 		model;
    int			i;

    result = new ArrayList<>();
    model  = m_ListClasses.getActualModel();
    for (i = 0; i < model.getSize(); i++)
      result.add(model.getElementAt(i).toString());

    return result;
  }

  /**
   * Sets the initially selected class.
   *
   * @param value	the class to select
   */
  public void setSelectedClass(String value) {
    m_ListClasses.setSelectedValue(value, true);
  }

  /**
   * Returns the currently selected class.
   *
   * @return		the class, null if none selected
   */
  public String getSelectedClassName() {
    if (m_ListClasses.getSelectedIndex() == -1)
      return null;
    else
      return (String) m_ListClasses.getSelectedValue();
  }

  /**
   * Adds the change listener to notify whenever the class changes.
   *
   * @param l 		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener from notifications whenever the class changes.
   *
   * @param l 		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all change listeners.
   */
  protected void notifyChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Sets the listener for double-clicks on the classes (eg shortcut for selection).
   *
   * @param l		the listener to use, null to remove
   */
  public void setDoubleClickListener(DoubleClickListener l) {
    m_DoubleClickListener = l;
  }

  /**
   * Returns the current double-click listener on classes.
   *
   * @return		the listener in use, can be null
   */
  public DoubleClickListener getDoubleClickListener() {
    return m_DoubleClickListener;
  }

  /**
   * Removes any specified double-click listener.
   */
  public void removeDoubleClickListener() {
    setDoubleClickListener(null);
  }
}
