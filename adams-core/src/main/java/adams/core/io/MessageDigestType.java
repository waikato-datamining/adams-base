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
 * MessageDigestType.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import adams.core.EnumWithCustomDisplay;
import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.option.AbstractOption;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;

/**
 * Enumeration of available message digest algorithms.
 * <br><br>
 * See <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms"
 * target="_blank">here</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public enum MessageDigestType
  implements EnumWithCustomDisplay<MessageDigestType> {

  /** MD2. */
  MD2("MD2"),
  /** MD5. */
  MD5("MD5"),
  /** SHA-1. */
  SHA1("SHA-1"),
  /** SHA-256. */
  SHA256("SHA-256"),
  /** SHA-256. */
  SHA384("SHA-384"),
  /** SHA-256. */
  SHA512("SHA-512");

  /** the algorithm name. */
  private String m_Algorithm;

  /** the raw enum string. */
  private String m_Raw;

  /**
   * Initializes the type.
   *
   * @param algorithm	the display string
   */
  private MessageDigestType(String algorithm) {
    m_Algorithm = algorithm;
    m_Raw       = super.toString();
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toDisplay() {
    return m_Algorithm;
  }

  /**
   * Returns the raw enum string.
   *
   * @return		the raw enum string
   */
  public String toRaw() {
    return m_Raw;
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  @Override
  public String toString() {
    return m_Algorithm;
  }

  /**
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public MessageDigestType parse(String s) {
    return (MessageDigestType) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((MessageDigestType) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str	the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static MessageDigestType valueOf(AbstractOption option, String str) {
    MessageDigestType result;

    result = null;

    // default parsing
    try {
      result = valueOf(str);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (MessageDigestType dt: values()) {
	if (dt.toDisplay().equals(str)) {
	  result = dt;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Generates the digest.
   *
   * @param obj		the string, string array, file or file array
   * @param errors		for collecting any errors
   * @return			the digest if successful, otherwise null
   */
  public String digest(Object obj, MessageCollection errors) {
    StringBuilder 			result;
    String 				msg;
    java.security.MessageDigest	md;
    byte[]				digest;
    DigestInputStream stream;
    FileInputStream fis;
    File file;
    byte[]				buffer;

    result = new StringBuilder();
    msg    = null;
    fis    = null;
    stream = null;
    try {
      md = java.security.MessageDigest.getInstance(toDisplay());
      if (obj instanceof String) {
	md.update(((String) obj).getBytes());
      }
      else if (obj instanceof String[]) {
	for (String input : (String[]) obj)
	  md.update(input.toString().getBytes());
      }
      else if (obj instanceof File) {
	file   = (File) obj;
	fis    = new FileInputStream(file.getAbsolutePath());
	stream = new DigestInputStream(new BufferedInputStream(fis), md);
	buffer = new byte[1024];
	while (stream.read(buffer) != -1);
      }
      else if (obj instanceof File[]) {
	for (File f: (File[]) obj) {
	  fis    = new FileInputStream(f.getAbsolutePath());
	  stream = new DigestInputStream(new BufferedInputStream(fis), md);
	  buffer = new byte[1024];
	  while (stream.read(buffer) != -1) ;
	  FileUtils.closeQuietly(stream);
	  FileUtils.closeQuietly(fis);
	}
      }
      else {
	msg = "Unhandled input: " + Utils.classToString(obj);
      }
      if (msg == null) {
	digest = md.digest();
	for (byte b : digest)
	  result.append(Utils.toHex(b));
      }
    }
    catch (Exception e) {
      msg = "Failed to generate digest:\n" + Utils.throwableToString(e);
    }
    finally {
      FileUtils.closeQuietly(stream);
      FileUtils.closeQuietly(fis);
    }

    if (msg != null) {
      errors.add(msg);
      return null;
    }
    else {
      return result.toString();
    }
  }
}
