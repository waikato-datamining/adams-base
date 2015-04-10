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
 * LeastMedianSq.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import adams.core.option.WekaCommandLineHandler;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Uses base classifier to get guess, then get prediction from either lo/hi classifier
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> -S &lt;dbl&gt;
 *  Lowest value for hi classifier
 *  (default: 10)
 * </pre>
 * 
 * <pre> -P &lt;dbl&gt;
 *  Highest value for lo classifier
 *  (default: 15)
 * </pre>
 * 
 * <pre> -E &lt;dbl&gt;
 *  Decision boundary for hi/lo classifier
 *  (default: 12.5)
 * </pre>
 * 
 * <pre> -F
 *  Full name of hi classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> -G
 *  Full name of lo classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author Dale?
 * @version $Revision: 6936 $
 */
public class HighLowSplitSingleClassifier
extends SingleClassifierEnhancer
implements WeightedInstancesHandler{

	/** suid	 */
	private static final long serialVersionUID = -1948204213212845838L;


	protected Classifier m_loClassifier = new ZeroR();

	protected double m_loHipoint = 15.0;

	protected double m_splitpoint = 12.5;

	/**
	 * Returns a string describing classifier.
	 *
	 * @return 		a description suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Uses base classifier to get guess, then get prediction from either lo/hi classifier";
	}

	/**
	 * String describing default classifier.
	 */
	protected String defaultClassifierString() {
		return "weka.classifiers.rules.ZeroR";
	}

	/**
	 * String describing options for default classifier.
	 */
	protected String[] defaultClassifierOptions() {
		return new String[0];
	}

	/**
	 * Returns an enumeration describing the available options.
	 *
	 * @return an enumeration of all the available options.
	 */
	public Enumeration listOptions() {
		Vector newVector = new Vector();

		Enumeration enu = super.listOptions();
		while (enu.hasMoreElements()) {
			newVector.addElement(enu.nextElement());
		}

		newVector.addElement(new Option("\tHighest value for lo classifier\n"
				+ "\t(default: 15)\n",
				"P", 1, "-P <dbl>"));
		newVector.addElement(new Option("\tDecision boundary for hi/lo classifier\n"
				+ "\t(default: 12.5)\n",
				"E", 1, "-E <dbl>"));


		return newVector.elements();
	}

	/**
	 * Sets the OptionHandler's options using the given list. All options
	 * will be set (or reset) during this call (i.e. incremental setting
	 * of options is not possible).
	 *
   <!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -W
	 *  Full name of base classifier.
	 *  (default: weka.classifiers.rules.ZeroR)</pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> 
	 * Options specific to classifier weka.classifiers.rules.ZeroR:
	 * </pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> -S &lt;dbl&gt;
	 *  Lowest value for hi classifier
	 *  (default: 10)
	 * </pre>
	 * 
	 * <pre> -P &lt;dbl&gt;
	 *  Highest value for lo classifier
	 *  (default: 15)
	 * </pre>
	 * 
	 * <pre> -E &lt;dbl&gt;
	 *  Decision boundary for hi/lo classifier
	 *  (default: 12.5)
	 * </pre>
	 * 
	 * <pre> -F
	 *  Full name of hi classifier.
	 *  (default: weka.classifiers.rules.ZeroR)</pre>
	 * 
	 * <pre> -W
	 *  Full name of base classifier.
	 *  (default: weka.classifiers.rules.ZeroR)</pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> 
	 * Options specific to classifier weka.classifiers.rules.ZeroR:
	 * </pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> 
	 * Options specific to classifier weka.classifiers.rules.ZeroR:
	 * </pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> -G
	 *  Full name of lo classifier.
	 *  (default: weka.classifiers.rules.ZeroR)</pre>
	 * 
	 * <pre> -W
	 *  Full name of base classifier.
	 *  (default: weka.classifiers.rules.ZeroR)</pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> 
	 * Options specific to classifier weka.classifiers.rules.ZeroR:
	 * </pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
	 * <pre> 
	 * Options specific to classifier weka.classifiers.rules.ZeroR:
	 * </pre>
	 * 
	 * <pre> -output-debug-info
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -do-not-check-capabilities
	 *  If set, classifier capabilities are not checked before classifier is built
	 *  (use with caution).</pre>
	 * 
   <!-- options-end -->
	 *
	 * @param options the list of options as an array of strings
	 * @throws Exception if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {

		String curropt = Utils.getOption('P', options);
		if (curropt.length() != 0) {
			setLoHipoint(Double.parseDouble(curropt));
		} else {
			setLoHipoint(15.0);
		}

		curropt = Utils.getOption('E', options);
		if (curropt.length() != 0) {
			setSplitpoint(Double.parseDouble(curropt));
		} else {
			setSplitpoint(12.5);
		}

		super.setOptions(options);

		
	}

	/**
	 * Gets the current option settings for the OptionHandler.
	 *
	 * @return the list of current option settings as an array of strings
	 */
	public String[] getOptions() {
		List<String> result = new ArrayList<>();


		result.add("-P");
		result.add("" + getLoHipoint());

		result.add("-E");
		result.add("" + getSplitpoint());

		for (String option: super.getOptions())
			result.add(option);

		return result.toArray(new String[result.size()]);
	}


	public double getLoHipoint(){
		return m_loHipoint;
	}

	public void setLoHipoint(double l){
		m_loHipoint=l;
	}

	public String loHipointTipText(){
		return "low-high point";
	}

	public double getSplitpoint(){
		return(m_splitpoint);
	}

	public void setSplitpoint(double s){
		m_splitpoint = s;
	}

	public String splitpointTipText(){
		return "split point";
	}



	/**
	 * Returns default capabilities of the base classifier.
	 *
	 * @return      the capabilities of the base classifier
	 */
	public Capabilities getCapabilities() {
		Capabilities        result;

		result = super.getCapabilities();
		result.setMinimumNumberInstances(1);

		return result;
	}

	/**
	 * Builds the classifier.
	 *
	 * @param data	the training data
	 * @throws Exception	if something goes wrong
	 */
	public void buildClassifier(Instances data) throws Exception {
		getCapabilities().testWithFail(data);
		
		LogTargetRegressor ltr=new LogTargetRegressor();
		Classifier c=AbstractClassifier.makeCopy(getClassifier());
		ltr.setClassifier(c);
		m_loClassifier=ltr;
		

		// build overall
		m_Classifier.buildClassifier(data);

		// split into hi/lo

		Instances lo = new Instances(data,0);

		for (Instance i:data){
			if (i.classValue() <= m_loHipoint){
				Instance in=new DenseInstance(i);
				in.setDataset(lo);
				lo.add(in);
			}
		}
		

		//TODO perhaps if fail, use global classifier?
		m_loClassifier.buildClassifier(lo);
	}

	/**
	 * Returns the prediction.
	 */
	public double classifyInstance(Instance inst) throws Exception {
		double value = m_Classifier.classifyInstance(inst);
		if (value < getSplitpoint()) {
			return(m_loClassifier.classifyInstance(inst));
		} else {
			return(value);
		}
	}

	/**
	 * Returns description of classifier.
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();

		
		result.append("Low-High point: " + getLoHipoint() + "\n");

		result.append("\nClassifier:\n" + m_Classifier + "\n");
		result.append("\nBelow " + m_splitpoint + ":\n" + m_loClassifier + "\n");
		result.append("\nElse:\n" + m_Classifier);

		return result.toString();
	}

	public String getRevision() {
		return RevisionUtils.extract("$Revision: 6936 $");
	}

	/**
	 * Main method for running this class.
	 *
	 * @param argv the options
	 */
	public static void main(String[] argv) {
		runClassifier(new HighLowSplit(), argv);
	}
}

