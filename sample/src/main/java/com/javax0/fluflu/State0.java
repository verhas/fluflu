//Q+fHZfTu9IQCk2BGp8+MUZGS3jrAKeb1Lct4qGuB5qXJA5gKq21a8xUMtkqCjqsKyPdh7NJ8aIu4HnuO5Vn7rw==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Sat Mar 09 11:14:48 CET 2013

package com.javax0.fluflu;

public class State0 implements com.javax0.fluflu.State {
	private final com.javax0.fluflu.CoreClass core;

	public State0(com.javax0.fluflu.CoreClass core) {
		this.core = core;
	}
	public void end(){
		core.end();	
	}
	public com.javax0.fluflu.State1 b(){
		return new com.javax0.fluflu.State1(core.b());	
	}
	public com.javax0.fluflu.State0 with(java.lang.String par0, byte[] par1){
		return new com.javax0.fluflu.State0(core.with(par0, par1));	
	}
	public com.javax0.fluflu.State1 a(){
		return new com.javax0.fluflu.State1(core.a());	
	}

}