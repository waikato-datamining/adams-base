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
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.ClassLister;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BrowserHelper.DefaultHyperlinkListener;
import adams.gui.core.Fonts;
import adams.gui.core.SearchableBaseList;
import adams.gui.help.AbstractHelpGenerator;
import adams.gui.help.HelpContainer;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Simple panel for lookup of help information on classes (if available).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassHelpPanel
  extends BasePanel {

  private static final long serialVersionUID = 1459345403620296387L;

  /** the search panel. */
  protected JTextField m_TextSearch;

  /** the flow editor for displaying flows. */
  protected SearchableBaseList m_ListClasses;

  /** for displaying the help. */
  protected JEditorPane m_TextPaneHelp;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    BaseSplitPane 	split;
    List<String> 	classes;

    super.initGUI();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    m_TextSearch = new JTextField();
    m_TextSearch.getDocument().addDocumentListener(new DocumentListener() {
      protected void update() {
	m_ListClasses.search(m_TextSearch.getText().length() == 0 ? null : m_TextSearch.getText(), false);
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
    add(split, BorderLayout.CENTER);

    classes = getClasses(true);
    m_ListClasses = new SearchableBaseList(classes.toArray(new String[classes.size()]));
    m_ListClasses.search(null, false);
    m_ListClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListClasses.addListSelectionListener((ListSelectionEvent e) -> {
      String clsName = (String) m_ListClasses.getSelectedValue();
      if (clsName == null)
	return;
      displayHelp(clsName);
    });
    split.setTopComponent(new BaseScrollPane(m_ListClasses));

    m_TextPaneHelp = new JEditorPane();
    m_TextPaneHelp.setEditable(false);
    m_TextPaneHelp.setFont(Fonts.getMonospacedFont());
    m_TextPaneHelp.setAutoscrolls(true);
    m_TextPaneHelp.addHyperlinkListener(new DefaultHyperlinkListener());
    split.setBottomComponent(new BaseScrollPane(m_TextPaneHelp));

    split.setResizeWeight(1.0);
    split.setDividerLocation(200);
  }

  /**
   * Returns the list of classes to use.
   *
   * @param all		whether to list all classes or only managed ones
   * @return		the class names
   */
  protected List<String> getClasses(boolean all) {
    List<String> 	classes;
    int			i;
    String		name;
    Iterator<String>	iter;

    classes = new ArrayList<>();
    if (all) {
      // all classes
      iter = ClassLocator.getSingleton().getCache().packages();
      while (iter.hasNext()) {
	for (String cls: ClassLocator.getSingleton().getCache().getClassnames(iter.next())) {
	  if (cls.contains("$"))
	    continue;
	  classes.add(cls);
	}
      }
    }
    else {
      // only managed classes
      for (String supercls : ClassLister.getSingleton().getSuperclasses()) {
	for (Class cls : ClassLister.getSingleton().getClasses(supercls))
	  classes.add(cls.getName());
      }
    }
    Collections.sort(classes);
    i = 0;
    name = "";
    while (i < classes.size()) {
      if (!name.equals(classes.get(i))) {
	name = classes.get(i);
	i++;
      }
      else {
	classes.remove(i);
      }
    }

    return classes;
  }

  /**
   * Displays the help for the specified class.
   *
   * @param clsName	the class to display the help for
   */
  protected void displayHelp(String clsName) {
    HelpContainer	cont;

    cont = AbstractHelpGenerator.generateHelp(clsName);
    if (cont != null) {
      if (cont.isHtml())
	m_TextPaneHelp.setContentType("text/html");
      else
	m_TextPaneHelp.setContentType("text/plain");
      m_TextPaneHelp.setText(cont.getHelp());
      m_TextPaneHelp.setCaretPosition(0);
    }
  }
}
