package com.javax0.fluflu;

/**
 * Unit test for simple App.
 */
public class AppTest {
  public AppTest(String testName) {
  }

  /**
   * Rigorous Test :-)
   * 
   * @throws CloneNotSupportedException
   */
  public void testApp() throws CloneNotSupportedException {
    State0 c = CoreClass.start().a().z();
    State0 q = CoreClass.start().a().z().b().z(53).z();
    State0 d = c.b().with("z", "z".getBytes());
    State0 e = c.b().with("q", new byte[] { 0, 0, 1 });
    d.end();
    e.end();
  }

  public static void main(String[] args) {

  }
}
