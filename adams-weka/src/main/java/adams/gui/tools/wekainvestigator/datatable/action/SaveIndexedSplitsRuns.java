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
 * SaveIndexedSplitsRuns.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.core.ClassLister;
import adams.core.MessageCollection;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.io.output.AbstractIndexedSplitsRunsWriter;
import adams.data.io.output.JsonIndexedSplitsRunsWriter;
import adams.flow.transformer.indexedsplitsrunsgenerator.InstancesIndexedSplitsRunsGenerator;
import adams.flow.transformer.indexedsplitsrunsgenerator.InstancesRandomSplitGenerator;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.FileContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Saves the indexed splits runs generated from the selected data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SaveIndexedSplitsRuns
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /** the dialog for exporting. */
  protected ApprovalDialog m_Dialog;

  /** the GOE panel for the generator. */
  protected GenericObjectEditorPanel m_PanelGenerator;

  /** the GOE panel for the writer. */
  protected GenericObjectEditorPanel m_PanelWriter;

  /** the chooser panel for the output file. */
  protected FileChooserPanel m_PanelOutput;

  /**
   * Instantiates the action.
   */
  public SaveIndexedSplitsRuns() {
    super();
    setName("Save indexed splits runs");
    setIcon("indexed_splits_runs.gif");
    setAsynchronous(true);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer[]			conts;
    ParameterPanel			panelParams;
    AbstractIndexedSplitsRunsWriter 	writer;
    ExtensionFileFilter			fileFilter;

    if (m_Dialog == null) {
      if (getOwner().getParentDialog() != null)
        m_Dialog = new ApprovalDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
        m_Dialog = new ApprovalDialog(getOwner().getParentFrame(), true);
      m_Dialog.setTitle("Save indexed splits runs");

      panelParams = new ParameterPanel();
      m_Dialog.getContentPane().add(panelParams, BorderLayout.CENTER);

      m_PanelGenerator = new GenericObjectEditorPanel(InstancesIndexedSplitsRunsGenerator.class, new InstancesRandomSplitGenerator(), true);
      panelParams.addParameter("Generator", m_PanelGenerator);

      m_PanelWriter = new GenericObjectEditorPanel(AbstractIndexedSplitsRunsWriter.class, new JsonIndexedSplitsRunsWriter(), true);
      panelParams.addParameter("Writer", m_PanelWriter);

      m_PanelOutput = new FileChooserPanel();
      m_PanelOutput.setAcceptAllFileFilterUsed(false);
      m_PanelOutput.setPromptOverwriteFile(true);
      m_PanelOutput.setAutoAppendExtension(true);
      for (Class cls: ClassLister.getSingleton().getClasses(AbstractIndexedSplitsRunsWriter.class)) {
        try {
          writer     = (AbstractIndexedSplitsRunsWriter) cls.getDeclaredConstructor().newInstance();
          fileFilter = new ExtensionFileFilter(writer.getFormatDescription(), writer.getFormatExtensions());
          m_PanelOutput.addChoosableFileFilter(fileFilter);
          if (writer instanceof JsonIndexedSplitsRunsWriter)
	    m_PanelOutput.setFileFilter(fileFilter);
	}
	catch (Exception ex) {
          // ignored
	}
      }
      panelParams.addParameter("Output", m_PanelOutput);

      m_Dialog.pack();
      m_Dialog.setLocationRelativeTo(null);
    }

    m_Dialog.setVisible(true);
    if (m_Dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    conts = getSelectedData();
    // TODO requires queuing to work properly!
    final DataContainer data = conts[0];
    getOwner().startExecution(new InvestigatorTabJob(getOwner(), m_Dialog.getTitle()) {
      @Override
      protected void doRun() {
	InstancesIndexedSplitsRunsGenerator generator = (InstancesIndexedSplitsRunsGenerator) m_PanelGenerator.getCurrent();
	AbstractIndexedSplitsRunsWriter writer = (AbstractIndexedSplitsRunsWriter) m_PanelWriter.getCurrent();
	File outputFile = m_PanelOutput.getCurrent();
	MessageCollection errors = new MessageCollection();
	try {
	  // generate
	  IndexedSplitsRuns runs = generator.generate(data.getData(), errors);
	  if (runs == null) {
	    if (errors.isEmpty())
	      logError("Failed to generate indexed splits runs!", getTitle());
	    else
	      logError("Failed to generate indexed splits runs:\n" + errors, getTitle());
	    return;
	  }
	  // write
	  if (data instanceof FileContainer)
	    runs.getMetaData().put("file", data.getSource());
	  if (!writer.write(outputFile.getAbsoluteFile(), runs, errors)) {
	    if (errors.isEmpty())
	      logError("Failed to write indexed splits runs to '" + outputFile + "'!", getTitle());
	    else
	      logError("Failed to write indexed splits runs to '" + outputFile + "':\n" + errors, getTitle());
	  }
	  else {
	    showStatus("Indexed splits runs written to: " + outputFile);
	  }
	}
	catch (Exception ex) {
	  logError("Failed to save indexed splits runs: " + outputFile + "\n", ex, getTitle());
	}
      }
    });
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && getTable().getSelectedRowCount() == 1);
  }
}
