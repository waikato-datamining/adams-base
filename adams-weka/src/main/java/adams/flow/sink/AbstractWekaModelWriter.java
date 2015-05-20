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
 * AbstractModelWriter.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.Arrays;
import java.util.Vector;

import adams.core.SerializationHelper;
import adams.flow.container.WekaModelContainer;

/**
 * Ancestor for actors that serialize models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaModelWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -259222073894194923L;

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The filename to save the model (and optional header) in.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.container.WekaModelContainer.class + additional classes
   * @see		#getAdditionalAcceptedClasses()
   */
  public Class[] accepts() {
    Vector<Class>	result;

    result = new Vector<Class>();
    result.add(WekaModelContainer.class);
    result.addAll(Arrays.asList(getAdditionalAcceptedClasses()));

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns additional classes that are accepted as input.
   * <br><br>
   * Default implementation returns a zero-length array.
   *
   * @return		the additional classes
   */
  protected Class[] getAdditionalAcceptedClasses() {
    return new Class[0];
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    WekaModelContainer	cont;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof WekaModelContainer) {
	cont = (WekaModelContainer) m_InputToken.getPayload();
	if (cont.hasValue(WekaModelContainer.VALUE_HEADER)) {
	  SerializationHelper.writeAll(
	      m_OutputFile.getAbsolutePath(), 
	      new Object[]{
		cont.getValue(WekaModelContainer.VALUE_MODEL),
		cont.getValue(WekaModelContainer.VALUE_HEADER)
	      });
	}
	else {
	  SerializationHelper.write(
	      m_OutputFile.getAbsolutePath(), 
	      cont.getValue(WekaModelContainer.VALUE_MODEL));
	}
      }
      else {
	SerializationHelper.write(
	    m_OutputFile.getAbsolutePath(), 
	    m_InputToken.getPayload());
      }
    }
    catch (Exception e) {
      result = handleException("Failed to serialize model data to '" + m_OutputFile + "':", e);
    }

    return result;
  }
}
