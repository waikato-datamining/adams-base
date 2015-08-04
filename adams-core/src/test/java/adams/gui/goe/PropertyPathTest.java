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
 * PropertyPathTest.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.util.ArrayList;

import adams.core.discovery.PropertyPath;
import adams.core.logging.LoggingLevel;
import adams.data.filter.AbstractFilter;
import adams.data.filter.BaselineCorrection;
import adams.data.filter.MultiFilter;
import adams.data.filter.NamedSetup;
import adams.data.filter.RemoveNoise;
import adams.data.filter.ReportFilter;
import adams.core.discovery.PropertyPath.Path;
import adams.test.AdamsTestCase;

/**
 * Tests the {@link PropertyPath} class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertyPathTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public PropertyPathTest(String name) {
    super(name);
  }
  
  /**
   * Tests the {@link Path} class.
   */
  public void testPath() {
    String p;
    Path path;
    
    p = "get(1).get(3).blah";
    path = new Path(p);
    assertEquals("path differs", p, path.toString());

    try {
      p = "get(1).(3).blah";
      path = new Path(p);
      fail("Should have failed, since invalid path ('get' must be used for lists)");
    }
    catch (IllegalArgumentException e) {
      // expected outcome
    }
    
    p = "hello.world[2].nothing";
    path = new Path(p);
    assertEquals("path differs", p, path.toString());
    assertEquals("number of elements differ", 3, path.size());
    String[] name = new String[]{"hello", "world", "nothing"};
    int[] index = new int[]{-1, 2, -1};
    for (int i = 0; i < path.size(); i++) {
      assertEquals("path element #" + i + " differs: name", name[i], path.get(i).getName());
      assertEquals("path element #" + i + " differs: index", index[i], path.get(i).getIndex());
    }
  }
  
  /**
   * Tests the getValue method.
   */
  public void testGetValue() {
    BaselineCorrection base = new BaselineCorrection();
    RemoveNoise noise = new RemoveNoise();
    ReportFilter report = new ReportFilter();
    MultiFilter multi = new MultiFilter();
    multi.setSubFilters(new AbstractFilter[]{
	base,
	noise,
	report
    });
    
    Path path;
    Object obj;
    
    path = new Path("subFilters[1]");
    obj = PropertyPath.getValue(multi, path);
    assertEquals("objects differ: " + path, noise, obj);
    
    path = new Path("subFilters[0].baselineCorrection.loggingLevel");
    obj = PropertyPath.getValue(multi, path);
    assertEquals("objects differ: " + path, base.getBaselineCorrection().getLoggingLevel(), obj);
    
    // TODO access list item via [index]
  }
  
  /**
   * Tests the getValue method, with lists.
   */
  public void testGetValueFromList() {
    ArrayList list = new ArrayList();
    list.add("0");
    list.add("1");
    list.add("2");
    ArrayList nested1 = new ArrayList();
    list.add(nested1);
    nested1.add("3.0");
    nested1.add("3.1");
    ArrayList nested2 = new ArrayList();
    nested1.add(nested2);
    nested2.add("3.2.0");
    nested2.add("3.2.1");
    nested1.add("3.3");
    list.add("4");
    
    Path path;
    Object obj;
    
    path = new Path("get(0)");
    obj = PropertyPath.getValue(list, path);
    assertEquals("element differs", "0", obj);
    
    path = new Path("get(3)");
    obj = PropertyPath.getValue(list, path);
    assertEquals("element differs", nested1, obj);
    
    path = new Path("get(3).get(2)");
    obj = PropertyPath.getValue(list, path);
    assertEquals("element differs", nested2, obj);
    
    path = new Path("get(3).get(2).get(1)");
    obj = PropertyPath.getValue(list, path);
    assertEquals("element differs", "3.2.1", obj);
  }
  
  /**
   * Tests the setValue method.
   */
  public void testSetValue() {
    BaselineCorrection base = new BaselineCorrection();
    RemoveNoise noise = new RemoveNoise();
    ReportFilter report = new ReportFilter();
    MultiFilter multi = new MultiFilter();
    multi.setSubFilters(new AbstractFilter[]{
	base,
	noise,
	report
    });
    
    Path path;
    Object obj;
    NamedSetup named = new NamedSetup();
    named.setLoggingLevel(LoggingLevel.INFO);
    
    // old debug level
    path = new Path("subFilters[1].loggingLevel");
    obj = PropertyPath.getValue(multi, path);
    assertEquals("objects differ: " + path, base.getLoggingLevel(), obj);
    
    // set new filter at index 1
    path = new Path("subFilters[1]");
    PropertyPath.setValue(multi, path, named);
    obj = PropertyPath.getValue(multi, path);
    assertEquals("objects differ: " + path, named, obj);
    
    // new debug level
    path = new Path("subFilters[1].loggingLevel");
    obj = PropertyPath.getValue(multi, path);
    assertEquals("objects differ: " + path, named.getLoggingLevel(), obj);
  }
}
