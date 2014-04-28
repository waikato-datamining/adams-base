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
 * WekaSelectObjects.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import weka.gui.GenericArrayEditor;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.gui.dialog.ApprovalDialog;

/**
 <!-- globalinfo-start -->
 * Allows the user to select an arbitrary number of Weka objects from the specified class hierarchy using the GenericObjectArray.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaSelectObjects
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
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the objects get output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-super-class &lt;java.lang.String&gt; (property: superClass)
 * &nbsp;&nbsp;&nbsp;The superclass for the class hierarchy.
 * &nbsp;&nbsp;&nbsp;default: java.lang.Object
 * </pre>
 * 
 * <pre>-initial-objects &lt;adams.core.base.BaseString&gt; [-initial-objects ...] (property: initialObjects)
 * &nbsp;&nbsp;&nbsp;The initial objects to populate the dialog with.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial objects are forwarded without user interaction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaSelectObjects
  extends AbstractSelectObjects {

  /** for serialization. */
  private static final long serialVersionUID = 8941008707409028350L;

  /** the dialog for selecting the objects. */
  protected ApprovalDialog m_Dialog;

  /** the dialog for selecting the objects. */
  protected GenericArrayEditor m_ArrayEditor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Allows the user to select an arbitrary number of Weka objects from the "
	+ "specified class hierarchy using the GenericObjectArray.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the objects get output as array rather than one-by-one.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    try {
      return Class.forName(m_SuperClass);
    }
    catch (Exception e) {
      return Object.class;
    }
  }

  /**
   * Turns a commandline into an object.
   * 
   * @param cmdline	the commandline to convert
   * @return		the generated object, null if failed to convert
   */
  @Override
  protected Object commandlineToObject(String cmdline) {
    try {
      return OptionUtils.forAnyCommandLine(getItemClass(), cmdline);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to convert commandline: " + cmdline, e);
      return null;
    }
  }

  /**
   * Initializes the interactive dialog with the 
   */
  @Override
  protected void initializeDialog() {
    List 	current;
    Object	obj;
    Object	array;
    int		i;
    
    if (m_Dialog == null) {
      m_Dialog = new ApprovalDialog(null, ModalityType.DOCUMENT_MODAL);
      if (m_ShortTitle)
	m_Dialog.setTitle(getName());
      else
	m_Dialog.setTitle(getFullName());
      m_ArrayEditor = new GenericArrayEditor();
      m_Dialog.getContentPane().add(m_ArrayEditor.getCustomEditor(), BorderLayout.CENTER);
      m_Dialog.setSize(600, 400);
      m_Dialog.setLocationRelativeTo(null);
    }

    current = new ArrayList();
    for (BaseString initial: m_InitialObjects) {
      obj = commandlineToObject(initial.getValue());
      if (obj == null)
	getLogger().warning("Failed to convert commandline: " + initial);
      current.add(obj);
    }
    array = Array.newInstance(getItemClass(), current.size());
    for (i = 0; i < current.size(); i++)
      Array.set(array, i, current.get(i));
    m_ArrayEditor.setValue(array);
  }

  /**
   * Displays the dialog, prompting the user to select classes.
   * 
   * @return		the selected objects, null in case of user cancelling the dialog
   */
  @Override
  protected Object[] showDialog() {
    Object[]	result;
    Object	array;
    int		i;
    
    m_Dialog.setVisible(true);
    if (m_Dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;
    
    array = m_ArrayEditor.getValue();
    result = new Object[Array.getLength(array)];
    for (i = 0; i < result.length; i++)
      result[i] = Array.get(array, i);
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Dialog != null)
      m_Dialog.setVisible(false);
    
    super.stopExecution();
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    Runnable			run;
    final ApprovalDialog	dialog;
    
    if (m_Dialog != null) {
      dialog = m_Dialog;
      run = new Runnable() {
        @Override
        public void run() {
          dialog.dispose();
        }
      };
      m_Dialog = null;
      SwingUtilities.invokeLater(run);
    }

    if (m_ArrayEditor != null) {
      m_ArrayEditor = null;
    }
    
    super.wrapUp();
  }
}
