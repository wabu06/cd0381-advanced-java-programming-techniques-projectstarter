package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.Duration;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler
{
  private final Clock clock;
  private final ProfilingState state;
  private final Object delegate;
  private Instant start;
  private Duration passed;

  			// TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, ProfilingState state, Object delegate)
  {
    this.clock = Objects.requireNonNull(clock);
	this.state = Objects.requireNonNull(state);
	this.delegate = Objects.requireNonNull(delegate);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
  {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
	
	Object result;
	
	if( method.getAnnotation(Profiled.class) != null )
	{
		if( method.getParameterCount() != 0 )
		{
			try
			{
				start = clock.instant();
			
				result =  method.invoke(delegate, args);
			}
			catch(Exception exp)
			{}
			finally
			{
				passed = Duration.between( start, clock.instant() );
				state.record( delegate.getClass(), method, passed );
			}
		}
		else
		{
			try
			{
				start = clock.instant();
			
				result =  method.invoke(delegate);
			}
			catch(Exception exp)
			{}
			finally
			{
				passed = Duration.between( start, clock.instant() );
				state.record( delegate.getClass(), method, passed );
			}
		}
	}
	else
	{
		if( method.getParameterCount() != 0 )
			result =  method.invoke(delegate, args);
		else
			result =  method.invoke(delegate);
	}

    return result;
  }
}
