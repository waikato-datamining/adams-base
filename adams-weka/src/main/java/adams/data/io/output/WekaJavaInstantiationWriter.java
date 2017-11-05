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
 * WekaJavaInstantiationWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.input.AbstractObjectReader;
import weka.core.code.Instantiation;

/**
 * Generates Java code from {@link weka.core.OptionHandler} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaJavaInstantiationWriter
  extends AbstractObjectWriter {

  private static final long serialVersionUID = -6587359770880068492L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates Java code from Weka " + Utils.classToString(weka.core.OptionHandler.class) + " objects.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Java source code (Weka)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"java"};
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractObjectReader getCorrespondingReader() {
    return null;
  }

  /**
   * Performs checks.
   *
   * @param obj	the object to check
   */
  protected void check(Object obj) {
    super.check(obj);
    if (!(obj instanceof weka.core.OptionHandler))
      throw new IllegalStateException("Object does not implement " + Utils.classToString(weka.core.OptionHandler.class));
  }

  /**
   * Performs the actual writing of the object file.
   *
   * @param file	the file to write to
   * @param obj	        the object to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, Object obj) {
    String		result;
    Instantiation	instantiate;
    String		code;
    StringBuilder	cls;
    String		simple;

    result      = null;
    instantiate = new Instantiation();
    code        = instantiate.convert(OptionUtils.getCommandLine(obj));

    if (code != null) {
      simple = FileUtils.replaceExtension(file.getName(), "");
      cls = new StringBuilder();
      cls.append("package adams;\n");
      cls.append("\n");
      cls.append("import weka.associations.*;\n");
      cls.append("import weka.attributeSelection.*;\n");
      cls.append("import weka.core.*;\n");
      cls.append("import weka.core.converters.*;\n");
      cls.append("import weka.core.expressionlanguage.*;\n");
      cls.append("import weka.core.json.*;\n");
      cls.append("import weka.core.logging.*;\n");
      cls.append("import weka.core.matrix.*;\n");
      cls.append("import weka.core.metastore.*;\n");
      cls.append("import weka.core.neighboursearch.*;\n");
      cls.append("import weka.core.packageManagement.*;\n");
      cls.append("import weka.core.pmml.*;\n");
      cls.append("import weka.core.scripting.*;\n");
      cls.append("import weka.core.stemmers.*;\n");
      cls.append("import weka.core.stopwords.*;\n");
      cls.append("import weka.core.tokenizers.*;\n");
      cls.append("import weka.core.xml.*;\n");
      cls.append("import weka.classifiers.bayes.*;\n");
      cls.append("import weka.classifiers.bayes.net.*;\n");
      cls.append("import weka.classifiers.evaluation.*;\n");
      cls.append("import weka.classifiers.functions.*;\n");
      cls.append("import weka.classifiers.lazy.*;\n");
      cls.append("import weka.classifiers.meta.*;\n");
      cls.append("import weka.classifiers.misc.*;\n");
      cls.append("import weka.classifiers.pmml.*;\n");
      cls.append("import weka.classifiers.rules.*;\n");
      cls.append("import weka.classifiers.scripting.*;\n");
      cls.append("import weka.classifiers.trees.*;\n");
      cls.append("import weka.classifiers.xml.*;\n");
      cls.append("import weka.clusterers.*;\n");
      cls.append("import weka.datagenerators.*;\n");
      cls.append("import weka.estimators.*;\n");
      cls.append("import weka.experiment.*;\n");
      cls.append("import weka.filters.*;\n");
      cls.append("import weka.filters.supervised.attribute.*;\n");
      cls.append("import weka.filters.supervised.instance.*;\n");
      cls.append("import weka.filters.unsupervised.attribute.*;\n");
      cls.append("import weka.filters.unsupervised.instance.*;\n");
      cls.append("\n");
      cls.append("public class ").append(simple).append(" {\n");
      cls.append("  \n");
      cls.append("  public static void main(String[] args) throws Exception {\n");
      cls.append(Utils.indent(code, 4));
      cls.append("  }\n");
      cls.append("}\n");
      if (!FileUtils.writeToFile(file.getAbsolutePath(), cls.toString(), false))
	result = "Failed to write Java code to: " + file;
    }
    else {
      result = "Failed to generate any code!";
    }

    return result;
  }
}
