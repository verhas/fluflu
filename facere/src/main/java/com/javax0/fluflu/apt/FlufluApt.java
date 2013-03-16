package com.javax0.fluflu.apt;

import java.io.IOException;
import java.lang.reflect.Modifier;
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
import com.javax0.fluflu.Fluentize;
import com.javax0.fluflu.FluentizerMaker;

@SupportedAnnotationTypes("com.javax0.fluflu.*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FlufluApt extends AbstractProcessor {

	private void message(String s) {
		processingEnv.getMessager().printMessage(Kind.NOTE, s);
		System.out.println(s);
	}

	private void generateFluentizedClass(Element element) throws IOException {

		AnnotationMirror fluentize = FromThe.element(element)
				.getTheAnnotation("com.javax0.fluflu.Fluentize");
		String startState = FromThe.annotation(fluentize)
				.getStringValue("startState");
		String className = FromThe.annotation(fluentize)
				.getStringValue("className");
		String startMethod = FromThe.annotation(fluentize)
				.getStringValue("startMethod");
		String packageName = FromThe.element(element).getPackageName();
		
		String classToFluentize = FromThe.element(element).getClassName();
			if (className != null && className.length() > 0) {
				StringBuilder body = new StringBuilder();
				FluentizerMaker maker = new FluentizerMaker(packageName,
						className, null, classToFluentize);
				if (maker.fileIsIntactOrNewOrEmpty()) {
					body.append(maker.generateFluentClassHeader(startState,
							startMethod));
					if (startMethod.length() > 0) {
						body.append(maker.generateStartMethod(startState,
								startMethod));
					}
					if (The.element(element).isAbstract()) {
						body.append(maker.generateFluentClassMethods(klass));
					}
					body.append(maker.generateFluentClassFooter());
					maker.overwrite(getStateJavaFileName(className),
							body.toString());
				}
			}
		}
	}
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		for (Element rootElement : roundEnv.getRootElements()) {
			if (The.element(rootElement).hasAnnotation(
					"com.javax0.fluflu.Fluentize")) {
				message("[RENAME] " + rootElement.getSimpleName());
				for (AnnotationMirror annotationMirror : rootElement
						.getAnnotationMirrors()) {
					message("[REANN]" + annotationMirror.getAnnotationType());
				}
				

			}
		}
		for (TypeElement annotation : annotations) {
			message("[QNAME] " + annotation.getQualifiedName());
			message("[KIND] " + annotation.getKind().toString());
			for (Element element : annotation.getEnclosedElements()) {
				message("  [ENAME] " + element.getSimpleName());
			}
		}
		return false;
	}
}
