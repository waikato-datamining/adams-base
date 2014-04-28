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
 * Classifier.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.ml;

import java.util.Vector;

/**
 * 
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
abstract public class Classifier {

  /** List of attributes to use in modeling. */
  protected Vector<String> m_attributes;

  /** Target attribute. */
  protected String m_class;
  public class ClassificationResult{
    protected Double m_classification;
    protected String m_classificationS;
    protected String m_result;
    public ClassificationResult(String re){
      m_result=re;
    }
    public ClassificationResult(String re,Double d){
      m_result=re;
      m_classification=d;
      m_classificationS="";
    }
    public ClassificationResult(String re,Double d,String s){
      m_result=re;
      m_classification=d;
      m_classificationS=s;
    }

    public String toString(){
      return(m_result+":"+m_classification+":"+m_classificationS);
    }

  }
  public class BuildResult{
    protected String m_result;
    public BuildResult(String re){
      m_result=re;
    }
  }

  abstract BuildResult build(String classv);
  abstract BuildResult build(Vector<String> attributes, String classv);
  abstract BuildResult build(String[] srta, String classv);
  abstract ClassificationResult classify(DataRow row);
  public Vector<String> getOrder(){
    return(m_attributes);
  }
  public void setAttributes(String[] atts){
    m_attributes=new Vector<String>();
    if (atts != null){
      for (int i=0;i<atts.length;i++){
	m_attributes.add(atts[i]);
      }
    }
  }

}
