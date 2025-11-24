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
 * SerializableObjectReader.java
 * Copyright (C) 2017-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.LenientModeSupporter;
import adams.core.SerializableObject;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.classmanager.ClassManager;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.AbstractObjectWriter;
import adams.data.io.output.SerializableObjectWriter;

import java.util.logging.Level;

/**
 * Loads a {@link adams.core.SerializableObject}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SerializableObjectReader
  extends AbstractObjectReader
  implements LenientModeSupporter {

  private static final long serialVersionUID = -5427726959059688884L;

  /** whether to be lenient. */
  protected boolean m_Lenient;

  /**
   * Default constructor.
   */
  public SerializableObjectReader() {
    super();
  }

  /**
   * Initializes the reader with specified lenient mode.
   *
   * @param lenient 	whether to be lenient or not
   */
  public SerializableObjectReader(boolean lenient) {
    this();
    setLenient(lenient);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads a file containing data from a " + SerializableObject.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lenient", "lenient",
      false);
  }

  /**
   * Sets whether lenient, ie first tries to load the object as {@link SerializableObject}
   * and if that fails just deserializes it.
   *
   * @param value	true if lenient
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether lenient, ie first tries to load the object as {@link SerializableObject}
   * and if that fails just deserializes it.
   *
   * @return		true if lenient
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return "If enabled, first tries to load the object as "
	     + Utils.classToString(SerializableObject.class) +
	     " and if that fails just deserializes it.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Serializable Object";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"model", "model.gz", "ser", "ser.gz"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public AbstractObjectWriter getCorrespondingWriter() {
    return new SerializableObjectWriter();
  }

  /**
   * Performs the actual reading of the object file.
   *
   * @param file	the file to read
   * @return		the object, null if failed to read
   */
  @Override
  protected Object doRead(PlaceholderFile file) {
    Object		obj;
    Object[]		all;
    String 		cname;
    Object[]		data;
    SerializableObject 	result;

    try {
      obj = SerializationHelper.read(file.getAbsolutePath());
      if (obj instanceof Object[]) {
	all = (Object[]) obj;
	if (all.length == 2) {
	  cname = (String) all[0];
	  data  = (Object[]) all[1];
	  result = (SerializableObject) ClassManager.getSingleton().forName(cname).getDeclaredConstructor().newInstance();
	  result.setSerializationSetup(data);
	  result.setSetupLoadedOrGenerated(true);
	  return result;
	}
      }
      else if (m_Lenient) {
	return obj;
      }
      getLogger().severe("Expected to read array of length 2 (classname string and data Object array), instead found: " + Utils.classToString(obj));
      return null;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read object from: " + file, e);
      return null;
    }
  }
}
