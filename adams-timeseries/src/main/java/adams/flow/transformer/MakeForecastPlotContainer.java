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
 * MakeForecastPlotContainer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.evaluation.NumericPrediction;
import adams.core.QuickInfoHelper;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.WekaForecastContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Creates a named containers for the SequencePlotter actor using the incoming forecasts.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaForecastContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaForecastContainer: Model, Forecasts<br>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, IsMarker
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MakeForecastPlotContainer
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
 * <pre>-plot-names &lt;java.lang.String&gt; (property: plotNames)
 * &nbsp;&nbsp;&nbsp;The names for the plots (comma-separated list).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MakeForecastPlotContainer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2488434486963278287L;

  /** the names of the plots (comma-separated list). */
  protected String m_PlotNames;

  /** the containers to output. */
  protected List<SequencePlotterContainer> m_Queue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a named containers for the SequencePlotter actor using the incoming forecasts.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "plot-names", "plotNames",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Queue = new ArrayList<SequencePlotterContainer>();
  }
  
  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Queue.clear();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "plotNames", (m_PlotNames.length() > 0 ? m_PlotNames : "-unnamed-"));
  }

  /**
   * Sets the plot names (comma-separated list).
   *
   * @param value	the names
   */
  public void setPlotNames(String value) {
    m_PlotNames = value;
    reset();
  }

  /**
   * Returns the current plot names (comma-separated list).
   *
   * @return		the names
   */
  public String getPlotNames() {
    return m_PlotNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNamesTipText() {
    return "The names for the plots (comma-separated list).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.WekaForecastContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{WekaForecastContainer.class};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    WekaForecastContainer		forecast;
    SequencePlotterContainer		cont;
    String[]				names;
    List<List<NumericPrediction>>	list;
    int					i;

    result = null;
    
    forecast = (WekaForecastContainer) m_InputToken.getPayload();
    list     = (List<List<NumericPrediction>>) forecast.getValue(WekaForecastContainer.VALUE_FORECASTS);
    if (m_PlotNames.length() == 0)
      names = null;
    else
      names = m_PlotNames.split(",");
    
    for (List<NumericPrediction> preds: list) {
      // automatic names?
      if (names == null) {
	names = new String[preds.size()];
	for (i = 0; i < names.length; i++)
	  names[i] = "Forecast-" + (i+1);
      }
      
      // create containers
      for (i = 0; i < names.length && i < preds.size(); i++) {
	cont = new SequencePlotterContainer(names[i], preds.get(i).predicted());
	m_Queue.add(cont);
      }
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SequencePlotterContainer.class};
  }
  
  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Queue != null) && (m_Queue.size() > 0);
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    
    result = new Token(m_Queue.get(0));
    m_Queue.remove(0);
    
    return result;
  }
}
