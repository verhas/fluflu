package com.javax0.aptools;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class MethodTool {
	private ExecutableElement methodElement;

	protected MethodTool(ExecutableElement methodElement) {
		this.methodElement = methodElement;
	}

	public int getTheNumberOfParameters() {
		return methodElement.getParameters().size();
	}

	public List<? extends AnnotationMirror>[] getParameterAnnotations() {
		@SuppressWarnings("unchecked")
		List<? extends AnnotationMirror>[] annotationMirrorss = new List[getTheNumberOfParameters()];
		int i = 0;
		for (VariableElement parameterElement : methodElement.getParameters()) {
			annotationMirrorss[i] = parameterElement.getAnnotationMirrors();
			i++;
		}
		return annotationMirrorss;
	}

	public boolean isAbstract() {
		return methodElement.getModifiers().contains(Modifier.ABSTRACT);
	}

	private static final String alphas = "abcdefghjkmnopqrstzwxy";

	public String getArgumentName(int i) {

		if (i < alphas.length()) {
			return alphas.substring(i, i + 1);
		} else {
			return "p" + i;
		}

	}

	public String createParamList() {
		StringBuilder arglist = new StringBuilder();
		String sep = "";

		for (int i = 0; i < getTheNumberOfParameters(); i++) {
			arglist.append(sep).append(getArgumentName(i));
			sep = ", ";
		}
		return arglist.toString();
	}

	public String getReturnType() {
		return methodElement.getReturnType().toString();
	}

	public String getName() {
		return methodElement.getSimpleName().toString();
	}

	// TODO check that it works for varargs
	public String createArgList() {
		StringBuilder arglist = new StringBuilder();
		String sep = "";
		int i = 0;
		for (VariableElement parameterElement : methodElement.getParameters()) {
			final String declaration = parameterElement.asType().toString();
			arglist.append(sep).append(declaration).append(" ")
					.append(getArgumentName(i));
			sep = ", ";
			i++;
		}
		return arglist.toString();
	}
}
