package com.javax0.fluflu;

public class CoreClass {

	public static State0 start() {
		return new State0(new CoreClass());
	}

	@Transitions({ @Transition(from = State0.class, end = true),
			@Transition(from = State1.class, to = State1.class) })
	public CoreClass end() {
		return this;
	}

	@Transition(from = { State0.class, State1.class }, to = State0.class)
	public CoreClass with(String a, byte[] b) {
		return this;
	}

	@Transition(from = State1.class, to = State0.class)
	public CoreClass z() {
		return this;
	}

	@Transition(from = State0.class, to = State1.class)
	public CoreClass a() {
		return this;
	}

	@Transition(from = State0.class, to = State1.class)
	public CoreClass b() {
		return this;
	}
}
