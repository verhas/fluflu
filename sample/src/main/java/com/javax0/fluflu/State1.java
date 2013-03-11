//7IoqfMjT6uaVTNAd5j890Q93HgwCpPfAvrXoF+UkSPp2fls1OgmWgJOFo8XmvmcsAtpJvamBLfTZeohkeY9O0w==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Mon Mar 11 16:35:58 CET 2013

package com.javax0.fluflu;

public class State1 {
	private final Zaku core;

	public State1(Zaku core) {
		this.core = core;
	}
	public void end(){
		core.end();	
	}
	public State0 with(String a, byte[] b){
		return new State0(core.with(a, b));	
	}
	public State1 z(int a){
		return new State1(core.z(a));	
	}
	public State0 z(){
		return new State0(core.z());	
	}

}