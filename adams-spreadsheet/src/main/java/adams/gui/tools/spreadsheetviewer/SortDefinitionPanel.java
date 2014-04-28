/*
 * SortDefinitionPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.spreadsheetviewer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.SortSetupEvent;
import adams.gui.event.SortSetupEvent.EventType;

/**
 * Represents a single sorting definition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortDefinitionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 8473224107052394069L;

  /** the owning sort panel. */
  protected SortPanel m_Owner;

  /** the combobox with column names. */
  protected JComboBox m_ComboBoxNames;

  /** the type of sorting. */
  protected JComboBox m_ComboBoxSorting;

  /** the move up button. */
  protected JButton m_ButtonMoveUp;

  /** the move down button. */
  protected JButton m_ButtonMoveDown;

  /** the remove button. */
  protected JButton m_ButtonRemove;

  /**
   * Initializes the definition panel.
   *
   * @param owner	the owning sort panel
   */
  public SortDefinitionPanel(SortPanel owner) {
    super();
    m_Owner = owner;
    update();
    updateButtons();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JLabel	label;

    super.initGUI();

    setLayout(new FlowLayout(FlowLayout.LEFT));

    m_ComboBoxNames = new JComboBox();  // model gets set later
    m_ComboBoxNames.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setupModified();
      }
    });
    label = new JLabel("Names");
    label.setDisplayedMnemonic('N');
    label.setLabelFor(m_ComboBoxNames);
    add(label);
    add(m_ComboBoxNames);

    m_ComboBoxSorting = new JComboBox(new String[]{"Ascending", "Descending"});
    m_ComboBoxSorting.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setupModified();
      }
    });
    label = new JLabel("Sorting");
    label.setDisplayedMnemonic('S');
    label.setLabelFor(m_ComboBoxNames);
    add(label);
    add(m_ComboBoxSorting);

    m_ButtonMoveUp = new JButton(GUIHelper.getIcon("arrow_up.gif"));
    m_ButtonMoveUp.setToolTipText("Click to move condition up");
    m_ButtonMoveUp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	moveDefinition(true);
      }
    });
    add(m_ButtonMoveUp);

    m_ButtonMoveDown = new JButton(GUIHelper.getIcon("arrow_down.gif"));
    m_ButtonMoveDown.setToolTipText("Click to move condition down");
    m_ButtonMoveDown.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	moveDefinition(false);
      }
    });
    add(m_ButtonMoveDown);

    m_ButtonRemove = new JButton(GUIHelper.getIcon("delete.gif"));
    m_ButtonRemove.setToolTipText("Click to remove condition");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	removeDefinition();
      }
    });
    add(m_ButtonRemove);
  }

  /**
   * Returns the owning panel.
   *
   * @return		the owner
   */
  public SortPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the selected column name.
   *
   * @return		the column name, null if none selected
   */
  public String getColumnName() {
    if (m_ComboBoxNames.getSelectedIndex() == -1)
      return null;
    else
      return (String) m_ComboBoxNames.getSelectedItem();
  }

  /**
   * Returns whether ascending or descending is used.
   *
   * @return		true if ascending sorting is used
   */
  public boolean isAscending() {
    return (m_ComboBoxSorting.getSelectedIndex() == 0);
  }

  /**
   * Moves itself around in the list of sorting panels.
   */
  protected void moveDefinition(boolean up) {
    getOwner().moveDefinition(this, up);
  }

  /**
   * Removes itself from the list of sorting panels.
   */
  protected void removeDefinition() {
    getOwner().removeDefinition(this);
  }

  /**
   * Resets the comboboxes.
   */
  public void update() {
    List<String>	names;

    names = getOwner().getColumnNames();
    m_ComboBoxNames.setModel(new DefaultComboBoxModel(names.toArray()));
    if (names.size() > 0)
      m_ComboBoxNames.setSelectedIndex(0);

    m_ComboBoxSorting.setSelectedIndex(0);
  }

  /**
   * Updates the enabled status of the buttons.
   */
  public void updateButtons() {
    m_ButtonMoveUp.setEnabled(!getOwner().isFirstDefinition(this));
    m_ButtonMoveDown.setEnabled(!getOwner().isLastDefinition(this));
  }
  
  /**
   * Sends an event that the setup was modified.
   */
  protected void setupModified() {
    getOwner().notifySortSetupListeners(new SortSetupEvent(m_Owner, this, EventType.MODIFIED));
  }

  /**
   * Returns a short description of the current setup.
   *
   * @return		the current setup
   */
  @Override
  public String toString() {
    return hashCode() + ", colname=" + getColumnName() + ", asc=" + isAscending();
  }
}
