package main;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import GUI.Explorer;



public class Start 
{
	
	public static void main(String[] args)
	{	
		@SuppressWarnings("unused")
		Explorer explorer = new Explorer();
	
	}
	
	
	
	public static void restartApplication() throws URISyntaxException, IOException
	{
	  final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
	  final File currentJar = new File(Start.class.getProtectionDomain().getCodeSource().getLocation().toURI());


	  if(!currentJar.getName().endsWith(".jar"))
	    return;

	  final ArrayList<String> command = new ArrayList<String>();
	  command.add(javaBin);
	  command.add("-jar");
	  command.add(currentJar.getPath());

	  final ProcessBuilder builder = new ProcessBuilder(command);
	  builder.start();
	  System.exit(0);
	}

}
