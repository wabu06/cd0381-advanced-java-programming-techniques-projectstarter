package com.udacity.webcrawler.json;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonGenerator.Feature;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   */
  public void write(Path path) throws Exception
  {
    	// This is here to get rid of the unused variable warning.
    Objects.requireNonNull(path);
	
    // TODO: Fill in this method.
	
	ObjectMapper OM = new ObjectMapper();
	
	if( Files.exists(path) )
		OM.writeValue( Files.newBufferedWriter(path, StandardOpenOption.APPEND), result );
	else
		OM.writeValue( Files.newBufferedWriter(path), result );
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   */
  public void write(Writer writer) throws Exception
  {
    	// This is here to get rid of the unused variable warning.
    Objects.requireNonNull(writer);
	
    // TODO: Fill in this method.
	
	ObjectMapper OM = new ObjectMapper();
	
	OM.disable(Feature.AUTO_CLOSE_TARGET);
	
	System.out.println();
	OM.writeValue(writer, result);
	System.out.println();
  }
}
