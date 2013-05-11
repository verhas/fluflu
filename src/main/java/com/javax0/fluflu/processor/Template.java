package com.javax0.fluflu.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

class Template {
  static String load(String name) {
    try (InputStream is = ClassMaker.class.getClassLoader().getResourceAsStream(name); Scanner scanner = new Scanner(is, "utf-8")) {
      return scanner.useDelimiter("somethind that does not happen ever in the files").next();
    } catch (IOException ioe) {
      System.err.println("Resource '" + name + "' can not be loaded. This is an internal error.");
      return null;
    }

  }
}
