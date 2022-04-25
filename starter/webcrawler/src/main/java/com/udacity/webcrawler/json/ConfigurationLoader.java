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
	
	String line, config = "";
	
	try( BufferedReader R = Files.newBufferedReader(path) )
	{
		while( ( line = R.readLine() ) != null )
			config = config + line + "\n";
	}
	catch( Exception E )
	{
		System.out.println("Error reading config file\n");
		return new CrawlerConfiguration.Builder().build();
	}
	
	return this.read( new StringReader(config) );
    
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
	
	ObjectMapper OM = new ObjectMapper();
	
	OM.disable(Feature.AUTO_CLOSE_SOURCE);
	
	CrawlerConfiguration CC = OM.readValue(reader, CrawlerConfiguration.class);
	
	return CC;
	
    // TODO: Fill in this method

    //return new CrawlerConfiguration.Builder().build();
  }
}
