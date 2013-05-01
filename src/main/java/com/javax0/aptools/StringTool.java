package com.javax0.aptools;

/**
 * A helper class that can be used to perform some annotation specific string
 * manipulations.
 * 
 * @author Peter Verhas
 * 
 */
public class StringTool {
  private final String s;

  StringTool(String s) {
    this.s = s;
  }

  /**
   * Removes the quotes from the string, thus {@code "apple"} becomes
   * {@code apple}, or writing the same in Java notation {@code "\"apple\""}
   * becomes {@code "apple"}.
   * <p>
   * Note that this method simply chops off the first and the last characters.
   * Thus {@code apple} will become {@code ppl}. If there are less than two
   * characters in the string then RuntimeException will happen.
   * 
   * @return the string without the quotes.
   */
  public String unquoted() {
    return s.substring(1, s.length() - 1);
  }

  /**
   * Replace the placeholders with the actual strings.
   * <p>
   * This method is to be used to replace placeholders in templates with the
   * actual values. Placeholders are strings in the template that start and end
   * with the {@code #} character and there is a name between the two {@code #}
   * characters. For example {@code #packageName#} is a typical placeholder that
   * is presumably replaced by the actual name of a package.
   * <p>
   * The odd arguments of the method are interpreted as placeholder names, the
   * even arguments, each following a placeholder name are the actual values.
   * The placeholder names <b>should not</b> contain the {@code #} characters.
   * They will be taken care of this method. Only the names of the placeholders
   * are to be specified.
   * <p>
   * If there are odd number of argument strings then RuntimeException will be
   * thrown.
   * 
   * @param arg
   * @return
   */
  public String replace(String... arg) {
    if (arg.length % 2 != 0)
      throw new RuntimeException("replace was called with odd number of strings");
    String result = s;
    for (int i = 0; i < arg.length; i += 2) {
      result = result.replaceAll("#" + arg[i] + "#", arg[i + 1]);
    }
    return result;
  }

  /**
   * Calculate the prefix from the name of the package. This is the string that
   * has to be prepended in front of the name of the class to get the fully
   * qualified class name that can be used in source files.
   * 
   * @return the package name usable as prefix
   */
  public String makePrefix() {
    return s == null ? "" : s + ".";
  }
}
