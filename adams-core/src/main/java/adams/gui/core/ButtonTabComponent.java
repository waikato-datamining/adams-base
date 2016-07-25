/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ.
 */

package adams.gui.core;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

/**
 * Component to be used as tabComponent;
 * Contains a JLabel to show the text and 
 * a JButton to close the tab it belongs to 
 */
@MixedCopyright(
  copyright = "1995, 2008, Oracle and/or its affiliates",
  license = License.BSD3,
  url = "http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabComponentsDemoProject/src/components/ButtonTabComponent.java"
)
public class ButtonTabComponent extends JPanel {
  private final JTabbedPane pane;

  protected JLabel m_Label;

  protected JButton m_Button;

  public ButtonTabComponent(final JTabbedPane pane) {
    //unset default FlowLayout' gaps
    super(new FlowLayout(FlowLayout.LEFT, 0, 0));
    if (pane == null) {
      throw new NullPointerException("TabbedPane is null");
    }
    this.pane = pane;
    setOpaque(false);

    //make JLabel read titles from JTabbedPane
    m_Label = new JLabel() {
      public String getText() {
	int i = pane.indexOfTabComponent(ButtonTabComponent.this);
	if (i != -1)
	  return pane.getTitleAt(i);
	return null;
      }
    };

    add(m_Label);
    //add more space between the label and the button
    m_Label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    //tab button
    m_Button = new TabButton();
    add(m_Button);
    //add more space to the top of the component
    setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
  }

  public void setIcon(Icon value) {
    m_Label.setIcon(value);
  }

  public Icon getIcon() {
    return m_Label.getIcon();
  }

  /**
   * Adds the listener.
   *
   * @param l		the listener to add
   */
  @Override
  public synchronized void addMouseListener(MouseListener l) {
    super.addMouseListener(l);
    m_Label.addMouseListener(l);
  }

  /**
   * Removes the listener.
   *
   * @param l		the listener to remove
   */
  @Override
  public synchronized void removeMouseListener(MouseListener l) {
    super.removeMouseListener(l);
    m_Label.removeMouseListener(l);
  }

  protected class TabButton
    extends JButton
    implements ActionListener {

    /** icon when focused. */
    protected ImageIcon m_CloseIconFocused;

    /** icon when not focused. */
    protected ImageIcon m_CloseIconUnfocused;

    public TabButton() {
      int size = 16;
      m_CloseIconFocused = GUIHelper.getIcon("close_tab_focused.gif");
      m_CloseIconUnfocused = GUIHelper.getIcon("close_tab_unfocused.gif");
      setPreferredSize(new Dimension(size, size));
      setToolTipText("Close tab");
      //Make the button looks the same for all Laf's
      setUI(new BasicButtonUI());
      //Make it transparent
      setContentAreaFilled(false);
      //No need to be focusable
      setFocusable(false);
      setBorder(BorderFactory.createEtchedBorder());
      setBorderPainted(false);
      setRolloverEnabled(true);
      //Close the proper tab by clicking the button
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      int i = pane.indexOfTabComponent(ButtonTabComponent.this);
      if (i != -1)
	pane.remove(i);
    }

    //we don't want to update UI for this button
    public void updateUI() {
    }

    //paint the cross
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g.create();
      //shift the image for pressed buttons
      if (getModel().isPressed())
	g2.translate(1, 1);
      if (getModel().isRollover())
	g2.drawImage(m_CloseIconFocused.getImage(), null, null);
      else
	g2.drawImage(m_CloseIconUnfocused.getImage(), null, null);
      g2.dispose();
    }
  }
}


