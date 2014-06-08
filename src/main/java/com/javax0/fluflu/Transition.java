package com.javax0.fluflu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Repeatable(Transitions.class)
public @interface Transition {
	String[] from() default "";

	String to() default "";

	boolean end() default false;
	
	String name() default "";
}
