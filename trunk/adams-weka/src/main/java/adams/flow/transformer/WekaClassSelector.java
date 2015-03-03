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
 * WekaClassSelector.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Sets the class index. Can either honour an already existing one or override it. Also, one can apply the index on a subset of attributes defined by a regular expression applied to the attribute names (one can set the class index to the last attribute that starts with 'att_').
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaClassSelector
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-class &lt;adams.core.Index&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The index within the attribute subset defined by the regular expression 
 * &nbsp;&nbsp;&nbsp;('first' and 'last' are accepted as well).
 * &nbsp;&nbsp;&nbsp;default: last
 * </pre>
 * 
 * <pre>-override (property: override)
 * &nbsp;&nbsp;&nbsp;If set to true, then any existing class index will be overriden; otherwise 
 * &nbsp;&nbsp;&nbsp;the class index will only be set if not already set.
 * </pre>
 * 
 * <pre>-regex &lt;adams.core.base.BaseRegExp&gt; (property: regexName)
 * &nbsp;&nbsp;&nbsp;The regular expression used for selecting the subset of attributes.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-unset (property: unset)
 * &nbsp;&nbsp;&nbsp;Unsets the class attribute.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassSelector
  extends AbstractWekaInstanceAndWekaInstancesTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the class index. */
  protected Index m_ClassIndex;

  /** whether to override any set class attribute.  */
  protected boolean m_Override;

  /** the regular expression on the attribute for selecting the sub-set of attributes. */
  protected BaseRegExp m_RegexName;
  
  /** whether to unset the class index. */
  protected boolean m_Unset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Sets the class index. Can either honour an already existing one or "
      + "override it. Also, one can apply the index on a subset of attributes "
      + "defined by a regular expression applied to the attribute names (one "
      + "can set the class index to the last attribute that starts with 'att_').";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ClassIndex = new Index();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "class", "classIndex",
	    new Index(Index.LAST));

    m_OptionManager.add(
	    "override", "override",
	    false);

    m_OptionManager.add(
	    "regex", "regexName",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "unset", "unset",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (m_Unset || QuickInfoHelper.hasVariable(this, "unset")) {
      result  = QuickInfoHelper.toString(this, "unset", m_Unset, "unset");
    }
    else {
      result  = QuickInfoHelper.toString(this, "classIndex", m_ClassIndex);
      result += QuickInfoHelper.toString(this, "override", m_Override, "override", ", ");
      result += QuickInfoHelper.toString(this, "regexName", m_RegexName, ", name: ");
    }

    return result;
  }

  /**
   * Sets the class index.
   *
   * @param value	the index
   */
  public void setClassIndex(Index value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the class index.
   *
   * @return		the index
   */
  public Index getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return
        "The index within the attribute subset defined by the regular "
      + "expression ('first' and 'last' are accepted as well).";
  }

  /**
   * Sets whether to override any existing class index or nor.
   *
   * @param value	if true then any class index will be override
   */
  public void setOverride(boolean value) {
    m_Override = value;
    reset();
  }

  /**
   * Returns whether any existing class index will be overriden or not.
   *
   * @return		true if any class index will be overriden
   */
  public boolean getOverride() {
    return m_Override;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overrideTipText() {
    return
        "If set to true, then any existing class index will be overriden; "
      + "otherwise the class index will only be set if not already set.";
  }

  /**
   * Sets the regular expression for selecting the attributes.
   *
   * @param value	the regex
   */
  public void setRegexName(BaseRegExp value) {
    m_RegexName = value;
    reset();
  }

  /**
   * Returns the regular expression for selecting the attributes.
   *
   * @return		the regex
   */
  public BaseRegExp getRegexName() {
    return m_RegexName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regexNameTipText() {
    return "The regular expression used for selecting the subset of attributes.";
  }

  /**
   * Sets whether to unset the class attribute.
   *
   * @param value	true if to unset
   */
  public void setUnset(boolean value) {
    m_Unset = value;
    reset();
  }

  /**
   * Returns whether to unset the class attribute.
   *
   * @return		true if to unset
   */
  public boolean getUnset() {
    return m_Unset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String unsetTipText() {
    return "Unsets the class attribute.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			o;
    weka.core.Instances		inst;
    boolean			isInstances;
    List<Attribute>		atts;
    int				i;

    result = null;

    // dataset
    o           = m_InputToken.getPayload();
    isInstances = false;
    inst        = null;
    if (o instanceof weka.core.Instances) {
      inst        = (weka.core.Instances) o;
      inst        = new weka.core.Instances(inst);
      isInstances = true;
    }
    else if (o instanceof adams.data.instance.Instance) {
      inst = ((adams.data.instance.Instance) o).getDatasetHeader();
    }
    else if (o instanceof weka.core.Instance) {
      inst = ((weka.core.Instance) o).dataset();
    }
    else {
      result = "Cannot handle object of type " + o.getClass().getName() + "!";
    }

    if (result == null) {
      if (m_Unset) {
	inst.setClassIndex(-1);
      }
      else {
	// determine the attributes that fit the regular expression
	atts = new ArrayList<Attribute>();
	for (i = 0; i < inst.numAttributes(); i++) {
	  if (m_RegexName.isEmpty() || m_RegexName.isMatch(inst.attribute(i).name()))
	    atts.add(inst.attribute(i));
	}

	// class index
	m_ClassIndex.setMax(atts.size());
	if (m_Override || (inst.classIndex() == -1))
	  inst.setClassIndex(atts.get(m_ClassIndex.getIntIndex()).index());
      }

      // output
      if (isInstances)
	m_OutputToken = new Token(inst);
      else
	m_OutputToken = new Token(o);

      updateProvenance(m_OutputToken);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
