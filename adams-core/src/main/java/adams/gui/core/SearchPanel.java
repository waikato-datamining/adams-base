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
 * SearchPanel.java
 * Copyright (C) 2009-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;


import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A panel that displays a search box. With optional display of
 * regular expression checkbox.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SearchPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 984508072481111930L;

  /**
   * The layout of the panel.
   */
  public enum LayoutType {
    /** horizontal. */
    HORIZONTAL,
    /** vertical. */
    VERTICAL
  }

  /** the layout. */
  protected LayoutType m_LayoutType;

  /** whether regular expressions are enabled. */
  protected boolean m_RegExpEnabled;

  /** whether incremental search is enabled. */
  protected boolean m_Incremental;

  /** the prefix (caption + optional mnemonic indicator). */
  protected String m_Prefix;

  /** the caption (+ optional mnemonic indicator) for the search button. */
  protected String m_ButtonCaption;

  /** the label for the text field. */
  protected JLabel m_LabelPrefix;

  /** the panel for the search text and clear button. */
  protected JPanel m_PanelSearchText;

  /** the text field for entering the search text. */
  protected BaseTextField m_TextSearch;

  /** whether to use regular expressions for searching or plain string
   * comparison. */
  protected BaseCheckBox m_CheckboxRegExp;

  /** the button for clearing the search. */
  protected JLabel m_ButtonClear;

  /** the button for performing the search. */
  protected JLabel m_ButtonSearch;

  /** the button panel. */
  protected JPanel m_PanelButtons;

  /** the panel with the buttons and search box. */
  protected JPanel m_PanelWidgets;

  /** the widgets. */
  protected List<Component> m_Widgets;

  /** the listeners for a search being initiated. */
  protected Set<SearchListener> m_SearchListeners;

  /** the minimum number of characters before triggering search events. */
  protected int m_MinimumChars;

  /** the last search term. */
  protected String m_LastSearch;

  /**
   * Initializes the panel.
   *
   * @param layout	the layout of the panel
   * @param regExp	whether to display regular expressions
   */
  public SearchPanel(LayoutType layout, boolean regExp) {
    this(layout, regExp, null, "");
  }

  /**
   * Initializes the panel.
   *
   * @param layout	the layout of the panel
   * @param regExp	whether to display regular expressions
   * @param prefix	the prefix label to use, use null to ignore.
   * 			Mnemonics can be indicated by a preceding underscore "_".
   * @param button	the caption for the button.
   * 			Mnemonics can be indicated by a preceding underscore "_".
   */
  public SearchPanel(LayoutType layout, boolean regExp, String prefix, String button) {
    this(layout, regExp, prefix, false, button);
  }

  /**
   * Initializes the panel with optional incremental search (and no search
   * button if incremental search turned on).
   *
   * @param layout	the layout of the panel
   * @param regExp	whether to display regular expressions
   * @param prefix	the prefix label to use, use null to ignore.
   * 			Mnemonics can be indicated by a preceding underscore "_".
   * @param incremental	if true then no search button will be displayed
   * @param button	the caption for the button.
   * 			Mnemonics can be indicated by a preceding underscore "_".
   */
  public SearchPanel(LayoutType layout, boolean regExp, String prefix, boolean incremental, String button) {
    super();

    m_LayoutType    = layout;
    m_RegExpEnabled = regExp;
    m_Prefix        = prefix;
    m_ButtonCaption = (incremental ? null : button);
    m_Incremental   = incremental;

    initGUI();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_LayoutType      = null;
    m_SearchListeners = new HashSet<>();
    m_MinimumChars    = 1;
    m_LastSearch      = "";
    m_Widgets         = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    int		size;
    char	mnemonic;
    String	caption;

    if (m_LayoutType == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout());

    if (m_LayoutType == LayoutType.VERTICAL)
      size = 10;
    else
      size = 20;

    m_PanelSearchText = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    m_TextSearch = new BaseTextField(size);
    m_TextSearch.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (m_ButtonSearch != null)
          m_ButtonSearch.setEnabled(isValidSearch());
        if (!m_Incremental) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            e.consume();
            performSearch();
          }
        }
        if (!e.isConsumed())
          super.keyPressed(e);
      }
    });
    m_TextSearch.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          BasePopupMenu menu = createPopup(e);
          menu.show(m_TextSearch, e.getX(), e.getY());
        }
        if (!e.isConsumed())
          super.mouseClicked(e);
      }
    });
    m_TextSearch.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
        search();
      }
      public void insertUpdate(DocumentEvent e) {
        search();
      }
      public void changedUpdate(DocumentEvent e) {
        search();
      }
      protected void search() {
        if (m_Incremental) {
          if (m_TextSearch.getText().length() >= m_MinimumChars)
            performSearch();
          else
            clearSearch();
        }
      }
    });

    if (hasPrefix()) {
      mnemonic      = GUIHelper.getMnemonic(m_Prefix);
      caption       = GUIHelper.stripMnemonic(m_Prefix);
      m_LabelPrefix = new JLabel(caption);
      m_LabelPrefix.setLabelFor(m_TextSearch);
      if (mnemonic != '\0')
        m_LabelPrefix.setDisplayedMnemonic(mnemonic);
      else
        m_LabelPrefix.setDisplayedMnemonicIndex(-1);
    }

    m_CheckboxRegExp = new BaseCheckBox("Regex");
    m_CheckboxRegExp.setToolTipText("Whether to perform regular expression matching or just look for occurrences of the entered search string");

    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    m_ButtonClear = new JLabel(GUIHelper.getIcon("clear_text.png"));
    m_ButtonClear.setToolTipText("Clears the search text");
    m_ButtonClear.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e)) {
	  e.consume();
	  m_TextSearch.setText("");
	  performSearch();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });

    m_ButtonSearch = new JLabel(GUIHelper.getIcon("find.gif"));
    m_ButtonSearch.setToolTipText("Performs the search");
    setButtonCaption(m_ButtonCaption);
    m_ButtonSearch.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e)) {
	  e.consume();
	  performSearch();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });

    m_PanelWidgets = new JPanel();
    add(m_PanelWidgets, BorderLayout.CENTER);

    updateLayout();
  }

  /**
   * Updates the layout.
   */
  protected void updateLayout() {
    removeFromWidgetsPanel(m_LabelPrefix);
    removeFromWidgetsPanel(m_TextSearch);
    removeFromWidgetsPanel(m_PanelSearchText);
    removeFromWidgetsPanel(m_PanelButtons);
    removeFromWidgetsPanel(m_ButtonClear);

    if (m_Incremental)
      m_TextSearch.setToolTipText("Searches as soon as you type");
    else
      m_TextSearch.setToolTipText("Enter the search term and hit ENTER or click on the search button to perform the search");

    if (hasPrefix())
      addToWidgetsPanel(m_LabelPrefix);

    if (m_LayoutType == LayoutType.HORIZONTAL) {
      addToWidgetsPanel(m_TextSearch);
      addToWidgetsPanel(m_PanelSearchText);
      addToWidgetsPanel(m_ButtonClear);
    }
    else {
      m_PanelSearchText.removeAll();
      m_PanelSearchText.add(m_TextSearch);
      m_PanelSearchText.add(m_ButtonClear);
      addToWidgetsPanel(m_PanelSearchText);
    }

    m_PanelButtons.removeAll();
    if (isRegularExpressionEnabled())
      m_PanelButtons.add(m_CheckboxRegExp);
    if (!m_Incremental)
      m_PanelButtons.add(m_ButtonSearch);
    addToWidgetsPanel(m_PanelButtons);
  }

  /**
   * Generates a popup for the search field.
   *
   * @param e		the mouse event
   */
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu	menu;
    JMenuItem		menuitem;

    menu = new BasePopupMenu();

    menuitem = new JMenuItem("Cut", GUIHelper.getIcon("cut.gif"));
    menuitem.addActionListener((ActionEvent ex) -> m_TextSearch.cut());
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.addActionListener((ActionEvent ex) -> m_TextSearch.copy());
    menu.add(menuitem);

    menuitem = new JMenuItem("Paste", GUIHelper.getIcon("paste.gif"));
    menuitem.addActionListener((ActionEvent ex) -> m_TextSearch.paste());
    menu.add(menuitem);

    menu.addSeparator();
    if (isRegularExpressionEnabled())
      menuitem = new JMenuItem("Disable regexp search", GUIHelper.getEmptyIcon());
    else
      menuitem = new JMenuItem("Enable regexp search", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ex) -> setRegularExpressionEnabled(!isRegularExpressionEnabled()));
    menu.add(menuitem);

    return menu;
  }

  /**
   * Sets the enabled state of the components.
   *
   * @param value	if true then the components are enabled otherwise not
   */
  public void setEnabled(boolean value) {
    if (hasPrefix())
      m_LabelPrefix.setEnabled(value);
    m_TextSearch.setEnabled(value);
    m_CheckboxRegExp.setEnabled(value);
    if (m_ButtonSearch != null)
      m_ButtonSearch.setEnabled(value);
  }

  /**
   * Returns whether the panel is enabled or not.
   *
   * @return		true if the search components are enabled.
   */
  public boolean isEnabled() {
    return m_TextSearch.isEditable();
  }

  /**
   * Returns the type of layout.
   *
   * @return		the layout
   */
  public LayoutType getLayoutType() {
    return m_LayoutType;
  }

  /**
   * Checks whether a prefix string was provided.
   *
   * @return		true if a prefix string was supplied
   */
  protected boolean hasPrefix() {
    return (m_Prefix != null);
  }

  /**
   * Sets whether to use regexp search or plain substring matching.
   *
   * @param value	true if regular expression search is to be used
   */
  public void setRegularExpressionEnabled(boolean value) {
    m_RegExpEnabled = value;
    updateLayout();
  }

  /**
   * Returns whether regular expressions are turned on.
   *
   * @return		true if regular expressions are enabled
   */
  public boolean isRegularExpressionEnabled() {
    return m_RegExpEnabled;
  }

  /**
   * Sets the search text.
   *
   * @param value	the new search text
   */
  public void setSearchText(String value) {
    m_TextSearch.setText(value);
  }

  /**
   * Returns the text in the search box.
   *
   * @return		the current search text
   */
  public String getSearchText() {
    return m_TextSearch.getText();
  }

  /**
   * Sets whether the search text is a regular expression one or not.
   *
   * @param value	if true then regular expression search will be enabled
   */
  public void setRegularExpression(boolean value) {
    m_CheckboxRegExp.setSelected(value);
  }

  /**
   * Returns whether the search text is a regular expression one or not.
   *
   * @return		true if a regular expression search
   */
  public boolean isRegularExpression() {
    return (m_RegExpEnabled && m_CheckboxRegExp.isSelected());
  }

  /**
   * Sets whether to enabled incremental search.
   *
   * @param value	true if incremental search enabled
   */
  public void setIncremental(boolean value) {
    m_Incremental = value;
    updateLayout();
  }

  /**
   * Returns whether incremental search is enabled.
   *
   * @return		true if incremental search enabled
   */
  public boolean isIncremental() {
    return m_Incremental;
  }

  /**
   * Sets the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @param value	the minimum number of characters (>= 1)
   */
  public void setMinimumChars(int value) {
    if (value >= 1)
      m_MinimumChars = value;
  }

  /**
   * Returns the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @return		the minimum number of characters (>= 1)
   */
  public int getMinimumChars() {
    return m_MinimumChars;
  }

  /**
   * Sets the number of columns for the text field.
   *
   * @param value	the number of columns (>0)
   */
  public void setTextColumns(int value) {
    if (value > 0)
      m_TextSearch.setColumns(value);
    else
      System.err.println("Number of columns must be >0 (provided: " + value + ")!");
  }

  /**
   * Returns the number of columns of the text field.
   *
   * @return		the number of columns (>0)
   */
  public int getTextColumns() {
    return m_TextSearch.getColumns();
  }

  /**
   * The caption for the search button (incl. mnemonic). Ignored if button is
   * not being displayed.
   *
   * @param value	the caption
   */
  public void setButtonCaption(String value) {
    if (m_ButtonSearch == null)
      return;

    m_ButtonCaption = value;
    m_ButtonSearch.setText(GUIHelper.stripMnemonic(m_ButtonCaption));
  }

  /**
   * Returns the current caption of the search button (incl. mnemonic).
   *
   * @return		the caption or null if button not displayed
   */
  public String getButtonCaption() {
    if (m_ButtonSearch == null)
      return null;
    else
      return m_ButtonCaption;
  }

  /**
   * Removes all components from the panel holding the Widgets.
   */
  public void clearWidgetsPanel() {
    m_Widgets.clear();
    updateWidgets();
  }

  /**
   * Adds the component to the panel with the Widgets.
   *
   * @param comp	the component to add
   * @param index	the index where to add the component, -1 adds the
   * 			component at the end
   */
  public void addToWidgetsPanel(Component comp, int index) {
    removeFromWidgetsPanel(comp);
    if (index == -1)
      m_Widgets.add(comp);
    else
      m_Widgets.add(index, comp);
    updateWidgets();
  }

  /**
   * Adds the component to the panel with the Widgets.
   *
   * @param comp	the component to add
   */
  public void addToWidgetsPanel(Component comp) {
    addToWidgetsPanel(comp, -1);
  }

  /**
   * Removes the component from the panel with the Widgets.
   *
   * @param comp	the component to remove
   */
  public void removeFromWidgetsPanel(Component comp) {
    if (comp != null) {
      m_Widgets.remove(comp);
      updateWidgets();
    }
  }

  /**
   * Lays out the widgets according to the layout.
   */
  protected void updateWidgets() {
    GridBagLayout	layout;
    GridBagConstraints	c;
    int			i;

    m_PanelWidgets.removeAll();
    layout = new GridBagLayout();
    m_PanelWidgets.setLayout(layout);

    for (i = 0; i < m_Widgets.size(); i++) {
      c = new GridBagConstraints();
      switch (m_LayoutType) {
        case HORIZONTAL:
          c.gridx = i;
          c.gridy = 0;
          if (m_Widgets.get(i) == m_TextSearch) {
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
          }
          if (i > 0)
            c.insets = new Insets(0, 5, 0, 0);
          break;
        case VERTICAL:
          c.gridx = 0;
          c.gridy = i;
          c.fill  = GridBagConstraints.HORIZONTAL;
          c.weightx = 1.0;
          if (i > 0)
            c.insets = new Insets(5, 0, 0, 0);
          break;
        default:
          throw new IllegalStateException("Unhandled layout type: " + m_LayoutType);
      }
      layout.setConstraints(m_Widgets.get(i), c);
      m_PanelWidgets.add(m_Widgets.get(i));
    }

    invalidate();
    revalidate();
    doLayout();
  }

  /**
   * Adds the specified listener to the internal list of listeners.
   *
   * @param l		the listener to add
   */
  public void addSearchListener(SearchListener l) {
    m_SearchListeners.add(l);
  }

  /**
   * Removes the specified listener from the internal list of listeners.
   *
   * @param l		the listener to remove
   */
  public void removeSearchListener(SearchListener l) {
    m_SearchListeners.remove(l);
  }

  /**
   * Sends out the event to all listeners.
   *
   * @param e		the event to send
   */
  protected void notifySearchListeners(SearchEvent e) {
    Iterator<SearchListener>	iter;

    iter = m_SearchListeners.iterator();
    while (iter.hasNext())
      iter.next().searchInitiated(e);
  }

  /**
   * Initiates a search.
   */
  protected void performSearch() {
    if (!isValidSearch())
      return;
    m_LastSearch = getSearchText();
    notifySearchListeners(
      new SearchEvent(
        SearchPanel.this, getSearchText(), isRegularExpression()));
  }

  /**
   * Clears a previous search if the last search term had a non-zero length.
   */
  protected void clearSearch() {
    if (m_LastSearch.length() > 0) {
      m_LastSearch = "";
      notifySearchListeners(
        new SearchEvent(
          SearchPanel.this, "", isRegularExpression()));
    }
  }

  /**
   * Checks whether the search is valid.
   *
   * @return		true if the search is valid
   */
  protected boolean isValidSearch() {
    if (getSearchText().length() == 0)
      return true;

    if (!isRegularExpression()) {
      return true;
    }
    else {
      try {
        Pattern.compile(getSearchText());
        return true;
      }
      catch (Exception e) {
        return false;
      }
    }
  }

  /**
   * Imitates a click on the search button to initiate a search.
   */
  public void search() {
    performSearch();
  }

  /**
   * The text field for the search tries to grab the focus.
   */
  public void grabFocus() {
    m_TextSearch.grabFocus();
  }

  /**
   * In case of vertical display, the size of the panel can be adjusted to
   * the specified width.
   *
   * @param width	the new width
   */
  public void updateWidth(int width) {
    if (getLayoutType() != LayoutType.VERTICAL)
      return;

    setSize(width, getHeight());
  }
}
