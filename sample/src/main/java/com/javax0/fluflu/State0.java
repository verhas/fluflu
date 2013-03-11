//nUwUcTvqCqFl1uU6pWpBR0KBxyf/CAGGsLWjfiiiAiFDSqeT6h3qmCTuSu59DbQuYb65o3SRzwEPBToY2AoOXg==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Mon Mar 11 05:34:35 CET 2013

package com.javax0.fluflu;

public class State0 {
	private final Zaku core;

	public State0(Zaku core) {
		this.core = core;
	}
	public State1 a(){
		return new State1(core.a());	
	}
	public State0 with(String a, byte[] b){
		return new State0(core.with(a, b));	
	}
	public State1 vari(String... a){
		return new State1(core.vari(a));	
	}
	public void end(){
		core.end();	
	}
	public State1 b(){
		return new State1(core.b());	
	}

}