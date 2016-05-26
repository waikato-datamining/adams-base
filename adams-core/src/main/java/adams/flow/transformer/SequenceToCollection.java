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
 * SequenceToCollection.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns a sequence of tokens into a collection.<br>
 * In case of unspecified length (ie -1), a collection containing all elements collected so far is output each time a token arrives, i.e., the internal buffer never gets reset.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.ArrayList<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SequenceToCollection
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-size &lt;int&gt; (property: collectionSize)
 * &nbsp;&nbsp;&nbsp;The size of the collection(s).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-overlap &lt;int&gt; (property: overlap)
 * &nbsp;&nbsp;&nbsp;The overlap of elements between collections; e.g., sequence of 1,2,3,4 with 
 * &nbsp;&nbsp;&nbsp;length=2 and overlap=0 gets packaged in to (1,2),(3,4); with overlap=1, 
 * &nbsp;&nbsp;&nbsp;this changes to (1,2),(2,3),(3,4); collection size option must be &gt; 0.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-collection-class &lt;java.lang.String&gt; (property: collectionClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the collection.
 * &nbsp;&nbsp;&nbsp;default: java.util.ArrayList
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequenceToCollection
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1363005679574784724L;

  /** the key for storing the current elements in the backup. */
  public final static String BACKUP_ELEMENTS = "elements";

  /** the buffered elements of the collection that still need to be broadcasted. */
  protected List m_Elements;

  /** the length of the collections. */
  protected int m_CollectionSize;

  /** the overlap in elements between collections. */
  protected int m_Overlap;

  /** the class for the collection. */
  protected String m_CollectionClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns a sequence of tokens into a collection.\n"
        + "In case of unspecified length (ie -1), a collection containing all "
        + "elements collected so far is output each time a token arrives, "
        + "i.e., the internal buffer never gets reset.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "size", "collectionSize",
      1, -1, null);

    m_OptionManager.add(
      "overlap", "overlap",
      0, 0, null);

    m_OptionManager.add(
      "collection-class", "collectionClass",
      ArrayList.class.getName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "collectionSize", m_CollectionSize, "size: ");
    result += QuickInfoHelper.toString(this, "overlap", m_Overlap, ", overlap: ");
    result += QuickInfoHelper.toString(this, "collectionClass", (m_CollectionClass.length() != 0 ? m_CollectionClass : "-none-"), ", class: ");

    return result;
  }

  /**
   * Sets the size of the collection.
   *
   * @param value	the size
   */
  public void setCollectionSize(int value) {
    if (getOptionManager().isValid("collectionSize", value)) {
      m_CollectionSize = value;
      reset();
    }
  }

  /**
   * Returns the length of the collection.
   *
   * @return		the size
   */
  public int getCollectionSize() {
    return m_CollectionSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String collectionSizeTipText() {
    return "The size of the collection(s).";
  }

  /**
   * Sets the overlap of elements between collections.
   *
   * @param value	the overlap
   */
  public void setOverlap(int value) {
    if (getOptionManager().isValid("overlap", value)) {
      m_Overlap = value;
      reset();
    }
  }

  /**
   * Returns the overlap of elements between collections.
   *
   * @return		the overlap
   */
  public int getOverlap() {
    return m_Overlap;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapTipText() {
    return
      "The overlap of elements between collections; e.g., sequence of 1,2,3,4 with "
	+ "length=2 and overlap=0 gets packaged in to (1,2),(3,4); with overlap=1, "
	+ "this changes to (1,2),(2,3),(3,4); collection size option must be > 0.";
  }

  /**
   * Sets the class for the collection.
   *
   * @param value	the classname, use empty string to use class of first
   * 			element
   */
  public void setCollectionClass(String value) {
    if (value.length() > 0) {
      m_CollectionClass = value;
      reset();
    }
    else {
      getLogger().severe("Class cannot be empty!");
    }
  }

  /**
   * Returns the class for the collection.
   *
   * @return		the classname
   */
  public String getCollectionClass() {
    return m_CollectionClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String collectionClassTipText() {
    return "The class to use for the collection.";
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

    m_Elements    = new ArrayList();
    m_OutputToken = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.util.ArrayList.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    try {
      return new Class[]{Class.forName(m_CollectionClass)};
    }
    catch (Exception e) {
      return new Class[]{Collection.class};
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Collection	coll;
    int		diff;

    result = null;

    diff = m_Elements.size();
    if (m_CollectionSize > -1) {
      diff = m_CollectionSize - m_Overlap;
      if (diff <= 0)
	result = "Overlap must be smaller than collection size: overlap=" + m_Overlap + " >= collectionSize=" + m_CollectionSize;
    }

    if (result == null) {
      try {
	m_Elements.add(m_InputToken.getPayload());
	if (isLoggingEnabled())
	  getLogger().info("Buffered elements: " + m_Elements.size());
	if ((m_CollectionSize == -1) || (m_Elements.size() == m_CollectionSize)) {
	  coll = (Collection) Class.forName(m_CollectionClass).newInstance();
	  if (isLoggingEnabled())
	    getLogger().info("Collection type: " + coll.getClass().getComponentType());
	  coll.addAll(m_Elements);
	  m_OutputToken = new Token(coll);
	  if (m_CollectionSize > -1) {
	    while (diff > 0) {
	      m_Elements.remove(0);
	      diff--;
	    }
	  }
	  if (isLoggingEnabled())
	    getLogger().info("Collection generated");
	}
      }
      catch (Exception e) {
	result = handleException("Failed to turn sequence into collection: ", e);
      }
    }

    return result;
  }
}
