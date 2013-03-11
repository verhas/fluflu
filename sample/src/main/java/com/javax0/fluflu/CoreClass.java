//LLKxMl5hYHoE3xsY8fDI82Ch1YuDrQHvya45sWQAvOxZhpDNhqJu3+iwEglXywMTKi+qUwhM4KI1vCUXB6sJMA==
//DO NOT EDIT! THIS IS A FLUFLU GENERATED CLASS
// DATE: Mon Mar 11 17:55:09 CET 2013

package com.javax0.fluflu;

public class CoreClass extends ToBeFluentized {

	public static State0 start() {
		return new State0(new CoreClass());
	}

	public com.javax0.fluflu.ToBeFluentized with(String a, byte[] b) {
		CoreClass core;
		try {
			core = (CoreClass) this.clone();
		} catch (CloneNotSupportedException e) {
			core = this;
		}
		core.with = a;
		core.b.add(b);

		return core;
	}

	public com.javax0.fluflu.ToBeFluentized z(int a) {
		CoreClass core;
		try {
			core = (CoreClass) this.clone();
		} catch (CloneNotSupportedException e) {
			core = this;
		}
		core.j.add(a);

		return core;
	}

}