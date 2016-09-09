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
 * SimpleMailer.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.base.BaseRegExp;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.input.EmailFileReader;
import adams.data.io.input.MultiEmailReader;
import adams.data.io.input.PropertiesEmailFileReader;
import adams.env.Environment;

/**
 * Reads emails stored in files and sends them.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleMailer {

  /** the debugging level (0 = off, >0 = on). */
  protected int m_DebugLevel;
  
  /** the email reader to use. */
  protected EmailFileReader m_Reader;
  
  /** the directory to watch. */
  protected PlaceholderDirectory m_WatchDir;
  
  /** the expression to use for listing the files. */
  protected BaseRegExp m_RegExp;
  
  /** for sending the mail. */
  protected AbstractSendEmail m_SendEmail;
  
  /** the suffix for the successfully sent emails. */
  protected String m_SuccessfulSuffix;
  
  /** the suffix for the emails that couldn't be sent. */
  protected String m_FailedSuffix;

  /**
   * Sets the debugging level (0 = off).
   *
   * @param value 	>0 if debugging output should be printed
   */
  public void setDebugLevel(int value) {
    m_DebugLevel = value;
  }

  /**
   * Returns the debugging level (0 = turned off).
   *
   * @return 		true if debugging output is on
   */
  public int getDebugLevel() {
    return m_DebugLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String debugLevelTipText() {
    return "The greater the number the more additional info the scheme may output to the console (0 = off).";
  }

  /**
   * Returns true if debugging output is turned on (any level).
   *
   * @return		true if debugging output is turned on
   */
  protected boolean isDebugOn() {
    return (m_DebugLevel > 0);
  }

  /**
   * Processes the debugging message.
   *
   * @param msg		the debugging message to process
   */
  protected void debug(String msg) {
    debug(msg, 1);
  }

  /**
   * Processes the debugging message.
   *
   * @param level	the debugging level
   * @param msg		the debugging message to process
   */
  protected void debug(String msg, int level) {
    if (level <= m_DebugLevel)
      System.out.println("[DEBUG] " + msg);
  }

  /**
   * Sets the reader for the email files.
   *
   * @param value	the reader
   */
  public void setReader(EmailFileReader value) {
    m_Reader = value;
  }

  /**
   * Returns the reader for the email files.
   *
   * @return		the reader
   */
  public EmailFileReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader for reading the email files.";
  }

  /**
   * Sets the directory to watch.
   *
   * @param value	the directory
   */
  public void setWatchDir(PlaceholderDirectory value) {
    m_WatchDir = value;
  }

  /**
   * Returns the directory to watch.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getWatchDir() {
    return m_WatchDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watchDirTipText() {
    return "The directory to watch.";
  }

  /**
   * Sets the regular expression to match the files against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
  }

  /**
   * Returns the regular expression to match the files against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match the files against.";
  }

  /**
   * Sets the send email scheme to use.
   *
   * @param value	the send email
   */
  public void setSendEmail(AbstractSendEmail value) {
    m_SendEmail = value;
  }

  /**
   * Returns the send email scheme in use.
   *
   * @return		the send email
   */
  public AbstractSendEmail getSendEmail() {
    return m_SendEmail;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sendEmailTipText() {
    return "The send email scheme to use.";
  }

  /**
   * Sets the suffix for successfully sent emails.
   *
   * @param value	the suffix (incl dot)
   */
  public void setSuccessfulSuffix(String value) {
    m_SuccessfulSuffix = value;
  }

  /**
   * Returns the suffix for successfully sent emails.
   *
   * @return		the suffix (incl dot)
   */
  public String getSuccessfulSuffix() {
    return m_SuccessfulSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String successfulSuffixTipText() {
    return "The suffix to use for successfully sent emails.";
  }

  /**
   * Sets the suffix for emails that couldn't get sent.
   *
   * @param value	the suffix (incl dot)
   */
  public void setFailedSuffix(String value) {
    m_FailedSuffix = value;
  }

  /**
   * Returns the suffix for emails that couldn't get sent.
   *
   * @return		the suffix (incl dot)
   */
  public String getFailedSuffix() {
    return m_FailedSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String failedSuffixTipText() {
    return "The suffix to use for emails that couldn't get sent.";
  }

  /**
   * Lists the files in the watch directory.
   * 
   * @return		the files that matched
   */
  protected String[] list() {
    LocalDirectoryLister lister;
    
    lister = new LocalDirectoryLister();
    lister.setDebug(getDebugLevel() > 1);
    lister.setWatchDir(m_WatchDir);
    lister.setListFiles(true);
    lister.setRecursive(false);
    lister.setRegExp(m_RegExp);
    
    return lister.list();
  }
  
  /**
   * Locates and sends the emails.
   */
  public void execute() {
    String[]	files;
    Email	email;
    List<Email>	emails;
    boolean	failed;
    File	from;
    File	to;
    
    // list files
    files = list();
    if (isDebugOn())
      debug("# files found: " + files.length);
    if (files.length == 0) {
      if (isDebugOn())
	debug("Finished!");
      return;
    }
    
    // process files
    for (String file: files) {
      if (isDebugOn())
	debug("Processing: " + file);
      failed = false;
      
      // load emails
      emails = null;
      try {
	m_Reader.setInput(new PlaceholderFile(file));
	if (m_Reader instanceof MultiEmailReader) {
	  emails = ((MultiEmailReader) m_Reader).readAll();
	  if (emails == null)
	    System.err.println("Failed to load emails from file: " + file);
	}
	else {
	  email = m_Reader.read();
	  if (email == null) {
	    System.err.println("Failed to load email from file: " + file);
	  }
	  else {
	    emails = new ArrayList<Email>();
	    emails.add(email);
	  }
	}
      }
      catch (Exception e) {
	System.err.println("Failed to load email file: " + file);
	e.printStackTrace();
	emails = null;
	failed = true;
      }
      
      // send emails
      if (emails != null) {
	for (Email eml: emails) {
	  if (isDebugOn())
	    debug("--> Sending: " + eml);

	  try {
	    m_SendEmail.initializeSmtpSession(
		EmailHelper.getSmtpServer(), 
		EmailHelper.getSmtpPort(), 
		EmailHelper.getSmtpStartTLS(), 
		EmailHelper.getSmtpUseSSL(), 
		EmailHelper.getSmtpTimeout(), 
		EmailHelper.getSmtpRequiresAuthentication(), 
		EmailHelper.getSmtpUser(), 
		EmailHelper.getSmtpPassword());
	    if (!m_SendEmail.sendMail(eml)) {
	      System.err.println("Failed to send email!");
	      if (isDebugOn())
		debug("--> Failed!");
	    }
	    else {
	      if (isDebugOn())
		debug("--> Success!");
	    }
	  }
	  catch (Exception e) {
	    System.err.println("Failed to send email: " + eml);
	    e.printStackTrace();
	    failed = true;
	  }
	}
      }
      
      // rename file
      from = new PlaceholderFile(file);
      if (failed)
	to = new PlaceholderFile(file + m_FailedSuffix);
      else
	to = new PlaceholderFile(file + m_SuccessfulSuffix);
      try {
	if (isDebugOn())
	  debug("--> Renaming '" + from + "' to '" + to + "'");
	FileUtils.move(from, to);
	if (isDebugOn())
	  debug("--> Success!");
      }
      catch (Exception e) {
	System.err.println("Failed to move file from '" + from + "' to '" + to + "'!");
	e.printStackTrace();
	if (isDebugOn())
	  debug("--> Failed!");
      }
    }
    if (isDebugOn())
      debug("Finished!");
  }
  
  /**
   * Runs the mailer from the commandline.
   *
   * @param options	the commandline options
   * @return		the instantiated frame, null in case of an error or
   * 			invocation of help
   */
  public static void runMailer(String[] options) {
    SimpleMailer	simple;
    String		env;
    String		opt;

    try {
      env = OptionUtils.getOption(options, "-env");
      if ((env == null) || (env.length() == 0))
	env = adams.env.Environment.class.getName();
      Environment.setEnvironmentClass(Class.forName(env));
      
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	System.out.println("-env <classname>");
	System.out.println("\tThe environment class to use");
	System.out.println("\tdefault: " + Environment.class.getName());
	System.out.println("-D <level>");
	System.out.println("\tThe debug level (0 means off, >0 means on).");
	System.out.println("-reader <classname>");
	System.out.println("\tThe reader for reading the email files.");
	System.out.println("-watch-dir <directory>");
	System.out.println("\tThe directory to watch.");
	System.out.println("-regexp <expression>");
	System.out.println("\tThe regular expression to match the files against.");
	System.out.println("-send-email <classname+options>");
	System.out.println("\tThe send email scheme to use.");
	System.out.println("-successful-suffix");
	System.out.println("\tThe suffix to use for successfully sent emails.");
	System.out.println("-failed-suffix");
	System.out.println("\tThe suffix to use for emails that couldn't get sent.");
	System.out.println();
      }
      else {
	simple = new SimpleMailer();
	if ((opt = OptionUtils.getOption(options, "-D")) != null)
	  simple.setDebugLevel(Integer.parseInt(opt));
	else
	  simple.setDebugLevel(0);
	if ((opt = OptionUtils.getOption(options, "-reader")) != null)
	  simple.setReader((EmailFileReader) OptionUtils.forCommandLine(EmailFileReader.class, opt));
	else
	  simple.setReader(new PropertiesEmailFileReader());
	if ((opt = OptionUtils.getOption(options, "-watch-dir")) != null)
	  simple.setWatchDir(new PlaceholderDirectory(opt));
	else
	  simple.setWatchDir(new PlaceholderDirectory());
	if ((opt = OptionUtils.getOption(options, "-regexp")) != null)
	  simple.setRegExp(new BaseRegExp(opt));
	else
	  simple.setRegExp(new BaseRegExp(BaseRegExp.MATCH_ALL));
	if ((opt = OptionUtils.getOption(options, "-send-email")) != null)
	  simple.setSendEmail((AbstractSendEmail) OptionUtils.forCommandLine(AbstractSendEmail.class, opt));
	else
	  simple.setSendEmail(new JavaMailSendEmail());
	if ((opt = OptionUtils.getOption(options, "-successful-suffix")) != null)
	  simple.setSuccessfulSuffix(opt);
	else
	  simple.setSuccessfulSuffix(".sent");
	if ((opt = OptionUtils.getOption(options, "-failed-suffix")) != null)
	  simple.setFailedSuffix(opt);
	else
	  simple.setFailedSuffix(".failed");
	simple.execute();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * For running the mailer from command-line. Use "-env classname" to 
   * define the environment class. Default is {@link adams.env.Environment}.
   * 
   * @param args	the command-line arguments, use -h/-help to display them
   */
  public static void main(String[] args) {
    runMailer(args);
  }
}
