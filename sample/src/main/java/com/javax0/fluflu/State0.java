//+sB9Q4yQOBr9/D0BZgKhjoJDr5VZ58WvSFpEmhS2GMdm8nTQypPNHq8dKKr3u6pewv5KhnuA/XjsI7HyuHZiEA==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Mon Mar 11 16:35:58 CET 2013

package com.javax0.fluflu;

public class State0 {
	private final Zaku core;

	public State0(Zaku core) {
		this.core = core;
	}
	public State1 b(){
		return new State1(core.b());	
	}
	public void end(){
		core.end();	
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

}