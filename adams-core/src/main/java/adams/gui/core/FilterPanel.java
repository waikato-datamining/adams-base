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
 * FilterPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Compact filter component that allows user to enter text.
 * Any change in the text triggers a notification.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FilterPanel
  extends BasePanel {

  private static final long serialVersionUID = 7310268615807229210L;

  /** for undefined layout. */
  public final static int UNDEFINED = 0;

  /** for horizontal layout (label textfield button). */
  public final static int HORIZONTAL = 1;

  /** for vertical layout (label \n textfield button). */
  public final static int VERTICAL = 2;

  /** the type of layout to use. */
  protected int m_Layout;

  /** the label for the filter. */
  protected JLabel m_LabelFilter;

  /** the edit field for the filter. */
  protected BaseTextField m_TextFilter;

  /** the icon for clearing the text field. */
  protected JLabel m_LabelFilterClear;

  /** the filter listeners. */
  protected Set<ChangeListener> m_ChangeListeners;

  /**
   * Initializes the component.
   *
   * @param layout	the type of layout
   * @see		#HORIZONTAL
   * @see		#VERTICAL
   */
  public FilterPanel(int layout) {
    super();

    if ((layout == HORIZONTAL) || (layout == VERTICAL))
      m_Layout = layout;
    else
      throw new IllegalArgumentException("Unknown layout: " + layout);

    initGUI();
    finishInit();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Layout          = UNDEFINED;
    m_ChangeListeners = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    if (m_Layout == UNDEFINED)
      return;

    super.initGUI();

    m_TextFilter = new BaseTextField();
    m_TextFilter.getDocument().addDocumentListener(new DocumentListener() {
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
      protected void update() {
        m_LabelFilterClear.setEnabled(!m_TextFilter.getText().isEmpty());
	notifyChangeListeners();
      }
    });
    m_LabelFilterClear = new JLabel(GUIHelper.getIcon("clear_text.png"));
    m_LabelFilterClear.setToolTipText("Clears the filter text field");
    m_LabelFilterClear.setEnabled(false);
    m_LabelFilterClear.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isLeftClick(e)) {
	  e.consume();
          m_TextFilter.setText("");
        }
        else {
          super.mouseClicked(e);
        }
      }
    });

    m_LabelFilter = new JLabel("Filter");
    m_LabelFilter.setDisplayedMnemonic('F');
    m_LabelFilter.setLabelFor(m_TextFilter);

    if (m_Layout == VERTICAL) {
      panel = new JPanel(new BorderLayout(0, 0));
      panel.add(m_TextFilter, BorderLayout.CENTER);
      panel.add(m_LabelFilterClear, BorderLayout.EAST);
      setLayout(new GridLayout(2, 1, 5, 5));
      add(m_LabelFilter);
      add(panel);
    }
    else if (m_Layout == HORIZONTAL) {
      setLayout(new BorderLayout(0, 0));
      add(m_LabelFilter, BorderLayout.WEST);
      add(m_TextFilter, BorderLayout.CENTER);
      add(m_LabelFilterClear, BorderLayout.EAST);
    }
    else {
      throw new IllegalStateException("Unhandled layout: " + m_Layout);
    }
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    if (m_Layout == UNDEFINED)
      return;

    super.finishInit();
  }

  /**
   * Sets the text for the filter label.
   * Use "_" before the character that you want to use as mnemonic.
   *
   * @param value	the text
   */
  public void setFilterLabel(String value) {
    m_LabelFilter.setText(GUIHelper.stripMnemonic(value));
    if (GUIHelper.hasMnemonic(value))
      m_LabelFilter.setDisplayedMnemonic(GUIHelper.getMnemonic(value));
  }

  /**
   * Returns the text of the filter label (no mnemonic).
   *
   * @return		the text
   */
  public String getFilterLabel() {
    return m_LabelFilter.getText();
  }

  /**
   * Sets the filter string.
   *
   * @param value	the string
   */
  public void setFilter(String value) {
    m_TextFilter.setText(value);
  }

  /**
   * Returns the filter string.
   *
   * @return		the string
   */
  public String getFilter() {
    return m_TextFilter.getText();
  }

  /**
   * Adds the listener to the internal set.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener from the internal set.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Removes all change listeners.
   */
  public void clearChangeListeners() {
    m_ChangeListeners.clear();
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
}
