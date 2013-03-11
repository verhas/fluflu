package com.javax0.fluflu;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Fluentize(className = "CoreClass", startState = "State0", startMethod = "start")
public abstract class ToBeFluentized implements Cloneable {

	@Transitions({ @Transition(from = "State0", end = true),
			@Transition(from = "State1", end = true) })
	public void end() {
	}


	protected String with = null;
	protected List<byte[]> b = new LinkedList<>();

	@Transition(from = { "State0", "State1" }, to = "State0")
	public abstract ToBeFluentized with(@AssignTo("with") String a, @AddTo("b") byte[] b);

	@Transition(from = "State1", to = "State0")
	public ToBeFluentized z() {
		return this;
	}

	Set<Integer> j = new HashSet<>(); 
	@Transition(from = "State1", to = "State1")
	public abstract ToBeFluentized z(@AddTo("j") int j);

	@Transition(from = "State0", to = "State1")
	public ToBeFluentized a() {
		return this;
	}

	@Transition(from = "State0", to = "State1")
	public ToBeFluentized b() {
		return this;
	}

	@Transition(from = "State0", to = "State1")
	public ToBeFluentized vari(String... strings) {
		return this;
	}
}
