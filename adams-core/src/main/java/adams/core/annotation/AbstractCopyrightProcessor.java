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
 * AbstractCopyrightProcessor.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import adams.core.io.FileUtils;

/**
 * A processor that lists all classes/methods with mixed copyright.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see MixedCopyright
 */
public abstract class AbstractCopyrightProcessor<T extends Annotation>
  extends AbstractProcessor {

  /** whether the output represents the first one. */
  protected boolean m_IsFirst = true;

  /**
   * If the processor class is annotated with {@link
   * SupportedSourceVersion}, return the source version in the
   * annotation.  If the class is not so annotated, {@link
   * SourceVersion#RELEASE_6} is returned.
   *
   * @return the latest source version supported by this processor
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_7;
  }

  /**
   * If the processor class is annotated with {@link
   * SupportedOptions}, return an unmodifiable set with the same set
   * of strings as the annotation.  If the class is not so
   * annotated, an empty set is returned.
   *
   * @return the options recognized by this processor, or an empty
   * set if none
   */
  @Override
  public Set<String> getSupportedOptions() {
    return new HashSet<String>(Arrays.asList(new String[]{"module", "output", "printheader"}));
  }

  /**
   * If the processor class is annotated with {@link
   * SupportedAnnotationTypes}, return an unmodifiable set with the
   * same set of strings as the annotation.  If the class is not so
   * annotated, an empty set is returned.
   *
   * @return the names of the annotation types supported by this
   * processor, or an empty set if none
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return new HashSet<String>(Arrays.asList(new String[]{getAnnotationClass().getName()}));
  }
  
  /**
   * Returns the annotation class to process.
   * 
   * @return		the class
   */
  protected abstract Class getAnnotationClass();
  
  /**
   * Returns the output file to write the information to.
   * 
   * @param prefix	the path/filename prefix
   * @return		the full path/filename
   */
  protected abstract String getOutputFile(String prefix);
  
  /**
   * Returns the header row for the tab-separated output file.
   * 
   * @return		the header row
   */
  protected abstract String getHeaderRow();
  
  /**
   * Returns a data row for a specific annotation.
   * 
   * @param copyright	the copyright annotation to use
   * @param module	the module to use
   * @param cls		the class the annotation is located in
   * @param method	the method the annotation is located in, empty string if not applicable
   */
  protected abstract String getDataRow(T copyright, String module, String cls, String method);
  
  /**
   * Processes a set of annotation types on type elements
   * originating from the prior round and returns whether or not
   * these annotations are claimed by this processor.  If {@code
   * true} is returned, the annotations are claimed and subsequent
   * processors will not be asked to process them; if {@code false}
   * is returned, the annotations are unclaimed and subsequent
   * processors may be asked to process them.  A processor may
   * always return the same boolean value or may vary the result
   * based on chosen criteria.
   *
   * <p>The input set will be empty if the processor supports {@code
   * "*"} and the root elements have no annotations.  A {@code
   * Processor} must gracefully handle an empty set of annotations.
   *
   * @param annotations the annotation types requested to be processed
   * @param roundEnv  environment for information about the current and prior round
   * @return whether or not the set of annotations are claimed by this processor
   */
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    String		cls;
    String		method;
    T			copyright;
    String		output;
    String		module;
    Map<String,String>	options;
    String		line;
    boolean		printHeader;
    
    options     = processingEnv.getOptions();
    module      = options.containsKey("module") ? options.get("module") : "N/A";
    printHeader = (options.get("printheader") != null) && (options.get("printheader").equalsIgnoreCase("true"));
    output      = options.get("output");
    if (output != null)
      output = getOutputFile(output);

    for (Element el: (Set<? extends Element>)roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
      copyright = (T) el.getAnnotation(getAnnotationClass());

      if (el.getKind() == ElementKind.CLASS) {
	cls    = el.asType().toString();
	method = "";
      }
      else {
	cls    = el.getEnclosingElement().asType().toString();
	method = el.getSimpleName().toString();
      }

      line = getDataRow(copyright, module, cls, method);

      if (output == null) {
	if (m_IsFirst && printHeader)
	  System.out.println(getHeaderRow());
	System.out.println(line);
      }
      else {
	if (m_IsFirst && printHeader)
	  FileUtils.writeToFile(output, getHeaderRow(), false);
	FileUtils.writeToFile(output, line, true);
      }

      m_IsFirst = false;
    }

    return true;
  }
}