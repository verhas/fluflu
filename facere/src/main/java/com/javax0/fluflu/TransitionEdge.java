package com.javax0.fluflu;

import java.lang.reflect.Method;

public class TransitionEdge {

	Method method;
	String targetState;

	TransitionEdge(Method method, String targetState) {
		this.method = method;
		this.targetState = targetState;
	}
}
