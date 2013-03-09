package com.javax0.fluflu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transition {
	Class<? extends State>[] from() default State.class;

	Class<? extends State> to() default State.class;

	boolean end() default false;
}
