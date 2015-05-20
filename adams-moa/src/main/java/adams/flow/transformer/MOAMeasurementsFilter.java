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
 * MOAMeasurementsFilter.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import moa.core.Measurement;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Filters the measures based on the measurement name. The matching can be inverted as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;moa.core.Measurement[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;moa.core.Measurement[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MOAMeasurementsFilter
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
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to use for filtering the measurements.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-invert (property: invertMatching)
 * &nbsp;&nbsp;&nbsp;Whether to invert the matching sense of the regular expression.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAMeasurementsFilter
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8877674188038780612L;

  /** the regexp to use on the measurement names. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_InvertMatching;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Filters the measures based on the measurement name. The matching "
      + "can be inverted as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invertMatching",
	    false);
  }

  /**
   * Sets the regular expression to use for filtering.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use for filtering.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to use for filtering the measurements.";
  }

  /**
   * Sets whether to invert the matching sense of the regular expression.
   *
   * @param value	if true the matching is inverted
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether the matching sense of the regular expression is inverted.
   *
   * @return		true if the matching is inverted
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertMatchingTipText() {
    return "Whether to invert the matching sense of the regular expression.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExp", m_RegExp, (m_InvertMatching ? "! " : ""));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->moa.core.Measurement[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Measurement[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->moa.core.Measurement[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Measurement[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Measurement[]	measurements;
    List<Measurement>	filtered;
    int			i;

    result = null;

    measurements = (Measurement[]) m_InputToken.getPayload();
    filtered     = new ArrayList<Measurement>();
    for (i = 0; i < measurements.length; i++) {
      if (m_InvertMatching) {
	if (!m_RegExp.isMatch(measurements[i].getName()))
	  filtered.add(measurements[i]);
      }
      else {
	if (m_RegExp.isMatch(measurements[i].getName()))
	  filtered.add(measurements[i]);
      }
    }

    if (filtered.size() > 0) {
      m_OutputToken = new Token(filtered.toArray(new Measurement[filtered.size()]));
      if (m_InputToken.hasProvenance())
	m_OutputToken.setProvenance(m_InputToken.getProvenance().getClone());
    }

    return result;
  }
}
