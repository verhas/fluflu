package com.javax0.fluflu;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Facere {
	private static final String DEFAULT_JAVA_DIRECTORY = "src/main/java";
	private static final String DEFAULT_PACKGE = "";
	private static final String DEFAULT_CLASSES_DIRECTORYS = "target/classes";
	private static final String DEFAULT_CORE = null;

	private static Options createOptions() {
		Options options = new Options();
		options.addOption("c", "class", true,
				"define the core class to fluentize");
		options.addOption("p", "package", true,
				"define package for the fluentize classes");
		options.addOption("J", "java", true,
				"define the source location. default is '"
						+ DEFAULT_JAVA_DIRECTORY + "'");
		options.addOption("C", "classes", true,
				"define the .class files location. default is '"
						+ DEFAULT_CLASSES_DIRECTORYS + "'");
		options.addOption("h", "help", false, "print this text");
		return options;
	}

	private final CommandLine line;
	private String javaDirectory;
	private String packge;
	private String classesDirectory;
	private String classToFluentize;

	private void setClassToFluentize() {
		if (line.hasOption("class")) {
			classToFluentize = line.getOptionValue("class");
		} else {
			classToFluentize = DEFAULT_CORE;
		}
	}

	private void setClassesDirectory() {
		if (line.hasOption("classes")) {
			classesDirectory = line.getOptionValue("classes");
		} else {
			classesDirectory = DEFAULT_CLASSES_DIRECTORYS;
		}
	}

	private void setJavaDirectory() {
		if (line.hasOption("java")) {
			javaDirectory = line.getOptionValue("java");
		} else {
			javaDirectory = DEFAULT_JAVA_DIRECTORY;
		}
	}

	private void setPackge() {
		if (line.hasOption("package")) {
			packge = line.getOptionValue("package");
		} else {
			packge = DEFAULT_PACKGE;
		}
	}

	private Facere(CommandLine line) {
		this.line = line;
		setJavaDirectory();
		setPackge();
		setClassesDirectory();
		setClassToFluentize();
	}

	private void execute() throws ClassNotFoundException, IOException {
		new ClassParser(javaDirectory, classesDirectory, packge,
				classToFluentize).parse();
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileModifiedException
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		Options options = createOptions();

		final CommandLine line;
		CommandLineParser parser = new BasicParser();
		try {
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			return;
		}
		if (line.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("facere", options);
			return;
		}
		if (!line.hasOption("class")) {
			Out.error("option 'class' is mandatory. Use -h or --help for more information.");
			return;
		}
		Facere facere = new Facere(line);
		facere.execute();
	}

}
