package com.javax0.fluflu;

@FluentApi(className="CoreClass", startState="State0",startMethod="start")
public abstract class Zaku {

	@Transitions({ @Transition(from = "State0", end = true),
			@Transition(from = "State1", end = true) })
	public void end() {
	}

	@Transition(from = { "State0", "State1" }, to = "State0")
	public Zaku with(String a, byte[] b) {
		return this;
	}

	@Transition(from = "State1", to = "State0")
	public Zaku z() {
		return this;
	}

	@Transition(from = "State1", to = "State1")
	public Zaku z(int j) {
		return this;
	}	
	
	
	@Transition(from = "State0", to = "State1")
	public Zaku a() {
		return this;
	}

	@Transition(from = "State0", to = "State1")
	public Zaku b() {
		return this;
	}
	
	@Transition(from = "State0", to = "State1")
	public Zaku vari(String ...strings ){
		return this;
	}
}
