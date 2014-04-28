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

/**
 * WekaReorderAttributesToReference.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Reorder;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallabledActorHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Reorders the attributes of the Instance&#47;Instances passing through according to the provided reference dataset (callable actor or reference file).<br/>
 * This ensures that the generated data always has the same structure as the reference dataset.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaReorderAttributesToReference
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-reference-file &lt;adams.core.io.PlaceholderFile&gt; (property: referenceFile)
 * &nbsp;&nbsp;&nbsp;The reference dataset to load (when not pointing to a directory).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-use-custom &lt;boolean&gt; (property: useCustomLoader)
 * &nbsp;&nbsp;&nbsp;If set to true, then the custom loader will be used for loading the data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-loader &lt;weka.core.converters.AbstractFileLoader&gt; (property: customLoader)
 * &nbsp;&nbsp;&nbsp;The custom loader to use if enabled.
 * &nbsp;&nbsp;&nbsp;default: weka.core.converters.ArffLoader
 * </pre>
 * 
 * <pre>-reference-actor &lt;adams.flow.core.CallableActorReference&gt; (property: referenceActor)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the reference dataset in case reference 
 * &nbsp;&nbsp;&nbsp;file points to a directory.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-on-the-fly &lt;boolean&gt; (property: onTheFly)
 * &nbsp;&nbsp;&nbsp;If set to true, the reference file is not required to be present at set 
 * &nbsp;&nbsp;&nbsp;up time (eg if built on the fly), only at execution time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-init-once &lt;boolean&gt; (property: initializeOnce)
 * &nbsp;&nbsp;&nbsp;If set to true, then the internal reorder filter will get initialized only 
 * &nbsp;&nbsp;&nbsp;with the first batch of data; otherwise every time data gets passed through.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If set to true, attributes from the reference data that are missing in the 
 * &nbsp;&nbsp;&nbsp;incoming data get tolerated.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-keep &lt;boolean&gt; (property: keepRelationName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the filter won't change the relation name of the incoming 
 * &nbsp;&nbsp;&nbsp;dataset.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaReorderAttributesToReference
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 530323409335629567L;

  /** the key for storing the reference dataset in the backup. */
  public final static String BACKUP_REFERENCE = "reference";

  /** the key for storing the reorder filter in the backup. */
  public final static String BACKUP_REORDER = "reorder";

  /** the reference dataset to load. */
  protected PlaceholderFile m_ReferenceFile;
  
  /** whether to use a custom converter. */
  protected boolean m_UseCustomLoader;

  /** the custom loader. */
  protected AbstractFileLoader m_CustomLoader;

  /** the callable actor to get the reference data from. */
  protected CallableActorReference m_ReferenceActor;

  /** the reference dataset. */
  protected Instances m_Reference;

  /** whether the dataset gets generated on the fly and might not be available at setUp time. */
  protected boolean m_OnTheFly;

  /** whether to initialize filter only with the first batch. */
  protected boolean m_InitializeOnce;

  /** whether to tolerate attributes that are not present in the incoming data. */
  protected boolean m_Lenient;

  /** whether to keep the incoming relation name. */
  protected boolean m_KeepRelationName;

  /** the reorder filter to use. */
  protected MultiFilter m_Reorder;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	  "Reorders the attributes of the Instance/Instances passing through "
	+ "according to the provided reference dataset (callable actor or reference file).\n"
	+ "This ensures that the generated data always has the same structure "
	+ "as the reference dataset.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reference-file", "referenceFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "use-custom", "useCustomLoader",
	    false);

    m_OptionManager.add(
	    "loader", "customLoader",
	    new ArffLoader());

    m_OptionManager.add(
	    "reference-actor", "referenceActor",
	    new CallableActorReference("unknown"));

    m_OptionManager.add(
	    "on-the-fly", "onTheFly",
	    false);

    m_OptionManager.add(
	    "init-once", "initializeOnce",
	    false);

    m_OptionManager.add(
	    "lenient", "lenient",
	    false);

    m_OptionManager.add(
	    "keep", "keepRelationName",
	    false);
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Reference = null;
    m_Reorder   = null;
  }
  
  /**
   * Sets the file to load the reference dataset from.
   *
   * @param value	the reference file
   */
  public void setReferenceFile(PlaceholderFile value) {
    m_ReferenceFile = value;
    reset();
  }

  /**
   * Returns the file to load the reference dataset from.
   *
   * @return		the reference file
   */
  public PlaceholderFile getReferenceFile() {
    return m_ReferenceFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String referenceFileTipText() {
    return "The reference dataset to load (when not pointing to a directory).";
  }

  /**
   * Sets whether to use a custom loader or not.
   *
   * @param value	if true then the custom loader will be used
   */
  public void setUseCustomLoader(boolean value) {
    m_UseCustomLoader = value;
    reset();
  }

  /**
   * Returns whether a custom loader is used or not.
   *
   * @return		true if a custom loader is used
   */
  public boolean getUseCustomLoader() {
    return m_UseCustomLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomLoaderTipText() {
    return "If set to true, then the custom loader will be used for loading the data.";
  }

  /**
   * Sets the custom loader to use.
   *
   * @param value	the custom loader
   */
  public void setCustomLoader(AbstractFileLoader value) {
    m_CustomLoader = value;
    reset();
  }

  /**
   * Returns the custom loader in use.
   *
   * @return		the custom loader
   */
  public AbstractFileLoader getCustomLoader() {
    return m_CustomLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customLoaderTipText() {
    return "The custom loader to use if enabled.";
  }

  /**
   * Sets the callable actor to obtain the reference dataset from if reference file is pointing
   * to a directory.
   *
   * @param value	the actor reference
   */
  public void setReferenceActor(CallableActorReference value) {
    m_ReferenceActor = value;
    reset();
  }

  /**
   * Returns the callable actor to obtain the reference dataset from if reference file is pointing
   * to a directory.
   *
   * @return		the actor reference
   */
  public CallableActorReference getReferenceActor() {
    return m_ReferenceActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String referenceActorTipText() {
    return
        "The callable actor to use for obtaining the reference dataset in case "
      + "reference file points to a directory.";
  }

  /**
   * Sets whether the reference file gets built on the fly and might not be present
   * at start up time.
   *
   * @param value	if true then the reference does not have to be present at
   * 			start up time
   */
  public void setOnTheFly(boolean value) {
    m_OnTheFly = value;
    reset();
  }

  /**
   * Returns whether the reference file gets built on the fly and might not be present
   * at start up time.
   *
   * @return		true if the reference is not necessarily present at start
   * 			up time
   */
  public boolean getOnTheFly() {
    return m_OnTheFly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onTheFlyTipText() {
    return
        "If set to true, the reference file is not required to be present at "
      + "set up time (eg if built on the fly), only at execution time.";
  }

  /**
   * Sets whether the internal reorder filter gets initialized only with the first batch.
   *
   * @param value	true if the filter gets only initialized once
   */
  public void setInitializeOnce(boolean value) {
    m_InitializeOnce = value;
    reset();
  }

  /**
   * Returns whether the internal reorder filter gets initialized only with the first batch.
   *
   * @return		true if the filter gets only initialized once
   */
  public boolean getInitializeOnce() {
    return m_InitializeOnce;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initializeOnceTipText() {
    return
        "If set to true, then the internal reorder filter will get initialized only with the "
      + "first batch of data; otherwise every time data gets passed through.";
  }

  /**
   * Sets whether to tolerate attributes that are missing in the incoming data.
   *
   * @param value	true if to tolerate unknown attributes
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to tolerate attributes that are missing in the incoming data.
   *
   * @return		true if to tolerate unknown attributes
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return
        "If set to true, attributes from the reference data that are missing "
	+ "in the incoming data get tolerated.";
  }

  /**
   * Sets whether the filter doesn't change the relation name.
   *
   * @param value	true if the filter won't change the relation name
   */
  public void setKeepRelationName(boolean value) {
    m_KeepRelationName = value;
    reset();
  }

  /**
   * Returns whether the filter doesn't change the relation name.
   *
   * @return		true if the filter doesn't change the relation name
   */
  public boolean getKeepRelationName() {
    return m_KeepRelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepRelationNameTipText() {
    return
        "If set to true, then the filter won't change the relation name of the "
      + "incoming dataset.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "referenceFile", m_ReferenceFile, "file: ");
    result += QuickInfoHelper.toString(this, "referenceActor", m_ReferenceActor, ", actor: ");

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useCustomLoader", getUseCustomLoader(), m_CustomLoader.getClass().getSimpleName()));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "onTheFly", m_OnTheFly, "on-the-fly"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "initializeOnce", m_InitializeOnce, "once"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "lenient", m_Lenient, "lenient"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "keepRelationName", m_KeepRelationName, "keep"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instance.class, Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Instance.class, Instances.class};
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_REFERENCE);
    pruneBackup(BACKUP_REORDER);
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

    if (m_Reference != null)
      result.put(BACKUP_REFERENCE, m_Reference);
    if (m_Reorder != null)
      result.put(BACKUP_REORDER, m_Reorder);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_REFERENCE)) {
      m_Reference = (Instances) state.get(BACKUP_REFERENCE);
      state.remove(BACKUP_REFERENCE);
    }
    if (state.containsKey(BACKUP_REORDER)) {
      m_Reorder = (MultiFilter) state.get(BACKUP_REORDER);
      state.remove(BACKUP_REORDER);
    }

    super.restoreState(state);
  }

  /**
   * Loads the reference data.
   *
   * @return		null if everything worked, otherwise an error message
   */
  protected String setUpReference() {
    String		result;
    AbstractFileLoader	loader;
    DataSource		source;

    result = null;

    if (m_ReferenceFile.isDirectory()) {
      // obtain reference from callable actor
      try {
	m_Reference = (Instances) CallabledActorHelper.getSetupFromSource(null, m_ReferenceActor, this);
      }
      catch (Exception e) {
	m_Reference = null;
	result      = handleException("Failed to obtain reference from callable actor '" + m_ReferenceActor + "': ", e);
      }
    }
    else {
      // load reference
      try {
	if (m_UseCustomLoader) {
	  loader = m_CustomLoader;
	  loader.setFile(m_ReferenceFile.getAbsoluteFile());
	  source = new DataSource(loader);
	}
	else {
	  source = new DataSource(m_ReferenceFile.getAbsolutePath());
	}
	m_Reference = source.getDataSet();
	if (m_Reference == null)
	  result = "Failed to load reference dataset from '" + m_ReferenceFile + "'!";
      }
      catch (Exception e) {
	m_Reference = null;
	result      = handleException("Failed to load reference dataset from '" + m_ReferenceFile + "': ", e);
      }
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (!m_OnTheFly)
	result = setUpReference();
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
    String		result;
    Instances		dataOld;
    Instance		instOld;
    Instances		dataNew;
    Instance		instNew;
    Attribute		att;
    int			i;
    StringBuilder	order;
    List<Add>		adds;
    Add			add;
    int			index;
    StringBuilder	labels;
    int			n;
    List<Filter>	filters;
    Reorder		reorder;
    
    result = null;

    if (m_OnTheFly && (m_Reference == null)) {
      result = setUpReference();
      if (result != null)
	return result;
    }

    dataNew = null;
    instNew = null;
    
    // get input data
    if (m_InputToken.getPayload() instanceof Instance) {
      instOld = (Instance) m_InputToken.getPayload();
      dataOld = instOld.dataset();
    }
    else {
      instOld = null;
      dataOld = (Instances) m_InputToken.getPayload();
    }
    
    // do we need to initialize filter?
    if (m_InitializeOnce || (m_Reorder == null)) {
      // check incoming data
      if (!m_Lenient) {
	for (i = 0; i < m_Reference.numAttributes(); i++) {
	  att = m_Reference.attribute(i);
	  if (dataOld.attribute(att.name()) == null) {
	    if (result == null)
	      result = "Missing attribute(s) in incoming data: " + att.name();
	    else
	      result += ", " + att.name();
	  }
	}
	if (result != null)
	  getLogger().severe(result);
      }
      
      if (result == null) {
	try {
	  // determine indices
	  order = new StringBuilder();
	  adds  = new ArrayList<Add>();
	  for (i = 0; i < m_Reference.numAttributes(); i++) {
	    att = m_Reference.attribute(i);
	    if (dataOld.attribute(att.name()) == null) {
	      index = dataOld.numAttributes() + adds.size();
	      add = new Add();
	      add.setAttributeIndex("last");
	      add.setAttributeName(att.name());
	      add.setAttributeType(new SelectedTag(att.type(), Add.TAGS_TYPE));
	      if (att.isNominal()) {
		labels = new StringBuilder();
		for (n = 0; n < att.numValues(); n++) {
		  if (labels.length() > 0)
		    labels.append(",");
		  labels.append(att.value(n));
		}
		add.setNominalLabels(labels.toString());
	      }
	      adds.add(add);
	    }
	    else {
	      index = dataOld.attribute(att.name()).index();
	    }
	    if (order.length() > 0)
	      order.append(",");
	    order.append((index + 1));
	  }

	  // build reorder filter
	  reorder = new Reorder();
	  reorder.setAttributeIndices(order.toString());

	  // build multifilter
	  filters = new ArrayList<Filter>();
	  filters.addAll(adds);
	  filters.add(reorder);
	  m_Reorder = new MultiFilter();
	  m_Reorder.setFilters(filters.toArray(new Filter[filters.size()]));
	  
	  // initialize filter
	  m_Reorder.setInputFormat(dataOld);
	}
	catch (Exception e) {
	  result = handleException("Failed to initialize reorder filter!", e);
	}
      }
    }
    
    // reorder data
    if (result == null) {
      try {
	if (instOld != null) {
	  m_Reorder.input(instOld);
	  m_Reorder.batchFinished();
	  instNew = m_Reorder.output();
	  if (m_KeepRelationName)
	    instNew.dataset().setRelationName(dataOld.relationName());
	}
	else {
	  dataNew = Filter.useFilter(dataOld, m_Reorder);
	  if (m_KeepRelationName)
	    dataNew.setRelationName(dataOld.relationName());
	}
      }
      catch (Exception e) {
	result  = handleException("Failed to reorder data!", e);
	instNew = null;
	dataNew = null;
      }
    }
    
    if (instNew != null)
      m_OutputToken = new Token(instNew);
    else if (dataNew != null)
      m_OutputToken = new Token(dataNew);
    
    return result;
  }
}
