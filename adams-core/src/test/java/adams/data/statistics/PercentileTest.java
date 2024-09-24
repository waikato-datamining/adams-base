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
 * PercentileTest.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tests the adams.data.statistics.Percentile class. Run from commandline with: <br><br>
 * java adams.data.statistics.PercentileTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PercentileTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PercentileTest(String name) {
    super(name);
  }

  /**
   * Tests the quartiles for Integer objects.
   */
  public void testInteger() {
    List<Integer>	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<Integer>	q;
    Integer		q1;
    Integer		q3;

    values = new ArrayList<>();
    for (i = 1; i <= 100; i++)
      values.add(i);

    q    = new Percentile<>();
    rand = new Random(1);
    while (!values.isEmpty()) {
      index = rand.nextInt(values.size());
      q.add(values.get(index));
      values.remove(index);
    }

    q1 = q.getPercentile(0.25);
    q3 = q.getPercentile(0.75);

    assertEquals((Integer) 25, q1);
    assertEquals((Integer) 75, q3);
  }

  /**
   * Tests the quartiles for Double objects.
   */
  public void testDouble() {
    List<Double> 	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<Double>	q;
    Double		q1;
    Double		q3;

    values = new ArrayList<>();
    for (i = 1; i <= 100; i++)
      values.add(((double) i) / 10.0);

    q    = new Percentile<>();
    rand = new Random(1);
    while (!values.isEmpty()) {
      index = rand.nextInt(values.size());
      q.add(values.get(index));
      values.remove(index);
    }

    q1 = q.getPercentile(0.25);
    q3 = q.getPercentile(0.75);

    assertEquals(2.5, q1);
    assertEquals(7.5, q3);
  }

  /**
   * Tests the quartiles for String objects.
   */
  public void testString() {
    List<String>	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<String>	q;
    String		q1;
    String		q3;

    values = new ArrayList<>();
    for (i = 1; i <= 100; i++)
      values.add(Integer.toString(1000 + i));

    q    = new Percentile<>();
    rand = new Random(1);
    while (!values.isEmpty()) {
      index = rand.nextInt(values.size());
      q.add(values.get(index));
      values.remove(index);
    }

    q1 = q.getPercentile(0.25);
    q3 = q.getPercentile(0.75);

    assertEquals(Integer.toString(1000 + 25), q1);
    assertEquals(Integer.toString(1000 + 75), q3);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PercentileTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
