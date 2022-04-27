package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
import java.util.*;
import java.util.concurrent.*;
// import java.util.concurrent.ConcurrentSkipListSet;
// import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import java.util.regex.Pattern;



/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler
{
  	private final Clock clock;
	private final PageParserFactory parserFactory;
  	private final Duration timeout;
  	private final int popularWordCount;
	private final int maxDepth;
  	private final List<Pattern> ignoredUrls;
  	private final ForkJoinPool pool;

  	@Inject
  	ParallelWebCrawler
		(
			Clock clock,
			PageParserFactory parserFactory,
			@Timeout Duration timeout,
			@PopularWordCount int popularWordCount,
			@MaxDepth int maxDepth,
      		@IgnoredUrls List<Pattern> ignoredUrls,
			@TargetParallelism int threadCount
		)
	{
    	this.clock = clock;
		this.parserFactory = parserFactory;
    	this.timeout = timeout;
    	this.popularWordCount = popularWordCount;
		this.maxDepth = maxDepth;
    	this.ignoredUrls = ignoredUrls;
    	this.pool = new ForkJoinPool( Math.min(threadCount, getMaxParallelism()) );
  	}
	
	public static final class CrawlInternal extends RecursiveAction
	{
		private Clock clock;
		private PageParserFactory parserFactory;
		
		private String url;
		
		private Instant deadline;
		
		private int maxDepth;
		
    	private Map<String, Integer> counts;
		
    	private Set<String> visitedUrls;
		
		private List<Pattern> ignoredUrls;
		
		public CrawlInternal
			(
				Clock clock,
				PageParserFactory parserFactory,
				String url,
				Instant deadline,
				int maxDepth,
				Map<String, Integer> counts,
				Set<String> visitedUrls,
				List<Pattern> ignoredUrls
			)
		{
			this.clock = clock;
			this.parserFactory = parserFactory;
			this.url = url;
			this.deadline = deadline;
			this.maxDepth = maxDepth;
			this.counts = counts;
			this.visitedUrls = visitedUrls;
			this.ignoredUrls = ignoredUrls;
		}
		
		@Override
 		protected void compute()
		{
			if( maxDepth == 0 || clock.instant().isAfter(deadline) )
      			return;
				
			for (Pattern pattern : ignoredUrls)
			{
      			if ( pattern.matcher(url).matches() )
        			return;
    		}
			
    		if( visitedUrls.contains(url) )
      			return;

    		visitedUrls.add(url);
			
			PageParser.Result result = parserFactory.get(url).parse();
			
			for (String link : result.getLinks())
      			new CrawlInternal(clock, parserFactory, link, deadline, maxDepth - 1, counts, visitedUrls, ignoredUrls).invoke();
			
			//result.getLinks().stream()
						//.forEach( u -> invoke(  new crawlInternal(clock, PF, u, deadline, maxDepth - 1, counts, visitedUrls)) );
			
			for ( Map.Entry<String, Integer> e : result.getWordCounts().entrySet() )
			{
      			if( counts.containsKey(e.getKey()) )
				{
        			counts.put(e.getKey(), e.getValue() + counts.get(e.getKey()));
      			}
				else
				{
        			counts.put(e.getKey(), e.getValue());
      			}
    		}
		}
	}

  	@Override
  	public CrawlResult crawl(List<String> startingUrls)
	{
		Instant deadline = clock.instant().plus(timeout);
		
    	Map<String, Integer> counts = new ConcurrentHashMap<>();
		
    	Set<String> visitedUrls = new CopyOnWriteArraySet<>();
		
		//List<CrawlInternal> pages = new ArrayList<>();
		
		CrawlInternal CI;
		
		for(String url: startingUrls)
		{
			CI = new CrawlInternal(clock, parserFactory, url, deadline, maxDepth, counts, visitedUrls, ignoredUrls);
			//pages.add(CI);
			pool.invoke(CI);
		}
		
		if( counts.isEmpty() )
		{
      		return new CrawlResult.Builder()
          		.setWordCounts(counts)
          		.setUrlsVisited( visitedUrls.size() )
          		.build();
    	}
		else
		{
			return new CrawlResult.Builder()
        		.setWordCounts( WordCounts.sort(counts, popularWordCount) )
        		.setUrlsVisited( visitedUrls.size() )
        		.build();
		}
		
    	//return new CrawlResult.Builder().build();
  	}

  	@Override
  	public int getMaxParallelism()
	{
    	return Runtime.getRuntime().availableProcessors();
  	}
}
