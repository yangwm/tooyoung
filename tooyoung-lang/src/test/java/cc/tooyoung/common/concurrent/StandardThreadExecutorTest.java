/**
 * 
 */
package cc.tooyoung.common.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author yuanming
 *
 */
public class StandardThreadExecutorTest {

	@Test
	public void testStandardThreadExecutor(){
		int minThread = 2;
		int maxThead = 10;
		StandardThreadExecutor executor = new StandardThreadExecutor(minThread,maxThead);
		for(int i=0;i<minThread;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(minThread,executor.getPoolSize());
		for(int i=minThread;i<maxThead;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(maxThead,executor.getPoolSize());
		for(int i=0;i<maxThead;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(maxThead,executor.getQueueSize());
		try{
			executor.submit(new SleepTask(10000));
			Assert.fail();
		}catch(RejectedExecutionException e){
			Assert.assertTrue(true);
		}
	}
	
	
	@Test
	public void testStandardThreadExecutorQueueLimit(){
		int minThread = 2;
		int maxThead = 10;
		int queueLength = 3;
		StandardThreadExecutor executor = new StandardThreadExecutor(minThread,maxThead,queueLength);
		for(int i=0;i<minThread;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(minThread,executor.getPoolSize());
		for(int i=minThread;i<maxThead;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(maxThead,executor.getPoolSize());
		for(int i=0;i<queueLength;i++){
			executor.submit(new SleepTask(10000));
		}
		System.out.println(executor.getSubmittedTasksCount());
		Assert.assertEquals(maxThead+queueLength,executor.getSubmittedTasksCount());
		Assert.assertEquals(queueLength,executor.getQueueSize());
		try{
			executor.submit(new SleepTask(10000));
			Assert.fail();
		}catch(RejectedExecutionException e){
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testJdkThreadExecutor(){
		int minThread = 2;
		int maxThead = 10;
		ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(maxThead);
		ThreadPoolExecutor executor =  new ThreadPoolExecutor(minThread,maxThead, 60, TimeUnit.SECONDS,queue,Executors.defaultThreadFactory());
		for(int i=0;i<minThread;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(minThread,executor.getPoolSize());
		for(int i=minThread;i<maxThead;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(2,executor.getPoolSize());
		Assert.assertEquals(8,queue.size());
		for(int i=0;i<maxThead;i++){
			executor.submit(new SleepTask(10000));
		}
		Assert.assertEquals(maxThead,executor.getPoolSize());
		try{
			executor.submit(new SleepTask(10000));
			Assert.fail();
		}catch(RejectedExecutionException e){
			Assert.assertTrue(true);
		}
	}
	
	public static class SleepTask implements Runnable{
		private long time;
		
		private SleepTask(long time) {
			this.time = time;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
