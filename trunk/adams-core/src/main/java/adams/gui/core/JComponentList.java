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
 * JComponentList.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

/**
 * A specialized JList that displays/renders arbitrary JComponents.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see JList
 */
public class JComponentList
  extends JList {

  /** for serialization. */
  private static final long serialVersionUID = -5162054751544750195L;

  /** the list itself. */
  protected JComponentList m_Self;

  /**
   * An interface for list items that provide a popup menu.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static interface PopupMenuProvider {

    /**
     * Returns whether this particular list item has a popup menu at the
     * moment.
     *
     * @return		true if the provider has a popup menu
     */
    public boolean hasPopupMenu();

    /**
     * Returns the popup menu.
     *
     * @return		the popup menu
     * @see		#hasPopupMenu()
     */
    public JPopupMenu getPopupMenu();
  }

  /**
   * A specialized JLabel to be used in a JComponentList, which provides a
   * rudimentary popup menu.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class LabelListItem
    extends JLabel
    implements PopupMenuProvider, ActionListener {

    /** for serialization. */
    private static final long serialVersionUID = -1281892849183281829L;

    /**
     * Available actions.
     *
     * @author  fracpete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static enum Actions {
      /** the remove action. */
      REMOVE("Remove"),
      /** the remove all action. */
      REMOVE_ALL("Remove all");

      /** the display string. */
      protected String m_Display;

      /**
       * Initializes the action.
       *
       * @param display		the display string to use
       */
      private Actions(String display) {
	m_Display = display;
      }

      /**
       * Returns the display string.
       *
       * @return		the display string
       */
      public String getDisplay() {
	return m_Display;
      }
    }

    /** the list this item belongs to. */
    protected JComponentList m_List;

    /** contains the disabled actions. */
    protected HashSet<Actions> m_Disabled;

    /** contains overrides for the display string per action. */
    protected Hashtable<Actions,String> m_Display;

    /**
     * Initializes the item.
     *
     * @param list	the list this item belongs to
     */
    public LabelListItem(JComponentList list) {
      super();

      m_List     = list;
      m_Disabled = new HashSet<Actions>();
      m_Display  = new Hashtable<Actions,String>();
    }

    /**
     * Returns the list this item belongs to.
     *
     * @return		the list
     */
    public JComponentList getList() {
      return m_List;
    }

    /**
     * Checks whether the given action is enabled.
     *
     * @param action	the action to check
     * @return		true if the action is enabled
     */
    public boolean isEnabled(Actions action) {
      return (!m_Disabled.contains(action));
    }

    /**
     * Sets the enabled/disabled state of an action, i.e., whether it shows
     * up in the menu or not. By default, all actions are enabled.
     *
     * @param action	the action to set the state for
     * @param enabled	if true the action will be displayed in the menu
     */
    public void setEnabled(Actions action, boolean enabled) {
      if (enabled)
	m_Disabled.remove(action);
      else
	m_Disabled.add(action);
    }

    /**
     * Checks whether the given action's display string is overridden.
     *
     * @param action	the action to check
     * @return		true if the action has an override
     */
    public boolean hasDisplayOverride(Actions action) {
      return m_Display.containsKey(action);
    }

    /**
     * Sets an override for the display string for the given action.
     *
     * @param action	the action to set an override for
     * @param display	the override
     */
    public void addDisplayOverride(Actions action, String display) {
      m_Display.put(action, display);
    }

    /**
     * Removes the display override for the given action.
     *
     * @param action	the action to remove the override for
     */
    public void removeDisplayOverride(Actions action) {
      m_Display.remove(action);
    }

    /**
     * Returns the display override for the given action.
     *
     * @param action	the action to get the override for
     * @return		the override string
     */
    public String getDisplayOverride(Actions action) {
      return m_Display.get(action);
    }

    /**
     * Returns whether this particular list item has a popup menu at the
     * moment.
     *
     * @return		true if the provider has a popup menu
     */
    public boolean hasPopupMenu() {
      return true;
    }

    /**
     * Returns the popup menu.
     *
     * @return		the popup menu
     * @see		#hasPopupMenu()
     */
    public JPopupMenu getPopupMenu() {
      JPopupMenu	result;
      JMenuItem		item;

      result = new JPopupMenu();

      for (Actions action: Actions.values()) {
	if (!isEnabled(action))
	  continue;

	item = new JMenuItem();
	if (hasDisplayOverride(action))
	  item.setText(getDisplayOverride(action));
	else
	  item.setText(action.getDisplay());
	item.setActionCommand(action.toString());
	item.addActionListener(this);
	result.add(item);
      }

      return result;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e		the event
     */
    public abstract void actionPerformed(ActionEvent e);
  }

  /**
   * A specialized JCheckBox to be used in a JComponentList, which provides a
   * rudimentary popup menu.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class CheckBoxListItem
    extends JCheckBox
    implements PopupMenuProvider, ActionListener {

    /** for serialization. */
    private static final long serialVersionUID = -5615085802034434150L;

    /**
     * Available actions.
     *
     * @author  fracpete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static enum Actions {
      /** the remove action. */
      TOGGLE("Toggle"),
      /** the remove action. */
      REMOVE("Remove"),
      /** the remove all action. */
      REMOVE_ALL("Remove all");

      /** the display string. */
      protected String m_Display;

      /**
       * Initializes the action.
       *
       * @param display		the display string to use
       */
      private Actions(String display) {
	m_Display = display;
      }

      /**
       * Returns the display string.
       *
       * @return		the display string
       */
      public String getDisplay() {
	return m_Display;
      }
    }

    /** the list this item belongs to. */
    protected JComponentList m_List;

    /** contains the disabled actions. */
    protected HashSet<Actions> m_Disabled;

    /** contains overrides for the display string per action. */
    protected Hashtable<Actions,String> m_Display;

    /**
     * Initializes the item.
     *
     * @param list	the list this item belongs to
     */
    public CheckBoxListItem(JComponentList list) {
      super();

      m_List     = list;
      m_Disabled = new HashSet<Actions>();
      m_Display  = new Hashtable<Actions,String>();
    }

    /**
     * Returns the list this item belongs to.
     *
     * @return		the list
     */
    public JComponentList getList() {
      return m_List;
    }

    /**
     * Checks whether the given action is enabled.
     *
     * @param action	the action to check
     * @return		true if the action is enabled
     */
    public boolean isEnabled(Actions action) {
      return (!m_Disabled.contains(action));
    }

    /**
     * Sets the enabled/disabled state of an action, i.e., whether it shows
     * up in the menu or not. By default, all actions are enabled.
     *
     * @param action	the action to set the state for
     * @param enabled	if true the action will be displayed in the menu
     */
    public void setEnabled(Actions action, boolean enabled) {
      if (enabled)
	m_Disabled.remove(action);
      else
	m_Disabled.add(action);
    }

    /**
     * Checks whether the given action's display string is overridden.
     *
     * @param action	the action to check
     * @return		true if the action has an override
     */
    public boolean hasDisplayOverride(Actions action) {
      return m_Display.containsKey(action);
    }

    /**
     * Sets an override for the display string for the given action.
     *
     * @param action	the action to set an override for
     * @param display	the override
     */
    public void addDisplayOverride(Actions action, String display) {
      m_Display.put(action, display);
    }

    /**
     * Removes the display override for the given action.
     *
     * @param action	the action to remove the override for
     */
    public void removeDisplayOverride(Actions action) {
      m_Display.remove(action);
    }

    /**
     * Returns the display override for the given action.
     *
     * @param action	the action to get the override for
     * @return		the override string
     */
    public String getDisplayOverride(Actions action) {
      return m_Display.get(action);
    }

    /**
     * Returns whether this particular list item has a popup menu at the
     * moment.
     *
     * @return		true if the provider has a popup menu
     */
    public boolean hasPopupMenu() {
      return true;
    }

    /**
     * Returns the popup menu.
     *
     * @return		the popup menu
     * @see		#hasPopupMenu()
     */
    public JPopupMenu getPopupMenu() {
      JPopupMenu	result;
      JMenuItem		item;

      result = new JPopupMenu();

      for (Actions action: Actions.values()) {
	if (!isEnabled(action))
	  continue;

	item = new JMenuItem();
	if (hasDisplayOverride(action))
	  item.setText(getDisplayOverride(action));
	else
	  item.setText(action.getDisplay());
	item.setActionCommand(action.toString());
	item.addActionListener(this);
	result.add(item);

	if (action == Actions.TOGGLE)
	  result.addSeparator();
      }

      return result;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e		the event
     */
    public abstract void actionPerformed(ActionEvent e);
  }

  /**
   * A specialized model.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @see DefaultListModel
   */
  public static class JComponentListModel
    extends DefaultListModel {

    /** for serialization. */
    private static final long serialVersionUID = 962859537727414215L;

    /**
     * Inserts the specified JComponent at the specified position in this list.
     *
     * @param index	index at which the specified element is to be inserted
     * @param element	JComponet to be inserted
     */
    @Override
    public void add(int index, Object element) {
      if (!(element instanceof JComponent))
	throw new IllegalArgumentException("Only descendants of JComponent are allowed!");

      super.add(index, element);
    }

    /**
     * Adds the specified JComponent to the end of this list.
     *
     * @param obj	the JComponent to be added
     */
    @Override
    public void addElement(Object obj) {
      if (!(obj instanceof JComponent))
	throw new IllegalArgumentException("Only descendants of JComponent are allowed!");

      super.addElement(obj);
    }

    /**
     * Replaces the JComponent at the specified position in this list with the
     * specified JComponent.
     *
     * @param index	index of JComponent to replace
     * @param element	JComponent to be stored at the specified position
     * @return		the JComponent previously at the specified position
     */
    @Override
    public Object set(int index, Object element) {
      if (!(element instanceof JComponent))
	throw new IllegalArgumentException("Only descendants of JComponent are allowed!");

      return super.set(index, element);
    }

    /**
     * Sets the JComponent at the specified index of this list to be the
     * specified object.
     *
     * @param obj	what the JComponent is to be set to
     * @param index	the specified index
     */
    @Override
    public void setElementAt(Object obj, int index) {
      if (!(obj instanceof JComponent))
	throw new IllegalArgumentException("Only descendants of JComponent are allowed!");

      super.setElementAt(obj, index);
    }
  }


  /**
   * A specialized cell renderer.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @see DefaultListCellRenderer
   */
  public static class ListCellRenderer
    extends DefaultListCellRenderer {

    /** for serialization. */
    private static final long serialVersionUID = -5578231619307079271L;

    /**
     * Initializes the renderer.
     */
    public ListCellRenderer() {
      super();

      setOpaque(true);
    }

    /**
     * Return a component that has been configured to display the specified
     * value.
     *
     * @param list		The JList we're painting.
     * @param value		The value returned by list.getModel().getElementAt(index).
     * @param index		The cells index.
     * @param isSelected	True if the specified cell was selected.
     * @param cellHasFocus	True if the specified cell has the focus.
     * @return			A component whose paint() method will render
     * 				the specified value.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JComponent 	comp;

      comp = (JComponent) value;
      comp.setOpaque(true);

      if (isSelected)
	comp.setBackground(list.getSelectionBackground());
      else
	comp.setBackground(list.getBackground());

      return comp;
    }
  }

  /**
   * Initializes the list with an empty model.
   */
  public JComponentList() {
    this(new JComponentListModel());
  }

  /**
   * Initializes the list with the given model.
   *
   * @param model	the model containing the list items
   */
  public JComponentList(JComponentListModel model) {
    super(model);

    m_Self = this;

    setCellRenderer(new ListCellRenderer());

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  int index = m_Self.locationToIndex(e.getPoint());
	  if (index > -1) {
	    Object element = m_Self.getModel().getElementAt(index);
	    if (element instanceof PopupMenuProvider) {
	      PopupMenuProvider provider = (PopupMenuProvider) element;
	      if (provider.hasPopupMenu()) {
		e.consume();
		JPopupMenu menu = provider.getPopupMenu();
		menu.show(m_Self, e.getX(), e.getY());
	      }
	    }
	  }
	}

	super.mouseClicked(e);
      }
    });
  }

  /**
   * Sets the model. Only onces of type JComponentListModel are allowed.
   *
   * @param model	the model to set
   */
  @Override
  public void setModel(ListModel model) {
    if (!(model instanceof JComponentListModel))
      throw new IllegalArgumentException(
	  "Model must be of type " + JComponentListModel.class.getName() + "!");

    super.setModel(model);
  }

  /**
   * Only for testing. Displays a frame with a sample JComponentList.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.setSize(400, 300);
    JComponentListModel model = new JComponentListModel();
    JComponentList list = new JComponentList(model);
    frame.add(new BaseScrollPane(list), BorderLayout.CENTER);
    frame.setVisible(true);

    for (int i = 0; i < 10; i++)
      model.addElement(new JLabel("" + i));
    model.addElement(new JLabel(GUIHelper.getIcon("exit.png"), SwingConstants.LEFT));
    model.addElement(new JLabel(GUIHelper.getIcon("hand.gif"), SwingConstants.LEFT));
  }
}
