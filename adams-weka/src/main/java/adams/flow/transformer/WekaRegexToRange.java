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
 * WekaRegexToRange.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
<!-- globalinfo-start -->
* Produces a range string from a regular expression describing attributes.
* <br><br>
<!-- globalinfo-end -->
*
<!-- flow-summary-start -->
* Input/output:<br>
* - accepts:<br>
* &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
* &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
* - generates:<br>
* &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
* &nbsp;&nbsp;&nbsp;default: RegexToRange
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
* <pre>-regex &lt;java.lang.String&gt; (property: regex)
* &nbsp;&nbsp;&nbsp;The regular expression for attribute matching.
* &nbsp;&nbsp;&nbsp;default: .*
* </pre>
*
* <pre>-invert (property: invert)
* &nbsp;&nbsp;&nbsp;invert matching sense of regular expression.
* </pre>
*
<!-- options-end -->
*
* @author  dale (dale at waikato dot ac dot nz)
* @version $Revision$
*/
public class WekaRegexToRange
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2556622944506847666L;

  /** regular expression used to determine attribute list. */
  protected String m_Regex;

  /** invert matching? */
  protected boolean m_Invert;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regex", "regex",
	    ".*");

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regex", m_Regex, (m_Invert ? "! " : ""));
  }

  /**
   * Invert match?
   * @param value  invert?
   */
  public void setInvert(boolean value){
    m_Invert=value;
  }

  /**
   * Get invert match?
   * @return	invert the match?
   */
  public boolean getInvert(){
    return(m_Invert);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return
        "invert matching sense of regular expression.";
  }

  /**
   * Sets the regular expression for attribute matching.
   *
   * @param value	the regular expression.
   */
  public void setRegex(String value) {
    m_Regex = value;
    reset();
  }

  /**
   * Returns the regular expression for attribute matching.
   *
   * @return		the regular expression for attribute matching.
   */
  public String getRegex() {
    return m_Regex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regexTipText() {
    return
        "The regular expression for attribute matching.";
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Produces a range string from a regular expression describing attributes.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class, weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class, Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Return match, given invert status.
   *
   * @param input	string to match
   * @return		matches? Given invert status.
   */
  protected boolean match(String input){
    boolean ret=input.matches(m_Regex);
    if (m_Invert) {
      return(!ret);
    }
    return(ret);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 	result;
    String	range;
    Instances	inst;

    result = null;
    range = "";

    if (m_InputToken.getPayload() instanceof Instances)
      inst = (Instances) m_InputToken.getPayload();
    else
      inst = ((Instance) m_InputToken.getPayload()).dataset();

    int firstInRange=Integer.MIN_VALUE;
    int lastInRange=Integer.MIN_VALUE;
    int last=Integer.MIN_VALUE;

    for (int i=0;i<inst.numAttributes();i++){
      if (match(inst.attribute(i).name())){
	if (i == last+1){
	  lastInRange=i;
	} else {
	  if (firstInRange != Integer.MIN_VALUE){
	    if (!range.equals("")){
	      range+=",";
	    }
	    if (firstInRange - lastInRange == 0){
	      range+=""+(firstInRange+1);
	    } else  {
	      range+=""+(firstInRange+1)+"-"+(lastInRange+1);
	    }
	  }

	  firstInRange=i;
	  lastInRange=i;
	}
	last=i;
      }
    }
    if (!range.equals("")){
      range+=",";
    }
    if (firstInRange < 0){
      range="";
    } else if (lastInRange < 0 || lastInRange == firstInRange){
      range+=""+(firstInRange+1);
    } else {
      range+=""+(firstInRange+1)+"-"+(lastInRange+1);
    }

    m_OutputToken = new Token(range);

    return result;
  }
}
