//jEqXuPzK5thNuO8LXtwymNxp6J0FLf39FgwqM9KQAAHKEZgKa1VS6PWZJNq8S4z7pcPqHabR/F/0LnOzr3i0mg==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Mon Mar 11 16:35:58 CET 2013

package com.javax0.fluflu;

public class CoreClass extends Zaku {


	public static State0 start() {
		return new State0(new CoreClass());
	}
		public com.javax0.fluflu.Zaku with(String a, byte[] b) {
		CoreClass core;
		try {
			core = (CoreClass)this.clone();
		}catch(CloneNotSupportedException e){
			core = this;
		}
		    core.with = a;
    core.b.add(b);

		return core;	
	}
	public com.javax0.fluflu.Zaku z(int a) {
		CoreClass core;
		try {
			core = (CoreClass)this.clone();
		}catch(CloneNotSupportedException e){
			core = this;
		}
		    core.j.add(a);

		return core;	
	}

}