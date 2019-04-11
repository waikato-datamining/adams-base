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
 * TreeGraphML.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.GraphHelper;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.io.GraphMLExporter;
import weka.core.Drawable;
import weka.gui.visualize.plugins.JGraphTTreeVisualization;
import weka.gui.visualize.plugins.jgrapht.SimpleEdge;
import weka.gui.visualize.plugins.jgrapht.SimpleEdgeAttributeProvider;
import weka.gui.visualize.plugins.jgrapht.SimpleEdgeIDProvider;
import weka.gui.visualize.plugins.jgrapht.SimpleEdgeLabelProvider;
import weka.gui.visualize.plugins.jgrapht.SimpleVertex;
import weka.gui.visualize.plugins.jgrapht.SimpleVertexAttributeProvider;
import weka.gui.visualize.plugins.jgrapht.SimpleVertexIDProvider;
import weka.gui.visualize.plugins.jgrapht.SimpleVertexLabelProvider;

import javax.swing.JComponent;
import java.io.StringWriter;

/**
 * Displays the GraphML source code of the tree graph.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TreeGraphML
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the tree graph source as GraphML (https://en.wikipedia.org/wiki/GraphML).";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Tree GraphML";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasModel() && (item.getModel() instanceof Drawable) && GraphHelper.isDottyTree((Drawable) item.getModel());
  }

  /**
   * Converts the dotty string and saves it as graphml file.
   *
   * @param dotty	the graph in dotty notation to turn into graphml
   * @param errors 	for collecting errors
   * @return		the GraphML representation, null in case of error
   */
  protected String toGraphml(String dotty, MessageCollection errors) {
    DirectedPseudograph<SimpleVertex, SimpleEdge> graph;
    GraphMLExporter<SimpleVertex, SimpleEdge> exporter;
    StringWriter writer;

    try {
      writer = new StringWriter();
      graph = JGraphTTreeVisualization.getSingleton().importDotty(dotty);
      exporter = new GraphMLExporter<>(
        new SimpleVertexIDProvider(),
	new SimpleVertexLabelProvider(),
	new SimpleVertexAttributeProvider(),
	new SimpleEdgeIDProvider(),
	new SimpleEdgeLabelProvider(),
	new SimpleEdgeAttributeProvider());
      exporter.exportGraph(graph, writer);
      return writer.toString();
    }
    catch (Exception e) {
      errors.add("Failed to export dotty as GraphML:\n" + dotty, e);
      return null;
    }
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    BaseTextArea 	text;
    String		dotty;
    String		graphml;

    if (!item.hasModel()) {
      errors.add("No model available!");
      return null;
    }

    try {
      dotty = ((Drawable) item.getModel()).graph();
      graphml = toGraphml(dotty, errors);
      if (graphml == null)
        return null;
      text = new BaseTextArea();
      text.setEditable(false);
      text.setTextFont(Fonts.getMonospacedFont());
      text.setText(graphml);
      text.setCaretPosition(0);
      return new TextualContentPanel(text, true);
    }
    catch (Exception e) {
      errors.add("Failed to obtain graph source!", e);
      return null;
    }
  }
}
