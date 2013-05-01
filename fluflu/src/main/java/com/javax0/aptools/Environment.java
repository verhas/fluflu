package com.javax0.aptools;

import javax.annotation.processing.ProcessingEnvironment;

public class Environment {
  static final ThreadLocal<ProcessingEnvironment> processingEnvironment = new ThreadLocal<>();

  public static void set(ProcessingEnvironment pe) {
    processingEnvironment.set(pe);
  }

  public static ProcessingEnvironment get() {
    return processingEnvironment.get();
  }
}
