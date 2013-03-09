package com.javax0.fluflu;

import java.lang.reflect.Method;

public class TransitionEdge {

	Method method;
	Class<? extends State> targetState;

	TransitionEdge(Method method, Class<? extends State> targetState) {
		this.method = method;
		this.targetState = targetState;
	}
}
