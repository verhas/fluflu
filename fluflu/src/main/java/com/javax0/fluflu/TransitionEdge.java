package com.javax0.fluflu;

import javax.lang.model.element.ExecutableElement;

public class TransitionEdge {

	ExecutableElement method;
	String targetState;

	TransitionEdge(ExecutableElement method, String targetState) {
		this.method = method;
		this.targetState = targetState;
	}
}
