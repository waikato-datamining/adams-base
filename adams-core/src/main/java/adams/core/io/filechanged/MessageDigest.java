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
 * MessageDigest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filechanged;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.io.MessageDigestType;

import java.io.File;

/**
 * Generates a message digest and uses that for comparison.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MessageDigest
  extends AbstractFileChangeMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /** the digest type. */
  protected MessageDigestType m_Type;

  /** the digest. */
  protected String m_Digest;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a message digest and uses that for comparison.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      MessageDigestType.SHA256);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    m_Digest = null;
  }

  /**
   * Sets the type of digest to use.
   *
   * @param value	the type
   */
  public void setType(MessageDigestType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of digest to use.
   *
   * @return		the type
   */
  public MessageDigestType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of message digest (algorithm) to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type, "type: ");
  }

  /**
   * Performs the actual initialization of the monitor with the specified file.
   *
   * @param file	the file to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doInitialize(File file) {
    MessageCollection	errors;
    String		digest;

    errors = new MessageCollection();
    digest = m_Type.digest(file, errors);

    if (digest == null) {
      if (errors.isEmpty())
        return "Failed to compute digest!";
      else
	return errors.toString();
    }
    else {
      m_Digest = digest;
      return null;
    }
  }

  /**
   * Performs the actual check whether the file has changed.
   *
   * @param file	the file to check
   * @return		true if changed
   */
  @Override
  protected boolean checkChange(File file) {
    MessageCollection	errors;
    String		digest;

    errors = new MessageCollection();
    digest = m_Type.digest(file, errors);

    return errors.isEmpty() && (digest != null) && !m_Digest.equals(digest);
  }

  /**
   * Performs the actual updating of the monitor with the specified file.
   *
   * @param file	the file to update with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdate(File file) {
    return doInitialize(file);
  }
}
