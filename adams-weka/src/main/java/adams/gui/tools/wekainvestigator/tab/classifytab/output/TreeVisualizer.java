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
 * TreeVisualizer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.output.GraphHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedPseudograph;
import weka.core.Drawable;
import weka.gui.visualize.plugins.JGraphTTreeVisualization;
import weka.gui.visualize.plugins.jgrapht.SimpleEdge;
import weka.gui.visualize.plugins.jgrapht.SimpleVertex;

import javax.swing.JComponent;

/**
 * Displays the tree that the model generated.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TreeVisualizer
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a tree visualization of the model.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Tree visualizer";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasModel() && (item.getModel() instanceof Drawable) && GraphHelper.hasGraph((Drawable) item.getModel());
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    String						dotty;
    DirectedPseudograph<SimpleVertex, SimpleEdge> 	graph;
    JGraphXAdapter<SimpleVertex, SimpleEdge> 		jgxAdapter;
    mxGraphComponent 					component;
    mxCompactTreeLayout 				layout;

    try {
      dotty = ((Drawable) item.getModel()).graph();
      graph = JGraphTTreeVisualization.getSingleton().importDotty(dotty);
      jgxAdapter = new JGraphXAdapter<>(graph);
      jgxAdapter.setCellsEditable(false);
      jgxAdapter.setLabelsVisible(true);
      component = new mxGraphComponent(jgxAdapter);
      component.getConnectionHandler().setEnabled(false);
      component.setPanning(true);
      component.setConnectable(false);
      //component.setEnabled(false);
      component.setDragEnabled(true);
      component.setZoomFactor(5.0);
      component.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
      layout = new mxCompactTreeLayout(jgxAdapter);
      layout.setHorizontal(false);
      layout.execute(jgxAdapter.getDefaultParent());
      return new ComponentContentPanel(component, false);
    }
    catch (Exception e) {
      errors.add("Failed to parse/display tree graph!", e);
      return null;
    }
  }
}
