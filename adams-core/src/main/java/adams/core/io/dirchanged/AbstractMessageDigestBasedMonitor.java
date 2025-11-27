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

package adams.core.io.dirchanged;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.io.MessageDigestType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Ancestor for message digest based monitors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMessageDigestBasedMonitor
  extends AbstractDirChangeMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /** the digest type. */
  protected MessageDigestType m_Type;

  /** the digests per file. */
  protected Map<File, String> m_Digests;

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
    m_Digests = null;
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
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");

    return result;
  }

  /**
   * Generates the message digest, if possible.
   *
   * @param file	the file to generate the digest for
   * @param errors	for collecting any errors
   * @return		the digest
   */
  protected abstract String computeDigest(File file, MessageCollection errors);

  /**
   * Performs the actual initialization of the monitor with the specified dir.
   *
   * @param dir		the dir to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doInitialize(File dir) {
    MessageCollection	errors;
    String		digest;

    m_Digests = new HashMap<>();
    for (File file: listFiles(dir)) {
      errors = new MessageCollection();
      digest = computeDigest(file, errors);

      if (digest == null) {
	if (errors.isEmpty())
	  return "Failed to compute digest for: " + file;
	else
	  return errors.toString();
      }
      else {
	m_Digests.put(file, digest);
      }
    }

    return null;
  }

  /**
   * Performs the actual check whether the dir has changed.
   *
   * @param dir		the dir to check
   * @return		true if changed
   */
  @Override
  protected boolean checkChange(File dir) {
    MessageCollection	errors;
    String		digest;

    for (File file: listFiles(dir)) {
      if (!m_Digests.containsKey(file))
	return true;
      digest = null;
      errors = new MessageCollection();
      if (!file.exists())
	errors.add("File does not exist: " + file);
      else
	digest = computeDigest(file, errors);
      if (!errors.isEmpty() || (digest == null))
	return true;
      if (!m_Digests.get(file).equals(digest))
	return true;
    }

    return false;
  }

  /**
   * Performs the actual updating of the monitor with the specified dir.
   *
   * @param dir		the dir to update with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdate(File dir) {
    return doInitialize(dir);
  }
}
