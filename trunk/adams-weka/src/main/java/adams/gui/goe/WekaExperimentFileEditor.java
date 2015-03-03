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
 * WekaExperimentFileEditor.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.classifiers.Classifier;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.gui.experiment.SimpleSetupPanel;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOption;
import adams.data.WekaExperimentFile;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

/**
 * A PropertyEditor for WekaExperimentFile objects that lets the user select a file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentFileEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport {

  /**
   * A dialog for displaying the simple setup of an experiment.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SimpleSetupDialog
    extends JDialog {

    /** for serialization. */
    private static final long serialVersionUID = 7551996596456389490L;

    /** the setup panel. */
    protected SimpleSetupPanel m_Panel;

    /** the current experiment. */
    protected Experiment m_Experiment;

    /** the OK button. */
    protected JButton m_ButtonOK;

    /** the Cancel button. */
    protected JButton m_ButtonCancel;

    /**
     * Initializes the dialog.
     *
     * @param owner	the owning frame
     */
    public SimpleSetupDialog(Frame owner) {
      super(owner, "WEKA Experiment setup", true);

      initialize();
      initGUI();
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the owning dialgo
     */
    public SimpleSetupDialog(Dialog owner) {
      super(owner, "WEKA Experiment setup", true);

      initialize();
      initGUI();
    }

    /**
     * Initializes its members.
     */
    protected void initialize() {
      m_Experiment = newExperiment();
    }

    /**
     * Sets up the GUI components.
     */
    protected void initGUI() {
      JPanel	panel;

      setLayout(new BorderLayout());

      m_Panel = new SimpleSetupPanel();
      disableButtons(m_Panel);
      add(m_Panel, BorderLayout.CENTER);

      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      add(panel, BorderLayout.SOUTH);

      m_ButtonOK = new JButton("OK");
      m_ButtonOK.setMnemonic('O');
      m_ButtonOK.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_Experiment = m_Panel.getExperiment();
	  setVisible(false);
	}
      });
      panel.add(m_ButtonOK);

      m_ButtonCancel = new JButton("Cancel");
      m_ButtonCancel.setMnemonic('C');
      m_ButtonCancel.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  setVisible(false);
	}
      });
      panel.add(m_ButtonCancel);

      pack();
      setSize(new Dimension(800, 600));
    }

    /**
     * Disables the buttons in the SimpleSetupPanel.
     *
     * @param cont	the container to search
     * @return		true if disabled
     */
    protected boolean disableButtons(Container cont) {
      boolean	result;
      int	i;
      String	caption;

      result  = false;
      caption = "Open...";

      for (i = 0; i < cont.getComponentCount(); i++) {
	if (cont.getComponent(i) instanceof JButton) {
	  // disable buttons
	  if (((JButton) cont.getComponent(i)).getText().equals(caption)) {
	    cont.setVisible(false);
	    result = true;
	  }
	}
	else if (cont.getComponent(i) instanceof JPanel) {
	  result = disableButtons((Container) cont.getComponent(i));
	}

	if (result)
	  break;
      }

      return result;
    }

    /**
     * Sets the experiment to use.
     *
     * @param value	the experiment to displays and modify
     */
    public void setExperiment(Experiment value) {
      if (value == null)
	value = newExperiment();
      m_Experiment = (Experiment) Utils.deepCopy(value);
      m_Panel.setExperiment(value);
    }

    /**
     * Returns the experiment.
     *
     * @return		the experiment
     */
    public Experiment getExperiment() {
      return (Experiment) Utils.deepCopy(m_Experiment);
    }
  }

  /** the panel for selecting the experiment file. */
  protected FileChooserPanel m_PanelFile;

  /** the editor panel. */
  protected BasePanel m_PanelEditor;

  /** the button to bring up the dialog for editing the experiment. */
  protected JButton m_ButtonEdit;

  /** the OK button. */
  protected JButton m_ButtonOK;

  /** the Cancel button. */
  protected JButton m_ButtonCancel;

  /**
   * Returns the file as string.
   *
   * @param option	the current option
   * @param object	the file object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((WekaExperimentFile) object).getPath();
  }

  /**
   * Returns a file generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a file
   * @return		the generated file
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new WekaExperimentFile(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Generates a new (simple) experiment.
   *
   * @return		the generated experiment
   */
  public static Experiment newExperiment() {
    Experiment				result;
    CrossValidationResultProducer 	cvrp;

    result = new Experiment();
    cvrp = new CrossValidationResultProducer();
    cvrp.setNumFolds(10);
    cvrp.setSplitEvaluator(new ClassifierSplitEvaluator());
    result.setResultProducer(cvrp);
    result.setPropertyArray(new Classifier[0]);
    result.setUsePropertyIterator(true);

    return result;
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  public String getJavaInitializationString() {
    WekaExperimentFile f = (WekaExperimentFile) getValue();
    if (f == null)
      return "null";
    else
      return "new WekaExperimentFile(\"" + f.getName() + "\")";
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  protected JComponent createCustomEditor() {
    WekaExperimentFile	currentFile;
    JPanel		panel;

    m_PanelEditor = new BasePanel();
    m_PanelEditor.setLayout(new GridLayout(3, 1));
    m_PanelEditor.setBorder(BorderFactory.createEmptyBorder());

    // file
    m_PanelFile = new FileChooserPanel();
    currentFile = (WekaExperimentFile) getValue();
    if (currentFile == null)
      currentFile = new WekaExperimentFile(System.getProperty("user.dir"));
    m_PanelFile.setCurrent(currentFile);
    m_PanelFile.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	File file = m_PanelFile.getCurrent();
        m_ButtonEdit.setEnabled(!file.isDirectory());
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelFile);
    m_PanelEditor.add(panel);

    // edit
    m_ButtonEdit = new JButton("Edit");
    m_ButtonEdit.setMnemonic('E');
    m_ButtonEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	PlaceholderFile file = new PlaceholderFile(m_PanelFile.getCurrent());
	Experiment exp = null;
	if (file.exists()) {
	  try {
	    exp = Experiment.read(file.getAbsolutePath());
	  }
	  catch (Exception ex) {
	    ex.printStackTrace();
	    exp = null;
	  }
	}
	if (exp == null)
	  exp = newExperiment();
	SimpleSetupDialog dlg;
	if (m_PanelEditor.getParentDialog() != null)
	  dlg = new SimpleSetupDialog(m_PanelEditor.getParentDialog());
	else
	  dlg = new SimpleSetupDialog(m_PanelEditor.getParentFrame());
	dlg.setExperiment(exp);
	dlg.setLocationRelativeTo(dlg.getOwner());
	dlg.setVisible(true);
	try {
	  Experiment.write(
	      new PlaceholderFile(m_PanelFile.getCurrent()).getAbsolutePath(),
	      dlg.getExperiment());
	}
	catch (Exception ex) {
	  ex.printStackTrace();
	  GUIHelper.showErrorMessage(
	      dlg.getOwner(),
	      "Couldn't save WEKA experiment file:\n"
	      + m_PanelFile.getCurrent()
	      + "\nReason:\n" + ex.getMessage(),
	      "Save WEKA Experiment");
	}
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_ButtonEdit);
    m_PanelEditor.add(panel);

    // buttons
    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setValue(new WekaExperimentFile(m_PanelFile.getCurrent()));
	m_PanelEditor.closeParent();
      }
    });
    m_ButtonCancel = new JButton("Cancel");
    m_ButtonCancel.setMnemonic('C');
    m_ButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_PanelEditor.closeParent();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(m_ButtonOK);
    panel.add(m_ButtonCancel);
    m_PanelEditor.add(panel);

    return m_PanelEditor;
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    WekaExperimentFile 	currentFile;

    super.initForDisplay();

    currentFile = (WekaExperimentFile) getValue();
    if (currentFile == null)
      currentFile = new WekaExperimentFile(System.getProperty("user.dir"));
    m_PanelFile.setCurrent(currentFile);
    m_ButtonEdit.setEnabled(!currentFile.isDirectory());
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    FontMetrics fm = gfx.getFontMetrics();
    int vpad = (box.height - fm.getHeight()) / 2 ;
    WekaExperimentFile f = (WekaExperimentFile) getValue();
    String val = "No file";
    if (f != null)
      val = f.toString();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    if (isInlineValueValid(value))
      setValue(new WekaExperimentFile(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((WekaExperimentFile) getValue()).toString();
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    try {
      new WekaExperimentFile(value).getAbsolutePath();
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
}
