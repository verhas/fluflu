package com.javax0.aptools;

public class There {
	public static boolean is(String s) {
		return s != null && s.length() > 0;
	}

	public static boolean is(Object s) {
		return s != null;
	}
}
