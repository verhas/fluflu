package com.javax0.fluflu;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassParser {
	final private static Logger log = LoggerFactory
			.getLogger(ClassParser.class);
	final String bin;
	final String core;
	final String src;

	public ClassParser(String bin, String core, String src) {
		this.bin = bin;
		this.core = core;
		this.src = src;
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

	private final Map<Class<? extends State>, List<TransitionEdge>> transitionMap = new HashMap<>();

	private void processTransition(Transition transition, Method method) {
		final Class<? extends State>[] fromStates = transition.from();
		final Class<? extends State> toState = transition.to();
		final boolean end = transition.end();
		for (Class<? extends State> fromState : fromStates) {
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

	private String getStateJavaFile(Class<? extends State> state) {
		return src + "/" + state.getCanonicalName().replaceAll("\\.", "/") + ".java";
	}

	private void generateStateClass(Class<? extends State> state)
			throws IOException, FileModifiedException {
		FluentizerMaker maker = new FluentizerMaker(state.getPackage()
				.getName(), state.getSimpleName(), src, core);
		maker.assertFileIsIntactOrNewOrEmpty();
		String header = maker.generateStateClassHeader();
		StringBuilder body = new StringBuilder(header);
		for (TransitionEdge edge : transitionMap.get(state)) {
			body.append(maker.generateStateClassMethod(edge));
		}
		body.append(maker.generateStateClassFooter());
		maker.overwrite(getStateJavaFile(state), body.toString());
	}

	private void generateStateClasses() throws IOException,
			FileModifiedException {
		for (Class<? extends State> state : transitionMap.keySet()) {
			generateStateClass(state);
		}
	}

	private void parse(Class<?> klass) throws IOException,
			FileModifiedException {
		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			parse(method);
		}
		if (overloadIsConsistent()) {
			generateStateClasses();
		}

	}

	public void parse() throws ClassNotFoundException, IOException,
			FileModifiedException {
		FluClassLoader classLoader = new FluClassLoader(
				ClassParser.class.getClassLoader(), bin);
		Class<?> klass = classLoader.loadClass(core);
		if (klass == null) {
			log.warn("{}.class can not be parsed from directory {}.", core, bin);
		} else {
			parse(klass);
		}
	}
}
