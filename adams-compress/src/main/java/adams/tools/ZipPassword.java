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
 * ZipPassword.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.tools;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.env.Environment;
import net.lingala.zip4j.core.ZipFile;

import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Attempts to determine the password of a password protected ZIP file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-zip &lt;adams.core.io.PlaceholderFile&gt; (property: zip)
 * &nbsp;&nbsp;&nbsp;The ZIP file to process.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-dictionary &lt;adams.core.io.PlaceholderFile&gt; (property: dictionary)
 * &nbsp;&nbsp;&nbsp;The dictionary file to process.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-chars &lt;java.lang.String&gt; (property: characters)
 * &nbsp;&nbsp;&nbsp;The characters to use for brute force attack.
 * &nbsp;&nbsp;&nbsp;default: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,;:\'\"-_!&#64;#$%^&amp;*()[]{}
 * </pre>
 * 
 * <pre>-max-length &lt;int&gt; (property: maxLength)
 * &nbsp;&nbsp;&nbsp;The maximum length for password strings when performing brute force attack.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-password &lt;adams.core.io.PlaceholderFile&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The file to store the password in (if one found).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ZipPassword
  extends AbstractTool {

  private static final long serialVersionUID = 3018437869824414157L;

  /** the default characters. */
  public final static String DEFAULT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,;:'\"-_!@#$%^&*()[]{}";

  /** the zip file to use. */
  protected PlaceholderFile m_Zip;

  /** the dictionary file to use. */
  protected PlaceholderFile m_Dictionary;

  /** the characters to use for brute force. */
  protected String m_Characters;

  /** the maximum length for passwords to test. */
  protected int m_MaxLength;

  /** the file to store the determined password in (if successful). */
  protected PlaceholderFile m_Password;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Attempts to determine the password of a password protected ZIP file.\n"
      + "If no dictionary file has been provided, a brute force attack is carried out.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "zip", "zip",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "dictionary", "dictionary",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "chars", "characters",
      DEFAULT_CHARS);

    m_OptionManager.add(
      "max-length", "maxLength",
      10, 1, null);

    m_OptionManager.add(
      "password", "password",
      new PlaceholderFile("."));
  }

  /**
   * Sets the ZIP file to use.
   *
   * @param value	the zip file
   */
  public void setZip(PlaceholderFile value) {
    m_Zip = value;
    reset();
  }

  /**
   * Returns the ZIP file to use.
   *
   * @return 		the zip file
   */
  public PlaceholderFile getZip() {
    return m_Zip;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String zipTipText() {
    return "The ZIP file to process.";
  }

  /**
   * Sets the dictionary file to use.
   *
   * @param value	the dictionary file
   */
  public void setDictionary(PlaceholderFile value) {
    m_Dictionary = value;
    reset();
  }

  /**
   * Returns the dictionary file to use.
   *
   * @return 		the dictionary file
   */
  public PlaceholderFile getDictionary() {
    return m_Dictionary;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String dictionaryTipText() {
    return "The dictionary file to process.";
  }

  /**
   * Sets the characters to use for brute force attack.
   *
   * @param value	the characters
   */
  public void setCharacters(String value) {
    m_Characters = value;
    reset();
  }

  /**
   * Returns the characters to use for brute force attack.
   *
   * @return 		the characters
   */
  public String getCharacters() {
    return m_Characters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String charactersTipText() {
    return "The characters to use for brute force attack.";
  }

  /**
   * Sets the maximum length of password string to generate for brute force
   * attack.
   *
   * @param value	the maximum length
   */
  public void setMaxLength(int value) {
    m_MaxLength = value;
    reset();
  }

  /**
   * Returns the maximum length of password string to generate for brute force
   * attack.
   *
   * @return 		the maximum length
   */
  public int getMaxLength() {
    return m_MaxLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String maxLengthTipText() {
    return "The maximum length for password strings when performing brute force attack.";
  }

  /**
   * Sets the file for outputting the password.
   *
   * @param value	the password file
   */
  public void setPassword(PlaceholderFile value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the file for outputting the password.
   *
   * @return 		the password file
   */
  public PlaceholderFile getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The file to store the password in (if one found).";
  }

  /**
   * Before the actual run is executed. Default implementation only resets the stopped flag.
   */
  @Override
  protected void preRun() {
    super.preRun();

    if (!m_Zip.exists())
      throw new IllegalArgumentException("ZIP file does not exist: " + m_Zip);
    if (m_Zip.isDirectory())
      throw new IllegalArgumentException("ZIP file points to directory: " + m_Zip);
  }

  /**
   * Performs brute force attack.
   *
   * @return		the password or null if unsuccessful
   */
  protected String doRunBruteForce() {
    ZipFile 	zipfile;
    int[] 	counter;
    char[] 	chars;
    int 	max;
    char[] 	pw;
    int 	index;
    int 	numChars;
    int 	count;
    int		i;
    String	tmpDir;

    try {
      zipfile    = new ZipFile(m_Zip.getAbsolutePath());
      if (!zipfile.isEncrypted()) {
        getLogger().warning("ZIP file is not encrypted: " + m_Zip);
	return null;
      }
      tmpDir     = TempUtils.getTempDirectoryStr();
      counter    = new int[m_MaxLength];
      counter[0] = -1;
      chars      = m_Characters.toCharArray();
      max        = chars.length;
      pw         = new char[counter.length];
      numChars   = 1;
      count      = 0;
      while (true) {
	count++;
	index = 0;
	while (index < counter.length) {
	  counter[index]++;
	  if (counter[index] == max) {
	    counter[index] = 0;
	    index++;
	    numChars = Math.max(numChars, index + 1);
	    if (numChars > counter.length)
	      return null;
	  }
	  else {
	    break;
	  }
	}
	for (i = 0; i < numChars; i++) {
	  pw[i] = chars[counter[i]];
	}
	try {
	  zipfile.setPassword(new String(pw).trim());
	  zipfile.extractAll(tmpDir);
	  return new String(pw).trim();
	}
	catch (Exception e) {
	  // ignored
	}
	if (count % 10000 == 0) {
          getLogger().info(new String(pw).trim());
          count = 0;
        }
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error accessing ZIP file: " + m_Zip, e);
    }

    return null;
  }

  /**
   * Uses dictionary for attak.
   *
   * @return		the password or null if unsuccessful
   */
  protected String doRunDictionary() {
    ZipFile 		zipfile;
    List<String> 	passwords;
    int			count;
    String		tmpDir;

    try {
      zipfile   = new ZipFile(m_Zip.getAbsolutePath());
      if (!zipfile.isEncrypted()) {
	getLogger().warning("ZIP file is not encrypted: " + m_Zip);
	return null;
      }
      tmpDir    = TempUtils.getTempDirectoryStr();
      passwords = FileUtils.loadFromFile(m_Dictionary);
      count     = 0;
      for (String pw : passwords) {
	count++;
	try {
	  zipfile.setPassword(pw);
	  zipfile.extractAll(tmpDir);
	  return pw;
	}
	catch (Exception e) {
	  // ignored
	}
	if (count % 10000 == 0) {
          getLogger().info(pw);
          count = 0;
        }
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error accessing ZIP file: " + m_Zip, e);
    }

    return null;
  }

  /**
   * Contains the actual run code.
   */
  @Override
  protected void doRun() {
    String	password;

    if (!m_Dictionary.exists() || m_Dictionary.isDirectory())
      password = doRunBruteForce();
    else
      password = doRunDictionary();

    if (password == null) {
      getLogger().severe("Failed to determine password!");
    }
    else {
      if (m_Password.isDirectory())
	System.out.println(password);
      else
	FileUtils.writeToFile(m_Password.getAbsolutePath(), password, false);
    }
  }

  /**
   * Runs the tool from commandline.
   *
   * @param args	the parameters
   * @throws Exception	if anything goes wrong
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ZipPassword tool = (ZipPassword) forName(ZipPassword.class.getName(), args);
    tool.run();
  }
}
