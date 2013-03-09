package com.javax0.fluflu;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Facere {
	private static final String DEFAULT_CONFIG = "fluflu.yaml";
	private static final String DEFAULT_SRC = "src/main/java";
	private static final String DEFAULT_PACKGE = "";
	private static final String DEFAULT_BIN = "target/classes";
	private static final String DEFAULT_CORE = null;
	private static final String[] DEFAULT_CLASSES = null;
	private static final boolean DEFAULT_RESET = false;

	private static Logger log = LoggerFactory.getLogger(Facere.class);

	private static Options createOptions() {
		Options options = new Options();
		options.addOption("r", "reset", false, "resets the configuration file");
		options.addOption("c", "config", true,
				"define an alternative configuration file. default is '"
						+ DEFAULT_CONFIG + "'");
		options.addOption("core", "core", true,
				"define the core class to fluentize");
		options.addOption("p", "package", true,
				"define package for the fluentize classes");
		options.addOption("classes", "classes", true,
				"define comma separated list of fluentize classes");
		options.addOption("s", "src", true,
				"define the source location. default is '" + DEFAULT_SRC + "'");
		options.addOption("b", "bin", true,
				"define the .class files location. default is '" + DEFAULT_BIN
						+ "'");
		options.addOption("h", "help", false, "print this text");
		return options;
	}

	private final CommandLine line;
	private String config;
	private String src;
	private String packge;
	private String bin;
	private String core;
	private String[] classes;
	private boolean reset;

	private void setCore() {
		if (line.hasOption("core")) {
			core = line.getOptionValue("core");
		} else {
			core = DEFAULT_CORE;
		}
	}

	private void setConfig() {
		if (line.hasOption("config")) {
			config = line.getOptionValue("config");
		} else {
			config = DEFAULT_CONFIG;
		}
	}

	private void setBin() {
		if (line.hasOption("bin")) {
			bin = line.getOptionValue("bin");
		} else {
			bin = DEFAULT_BIN;
		}
	}

	private void setSrc() {
		if (line.hasOption("src")) {
			src = line.getOptionValue("src");
		} else {
			src = DEFAULT_SRC;
		}
	}

	private void setPackge() {
		if (line.hasOption("package")) {
			packge = line.getOptionValue("package");
		} else {
			packge = DEFAULT_PACKGE;
		}
	}

	private void setClasses() {
		if (line.hasOption("classes")) {
			classes = line.getOptionValue("classes").split(",");
		} else {
			classes = DEFAULT_CLASSES;
		}
	}

	private void setReset() {
		if (line.hasOption("reset")) {
			reset = true;
		} else {
			reset = DEFAULT_RESET;
		}
	}

	private Facere(CommandLine line) {
		this.line = line;
		setConfig();
		setSrc();
		setPackge();
		setClasses();
		setReset();
		setBin();
		setCore();
	}

	private void resetConfiguration() {
		File configFile = new File(config);
		if (configFile.exists()) {
			if (configFile.delete()) {
				log.info("Configuration file '{}' was deleted",
						configFile.getAbsolutePath());
			} else {
				log.warn("I could not delete the configuration file '{}'",
						configFile.getAbsolutePath());
			}
		} else {
			log.warn("Configuration file '{}' does not exist",
					configFile.getAbsolutePath());
		}
	}

	private void createFluentizeClass(String className) throws IOException {
		FluentizerMaker maker = new FluentizerMaker(packge, className, src,
				core);
		try {
			maker.createDefaultClass();
		} catch (Exception e) {
			log.error("can not overwrite {} in package {}", className, packge,
					e);
		}
	}

	private void createFluentizeClasses() throws IOException {
		if (classes.length == 1 && classes[0].matches("^\\d+$")) {
			final int numberOfStates = Integer.parseInt(classes[0]);
			for (int i = 0; i <= numberOfStates; i++) {
				createFluentizeClass("State" + i);
			}
		} else {
			for (String className : classes) {
				createFluentizeClass(className);
			}
		}
	}

	private void fluentizeCore() throws ClassNotFoundException, IOException, FileModifiedException {
		new ClassParser(bin, core, src).parse();
	}

	private void execute() throws ClassNotFoundException, IOException, FileModifiedException {
		if (reset) {
			resetConfiguration();
		}
		if (classes != null) {
			createFluentizeClasses();
		}
		if (core != null) {
			fluentizeCore();
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileModifiedException 
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, FileModifiedException {
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
			formatter.printHelp("ant", options);
		}
		Facere facere = new Facere(line);
		facere.execute();
	}

}
