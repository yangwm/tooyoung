/**
 * 
 */
package com.tooyoung.common.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.tooyoung.common.util.ApiLogger;


/**
 * 
 * 
 * @author yangwm Dec 14, 2012 1:03:47 PM
 */
public class ExecutorServiceUtil {
    
    public static <T> T get(ThreadPoolExecutor threadPoolExecutor,
            Callable<T> task, long timeout, TimeUnit unit, boolean cancelFuture) throws InterruptedException {
        T result = null;
        Future<T> future = null;
        try {
            /*
             * sumbit task  
             */
            future = threadPoolExecutor.submit(task);
            try {
                result = future.get(timeout, unit);
            } catch (CancellationException ignore) {
            } catch (ExecutionException ignore) {
            } catch (TimeoutException toe) {
                return result;
            }
            return result;
        } finally {
            if (future != null && !future.isDone()) {
                // log warn not done 
                ApiLogger.warn("ExecutorServiceUtil invoke notDone, name:" + threadPoolExecutor.getThreadFactory().toString());
                
                if (cancelFuture) {
                    future.cancel(true);
                }
            }
        }

    }

    public static <T> List<Future<T>> invokeAll(ThreadPoolExecutor threadPoolExecutor,
            Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit, boolean cancelFuture) throws InterruptedException {
        if (tasks == null || unit == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        List<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
        boolean done = false;
        try {
            /*
             * sumbit tasks 
             */
            long lastTime = System.nanoTime();
            for (Callable<T> t : tasks) {
                futures.add(threadPoolExecutor.submit(t));
                
                long now = System.nanoTime();
                nanos -= now - lastTime;
                lastTime = now;
                if (nanos <= 0) {
                    return futures;
                }
            }
            
            /*
             * get results from futures
             */
            for (Future<T> f : futures) {
                if (!f.isDone()) {
                    if (nanos <= 0) {
                        return futures;
                    }
                    try {
                        f.get(nanos, TimeUnit.NANOSECONDS);
                    } catch (CancellationException ignore) {
                    } catch (ExecutionException ignore) {
                    } catch (TimeoutException toe) {
                        return futures;
                    }
                    long now = System.nanoTime();
                    nanos -= now - lastTime;
                    lastTime = now;
                }
            }
            done = true;
            return futures;
        } finally {
            if (!done) {
                // log warn not done 
                ApiLogger.warn("ExecutorServiceUtil invokeAll notDone, name:" + threadPoolExecutor.getThreadFactory().toString());
                
                if (cancelFuture) {
                    for (Future<T> f : futures) {
                        f.cancel(true);
                    }
                }
            }
        }
    }

}
