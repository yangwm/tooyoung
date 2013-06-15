/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.tooyoung.common.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 本类主要逻辑是从Tomcat源码中移植过来
 *
 * 主要特点是：
 * 如果线程数超过 minSpareThreads,则会增加thread直到 maxThreads，然后才放入队列.队列长度为 maxThreads.
 * 
 */
public class StandardThreadExecutor extends java.util.concurrent.AbstractExecutorService {
	
	public static final int DEFAULT_MIN_SPARE_THREADS = 2;
	public static final int DEFAULT_MAX_THREADS = 200;
    
    // ---------------------------------------------- Properties
    protected int threadPriority = Thread.NORM_PRIORITY;

    protected boolean daemon = true;
    
    protected String namePrefix = "weibo-exec-";
    
    protected int maxThreads = 200;
    
    protected int minSpareThreads = 2;
    
    protected int maxIdleTime = 60000;
    
    protected ThreadPoolExecutor executor = null;
    
    protected String name;
    
    /**
     * Number of tasks submitted and not yet completed.
     */
    protected AtomicInteger submittedTasksCount;
    
    protected int taskQueueCapacity;
    
    public StandardThreadExecutor(){
    	this(DEFAULT_MIN_SPARE_THREADS,DEFAULT_MAX_THREADS);
    }
    
    public StandardThreadExecutor(int minSpareThreads,int maxThreads) {
    	 this(minSpareThreads,maxThreads,maxThreads);
    }
    
    public StandardThreadExecutor(int minSpareThreads,int maxThreads,int taskQueueCapacity) {
    	this.taskQueueCapacity = taskQueueCapacity;
    	submittedTasksCount = new AtomicInteger();
   	 	TaskQueue taskqueue = new TaskQueue(taskQueueCapacity);
        TaskThreadFactory tf = new TaskThreadFactory(namePrefix);
        this.minSpareThreads = minSpareThreads;
        this.maxThreads = maxThreads;
        executor = new ThreadPoolExecutor(minSpareThreads,maxThreads, maxIdleTime, TimeUnit.MILLISECONDS,taskqueue, tf) {
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				AtomicInteger atomic = submittedTasksCount;
				if(atomic!=null) {
					atomic.decrementAndGet();
				}
			}
        };
        taskqueue.setParent( (ThreadPoolExecutor) executor);
   }
    
    
	public void execute(Runnable command) {
		submittedTasksCount.incrementAndGet();
		try {
			executor.execute(command);
		} catch (RejectedExecutionException rx) {
			// there could have been contention around the queue
			if (!((TaskQueue) executor.getQueue()).force(command)) {
				submittedTasksCount.decrementAndGet();
				// throw new RejectedExecutionException();
				ApiLogger.warn("StandardThreadExecutor drop-thread");
				throw new RejectedExecutionException();
			}
		}
	}

    public int getThreadPriority() {
        return threadPriority;
    }

    public boolean isDaemon() {

        return daemon;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinSpareThreads() {
        return minSpareThreads;
    }

    public String getName() {
        return name;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        if (executor != null) {
            executor.setKeepAliveTime(maxIdleTime, TimeUnit.MILLISECONDS);
        }
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        executor.setMaximumPoolSize(maxThreads);
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        executor.setCorePoolSize(minSpareThreads);
    }

    public void setName(String name) {
        this.name = name;
    }

    // Statistics from the thread pool
    public int getActiveCount() {
    	return executor.getActiveCount();
    }
    
    public int getSubmittedTasksCount(){
    	return this.submittedTasksCount.get();
    }

    public long getCompletedTaskCount() {
    	return executor.getCompletedTaskCount();
    }

    public int getCorePoolSize() {
    	return	executor.getCorePoolSize();
    }

    public int getLargestPoolSize() {
    	return  executor.getLargestPoolSize();
    }

    public int getPoolSize() {
    	return executor.getPoolSize();
    }

    /**
     * 任务队列当前长度
     * @return
     */
    public int getQueueSize() {
    	return executor.getQueue().size();
    }
    
    /**
     * 等待任务队列最大长度
     * @return
     */
    public int getQueueCapacity(){
    	return this.taskQueueCapacity;
    }

    // ---------------------------------------------- TaskQueue Inner Class
    class TaskQueue extends LinkedBlockingQueue<Runnable> {
        ThreadPoolExecutor parent = null;

        public TaskQueue() {
            super();
        }

        public TaskQueue(int initialCapacity) {
            super(initialCapacity);
        }

        public TaskQueue(Collection<? extends Runnable> c) {
            super(c);
        }

        public void setParent(ThreadPoolExecutor tp) {
            parent = tp;
        }
        
        public boolean force(Runnable o) {
            if ( parent.isShutdown() ) throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
            return super.offer(o); //forces the item onto the queue, to be used if the task is rejected
        }

        public boolean offer(Runnable o) {
            //we can't do any checks
            if (parent==null) return super.offer(o);
            int poolSize = parent.getPoolSize();
            //we are maxed out on threads, simply queue the object
            if (poolSize == parent.getMaximumPoolSize()) return super.offer(o);
            //we have idle threads, just add it to the queue
            //note that we don't use getActiveCount(), see BZ 49730
			AtomicInteger submittedTasksCount = StandardThreadExecutor.this.submittedTasksCount;
			if(submittedTasksCount!=null) {
				if (submittedTasksCount.get()<=poolSize) return super.offer(o);
			}
            //if we have less threads than maximum force creation of a new thread
            if (poolSize<parent.getMaximumPoolSize()) return false;
            //if we reached here, we need to add it to the queue
            return super.offer(o);
        }
    }

    // ---------------------------------------------- ThreadFactory Inner Class
    class TaskThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        TaskThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(daemon);
            t.setPriority(getThreadPriority());
            return t;
        }
    }

	@Override
	public void shutdown() {
		this.executor.shutdown();
	}



	@Override
	public List<Runnable> shutdownNow() {
		return this.executor.shutdownNow();
	}



	@Override
	public boolean isShutdown() {
		return this.executor.isShutdown();
	}



	@Override
	public boolean isTerminated() {
		return this.executor.isTerminated();
	}



	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return this.executor.awaitTermination(timeout, unit);
	}
    

}
