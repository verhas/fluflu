//+5nXjwiTmJpZ9vhOUb4FCsZXfWoUVTaRIlLoiENxYiwhVFl9hOdB8cvhY+DRrKJptI8LNG3+4Nk2BnrerDHtAw==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Sat Mar 09 11:14:48 CET 2013

package com.javax0.fluflu;

public class State1 implements com.javax0.fluflu.State {
	private final com.javax0.fluflu.CoreClass core;

	public State1(com.javax0.fluflu.CoreClass core) {
		this.core = core;
	}
	public com.javax0.fluflu.State1 end(){
		return new com.javax0.fluflu.State1(core.end());	
	}
	public com.javax0.fluflu.State0 with(java.lang.String par0, byte[] par1){
		return new com.javax0.fluflu.State0(core.with(par0, par1));	
	}
	public com.javax0.fluflu.State0 z(){
		return new com.javax0.fluflu.State0(core.z());	
	}

}