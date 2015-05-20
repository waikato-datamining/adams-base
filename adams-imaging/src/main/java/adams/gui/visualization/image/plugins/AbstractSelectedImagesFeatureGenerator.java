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
 * AbstractImageFlattener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.SwingWorker;

import adams.data.conversion.SpreadSheetAddRowID;
import adams.data.conversion.TransposeSpreadSheet;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.columnfinder.ByContentType;
import adams.flow.control.Flow;
import adams.flow.control.SubProcess;
import adams.flow.core.ActorUtils;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SpreadSheetColumnFilter;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.GUIHelper;
import adams.gui.tools.spreadsheetviewer.chart.LinePlot;
import adams.gui.visualization.image.ImagePanel;

/**
 * Ancestor for image feature generator plugins.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectedImagesFeatureGenerator
  extends AbstractSelectedImagesViewerPluginWithGOE {

  /** for serialization. */
  private static final long serialVersionUID = -3111612432359476318L;
  
  /** for storing filtering errors. */
  protected String m_FilterError;

  /** the collected data. */
  protected List<Row> m_Collected;

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   * <br><br>
   * Panel must be non-null and must contain an image.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(ImagePanel panel) {
    return (panel != null) && (panel.getCurrentImage() != null);
  }

  /**
   * Returns the title for the dialog.
   * 
   * @return		the title
   */
  protected String getDialogTitle() {
    String	result;
    
    result = getLastSetup().getClass().getSimpleName();
    if (m_CurrentPanel.getCurrentFile() != null)
      result += " [" + m_CurrentPanel.getCurrentFile().getName() + " -- " + m_CurrentPanel.getCurrentFile().getParent() + "]";
    
    return result;
  }
  
  /**
   * Returns the default size of the dialog.
   * 
   * @return		the dimension of the dialog
   */
  @Override
  protected Dimension getDialogSize() {
    return new Dimension(800, 600);
  }

  /**
   * Initializes the processing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processInit() {
    String	result;
    
    result = super.processInit();
    
    if (result == null)
      m_Collected = new ArrayList<Row>();
    
    return result;
  }
  
  /**
   * Generats the features from the image.
   *
   * @param image	the image to process
   * @return		the generated features
   */
  protected abstract Row[] generateFeatures(BufferedImage image);
  
  /**
   * Processes the specified panel.
   * 
   * @param panel	the panel to process
   * @return		null if successful, error message otherwise
   */
  @Override
  protected String process(ImagePanel panel) {
    String		result;
    BufferedImage	input;
    Row[]		output;

    result = null;
    input  = panel.getCurrentImage();
    try {
      output = generateFeatures(input);
      m_Collected.addAll(Arrays.asList(output));

      // did user abort filtering?
      if (m_CanceledByUser)
	return result;

      if (output == null) {
	result = "Failed to generate features: ";
	if (m_FilterError == null)
	  result += "unknown reason";
	else
	  result += m_FilterError;
      }
    }
    catch (Exception e) {
      m_FilterError = e.toString();
      result = "Failed to generate features: ";
      getLogger().log(Level.SEVERE, result, e);
      result += m_FilterError;
    }

    return result;
  }

  /**
   * Collates the data for the plot.
   * 
   * @return		the generated data
   */
  protected SpreadSheet collate() throws Exception {
    SpreadSheet				result;
    SubProcess				sub;
    SpreadSheetColumnFilter		filter;
    ByContentType			finder;
    Convert				conv;
    TransposeSpreadSheet		transpose;
    SpreadSheetAddRowID			id;
    List				list;

    result = m_Collected.get(0).getOwner().getClone();
    result.clear();
    for (Row row: m_Collected)
      result.addRow().assign(row);
    
    sub = new SubProcess();
    
    finder = new ByContentType();
    finder.setContentTypes(new ContentType[]{ContentType.LONG, ContentType.DOUBLE});
    filter = new SpreadSheetColumnFilter();
    filter.setFinder(finder);
    sub.add(filter);
    
    transpose = new TransposeSpreadSheet();
    conv = new Convert();
    conv.setConversion(transpose);
    sub.add(conv);
    
    id = new SpreadSheetAddRowID();
    id.setHeader("features");
    conv = new Convert();
    conv.setConversion(id);
    sub.add(conv);
    
    list = ActorUtils.transform(sub, result);
    if ((list == null) || (list.size() != 1))
      throw new IllegalStateException("Failed to collate data from generated features!");
    result = (SpreadSheet) list.get(0);
    
    return result;
  }
  
  /**
   * Finishes up the processing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processFinish() {
    String		result;
    SpreadSheet		sheet;
    LinePlot		plot;
    final Flow		flow;
    SwingWorker		worker;
    
    result = super.processFinish();
    sheet  = null;

    if (m_Collected.size() == 0)
      return result;
    
    if (result == null) {
      try {
	sheet = collate();
	plot = new LinePlot();
	plot.setXColumn("1");
	plot.setYColumns("2-last");
	flow = plot.generate(getCaption(), sheet);
	flow.setParentComponent(m_CurrentPanel);
	worker = new SwingWorker() {
	  String msg = null;

	  @Override
	  protected Object doInBackground() throws Exception {
	    msg = flow.setUp();
	    if (msg != null)
	      msg = "Failed to setup flow for displaying generated features:\n" + msg;

	    if (msg == null) {
	      msg = flow.execute();
	      if (msg != null)
		msg = "Failed to execute flow for displaying generated features:\n" + msg;
	    }

	    if (msg == null) {
	      flow.wrapUp();
	      if (flow.hasStopMessage())
		msg = "Flow execution for displaying generated features was stopped:\n" + flow.getStopMessage();
	    }

	    return msg;
	  }

	  @Override
	  protected void done() {
	    super.done();
	    if (msg != null) {
	      GUIHelper.showErrorMessage(m_CurrentPanel, msg);
	      ConsolePanel.getSingleton().append(OutputType.ERROR, msg + "\n");
	      ConsolePanel.getSingleton().append(OutputType.ERROR, flow.toCommandLine() + "\n");
	      flow.destroy();
	    }
	    else {
	      m_CurrentPanel.addDependentFlow(flow);
	    }
	  }
	};

	worker.execute();
      }
      catch (Exception e) {
	result = e.toString();
      }
    }
    
    return result;
  }
}
