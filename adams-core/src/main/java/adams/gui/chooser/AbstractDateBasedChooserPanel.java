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

/**
 * AbstractDateBasedChooserPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.Date;

import adams.core.DateFormat;
import adams.gui.core.BasePanel;
import adams.gui.dialog.ApprovalDialog;

/**
 * Ancestor for chooser panels that use {@link Date} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of date
 */
public abstract class AbstractDateBasedChooserPanel<T extends Date>
  extends AbstractChooserPanel<T> {
  
  /** for serialization. */
  private static final long serialVersionUID = -1310837169790165724L;
  
  /** the formatter for the date. */
  protected transient DateFormat m_Formatter;

  /**
   * Creates an instance of the date formatter to use.
   * 
   * @return		the formatter
   */
  protected abstract DateFormat createFormatter();

  /**
   * Returns the date formatter in use.
   * 
   * @return		the formatter
   */
  protected DateFormat getFormatter() {
    if (m_Formatter == null)
      m_Formatter = createFormatter();
    return m_Formatter;
  }

  /**
   * The string that is used by default. E.g., if a "null" value is provided.
   *
   * @return		the default string
   */
  @Override
  protected String getDefaultString() {
    return getFormatter().format(new Date());
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected T fromString(String value) {
    return convert(getFormatter().parse(value));
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(Date value) {
    return getFormatter().format(value);
  }

  /**
   * Returns the title for the dialog.
   * 
   * @return		the dialog
   */
  protected abstract String getDialogTitle();
  
  /**
   * Creates the panel to display in the dialog.
   * <p/>
   * Must implement {@link DateProvider}.
   * 
   * @return		the panel
   */
  protected abstract BasePanel createPanel();
  
  /**
   * Converts the date into the appropriate derived type.
   * 
   * @param date	the date to convert
   * @return		the converted type
   */
  protected abstract T convert(Date date);
  
  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected T doChoose() {
    T			result;
    ApprovalDialog	dialog;
    BasePanel		panel;
    
    result = null;
    
    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle(getDialogTitle());
    panel = createPanel();
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      result = convert(((DateProvider) panel).getDate());
    }
    
    return result;
  }
}
