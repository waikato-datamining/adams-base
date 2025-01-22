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
 *    GenericArrayEditor.java
 *    Copyright (C) 1999-2024 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.CustomDisplayStringProvider;
import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractCommandLineHandler;
import adams.core.option.OptionUtils;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.event.RemoveItemsEvent;
import adams.gui.goe.Favorites.FavoriteSelectionEvent;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import gnu.trove.set.hash.TIntHashSet;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * A PropertyEditor for arrays of objects that themselves have
 * property editors.
 *
 * Based on weka.gui.GenericArrayEditor
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 */
public class GenericArrayEditor
  extends BasePanel
  implements PropertyEditor {

  /** if there are no elements in the list. */
  public static final String NONE = "[none]";

  /**
   * This class handles the creation of list cell renderers from the
   * property editors.
   */
  protected static class EditorListCellRenderer
    implements ListCellRenderer {

    /** The class of the property editor for array objects. */
    protected Class m_EditorClass;

    /** The class of the array values. */
    protected Class m_ValueClass;

    /**
     * Creates the list cell renderer.
     *
     * @param editorClass The class of the property editor for array objects
     * @param valueClass The class of the array values
     */
    public EditorListCellRenderer(Class editorClass, Class valueClass) {
      m_EditorClass = editorClass;
      m_ValueClass = valueClass;
    }

    /**
     * Creates a cell rendering component.
     *
     * @param list 		the list that will be rendered in
     * @param value 		the cell value
     * @param index		which element of the list to render
     * @param isSelected	true if the cell is selected
     * @param cellHasFocus	true if the cell has the focus
     * @return 			the rendering component
     */
    public Component getListCellRendererComponent(final JList list,
						  final Object value,
						  final int index,
						  final boolean isSelected,
						  final boolean cellHasFocus) {
      try {
	final PropertyEditor e = (PropertyEditor) m_EditorClass.getDeclaredConstructor().newInstance();
	AbstractGenericObjectEditorHandler handlerGOE = AbstractGenericObjectEditorHandler.getHandler(e);
	handlerGOE.setClassType(e, m_ValueClass);
	handlerGOE.setCanChangeClassInDialog(e, true);
	handlerGOE.setValue(e, value);

	// do we have a simple string display?
	String display;
	if (e instanceof CustomStringRepresentationHandler) {
	  display = ((CustomStringRepresentationHandler) e).toCustomStringRepresentation(value);
	}
	else {
	  AbstractCommandLineHandler handler = AbstractCommandLineHandler.getHandler(value);
	  display = value.getClass().getName().replaceAll(".*\\.", "");
	  display += " " + handler.joinOptions(handler.getOptions(value));
	  display = display.trim();
	}

	if (display == null) {
	  return new JPanel() {
	    private static final long serialVersionUID = -3124434678426673334L;
	    @Override
	    public void paintComponent(Graphics g) {
	      Insets i = getInsets();
	      Rectangle box = new Rectangle(i.left, i.top,
		  getWidth() - i.right,
		  getHeight() - i.bottom - 1);
	      g.setColor(isSelected
		  ? list.getSelectionBackground()
		      : list.getBackground());
	      g.fillRect(0, 0, getWidth(), getHeight());
	      g.setColor(isSelected
		  ? list.getSelectionForeground()
		      : list.getForeground());
	      e.paintValue(g, box);
	    }

	    @Override
	    public Dimension getPreferredSize() {
	      Font f = getFont();
	      FontMetrics fm = getFontMetrics(f);
	      return new Dimension(0, fm.getHeight() + 2);
	    }
	  };
	}
	else {
          if (display.isEmpty())
            display = AbstractPropertyEditorSupport.EMPTY;
	  JLabel label = new JLabel(display);
	  label.setFont(list.getFont().deriveFont(Font.PLAIN));
	  label.setOpaque(true);
	  if (isSelected)
	    label.setBackground(list.getSelectionBackground());
	  else
	    label.setBackground(list.getBackground());
	  if (isSelected)
	    label.setForeground(list.getSelectionForeground());
	  else
	    label.setForeground(list.getForeground());
	  return label;
	}
      }
      catch (Exception ex) {
	return null;
      }
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = 3914616975334750480L;

  /** for logging. */
  protected static Logger LOGGER = LoggingHelper.getLogger(GenericArrayEditor.class);

  /** Handles property change notification. */
  protected PropertyChangeSupport m_Support;

  /** The label for when we can't edit that type. */
  protected JLabel m_Label;

  /** The list component displaying current values. */
  protected BaseListWithButtons m_ElementList;

  /** The class of objects allowed in the array. */
  protected Class m_ElementClass;

  /** The defaultlistmodel holding our data. */
  protected DefaultListModel<Object> m_ListModel;

  /** The defaultlistmodel holding the backup of our data. */
  protected DefaultListModel<Object> m_ListModelBackup;

  /** The property editor for the class we are editing. */
  protected PropertyEditor m_ElementEditor;

  /** whether the objects are wrapped with BaseObject. */
  protected boolean m_IsPrimitive;

  /** whether the objects implement comparable. */
  protected boolean m_CanSort;

  /** Click this to delete the selected array values. */
  protected BaseButton m_ButtonRemove;

  /** Click this to delete all the array values. */
  protected BaseButton m_ButtonRemoveAll;

  /** Click this to edit the selected array value. */
  protected BaseButton m_ButtonEdit;

  /** Click this to move the selected array value(s) one up. */
  protected BaseButton m_ButtonUp;

  /** Click this to move the selected array value(s) one down. */
  protected BaseButton m_ButtonDown;

  /** Click to add the current object configuration to the array. */
  protected BaseButton m_ButtonAdd;

  /** Click to add multiple objects to the array. */
  protected BaseButton m_ButtonAddMultiple;

  /** Click to set favorites. */
  protected BaseButton m_ButtonSetFavorites;

  /** Click to copy the currently selected object in array into the edit field. */
  protected BaseButton m_ButtonCopy;

  /** Click to OK the dialog. */
  protected BaseButton m_ButtonOK;

  /** Click to cancel the dialog. */
  protected BaseButton m_ButtonCancel;

  /** More actions. */
  protected BaseButtonWithDropDownMenu m_ButtonActions;

  /** the restore action. */
  protected AbstractBaseAction m_ActionRestore;

  /** the sort (asc) action. */
  protected AbstractBaseAction m_ActionSortAsc;

  /** the sort (desc) action. */
  protected AbstractBaseAction m_ActionSortDesc;

  /** the panel for the buttons. */
  protected JPanel m_PanelDialogButtons;

  /** The currently displayed property dialog, if any. */
  protected GenericObjectEditorDialog m_Dialog;

  /** to catch the event when the user is closing the dialog via the "X". */
  protected WindowAdapter m_WindowAdapter;

  /** whether the content got modified. */
  protected boolean m_Modified;

  /** whether the OK button is always enabled. */
  protected boolean m_OkAlwaysEnabled;
  
  /** the view in use. */
  protected Component m_View;

  /** the change listeners (get notified whenever items get added/removed/updated). */
  protected Set<ChangeListener> m_ArrayChangeListeners;

  /** the listener for model updates. */
  protected ListDataListener m_ModelListener;

  /**
   * Sets up the array editor.
   */
  public GenericArrayEditor() {
    super();
    AdamsEditorsRegistration.registerEditors();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Support         = new PropertyChangeSupport(this);
    m_ElementClass    = String.class;
    m_Modified        = false;
    m_OkAlwaysEnabled = false;
    m_ArrayChangeListeners = new HashSet<>();
    m_WindowAdapter   = new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	restore();
	super.windowClosing(e);
      }
    };
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_Label = new JLabel("Can't edit", SwingConstants.CENTER);

    setMinimumSize(new Dimension(500, 300));
    setPreferredSize(new Dimension(500, 300));
    setLayout(new BorderLayout());
    add(m_Label, BorderLayout.CENTER);

    m_ButtonAdd = new BaseButton(ImageManager.getIcon("add.gif"));
    m_ButtonAdd.setToolTipText("Add the current item to the array");
    m_ButtonAdd.addActionListener((ActionEvent e) -> addObject(m_ElementEditor.getValue()));

    m_ButtonAddMultiple = new BaseButton(ImageManager.getIcon("add_multiple.gif"));
    m_ButtonAddMultiple.setToolTipText("Add multiple items to the array");
    m_ButtonAddMultiple.addActionListener((ActionEvent e) -> addMultipleObjects());

    m_ButtonSetFavorites = new BaseButton(ImageManager.getIcon("favorite.gif"));
    m_ButtonSetFavorites.setToolTipText("Applying a favorite replaces the current array elements");
    m_ButtonSetFavorites.addActionListener((ActionEvent e) -> setFavorites());

    m_ButtonCopy = new BaseButton(ImageManager.getIcon("copy_to_edit_field.gif"));
    m_ButtonCopy.setToolTipText("Copies the currently selected array item to the edit field");
    m_ButtonCopy.addActionListener((ActionEvent e) -> m_ElementEditor.setValue(ObjectCopyHelper.copyObject(m_ElementList.getSelectedValue())));

    m_ButtonRemove = new BaseButton(ImageManager.getIcon("delete.gif"));
    m_ButtonRemove.setToolTipText("Remove the selected array item(s)");
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeSelectedObjects());

    m_ButtonRemoveAll = new BaseButton(ImageManager.getIcon("delete_all.gif"));
    m_ButtonRemoveAll.setToolTipText("Remove all the array items");
    m_ButtonRemoveAll.addActionListener((ActionEvent e) -> removeAllObjects());

    m_ButtonEdit = new BaseButton(ImageManager.getIcon("properties.gif"));
    m_ButtonEdit.setToolTipText("Edit the selected array item");
    m_ButtonEdit.addActionListener((ActionEvent e) -> editSelectedObject());

    m_ButtonUp = new BaseButton(ImageManager.getIcon("arrow_up.gif"));
    m_ButtonUp.setToolTipText("Move the selected item(s) one up");
    m_ButtonUp.addActionListener((ActionEvent e) -> {
      m_ElementList.moveUp();
      m_Modified = true;
      updateButtons();
    });

    m_ButtonDown = new BaseButton(ImageManager.getIcon("arrow_down.gif"));
    m_ButtonDown.setToolTipText("Move the selected item(s) one down");
    m_ButtonDown.addActionListener((ActionEvent e) -> {
      m_ElementList.moveDown();
      m_Modified = true;
      updateButtons();
    });

    m_ButtonOK = new BaseButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.setToolTipText("Applies the changes and closes the dialog");
    m_ButtonOK.setEnabled(false);
    m_ButtonOK.addActionListener((ActionEvent e) -> {
      apply();
      firePropertyChange();
      close();
    });

    m_ButtonCancel = new BaseButton("Cancel");
    m_ButtonCancel.setMnemonic('C');
    m_ButtonCancel.setToolTipText("Cancels the dialog");
    m_ButtonCancel.addActionListener((ActionEvent e) -> {
      restore();
      updateButtons();
      close();
    });

    m_ButtonActions = new BaseButtonWithDropDownMenu();
    m_ButtonActions.setToolTipText("More actions");
    m_ActionRestore = new AbstractBaseAction() {
      private static final long serialVersionUID = -8746901680473804479L;
      @Override
      protected void initialize() {
	super.initialize();
	setName("Restore");
	setIcon("undo.gif");
      }
      @Override
      protected void doActionPerformed(ActionEvent e) {
	restore();
      }
    };
    m_ButtonActions.addToMenu(m_ActionRestore);
    m_ActionSortAsc = new AbstractBaseAction() {
      private static final long serialVersionUID = 3974602886089489212L;
      @Override
      protected void initialize() {
	super.initialize();
	setName("Sort (asc)");
	setIcon("sort-ascending.png");
      }
      @Override
      protected void doActionPerformed(ActionEvent e) {
	sort(true);
      }
    };
    m_ButtonActions.addToMenu(m_ActionSortAsc);
    m_ActionSortDesc = new AbstractBaseAction() {
      private static final long serialVersionUID = 3988628222623631911L;
      @Override
      protected void initialize() {
	super.initialize();
	setName("Sort (desc)");
	setIcon("sort-descending.png");
      }
      @Override
      protected void doActionPerformed(ActionEvent e) {
	sort(false);
      }
    };
    m_ButtonActions.addToMenu(m_ActionSortDesc);

    m_ElementList = new BaseListWithButtons();
    m_ElementList.setDoubleClickButton(m_ButtonEdit);
    m_ElementList.setInfoVisible(true);
    m_ElementList.addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    m_ElementList.addRemoveItemsListener((RemoveItemsEvent e) -> removeSelectedObjects());
    m_ElementList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  JPopupMenu menu = createListPopupMenu(e);
	  if (menu != null)
	    menu.show(m_ElementList, e.getX(), e.getY());
	}
      }
    });
    m_ModelListener = new ListDataListener() {
      @Override
      public void intervalAdded(ListDataEvent e) {
        notifyArrayChangeListeners();
      }
      @Override
      public void intervalRemoved(ListDataEvent e) {
        notifyArrayChangeListeners();
      }
      @Override
      public void contentsChanged(ListDataEvent e) {
        notifyArrayChangeListeners();
      }
    };
    m_ElementList.getModel().addListDataListener(m_ModelListener);
  }

  /**
   * Closes and disposes the window the GOE belongs to, if possible.
   */
  protected void close() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(false);
    else
      getParentFrame().setVisible(false);

    if ((getTopLevelAncestor() != null) && (getTopLevelAncestor() instanceof Window)) {
      Window w = (Window) getTopLevelAncestor();
      w.dispose();
    }
  }

  /**
   * Sets the correct enabled/disabled state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonAddMultiple.setEnabled(m_ElementEditor instanceof MultiSelectionEditor);
    m_ButtonOK.setEnabled(m_Modified || m_OkAlwaysEnabled);
    m_ActionRestore.setEnabled(m_Modified);
    m_ActionSortAsc.setEnabled(m_CanSort && (m_ListModel.getSize() > 1));
    m_ActionSortDesc.setEnabled(m_CanSort && (m_ListModel.getSize() > 1));
    if (m_ElementList.getSelectedIndex() != -1) {
      m_ButtonCopy.setEnabled(m_ElementList.getSelectedIndices().length == 1);
      m_ButtonRemove.setEnabled(true);
      m_ButtonRemoveAll.setEnabled(true);
      m_ButtonEdit.setEnabled(m_ElementList.getSelectedIndices().length == 1);
      m_ButtonUp.setEnabled(m_ElementList.canMoveUp());
      m_ButtonDown.setEnabled(m_ElementList.canMoveDown());
    }
    // disable delete/edit button
    else {
      m_ButtonCopy.setEnabled(false);
      m_ButtonRemove.setEnabled(false);
      m_ButtonRemoveAll.setEnabled((m_ListModel != null) && (m_ListModel.getSize() > 0));
      m_ButtonEdit.setEnabled(false);
      m_ButtonUp.setEnabled(false);
      m_ButtonDown.setEnabled(false);
    }
  }

  /**
   * Accepts the array.
   */
  protected void apply() {
    Object	obj;
    int		i;

    m_Modified = false;
    obj = Array.newInstance(m_ElementClass, m_ListModel.size());
    for (i = 0; i < m_ListModel.size(); i++)
      Array.set(obj, i, ObjectCopyHelper.copyObject(m_ListModel.get(i)));

    setValue(obj);
    updateButtons();
  }

  /**
   * Restores the values to the original ones.
   */
  protected void restore() {
    int 		i;
    DefaultListModel	listModel;

    m_ListModel.removeListDataListener(m_ModelListener);
    listModel = new DefaultListModel();
    for (i = 0; i < m_ListModelBackup.size(); i++)
      listModel.addElement(m_ListModelBackup.get(i));
    m_ListModel = listModel;
    m_ListModel.addListDataListener(m_ModelListener);
    m_ElementList.setModel(m_ListModel);
    apply();
  }

  /**
   * Sorts the values.
   *
   * @param ascending	if true, sorting is done ascending, otherwise descending
   */
  protected void sort(boolean ascending) {
    int 		i;
    DefaultListModel	listModel;
    Object[]		items;

    m_ListModel.removeListDataListener(m_ModelListener);
    items = new Object[m_ListModel.getSize()];
    for (i = 0; i < m_ListModel.size(); i++)
      items[i] = m_ListModel.get(i);
    Arrays.sort(items);
    listModel = new DefaultListModel();
    if (ascending) {
      for (i = 0; i < items.length; i++)
	listModel.addElement(items[i]);
    }
    else {
      for (i = items.length - 1; i >= 0; i--)
	listModel.addElement(items[i]);
    }
    m_ListModel = listModel;
    m_ListModel.addListDataListener(m_ModelListener);
    m_ElementList.setModel(m_ListModel);
    m_Modified = true;
    updateButtons();
  }

  /**
   * Creates the right-click popup for the list.
   *
   * @param e		the triggering event
   */
  protected JPopupMenu createListPopupMenu(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		menuitem;

    result = new BasePopupMenu();

    menuitem = new JMenuItem("Copy", ImageManager.getIcon("copy"));
    menuitem.setEnabled(m_ElementList.getSelectedIndices().length > 0);
    menuitem.addActionListener((ActionEvent ae) -> {
      StringBuilder content = new StringBuilder();
      for (int index: m_ElementList.getSelectedIndices()) {
	if (content.length() > 0)
	  content.append("\n");
	content.append(OptionUtils.getCommandLine(m_ListModel.get(index)));
      }
      ClipboardHelper.copyToClipboard(content.toString());
    });
    result.add(menuitem);

    return result;
  }

  /**
   * Updates the type of object being edited, so attempts to find an
   * appropriate propertyeditor.
   *
   * @param o a value of type 'Object'
   */
  protected void updateEditorType(Object o) {
    Class 				elementClass;
    PropertyEditor 			editor;
    boolean				primitive;
    Component 				view;
    ListCellRenderer 			lcr;
    AbstractGenericObjectEditorHandler 	handler;
    Method 				method;
    Object[] 				obj;
    JPanel 				panel;
    int					i;
    JPanel				panelAdd;
    JPanel 				panelMove;
    JPanel 				panelCopyEdit;
    JPanel 				panelRemove;
    JPanel 				panelLeft;
    JPanel 				panelRight;

    // Determine if the current object is an array
    m_ElementEditor = null;
    m_View          = null;
    m_ListModel     = null;
    m_IsPrimitive   = false;
    m_CanSort       = false;
    m_ButtonAdd.setIcon(ImageManager.getIcon("add.gif"));
    removeAll();

    if ((o != null) && (o.getClass().isArray())) {
      elementClass = o.getClass().getComponentType();
      primitive    = Utils.isPrimitive(elementClass);
      if (primitive)
	elementClass = Utils.getWrapperClass(elementClass);
      m_CanSort = (elementClass != null) && ClassLocator.hasInterface(Comparable.class, elementClass);
      editor = PropertyEditorManager.findEditor(elementClass);
      view         = null;
      lcr          = new DefaultListCellRenderer();
      if (editor != null) {
	handler = AbstractGenericObjectEditorHandler.getHandler(editor);
	handler.setClassType(editor, elementClass);
	handler.setCanChangeClassInDialog(editor, true);

	//setting the value in the editor so that
	//we don't get a NullPointerException
	//when we do getAsText() in the constructor of
	//PropertyValueSelector()
	if (Array.getLength(o) > 0) {
	  if (primitive)
	    editor.setValue(Utils.wrapPrimitive(Array.get(o, 0)));
	  else
	    editor.setValue(Array.get(o,0));
	}
	else {
	  if (editor instanceof GenericObjectEditor) {
	    ((GenericObjectEditor) editor).setDefaultValue();
	  }
	  else if ((elementClass != null) && ClassLocator.isSubclass(Enum.class, elementClass)) {
	    try {
	      method = elementClass.getMethod("values");
	      obj    = (Object[]) method.invoke(null, new Object[0]);
	      editor.setValue(obj[0]);
	    }
	    catch (Exception e) {
	      LOGGER.log(Level.SEVERE, "Failed to get values and update editor!", e);
	    }
	  }
	  else {
	    if (elementClass != null) {
	      try {
		editor.setValue(ObjectCopyHelper.newInstance(elementClass));
	      }
	      catch (Exception ex) {
		m_ElementEditor = null;
		m_IsPrimitive = false;
		m_View = null;
		LOGGER.log(Level.SEVERE, "Failed to instantiate: " + Utils.classToString(elementClass), ex);
		add(m_Label, BorderLayout.CENTER);
		firePropertyChange();
		validate();
		return;
	      }
	    }
	  }
	}

	if (editor.isPaintable() && editor.supportsCustomEditor()) {
	  view = new PropertyPanel(editor);
	  lcr  = new EditorListCellRenderer(editor.getClass(), elementClass);
	}
	else if (editor.getTags() != null) {
	  view = new PropertyValueSelector(editor);
	}
	else if (editor.getAsText() != null) {
	  view = new PropertyText(editor);
	}
      }

      if (view == null) {
	if (elementClass != null)
	  LOGGER.warning("No property editor for class: " + elementClass.getName());
      }
      else {
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
	panel.add(view, BorderLayout.CENTER);
	add(panel, BorderLayout.NORTH);

	m_ElementEditor = editor;
	m_View          = view;
	m_IsPrimitive   = primitive;

	// Create the ListModel and populate it
	m_ListModel       = new DefaultListModel<>();
	m_ListModel.addListDataListener(m_ModelListener);
	m_ListModelBackup = new DefaultListModel<>();
	m_ElementClass = elementClass;
	for (i = 0; i < Array.getLength(o); i++) {
	  if (primitive) {
	    m_ListModel.addElement(Utils.wrapPrimitive(ObjectCopyHelper.copyObject(Array.get(o, i))));
	    m_ListModelBackup.addElement(Utils.wrapPrimitive(ObjectCopyHelper.copyObject(Array.get(o, i))));
	  }
	  else {
	    m_ListModel.addElement(ObjectCopyHelper.copyObject(Array.get(o, i)));
	    m_ListModelBackup.addElement(ObjectCopyHelper.copyObject(Array.get(o, i)));
	  }
	}
	m_ElementList.getComponent().setCellRenderer(lcr);
	m_ElementList.setModel(m_ListModel);
	if (m_ListModel.getSize() > 0)
	  m_ElementList.setSelectedIndex(0);

	add(m_ElementList, BorderLayout.CENTER);

	if (m_ElementEditor instanceof MultiSelectionEditor) {
	  panelAdd = new JPanel(new GridLayout(1, 2, 0, 0));
	  panelAdd.add(m_ButtonAdd);
	  panelAdd.add(m_ButtonAddMultiple);
	  panelAdd.add(m_ButtonSetFavorites);
	}
	else {
	  panelAdd = new JPanel(new GridLayout(1, 1, 0, 0));
	  panelAdd.add(m_ButtonAdd);
	  panelAdd.add(m_ButtonSetFavorites);
	}

	panelMove = new JPanel(new GridLayout(1, 2, 0, 0));
	panelMove.add(m_ButtonUp);
	panelMove.add(m_ButtonDown);

	panelCopyEdit = new JPanel(new GridLayout(1, 2, 0, 0));
	panelCopyEdit.add(m_ButtonCopy);
	panelCopyEdit.add(m_ButtonEdit);

	panelRemove = new JPanel(new GridLayout(1, 2, 0, 0));
	panelRemove.add(m_ButtonRemove);
	panelRemove.add(m_ButtonRemoveAll);

	m_ElementList.clearButtonsPanel();
	m_ElementList.addToButtonsPanel(panelAdd);
	m_ElementList.addToButtonsPanel(panelCopyEdit);
	m_ElementList.addToButtonsPanel(new JLabel(""));
	m_ElementList.addToButtonsPanel(panelMove);
	m_ElementList.addToButtonsPanel(new JLabel(""));
	m_ElementList.addToButtonsPanel(panelRemove);

	m_PanelDialogButtons = new JPanel(new GridLayout(1, 2));
	panelLeft            = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panelRight           = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	m_PanelDialogButtons.add(panelLeft);
	m_PanelDialogButtons.add(panelRight);
	panelLeft.add(m_ButtonActions);
	panelRight.add(m_ButtonOK);
	panelRight.add(m_ButtonCancel);
	add(m_PanelDialogButtons, BorderLayout.SOUTH);

	m_ElementEditor.addPropertyChangeListener(new PropertyChangeListener() {
	  public void propertyChange(PropertyChangeEvent e) {
	    m_ButtonAdd.setIcon(ImageManager.getIcon("add_modified.gif"));
	    repaint();
	  }
	});

	updateButtons();
      }
    }

    if (m_ElementEditor == null)
      add(m_Label, BorderLayout.CENTER);
    firePropertyChange();
    validate();
  }

  /**
   * Sets the current object array.
   *
   * @param o 		an object that must be an array.
   */
  public void setValue(Object o) {
    updateEditorType(o);
  }

  /**
   * Gets the current object array.
   *
   * @return 		the current object array
   */
  public Object getValue() {
    int 	length;
    Object 	result;
    int 	i;
    
    if (m_ListModel == null)
      return null;

    // Convert the listmodel to an array and return it.
    length = m_ListModel.getSize();
    if (m_IsPrimitive)
      result = Array.newInstance(Utils.getPrimitiveClass(m_ElementClass), length);
    else
      result = Array.newInstance(m_ElementClass, length);
    for (i = 0; i < length; i++) {
      if (m_IsPrimitive)
	Array.set(result, i, Utils.unwrapPrimitive(ObjectCopyHelper.copyObject(m_ListModel.elementAt(i))));
      else
	Array.set(result, i, ObjectCopyHelper.copyObject(m_ListModel.elementAt(i)));
    }

    return result;
  }

  /**
   * Returns the element editor in use.
   * 
   * @return		the editor, null if not available
   */
  public PropertyEditor getElementEditor() {
    return m_ElementEditor;
  }
  
  /**
   * Supposedly returns an initialization string to create a classifier
   * identical to the current one, including it's state, but this doesn't
   * appear possible given that the initialization string isn't supposed to
   * contain multiple statements.
   *
   * @return 		the java source code initialisation string
   */
  public String getJavaInitializationString() {
    return "null";
  }

  /**
   * Returns true to indicate that we can paint a representation of the
   * string array.
   *
   * @return 		always true
   */
  public boolean isPaintable() {
    return true;
  }

  /**
   * Paints a representation of the current classifier.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  public void paintValue(Graphics gfx, Rectangle box) {
    int[] 			offset;
    String 			rep;
    Object			obj;
    int				pos;
    AbstractCommandLineHandler	handler;
    String			optionStr;
    PropertyEditor 		editor;

    editor = PropertyEditorManager.findEditor(m_ElementClass);
    if ((m_ListModel == null) || m_ListModel.isEmpty()) {
      rep = NONE;
    }
    else if (m_ListModel.size() == 1) {
      obj = m_ListModel.get(0);
      if (obj instanceof CustomDisplayStringProvider) {
	rep = ((CustomDisplayStringProvider) obj).toDisplay();
      }
      else if (editor instanceof CustomStringRepresentationHandler) {
	rep = ((CustomStringRepresentationHandler) editor).toCustomStringRepresentation(obj);
      }
      else {
	rep = obj.getClass().getName();
	pos = rep.lastIndexOf('.');
	if (pos != -1)
	  rep = rep.substring(pos + 1);
	pos = rep.lastIndexOf('$');
	if (pos != -1)
	  rep = rep.substring(pos + 1);
	handler   = AbstractCommandLineHandler.getHandler(obj);
	optionStr = handler.joinOptions(handler.getOptions(obj));
	rep += " " + optionStr;
	rep = rep.trim();
      }
      if (rep.isEmpty())
	rep = AbstractPropertyEditorSupport.EMPTY;
    }
    else {
      rep = m_ListModel.getSize() + " " + m_ElementClass.getName().replaceAll(".*\\.", "");
    }
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(rep, offset[0], offset[1]);
  }

  /**
   * Returns null as we don't support getting/setting values as text.
   *
   * @return 		always null
   */
  public String getAsText() {
    return null;
  }

  /**
   * Returns null as we don't support getting/setting values as text.
   *
   * @param text 	the text value
   * @throws IllegalArgumentException 	as we don't support getting/setting values as text.
   */
  public void setAsText(String text) {
    throw new IllegalArgumentException(text);
  }

  /**
   * Returns null as we don't support getting values as tags.
   *
   * @return 		always null
   */
  public String[] getTags() {
    return null;
  }

  /**
   * Returns true because we do support a custom editor.
   *
   * @return 		always true
   */
  public boolean supportsCustomEditor() {
    return true;
  }

  /**
   * Returns the array editing component.
   *
   * @return 		itself
   */
  public Component getCustomEditor() {
    if (getParentDialog() != null) {
      getParentDialog().removeWindowListener(m_WindowAdapter);
      getParentDialog().addWindowListener(m_WindowAdapter);
    }
    else if (getParentFrame() != null) {
      getParentFrame().removeWindowListener(m_WindowAdapter);
      getParentFrame().addWindowListener(m_WindowAdapter);
    }

    return this;
  }

  /**
   * Adds a PropertyChangeListener who will be notified of value changes.
   *
   * @param l 		a value of type 'PropertyChangeListener'
   */
  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    if (m_Support != null)
      m_Support.addPropertyChangeListener(l);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param l 		a value of type 'PropertyChangeListener'
   */
  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    if (m_Support != null)
      m_Support.removePropertyChangeListener(l);
  }

  /**
   * This is used to hook an action listener to the ok button.
   *
   * @param a 		The action listener.
   */
  public void addOkListener(ActionListener a) {
    m_ButtonOK.addActionListener(a);
  }

  /**
   * This is used to hook an action listener to the cancel button.
   *
   * @param a 		The action listener.
   */
  public void addCancelListener(ActionListener a) {
    m_ButtonCancel.addActionListener(a);
  }

  /**
   * This is used to remove an action listener from the ok button.
   *
   * @param a 		The action listener
   */
  public void removeOkListener(ActionListener a) {
    m_ButtonOK.removeActionListener(a);
  }

  /**
   * This is used to remove an action listener from the cancel button.
   *
   * @param a 	The action listener
   */
  public void removeCancelListener(ActionListener a) {
    m_ButtonCancel.removeActionListener(a);
  }
  
  /**
   * Adds the object to the list.
   * 
   * @param object	the object to add
   * @return		true if successfully added
   */
  public boolean addObject(Object object) {
    boolean	result;
    int 	selected;
    
    selected = m_ElementList.getSelectedIndex();
    try {
      // Make a full copy of the object using serialization
      object = ObjectCopyHelper.copyObject(object);
      if (selected != -1)
	m_ListModel.insertElementAt(object, selected);
      else
	m_ListModel.addElement(object);
      m_ButtonAdd.setIcon(ImageManager.getIcon("add.gif"));
      m_Modified = true;
      result     = true;
      updateButtons();
    }
    catch (Exception ex) {
      result = false;
      GUIHelper.showErrorMessage(
	  GenericArrayEditor.this, "Could not create an object copy/add object");
    }
    
    return result;
  }

  /**
   * If the element editor implements {@link MultiSelectionEditor} a dialog
   * is popped up to enter multiple objects.
   * 
   * @return		true if a dialog was popped up and all objects added successfully
   */
  public boolean addMultipleObjects() {
    if (m_ElementEditor instanceof MultiSelectionEditor) {
      Object[] objects = ((MultiSelectionEditor) m_ElementEditor).getSelectedObjects(this);
      return addMultipleObjects(objects);
    }
    else {
      return false;
    }
  }

  /**
   * Adds multiple objects.
   * 
   * @param objects	the objects to add
   * @return		true if all objects were added successfully
   */
  public boolean addMultipleObjects(Object[] objects) {
    boolean			result;
    int 			selected;
    int				i;
    DefaultListModel<Object>	updated;

    m_ListModel.removeListDataListener(m_ModelListener);
    result   = true;
    objects  = ObjectCopyHelper.copyObjects(objects);
    selected = m_ElementList.getSelectedIndex();
    updated  = new DefaultListModel<>();
    for (i = 0; i < m_ListModel.getSize(); i++)
      updated.addElement(m_ListModel.getElementAt(i));
    for (i = 0; i < objects.length; i++) {
      try {
	if (selected != -1)
	  updated.insertElementAt(objects[i], selected + i);
	else
	  updated.addElement(objects[i]);
	m_Modified = true;
      }
      catch (Exception ex) {
	result = false;
	GUIHelper.showErrorMessage(
	    GenericArrayEditor.this, 
	    "Could not create an object copy/add object #" + (i+1) + ":\n" + LoggingHelper.throwableToString(ex));
      }
    }
    m_ListModel = updated;
    m_ListModel.addListDataListener(m_ModelListener);
    m_ElementList.setModel(m_ListModel);
    updateButtons();

    return result;
  }

  /**
   * Opens the favorites menu and replaces the current array elements when applying a favorite.
   */
  public void setFavorites() {
    JPopupMenu 	menu;
    Object	array;

    if (m_IsPrimitive)
      array = Array.newInstance(Utils.getPrimitiveClass(m_ElementClass), 0);
    else
      array = Array.newInstance(m_ElementClass, 0);

    menu = new JPopupMenu();
    Favorites.getSingleton().addFavoritesMenuItems(
      menu,
      array.getClass(),
      getValue(),
      (FavoriteSelectionEvent fe) -> setValue(fe.getFavorite().getObject()));
    menu.show(m_ButtonSetFavorites, 0, m_ButtonSetFavorites.getHeight());
  }

  /**
   * Removes all elements.
   */
  public void removeAllObjects() {
    m_ListModel.clear();
    m_Modified = true;
    updateButtons();
  }

  /**
   * Removes all currently selected objects.
   */
  protected void removeSelectedObjects() {
    int[] 			selected;
    int 			i;
    DefaultListModel<Object>	listModel;
    TIntHashSet			selIndices;

    selected = m_ElementList.getSelectedIndices();
    if (selected != null) {
      m_ListModel.removeListDataListener(m_ModelListener);
      selIndices = new TIntHashSet(selected);
      listModel = new DefaultListModel<>();
      for (i = 0; i < m_ListModel.getSize(); i++) {
	if (selIndices.contains(i))
	  continue;
	listModel.addElement(m_ListModel.getElementAt(i));
      }
      m_Modified  = true;
      m_ListModel.addListDataListener(m_ModelListener);
      m_ListModel = listModel;
      m_ElementList.setModel(m_ListModel);
      updateButtons();
    }
  }

  /**
   * Edits the selected object.
   * 
   * @return		true is successfully edited
   */
  protected boolean editSelectedObject() {
    boolean				result;
    PropertyEditor 			editor;
    AbstractGenericObjectEditorHandler 	handlerElement;
    AbstractGenericObjectEditorHandler 	handlerGOE;
    
    result         = false;
    editor         = PropertyEditorManager.findEditor(m_ElementClass);
    handlerElement = AbstractGenericObjectEditorHandler.getHandler(m_ElementEditor);
    handlerGOE     = AbstractGenericObjectEditorHandler.getHandler(editor);
    handlerGOE.setClassType(editor, m_ElementClass);
    handlerGOE.setCanChangeClassInDialog(editor, handlerElement.getCanChangeClassInDialog(m_ElementEditor));
    handlerGOE.setValue(editor, m_ElementList.getSelectedValue());
    if (editor.getValue() != null) {
      if (m_Dialog == null) {
	m_Dialog = GenericObjectEditorDialog.createDialog(this, editor);
	m_Dialog.setLocationRelativeTo(m_Dialog.getParent());
	m_Dialog.setVisible(true);
      }
      else {
	m_Dialog.setEditor(editor);
	m_Dialog.setVisible(true);
      }
      if (m_Dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION) {
	m_ListModel.set(m_ElementList.getSelectedIndex(), editor.getValue());
	m_Modified = true;
	result     = true;
	updateButtons();
      }
    }
    
    return result;
  }
  
  /**
   * Fires a property change event.
   * 
   * @see		#m_Support
   */
  public void firePropertyChange() {
    m_Support.firePropertyChange("", null, null);
  }
  
  /**
   * Sets whether the OK button is always enabled, not just when array
   * was modified.
   * 
   * @param value	true if to always enable
   */
  public void setOkAlwaysEnabled(boolean value) {
    m_OkAlwaysEnabled = value;
    updateButtons();
  }
  
  /**
   * Returns whether the OK button is always enabled, not just when array
   * was modified.
   * 
   * @return		true if always enabled
   */
  public boolean isOkAlwaysEnabled() {
    return m_OkAlwaysEnabled;
  }

  /**
   * Sets the visibility state of the OK/Cancel/action buttons.
   *
   * @param value	whether to show them or hide them
   */
  public void setButtonsVisible(boolean value) {
    m_PanelDialogButtons.setVisible(value);
  }

  /**
   * Returns whether the OK/Cancel/action buttons are visible.
   *
   * @return		true if visible
   */
  public boolean getButtonsVisible() {
    return m_PanelDialogButtons.isVisible();
  }

  /**
   * Adds the change listener. Gets notified whenever the array elements change.
   *
   * @param l		the listener to add
   */
  public void addArrayChangeListener(ChangeListener l) {
    m_ArrayChangeListeners.add(l);
  }

  /**
   * Removes the change listener.
   *
   * @param l		the listener to remove
   */
  public void removeArrayChangeListener(ChangeListener l) {
    m_ArrayChangeListeners.remove(l);
  }

  /**
   * Notifies all change listeners that the array elements have changed.
   */
  protected void notifyArrayChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ArrayChangeListeners)
      l.stateChanged(e);
  }
}
