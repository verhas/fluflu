package com.javax0.fluflu;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.javax0.aptools.FromThe;
import com.javax0.aptools.The;
import com.javax0.aptools.There;

@SupportedAnnotationTypes("com.javax0.fluflu.*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FlufluApt extends AbstractProcessor {

	private void message(String s) {
		processingEnv.getMessager().printMessage(Kind.NOTE, s);
		System.out.println(s);
	}

	private void generateFluentizedClassFrom(Element classElement)
			throws IOException {

		AnnotationMirror fluentize = FromThe.element(classElement)
				.getTheAnnotation("com.javax0.fluflu.Fluentize");
		String startState = FromThe.annotation(fluentize).getStringValue(
				"startState");
		String className = FromThe.annotation(fluentize).getStringValue(
				"className");
		String startMethod = FromThe.annotation(fluentize).getStringValue(
				"startMethod");

		String packageName = FromThe.element(classElement).getPackageName();
		String classToFluentize = FromThe.element(classElement).getClassName();

		StringBuilder body = new StringBuilder();
		if (There.is(className)) {
			FluentizerMaker maker = new FluentizerMaker(packageName, className,
					classToFluentize, null);
			body.append(maker
					.generateFluentClassHeader(startState, startMethod));
			if (There.is(startMethod)) {
				body.append(maker.generateStartMethod(startState, startMethod));
			}
			if (The.element(classElement).isAbstract()) {
				body.append(maker.generateFluentClassMethods(classElement));
			}
			body.append(maker.generateFluentClassFooter());

		}
		new ClassWriter(processingEnv).writeSource(packageName, className,
				body.toString());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		for (Element rootElement : roundEnv.getRootElements()) {
			if (The.element(rootElement).hasAnnotation(
					"com.javax0.fluflu.Fluentize")) {
				message("[RENAME] " + rootElement.getSimpleName());
				try {
					generateFluentizedClassFrom(rootElement);
					new ClassParser(rootElement, new ClassWriter(processingEnv))
							.parse();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
