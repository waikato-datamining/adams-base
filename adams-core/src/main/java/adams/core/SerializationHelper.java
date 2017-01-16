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
 * SerializationHelper.java
 * Copyright (C) 2007-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A helper class for determining serialVersionUIDs and checking whether
 * classes contain one and/or need one. One can also serialize and deserialize
 * objects to and fro files or streams.
 * <br><br>
 * Based on WEKA's weka.core.SerializationHelper
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.core.SerializationHelper
 */
public class SerializationHelper {

  /** the field name of serialVersionUID. */
  public final static String SERIAL_VERSION_UID = "serialVersionUID";

  /**
   * checks whether a class is serializable.
   *
   * @param classname	the class to check
   * @return		true if the class or one of its ancestors implements
   * 			the Serializable interface, otherwise false (also if
   * 			the class cannot be loaded)
   */
  public static boolean isSerializable(String classname) {
    boolean	result;

    try {
      result = isSerializable(Class.forName(classname));
    }
    catch (Exception e) {
      result = false;
    }

    return result;
  }

  /**
   * checks whether a class is serializable.
   *
   * @param c		the class to check
   * @return		true if the class or one of its ancestors implements
   * 			the Serializable interface, otherwise false
   */
  public static boolean isSerializable(Class c) {
    return ClassLocator.hasInterface(Serializable.class, c);
  }

  /**
   * checks whether the given class contains a serialVersionUID.
   *
   * @param classname	the class to check
   * @return		true if the class contains a serialVersionUID,
   * 			otherwise false (also if the class is not
   * 			implementing serializable or cannot be loaded)
   */
  public static boolean hasUID(String classname) {
    boolean	result;

    try {
      result = hasUID(Class.forName(classname));
    }
    catch (Exception e) {
      result = false;
    }

    return result;
  }

  /**
   * checks whether the given class contains a serialVersionUID.
   *
   * @param c		the class to check
   * @return		true if the class contains a serialVersionUID,
   * 			otherwise false (also if the class is not
   * 			implementing serializable)
   */
  public static boolean hasUID(Class c) {
    boolean	result;

    result = false;

    if (isSerializable(c)) {
      try {
	c.getDeclaredField(SERIAL_VERSION_UID);
	result = true;
      }
      catch (Exception e) {
	result = false;
      }
    }

    return result;
  }

  /**
   * checks whether a class needs to declare a serialVersionUID, i.e., it
   * implements the java.io.Serializable interface but doesn't declare a
   * serialVersionUID.
   *
   * @param classname	the class to check
   * @return		true if the class needs to declare one, false otherwise
   * 			(also if the class cannot be loaded!)
   */
  public static boolean needsUID(String classname) {
    boolean	result;

    try {
      result = needsUID(Class.forName(classname));
    }
    catch (Exception e) {
      result = false;
    }

    return result;
  }

  /**
   * checks whether a class needs to declare a serialVersionUID, i.e., it
   * implements the java.io.Serializable interface but doesn't declare a
   * serialVersionUID.
   *
   * @param c		the class to check
   * @return		true if the class needs to declare one, false otherwise
   */
  public static boolean needsUID(Class c) {
    boolean	result;

    if (isSerializable(c))
      result = !hasUID(c);
    else
      result = false;

    return result;
  }

  /**
   * reads or creates the serialVersionUID for the given class.
   *
   * @param classname	the class to get the serialVersionUID for
   * @return		the UID, 0L for non-serializable classes (or if the
   * 			class cannot be loaded)
   */
  public static long getUID(String classname) {
    long	result;

    try {
      result = getUID(Class.forName(classname));
    }
    catch (Exception e) {
      result = 0L;
    }

    return result;
  }

  /**
   * reads or creates the serialVersionUID for the given class.
   *
   * @param c		the class to get the serialVersionUID for
   * @return		the UID, 0L for non-serializable classes
   */
  public static long getUID(Class c) {
    return ObjectStreamClass.lookup(c).getSerialVersionUID();
  }

  /**
   * Serializes the given object to the specified file.
   *
   * @param filename	the file to write the object to
   * @param o		the object to serialize
   * @throws Exception	if serialization fails
   */
  public static void write(String filename, Object o) throws Exception {
    FileOutputStream 	fos;
    GZIPOutputStream	gos;

    fos = new FileOutputStream(filename);
    gos = null;
    try {
      if (filename.endsWith(".gz")) {
	gos = new GZIPOutputStream(fos);
	write(gos, o);
      }
      else {
	write(fos, o);
      }
    }
    finally {
      if (gos != null)
	FileUtils.closeQuietly(gos);
      else
	FileUtils.closeQuietly(fos);
    }
  }

  /**
   * Serializes the given object to the specified stream.
   * Does not close the stream.
   *
   * @param stream	the stream to write the object to
   * @param o		the object to serialize
   * @throws Exception	if serialization fails
   */
  public static void write(OutputStream stream, Object o) throws Exception {
    ObjectOutputStream	oos;

    if (!(stream instanceof BufferedOutputStream))
      stream = new BufferedOutputStream(stream);

    oos = new ObjectOutputStream(stream);
    oos.writeObject(o);
    oos.flush();
  }

  /**
   * Serializes the given objects to the specified file.
   *
   * @param filename	the file to write the object to
   * @param o		the objects to serialize
   * @throws Exception	if serialization fails
   */
  public static void writeAll(String filename, Object[] o) throws Exception {
    FileOutputStream	fos;
    GZIPOutputStream	gos;

    fos = new FileOutputStream(filename);
    gos = null;
    try {
      if (filename.endsWith(".gz")) {
	gos = new GZIPOutputStream(fos);
	writeAll(gos, o);
      }
      else {
	writeAll(fos, o);
      }
    }
    finally {
      if (gos != null)
	FileUtils.closeQuietly(gos);
      else
	FileUtils.closeQuietly(fos);
    }
  }

