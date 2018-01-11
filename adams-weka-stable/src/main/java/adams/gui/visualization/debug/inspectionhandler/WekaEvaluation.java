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
 * WekaEvaluation.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import adams.flow.core.EvaluationStatistic;
import adams.flow.core.Token;
import adams.flow.transformer.WekaEvaluationValues;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.classifiers.Evaluation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Provides further insight into an {@link Evaluation} object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEvaluation
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Evaluation.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  @Override
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    Evaluation 			eval;
    WekaEvaluationValues	values;
    boolean			numeric;
    boolean			nominal;
    List<EvaluationStatistic>	stats;
    String			msg;
    Token			token;

    result = new Hashtable<>();

    eval = (Evaluation) obj;

    result.put("header", eval.getHeader());
    result.put("predictions", eval.predictions());

    nominal = eval.getHeader().classAttribute().isNominal();
    numeric = eval.getHeader().classAttribute().isNumeric();
    stats   = new ArrayList<>();
    for (EvaluationStatistic stat: EvaluationStatistic.values()) {
      if (nominal && stat.isOnlyNominal())
	stats.add(stat);
      else if (numeric && stat.isOnlyNumeric())
	stats.add(stat);
      else if (!stat.isOnlyNumeric() && !stat.isOnlyNominal())
	stats.add(stat);
    }
    values = new WekaEvaluationValues();
    values.setStatisticValues(stats.toArray(new EvaluationStatistic[stats.size()]));
    values.input(new Token(eval));
    msg = values.execute();
    if (msg == null) {
      token = values.output();
      if (token != null)
	result.put("statistics", token.getPayload());
    }
    else {
      System.err.println(getClass().getName() + ": Failed to extract statistics:\n" + msg);
    }

    return result;
  }
}
