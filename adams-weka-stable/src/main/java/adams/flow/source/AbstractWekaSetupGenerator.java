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
 * AbstractWekaSetupGenerator.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import weka.core.SetupGenerator;
import weka.core.setupgenerator.AbstractParameter;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 * Abstract ancestor for setup generator sources.
 * Derived classes must defined get/set/tiptext methods for the property
 * "setup".
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of setups to generate
 */
public abstract class AbstractWekaSetupGenerator<T>
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = 673114129476898021L;

  /** whether to output an array or a sequence of setups. */
  protected boolean m_OutputArray;

  /** the underlying setup generator. */
  protected SetupGenerator m_Generator;

  /** all the setups. */
  protected List<T> m_Setups;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "setup", "setup",
	    getDefaultSetup());

    m_OptionManager.add(
	    "parameter", "parameters",
	    getDefaultParameters());

    m_OptionManager.add(
	    "array", "outputArray",
	    false);
  }

  /**
   * Returns the default setup. Used in the options as default value.
   *
   * @return		the default setup
   * @see		#defineOptions()
   */
  protected abstract T getDefaultSetup();

  /**
   * Returns the default parameters. Used in the options as default value.
   *
   * @return		the default parameters
   * @see		#defineOptions()
   */
  protected abstract AbstractParameter[] getDefaultParameters();

  /**
   * Returns the default package of the types of setups to generate.
   *
   * @return		the default package
   * @see		#getQuickInfo()
   * @see		#getDefaultSuperClass()
   */
  protected String getDefaultPackage() {
    String	result;

    result = getDefaultSuperClass().getPackage().getName();
    result = result.substring(0, result.lastIndexOf('.'));

    return result;
  }

  /**
   * Returns the default super class, the same as the type "T" when defining
   * the generics.
   *
   * @return		the default super class
   */
  protected abstract Class getDefaultSuperClass();

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Generator = new SetupGenerator();
    m_Setups    = new ArrayList<T>();
  }

  /**
   * Sets the setup parameters.
   *
   * @param value	the parameters
   */
  public void setParameters(AbstractParameter[] value) {
    m_Generator.setParameters(value);
    reset();
  }

  /**
   * Returns the setup parameters.
   *
   * @return		the parameters
   */
  public AbstractParameter[] getParameters() {
    return m_Generator.getParameters();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String parametersTipText() {
    return "The parameters to use for generating the setups.";
  }

  /**
   * Sets whether to output an array or a sequence of classifier setups.
   *
   * @param value	if true then an array will be output
   */
  public void setOutputArray(boolean value) {
    m_OutputArray = value;
    reset();
  }

  /**
   * Returns whether to output an array or a sequence of classifier setups.
   *
   * @return		true if an array is output
   */
  public boolean getOutputArray() {
    return m_OutputArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputArrayTipText() {
    return
        "If set to true, then an array of setups will be output "
      + "instead of a sequence.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = null;

    if (m_Generator.getBaseObject() != null) {
      result = QuickInfoHelper.toString(this, "setup", m_Generator.getBaseObject().getClass());
      if (m_Generator.getParameters() != null)
	result +=   "/" + m_Generator.getParameters().length
	          + " parameter" + ((m_Generator.getParameters().length == 1) ? "" : "s");
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.classifiers.Classifier.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_OutputArray)
      return new Class[]{Array.newInstance(getDefaultSuperClass(), 0).getClass()};
    else
      return new Class[]{getDefaultSuperClass()};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Enumeration<Serializable>	setups;
    boolean			noSetups;

    result = null;

    m_Setups.clear();
    setups   = m_Generator.setups();
    if (setups != null) {
      noSetups = true;
      while (setups.hasMoreElements()) {
	noSetups = false;
	m_Setups.add((T) setups.nextElement());
      }
      if (noSetups)
	result = "No setups generated!";
    }
    else {
      result = "Error encountered in setup generation!";
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    if (m_OutputArray) {
      result = new Token(m_Setups.toArray((T[]) Array.newInstance(getDefaultSuperClass(), m_Setups.size())));
      m_Setups.clear();
    }
    else {
      result = new Token(m_Setups.get(0));
      m_Setups.remove(0);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Setups.size() > 0);
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_Setups.clear();

    super.wrapUp();
  }
}
