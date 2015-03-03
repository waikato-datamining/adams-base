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
 * AbstractInstanceGenerator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Instance;
import adams.core.QuickInfoHelper;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.flow.core.Token;

/**
 * Ancestor for transformers that turn data containers into WEKA Instance
 * objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data container to process
 */
public abstract class AbstractInstanceGenerator<T extends DataContainer>
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 9173099269238100664L;

  /** the generator to use. */
  protected adams.data.instances.AbstractInstanceGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    getDefaultGenerator());
  }

  /**
   * Returns the default generator.
   *
   * @return		the generator
   */
  protected abstract adams.data.instances.AbstractInstanceGenerator getDefaultGenerator();

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(adams.data.instances.AbstractInstanceGenerator value){
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator in use.
   *
   * @return		the generator
   */
  public adams.data.instances.AbstractInstanceGenerator getGenerator(){
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String generatorTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted data container
   */
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		weka.core.Instance.class
   */
  public Class[] generates() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns the database connection to use.
   *
   * @return		the database connection
   */
  protected abstract AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Generator.setDatabaseConnection(getDatabaseConnection());
      result = m_Generator.checkSetup();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    T		cont;
    Instance	inst;

    result        = null;
    cont          = (T) m_InputToken.getPayload();
    inst          = m_Generator.generate(cont);
    m_OutputToken = new Token(inst);

    return result;
  }
}
