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
 * ClassHelp.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.ClassLister;
import adams.core.option.HtmlHelpProducer;
import adams.core.option.OptionHandler;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BrowserHelper.DefaultHyperlinkListener;
import adams.gui.core.ConsolePanel;
import adams.gui.core.Fonts;
import adams.gui.core.SearchableBaseList;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Displays help for any selected class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassHelp
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -6548349613973153076L;

  /** the search panel. */
  protected JTextField m_TextSearch;

  /** the flow editor for displaying flows. */
  protected SearchableBaseList m_ListClasses;

  /** for displaying the help. */
  protected JEditorPane m_TextPaneHelp;

  /** the help producer. */
  protected HtmlHelpProducer m_HelpProducer;

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ClassHelp(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_HelpProducer = new HtmlHelpProducer();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Class help";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "java.png";
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_HELP;
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    JPanel		panel;
    BaseSplitPane	split;
    List<String>	classes;
    int			i;
    String		name;

    panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
    panel.add(m_TextSearch, BorderLayout.NORTH);

    split = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    panel.add(split, BorderLayout.CENTER);

    classes = new ArrayList<>();
    for (String supercls: ClassLister.getSingleton().getSuperclasses()) {
      for (Class cls: ClassLister.getSingleton().getClasses(supercls))
	classes.add(cls.getName());
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
    m_ListClasses = new SearchableBaseList(classes.toArray(new String[classes.size()]));
    m_ListClasses.search(null, false);
    m_ListClasses.setPreferredSize(new Dimension(400, 200));
    m_ListClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListClasses.addListSelectionListener((ListSelectionEvent e) -> {
      String clsName = (String) m_ListClasses.getSelectedValue();
      if (clsName == null)
	return;
      try {
	Class cls = Class.forName(clsName);
	Object obj = cls.newInstance();
	String help = null;
	boolean html = false;
	if (obj instanceof OptionHandler) {
	  m_HelpProducer.produce((OptionHandler) obj);
	  help = m_HelpProducer.toString();
	  html = true;
	}
	else {
	  try {
	    Method method = cls.getMethod("globalInfo");
	    help = (String) method.invoke(obj);
	  }
	  catch (Exception ex2) {
	    help = "";
	  }
	}
	if (html)
	  m_TextPaneHelp.setContentType("text/html");
	else
	  m_TextPaneHelp.setContentType("text/plain");
	m_TextPaneHelp.setText(help);
	m_TextPaneHelp.setCaretPosition(0);
      }
      catch (Exception ex) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate class: " + clsName, ex);
      }
    });
    split.setTopComponent(new BaseScrollPane(m_ListClasses));

    m_TextPaneHelp = new JEditorPane();
    m_TextPaneHelp.setEditable(false);
    m_TextPaneHelp.setFont(Fonts.getMonospacedFont());
    m_TextPaneHelp.setAutoscrolls(true);
    m_TextPaneHelp.addHyperlinkListener(new DefaultHyperlinkListener());
    split.setBottomComponent(new BaseScrollPane(m_TextPaneHelp));

    createChildFrame(panel, 600, 600);
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }
}