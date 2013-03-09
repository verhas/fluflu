package com.javax0.fluflu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transitions {

	Transition[] value();

}
