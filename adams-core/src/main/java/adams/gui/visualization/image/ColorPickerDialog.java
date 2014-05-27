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
 * ColorPickerDialog.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

/**
 * Color picker dialog.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ColorPickerDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -6333253045301958331L;
  
  /** the color picker panel. */
  protected ColorPickerPanel m_PanelPicker;
  
  /**
   * Initializes the dialog.
   *
   * @param owner	the owning dialog
   * @param panel	the image panel this picker is for
   */
  public ColorPickerDialog(Dialog owner, ImagePanel panel) {
    super(owner, ModalityType.MODELESS);
    m_PanelPicker.setOwner(panel);
  }

  /**
   * Initializes the dialog.
   *
   * @param owner	the owning frame
   */
  public ColorPickerDialog(Frame owner, ImagePanel panel) {
    super(owner, false);
    m_PanelPicker.setOwner(panel);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setTitle("Pick color");
    
    setApproveVisible(true);
    setApproveCaption("Copy");
    setApproveMnemonic('o');
    setCancelVisible(true);
    setDiscardVisible(false);
    
    m_PanelPicker = new ColorPickerPanel();
    getContentPane().add(m_PanelPicker, BorderLayout.CENTER);
    
    m_ButtonApprove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	GUIHelper.copyToClipboard(ColorHelper.toHex(m_PanelPicker.getSelectedColor()));
      }
    });
  }
  
  @Override
  protected void finishInit() {
    super.finishInit();
    pack();
  }
  
  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();
    m_PanelPicker.start();
  }
  
  /**
   * Hook method just before the dialog is hidden.
   */
  @Override
  protected void beforeHide() {
    super.beforeHide();
    m_PanelPicker.stop();
  }
}
