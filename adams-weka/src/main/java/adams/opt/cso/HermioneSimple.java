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
 * HermioneSimple.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import adams.core.base.BaseClassname;
import adams.core.discovery.DefaultPropertyDiscovery;
import adams.core.discovery.PropertyPath;
import adams.core.discovery.cso.AbstractCatSwarmOptimizationDiscoveryHandler;
import adams.core.discovery.cso.GenericDouble;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.opt.cso.stopping.MaxIterationsWithoutImprovement;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import org.jblas.DoubleMatrix;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegressionJ;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HermioneSimple
  extends AbstractClassifierBasedSimpleCatSwarmOptimization {

  private static final long serialVersionUID = -6038649991364374788L;

  /** the handlers to use for discovery. */
  protected AbstractCatSwarmOptimizationDiscoveryHandler[] m_Handlers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple swarm-based Hermione.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handlers",
      new AbstractCatSwarmOptimizationDiscoveryHandler[0]);
  }

  /**
   * Returns the default output type to use.
   *
   * @return		the type
   */
  @Override
  protected OutputType getDefaultOutputType() {
    return OutputType.SETUP;
  }

  /**
   * Sets the discovery handlers to use.
   *
   * @param value	the classifier
   */
  public void setHandlers(AbstractCatSwarmOptimizationDiscoveryHandler[] value) {
    m_Handlers = value;
    reset();
  }

  /**
   * Returns the currently set discovery handlers.
   *
   * @return		the handlers
   */
  public AbstractCatSwarmOptimizationDiscoveryHandler[] getHandlers() {
    return m_Handlers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlersTipText() {
    return "The discovery handlers to use.";
  }

  /**
   * Generate the classifier from current bit array
   *
   * @param particle	the particle to obtain the values from
   * @return		the classifier
   */
  public Classifier generateClassifier(DoubleMatrix particle) {
    AbstractCatSwarmOptimizationDiscoveryHandler 	handlers[];
    Classifier 						result;
    DefaultPropertyDiscovery 				discovery;
    int 						pos;
    int							dimensions;
    int							i;
    double[] 						values;

    if (isLoggingEnabled())
      getLogger().fine("[generateClassifier] Particle: " + particle);

    handlers = new AbstractCatSwarmOptimizationDiscoveryHandler[m_Handlers.length];
    for (i = 0; i < handlers.length;i++)
      handlers[i]=(AbstractCatSwarmOptimizationDiscoveryHandler) m_Handlers[i].shallowCopy();

    result = (Classifier) OptionUtils.shallowCopy(getClassifier());
    if (result == null) {
      getLogger().severe("Failed to copy classifier!");
      return null;
    }
    discovery = new DefaultPropertyDiscovery();
    discovery.discover(handlers, result);

    // apply values
    pos = 0;
    for (AbstractCatSwarmOptimizationDiscoveryHandler handler : handlers) {
      dimensions = handler.getDimensions();
      for (PropertyPath.PropertyContainer cont : handler.getContainers()) {
	values = new double[dimensions];
	for (i = 0; i < dimensions; i++)
	  values[i] = particle.get(0, pos + i);
	handler.apply(cont, values);
	pos += dimensions;
      }
    }

    return result;
  }

  /**
   * Problem-specific fitness function
   * -- expects a one-dimensional matrix
   * -- returns a non-negative value where lower is better
   * -- should be implemented for different problems
   */
  @Override
  public double particleFitness(DoubleMatrix particle) {
    Double		result;
    Classifier		cls;

    result = getResult(particle);
    if (result != null) {
      if (isLoggingEnabled())
	getLogger().info("Already present: " + result);
      return result;
    }

    cls = generateClassifier(particle);
    if (cls == null)
      return Double.NaN;

    try {
      result = evaluateClassifier(cls, m_Instances, m_Folds, m_CrossValidationSeed);
      addResult(particle, result);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to evaluate classifier!", e);
      result = Double.NaN;
    }

    if (isLoggingEnabled())
      getLogger().fine("[particleFitness] fitness=" + result + " from " + particle);

    return result;
  }

  /**
   * Problem-specific random particle generator
   * -- should return a one dimensional matrix of fixed length
   *
   */
  @Override
  public DoubleMatrix randomParticle() {
    DoubleMatrix					result;
    Classifier						cls;
    TDoubleList 					collected;
    AbstractCatSwarmOptimizationDiscoveryHandler 	handlers[];
    DefaultPropertyDiscovery 				discovery;
    int							i;
    double[]						values;

    handlers = new AbstractCatSwarmOptimizationDiscoveryHandler[m_Handlers.length];
    for (i = 0; i < handlers.length;i++) {
      handlers[i] = (AbstractCatSwarmOptimizationDiscoveryHandler) m_Handlers[i].shallowCopy();
      handlers[i].setSeed(m_Random.nextInt());
    }

    cls = (Classifier) OptionUtils.shallowCopy(getClassifier());
    if (cls == null) {
      getLogger().severe("Failed to copy classifier!");
      return null;
    }
    discovery = new DefaultPropertyDiscovery();
    discovery.discover(handlers, cls);

    collected = new TDoubleArrayList();
    for (AbstractCatSwarmOptimizationDiscoveryHandler handler : handlers) {
      for (PropertyPath.PropertyContainer cont : handler.getContainers()) {
	values = handler.random();
	collected.add(values);
      }
    }

    result = new DoubleMatrix(1, collected.size());
    for (i = 0; i < collected.size(); i++)
      result.data[i] = collected.get(i);

    if (isLoggingEnabled())
      getLogger().fine("[randomParticle] " + result);

    return result;
  }

  /**
   * Method to get the best classifier from the swarm.
   *
   * @return 		the best classifier
   */
  public Classifier getBestSetup() {
    return generateClassifier(getBest());
  }

  /**
   * For testing only.
   *
   * @param args	the dataset to use
   * @throws Exception	if something fails
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    Instances data = DataSource.read(args[0]);
    if (data.classIndex() == -1)
      data.setClassIndex(data.numAttributes() - 1);
    MaxIterationsWithoutImprovement stopping = new MaxIterationsWithoutImprovement();
    stopping.setNumIterations(2);
    stopping.setMinimumImprovement(0.001);
    stopping.setLoggingLevel(LoggingLevel.INFO);
    HermioneSimple simple = new HermioneSimple();
    simple.setEvalParallel(true);
    simple.setMeasure(Measure.CC);
    simple.setStopping(stopping);
    simple.setLoggingLevel(LoggingLevel.INFO);
    simple.setInstances(data);
    /*
    simple.setClassifier(new GPD());
    simple.setHandlers(new AbstractCatSwarmOptimizationDiscoveryHandler[]{
      new GPDGamma(),
      new GPDNoise(),
    });
    */
    LinearRegressionJ cls = new LinearRegressionJ();
    cls.setEliminateColinearAttributes(false);
    cls.setAttributeSelectionMethod(new SelectedTag(LinearRegressionJ.SELECTION_NONE, LinearRegressionJ.TAGS_SELECTION));
    simple.setClassifier(new LinearRegressionJ());
    GenericDouble ridge = new GenericDouble();
    ridge.setClassname(new BaseClassname(cls.getClass()));
    ridge.setProperty("ridge");
    ridge.setMinimum(1e-8);
    ridge.setMaximum(1);
    simple.setHandlers(new AbstractCatSwarmOptimizationDiscoveryHandler[]{
      ridge,
    });
    DoubleMatrix best = simple.run();
    System.out.println(best);
  }
}
