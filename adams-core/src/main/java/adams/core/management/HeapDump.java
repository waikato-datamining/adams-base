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
 * HeapDump.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.core.Utils;
import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * Helper class for generating heap dumps.
 * Based on idea from here:
 * https://blogs.oracle.com/sundararajan/programmatically-dumping-heap-from-java-applications
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HeapDump {

  /** the hotspot bean for generating the dump. */
  public final static String DIAGNOSTIC_NAME = "com.sun.management:type=HotSpotDiagnostic";

  /** the singleton instance. */
  protected static HotSpotDiagnosticMXBean DIAGNOSTIC_INSTANCE;

  /** whether the bean has been instantiated. */
  protected static boolean INSTANTIATED;

  /**
   * Initializes the diagnostic bean if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  protected static String initDiagnosticBean() {
    MBeanServer		server;

    if (DIAGNOSTIC_INSTANCE != null)
      return null;

    if (INSTANTIATED)
      return "Failed to instantiate diagnostic bean for heapdumps before, canceling!";

    INSTANTIATED = true;
    try {
      server = ManagementFactory.getPlatformMBeanServer();
      DIAGNOSTIC_INSTANCE = ManagementFactory.newPlatformMXBeanProxy(
        server, DIAGNOSTIC_NAME, HotSpotDiagnosticMXBean.class);
      return null;
    }
    catch (Exception e) {
      return "Failed to initialize diagnostic bean '" + DIAGNOSTIC_NAME + "':\n"
	+ Utils.throwableToString(e);
    }
  }

  /**
   * Generates a heap dump.
   *
   * @param output	the file to save it to (.hprof extension)
   * @param live	whether to use only live or all objects
   * @return		null if successful, otherwise error message
   */
  public synchronized static String generate(File output, boolean live) {
    String	result;

    result = initDiagnosticBean();
    if (result == null) {
      try {
	DIAGNOSTIC_INSTANCE.dumpHeap(output.getAbsolutePath(), live);
      }
      catch (Exception e) {
        result = "Failed to generate heapdump file (live=" + live + "): " + output + "\n"
	  + Utils.throwableToString(e);
      }
    }

    return result;
  }
}
