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
 * SerializableObjectHelper.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.logging.CustomLoggingLevelObject;
import adams.core.option.ArrayProducer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;

import java.util.HashSet;
import java.util.logging.Level;

/**
 * Helper class for serializing an object's member variables.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializableObjectHelper
  extends CustomLoggingLevelObject
  implements Destroyable {

  /** for serialization. */
  private static final long serialVersionUID = -6245176175718570463L;

  /** the owner. */
  protected SerializableObject m_Owner;

  /** whether the setup got loaded or generated already. */
  protected boolean m_SetupLoadedOrGenerated;

  /** whether to enforce strict option checks (ie rebuild if different) or just warn. */
  protected boolean m_Strict;

  /**
   * Initializes the helper.
   *
   * @param owner	the owning object
   */
  public SerializableObjectHelper(SerializableObject owner) {
    super();

    m_Owner                  = owner;
    m_SetupLoadedOrGenerated = false;
    m_Strict                 = false;
  }

  /**
   * Returns the owning object.
   *
   * @return		the owner this helper belongs to
   */
  public SerializableObject getOwner() {
    return m_Owner;
  }

  /**
   * Resets the helper, i.e., it will attempt to load the setup again.
   */
  public void reset() {
    m_SetupLoadedOrGenerated = false;
  }

  /**
   * Sets whether to use strict mode (options HAVE to be the same) or
   * not (simply output warning that options changed).
   *
   * @param value whether options have changed
   */
  public void setStrict(boolean value) {
    m_Strict = value;
    reset();
  }

  /**
   * Returns whether to use strict mode (options HAVE to be the same) or
   * not (simple warning then).
   */
  public boolean isStrict() {
    return m_Strict;
  }

  /**
   * Obtains the command-line for the object.
   * 
   * @param obj		the object to get the command-line for
   * @return		the command-line
   */
  protected String getCommandLine(Object obj) {
    String		result;
    ArrayProducer	producer;
    HashSet<String>	skipped;
    
    if (obj instanceof OptionHandler) {
      skipped = new HashSet<String>();
      skipped.add("serializationFile");
      skipped.add("overrideSerializedFile");
      producer = new ArrayProducer();
      producer.setSkippedProperties(skipped);
      producer.produce((OptionHandler) obj);
      result = producer.toString();
      producer.cleanUp();
    }
    else {
      result = OptionUtils.getCommandLine(obj);
    }
    
    return result;
  }
  
  /**
   * Attempts to load the setup. If the serialization file object is pointing
   * to a directory or the override flag is set in the owner, then the
   * deserialization from file will be skipped and a fresh setup generated
   * instead. After the new setup has been generated, the data is serialized
   * only if the serialization file is to pointing to an actual file.
   *
   * @see	SerializableObject#getSerializationFile()
   * @see 	SerializableObject#getOverrideSerializedFile()
   * @see	#generateSetup()
   * @see	#saveSetup()
   */
  public void loadSetup() {
    Object[]	setup;
    Object[]	fullSetup;
    String	options;
    boolean	generate;

    // can we skip loading the setup?
    if (m_SetupLoadedOrGenerated && !m_Owner.getOverrideSerializedFile())
      return;

    generate = false;

    if (    m_Owner.getOverrideSerializedFile()
	 || m_Owner.getSerializationFile().isDirectory()
	 || !m_Owner.getSerializationFile().exists() ) {
      generate = true;
      if (isLoggingEnabled()) {
	getLogger().log(Level.INFO,
	    "m_Owner.getOverrideSerializedFile()=" + m_Owner.getOverrideSerializedFile() + ", "
	    + "m_Owner.getSerializationFile().isDirectory()=" + m_Owner.getSerializationFile().isDirectory() + ", "
	    + "!m_Owner.getSerializationFile().exists()=" + !m_Owner.getSerializationFile().exists()
	    + " --> generate");
      }
    }
    else {
      fullSetup = null;
      try {
	fullSetup = SerializationHelper.readAll(m_Owner.getSerializationFile().getAbsolutePath());
      }
      catch (Exception e) {
	fullSetup = null;
	getLogger().log(Level.SEVERE, "Error deserializing from '" + m_Owner.getSerializationFile() + "'!", e);
      }

      // do we have to regenerate the setup, since loading failed?
      if (fullSetup == null) {
	generate = true;
      }
      else {
	setup = new Object[fullSetup.length - 1];
	if (fullSetup.length > 1)
	  System.arraycopy(fullSetup, 1, setup, 0, fullSetup.length - 1);
	options = (String) fullSetup[0];
	// options different? -> regenerate setup!
	if (!options.equals(getCommandLine(m_Owner))) {
	  generate = m_Strict;
	  if (isLoggingEnabled()) {
            if (m_Strict)
              getLogger().log(Level.SEVERE, "Options differ --> generate");
            else
              getLogger().log(Level.WARNING, "Options differ");
	  }
	}
	else {
	  m_Owner.setSerializationSetup(setup);
	  m_SetupLoadedOrGenerated = true;
	}
      }
    }

    // generate the setup again, if necessary
    if (generate) {
      generateSetup();
      if (!m_Owner.getSerializationFile().isDirectory()) {
	saveSetup();
	if (isLoggingEnabled()) {
	  getLogger().log(Level.INFO,
	      "Serializing setup: " + m_Owner.getSerializationFile());
	}
      }
      m_SetupLoadedOrGenerated = true;
    }
  }

  /**
   * Saves the owner's member variables to the specified file (ignored if
   * pointing to a directory).
   *
   * @see	SerializableObject#getSerializationFile()
   * @see	SerializableObject#retrieveSerializationSetup()
   */
  public void saveSetup() {
    Object[]	setup;
    Object[]	fullSetup;

    if (m_Owner.getSerializationFile().isDirectory())
      return;

    try {
      // add options (if available) to data that is to be serialized
      setup        = m_Owner.retrieveSerializationSetup();
      fullSetup    = new Object[setup.length + 1];
      fullSetup[0] = getCommandLine(m_Owner);
      if (setup.length > 0)
	System.arraycopy(setup, 0, fullSetup, 1, setup.length);

      // write data to disk
      SerializationHelper.writeAll(m_Owner.getSerializationFile().getAbsolutePath(), fullSetup);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error serializing to '" + m_Owner.getSerializationFile() + "'!", e);
    }
  }

  /**
   * Forces the owner to regenerate all objects that are necessary for
   * serialization.
   */
  public void generateSetup() {
    m_Owner.initSerializationSetup();
  }

  /**
   * Generates some information.
   *
   * @return		the information
   */
  @Override
  public String toString() {
    String	result;

    result =   "Owner=" + m_Owner.getClass().getName() + ", "
             + "File=" + m_Owner.getSerializationFile() + ", "
             + "IsDirectory=" + m_Owner.getSerializationFile().isDirectory() + ", "
             + "Override=" + m_Owner.getOverrideSerializedFile();

    return result;
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    reset();
    m_Owner = null;
  }
}
