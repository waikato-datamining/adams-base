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
 * SerializableObject.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.io.Serializable;

import adams.core.io.PlaceholderFile;

/**
 * Interface for classes that handle their own serialization/deserialization.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SerializableObject
  extends Serializable {

  /**
   * Sets the file to serialize to.
   * 
   * @param value	the file
   */
  public void setSerializationFile(PlaceholderFile value);
  
  /**
   * Returns the current file to serialize to.
   * 
   * @return		the file
   */
  public PlaceholderFile getSerializationFile();
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String serializationFileTipText();

  /**
   * Sets whether to override an existing serialized setup.
   * 
   * @param value	true if to override existing setup
   */
  public void setOverrideSerializedFile(boolean value);
  
  /**
   * Returns whether to override an existing serialized setup.
   * 
   * @return		true if existing file is ignored
   */
  public boolean getOverrideSerializedFile();
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overrideSerializedFileTipText();
  
  /**
   * Regenerates all the objects that are necessary for serialization.
   */
  public void initSerializationSetup();
  
  /**
   * Returns the member variables to serialize to a file.
   * 
   * @return		the objects to serialize
   */
  public Object[] retrieveSerializationSetup();
  
  /**
   * Updates the member variables with the provided objects obtained from
   * deserialization.
   * 
   * @param value	the deserialized objects
   */
  public void setSerializationSetup(Object[] value);
}
