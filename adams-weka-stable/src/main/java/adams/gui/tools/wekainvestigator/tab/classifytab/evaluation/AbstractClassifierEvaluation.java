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
 * AbstractClassifierEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.ClassLister;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetView;
import adams.flow.core.Token;
import adams.flow.transformer.SpreadSheetInsertColumn;
import adams.gui.chooser.SelectOptionPanel;
import adams.gui.tools.wekainvestigator.evaluation.AbstractEvaluation;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.ml.data.InstancesView;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.apache.commons.lang.time.StopWatch;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for classifier evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierEvaluation
  extends AbstractEvaluation<ClassifyTab, ResultItem> {

  private static final long serialVersionUID = -5847790432092994409L;

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(Classifier classifier);

  /**
   * Initializes the result item.
   *
   * @param classifier	the current classifier
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public abstract ResultItem init(Classifier classifier) throws Exception;

  /**
   * Evaluates the classifier and updates the result item.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  protected abstract void doEvaluate(Classifier classifier, ResultItem item) throws Exception;

  /**
   * Evaluates the classifier and updates the result item.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  public void evaluate(Classifier classifier, ResultItem item) throws Exception {
    StopWatch 	watch;

    watch = new StopWatch();
    watch.start();
    doEvaluate(classifier, item);
    watch.stop();
    if (item.hasRunInformation())
      item.getRunInformation().add("Total time", (watch.getTime() / 1000.0) + "s");
  }

  /**
   * Fills the panel for selection options with the attributes from the
   * specified data container.
   *
   * @param select	the panel to fill
   * @param index	the index, ignored if -1
   */
  protected void fillWithAttributeNames(SelectOptionPanel select, int index) {
    Instances		data;
    List<String>	atts;
    int			i;

    atts = new ArrayList<>();
    if (index > -1) {
      if (index < getOwner().getData().size()) {
	data = getOwner().getData().get(index).getData();
	for (i = 0; i < data.numAttributes(); i++)
	  atts.add(data.attribute(i).name());
	Collections.sort(atts);
      }
    }
    select.setOptions(atts.toArray(new String[atts.size()]));
  }

  /**
   * Transfers the additional attributes into a spreadsheet.
   *
   * @param select	the selected attributes
   * @param data	the data to transfer
   * @return		the spreadsheet
   */
  protected SpreadSheet transferAdditionalAttributes(SelectOptionPanel select, Instances data) {
    SpreadSheet			result;
    InstancesView 		iview;
    SpreadSheetView		sview;
    String[]			atts;
    TIntList			indices;
    SpreadSheetInsertColumn	insert;
    Token			token;
    String			msg;
    int				i;

    atts    = select.getCurrent();
    indices = new TIntArrayList();
    for (String att: atts) {
      if (data.attribute(att) != null)
	indices.add(data.attribute(att).index());
    }
    iview = new InstancesView(data);
    sview = new SpreadSheetView(iview, null, indices.toArray());
    result = new DefaultSpreadSheet();
    result.getHeaderRow().assign(sview.getHeaderRow());
    for (Row row: sview.rows())
      result.addRow().assign(row);
    insert = new SpreadSheetInsertColumn();
    insert.setHeader("Instance Index");
    insert.setNoCopy(false);
    insert.setPosition(new SpreadSheetColumnIndex("1"));
    insert.setAfter(false);
    insert.input(new Token(result));
    msg = insert.execute();
    if (msg != null) {
      getLogger().severe("Failed to transfer additional attributes!\n" + msg);
      return null;
    }
    token = insert.output();
    result = (SpreadSheet) token.getPayload();
    for (i = 0; i < result.getRowCount(); i++)
      result.getRow(i).getCell(0).setContent(i+1);

    return result;
  }

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getEvaluations() {
    return ClassLister.getSingleton().getClasses(AbstractClassifierEvaluation.class);
  }
}
