package com.udacity.webcrawler.json;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser.Feature;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() throws Exception
  {
    // TODO: Fill in this method.
	
	String config;
	
	try
	{
		config = Files.lines(path).reduce( (s1, s2) -> s1 + s2).orElse(null);
		
		if( config == null )
			throw new Exception("File I/O Error\n");
	}
	catch( Exception E )
	{
		System.out.println("\nFile I/O Error ...");
		System.out.println("Ending Application ...\n");
		System.exit(1);
		
		return new CrawlerConfiguration.Builder().build();
	}
	
	CrawlerConfiguration crawlConfig = null;
	
	try( Reader SR = new StringReader(config) )
	{
		crawlConfig = this.read(SR);
	}
	catch(Throwable T)
	{
		T.printStackTrace();
	}
	
	return crawlConfig;
    
	//return new CrawlerConfiguration.Builder().build();
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) throws Exception
  {
    	// This is here to get rid of the unused variable warning.
    Objects.requireNonNull(reader);
	
		// TODO: Fill in this method
	
	ObjectMapper OM = new ObjectMapper();
	
	OM.disable(Feature.AUTO_CLOSE_SOURCE);
	
	CrawlerConfiguration crawlConfig = null;
	
	try
	{
		crawlConfig = OM.readValue(reader, CrawlerConfiguration.class);
	}
	catch(Throwable T)
	{
		System.out.println("\nIncorrectly Formatted Configuration File ...");
		System.out.println("Ending Application ...\n");
		System.exit(1);
	}
	
	return crawlConfig;
	
    //return new CrawlerConfiguration.Builder().build();
  }
}
