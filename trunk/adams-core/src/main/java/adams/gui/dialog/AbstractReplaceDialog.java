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
 * AbstractReplaceDialog.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.core.CleanUpHandler;

/**
 * A dialog that allows the user to select a field to find and replace
 * with another one.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of object to handle
 */
public abstract class AbstractReplaceDialog<T>
  extends ApprovalDialog
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -679877900759195772L;

  /** the recursive label. */
  protected JLabel m_LabelRecursive;

  /** the recursive checkbox. */
  protected JCheckBox m_CheckBoxRecursive;

  /**
   * Creates a modal dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  protected AbstractReplaceDialog(Dialog owner) {
    super(owner, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  protected AbstractReplaceDialog(Frame owner) {
    super(owner, true);
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setTitle(getDefaultTitle());

    // panel with find/replace
    panel2 = setupPanel();
    getContentPane().add(panel2, BorderLayout.CENTER);

    // recursive
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel2.add(panel);
    m_CheckBoxRecursive = new JCheckBox("");
    m_LabelRecursive = new JLabel("Recursive");
    m_LabelRecursive.setDisplayedMnemonic('v');
    m_LabelRecursive.setLabelFor(m_CheckBoxRecursive);
    panel.add(m_LabelRecursive);
    panel.add(m_CheckBoxRecursive);

    // layout
    pack();
    adjustLabels();
    invalidate();
    validate();
    pack();

    setLocationRelativeTo(getOwner());

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	beforeHide();
        super.windowClosing(e);
      }
    });
  }

  /**
   * Returns the title of the dialog.
   *
   * @return		the title
   */
  protected abstract String getDefaultTitle();

  /**
   * Creates and returns the panel that is placed in the CENTER, containing
   * the find and replace fields. The panel must use the GridLayout layout
   * manager.
   *
   * @return		the generated panel
   */
  protected abstract JPanel setupPanel();

  /**
   * Method for adjusting the label sizes.
   */
  protected abstract void adjustLabels();

  /**
   * Sets the object to find.
   *
   * @param value	the object to find
   */
  public abstract void setFind(T value);

  /**
   * Returns the currently set object to look for.
   *
   * @return		the object, can be null
   */
  public abstract T getFind();

  /**
   * Sets the object to replace with.
   *
   * @param value	the object to replace with
   */
  public abstract void setReplace(T value);

  /**
   * Returns the currently set object to replace with.
   *
   * @return		the object, can be null
   */
  public abstract T getReplace();

  /**
   * Sets the recursive state.
   *
   * @param value	if true then recursive will be ticked
   */
  public void setRecursive(boolean value) {
    m_CheckBoxRecursive.setSelected(value);
  }

  /**
   * Returns whether the replace is to be recursive.
   *
   * @return		true if recursive
   */
  public boolean isRecursive() {
    return m_CheckBoxRecursive.isSelected();
  }

  /**
   * Hook method just before the dialog is hidden.
   */
  protected void beforeHide() {
    super.beforeHide();

    cleanUp();
  }

  /**
   * Cleans up data structures, frees up memory.
   * <p/>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}
