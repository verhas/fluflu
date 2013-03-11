//ND/WcyTCTgfxeUASNvrTiWQ1up6+M7oI34n9/e8xd6NrUjBJ33q90nY9YULVWWjDMWEJQnlBkRXZhfYhtiK/xA==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Mon Mar 11 05:34:35 CET 2013

package com.javax0.fluflu;

public class State1 {
	private final Zaku core;

	public State1(Zaku core) {
		this.core = core;
	}
	public State0 with(String a, byte[] b){
		return new State0(core.with(a, b));	
	}
	public State0 z(){
		return new State0(core.z());	
	}
	public State1 z(int a){
		return new State1(core.z(a));	
	}
	public void end(){
		core.end();	
	}

}