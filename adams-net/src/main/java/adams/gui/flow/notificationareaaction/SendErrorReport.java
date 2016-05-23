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
 * SendErrorReport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.notificationareaaction;

import adams.core.DateUtils;
import adams.core.io.FileUtils;
import adams.core.io.TempUtils;
import adams.core.logging.LoggingLevel;
import adams.core.net.AbstractSendEmail;
import adams.core.net.Email;
import adams.core.net.EmailAddress;
import adams.core.net.EmailHelper;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.flow.core.ActorUtils;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.PanelType;
import adams.gui.core.GUIHelper;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sends an error report to the support email address (if configured).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see EmailHelper#isEnabled()
 * @see EmailHelper#getSupportEmail()
 */
public class SendErrorReport
  extends AbstractNotificationAreaAction {

  private static final long serialVersionUID = -2884370713454014768L;

  /**
   * Instantiates the action.
   */
  public SendErrorReport() {
    super();
    setName("Send error report...");
    setIcon(GUIHelper.getIcon("email.png"));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    SwingWorker 		worker;
    final String		comment;

    // comment?
    comment = GUIHelper.showInputDialog(null, "Please supply some additional information on error report");

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	AbstractSendEmail 	sendEmail;
	Email 			email;
	List<File> 		atts;
	File			prefix;
	File			file;
	String			console;
	SpreadSheet 		info;
	CsvSpreadSheetWriter 	writer;

	SwingUtilities.invokeLater(() -> m_Owner.getOwner().showStatus("Preparing error report..."));

	// data
	console = ConsolePanel.getSingleton().getPanel(PanelType.ALL).getContent();
	info    = new adams.core.SystemInfo().toSpreadSheet();

	// attachements
	atts = new ArrayList<>();
	prefix = TempUtils.createTempFile("errorreport", null);

	if (m_Owner.getOwner().getLastFlow() != null) {
	  file = new File(prefix.getAbsolutePath() + ".flow");
	  if (ActorUtils.write(file.getAbsolutePath(), m_Owner.getOwner().getLastFlow()))
	    atts.add(file);
	  else
	    ConsolePanel.getSingleton().append(
	      LoggingLevel.SEVERE, "Failed to save current flow to: " + file);
	}

	file = new File(prefix.getAbsolutePath() + ".txt");
	if (FileUtils.writeToFile(file.getAbsolutePath(), console, false))
	  atts.add(file);
	else
	  ConsolePanel.getSingleton().append(
	    LoggingLevel.SEVERE, "Failed to write console panel content for error report to: " + file);

	file   = new File(prefix.getAbsolutePath() + ".csv");
	writer = new CsvSpreadSheetWriter();
	if (writer.write(info, file))
	  atts.add(file);
	else
	  ConsolePanel.getSingleton().append(
	    LoggingLevel.SEVERE, "Failed to write system info for error report to: " + file);

	SwingUtilities.invokeLater(() -> m_Owner.getOwner().showStatus("Sending error report..."));

	// email
	email = new Email(
	  new EmailAddress(EmailHelper.getDefaultFromAddress()),
	  new EmailAddress(EmailHelper.getSupportEmail()),
	  Environment.getInstance().getProject() + " error report",
	  "Error report generated at " + DateUtils.getTimestampFormatterMsecs().format(new Date()) + "\n"
	    + "See attachements for details"
	    + (comment == null ? "" : "\nUser comment:\n" + comment),
	  atts.toArray(new File[atts.size()]));

	// send
	sendEmail = EmailHelper.getDefaultSendEmail();
	try {
	  if (sendEmail.requiresSmtpSessionInitialization()) {
	    sendEmail.initializeSmtpSession(
	      EmailHelper.getSmtpServer(),
	      EmailHelper.getSmtpPort(),
	      EmailHelper.getSmtpStartTLS(),
	      EmailHelper.getSmtpUseSSL(),
	      EmailHelper.getSmtpTimeout(),
	      EmailHelper.getSmtpRequiresAuthentication(),
	      EmailHelper.getSmtpUser(),
	      EmailHelper.getSmtpPassword());
	  }
	  sendEmail.sendMail(email);
	  SwingUtilities.invokeLater(() -> m_Owner.getOwner().showStatus("Error report sent!"));
	  GUIHelper.showInformationMessage(null, "Error report sent to " + EmailHelper.getSupportEmail());
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(null, "Failed to send error report email!", ex);
	}
	return null;
      }

      @Override
      protected void done() {
	SwingUtilities.invokeLater(() -> m_Owner.getOwner().showStatus(""));
	super.done();
      }
    };
    worker.execute();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(
      EmailHelper.isEnabled()
	&& !EmailHelper.getSupportEmail().isEmpty()
	&& !EmailHelper.getDefaultFromAddress().isEmpty());
  }
}
