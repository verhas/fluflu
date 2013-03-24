package com.javax0.fluflu.apt;

import static com.javax0.fluflu.PackagePrefixCalculator.packagePrefix;

import java.io.IOException;
import java.io.Writer;
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
import javax.tools.JavaFileObject;

import com.javax0.aptools.FromThe;
import com.javax0.aptools.The;
import com.javax0.fluflu.FluentizerMaker;

@SupportedAnnotationTypes("com.javax0.fluflu.*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FlufluApt extends AbstractProcessor {

	private void message(String s) {
		processingEnv.getMessager().printMessage(Kind.NOTE, s);
		System.out.println(s);
	}

	private void generateFluentizedClass(Element element) throws IOException {

		AnnotationMirror fluentize = FromThe.element(element).getTheAnnotation(
				"com.javax0.fluflu.Fluentize");
		String startState = FromThe.annotation(fluentize).getStringValue(
				"startState");
		String className = FromThe.annotation(fluentize).getStringValue(
				"className");
		String startMethod = FromThe.annotation(fluentize).getStringValue(
				"startMethod");
		String packageName = FromThe.element(element).getPackageName();

		String classToFluentize = FromThe.element(element).getClassName();
		StringBuilder body = new StringBuilder();
		if (className != null && className.length() > 0) {
			FluentizerMaker maker = new FluentizerMaker(packageName, className,
					null, classToFluentize);
			body.append(maker
					.generateFluentClassHeader(startState, startMethod));
			if (startMethod.length() > 0) {
				body.append(maker.generateStartMethod(startState, startMethod));
			}
			if (The.element(element).isAbstract()) {
				body.append(maker.generateFluentClassMethods(element));
			}
			body.append(maker.generateFluentClassFooter());

		}
		String name = packagePrefix(packageName) + className;
		JavaFileObject jfo = processingEnv.getFiler().createSourceFile(name,
				(Element[]) null);
		Writer writer = jfo.openWriter();
		writer.write(body.toString());
		writer.close();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		for (Element rootElement : roundEnv.getRootElements()) {
			if (The.element(rootElement).hasAnnotation(
					"com.javax0.fluflu.Fluentize")) {
				message("[RENAME] " + rootElement.getSimpleName());
				try {
					generateFluentizedClass(rootElement);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
