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
 * AbstractWekaModelReader.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;

/**
 * Ancestor for actors that deserialize models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaModelReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2949611378612429555L;

  /** whether to only output the model. */
  protected boolean m_OutputOnlyModel;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-only-model", "outputOnlyModel",
	    false);
  }

  /**
   * Sets whether to output only the model instead of the container.
   *
   * @param value	true if only to output the model
   */
  public void setOutputOnlyModel(boolean value) {
    m_OutputOnlyModel = value;
    reset();
  }

  /**
   * Returns whether only the model is output instead of the container.
   *
   * @return		true if only the model is output
   */
  public boolean getOutputOnlyModel() {
    return m_OutputOnlyModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputOnlyModelTipText() {
    return "If enabled, only the model will be output instead of a model container.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputOnlyModel", m_OutputOnlyModel, "only model");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class that the producer generates.
   *
   * @return		adams.flow.container.WekaModelContainer.class or Object
   */
  public Class[] generates() {
    if (m_OutputOnlyModel)
      return new Class[]{Object.class};
    else
      return new Class[]{WekaModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		file;
    Object[]		data;
    WekaModelContainer	cont;

    result = null;

    if (m_InputToken.getPayload() instanceof File)
      file = (File) m_InputToken.getPayload();
    else
      file = new PlaceholderFile((String) m_InputToken.getPayload());

    try {
      data = SerializationHelper.readAll(file.getAbsolutePath());
      if (m_OutputOnlyModel) {
	if (data.length == 1)
	  m_OutputToken = new Token(data[0]);
	else if (data.length == 2)
	  m_OutputToken = new Token(data[0]);
	else
	  result = "Unexpected number of objects in serialized file (instead of 1 or 2): " + data.length;
      }
      else {
	cont = null;
	if (data.length == 1)
	  cont = new WekaModelContainer(data[0]);
	else if (data.length == 2)
	  cont = new WekaModelContainer(data[0], (Instances) data[1]);
	else
	  result = "Unexpected number of objects in serialized file (instead of 1 or 2): " + data.length;
	if (cont != null)
	  m_OutputToken = new Token(cont);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to deserialize model data from '" + file + "':", e);
    }

    return result;
  }
}
