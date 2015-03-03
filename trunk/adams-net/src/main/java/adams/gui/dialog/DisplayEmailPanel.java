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
 * DisplayEmailPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import adams.core.net.Email;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTextArea;
import adams.gui.core.KeyValuePairTableModel;

/**
 * Panel for displaying an {@link Email} object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DisplayEmailPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 8638983030175738046L;

  /** the underlying email. */
  protected Email m_Email;
  
  /** the sender. */
  protected JTextField m_TextSender;
  
  /** the recipients. */
  protected BaseTable m_TableRecipients;

  /** the recipients model. */
  protected KeyValuePairTableModel m_ModelRecipients;
  
  /** the recipients scroll pane. */
  protected BaseScrollPane m_ScrollPaneRecipients;
  
  /** the subject. */
  protected JTextField m_TextSubject;
  
  /** the body. */
  protected BaseTextArea m_TextBody;
  
  /** the attachements. */
  protected BaseTable m_TableAttachments;

  /** the attachments model. */
  protected KeyValuePairTableModel m_ModelAttachments;
  
  /** the attachmentss scroll pane. */
  protected BaseScrollPane m_ScrollPaneAttachments;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Email = null;
  }
  
  /**
   * Configures the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelTop;
    JLabel	label;
    JPanel	panel;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    panelTop = new JPanel(new BorderLayout());
    add(panelTop, BorderLayout.NORTH);

    m_TextSender = new JTextField(40);
    m_TextSender.setEditable(false);
    label        = new JLabel("From");
    label.setDisplayedMnemonic('F');
    label.setLabelFor(m_TextSender);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelTop.add(panel, BorderLayout.NORTH);
    panel.add(label);
    panel.add(m_TextSender);
    
    m_ModelRecipients = new KeyValuePairTableModel(new String[0][], getHeaderRecipients());
    m_TableRecipients = new BaseTable(m_ModelRecipients);
    m_TableRecipients.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_ScrollPaneRecipients = new BaseScrollPane(m_TableRecipients);
    m_ScrollPaneRecipients.setPreferredSize(new Dimension(0, 125));
    panelTop.add(m_ScrollPaneRecipients, BorderLayout.CENTER);
    
    m_TextSubject = new JTextField(40);
    m_TextSubject.setEditable(false);
    label        = new JLabel("Subject");
    label.setDisplayedMnemonic('S');
    label.setLabelFor(m_TextSubject);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelTop.add(panel, BorderLayout.SOUTH);
    panel.add(label);
    panel.add(m_TextSubject);
    
    m_TextBody = new BaseTextArea(5, 40);
    m_TextBody.setEditable(false);
    add(new BaseScrollPane(m_TextBody), BorderLayout.CENTER);
    
    m_ModelAttachments = new KeyValuePairTableModel(new String[0][], getHeaderAttachments());
    m_TableAttachments = new BaseTable(m_ModelAttachments);
    m_TableAttachments.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_ScrollPaneAttachments = new BaseScrollPane(m_TableAttachments);
    m_ScrollPaneAttachments.setPreferredSize(new Dimension(0, 75));
    add(m_ScrollPaneAttachments, BorderLayout.SOUTH);
  }
  
  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
    doLayout();
    m_TableRecipients.getPreferredSize().height = 100;
    m_TableAttachments.getPreferredSize().height = 50;
    doLayout();
  }
  
  /**
   * Sets the email to display.
   * 
   * @param value	the email
   */
  public void setEmail(Email value) {
    m_Email = value;
    update();
  }
  
  /**
   * Returns the currently set email.
   * 
   * @return		the current email, null if none set
   */
  public Email getEmail() {
    return m_Email;
  }

  /**
   * Returns the strings for the recipients table header.
   * 
   * @return		the header
   */
  protected String[] getHeaderRecipients() {
    return new String[]{"Type", "Address"};
  }

  /**
   * Returns the strings for the attachments table header.
   * 
   * @return		the header
   */
  protected String[] getHeaderAttachments() {
    return new String[]{"Index", "Attachment"};
  }
  
  /**
   * Updates the panel with the current email.
   */
  protected void update() {
    String[][] 	recipients;
    String[][]	attachments;
    int		i;
    int		offset;
    
    if (m_Email == null) {
      m_TextSender.setText("");
      m_ModelRecipients = new KeyValuePairTableModel(new String[0][], getHeaderRecipients());
      m_TableRecipients.setModel(m_ModelRecipients);
      m_TextSubject.setText(Email.NO_SUBJECT);
      m_TextBody.setText("");
      m_ModelAttachments = new KeyValuePairTableModel(new String[0][], getHeaderAttachments());
      m_TableAttachments.setModel(m_ModelAttachments);
    }
    else {
      m_TextSender.setText(m_Email.getFrom().getValue());
      recipients = new String[m_Email.getTo().length + m_Email.getCC().length + m_Email.getBCC().length][2];
      offset     = 0;
      for (i = 0; i < m_Email.getTo().length; i++) {
	recipients[offset + i][0] = "To";
	recipients[offset + i][1] = m_Email.getTo()[i].getValue();
      }
      offset += m_Email.getTo().length;
      for (i = 0; i < m_Email.getCC().length; i++) {
	recipients[offset + i][0] = "CC";
	recipients[offset + i][1] = m_Email.getCC()[i].getValue();
      }
      offset += m_Email.getCC().length;
      for (i = 0; i < m_Email.getBCC().length; i++) {
	recipients[offset + i][0] = "BCC";
	recipients[offset + i][1] = m_Email.getBCC()[i].getValue();
      }
      m_ModelRecipients = new KeyValuePairTableModel(recipients, getHeaderRecipients());
      m_TableRecipients.setModel(m_ModelRecipients);
      m_TextSubject.setText(m_Email.getSubject());
      m_TextBody.setText(m_Email.getBody());
      attachments = new String[m_Email.getAttachments().length][2];
      for (i = 0; i < m_Email.getAttachments().length; i++) {
	attachments[i][0] = "" + (i+1);
	attachments[i][1] = m_Email.getAttachments()[i].toString();
      }
      m_ModelAttachments = new KeyValuePairTableModel(attachments, getHeaderAttachments());
      m_TableAttachments.setModel(m_ModelAttachments);
    }

    m_TableRecipients.setOptimalColumnWidth();
    m_TableAttachments.setOptimalColumnWidth();
  }
}
