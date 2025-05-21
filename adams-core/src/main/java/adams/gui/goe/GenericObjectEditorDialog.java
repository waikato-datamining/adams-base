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
 * GenericObjectEditorDialog.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.core.option.UserMode;
import adams.core.option.UserModeSupporter;
import adams.env.Environment;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;
import adams.gui.core.Undo;
import adams.gui.core.UndoHandler;
import adams.gui.core.UserModeUtils;
import adams.gui.goe.GenericObjectEditor.GOEPanel;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;

/**
 * Displays a GenericObjectEditor.
 * <br><br>
 * Calling code needs to dispose the dialog manually or enable automatic
 * disposal:
 * <pre>
 * GenericObjectEditorDialog dialog = new ...
 * dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditorDialog
  extends BaseDialog
  implements ActionListener, UserModeSupporter, UndoHandler {

  /** for serialization. */
  private static final long serialVersionUID = 450801082654308978L;

  /** constant for dialog cancellation. */
  public final static int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;

  /** constant for dialog approval. */
  public final static int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

  /** the underlying editor. */
  protected PropertyEditor m_Editor;

  /** the current object. */
  protected Object m_Current;

  /** whether the dialog was cancelled or ok'ed. */
  protected int m_Result;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public GenericObjectEditorDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public GenericObjectEditorDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public GenericObjectEditorDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public GenericObjectEditorDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public GenericObjectEditorDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public GenericObjectEditorDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public GenericObjectEditorDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public GenericObjectEditorDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Editor  = new GenericObjectEditor();
    m_Current = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setDefaultCloseOperation(GenericObjectEditorDialog.HIDE_ON_CLOSE);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(m_Editor.getCustomEditor(), BorderLayout.CENTER);
    ((GOEPanel) m_Editor.getCustomEditor()).addOkListener(this);

    pack();
    fixSize();
  }

  /**
   * Sets the editor to use.
   *
   * @param value	the editor to use
   */
  public void setEditor(PropertyEditor value) {
    Component		view;
    JPanel		panelAll;
    JPanel		panelButton;
    final BaseButton	buttonOK;
    
    if (m_Editor.getCustomEditor() instanceof GOEPanel)
      ((GOEPanel) m_Editor.getCustomEditor()).removeOkListener(this);
    getContentPane().remove(0);

    m_Editor = value;

    if (m_Editor.getCustomEditor() instanceof GOEPanel)
      ((GOEPanel) m_Editor.getCustomEditor()).addOkListener(this);
    if (m_Editor.supportsCustomEditor()) {
      view = m_Editor.getCustomEditor();
    }
    else {
      view        = EditorHelper.findView(m_Editor);
      panelAll    = new JPanel(new BorderLayout());
      panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
      buttonOK    = new BaseButton("Close");
      buttonOK.addActionListener((ActionEvent e) -> GUIHelper.closeParent(buttonOK));
      panelButton.add(buttonOK);
      panelAll.add(panelButton, BorderLayout.SOUTH);
      panelAll.add(view, BorderLayout.CENTER);
      view = panelAll;
    }
    getContentPane().add(view, BorderLayout.CENTER);
    pack();
    fixSize();
  }

  /**
   * Ensures that the dialog doesn't get too big.
   */
  protected void fixSize() {
    boolean	update;
    int		width;
    int		height;
    Dimension	max;

    update = false;

    width  = getWidth();
    height = getHeight();
    max    = GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension(), 0.2);

    // width
    if (width > max.getWidth()) {
      width  = (int) max.getWidth();
      update = true;
    }

    // height
    if (height > max.getHeight()) {
      height = (int) max.getHeight();
      update = true;
    }

    if (update)
      setSize(width, height);
  }

  /**
   * Returns the underlying editor.
   *
   * @return		the editor in use
   */
  public PropertyEditor getEditor() {
    return m_Editor;
  }

  /**
   * Returns whether the underlying editor is GenericObjectEditor.
   *
   * @return		true if editor is a GenericObjectEditor one
   */
  public boolean isGOEEditor() {
    return (m_Editor instanceof GenericObjectEditor);
  }

  /**
   * Returns the underlying GOE editor.
   *
   * @return		the GOE editor in use, or null if other editor used
   */
  public GenericObjectEditor getGOEEditor() {
    if (m_Editor instanceof GenericObjectEditor)
      return (GenericObjectEditor) m_Editor;
    else
      return null;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();

    setUserMode(UserModeUtils.getUserMode(getParent()));

    m_Current = m_Editor.getValue();

    if (isUndoSupported()) {
      getUndo().clear();
      if (m_Current != null)
	getUndo().addUndo(m_Current, Utils.classToString(m_Current));
    }

    // only in case of GOEPanels can be determined whether OK or Cancel was
    // selected.
    if (m_Editor.getCustomEditor() instanceof GOEPanel)
      m_Result = CANCEL_OPTION;
    else
      m_Result = APPROVE_OPTION;

    if (!getUISettingsApplied())
      fixSize();
  }

  /**
   * Sets the current object.
   *
   * @param value	the current object, use null for default object
   */
  public void setCurrent(Object value) {
    if (value == null)
      ((GenericObjectEditor) m_Editor).setDefaultValue();
    else
      m_Editor.setValue(value);
    m_Current = value;
    fixSize();
  }

  /**
   * Returns the current object.
   *
   * @return		the current object
   */
  public Object getCurrent() {
    return m_Current;
  }

  /**
   * Sets the proposed classes based on the provided objects (in case the
   * editor is a GenericObjectEditor).
   *
   * @param value	the proposed objects
   * @see		#setProposedClasses(Class[])
   */
  public void setProposedClasses(Object[] value) {
    if (getGOEEditor() != null)
      getGOEEditor().setProposedClasses(value);
  }

  /**
   * Sets the proposed classes (in case the editor is a GenericObjectEditor).
   * This call needs to happen before calling setValue(Object).
   *
   * @param value	the proposed classes
   */
  public void setProposedClasses(Class[] value) {
    if (getGOEEditor() != null)
      getGOEEditor().setProposedClasses(value);
  }

  /**
   * Returns the proposed classes (in case the editor is a GenericObjectEditor).
   * This call needs to happen before calling setValue(Object).
   *
   * @return		the proposed classes
   */
  public Class[] getProposedClasses() {
    if (getGOEEditor() != null)
      return getGOEEditor().getProposedClasses();
    else
      return new Class[0];
  }

  /**
   * Returns whether the dialog got cancelled or approved.
   *
   * @return		the result
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getResult() {
    return m_Result;
  }

  /**
   * Returns whether the dialog got cancelled or approved.
   *
   * @return		the result
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getResultType() {
    return m_Result;
  }

  /**
   * Sets the user mode.
   *
   * @param value	the mode
   */
  @Override
  public void setUserMode(UserMode value) {
    if (getGOEEditor() != null)
      getGOEEditor().setUserMode(value);
  }

  /**
   * Returns the user mode.
   *
   * @return		the mode
   */
  @Override
  public UserMode getUserMode() {
    if (getGOEEditor() != null)
      return getGOEEditor().getUserMode();
    else
      return UserMode.HIGHEST;
  }

  /**
   * Gets called when the one of the buttons in the GOE panel gets pressed.
   *
   * @param e		the event
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(GenericObjectEditor.ACTION_CMD_OK)) {
      m_Current = m_Editor.getValue();
      m_Result  = APPROVE_OPTION;
      setVisible(false);
    }
  }

  /**
   * Creates a modal dialog for the parent.
   *
   * @param parent	the parent to make the dialog modal
   * @return		the dialog
   */
  public static GenericObjectEditorDialog createDialog(Container parent) {
    return createDialog(parent, null);
  }

  /**
   * Creates a modal dialog for the parent with the provided editor.
   *
   * @param parent	the parent to make the dialog modal
   * @param editor	the editor to use
   * @return		the dialog
   */
  public static GenericObjectEditorDialog createDialog(Container parent, PropertyEditor editor) {
    return createDialog(parent, editor, null);
  }

  /**
   * Creates a modal dialog for the parent with the provided editor and initial value.
   *
   * @param parent	the parent to make the dialog modal
   * @param editor	the editor to use, ignored if null
   * @param value	the value to use, ignored if null
   * @return		the dialog
   */
  public static GenericObjectEditorDialog createDialog(Container parent, PropertyEditor editor, Object value) {
    GenericObjectEditorDialog	result;

    if (GUIHelper.getParentDialog(parent) != null)
      result = new GenericObjectEditorDialog(GUIHelper.getParentDialog(parent));
    else
      result = new GenericObjectEditorDialog(GUIHelper.getParentFrame(parent));
    result.setModalityType(ModalityType.DOCUMENT_MODAL);
    result.setTitle("Object editor");

    // custom editor?
    if (editor != null)
      result.setEditor(editor);

    // initial value?
    if (value != null)
      result.setCurrent(value);

    return result;
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  @Override
  public void setUndo(Undo value) {
    if (getGOEEditor() != null)
      getGOEEditor().setUndo(value);
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  @Override
  public Undo getUndo() {
    if (getGOEEditor() != null)
      return getGOEEditor().getUndo();
    else
      return null;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  @Override
  public boolean isUndoSupported() {
    return (getGOEEditor() != null) && getGOEEditor().isUndoSupported();
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    GenericObjectEditorDialog dialog = new GenericObjectEditorDialog((Frame) null, "Object editor", true);
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.getGOEEditor().setClassType(adams.data.filter.Filter.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.setCurrent(new adams.data.filter.PassThrough());
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getResult() == APPROVE_OPTION)
      System.out.println(dialog.getCurrent());
  }
}