  /**
   * Serializes the given objects to the specified stream.
   * Does not close the stream.
   *
   * @param stream	the stream to write the object to
   * @param o		the objects to serialize
   * @throws Exception	if serialization fails
   */
  public static void writeAll(OutputStream stream, Object[] o) throws Exception {
    ObjectOutputStream	oos;
    int			i;

    if (!(stream instanceof BufferedOutputStream))
      stream = new BufferedOutputStream(stream);

    oos = new ObjectOutputStream(stream);
    try {
      for (i = 0; i < o.length; i++)
	oos.writeObject(o[i]);
    }
    finally {
      try {
	oos.flush();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Deserializes the given file and returns the object from it.
   *
   * @param filename	the file to deserialize from
   * @return		the deserialized object
   * @throws Exception	if deserialization fails
   */
  public static Object read(String filename) throws Exception {
    Object		result;
    FileInputStream	fis;
    GZIPInputStream	gis;

    fis = new FileInputStream(filename);
    gis = null;
    try {
      if (filename.endsWith(".gz")) {
	gis    = new GZIPInputStream(fis);
	result = read(gis);
      }
      else
	result = read(fis);
    }
    finally {
      if (gis != null)
	FileUtils.closeQuietly(gis);
      else
	FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Deserializes from the given stream and returns the object from it.
   * Does not close the stream.
   *
   * @param stream	the stream to deserialize from
   * @return		the deserialized object
   * @throws Exception	if deserialization fails
   */
  public static Object read(InputStream stream) throws Exception {
    ObjectInputStream 	ois;
    Object		result;

    if (!(stream instanceof BufferedInputStream))
      stream = new BufferedInputStream(stream);

    ois    = new ObjectInputStream(stream);
    result = ois.readObject();

    return result;
  }

  /**
   * deserializes the given file and returns the objects from it.
   *
   * @param filename	the file to deserialize from
   * @return		the deserialized objects
   * @throws Exception	if deserialization fails
   */
  public static Object[] readAll(String filename) throws Exception {
    Object[]		result;
    InputStream 	fis;
    GZIPInputStream	gis;

    fis = new FileInputStream(filename);
    gis = null;
    try {
      if (filename.endsWith(".gz")) {
	gis    = new GZIPInputStream(fis);
	result = readAll(gis);
      }
      else {
	result = readAll(fis);
      }
    }
    finally {
      if (gis != null)
	FileUtils.closeQuietly(gis);
      else
	FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * deserializes from the given stream and returns the object from it.
   *
   * @param stream	the stream to deserialize from
   * @return		the deserialized object
   * @throws Exception	if deserialization fails
   */
  public static Object[] readAll(InputStream stream) throws Exception {
    ObjectInputStream 	ois;
    List<Object>	result;

    if (!(stream instanceof BufferedInputStream))
      stream = new BufferedInputStream(stream);

    result = new ArrayList<>();
    ois    = new ObjectInputStream(stream);
    try {
      while (true) {
	result.add(ois.readObject());
      }
    }
    catch (Exception e) {
      // ignored
    }
    finally {
      FileUtils.closeQuietly(ois);
    }

    return result.toArray(new Object[result.size()]);
  }

  /**
   * Serializes the given object into a byte array.
   *
   * @param o		the object to serialize
   * @return		the byte array
   * @throws Exception	if serialization fails
   */
  public static byte[] toByteArray(Object o) throws Exception {
    ByteArrayOutputStream	bos;
    ObjectOutputStream		oos;

    bos = new ByteArrayOutputStream();
    oos = new ObjectOutputStream(bos);
    try {
      oos.writeObject(o);
    }
    finally {
      FileUtils.closeQuietly(oos);
    }

    return bos.toByteArray();
  }

  /**
   * Serializes the given object into a byte array.
   *
   * @param os		the objects to serialize
   * @return		the byte array
   * @throws Exception	if serialization fails
   */
  public static byte[] toByteArray(Object[] os) throws Exception {
    ByteArrayOutputStream	bos;
    ObjectOutputStream		oos;

    bos = new ByteArrayOutputStream();
    oos = new ObjectOutputStream(bos);
    try {
      for (Object o : os)
	oos.writeObject(o);
    }
    finally {
      FileUtils.closeQuietly(oos);
    }

    return bos.toByteArray();
  }

  /**
   * Deserializes from the given byte array and returns the objects from it.
   *
   * @param data	the byte array to deserialize from
   * @return		the deserialized objects
   * @throws Exception	if deserialization fails
   */
  public static Object[] fromByteArray(byte[] data) throws Exception {
    return readAll(new ByteArrayInputStream(data));
  }

  /**
   * Outputs information about a class on the commandline, takes class
   * name as arguments.
   *
   * @param args	the classnames to check
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("\nUsage: " + SerializationHelper.class.getName() + " classname [classname [classname [...]]]\n");
      System.exit(1);
    }

    // check all the classes
    System.out.println();
    for (int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
      System.out.println("- is serializable: " + isSerializable(args[i]));
      System.out.println("- has " + SERIAL_VERSION_UID + ": " + hasUID(args[i]));
      System.out.println("- needs " + SERIAL_VERSION_UID + ": " + needsUID(args[i]));
      System.out.println("- " + SERIAL_VERSION_UID + ": private static final long serialVersionUID = " + getUID(args[i]) + "L;");
      System.out.println();
    }
  }
}
