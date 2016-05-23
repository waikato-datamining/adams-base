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
 * SendErrorReport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

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
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.PanelType;
import adams.gui.core.GUIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sends an error report to the specified error report email address.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendErrorReport
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 4542388996174240562L;

  /**
   * Initializes the menu item with no owner.
   */
  public SendErrorReport() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public SendErrorReport(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "email.png";
  }

  /**
   * Returns whether the menu item is available.
   *
   * @return		true if available
   */
  @Override
  public boolean isAvailable() {
    return EmailHelper.isEnabled()
      && !EmailHelper.getSupportEmail().isEmpty()
      && !EmailHelper.getDefaultFromAddress().isEmpty();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Send error report...";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    String			comment;
    AbstractSendEmail 		sendEmail;
    Email			email;
    List<File> 			atts;
    File			file;
    String			console;
    SpreadSheet 		info;
    CsvSpreadSheetWriter 	writer;

    // comment?
    comment = GUIHelper.showInputDialog(null, "Please supply some additional information on error report");

    // data
    console = ConsolePanel.getSingleton().getPanel(PanelType.ALL).getContent();
    info    = new adams.core.SystemInfo().toSpreadSheet();

    // attachements
    atts = new ArrayList<>();

    file = TempUtils.createTempFile("errorreport", ".txt");
    if (FileUtils.writeToFile(file.getAbsolutePath(), console, false))
      atts.add(file);
    else
      ConsolePanel.getSingleton().append(
	LoggingLevel.SEVERE, "Failed to write console panel content for error report to: " + file);

    file   = TempUtils.createTempFile("errorreport", ".csv");
    writer = new CsvSpreadSheetWriter();
    if (writer.write(info, file))
      atts.add(file);
    else
      ConsolePanel.getSingleton().append(
	LoggingLevel.SEVERE, "Failed to write system info for error report to: " + file);

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
      GUIHelper.showInformationMessage(null, "Error report sent to " + EmailHelper.getSupportEmail());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(null, "Failed to send error report email!", e);
    }
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_PROGRAM;
  }
}