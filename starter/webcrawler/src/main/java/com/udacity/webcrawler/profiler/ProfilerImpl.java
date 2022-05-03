package com.udacity.webcrawler.profiler;

import java.lang.reflect.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler
{
  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock)
  {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) throws IllegalArgumentException
  {
    Objects.requireNonNull(klass);
	
	boolean found = false;
	
	for( Method M: klass.getMethods() )
	{
		if( M.getAnnotation(Profiled.class) != null )
		{
			found = true;
			break;
		}
	}
	
	if(!found)
		throw new IllegalArgumentException("No Profiled Method Found");

    // TODO: Use a dynamic proxy (java.lang.reflect.Proxy) to "wrap" the delegate in a
    //       ProfilingMethodInterceptor and return a dynamic proxy from this method.
    //       See https://docs.oracle.com/javase/10/docs/api/java/lang/reflect/Proxy.html.
	
	//delegate = (Object) delegate;
	
	//return delegate.getClass().cast()
	
	return (T) Proxy.newProxyInstance
				(
					ProfilerImpl.class.getClassLoader(),
					new Class<?>[] { klass },
					new ProfilingMethodInterceptor(clock, state, delegate)
				);

    //return delegate;
  }

  @Override
  public void writeData(Path path) throws IOException
  {
    // TODO: Write the ProfilingState data to the given file path. If a file already exists at that
    //       path, the new data should be appended to the existing file.
	
	Writer writer;
	
	if( Files.exists(path) )
		writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND);
	else
		writer = Files.newBufferedWriter(path);
	
	writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
	
	writer.close();
  }

  @Override
  public void writeData(Writer writer) throws IOException
  {
    writer.write("\nRun at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
	
	//writer.close();
  }
}
