fluflu
======

Fluent Api Creator

## What is fluent api

You can read the following articles to get to know what fluent api is:

[Fluent Api & Wikipedia](http://en.wikipedia.org/wiki/Fluent_interface)

[FluentInterface from Martin Fowler](http://martinfowler.com/bliki/FluentInterface.html)

[The Java Fluent API Designer Crash Course](http://java.dzone.com/articles/java-fluent-api-designer-crash)

## What is fluent api for the impatient

Fluent API in Java is a technique that results readable method call chaining. Example:


    CreateSql.select("colmn1","column2").from("tableName").where("column1 = 'value'")

This can be reached implementing the methods `select`, `from` and `where` returning an instance of the the class `CreateSql`. (Note: `select` is static.) This is simple and straightforward. However this is far from real fluent API. This implementation will not prevent someone write

    // WRONG!!!
    CreateSql.select("colmn1","column2").from("tableName").from("thatTable")    

which is simply wrong. To prevent this you have to define extra interfaces as depicted in the articles [The Java Fluent API Designer Crash Course](http://java.dzone.com/articles/java-fluent-api-designer-crash) You can also create classes instead of interfaces. Or you can use fluflu to create classes.

## What is fluflu?

Fluflu is a software to generate the classes that convert your method chaining classes to real fluent API. It contains a library that you have to put on the compile and runtime classpath of your application and a command line tool. What you should depend in your maven project is:


    	<dependencies>
		    <dependency>
			    <groupId>com.javax0.fluflu</groupId>
			    <artifactId>fluflu</artifactId>
			    <version>1.0.0</version>
			    <scope>compile</scope>
		    </dependency>
    </dependencies>
	
This library defines the annotations and the annotation processing tool libraries that handle the annotations during compile time and generate the extra classes in form of Java source code that do the work of fluentization.

You can create the class with method chaining and you have to decide the order the methods should/may be called. This order can be arbitrary complex so long as long it can be described using [http://en.wikipedia.org/wiki/Finite-state_machine](finite state automata). The states will become classes and the transitions between the states will be the methods. Every method in your class will have a pair in one or more of the generated classes. These methods will call your methods and will return an instance of another class representing the state where the transition brings the automata to.

When you have this library available and you designed your finite state automata you have to create your class that you want to fluentize. For example:

    package com.javax0.fluflu;
    
    import java.util.HashSet;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Set;
    
    @Fluentize(className = "CoreClass", startState = "State0", startMethod = "start")
    public abstract class ToBeFluentized implements Cloneable {
    
    	@Transitions({ @Transition(from = "State0", end = true),
    			@Transition(from = "State1", end = true) })
    	public void end() {
    	}
    
    	protected String with = null;
    	protected List<byte[]> b = new LinkedList<>();
    
    	@Transition(from = { "State0", "State1" }, to = "State0")
    	public abstract ToBeFluentized with(@AssignTo("with") String a, @AddTo("b") byte[] b);
    
    	@Transition(from = "State1", to = "State0")
    	public ToBeFluentized z() {
    		return this;
    	}
    
    	Set<Integer> j = new HashSet<>(); 
    	@Transition(from = "State1", to = "State1")
    	public abstract ToBeFluentized z(@AddTo("j") int j);
    
    	@Transition(from = "State0", to = "State1")
    	public ToBeFluentized a() {
    		return this;
    	}
    
    	@Transition(from = "State0", to = "State1")
    	public ToBeFluentized b() {
    		return this;
    	}
    
    	@Transition(from = "State0", to = "State1")
    	public ToBeFluentized vari(String... strings) {
    		return this;
    	}
    }

What you see here is that the class is annotated using the annotation `@Fluentize`. The class itself is abstract. It actually need not be abstract, it is only a possibility to further ease your life. Also note that the class implements the `Cloneable` interface. This is not a must, but makes the use of the generated fluent API more consistent.

The optional arguments define the class that will contain the fluent interface extending your class and the name of the static method that will instantiate the first instance of the class when using the fluent API. If your class is abstract then the argument `className` is better to be specified otherwise you have to create a concrete class extending the abstract class.

The methods that become part of the fluent API should be annotated using the annotation `@Transition`. This annotation should have a `from` parameter (String[]), which will name the states from where the method brings the api. The optional parameter `to` specified one state where the method ends. In the generated classes the `to` parameter will be the return type of the method and the `from` parameter specified which classes implement the method.

If a method can bring the automata from different states to different states, for example from A to B, but when the automata is in state C then to D, the annotation `@Transitions` (note the plural) should be used that will contain multiple `@Transition` (singular) annotations.

There can be methods in the fluent API that close the chain. It is recommended to name this method to be `end()`. When this method is called in your class usually all parameters are collected and are available and you can do the work in your class. Those methods may be chained when you use fluflu or may return `void` or just may return anything. To make a method terminal, closing the chain use the parameter `end=true` in the annotation `@Transition`.

Note that you can use a method to be terminal as well as non terminal at the same time when the method can be invoked from different states (classes). It is, however, not recommended at all.

Non terminal fluent api methods usually only collect the parameters. This is also some coding practice that can and should be automated. If you do not want to write the collection coding yourself have some `protected` fields that will contain the values and annotate the parameters of the abstract methods of your class using `@AssignTo` and/or `@AddTo`. The value of these annotations should be the name of the field where the values passed to the fluent api are to be stored.

Use the annotation `@AssignTo` when there is only one value you want to store. This will generate a method that will assign the passed value to your field. Use the annotation `@AddTo` when you want to have all the passed values (when the method is presented in the method chain more than once). In this case declare *AND INITIALIZE* your field to some variable type that can accommodate the method `.add(value)`.

When you have all these annotations all you have to do is execute the commands

    mvm clean install
    java -jar facere.jar -p com.javax0.fluflu -class ToBeFluentized 

that will generate the state classes and the class that extends your abstract class (if you specified the class name in the annotation `@Fluentize`). Try and have a look at the generated code.

**NOTE** that flu flu facere will *never* overwrite any hand made file. It generates files that are non existent and will overwrite only files that it generated and which was not tampered. It writes an SHA-512 hash code bas64 encoded on the first line of the files (following the Java one line // comment starter) and if it does not match the unspaced content of the file it refuses to overwrite. In that case you have to delete it yourself. (Unspaced means that you can format the code if you want, insert new lines, spaces: does not matter. This can be important if your IDE automatically formats the code upon save.)

## Why Cloneable ?

Fluflu facers will create code:

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
	
This creates a clone of your class every time it is called if it is possible. This means that you can safely and consistently use your api in the following way:


  		State0 c = CoreClass.start().a().z();
		State0 d = c.b().with("z", "z".getBytes());
		State0 e = c.b().with("q", new byte[]{0,0,1});
		d.end();
		e.end();
		
The two calls on the last line will not interfere with each other if you consistently create clones on each call to your fluent API. (Note that the sample class in this documentation does actually not.)			





	
	
	