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
 * InstancesIndexedSplitsRunsCompatibility.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.MetaData;
import adams.data.io.input.PropertiesIndexedSplitsRunsReader;
import adams.env.Environment;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;

/**
 * Performs compatibility tests between indexed splits configurations and Weka Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesIndexedSplitsRunsCompatibility
  extends AbstractIndexedSplitsRunsCompatibility {

  private static final long serialVersionUID = -5319544776172513377L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs compatibility tests between indexed splits configurations and Weka Instances objects.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String lenientTipText() {
    return "If enabled, only the number of instances are used in the compatibility tests.";
  }

  /**
   * Checks whether the data is compatible with the indexed splits.
   *
   * @param data the data to check
   * @param runs the indexed splits to compare against
   * @return null if successfully passed checks, otherwise error message
   */
  @Override
  public String isCompatible(Object data, IndexedSplitsRuns runs) {
    Instances	inst;
    MetaData	meta;
    int		numInst;
    int		numAtts;
    int		i;
    String	name;
    String	type;
    String 	nameMeta;
    String 	typeMeta;
    String	nameAct;
    String	typeAct;

    meta = runs.getMetaData();
    inst = (Instances) data;

    if (meta.containsKey(InstancesIndexedSplitsRunsGenerator.DATASET_NUMINSTANCES)) {
      numInst = meta.getInteger(InstancesIndexedSplitsRunsGenerator.DATASET_NUMINSTANCES, -1);
      if (numInst > -1) {
        if (numInst != inst.numInstances())
          return "Number of instances differ (meta-data vs current data): " + numInst + " != " + inst.numInstances();
      }
    }

    if (m_Lenient)
      return null;

    if (meta.containsKey(InstancesIndexedSplitsRunsGenerator.DATASET_NUMATTRIBUTES)) {
      numAtts = meta.getInteger(InstancesIndexedSplitsRunsGenerator.DATASET_NUMATTRIBUTES, -1);
      if (numAtts > -1) {
        if (numAtts != inst.numAttributes())
          return "Number of attributes differ (meta-data vs current data): " + numAtts + " != " + inst.numAttributes();
      }

      for (i = 0; i < inst.numAttributes(); i++) {
        name = InstancesIndexedSplitsRunsGenerator.PREFIX_DATASET_ATTRIBUTE + i + InstancesIndexedSplitsRunsGenerator.SUFFIX_NAME;
        type = InstancesIndexedSplitsRunsGenerator.PREFIX_DATASET_ATTRIBUTE + i + InstancesIndexedSplitsRunsGenerator.SUFFIX_TYPE;
        if (meta.containsKey(name) && meta.containsKey(type)) {
          nameAct  = inst.attribute(i).name();
          typeAct  = Attribute.typeToStringShort(inst.attribute(i).type());
          nameMeta = meta.getString(name, "");
          typeMeta = meta.getString(type, "");
          if (!nameMeta.equals(nameAct))
            return "Names of attribute #" + i + "differ (meta-data vs current data): " + nameMeta + " != " + nameAct;
          if (!typeMeta.equals(typeAct))
            return "Types of attribute #" + i + "differ (meta-data vs current data): " + typeMeta + " != " + typeAct;
	}
	else {
          if (isLoggingEnabled()) {
	    if (!meta.containsKey(name))
	      getLogger().info("Missing key in meta-data: " + name);
	    if (!meta.containsKey(type))
	      getLogger().info("Missing key in meta-data: " + type);
	  }
	}
      }
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);

    Instances data = DataSource.read("/home/fracpete/temp/Fe.arff");
    data.setClassIndex(data.numAttributes() - 1);

    IndexedSplitsRuns runs;
    MessageCollection errors = new MessageCollection();
    PropertiesIndexedSplitsRunsReader reader = new PropertiesIndexedSplitsRunsReader();
    runs = reader.read(new File("/home/fracpete/temp/out.props"), errors);
    //JsonIndexedSplitsRunsReader reader = new JsonIndexedSplitsRunsReader();
    //runs = reader.read(new File("/home/fracpete/temp/out.json"), errors);

    String msg;
    InstancesIndexedSplitsRunsCompatibility comp = new InstancesIndexedSplitsRunsCompatibility();
    comp.setLenient(true);
    msg = comp.isCompatible(data, runs);
    System.out.println("lenient: " + msg);

    comp.setLenient(false);
    msg = comp.isCompatible(data, runs);
    System.out.println("strict: " + msg);
  }
}
