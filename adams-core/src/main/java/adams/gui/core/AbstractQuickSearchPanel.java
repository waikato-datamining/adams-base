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
 * AbstractQuickSearchPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.gui.core.DelayedActionRunnable.AbstractAction;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.core.dotnotationtree.AcceptAllItemFilter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Ancestor for panels that .
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AbstractQuickSearchPanel
  extends BasePanel {

  private static final long serialVersionUID = -9124265315754284681L;

  /** the search panel. */
  protected BaseTextField m_TextSearch;

  /** the flow editor for displaying the items. */
  protected SearchableBaseList m_ListItems;

  /** the change listeners. */
  protected Set<ChangeListener> m_ChangeListeners;

  /** the selection listeners (double click or enter). */
  protected Set<ListSelectionListener> m_SelectionListeners;

  /** the cancel listeners (hitting ESC). */
  protected Set<ChangeListener> m_CancelListeners;

  /** for updating the search etc. */
  protected DelayedActionRunnable m_DelayedAction;

  /** the filter to use. */
  protected AbstractItemFilter m_ItemFilter;

  /**
   * Initializes the listeners.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ChangeListeners    = new HashSet<>();
    m_SelectionListeners = new HashSet<>();
    m_CancelListeners    = new HashSet<>();
    m_DelayedAction      = new DelayedActionRunnable(500, 50);
    m_ItemFilter         = new AcceptAllItemFilter();
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    m_TextSearch = new BaseTextField();
    m_TextSearch.getDocument().addDocumentListener(new DocumentListener() {
      protected void update() {
	m_ListItems.search(m_TextSearch.getText().length() == 0 ? null : m_TextSearch.getText(), false);
	m_DelayedAction.queue(new AbstractAction(m_DelayedAction) {
	  @Override
	  public String execute() {
	    SwingUtilities.invokeLater(() -> {
	      if (m_ListItems.getModel().getSize() > 0) {
		int index = m_ListItems.getSelectedIndex();
		if (index == -1)
		  index = 0;
		m_ListItems.ensureIndexIsVisible(index);
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
    m_TextSearch.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getModifiersEx() == 0) {
          int selIndex = m_ListItems.getSelectedIndex();
          boolean update = false;
          switch (e.getKeyCode()) {
	    case KeyEvent.VK_UP:
	      if (selIndex == -1) {
	        selIndex = 0;
	        update   = true;
	      }
	      else if (selIndex > 0) {
	        selIndex--;
	        update = true;
	      }
	      break;
	    case KeyEvent.VK_DOWN:
	      if (selIndex == -1) {
	        selIndex = 0;
	        update   = true;
	      }
	      else if (selIndex < m_ListItems.getModel().getSize() - 1) {
	        selIndex++;
	        update = true;
	      }
	      break;
	    case KeyEvent.VK_ENTER:
	      if (selIndex != -1) {
	        notifySelectionListeners();
	        e.consume();
	      }
	      break;
	    case KeyEvent.VK_ESCAPE:
	      notifyCancelListeners();
	      break;
	  }
	  if (update) {
            e.consume();
	    m_ListItems.setSelectedIndex(selIndex);
	  }
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    });
    add(m_TextSearch, BorderLayout.NORTH);

    m_ListItems = new SearchableBaseList();
    m_ListItems.search(null, false);
    m_ListItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListItems.addListSelectionListener((ListSelectionEvent e) -> notifyChangeListeners());
    m_ListItems.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isDoubleClick(e)) {
          if (m_ListItems.getSelectedIndex() != -1) {
            notifySelectionListeners();
            e.consume();
	  }
	}
	if (!e.isConsumed())
	  super.mouseClicked(e);
      }
    });
    add(new BaseScrollPane(m_ListItems), BorderLayout.CENTER);
  }

  /**
   * Adds the change listener to notify whenever the selected item changes.
   *
   * @param l 		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener from notifications whenever the selected item changes.
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
    ChangeEvent e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Adds the selection listener to notify whenever the user selects an item (double click or enter).
   *
   * @param l 		the listener to add
   */
  public void addSelectionListener(ListSelectionListener l) {
    m_SelectionListeners.add(l);
  }

  /**
   * Removes the selection listener from notifications whenever the user selects an item (double click or enter).
   *
   * @param l 		the listener to remove
   */
  public void removeSelectionListener(ListSelectionListener l) {
    m_SelectionListeners.remove(l);
  }

  /**
   * Notifies all selection listeners.
   */
  protected void notifySelectionListeners() {
    ListSelectionEvent e;

    e = new ListSelectionEvent(m_ListItems, m_ListItems.getSelectedIndex(), m_ListItems.getSelectedIndex(), m_ListItems.getValueIsAdjusting());
    for (ListSelectionListener l: m_SelectionListeners)
      l.valueChanged(e);
  }

  /**
   * Adds the cancel listener to notify when the user hits ESC.
   *
   * @param l 		the listener to add
   */
  public void addCancelListener(ChangeListener l) {
    m_CancelListeners.add(l);
  }

  /**
   * Removes the cancel listener from notifications when the user hits ESC.
   *
   * @param l 		the listener to remove
   */
  public void removeCancelListener(ChangeListener l) {
    m_CancelListeners.remove(l);
  }

  /**
   * Notifies all cancel listeners.
   */
  protected void notifyCancelListeners() {
    ChangeEvent e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_CancelListeners)
      l.stateChanged(e);
  }

  /**
   * Sets the item filter to use for filtering the items.
   *
   * @param value	the filter to use
   */
  public void setItemFilter(AbstractItemFilter value) {
    m_ItemFilter = value;
  }

  /**
   * Returns the current item filter in use.
   *
   * @return		the filter in use
   */
  public AbstractItemFilter getItemFilter() {
    return m_ItemFilter;
  }

  /**
   * Checks whether the item should be used.
   *
   * @param item	the item to check
   * @return		true if to use
   */
  protected boolean acceptItem(String item) {
    return m_ItemFilter.filter(item);
  }

  /**
   * Updates the model, applies the item filter to the items.
   *
   * @param items	the items to filter and then use
   */
  protected void updateModel(String[] items) {
    DefaultListModel<String>	model;

    model = new DefaultListModel<>();
    for (String item : items) {
      if (acceptItem(item))
        model.addElement(item);
    }
    m_ListItems.setModel(model);
  }

  /**
   * Returns the index of the user-selected item in the original list of items.
   *
   * @return		the index
   */
  public int getSelectedItemIndex() {
    return m_ListItems.getActualIndex(m_ListItems.getSelectedIndex());
  }
}
