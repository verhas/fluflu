package com.javax0.aptools;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

public class AnnotationTool {
	private AnnotationMirror annotationMirror;

	public AnnotationTool(AnnotationMirror annotationMirror) {
		this.annotationMirror = annotationMirror;
	}

	private AnnotationValue getAnnotationValue(String name) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> map = annotationMirror
				.getElementValues();
		for (ExecutableElement annotationElement : map.keySet()) {
			if (annotationElement.getSimpleName().toString().equals(name)) {
				return map.get(annotationElement);
			}
		}
		return null;
	}

	public String getStringValue() {
		return getStringValue("value");
	}

	public String getStringValue(String name) {
		AnnotationValue av = getAnnotationValue(name);
		String value;
		if (av == null) {
			value = null;
		} else {
			value = GetThe.string(av.toString()).unquoted();
		}
		return value;
	}
}
