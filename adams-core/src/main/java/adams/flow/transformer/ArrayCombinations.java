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
 * ArrayCombinations.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Turns an array of any type into a sequence of array combinations of given size.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: ArrayCombinations
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
 * <pre>-length &lt;int&gt; (property: length)
 * &nbsp;&nbsp;&nbsp;the r in nCr
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-subsets &lt;COMBINATIONS|PERMUTATIONS&gt; (property: subsets)
 * &nbsp;&nbsp;&nbsp;combinations or permutations.
 * &nbsp;&nbsp;&nbsp;default: COMBINATIONS
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayCombinations
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1405432778848290110L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_ELEMENTS = "elements";

  /** number of elements in subarray, */
  protected int m_Length;

  /** the remaining elements of the array that still need to be broadcasted. */
  protected List m_Elements;

  /** the subset type. */
  protected SubsetsType m_Subsets;

  /**
   * Defines whether to do combinations or permutations.
   *
   * @author  dale (dale at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum SubsetsType {
    /** combinations. */
    COMBINATIONS,
    /** permutations. */
    PERMUTATIONS
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an array of any type into a sequence of array combinations of given size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "length", "length",
	    1, 1, null);

    m_OptionManager.add(
	    "subsets", "subsets",
	    SubsetsType.COMBINATIONS);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "subsets", m_Subsets);
  }

  /**
   * Choose combinations or permutations.
   *
   * @param value	the action
   */
  public void setSubsets(SubsetsType value) {
    m_Subsets = value;
    reset();
  }

  /**
   * combinations or permutations.
   *
   * @return		the action
   */
  public SubsetsType getSubsets() {
    return m_Subsets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subsetsTipText() {
    return "combinations or permutations.";
  }

  /**
   * Set length.
   *
   * @param len	 length
   */
  public void setLength(int len){
    m_Length=len;
    reset();
  }

  /**
   * Get length.
   *
   * @return	length
   */
  public int getLength(){
    return(m_Length);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lengthTipText(){
    return("the r in nCr");
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_ELEMENTS);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_ELEMENTS, m_Elements);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_ELEMENTS)) {
      m_Elements = (List) state.get(BACKUP_ELEMENTS);
      state.remove(BACKUP_ELEMENTS);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Elements = new ArrayList();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_Elements.get(0));
    m_Elements.remove(0);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Elements.size() > 0);
  }

  protected Object[] remove(Object[] obj, int pos){
    Object ret[] =new Object[obj.length-1];
    int count=0;
    for (int i=0;i<obj.length;i++){
      if (i != pos){
	ret[count++]=obj[i];
      }
    }
    return(ret);
  }

  protected Object[] removeUpToIncluding(Object[] obj, int pos){
    Object ret[] =new Object[obj.length-(pos+1)];
    int count=0;
    for (int i=0;i<obj.length;i++){
      if (i > pos){
	ret[count++]=obj[i];
      }
    }
    return(ret);
  }

  protected Object[] combine(Object o, Object arr[]){
    Object ret[] =new Object[arr.length+1];
    ret[0]=o;
    for (int i=0;i<arr.length;i++){
      ret[i+1]=arr[i];
    }
    return(ret);
  }

  protected Vector<Object[]> genCombinations(Object[] in, int num){
    Vector<Object[]> vobj=new Vector<Object[]>();
    if (num == 0){
      return(vobj);
    }
    for (int i=0;i<in.length;i++){
      // if unable to complete, continue
      if (in.length < num){
	continue;
      }
      Vector<Object[]> combs;
      if (num == 1){
	Object[] o=new Object[1];
	o[0]=in[i];
	vobj.add(o);
      } else {
	if (m_Subsets == SubsetsType.COMBINATIONS){
	  combs=genCombinations(removeUpToIncluding(in,i),num-1); // combinations
	} else {
	  combs=genCombinations(remove(in,i),num-1); // permutations
	}
	for (Object[] arr:combs){
	  vobj.add(combine(in[i],arr));
	}
      }

    }
    return(vobj);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object[]	array;

    result = null;

    try {
      m_Elements.clear();
      array = (Object[]) m_InputToken.getPayload();
      Vector<Object[]> vobj=genCombinations(array,m_Length);
      for (Object[] arr:vobj){
	m_Elements.add(arr);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to generate combinations:", e);
    }

    return result;
  }
}
