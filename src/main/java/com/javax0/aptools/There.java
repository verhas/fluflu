package com.javax0.aptools;

/**
 * Helper class to check object and string existence.
 * 
 */
public class There {

  /**
   * Checks that the string exists
   * 
   * @param s
   *          is the string to check
   * @return {@code true} if the string is not null and is not zero length
   */
  public static boolean is(String s) {
    return s != null && s.length() > 0;
  }

  /**
   * Checks that the object exists.
   * 
   * @param s
   *          the object
   * @return {@code true} if the object is not null
   */
  public static boolean is(Object s) {
    return s != null;
  }
}
