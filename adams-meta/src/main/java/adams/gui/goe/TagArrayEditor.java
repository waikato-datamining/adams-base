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
 * TagArrayEditor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.ObjectCopyHelper;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.core.tags.Tag;
import adams.core.tags.TagDataType;
import adams.core.tags.TagInfo;
import adams.core.tags.TagProcessorHelper;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.TagInfoTableModel;
import adams.gui.core.TagTableModel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.PropertiesParameterDialog;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Editor for Tag arrays.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TagArrayEditor
  extends BasePanel
  implements PropertyEditor {

  /** if there are no elements in the list. */
  public static final String NONE = "[none]";

  /** the tags. */
  protected TagTableModel m_TableModel;

  /** the backup. */
  protected TagTableModel m_TableModelBackup;

  /** the listener for model updates. */
  protected TableModelListener m_ModelListener;

  /** The list component displaying current values. */
  protected BaseTableWithButtons m_Table;

  /** the button for adding a tag. */
  protected BaseButton m_ButtonAdd;

  /** the button for editing a tag. */
  protected BaseButton m_ButtonEdit;

  /** the button for removing the selected tag(s). */
  protected BaseButton m_ButtonRemove;

  /** the OK button. */
  protected BaseButton m_ButtonOK;

  /** the cancel button. */
  protected BaseButton m_ButtonCancel;

  /** to catch the event when the user is closing the dialog via the "X". */
  protected WindowAdapter m_WindowAdapter;

  /** Handles property change notification. */
  protected PropertyChangeSupport m_Support;

  /** whether the content got modified. */
  protected boolean m_Modified;

  /** the change listeners (get notified whenever items get added/removed/updated). */
  protected Set<ChangeListener> m_ArrayChangeListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TableModel           = new TagTableModel();
    m_TableModelBackup     = new TagTableModel();
    m_Support              = new PropertyChangeSupport(this);
    m_Modified             = false;
    m_ArrayChangeListeners = new HashSet<>();
    m_WindowAdapter        = new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	restore();
	super.windowClosing(e);
      }
    };
  }

  /**
   * Initializes the widget.
   */
  @Override
  protected void initGUI() {
    BasePanel	panelButtons;

    super.initGUI();

    setMinimumSize(new Dimension(500, 300));
    setPreferredSize(new Dimension(500, 300));

    setLayout(new BorderLayout());

    m_Table = new BaseTableWithButtons(m_TableModel);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.getComponent().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    add(m_Table, BorderLayout.CENTER);

    m_ButtonAdd = new BaseButton("Add");
    m_ButtonAdd.addActionListener((ActionEvent e) -> addTag());
    m_Table.addToButtonsPanel(m_ButtonAdd);

    m_ButtonEdit = new BaseButton("Edit");
    m_ButtonEdit.addActionListener((ActionEvent e) -> editTag());
    m_Table.addToButtonsPanel(m_ButtonEdit);
    m_Table.setDoubleClickButton(m_ButtonEdit);

    m_ButtonRemove = new BaseButton("Remove");
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeSelectedTags());
    m_Table.addToButtonsPanel(m_ButtonRemove);

    m_ModelListener = (TableModelEvent e) -> notifyArrayChangeListeners();
    m_TableModel.addTableModelListener(m_ModelListener);

    panelButtons = new BasePanel(new FlowLayout(FlowLayout.RIGHT));
    add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new BaseButton("OK");
    m_ButtonOK.addActionListener((ActionEvent e) -> {
      apply();
      firePropertyChange();
      close();
    });
    panelButtons.add(m_ButtonOK);

    m_ButtonCancel = new BaseButton("Cancel");
    m_ButtonCancel.addActionListener((ActionEvent e) -> {
      restore();
      updateButtons();
      close();
    });
    panelButtons.add(m_ButtonCancel);
  }

  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonEdit.setEnabled(m_Table.getSelectedRowCount() == 1);
    m_ButtonRemove.setEnabled(m_Table.getSelectedRowCount() > 0);
  }

  /**
   * Set (or change) the object that is to be edited.  Primitive types such
   * as "int" must be wrapped as the corresponding object type such as
   * "java.lang.Integer".
   *
   * @param value The new target object to be edited.  Note that this
   *              object should not be modified by the PropertyEditor, rather
   *              the PropertyEditor should create a new object to hold any
   *              modified value.
   */
  @Override
  public void setValue(Object value) {
    Tag[]	tags;

    tags = (Tag[]) value;
    m_TableModel.clear();
    m_TableModel.addAll(Arrays.asList(ObjectCopyHelper.copyObjects(tags)));
    m_TableModelBackup.addAll(Arrays.asList(ObjectCopyHelper.copyObjects(tags)));
    m_Table.setOptimalColumnWidth();
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property.  Primitive types such as "int" will
   * be wrapped as the corresponding object type such as "java.lang.Integer".
   */
  @Override
  public Object getValue() {
    Tag[]	result;
    int		i;

    result = new Tag[m_TableModel.getRowCount()];
    for (i = 0; i < m_TableModel.getRowCount(); i++)
      result[i] = (Tag) m_TableModel.get(i).getClone();

    return result;
  }

  /**
   * Determines whether this property editor is paintable.
   *
   * @return True if the class will honor the paintValue method.
   */
  @Override
  public boolean isPaintable() {
    return true;
  }

  /**
   * Paint a representation of the value into a given area of screen
   * real estate.  Note that the propertyEditor is responsible for doing
   * its own clipping so that it fits into the given rectangle.
   * <p>
   * If the PropertyEditor doesn't honor paint requests (see isPaintable)
   * this method should be a silent noop.
   * <p>
   * The given Graphics object will have the default font, color, etc of
   * the parent container.  The PropertyEditor may change graphics attributes
   * such as font and color and doesn't need to restore the old values.
   *
   * @param gfx Graphics object to paint into.
   * @param box Rectangle within graphics object into which we should paint.
   */
  @Override
  public void paintValue(Graphics gfx, Rectangle box) {
    int[] 	offset;
    String 	rep;
    Tag		tag;

    if ((m_TableModel == null) || m_TableModel.isEmpty()) {
      rep = NONE;
    }
    else if (m_TableModel.getRowCount() == 1) {
      tag = m_TableModel.get(0);
      rep = tag.tagName() + "=" + tag.tagValue();
    }
    else {
      rep = m_TableModel.getRowCount() + " tags: " + Utils.flatten(m_TableModel.toArray(), " | ");
    }
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(rep, offset[0], offset[1]);
  }

  /**
   * Returns a fragment of Java code that can be used to set a property
   * to match the editors current state. This method is intended
   * for use when generating Java code to reflect changes made through the
   * property editor.
   * <p>
   * The code fragment should be context free and must be a legal Java
   * expression as specified by the JLS.
   * <p>
   * Specifically, if the expression represents a computation then all
   * classes and static members should be fully qualified. This rule
   * applies to constructors, static methods and non primitive arguments.
   * <p>
   * Caution should be used when evaluating the expression as it may throw
   * exceptions. In particular, code generators must ensure that generated
   * code will compile in the presence of an expression that can throw
   * checked exceptions.
   * <p>
   * Example results are:
   * <ul>
   * <li>Primitive expresssion: {@code 2}
   * <li>Class constructor: {@code new java.awt.Color(127,127,34)}
   * <li>Static field: {@code java.awt.Color.orange}
   * <li>Static method: {@code javax.swing.Box.createRigidArea(new
   *                                   java.awt.Dimension(0, 5))}
   * </ul>
   *
   * @return a fragment of Java code representing an initializer for the
   * current value. It should not contain a semi-colon
   * ('{@code ;}') to end the expression.
   */
  @Override
  public String getJavaInitializationString() {
    return "null";
  }

  /**
   * Gets the property value as text.
   *
   * @return The property value as a human editable string.
   * <p>   Returns null if the value can't be expressed as an editable string.
   * <p>   If a non-null value is returned, then the PropertyEditor should
   * be prepared to parse that string back in setAsText().
   */
  @Override
  public String getAsText() {
    String[] 	str;
    int		i;

    str = new String[m_TableModel.getRowCount()];
    for (i = 0; i < m_TableModel.getRowCount(); i++)
      str[i] = m_TableModel.get(i).pairValue();

    return OptionUtils.joinOptions(str);
  }

  /**
   * Set the property value by parsing a given String.  May raise
   * java.lang.IllegalArgumentException if either the String is
   * badly formatted or if this kind of property can't be expressed
   * as text.
   *
   * @param text The string to be parsed.
   */
  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    String[]	parts;
    Tag[]	tags;
    int		i;

    try {
      parts = OptionUtils.splitOptions(text);
      tags  = new Tag[parts.length];
      for (i = 0; i < parts.length; i++)
	tags[i] = new Tag(parts[i]);
      setValue(tags);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse: " + text, e);
    }
  }

  /**
   * If the property value must be one of a set of known tagged values,
   * then this method should return an array of the tags.  This can
   * be used to represent (for example) enum values.  If a PropertyEditor
   * supports tags, then it should support the use of setAsText with
   * a tag value as a way of setting the value and the use of getAsText
   * to identify the current value.
   *
   * @return The tag values for this property.  May be null if this
   * property cannot be represented as a tagged value.
   */
  @Override
  public String[] getTags() {
    return null;
  }

  /**
   * A PropertyEditor may choose to make available a full custom Component
   * that edits its property value.  It is the responsibility of the
   * PropertyEditor to hook itself up to its editor Component itself and
   * to report property value changes by firing a PropertyChange event.
   * <p>
   * The higher-level code that calls getCustomEditor may either embed
   * the Component in some larger property sheet, or it may put it in
   * its own individual dialog, or ...
   *
   * @return A java.awt.Component that will allow a human to directly
   * edit the current property value.  May be null if this is
   * not supported.
   */
  @Override
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
   * Determines whether this property editor supports a custom editor.
   *
   * @return True if the propertyEditor can provide a custom editor.
   */
  @Override
  public boolean supportsCustomEditor() {
    return true;
  }

  /**
   * Determines the tag infos to choose from.
   *
   * @return		the infos
   */
  protected List<TagInfo> determineTagInfos() {
    List<TagInfo>		result;
    Object			parent;
    GenericObjectEditorDialog	dlg;
    Object			obj;

    result = null;

    // locate the GOE dialog of this editor
    parent = GUIHelper.getParent(this, GenericObjectEditorDialog.class);
    if (parent != null) {
      // find parent GOE dialog to determine the TagHandler
      parent = GUIHelper.getParent(((GenericObjectEditorDialog) parent).getParent(), GenericObjectEditorDialog.class);
      if (parent != null) {
	dlg = (GenericObjectEditorDialog) parent;
	obj = dlg.getCurrent();
	System.out.println(obj.getClass());
	if (obj != null)
	  result = TagProcessorHelper.getApplicableTags(obj.getClass());
	System.out.println(result);
      }
    }

    if (result == null)
      result = TagProcessorHelper.getAllTags();

    return result;
  }

  /**
   * Converts the tag data type into the type for the dialog.
   *
   * @param type	the type to convert
   * @return		the converted type
   */
  protected PropertyType convertType(TagDataType type) {
    switch (type) {
      case BOOLEAN:
	return PropertyType.BOOLEAN;
      case BYTE:
      case INT:
      case LONG:
	return PropertyType.INTEGER;
      case FLOAT:
      case DOUBLE:
	return PropertyType.DOUBLE;
      default:
	return PropertyType.STRING;
    }
  }

  /**
   * Displays dialog for entering a tag.
   *
   * @param tag 	the tag to enter
   * @param title 	the title for the dialog
   * @return		the new tag if accepted, null otherwise
   */
  protected Tag enterTag(Tag tag, String title) {
    Tag 			result;
    TagInfo 			info;
    PropertiesParameterDialog 	dialog;
    Properties 			props;

    info = TagProcessorHelper.getTagInfo(tag.tagName());

    if (getParentDialog() != null)
      dialog = new PropertiesParameterDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new PropertiesParameterDialog(getParentFrame(), true);
    dialog.setTitle(title);
    dialog.setDefaultCloseOperation(PropertiesParameterDialog.DISPOSE_ON_CLOSE);
    dialog.setInfo(info.getInformation());
    dialog.getPropertiesParameterPanel().setUseMnemonicIndicators(false);
    dialog.getPropertiesParameterPanel().addPropertyType("value", convertType(info.getDataType()));
    dialog.getPropertiesParameterPanel().setLabel("value", tag.tagName());
    props = new Properties();
    props.setProperty("value", tag.tagValue());
    dialog.setProperties(props);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getOption() != PropertiesParameterDialog.APPROVE_OPTION)
      return null;

    result = new Tag(tag.tagName(), dialog.getProperties().getProperty("value"));

    return result;
  }

  /**
   * Lets the user add a tag.
   */
  protected void addTag() {
    ApprovalDialog	tagInfoDialog;
    TagInfoTableModel	tagInfoModel;
    BaseTable		tagInfoTable;
    JPanel		panelTable;
    List<TagInfo>	infos;
    TagInfo		info;
    Tag			tag;
    Tag			tagNew;

    // get possible tags
    infos = determineTagInfos();

    // remove ones already present
    for (Tag t: m_TableModel.toArray()) {
      info = TagProcessorHelper.getTagInfo(t.tagName());
      if (info != null)
	infos.remove(info);
    }

    // 1. select the tag to add
    if (getParentDialog() != null)
      tagInfoDialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      tagInfoDialog = new ApprovalDialog(getParentFrame(), false);
    tagInfoDialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    tagInfoDialog.setTitle("Select tag to add");
    tagInfoModel = new TagInfoTableModel(infos);
    tagInfoTable = new BaseTable(tagInfoModel);
    tagInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tagInfoTable.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    tagInfoTable.setOptimalColumnWidth();
    panelTable = new JPanel(new BorderLayout());
    panelTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelTable.add(new BaseScrollPane(tagInfoTable), BorderLayout.CENTER);
    tagInfoDialog.getContentPane().add(panelTable, BorderLayout.CENTER);
    tagInfoDialog.pack();
    tagInfoDialog.setLocationRelativeTo(this);
    tagInfoDialog.setVisible(true);
    if (tagInfoDialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    // 2. enter tag value
    info   = tagInfoModel.get(tagInfoTable.getSelectedRow());
    tag    = new Tag(info.getName(), "" + info.getDefaultValue());
    tagNew = enterTag(tag, "Add tag");
    if (tagNew == null)
      return;

    // add tag
    m_TableModel.add(tagNew);
    m_Table.setOptimalColumnWidth();
  }

  /**
   * Lets the user edit a tag.
   */
  protected void editTag() {
    Tag				tag;
    Tag				tagNew;

    tag    = m_TableModel.get(m_Table.getSelectedRow());
    tagNew = enterTag(tag, "Edit tag");
    if (tagNew == null)
      return;

    // update tag
    m_TableModel.set(m_Table.getSelectedRow(), tagNew);
  }

  /**
   * Removes the selected tags.
   */
  protected void removeSelectedTags() {
    int[]	indices;
    int		i;

    indices = m_Table.getSelectedRows();
    for (i = indices.length - 1; i >= 0; i--)
      m_TableModel.remove(indices[i]);
  }

  /**
   * Accepts the array.
   */
  protected void apply() {
    Object	obj;
    int		i;

    m_Modified = false;
    obj = Array.newInstance(Tag.class, m_TableModel.getRowCount());
    for (i = 0; i < m_TableModel.getRowCount(); i++)
      Array.set(obj, i, ObjectCopyHelper.copyObject(m_TableModel.get(i)));

    setValue(obj);
    updateButtons();
  }

  /**
   * Restores the values to the original ones.
   */
  protected void restore() {
    int 		i;
    TagTableModel 	tagModel;

    m_TableModel.removeTableModelListener(m_ModelListener);
    tagModel = new TagTableModel();
    for (i = 0; i < m_TableModelBackup.getRowCount(); i++)
      tagModel.add(m_TableModelBackup.get(i));
    m_TableModel = tagModel;
    m_TableModel.addTableModelListener(m_ModelListener);
    m_Table.setModel(m_TableModel);
    apply();
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
   * Fires a property change event.
   *
   * @see		#m_Support
   */
  public void firePropertyChange() {
    m_Support.firePropertyChange("", null, null);
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
    ChangeEvent e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ArrayChangeListeners)
      l.stateChanged(e);
  }
}
