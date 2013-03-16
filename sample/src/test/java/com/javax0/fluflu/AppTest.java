package com.javax0.fluflu;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * @throws CloneNotSupportedException 
	 */
	public void testApp() throws CloneNotSupportedException {
		State0 c = CoreClass.start().a().z();
		State0 d = c.b().with("z", "z".getBytes());
		State0 e = c.b().with("q", new byte[]{0,0,1});
		d.end();
		e.end();
		assertTrue(true);
	}
}
