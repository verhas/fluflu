package com.javax0.aptools;

public class StringTool {
  private final String s;

  StringTool(String s) {
    this.s = s;
  }

  public String unquoted() {
    return s.substring(1, s.length() - 1);
  }

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
