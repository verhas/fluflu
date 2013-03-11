package com.javax0.fluflu;

import static com.javax0.fluflu.PackagePrefixCalculator.packagePrefix;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassParser {
	final String classesDirectory;
	final String classToFluentize;
	final String javaDirectory;
	final String packageName;

	public ClassParser(String javaDirectory, String classesDirectory,
			String packageName, String classToFluentize) {
		this.classesDirectory = classesDirectory;
		this.classToFluentize = classToFluentize;
		this.javaDirectory = javaDirectory;
		this.packageName = packageName;
	}

	private Transition[] getTransitions(Method method) {
		Transitions ts = method.getAnnotation(Transitions.class);
		final Transition[] transitions;
		if (ts == null) {
			Transition t = method.getAnnotation(Transition.class);
			if (t != null) {
				transitions = new Transition[1];
				transitions[0] = t;
			} else {
				transitions = null;
			}
		} else {
			transitions = ts.value();
		}
		return transitions;
	}

	private final Map<String, List<TransitionEdge>> transitionMap = new HashMap<>();

	private void processTransition(Transition transition, Method method) {
		final String[] fromStates = transition.from();
		final String toState = transition.to();
		final boolean end = transition.end();
		for (String fromState : fromStates) {
			if (!transitionMap.containsKey(fromState)) {
				transitionMap.put(fromState, new LinkedList<TransitionEdge>());
			}
			List<TransitionEdge> transitions = transitionMap.get(fromState);
			transitions.add(new TransitionEdge(method, end ? null : toState));
		}
	}

	/**
	 * This method checks that he methods having the same name (overloaded) and
	 * originating from the same state will bring to the same state. This is
	 * because in a single class overloaded methods should return the same
	 * class.
	 */
	private boolean overloadIsConsistent() {
		// TODO
		return true;
	}

	private void parse(Method method) {
		final Transition[] transitions = getTransitions(method);
		if (transitions != null) {
			for (Transition transition : transitions) {
				processTransition(transition, method);
			}
		}
	}

	private String getStateJavaFileName(String state) {
		return (javaDirectory + "/" + packagePrefix(packageName) + state)
				.replaceAll("\\.", "/") + ".java";
	}

	private void generateStateClass(String state) throws IOException {
		FluentizerMaker maker = new FluentizerMaker(packageName, state,
				javaDirectory, classToFluentize);
		if (maker.fileIsIntactOrNewOrEmpty()) {
			String header = maker.generateStateClassHeader();
			StringBuilder body = new StringBuilder(header);
			for (TransitionEdge edge : transitionMap.get(state)) {
				body.append(maker.generateStateClassMethod(edge));
			}
			body.append(maker.generateStateClassFooter());
			maker.overwrite(getStateJavaFileName(state), body.toString());
		}
	}

	private void generateStateClasses() throws IOException {
		for (String state : transitionMap.keySet()) {
			generateStateClass(state);
		}
	}

	private void parse(Class<?> klass) throws IOException {
		FluentApi fluentApiAnnotation = klass.getAnnotation(FluentApi.class);
		if (fluentApiAnnotation != null) {
			String className = fluentApiAnnotation.className();
			String startState = fluentApiAnnotation.startState();
			String startMethod = fluentApiAnnotation.startMethod();
			FluentizerMaker maker = new FluentizerMaker(packageName, className,
					javaDirectory, classToFluentize);
			maker.fileIsIntactOrNewOrEmpty();
			String fluentClassContent = maker.generateFluentClass(startState,
					startMethod);
			maker.overwrite(getStateJavaFileName(className), fluentClassContent);
		}
		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			parse(method);
		}
		if (overloadIsConsistent()) {
			generateStateClasses();
		}

	}

	public void parse() throws ClassNotFoundException, IOException {
		FluClassLoader classLoader = new FluClassLoader(
				ClassParser.class.getClassLoader(), classesDirectory);
		Class<?> klass = classLoader.loadClass(packagePrefix(packageName)
				+ classToFluentize);
		if (klass == null) {
			Out.warn(classToFluentize
					+ ".class can not be parsed from directory"
					+ classesDirectory + ".");
		} else {
			parse(klass);
		}
	}
}
