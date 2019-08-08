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
 * WekaFilter.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.io.ModelFileHandler;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.flow.container.OptionalContainerOutput;
import adams.flow.container.WekaFilterContainer;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.AbstractModelLoader.ModelLoadingType;
import adams.flow.core.CallableActorReference;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.ModelLoaderSupporter;
import adams.flow.core.OptionalOneTimeInitializer;
import adams.flow.core.Token;
import adams.flow.core.WekaFilterModelLoader;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Filters Instances&#47;Instance objects using the specified filter.<br>
 * <br>
 * The following order is used to obtain the model (when using AUTO):<br>
 * 1. model file present?<br>
 * 2. source actor present?<br>
 * 3. storage item present?<br>
 * 4. use specified filter definition
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaFilter
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-property &lt;adams.core.base.BaseString&gt; [-property ...] (property: properties)
 * &nbsp;&nbsp;&nbsp;The properties to update with the values associated with the specified values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; [-variable ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The names of the variables to update the properties with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-filter &lt;weka.filters.Filter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to use for filtering the Instances&#47;Instance objects.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.AllFilter
 * </pre>
 *
 * <pre>-model-loading-type &lt;AUTO|FILE|SOURCE_ACTOR|STORAGE&gt; (property: modelLoadingType)
 * &nbsp;&nbsp;&nbsp;Determines how to load the model, in case of AUTO, first the model file
 * &nbsp;&nbsp;&nbsp;is checked, then the callable actor and then the storage.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The file to load the model from, ignored if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-source &lt;adams.flow.core.CallableActorReference&gt; (property: modelActor)
 * &nbsp;&nbsp;&nbsp;The callable actor (source) to obtain the model from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage &lt;adams.flow.control.StorageName&gt; (property: modelStorage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the model from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-init-once &lt;boolean&gt; (property: initializeOnce)
 * &nbsp;&nbsp;&nbsp;If set to true, then the filter will get initialized only with the first
 * &nbsp;&nbsp;&nbsp;batch of data; otherwise every time data gets passed through; only applies
 * &nbsp;&nbsp;&nbsp;when using the filter definition, the others (model file, source, storage
 * &nbsp;&nbsp;&nbsp;) assume the filter to be built.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-keep &lt;boolean&gt; (property: keepRelationName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the filter won't change the relation name of the incoming
 * &nbsp;&nbsp;&nbsp;dataset.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-container &lt;boolean&gt; (property: outputContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, a adams.flow.container.WekaFilterContainer is output with the
 * &nbsp;&nbsp;&nbsp;filter and the filtered data (Instance or Instances).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaFilter
  extends AbstractTransformerWithPropertiesUpdating
  implements OptionalContainerOutput, ModelFileHandler,
             StorageUser, ModelLoaderSupporter, OptionalOneTimeInitializer {

  /** for serialization. */
  private static final long serialVersionUID = 9078845385089445202L;

  /** the key for storing the current initialized state in the backup. */
  public final static String BACKUP_INITIALIZED = "initialized";

  /** the filter to apply. */
  protected weka.filters.Filter m_Filter;

  /** the actual filter used. */
  protected weka.filters.Filter m_ActualFilter;

  /** whether to initialize filter only with the first batch. */
  protected boolean m_InitializeOnce;

  /** whether to keep the incoming relation name. */
  protected boolean m_KeepRelationName;

  /** whether to output a container. */
  protected boolean m_OutputContainer;

  /** whether the filter has been initialized. */
  protected boolean m_Initialized;

  /** whether the flow context has been updated. */
  protected boolean m_FlowContextUpdated;

  /** the model loader. */
  protected WekaFilterModelLoader m_ModelLoader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Filters Instances/Instance objects using the specified filter.\n"
	+ "When re-using a trained filter, ensure that 'initializeOnce' is checked."
	+ "\n\n"
	+ m_ModelLoader.automaticOrderInfo();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new AllFilter());

    m_OptionManager.add(
      "model-loading-type", "modelLoadingType",
      ModelLoadingType.AUTO);

    m_OptionManager.add(
      "model", "modelFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "source", "modelActor",
      new CallableActorReference());

    m_OptionManager.add(
      "storage", "modelStorage",
      new StorageName());

    m_OptionManager.add(
      "init-once", "initializeOnce",
      false);

    m_OptionManager.add(
      "keep", "keepRelationName",
      false);

    m_OptionManager.add(
      "output-container", "outputContainer",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelLoader = new WekaFilterModelLoader();
    m_ModelLoader.setFlowContext(this);
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_ModelLoader.setLoggingLevel(value);
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(weka.filters.Filter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public weka.filters.Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the Instances/Instance objects.";
  }

  /**
   * Sets the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setModelLoadingType(ModelLoadingType value) {
    m_ModelLoader.setModelLoadingType(value);
    reset();
  }

  /**
   * Returns the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public ModelLoadingType getModelLoadingType() {
    return m_ModelLoader.getModelLoadingType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelLoadingTypeTipText() {
    return m_ModelLoader.modelLoadingTypeTipText();
  }

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelLoader.setModelFile(value);
    reset();
  }

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelLoader.getModelFile();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return m_ModelLoader.modelFileTipText();
  }

  /**
   * Sets the filter source actor.
   *
   * @param value	the source
   */
  public void setModelActor(CallableActorReference value) {
    m_ModelLoader.setModelActor(value);
    reset();
  }

  /**
   * Returns the filter source actor.
   *
   * @return		the source
   */
  public CallableActorReference getModelActor() {
    return m_ModelLoader.getModelActor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelActorTipText() {
    return m_ModelLoader.modelActorTipText();
  }

  /**
   * Sets the filter storage item.
   *
   * @param value	the storage item
   */
  public void setModelStorage(StorageName value) {
    m_ModelLoader.setModelStorage(value);
    reset();
  }

  /**
   * Returns the filter storage item.
   *
   * @return		the storage item
   */
  public StorageName getModelStorage() {
    return m_ModelLoader.getModelStorage();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelStorageTipText() {
    return m_ModelLoader.modelStorageTipText();
  }

  /**
   * Sets whether the filter gets initialized only with the first batch.
   *
   * @param value	true if the filter gets only initialized once
   */
  public void setInitializeOnce(boolean value) {
    m_InitializeOnce = value;
    reset();
  }

  /**
   * Returns whether the filter gets initialized only with the first batch.
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
      "If set to true, then the filter will get initialized only with the "
	+ "first batch of data; otherwise every time data gets passed through; "
	+ "only applies when using the filter definition, the others (model file, "
	+ "source, storage) assume the filter to be built.";
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
   * Sets whether to output a container with the filter alongside the
   * filtered data or just the filtered data.
   *
   * @param value 	true if to output the container
   */
  public void setOutputContainer(boolean value) {
    m_OutputContainer = value;
    reset();
  }

  /**
   * Returns whether to output a container with the filter alongside the
   * filtered data or just the filtered data.
   *
   * @return 		true if to output the container
   */
  public boolean getOutputContainer() {
    return m_OutputContainer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputContainerTipText() {
    return
      "If enabled, a " + WekaFilterContainer.class.getName()
	+ " is output with the filter and the filtered data (Instance or Instances).";
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    // unfortunately, can't tell whether we're really using it
    return !getSkip();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] generates() {
    if (m_OutputContainer)
      return new Class[]{WekaFilterContainer.class};
    else
      return new Class[]{weka.core.Instance.class, weka.core.Instances.class, adams.data.instance.Instance.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	info;

    result  = QuickInfoHelper.toString(this, "filter", Shortening.shortenEnd(OptionUtils.getShortCommandLine(m_Filter), 40));
    result += QuickInfoHelper.toString(this, "modelLoadingType", getModelLoadingType(), ", type: ");
    result += QuickInfoHelper.toString(this, "modelFile", getModelFile(), ", model: ");
    result += QuickInfoHelper.toString(this, "modelSource", getModelActor(), ", source: ");
    result += QuickInfoHelper.toString(this, "modelStorage", getModelStorage(), ", storage: ");
    result += QuickInfoHelper.toString(this, "keepRelationName", m_KeepRelationName, "keep relation name", ", ");
    result += QuickInfoHelper.toString(this, "outputContainer", m_OutputContainer, "output container", ", ");
    info    = super.getQuickInfo();
    if (!info.isEmpty())
      result += ", " + info;

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_INITIALIZED);
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

    result.put(BACKUP_INITIALIZED, m_Initialized);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INITIALIZED)) {
      m_Initialized = (Boolean) state.get(BACKUP_INITIALIZED);
      state.remove(BACKUP_INITIALIZED);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized        = false;
    m_FlowContextUpdated = false;
    m_ModelLoader.reset();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] accepts() {
    return new Class[]{weka.core.Instance.class, weka.core.Instances.class, adams.data.instance.Instance.class};
  }

  /**
   * Creates a token with the data. If required creates a container with the
   * filter.
   *
   * @param data	the data to output in the token
   * @return		the generated token
   */
  protected Token createToken(Object input, Object data) {
    WekaFilterContainer		cont;

    if (m_OutputContainer) {
      if (data instanceof Instances)
	cont = new WekaFilterContainer(m_ActualFilter, (Instances) data);
      else if (data instanceof Instance)
	cont = new WekaFilterContainer(m_ActualFilter, (Instance) data);
      else if (data instanceof adams.data.instance.Instance)
	cont = new WekaFilterContainer(m_ActualFilter, (adams.data.instance.Instance) data);
      else
	throw new IllegalArgumentException("Unhandled data type: " + data.getClass().getName());
      cont.setValue(WekaFilterContainer.VALUE_INPUT, input);
      return new Token(cont);
    }
    else {
      return new Token(data);
    }
  }

  /**
   * Initializes the actual filter to use.
   *
   * @param data	the data to initialize the filter when using the filter definition
   * @return		null if successful, otherwise error message
   * @throws Exception	if initialization, copying etc fails
   */
  protected String initActualFilter(Instances data) throws Exception {
    String		result;
    MessageCollection	errors;
    boolean		initFilter;

    errors         = new MessageCollection();
    initFilter     = false;
    m_ActualFilter = m_ModelLoader.getModel(errors);
    if (m_ActualFilter == null) {
      initFilter = true;
      if (getModelLoadingType() == ModelLoadingType.AUTO)
	m_ActualFilter = (Filter) OptionUtils.shallowCopy(m_Filter);
      else
	return errors.toString();
    }

    // configure containers
    result = setUpContainers(m_ActualFilter);
    if (result == null)
      result = updateObject(m_ActualFilter);

    if (!m_InitializeOnce || initFilter)
      m_ActualFilter.setInputFormat(data);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    weka.core.Instances			data;
    weka.core.Instances			filteredData;
    weka.core.Instance			inst;
    adams.data.instance.Instance	instA;
    weka.core.Instance			filteredInst;
    String				relation;

    result = null;

    data = null;
    inst = null;
    if (m_InputToken.hasPayload(weka.core.Instance.class))
      inst = m_InputToken.getPayload(weka.core.Instance.class);
    else if (m_InputToken.hasPayload(adams.data.instance.Instance.class))
      inst = m_InputToken.getPayload(adams.data.instance.Instance.class).toInstance();
    else if (m_InputToken.hasPayload(weka.core.Instances.class))
      data = m_InputToken.getPayload(weka.core.Instances.class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      try {
        // initialize filter?
        if (!m_Initialized || !m_InitializeOnce) {
          if (data == null) {
            data = new weka.core.Instances(inst.dataset(), 0);
            data.add(inst);
          }
          result = initActualFilter(data);
        }
      }
      catch (Exception e) {
        result = handleException("Failed to initialize filter!", e);
      }
    }

    if (result == null) {
      try {
	synchronized(m_ActualFilter) {
          if (!m_FlowContextUpdated) {
            m_FlowContextUpdated = true;
            if (m_ActualFilter instanceof FlowContextHandler)
              ((FlowContextHandler) m_ActualFilter).setFlowContext(this);
          }

          // filter data
          filteredData = null;
          filteredInst = null;
          if (data != null) {
            relation = data.relationName();
            filteredData = weka.filters.Filter.useFilter(data, m_ActualFilter);
            if (m_KeepRelationName) {
              filteredData.setRelationName(relation);
              if (isLoggingEnabled())
                getLogger().info("Setting relation name: " + relation);
            }
            m_Initialized = true;
          }
          else {
            relation = inst.dataset().relationName();
            m_ActualFilter.input(inst);
            m_ActualFilter.batchFinished();
            filteredInst = m_ActualFilter.output();
            if (m_KeepRelationName) {
              filteredInst.dataset().setRelationName(relation);
              if (isLoggingEnabled())
                getLogger().info("Setting relation name: " + relation);
            }
          }
        }

	// build output token
	if (inst != null) {
	  if (filteredInst != null) {
	    if (m_InputToken.getPayload() instanceof weka.core.Instance) {
	      m_OutputToken = new Token(filteredInst);
	    }
	    else {
	      instA = new adams.data.instance.Instance();
	      instA.set(filteredInst);
	      m_OutputToken = createToken(m_InputToken.getPayload(), instA);
	    }
	  }
	  else if ((filteredData != null) && (filteredData.numInstances() > 0)) {
	    m_OutputToken = createToken(m_InputToken.getPayload(), filteredData.instance(0));
	  }
	}
	else {
	  m_OutputToken = createToken(m_InputToken.getPayload(), filteredData);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to filter data: ", e);
      }
    }

    return result;
  }
}
