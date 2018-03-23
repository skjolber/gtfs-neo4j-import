package com.github.skjolber.gtfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.input.CloseShieldInputStream;

import com.github.skjolber.gtfs.transform.TransformEngine;

public class GtfsTransformEngineRunner {

	public static void main(String[] args) throws Exception {
		
		if(args.length != 3) {
			System.out.println("Usage: <input zip file or HTTP URL> <CSV output directory> <script output directory>");
			System.exit(1);
			return;
		}
		
		File outputDirectory = new File(args[1]);
		if(!outputDirectory.exists()) {
			if(!outputDirectory.mkdirs()) {
				System.out.println("Unable to create database output directory " + outputDirectory);
				System.exit(1);
				return;
			}
		} else if(!outputDirectory.isDirectory()) {
			System.out.println(args[1] + " is not a directory");
			System.exit(1);
			return;
		}
		
		File scriptOutputDirectory = new File(args[2]);
		if(!scriptOutputDirectory.exists()) {
			if(!scriptOutputDirectory.mkdirs()) {
				System.out.println("Unable to create script output directory " + scriptOutputDirectory);
				System.exit(1);
				return;
			}
		} else if(!scriptOutputDirectory.isDirectory()) {
			System.out.println(args[2] + " is not a directory");
			System.exit(1);
			return;
		}
		
		TransformEngine build = new GtfsTransformEngineBuilder().build();
		build.setOutputDirectory(outputDirectory);
		
		InputStream fis;
		if(!args[0].contains("://")) {
			File input = new File(args[0]);
			if(!input.exists()) {
				System.out.println("File " + input + " does not exist");
				System.exit(1);
				return;
			} else if(input.isDirectory()) {
				System.out.println(args[0] + " is not a file");
				System.exit(1);
				return;
			}
			fis = new FileInputStream(input);
		} else {
			URL url = new URL(args[0]);
			
			fis = url.openStream();
		}

		try {
	        ZipInputStream zis = new ZipInputStream(fis);
	        ZipEntry entry;
	        while ((entry = zis.getNextEntry()) != null) {
	            System.out.println("Process: " + entry.getName() + ", " + entry.getSize());
	        	
	        	build.process(entry.getName(), new CloseShieldInputStream(zis));
	        }
		} finally {
			fis.close();
		}
		
        File cypher = new File(scriptOutputDirectory, "initialize.cypher");
        write(cypher, build.toCypherStatement());
        System.out.println("Wrote " + cypher);
        
        File script = new File(scriptOutputDirectory, "import.sh");
        write(script, build.toStatement());
        System.out.println("Wrote " + script);
	}

	private static void write(File cypher, String cypherStatement) throws FileNotFoundException, IOException {
		FileOutputStream fout = new FileOutputStream(cypher);
        try {
        	fout.write(cypherStatement.getBytes(StandardCharsets.UTF_8));
        } finally {
        	fout.close();
        }
	}

}
