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
 *    InlineEditor.java
 *
 *    Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.PropertyPanel.PopupMenuCustomizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

/** 
 * Support for inline editing of editors that support this, in order to reduce
 * number of clicks/dialogs required to enter a value.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InlineEditor
  extends BasePanel 
  implements PropertyEditor, CustomStringRepresentationHandler, 
             PopupMenuCustomizer, PropertyChangeListener , MultiSelectionEditor {

  /** for serialization. */
  private static final long serialVersionUID = 2445831775173113711L;

  /** the text field. */
  protected JTextField m_TextField;
  
  /** the button for bringing up the custom editor. */
  protected JButton m_ButtonEditor;
  
  /** whether the editor is located in a PropertySheetPanel. */
  protected PropertySheetPanel m_PropertySheetPanel;
  
  /** The property editor. */
  protected PropertyEditor m_Editor;

  /** the default background color of the panel. */
  protected Color m_DefaultBackground;

  /** the property change listeners. */
  protected HashSet<PropertyChangeListener> m_PropertyChangeListeners;

  /** whether to update the text after the actual editor was displayed. */
  protected boolean m_UpdateTextAfterDisplay;
  
  /**
   * Sets up the editing component with the supplied editor.
   *
   * @param pe 		the PropertyEditor
   */
  public InlineEditor(PropertyEditor pe) {
    this(pe, null);
  }
  
  /**
   * Sets up the editing component with the supplied editor.
   *
   * @param pe 		the PropertyEditor
   * @param panel	the ProperySheetPanel
   */
  public InlineEditor(PropertyEditor pe, PropertySheetPanel panel) {
    super();
    
    if (pe == null)
      throw new IllegalArgumentException("Editor is null!");
    if (!(pe instanceof InlineEditorSupport))
      throw new IllegalArgumentException("Editor '" + pe.getClass().getName() + "' does not implement '" + InlineEditorSupport.class.getName() + "'!");
    if (!pe.supportsCustomEditor() || (pe.getCustomEditor() == null)) {
      m_ButtonEditor.setVisible(false);
      remove(m_ButtonEditor);
    }
    
    m_Editor             = pe;
    m_PropertySheetPanel = panel;
    updateText(getInlineEditor().getInlineValue());
  }
  
  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    
    m_PropertyChangeListeners = new HashSet<PropertyChangeListener>();  
  }
  
  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());

    m_DefaultBackground = getBackground();
    setBorder(BorderFactory.createLineBorder(m_DefaultBackground));
    
    m_TextField = new JTextField(20);
    m_TextField.setText("");  // gets set after initialization
    m_TextField.setPreferredSize(
	new Dimension(
	    m_TextField.getPreferredSize().width,
	    m_TextField.getPreferredSize().height + 4));
    m_TextField.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
	updateText(m_TextField.getText());
        super.focusLost(e);
      }
    });
    m_TextField.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
	indicateValidity();
      }
      public void insertUpdate(DocumentEvent e) {
	indicateValidity();
      }
      public void changedUpdate(DocumentEvent e) {
	indicateValidity();
      }
    });
    m_TextField.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          e.consume();
          BasePopupMenu menu = new BasePopupMenu();
          customizePopupMenu(InlineEditor.this, menu);
          if (m_PropertySheetPanel != null)
            VariableSupport.updatePopup(m_PropertySheetPanel, InlineEditor.this, menu);
          menu.showAbsolute(m_TextField, e);
        }
        else {
          super.mouseClicked(e);
        }
      }
    });
    add(m_TextField, BorderLayout.CENTER);

    m_ButtonEditor = new JButton("...");
    m_ButtonEditor.setPreferredSize(
	new Dimension(
	    m_ButtonEditor.getPreferredSize().width,
	    m_TextField.getPreferredSize().height));
    m_ButtonEditor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showEditor();
      }
    });
    add(m_ButtonEditor, BorderLayout.EAST);

    updatePreferredSize();
  }

  /**
   * Returns the editor in use.
   * 
   * @return		the editor
   */
  public PropertyEditor getEditor() {
    return m_Editor;
  }
  
  /**
   * Returns the inline editor.
   * 
   * @return		the editor
   */
  public InlineEditorSupport getInlineEditor() {
    return (InlineEditorSupport) m_Editor;
  }

  /**
   * Displays ther actual editor.
   */
  protected void showEditor() {
    BaseDialog 	dlg;
    
    m_UpdateTextAfterDisplay = false;
    
    if (getParentDialog() != null)
      dlg = new BaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dlg = new BaseDialog(getParentFrame(), true);
    dlg.getContentPane().add(m_Editor.getCustomEditor(), BorderLayout.CENTER);
    dlg.pack();
    dlg.setLocationRelativeTo(InlineEditor.this);
    m_Editor.addPropertyChangeListener(InlineEditor.this);
    dlg.setVisible(true);
    m_Editor.removePropertyChangeListener(InlineEditor.this);

    if (m_UpdateTextAfterDisplay)
      setValue(m_Editor.getValue());
    m_UpdateTextAfterDisplay = false;
  }

  /**
   * Checks whether the string is valid.
   * 
   * @param s 		the string to check
   * @return		true if valid
   */
  protected boolean isValid(String s) {
    return getInlineEditor().isInlineValueValid(s);
  }
  
  /**
   * Sets the string in the text field only if valid, otherwise the current
   * inline value is used to up the text field.
   * 
   * @param s		the string to attempt to set
   * @return		true if successfully set
   */
  protected boolean updateText(String s) {
    if (isValid(s)) {
      m_TextField.setText(s);
      getInlineEditor().setInlineValue(s);
      notifyPropertyChangeListeners();
      return true;
    }
    else {
      m_TextField.setText(getInlineEditor().getInlineValue());
      return false;
    }
  }
  
  /**
   * Updates the color of the border, indicating with RED if the
   * input is invalid.
   */
  protected void indicateValidity() {
    Color	curColor;
    Color	newColor;
    
    curColor = ((LineBorder) getBorder()).getLineColor();
    if (isValid(m_TextField.getText()))
      newColor = m_DefaultBackground;
    else
      newColor = Color.RED;

    if (!newColor.equals(curColor))
      setBorder(BorderFactory.createLineBorder(newColor));
  }
  
  /**
   * Sets the number of columns for the text field.
   *
   * @param value	the number of columns (>0)
   */
  public void setTextColumns(int value) {
    if (value > 0)
      m_TextField.setColumns(value);
    else
      System.err.println("Number of columns must be >0 (provided: " + value + ")!");

    updatePreferredSize();
  }

  /**
   * Returns the number of columns of the text field.
   *
   * @return		the number of columns (>0)
   */
  public int getTextColumns() {
    return m_TextField.getColumns();
  }

  /**
   * Updates the preferred size of the panel.
   */
  protected void updatePreferredSize() {
    setPreferredSize(
	new Dimension(
	    m_TextField.getPreferredSize().width
	    + m_ButtonEditor.getPreferredSize().width,
	    m_TextField.getPreferredSize().height));
  }
  
  /**
   * Updates the text field with the current value of the editor.
   */
  public void update() {
    updateText(getInlineEditor().getInlineValue());
  }

  /**
   * Returns a custom string representation of the object.
   * 
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    if (m_Editor instanceof CustomStringRepresentationHandler) {
      return ((CustomStringRepresentationHandler) m_Editor).toCustomStringRepresentation(obj);
    }
    else {
      try {
	m_Editor.setValue(obj);
	return getInlineEditor().getInlineValue();
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to obtain custom string representation", e);
      }
    }
  }

  /**
   * Returns an object created from the custom string representation.
   * 
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    if (m_Editor instanceof CustomStringRepresentationHandler) {
      return ((CustomStringRepresentationHandler) m_Editor).fromCustomStringRepresentation(str);
    }
    else {
      try {
	getInlineEditor().setInlineValue(str);
	return m_Editor.getValue();
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to set custom string representation", e);
      }
    }
  }

  /**
   * For customizing the popup menu.
   *
   * @param owner	the property panel from where the menu originates
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(BasePanel owner, JPopupMenu menu) {
    JMenuItem		menuitem;

    menuitem = new JMenuItem("Copy");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(isValid(m_TextField.getText()));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_TextField.getSelectedText() != null)
	  GUIHelper.copyToClipboard(m_TextField.getSelectedText());
	else
	  GUIHelper.copyToClipboard(m_TextField.getText());
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Paste");
    menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
    menuitem.setEnabled(GUIHelper.canPasteStringFromClipboard());
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextField.replaceSelection(GUIHelper.pasteStringFromClipboard());
      }
    });
    menu.add(menuitem);
    
    // does the editor itself customize the menu?
    if (m_Editor instanceof PopupMenuCustomizer)
      ((PopupMenuCustomizer) m_Editor).customizePopupMenu(owner, menu);
  }

  /**
   * Set (or change) the object that is to be edited.  Primitive types such
   * as "int" must be wrapped as the corresponding object type such as
   * "java.lang.Integer".
   *
   * @param value The new target object to be edited.  Note that this
   *     object should not be modified by the PropertyEditor, rather 
   *     the PropertyEditor should create a new object to hold any
   *     modified value.
   */
  public void setValue(Object value) {
    updateText(toCustomStringRepresentation(value));
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property.  Primitive types such as "int" will
   * be wrapped as the corresponding object type such as "java.lang.Integer".
   */
  public Object getValue() {
    return fromCustomStringRepresentation(m_TextField.getText());
  }

  //----------------------------------------------------------------------

  /**
   * Determines whether this property editor is paintable.
   *
   * @return  True if the class will honor the paintValue method.
   */
  public boolean isPaintable() {
    return false;
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
   * @param gfx  Graphics object to paint into.
   * @param box  Rectangle within graphics object into which we should paint.
   */
  public void paintValue(Graphics gfx, Rectangle box) {
  }

  //----------------------------------------------------------------------

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
   * <li>Primitive expresssion: <code>2</code>
   * <li>Class constructor: <code>new java.awt.Color(127,127,34)</code>
   * <li>Static field: <code>java.awt.Color.orange</code>
   * <li>Static method: <code>javax.swing.Box.createRigidArea(new 
   *                                   java.awt.Dimension(0, 5))</code>
   * </ul>
   *
   * @return a fragment of Java code representing an initializer for the
   *         current value. It should not contain a semi-colon 
   *         ('<code>;</code>') to end the expression.
   */
  public String getJavaInitializationString() {
    return "";
  }

  //----------------------------------------------------------------------

  /**
   * Gets the property value as text.
   * 
   * @return The property value as a human editable string.
   * <p>   Returns null if the value can't be expressed as an editable string.
   * <p>   If a non-null value is returned, then the PropertyEditor should
   *	     be prepared to parse that string back in setAsText().
   */
  public String getAsText() {
    return m_TextField.getText();
  }

  /**
   * Set the property value by parsing a given String.  May raise
   * java.lang.IllegalArgumentException if either the String is
   * badly formatted or if this kind of property can't be expressed
   * as text.
   * @param text  The string to be parsed.
   */
  public void setAsText(String text) throws IllegalArgumentException {
    m_TextField.setText(text);
  }

  //----------------------------------------------------------------------

  /**
   * If the property value must be one of a set of known tagged values, 
   * then this method should return an array of the tags.  This can
   * be used to represent (for example) enum values.  If a PropertyEditor
   * supports tags, then it should support the use of setAsText with
   * a tag value as a way of setting the value and the use of getAsText
   * to identify the current value.
   *
   * @return The tag values for this property.  May be null if this 
   *   property cannot be represented as a tagged value.
   *	
   */
  public String[] getTags() {
    return null;
  }

  //----------------------------------------------------------------------

  /**
   * A PropertyEditor may choose to make available a full custom Component
   * that edits its property value.  It is the responsibility of the
   * PropertyEditor to hook itself up to its editor Component itself and
   * to report property value changes by firing a PropertyChange event.
   * <P>
   * The higher-level code that calls getCustomEditor may either embed
   * the Component in some larger property sheet, or it may put it in
   * its own individual dialog, or ...
   *
   * @return A java.awt.Component that will allow a human to directly
   *      edit the current property value.  May be null if this is
   *	    not supported.
   */

  public Component getCustomEditor() {
    return null;
  }

  /**
   * Determines whether this property editor supports a custom editor.
   *
   * @return  True if the propertyEditor can provide a custom editor.
   */
  public boolean supportsCustomEditor() {
    return false;
  }

  //----------------------------------------------------------------------

  /**
   * Register a listener for the PropertyChange event.  When a
   * PropertyEditor changes its value it should fire a PropertyChange
   * event on all registered PropertyChangeListeners, specifying the
   * null value for the property name and itself as the source.
   *
   * @param listener  An object to be invoked when a PropertyChange
   *		event is fired.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    super.addPropertyChangeListener(listener);
    m_PropertyChangeListeners.add(listener);
  }

  /**
   * Remove a listener for the PropertyChange event.
   *
   * @param listener  The PropertyChange listener to be removed.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    super.removePropertyChangeListener(listener);
    m_PropertyChangeListeners.remove(listener);
  }

  /**
   * Notifies the listeners.
   */
  protected void notifyPropertyChangeListeners() {
    PropertyChangeEvent		e;
    
    e = new PropertyChangeEvent(this, "", null, null);
    
    for (PropertyChangeListener l: m_PropertyChangeListeners)
      l.propertyChange(e);
  }
  
  /**
   * This method gets called when a bound property is changed.
   * 
   * @param evt 	A PropertyChangeEvent object describing the event source 
   *   			and the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    m_UpdateTextAfterDisplay = true;
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    Object[]			result;
    MultiLineValueDialog	dialog;
    Vector<String>		lines;
    Class			cls;
    int				i;
    Object			backup;

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the string representations, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    backup = getEditor().getValue();
    cls    = backup.getClass();
    
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new Vector<String>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = (Object[]) Array.newInstance(cls, lines.size());
      for (i = 0; i < lines.size(); i++)
	Array.set(result, i, fromCustomStringRepresentation(lines.get(i)));
      getEditor().setValue(backup);
    }
    else {
      result = (Object[]) Array.newInstance(cls, 0);
    }

    return result;
  }
  
  /**
   * Registers the text to display in a tool tip of the text field as well.
   *
   * @param text  the string to display; if the text is <code>null</code>,
   *              the tool tip is turned off for this component
   */
  public void setToolTipText(String value) {
    super.setToolTipText(value);
    m_TextField.setToolTipText(value);
  }
}
