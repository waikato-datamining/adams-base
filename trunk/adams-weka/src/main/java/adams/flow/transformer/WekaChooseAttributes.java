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
 * WekaChooseAttributes.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.option.OptionUtils;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.dialog.ApprovalDialog;

/**
 <!-- globalinfo-start -->
 * Lets the user select attributes interactively to use down the track.<br/>
 * Internally, a weka.filters.unsupervised.attribute.Remove WEKA filter is constructed from the selection, to remove the attributes that the user didn't select.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaChooseAttributes
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
 * <pre>-stop-if-canceled (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-pre-selection &lt;adams.core.base.BaseRegExp&gt; (property: preSelection)
 * &nbsp;&nbsp;&nbsp;The regular expression to use for pre-selecting attributes.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-non-interactive (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, attributes that match the 'pre-selection' pattern get selected 
 * &nbsp;&nbsp;&nbsp;automatically.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaChooseAttributes
  extends AbstractInteractiveTransformer
  implements AutomatableInteractiveActor, ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -1483735876005865608L;

  /** the regular expression for pre-selecting attributes by name. */
  protected BaseRegExp m_PreSelection;
  
  /** whether to automate the actor. */
  protected boolean m_NonInteractive;
  
  /** the Remove filter in use. */
  protected Remove m_Remove;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Lets the user select attributes interactively to use down the track.\n"
	+ "Internally, a " + Remove.class.getName() + " WEKA filter is constructed "
	+ "from the selection, to remove the attributes that the user didn't select.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "pre-selection", "preSelection",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "non-interactive", "nonInteractive",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Remove = new Remove();
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

    result = QuickInfoHelper.toString(this, "preSelection", m_PreSelection);

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow if canceled"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "nonInteractive", m_NonInteractive, "non-interactive"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the regular expression to pre-select attributes for the dialog.
   *
   * @param value	the expression
   */
  public void setPreSelection(BaseRegExp value) {
    m_PreSelection = value;
    reset();
  }

  /**
   * Returns the regular expression to pre-select attributes for the dialog.
   *
   * @return 		the expression
   */
  public BaseRegExp isPreSelection() {
    return m_PreSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String preSelectionTipText() {
    return "The regular expression to use for pre-selecting attributes.";
  }

  /**
   * Sets whether to enable/disable interactiveness.
   *
   * @param value	if true actor is not interactive, but automated
   */
  public void setNonInteractive(boolean value) {
    m_NonInteractive = value;
    reset();
  }

  /**
   * Returns whether interactiveness is enabled/disabled.
   *
   * @return 		true if actor is not interactive i.e., automated
   */
  public boolean isNonInteractive() {
    return m_NonInteractive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String nonInteractiveTipText() {
    return "If enabled, attributes that match the 'pre-selection' pattern get selected automatically.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Prompts the user to select attributes.
   * 
   * @param inst	the data to present
   * @param preSelected	the indices of the attributes to use by default
   * @return		the list of selected attributes to keep, null if cancelled
   */
  protected List<Integer> selectAttributes(Instances inst, List<Integer> preSelected) {
    ArrayList<Integer>	result;
    DefaultTableModel	model;
    BaseTable		table;
    String[][]		names;
    int			i;
    int			n;
    ApprovalDialog	dialog;
    JPanel		panelAll;
    JPanel		panelOptions;
    JCheckBox		checkBoxInvert;
    Range		range;
    int[][]		segments;
    int			numAtts;
    
    result = new ArrayList<Integer>();
    
    numAtts = inst.numAttributes();
    if (inst.classIndex() > -1)
      numAtts--;
    names = new String[numAtts][1];
    n = 0;
    for (i = 0; i < inst.numAttributes(); i++) {
      if (inst.classIndex() == i)
	continue;
      names[n][0] = inst.attribute(i).name();
      n++;
    }
    model = new DefaultTableModel(names, new String[]{"Attribute"});

    range = new Range();
    range.setMax(numAtts);
    range.setIndices(Utils.toIntArray(preSelected));
    segments = range.getIntSegments();
    table = new BaseTable(model);
    table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    for (int[] segment: segments)
      table.getSelectionModel().addSelectionInterval(segment[0], segment[1]);
    
    panelAll = new JPanel(new BorderLayout());
    panelAll.add(new BaseScrollPane(table), BorderLayout.CENTER);
    panelOptions = new JPanel(new FlowLayout(FlowLayout.LEFT));
    checkBoxInvert = new JCheckBox("Remove selected attributes rather than keep them");
    panelOptions.add(checkBoxInvert);
    panelAll.add(panelOptions, BorderLayout.SOUTH);
    dialog = new ApprovalDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Choose attributes to use");
    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(getParentComponent());
    dialog.setVisible(true);
    
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;
    
    if (checkBoxInvert.isSelected()) {
      range.setIndices(table.getSelectedRows());
      range.setInverted(true);
      result.addAll(Utils.toList(range.getIntIndices()));
    }
    else {
      result.addAll(Utils.toList(table.getSelectedRows()));
    }
    
    return result;
  }
  
  /**
   * Returns the pre-selected indices.
   * 
   * @param inst	the dataset to work on
   * @return		the indices
   */
  protected List<Integer> getPreSelectedIndices(Instances inst) {
    List<Integer>	result;
    int			i;

    result = new ArrayList<Integer>();
    for (i = 0; i < inst.numAttributes(); i++) {
      if (inst.classIndex() == i)
	continue;
      if (m_PreSelection.isMatch(inst.attribute(i).name()))
	result.add(i);
    }
    
    return result;
  }
  
  /**
   * Filters the data.
   * 
   * @param inst	the data to filter
   * @param selected	the selected attributes
   * @return		the new dataset
   * @throws Exception	in case filtering fails
   */
  protected Instances filter(Instances inst, List<Integer> selected) throws Exception {
    Instances	result;
    
    m_Remove.setInvertSelection(true);
    m_Remove.setAttributeIndicesArray(Utils.toIntArray(selected));
    m_Remove.setInputFormat(inst);
    result = Filter.useFilter(inst, m_Remove);
    
    return result;
  }
  
  /**
   * Creates the output token with the subset of data.
   * 
   * @param inst	the instances to process
   * @param selected	the indices to select
   * @return		true if successfully generated
   */
  protected boolean generateOutput(Instances inst, List<Integer> selected) {
    boolean	result;

    try {
      m_OutputToken = new Token(filter(inst, selected));
      updateProvenance(m_OutputToken);
      result = true;
    }
    catch (Exception e) {
      result = false;
      handleException("Failed to remove attributes: " + OptionUtils.getCommandLine(m_Remove), e);
    }

    return result;
  }
  
  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    boolean		result;
    List<Integer>	selected;
    Instances		inst;

    result = true;
    inst   = (Instances) m_InputToken.getPayload();

    // determine pre-selection
    selected = getPreSelectedIndices(inst);

    // interact with user
    if (!m_NonInteractive) {
      selected = selectAttributes(inst, selected);
      result   = (selected != null);
    }
    
    if (result)
      result = generateOutput(inst, selected);
    
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
    Instances	inst;
    
    result = null;
    
    if (m_Headless) {
      inst = (Instances) m_InputToken.getPayload();
      if (!generateOutput(inst, getPreSelectedIndices(inst)))
	result = "Failed to generate subset!";
    }
    else {
      result = super.doExecute();
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
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), OptionUtils.getOptions(m_Remove), m_OutputToken.getPayload().getClass()));
    }
  }
}
