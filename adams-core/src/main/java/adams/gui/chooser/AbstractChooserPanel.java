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
 * AbstractSelectorPanel.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.CleanUpHandler;
import adams.core.option.OptionUtils;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A panel that contains a text field with the current choice and a
 * button for bringing up a dialog offering the choice.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of object to handle
 */
public abstract class AbstractChooserPanel<T>
  extends BasePanel
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -824479551072915989L;

  /**
   * Interface for editors that can customize the popup menu.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public interface PopupMenuCustomizer
    extends adams.gui.core.PopupMenuCustomizer<AbstractChooserPanel> {

    /**
     * For customizing the popup menu.
     *
     * @param owner	the panel from where the menu originates
     * @param menu	the menu to customize
     */
    public void customizePopupMenu(AbstractChooserPanel owner, JPopupMenu menu);
  }

  /** the panel itself. */
  protected AbstractChooserPanel m_Self;

  /** the text field. */
  protected JTextField m_TextSelection;

  /** the panel for the prefix. */
  protected JPanel m_PanelPrefix;

  /** the prefix label. */
  protected JLabel m_LabelPrefix;

  /** a spacer for the prefix label. */
  protected JLabel m_LabelPrefixSpacer;

  /** the panel for the button(s). */
  protected JPanel m_PanelButtons;

  /** the button for bringing up the chooser dialog. */
  protected JButton m_ButtonSelection;

  /** listeners that listen to changes of the selected value. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** optional customizer of the popup. */
  protected PopupMenuCustomizer m_PopupMenuCustomizer;
  
  /** whether the chooser is editable. */
  protected boolean m_Editable;

  /** whether inline editing is enabled. */
  protected boolean m_InlineEditingEnabled;

  /**
   * Initializes the panel with no value.
   */
  protected AbstractChooserPanel() {
    super();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self                 = this;
    m_ChangeListeners      = new HashSet<>();
    m_Editable             = true;
    m_InlineEditingEnabled = false;
  }

  /**
   * Initializes the GUI elements.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelPrefix = new JPanel(new BorderLayout());
    m_LabelPrefix       = new JLabel("");
    m_LabelPrefixSpacer = new JLabel("");
    m_PanelPrefix.add(m_LabelPrefix, BorderLayout.CENTER);
    m_PanelPrefix.add(m_LabelPrefixSpacer, BorderLayout.EAST);
    add(m_PanelPrefix, BorderLayout.WEST);

    m_TextSelection = new JTextField(getSelectionColumns());
    m_TextSelection.setTransferHandler(null);
    m_TextSelection.setText(getDefaultString());
    m_TextSelection.setEditable(false);
    m_TextSelection.setPreferredSize(
      new Dimension(
        m_TextSelection.getPreferredSize().width,
        m_TextSelection.getPreferredSize().height + 4));
    m_TextSelection.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isDoubleClick(e) && !isInlineEditingEnabled()) {
          e.consume();
          choose();
        }
        else if (MouseUtils.isRightClick(e)) {
          e.consume();
          BasePopupMenu menu = getPopupMenu();
          if (menu != null)
            menu.showAbsolute(m_TextSelection, e);
        }
        else {
          super.mouseClicked(e);
        }
      }
    });
    m_TextSelection.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (KeyUtils.isCopy(e)) {
          e.consume();
          copyToClipboard();
        }
        else if (KeyUtils.isPaste(e) && ClipboardHelper.canPasteStringFromClipboard()) {
          e.consume();
          pasteFromClipboard();
        }

        if (!e.isConsumed())
          super.keyPressed(e);
      }
    });
    m_TextSelection.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        super.focusLost(e);
        if (isInlineEditingEnabled())
          setCurrent(fromString(m_TextSelection.getText()));
      }
    });
    add(m_TextSelection, BorderLayout.CENTER);

    m_PanelButtons = new JPanel(new GridLayout(1, 0));
    add(m_PanelButtons, BorderLayout.EAST);

    m_ButtonSelection = new JButton("...");
    m_ButtonSelection.setPreferredSize(
	new Dimension(
	    m_ButtonSelection.getPreferredSize().width,
	    m_TextSelection.getPreferredSize().height));
    m_ButtonSelection.addActionListener(e -> choose());
    m_PanelButtons.add(m_ButtonSelection);

    updatePreferredSize();
  }

  /**
   * Returns the number of columns in the selection text field.
   *
   * @return		the number of columns
   */
  protected int getSelectionColumns() {
    return 30;
  }

  /**
   * Sets the text for the prefix label. Mnemonics can be indicated by a
   * preceding underscore "_".
   *
   * @param value	the text to display
   * @see		GUIHelper#MNEMONIC_INDICATOR
   */
  public void setPrefix(String value) {
    char	mnemonic;
    String	caption;

    mnemonic = GUIHelper.getMnemonic(value);
    caption  = GUIHelper.stripMnemonic(value);

    m_LabelPrefix.setText(caption);
    if (mnemonic != '\0')
      m_LabelPrefix.setDisplayedMnemonic(mnemonic);
    else
      m_LabelPrefix.setDisplayedMnemonicIndex(-1);
    if (value.length() > 0)
      m_LabelPrefixSpacer.setText(" ");
    else
      m_LabelPrefixSpacer.setText("");
  }

  /**
   * Returns the current text of the prefix label.
   *
   * @return		the text currently being displayed
   */
  public String getPrefix() {
    return m_LabelPrefix.getText();
  }

  /**
   * Returns the prefix label.
   *
   * @return		the label
   */
  public JLabel getPrefixLabel() {
    return m_LabelPrefix;
  }

  /**
   * The string that is used by default. E.g., if a "null" value is provided.
   *
   * @return		the default string
   */
  protected String getDefaultString() {
    return "";
  }

  /**
   * Sets the default value.
   */
  public void setDefault() {
    m_TextSelection.setText(getDefaultString());
    m_TextSelection.setCaretPosition(0);
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  protected abstract String toString(T value);

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		if successfully set
   */
  public boolean setCurrent(T value) {
    boolean	result;
    String	str;

    result = true;

    str = getDefaultString();
    if (value != null) {
      try {
	str = toString(value);
      }
      catch (Exception e) {
	e.printStackTrace();
	str    = getDefaultString();
	result = false;
      }
    }

    m_TextSelection.setText(str);
    m_TextSelection.setCaretPosition(0);

    return result;
  }

  /**
   * Fires a change event to all subscribed listeners that the value has
   * changed.
   */
  public void fireCurrentValueChanged() {
    notifyChangeListeners(new ChangeEvent(m_Self));
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  protected abstract T fromString(String value);

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  public T getCurrent() {
    return fromString(m_TextSelection.getText());
  }

  /**
   * Copies the current settings to the clipboard.
   */
  protected void copyToClipboard() {
    if (m_TextSelection.getSelectedText() == null)
      ClipboardHelper.copyToClipboard(m_TextSelection.getText());
    else
      ClipboardHelper.copyToClipboard(m_TextSelection.getSelectedText());
  }

  /**
   * Returns the current string from the clipboard.
   *
   * @return		the string, null if not available
   */
  protected String getStringFromClipboard() {
    if (isEditable())
      return ClipboardHelper.pasteStringFromClipboard();
    else
      return OptionUtils.pasteSetupFromClipboard();
  }

  /**
   * Pastes the string representation from the clipboard.
   */
  protected void pasteFromClipboard() {
    StringBuilder	text;
    int			caret;
    String 		clipboard;

    try {
      caret     = m_TextSelection.getCaretPosition();
      clipboard = getStringFromClipboard();
      if (isEditable()) {
	caret += clipboard.length();
	if (m_TextSelection.getSelectedText() == null) {
	  text = new StringBuilder(m_TextSelection.getText());
	  text.insert(m_TextSelection.getCaretPosition(), clipboard);
	}
	else {
	  text = new StringBuilder(m_TextSelection.getText());
	  text.replace(m_TextSelection.getSelectionStart(), m_TextSelection.getSelectionEnd(), clipboard);
	}
	setCurrent(fromString(text.toString()));
      }
      else {
	setCurrent(fromString(clipboard));
      }
      if (isEditable()) {
	try {
	  m_TextSelection.setCaretPosition(caret);
	}
	catch (Exception e) {
	  // ignored
	}
      }
      notifyChangeListeners(new ChangeEvent(m_Self));
    }
    catch (Exception e) {
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  this, "Error processing clipboard content:\n" + e);
    }
  }

  /**
   * Adds a listener for connect/disconnect events to the internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes a listener for connect/disconnect events from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners with the given event.
   *
   * @param e		the event to send to the listeners
   */
  protected void notifyChangeListeners(ChangeEvent e) {
    Iterator<ChangeListener>	iter;

    iter = m_ChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().stateChanged(e);
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param enabled	if true then the sub-elements will be enabled
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);

    m_ButtonSelection.setEnabled(enabled);
    m_TextSelection.setEnabled(enabled);
  }

  /**
   * Sets whether the chooser is editable or read-only.
   * 
   * @param value	if true then the user can change the value
   */
  public void setEditable(boolean value) {
    m_Editable = value;
  }
  
  /**
   * Returns whether the chooser is editable or read-only.
   * 
   * @return		true if the user can change the value
   */
  public boolean isEditable() {
    return m_Editable;
  }

  /**
   * Sets whether inline editing is enabled, i.e., editing without bringing
   * up chooser.
   *
   * @param value         true if inlined editing enabled
   */
  public void setInlineEditingEnabled(boolean value) {
    m_InlineEditingEnabled = value;
    m_TextSelection.setEditable(value);
  }

  /**
   * Returns whether inline editing is enabled, i.e., editing without having
   * to bring up the chooser.
   *
   * @return              true if inline editing enabled
   */
  public boolean isInlineEditingEnabled() {
    return m_InlineEditingEnabled;
  }

  /**
   * Sets the tool tip, displayed when hovering with the mouse.
   *
   * @param text         the text, null to turn off
   */
  @Override
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    m_TextSelection.setToolTipText(text);
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected abstract T doChoose();

  /**
   * Lets the user choose and updates the current value if choosing was
   * successful.
   */
  public void choose() {
    T	value;

    value = doChoose();
    
    if (m_Editable) {
      if (value != null) {
	setCurrent(value);
	fireCurrentValueChanged();
      }
    }
  }

  /**
   * Checks whether the value of text field is different from the default
   * value, i.e., a proper value.
   *
   * @return		true if a proper value is available
   */
  public boolean hasValue() {
    return (!m_TextSelection.getText().equals(getDefaultString()));
  }

  /**
   * Updates the preferred size of the panel.
   */
  protected void updatePreferredSize() {
    if (m_LabelPrefix.getText().length() == 0)
      setPreferredSize(
	  new Dimension(
	        m_TextSelection.getPreferredSize().width
	      + m_ButtonSelection.getPreferredSize().width,
	      m_TextSelection.getPreferredSize().height));
    else
      setPreferredSize(
	  new Dimension(
	        m_LabelPrefix.getPreferredSize().width
	      + m_LabelPrefixSpacer.getPreferredSize().width
	      + m_TextSelection.getPreferredSize().width
	      + m_ButtonSelection.getPreferredSize().width,
	      m_TextSelection.getPreferredSize().height));
  }

  /**
   * Sets the number of columns for the text field.
   *
   * @param value	the number of columns (>0)
   */
  public void setTextColumns(int value) {
    if (value > 0)
      m_TextSelection.setColumns(value);
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
    return m_TextSelection.getColumns();
  }

  /**
   * Lets the text component grab the focus.
   */
  @Override
  public void grabFocus() {
    m_TextSelection.grabFocus();
  }

  /**
   * Returns a popup menu when right-clicking on the edit field.
   *
   * @return		the menu, null if non available
   */
  protected BasePopupMenu getPopupMenu() {
    BasePopupMenu	result;
    JMenuItem	        menuitem;

    result = new BasePopupMenu();

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.setAccelerator(GUIHelper.getKeyStroke("control pressed C"));
    menuitem.addActionListener(e -> copyToClipboard());
    result.add(menuitem);

    menuitem = new JMenuItem("Paste", GUIHelper.getIcon("paste.gif"));
    menuitem.setAccelerator(GUIHelper.getKeyStroke("control pressed V"));
    menuitem.setEnabled(isEditable() && ClipboardHelper.canPasteStringFromClipboard());
    menuitem.addActionListener(e -> pasteFromClipboard());
    result.add(menuitem);

    if (m_PopupMenuCustomizer != null) {
      result.addSeparator();
      m_PopupMenuCustomizer.customizePopupMenu(this, result);
    }

    return result;
  }

  /**
   * Sets the customizer. Use null do disable.
   *
   * @param value	the customizer, or null to disable
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the currently set customizer.
   *
   * @return		the customizer, or null if none set
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Moves the "..." button to either the left or right.
   *
   * @param left	if true the button gets moved to the left, otherwise
   *                    to the right
   */
  public void moveChooseButton(boolean left) {
    if (left)
      m_PanelPrefix.add(m_PanelButtons, BorderLayout.EAST);
    else
      add(m_PanelButtons, BorderLayout.EAST);
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br><br>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}
