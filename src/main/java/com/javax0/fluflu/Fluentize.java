package com.javax0.fluflu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Fluentize {
	String className() default "";
	String startState() default "";
	String startMethod() default "";
}
