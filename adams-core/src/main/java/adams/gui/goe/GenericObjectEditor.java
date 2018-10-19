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
 *    GenericObjectEditor.java
 *    Copyright (C) 2002-2018 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.ClassLister;
import adams.core.CustomDisplayStringProvider;
import adams.core.ObjectCopyHelper;
import adams.core.io.PlaceholderFile;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractCommandLineHandler;
import adams.data.io.input.AbstractObjectReader;
import adams.data.io.output.AbstractObjectWriter;
import adams.gui.chooser.ObjectFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.goe.classtree.ClassTree;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * A PropertyEditor for objects.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Xin Xu (xx5@cs.waikato.ac.nz)
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditor
  implements PropertyEditor, CustomPanelSupplier {

  /** the action command for OK. */
  public final static String ACTION_CMD_OK = "ok";

  /** the action command for Cancel. */
  public final static String ACTION_CMD_CANCEL = "cancel";

  /** the action command for Open. */
  public final static String ACTION_CMD_OPEN = "open";

  /** the action command for Save. */
  public final static String ACTION_CMD_SAVE = "save";

  /** the action command for Revert. */
  public final static String ACTION_CMD_REVERT = "revert";

  /** for logging. */
  protected static Logger LOGGER = LoggingHelper.getLogger(GenericObjectEditor.class);

  /** The object being configured. */
  protected Object m_Object;

  /** Holds a copy of the current object that can be reverted to
      if the user decides to cancel. */
  protected Object m_Backup;

  /** the default value. */
  protected Object m_DefaultValue;

  /** whether a default value has been determined. */
  protected Boolean m_DefaultValueDetermined;

  /** Handles property change notification. */
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);

  /** The Class of objects being edited. */
  protected Class m_ClassType;

  /** The model containing the list of names to select from. */
  protected List<String> m_ObjectNames;

  /** The GUI component for editing values, created when needed. */
  protected GOEPanel m_EditorComponent;

  /** True if the GUI component is needed. */
  protected boolean m_Enabled = true;

  /** the custom panel provided by this editor. */
  protected JPanel m_CustomPanel;

  /** the button for choosing a different class in the custom panel. */
  protected BaseButton m_CustomPanelChooseButton;

  /** The property panel created for the objects. */
  protected PropertyPanel m_ObjectPropertyPanel;

  /** whether the class can be changed. */
  protected boolean m_canChangeClassInDialog;

  /** the class filter in use. */
  protected AbstractItemFilter m_Filter;

  /** the minimum number of characters before triggering search events. */
  protected int m_MinimumChars;

  /** the proposed classes. */
  protected Class[] m_ProposedClasses = new Class[0];

  /** for post-processing objects. */
  protected PostProcessObjectHandler m_PostProcessObjectHandler;

  /**
   * Handles the GUI side of editing values.
   *
   * @version $Revision$
   */
  public class GOEPanel
    extends JPanel {

    /** for serialization. */
    static final long serialVersionUID = 3656028520876011335L;

    /** the split pane. */
    protected BaseSplitPane m_SplitPane;

    /** the left panel. */
    protected JPanel m_PanelLeft;

    /** the right panel. */
    protected JPanel m_PanelRight;

    /** the tree to use. */
    protected ClassTree m_Tree;

    /** the class tree. */
    protected GenericObjectEditorClassTreePanel m_PanelTree;

    /** The component that performs object customization. */
    protected PropertySheetPanel m_PropertySheetChild;

    /** The names of the proposed classes. */
    protected JComboBox<String> m_ComboBoxClassname;

    /** The name of the current class. */
    protected JLabel m_LabelClassname;

    /** Open object from disk. */
    protected BaseButton m_ButtonOpen;

    /** Save object to disk. */
    protected BaseButton m_ButtonSave;

    /** ok button. */
    protected BaseButton m_ButtonOK;

    /** cancel button. */
    protected BaseButton m_ButtonCancel;

    /** revert button. */
    protected BaseButton m_ButtonRevert;

    /** The filechooser for opening and saving object files. */
    protected transient ObjectFileChooser m_FileChooser;

    /** the button for copy/paste menu. */
    protected BaseButton m_ButtonCopyPaste;

    /** the top panel with the classname and choose button. */
    protected JPanel m_TopPanel;

    /** whether to ignore selection changes to the combobox. */
    protected boolean m_IgnoreChanges;

    /**
     * Creates the GUI editor component.
     */
    public GOEPanel() {
      super();

      m_Backup = copyObject(m_Object);

      setLayout(new BorderLayout());

      m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
      m_SplitPane.setResizeWeight(0.0);
      m_SplitPane.setDividerLocation(250);
      add(m_SplitPane, BorderLayout.CENTER);

      m_PanelLeft = new JPanel(new BorderLayout());
      m_SplitPane.setLeftComponent(m_PanelLeft);

      m_PanelRight = new JPanel(new BorderLayout());
      m_SplitPane.setRightComponent(m_PanelRight);

      m_Tree = new ClassTree();
      m_Tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      m_Tree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
	if (m_IgnoreChanges || isReadOnly())
	  return;
	String clsname = m_Tree.getSelectedItem();
	try {
	  Class cls = Class.forName(clsname);
	  setValue(cls.newInstance());
	}
	catch (Exception ex) {
	  // ignored
	}
      });
      m_PanelTree = new GenericObjectEditorClassTreePanel(m_Tree);
      m_PanelTree.setVisible(m_canChangeClassInDialog);
      m_PanelLeft.add(m_PanelTree, BorderLayout.CENTER);

      m_LabelClassname = new JLabel("None");
      m_ComboBoxClassname = new JComboBox<>(new String[]{"None"});
      m_ComboBoxClassname.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      m_ComboBoxClassname.addActionListener((ActionEvent e) -> {
	if ((m_ComboBoxClassname.getSelectedIndex() == -1) || m_IgnoreChanges)
	  return;
	// update property sheet
	try {
	  setValue(ObjectCopyHelper.newInstance("" + m_ComboBoxClassname.getSelectedItem()));
	}
	catch (Exception ex) {
	  ex.printStackTrace();
	}
      });

      m_PropertySheetChild = new PropertySheetPanel();
      m_PropertySheetChild.addPropertyChangeListener((PropertyChangeEvent evt) -> {
        if (!m_IgnoreChanges)
	  GenericObjectEditor.this.firePropertyChange();
      });

      m_ButtonOpen = new BaseButton(GUIHelper.getIcon("open.gif"));
      m_ButtonOpen.setActionCommand(ACTION_CMD_OPEN);
      m_ButtonOpen.setToolTipText("Load a serialized object");
      m_ButtonOpen.setEnabled(true);
      m_ButtonOpen.addActionListener((ActionEvent e) -> {
	Object object = openObject();
	if (object != null)
	  setValue(object);
      });

      m_ButtonSave = new BaseButton(GUIHelper.getIcon("save.gif"));
      m_ButtonSave.setActionCommand(ACTION_CMD_SAVE);
      m_ButtonSave.setToolTipText("Save the current as serialized object");
      m_ButtonSave.setEnabled(true);
      m_ButtonSave.addActionListener((ActionEvent e) -> saveObject(m_Object));

      m_ButtonOK = new BaseButton("OK");
      m_ButtonOK.setActionCommand(ACTION_CMD_OK);
      m_ButtonOK.setEnabled(true);
      m_ButtonOK.setMnemonic('O');
      m_ButtonOK.setToolTipText("Use this setup and close dialog");
      m_ButtonOK.addActionListener((ActionEvent e) -> {
	m_Backup = copyObject(m_Object);
	close();
      });

      m_ButtonCancel = new BaseButton("Cancel");
      m_ButtonCancel.setActionCommand(ACTION_CMD_CANCEL);
      m_ButtonCancel.setEnabled(true);
      m_ButtonCancel.setMnemonic('C');
      m_ButtonCancel.setToolTipText("Discard changes and close dialog");
      m_ButtonCancel.addActionListener((ActionEvent e) -> {
	if (m_Backup != null)
	  m_Object = copyObject(m_Backup);
	close();
      });

      m_ButtonRevert = new BaseButton(GUIHelper.getIcon("undo.gif"));
      m_ButtonRevert.setActionCommand(ACTION_CMD_REVERT);
      m_ButtonRevert.setEnabled(true);
      m_ButtonRevert.setToolTipText("Revert changes");
      m_ButtonRevert.addActionListener((ActionEvent e) -> {
	if (m_Backup != null) {
	  m_Object = copyObject(m_Backup);
	  GenericObjectEditor.this.firePropertyChange();
	  m_ObjectNames = getClasses();
	  updateObjectNames();
	  updateChildPropertySheet();
	}
      });

      m_ButtonCopyPaste = new BaseButton(GUIHelper.getIcon("arrow-head-down.png"));
      m_ButtonCopyPaste.setToolTipText("Displays copy/paste/favorites action menu");
      m_ButtonCopyPaste.addActionListener((ActionEvent e) -> {
	GenericObjectEditorPopupMenu menu = new GenericObjectEditorPopupMenu(GenericObjectEditor.this, m_ButtonCopyPaste);
	menu.show(m_ButtonCopyPaste, 0, m_ButtonCopyPaste.getHeight());
      });

      m_TopPanel = new JPanel(new BorderLayout());
      JPanel chooseButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      chooseButtonPanel.add(m_ButtonCopyPaste);
      m_TopPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      m_TopPanel.add(chooseButtonPanel, BorderLayout.EAST);
      m_TopPanel.add(m_ComboBoxClassname, BorderLayout.CENTER);
      m_PanelRight.add(m_TopPanel, BorderLayout.NORTH);

      // popup menu
      m_ComboBoxClassname.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isRightClick(e)) {
	    e.consume();
	    GenericObjectEditorPopupMenu menu = new GenericObjectEditorPopupMenu(GenericObjectEditor.this, GOEPanel.this);
	    menu.show(GOEPanel.this, e.getX(), e.getY());
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
      });

      JPanel childPanel = new JPanel(new BorderLayout());
      childPanel.add(m_PropertySheetChild, BorderLayout.CENTER);
      m_PanelRight.add(childPanel, BorderLayout.CENTER);

      JPanel allButs = new JPanel(new GridLayout(1, 2));
      JPanel leftButs = new JPanel();
      JPanel rightButs = new JPanel();
      allButs.add(leftButs, BorderLayout.WEST);
      allButs.add(rightButs, BorderLayout.EAST);
      allButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      leftButs.setLayout(new FlowLayout(FlowLayout.LEFT));
      leftButs.add(m_ButtonOpen);
      leftButs.add(m_ButtonSave);
      leftButs.add(m_ButtonRevert);
      rightButs.setLayout(new FlowLayout(FlowLayout.RIGHT));
      rightButs.add(m_ButtonOK);
      rightButs.add(m_ButtonCancel);
      add(allButs, BorderLayout.SOUTH);

      if (m_ClassType != null) {
	m_ObjectNames = getClasses();
	if (m_Object != null) {
	  updateObjectNames();
	  updateChildPropertySheet();
	}
      }

      m_PanelTree.focusSearch();
    }

    /**
     * Disposes the window the GOE belongs to, if possible.
     */
    protected void close() {
      if ((getTopLevelAncestor() != null) && (getTopLevelAncestor() instanceof Window)) {
	Window w = (Window) getTopLevelAncestor();
	w.dispose();
      }
    }

    /**
     * Enables/disables the cancel button.
     *
     * @param flag 	true to enable cancel button, false
     * 			to disable it
     */
    protected void setCancelButton(boolean flag) {
      if (m_ButtonCancel != null)
	m_ButtonCancel.setEnabled(flag);
    }

    /**
     * Opens an object from a file selected by the user.
     *
     * @return 		the loaded object, or null if the operation was
     * 			cancelled or failed to load object or incompatible type
     */
    protected Object openObject() {
      getFileChooser().setDialogTitle("Load setup from serialized object");
      int returnVal = getFileChooser().showOpenDialog(this);
      if (returnVal == ObjectFileChooser.APPROVE_OPTION) {
	File selected = getFileChooser().getSelectedFile();
	AbstractObjectReader reader = getFileChooser().getObjectReader();
	Object obj = reader.read(new PlaceholderFile(selected));
	if (obj == null) {
	  GUIHelper.showErrorMessage(
	    this, "Couldn't read object:\n" + selected, "Open object");
	  return null;
	}
	if (!m_ClassType.isAssignableFrom(obj.getClass())) {
	  GUIHelper.showErrorMessage(
	    this, "Object loaded from '" + selected + "' not of type:\n" + m_ClassType.getName(), "Open object");
	  return null;
	}
	return obj;
      }
      return null;
    }

    /**
     * Saves an object to a file selected by the user.
     *
     * @param object 	the object to save
     */
    protected void saveObject(Object object) {
      getFileChooser().setDialogTitle("Save setup as serialized object");
      int returnVal = getFileChooser().showSaveDialog(this);
      if (returnVal == ObjectFileChooser.APPROVE_OPTION) {
	File file = getFileChooser().getSelectedFile();
	AbstractObjectWriter writer = getFileChooser().getObjectWriter();
	String msg = writer.write(new PlaceholderFile(file), object);
	if (msg != null)
	    GUIHelper.showErrorMessage(this, msg, "Save object");
      }
    }

    /**
     * Creates the file chooser the user will use to save/load files with.
     */
    protected ObjectFileChooser getFileChooser() {
      ObjectFileChooser		fileChooser;

      if (m_FileChooser == null) {
	fileChooser   = new ObjectFileChooser(new File(System.getProperty("user.dir")));
	m_FileChooser = fileChooser;
      }
      
      return m_FileChooser;
    }

    /**
     * Makes a copy of an object using OptionUtils#shallowCopy(Object),
     * CloneHandler#getClone() or serialization.
     *
     * @param source 	the object to copy
     * @return 		a copy of the source object
     */
    protected Object copyObject(Object source) {
      Object 	result;

      result = ObjectCopyHelper.copyObject(source);
      setCancelButton(result != null);

      return result;
    }

    /**
     * Allows customization of the action label on the dialog.
     *
     * @param newLabel 	the new string for the ok button
     */
    public void setOkButtonText(String newLabel) {
      m_ButtonOK.setText(newLabel);
    }

    /**
     * This is used to hook an action listener to the ok button.
     *
     * @param a 	The action listener.
     */
    public void addOkListener(ActionListener a) {
      m_ButtonOK.addActionListener(a);
    }

    /**
     * This is used to hook an action listener to the cancel button.
     *
     * @param a 	The action listener.
     */
    public void addCancelListener(ActionListener a) {
      m_ButtonCancel.addActionListener(a);
    }

    /**
     * This is used to remove an action listener from the ok button.
     *
     * @param a 	The action listener
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
     * Updates the child property sheet, and creates if needed.
     */
    public void updateChildPropertySheet() {
      String 			classname;
      List<String>		list;

      m_IgnoreChanges = true;

      // Update the object name displayed
      classname = "None";
      if (m_Object != null)
	classname = m_Object.getClass().getName();

      list = new ArrayList<>();
      list.add(classname);
      for (Class cls: m_ProposedClasses) {
	if (cls.getName().equals(classname))
	  continue;
	list.add(cls.getName());
      }
      Collections.sort(list);
      m_ComboBoxClassname.setModel(new DefaultComboBoxModel<>(list.toArray(new String[list.size()])));
      m_ComboBoxClassname.setSelectedItem(classname);
      m_LabelClassname.setText(classname);

      // only one classname?
      if (list.size() == 1) {
	m_TopPanel.add(m_LabelClassname, BorderLayout.CENTER);
	m_TopPanel.remove(m_ComboBoxClassname);
      }
      else {
	m_TopPanel.add(m_ComboBoxClassname, BorderLayout.CENTER);
	m_TopPanel.remove(m_LabelClassname);
      }

      // Set the object as the target of the propertysheet
      m_PropertySheetChild.setTarget(m_Object);

      // update tree
      if (getCanChangeClassInDialog()) {
	m_Tree.setFilter(m_Filter);
	m_Tree.setItems(m_ObjectNames);
	m_Tree.setSelectedItem(m_Object.getClass().getName());
	if (m_Tree.getSelectedItem() == null)
	  m_Tree.expandAll();
        m_PanelTree.updateFilterPanel();
        m_SplitPane.setLeftComponentHidden(m_Tree.numItems() < 2);
      }

      // Adjust size of containing window if possible
      if ((getTopLevelAncestor() != null) && (getTopLevelAncestor() instanceof Window))
	((Window) getTopLevelAncestor()).pack();

      m_IgnoreChanges = false;
    }

    /**
     * Sets whether the user can change the class in the dialog.
     *
     * @param value	if true then the user can change the class
     */
    public void setCanChangeClassInDialog(boolean value) {
      m_SplitPane.setLeftComponentHidden(!value);
      m_PanelTree.setVisible(value);
    }

    /**
     * Returns whether the user can change the class in the dialog.
     *
     * @return		true if the user can change the class
     */
    public boolean getCanChangeClassInDialog() {
      return m_PanelTree.isVisible();
    }

    /**
     * Sets whether the dialog is read-only or not.
     *
     * @param value	if true then the dialog is read-only
     */
    public void setReadOnly(boolean value) {
      m_ButtonOK.setEnabled(!value);
      m_PanelTree.setReadOnly(value);
    }

    /**
     * Returns whether the dialog is read-only or not.
     *
     * @return		true if the dialog is read-only
     */
    public boolean isReadOnly() {
      return !m_ButtonOK.isEnabled();
    }
  }

  /**
   * Interface for post-processing the object after selecting it, but before
   * setting it.
   *
   * @see	GenericObjectEditor#setObject(Object)
   */
  public interface PostProcessObjectHandler {

    /**
     * Gets called just before the object would be set, i.e., updating the UI.
     *
     * @param goe	the generic object editor that triggered it
     * @param o		the object to be set
     * @return		the potentially updated object
     */
    public Object postProcessObject(GenericObjectEditor goe, Object o);
  }

  /**
   * Default constructor.
   */
  public GenericObjectEditor() {
    this(false);
  }

  /**
   * Constructor that allows specifying whether it is possible
   * to change the class within the editor dialog.
   *
   * @param canChangeClassInDialog 	whether the user can change the class
   */
  public GenericObjectEditor(boolean canChangeClassInDialog) {
    AdamsEditorsRegistration.registerEditors();
    Favorites.getSingleton();

    m_DefaultValueDetermined   = null;
    m_DefaultValue             = null;
    m_MinimumChars             = 0;
    m_PostProcessObjectHandler = null;

    setCanChangeClassInDialog(canChangeClassInDialog);
  }

  /**
   * Sets whether the user can change the class in the dialog.
   *
   * @param value	if true then the user can change the class
   */
  public void setCanChangeClassInDialog(boolean value) {
    m_canChangeClassInDialog = value;
    if (m_EditorComponent != null)
      m_EditorComponent.setCanChangeClassInDialog(value);
    if (m_CustomPanelChooseButton != null)
      m_CustomPanelChooseButton.setVisible(value);
  }

  /**
   * Returns whether the user can change the class in the dialog.
   *
   * @return		true if the user can change the class
   */
  public boolean getCanChangeClassInDialog() {
    return m_canChangeClassInDialog;
  }

  /**
   * Sets whether the dialog is read-only.
   *
   * @param value	if true then the dialog is read-only
   */
  public void setReadOnly(boolean value) {
    m_EditorComponent.setReadOnly(value);
  }

  /**
   * Returns whether the dialog is read-only.
   *
   * @return		true if the dialog is read-only
   */
  public boolean isReadOnly() {
    return m_EditorComponent.isReadOnly();
  }

  /**
   * Sets the filter to use for filtering the display.
   *
   * @param value	the filter, null if to disable filtering
   */
  public void setFilter(AbstractItemFilter value) {
    m_Filter = value;
  }

  /**
   * Returns the current filter.
   *
   * @return		the filter, null if none set
   */
  public AbstractItemFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the backup object (may be null if there is no
   * backup.
   *
   * @return 		the backup object
   */
  public Object getBackup() {
    return m_Backup;
  }

  /**
   * Called when the class of object being edited changes.
   *
   * @return 		the vector containing all the (sorted) classnames
   */
  protected List<String> getClasses() {
    return new ArrayList<>(Arrays.asList(ClassLister.getSingleton().getClassnames(m_ClassType)));
  }

  /**
   * Updates the list of selectable object names, adding any new names to the
   * list.
   */
  protected void updateObjectNames() {
    if (m_ObjectNames == null)
      m_ObjectNames = getClasses();

    if (m_Object != null) {
      String className = m_Object.getClass().getName();
      if (!m_ObjectNames.contains(className)) {
	m_ObjectNames.add(className);
	Collections.sort(m_ObjectNames);
      }
    }
  }

  /**
   * Sets whether the editor is "enabled", meaning that the current
   * values will be painted.
   *
   * @param newVal 	a value of type 'boolean'
   */
  public void setEnabled(boolean newVal) {
    if (newVal != m_Enabled)
      m_Enabled = newVal;
  }

  /**
   * Sets the class of values that can be edited.
   *
   * @param type 	a value of type 'Class'
   */
  public void setClassType(Class type) {
    m_ClassType = type;
    m_ObjectNames = getClasses();
    m_DefaultValueDetermined = null;
  }

  /**
   * Returns the currently set class.
   *
   * @return		the current class
   */
  public Class getClassType() {
    return m_ClassType;
  }

  /**
   * Determines the default value.
   *
   * @return		the default value, null if none found or not possible
   * 			to instantiate
   */
  protected Object determineDefaultValue() {
    String 		defaultValue;
    List<String>	list;
    int			i;

    if (m_DefaultValueDetermined == null) {
      m_DefaultValueDetermined = true;
      list = getClasses();
      if (m_Filter != null) {
	i    = 0;
	while ((list.size() > 0) && (i < list.size())) {
	  if (!m_Filter.filter(list.get(i)))
	    list.remove(i);
	  else
	    i++;
	}
      }
      defaultValue = null;
      if (list.size() > 0)
	defaultValue = list.get(0);
      try {
	if (defaultValue == null)
	  throw new IllegalStateException("No classes available!");
	else
	  m_DefaultValue = ObjectCopyHelper.newInstance(defaultValue);
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Problem loading the first class: " + defaultValue, e);
	m_DefaultValue = null;
      }
    }

    return m_DefaultValue;
  }

  /**
   * Checks and, if possible, sets the class type for the given object.
   * If a class type is determined successfully, {@link #m_canChangeClassInDialog}
   * is enabled as well.
   * <br><br>
   * It is assumed that a missing class type is due to the fact that the
   * GOE is called from another framework which doesn't know that 
   * {@link #setClassType(Class)} needs to called in order to make it work
   * properly.
   * 
   * @param obj		the object to (potentially) set the class type for
   */
  protected boolean checkClassType(Object obj) {
    String[]	superclasses;
    
    if (m_ClassType != null)
      return true;
    
    superclasses = ClassLister.getSingleton().getSuperclasses(obj.getClass());
    if (superclasses.length == 0) {
      LOGGER.log(Level.SEVERE, "No class type set up for GenericObjectEditor and unable to determine one!");
      return false;
    }
    
    if (superclasses.length > 1)
      LOGGER.log(Level.SEVERE, "No class type set up for GenericObjectEditor and more than one superclass found, defaulting to: " + superclasses[0]);
    
    try {
      m_ClassType              = Class.forName(superclasses[0]);
      m_canChangeClassInDialog = true;
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize class type: " + superclasses[0], e);
      return false;
    }
    
    return true;
  }
  
  /**
   * Sets the current object to be the default, taken as the first item in
   * the chooser.
   */
  public void setDefaultValue() {
    Object	defaultValue;

    if (m_ClassType == null) {
      LOGGER.log(Level.SEVERE, "setDefaultValue: No class type set up for GenericObjectEditor!");
      return;
    }

    defaultValue = determineDefaultValue();
    if (defaultValue != null)
      setValue(defaultValue);
  }

  /**
   * Sets the current Object. If the Object is in the
   * Object chooser, this becomes the selected item (and added
   * to the chooser if necessary).
   *
   * @param o 		an object that must be a Object.
   */
  public void setValue(Object o) {
    if (!checkClassType(o))
      return;

    if (!m_ClassType.isAssignableFrom(o.getClass())) {
      LOGGER.log(Level.SEVERE, "setValue object not of correct type: " + m_ClassType.getName() + " != " + o.getClass().getName());
      return;
    }

    setObject(o);

    if (m_EditorComponent != null)
      m_EditorComponent.repaint();

    updateObjectNames();
  }

  /**
   * Sets the current Object.
   *
   * @param obj 	a value of type 'Object'
   */
  protected void setObject(Object obj) {
    Object	newObj;

    // This should really call equals() for comparison.
    boolean trueChange ;
    if (getValue() != null)
      trueChange = (!obj.equals(getValue()));
    else
      trueChange = true;

    if (m_Object != null)
      m_Backup = m_Object;
    else
      m_Backup = ObjectCopyHelper.copyObject(obj);

    newObj = ObjectCopyHelper.copyObject(obj);
    if (m_PostProcessObjectHandler != null)
      newObj = m_PostProcessObjectHandler.postProcessObject(this, newObj);

    m_Object = newObj;

    if (m_EditorComponent != null)
      m_EditorComponent.updateChildPropertySheet();

    if (trueChange)
      firePropertyChange();
  }

  /**
   * Gets the current Object.
   *
   * @return 		the current Object
   */
  public Object getValue() {
    return ObjectCopyHelper.copyObject(m_Object);
  }

  /**
   * Supposedly returns an initialization string to create a Object
   * identical to the current one, including it's state, but this doesn't
   * appear possible given that the initialization string isn't supposed to
   * contain multiple statements.
   *
   * @return 		the java source code initialisation string
   */
  public String getJavaInitializationString() {
    return "new " + m_Object.getClass().getName() + "()";
  }

  /**
   * Returns true to indicate that we can paint a representation of the
   * Object.
   *
   * @return 		always true
   */
  public boolean isPaintable() {
    return true;
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  public void paintValue(Graphics gfx, Rectangle box) {
    String 			rep;
    int 			dotPos;
    Font 			originalFont;
    Color			originalColor;
    FontMetrics			fm;
    int 			vpad;
    String			optionStr;
    AbstractCommandLineHandler	handler;

    if (m_Enabled) {
      if (m_Object != null) {
	if (m_Object instanceof CustomDisplayStringProvider) {
	  rep = ((CustomDisplayStringProvider) m_Object).toDisplay();
	}
	else {
	  rep = m_Object.getClass().getName();
	  dotPos = rep.lastIndexOf('.');
	  if (dotPos != -1)
	    rep = rep.substring(dotPos + 1);
	  handler   = AbstractCommandLineHandler.getHandler(m_Object);
	  optionStr = handler.joinOptions(handler.getOptions(m_Object));
	  rep += " " + optionStr;
	  rep = rep.trim();
	}
        if (rep.isEmpty())
          rep = AbstractPropertyEditorSupport.EMPTY;
      }
      else {
	rep = "None";
      }
      originalFont = gfx.getFont();
      originalColor = gfx.getColor();

      gfx.setFont(originalFont.deriveFont(Font.PLAIN));
      gfx.setColor(Color.BLACK);
      fm   = gfx.getFontMetrics();
      vpad = (box.height - fm.getHeight());
      gfx.drawString(rep, 2, fm.getAscent() + vpad);

      gfx.setFont(originalFont);
      gfx.setColor(originalColor);
    }
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
   * @param text 			the text value
   * @throws IllegalArgumentException 	as we don't support getting/setting
   * 					values as text.
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
   * @return 		a value of type 'Component'
   */
  public Component getCustomEditor() {
    if (m_EditorComponent == null) {
      m_EditorComponent = new GOEPanel();
      m_EditorComponent.setCanChangeClassInDialog(getCanChangeClassInDialog());
    }

    return m_EditorComponent;
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
   * Adds a PropertyChangeListener who will be notified of value changes.
   *
   * @param l 		a value of type 'PropertyChangeListener'
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    m_Support.addPropertyChangeListener(l);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param l 		a value of type 'PropertyChangeListener'
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    m_Support.removePropertyChangeListener(l);
  }

  /**
   * Gets the custom panel used for editing the object.
   *
   * @return 		the panel
   */
  public JPanel getCustomPanel() {
    JPanel 	buttonPanel;

    if (m_CustomPanel == null) {
      m_ObjectPropertyPanel = new PropertyPanel(this, true);
      m_CustomPanel = new JPanel(new BorderLayout()) {
	private static final long serialVersionUID = 7871364274183770690L;
	/** Forwards the tool tip to all its sub-panels. */
	@Override
	public void setToolTipText(String text) {
	  super.setToolTipText(text);
	  for (int i = 0; i < getComponentCount(); i++) {
	    if (getComponent(i) instanceof JPanel) {
	      ((JPanel) getComponent(i)).setToolTipText(text);
	    }
	  }
	}
      };
      buttonPanel = new JPanel(new GridLayout(1, 1));
      m_CustomPanelChooseButton = createChooseClassButton();
      buttonPanel.add(m_CustomPanelChooseButton);
      m_CustomPanel.add(buttonPanel, BorderLayout.EAST);
      m_CustomPanel.add(m_ObjectPropertyPanel, BorderLayout.CENTER);
    }

    return m_CustomPanel;
  }

  /**
   * Creates a button that when clicked will enable the user to change
   * the class of the object being edited.
   *
   * @return 		the choose button
   */
  protected BaseButton createChooseClassButton() {
    BaseButton setButton = new BaseButton(GUIHelper.getIcon("tree.gif"));
    setButton.setToolTipText("Select different class");

    // anonymous action listener shows a JTree popup and allows the user
    // to choose the class they want
    setButton.addActionListener((ActionEvent e) -> {
      JPopupMenu popup = getChooseClassPopupMenu();

      // show the popup where the source component is
      if (e.getSource() instanceof Component) {
	Component comp = (Component) e.getSource();
	popup.pack();
	Point p = comp.getLocationOnScreen();
	popup.show(comp, comp.getWidth(), 0);
	GUIHelper.setSizeAndLocation(popup, (int) p.getY(), (int) p.getX() + comp.getWidth());
      }
    });

    return setButton;
  }

  /**
   * Returns a popup menu that allows the user to change
   * the class of object.
   *
   * @return 		a JPopupMenu that when shown will let the user choose
   * 			the class
   */
  public BasePopupMenu getChooseClassPopupMenu() {
    updateObjectNames();

    final ClassTree tree = new ClassTree();
    tree.setFilter(m_Filter);
    tree.setItems(m_ObjectNames);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setSelectedItem(getValue().getClass().getName());
    if (tree.getSelectedItem() == null)
      tree.expandAll();

    // create the popup
    final GenericObjectEditorClassTreePopupMenu popup = new GenericObjectEditorClassTreePopupMenu(tree);
    popup.setMinimumChars(getMinimumChars());

    // respond when the user chooses a class
    tree.addTreeSelectionListener((TreeSelectionEvent e) -> {
      String classname = tree.getSelectedItem();
      if (classname == null)
        return;
      classSelected(classname);
      popup.setVisible(false);
    });

    return popup;
  }

  /**
   * Called when the user selects an class type to change to.
   *
   * @param className 	the name of the class that was selected
   */
  protected void classSelected(String className) {
    try {
      if ((m_Object != null) && m_Object.getClass().getName().equals(className))
	return;

      setValue(ObjectCopyHelper.newInstance(className));
      if (m_EditorComponent != null)
	m_EditorComponent.updateChildPropertySheet();
    }
    catch (Exception ex) {
      GUIHelper.showErrorMessage(
	  null,
	  "Could not create an example of\n"
	  + className + "\n"
	  + "from the current classpath",
	  "Class load failed");
      ex.printStackTrace();
      try {
	if (m_Backup != null)
	  setValue(m_Backup);
	else
	  setDefaultValue();
      }
      catch(Exception e) {
	LOGGER.log(Level.SEVERE, ex.getMessage(), e);
      }
    }
  }

  /**
   * Sets the proposed classes based on the provided objects.
   * This call needs to happen before calling setValue(Object).
   *
   * @param value	the proposed objects
   * @see		#setProposedClasses(Class[])
   */
  public void setProposedClasses(Object[] value) {
    List<Class>	classes;
    int		i;

    classes = new ArrayList<>();
    if (value != null) {
      for (i = 0; i < value.length; i++) {
        if (!classes.contains(value[i].getClass()))
          classes.add(value[i].getClass());
      }
    }
    setProposedClasses(classes.toArray(new Class[classes.size()]));
  }

  /**
   * Sets the proposed classes.
   * This call needs to happen before calling setValue(Object).
   *
   * @param value	the proposed classes
   */
  public void setProposedClasses(Class[] value) {
    if (value == null)
      m_ProposedClasses = new Class[0];
    else
      m_ProposedClasses = value.clone();
  }

  /**
   * Returns the proposed classes.
   *
   * @return		the proposed classes
   */
  public Class[] getProposedClasses() {
    return m_ProposedClasses;
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
   * Sets the handler for post-processing objects after they have been selected
   * but before updating the UI.
   *
   * @param value	the handler, null to remove
   */
  public void setPostProcessObjectHandler(PostProcessObjectHandler value) {
    m_PostProcessObjectHandler = value;
  }

  /**
   * Returns the handler for post-processing objects after they have been
   * selected but before updating the UI.
   *
   * @return		the handler, null if none set
   */
  public PostProcessObjectHandler getPostProcessObjectHandler() {
    return m_PostProcessObjectHandler;
  }
}
