/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, NZ
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.AbstractImageReader;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.image.ImagePanel;

/**
 * Preview component for a {@link JFileChooser}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "1995, 2008, Oracle and/or its affiliates",
    license = License.BSD3,
    url = "http://docs.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemo2Project/src/components/ImagePreview.java"
)
public class ImagePreview 
  extends JComponent
  implements PropertyChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = -2018506061088072140L;
  
  /** the owning filechooser. */
  protected JFileChooser m_Owner;

  /** the width of the preview. */
  protected int m_PreviewWidth;

  /** the height of the preview. */
  protected int m_PreviewHeight;
  
  /** the thumbnail. */
  protected ImageIcon m_Thumbnail;
  
  /** the thumbnail. */
  protected File m_File;
  
  /**
   * Initializes the preview panel with a default size of 100x50 pixels.
   * 
   * @param owner	the owning file chooser
   */
  public ImagePreview(JFileChooser owner) {
    this(owner, 100, 50);
  }

  /**
   * Initializes the preview panel with a default size of 100x50 pixels.
   * 
   * @param owner	the owning file chooser
   * @param width	the width of the preview
   * @param height	the height of the preview
   */
  public ImagePreview(JFileChooser owner, int width, int height) {
    super();
    initialize();
    
    m_Owner         = owner;
    m_PreviewWidth  = width;
    m_PreviewHeight = height;
    
    setPreferredSize(new Dimension(m_PreviewWidth, m_PreviewHeight));
    owner.addPropertyChangeListener(this);
    
    setToolTipText("Click for full size view");
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e)) {
	  showImage();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Thumbnail = null;
    m_File      = null;
  }

  /**
   * Returns the owning filechooser instance.
   * 
   * @return		the owner
   */
  public JFileChooser getOwner() {
    return m_Owner;
  }
  
  /**
   * Loads the image.
   */
  protected void loadImage() {
    AbstractImageContainer	cont;
    AbstractImageReader		reader;
    ImageIcon 			tmpIcon;
    
    if (m_File == null) {
      m_Thumbnail = null;
      return;
    }
    
    // fallback method
    tmpIcon = null;
    if (m_File.exists() && !m_File.isDirectory()) {
      cont = null;
      // TODO limit size?
      if (m_Owner instanceof ImageFileChooser) {
	reader = ((ImageFileChooser) m_Owner).getReaderForFile(m_File);
	if (reader != null)
	  cont = reader.read(new PlaceholderFile(m_File));
      }
      if (cont == null) {
	cont = BufferedImageHelper.read(m_File);
      }
      if (cont != null)
	tmpIcon = new ImageIcon(cont.toBufferedImage());
    }
    
    if (tmpIcon != null) {
      if (tmpIcon.getIconWidth() > m_PreviewWidth - 10)
	m_Thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(m_PreviewWidth - 10, -1, Image.SCALE_DEFAULT));
      else
	m_Thumbnail = tmpIcon;
    }
  }

  /**
   * Gets called when a property in the file chooser changes.
   * 
   * @param e		the event
   */
  public void propertyChange(PropertyChangeEvent e) {
    boolean update = false;
    String prop = e.getPropertyName();

    //If the directory changed, don't show an image.
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      m_File = null;
      update = true;
    } 
    //If a file became selected, find out which one.
    else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      m_File = (File) e.getNewValue();
      update = true;
    }

    //Update the preview accordingly.
    if (update) {
      m_Thumbnail = null;
      if (isShowing()) {
	loadImage();
	repaint();
      }
    }
  }

  /**
   * Displays the image.
   * 
   * @param g		the graphics context
   */
  @Override
  protected void paintComponent(Graphics g) {
    if (m_Thumbnail == null)
      loadImage();

    if (m_Thumbnail != null) {
      int x = getWidth()/2 - m_Thumbnail.getIconWidth()/2;
      int y = getHeight()/2 - m_Thumbnail.getIconHeight()/2;

      if (y < 0)
	y = 0;
      if (x < 5)
	x = 5;
      
      m_Thumbnail.paintIcon(this, g, x, y);
    }
  }
  
  /**
   * Shows the current thumbnail as full-size image in a new dialog.
   */
  protected void showImage() {
    ApprovalDialog	dialog;
    ImagePanel		panel;
    
    if (m_Thumbnail == null)
      return;
    
    if (GUIHelper.getParentDialog(this) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(this), true);
    dialog.setTitle(m_File.getName());
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setDiscardVisible(false);
    dialog.setCancelVisible(false);
    dialog.setApproveVisible(true);
    dialog.setApproveCaption("Close");
    panel = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.load(m_File);
    panel.setScale(1.0);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(800, 600);
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    dialog.setVisible(true);
  }
}
