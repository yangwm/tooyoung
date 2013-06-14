package com.tooyoung.common.stat;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.tooyoung.common.json.JsonBuilder;

public class StatLog implements Runnable {
	
	private static Logger log = Logger.getLogger("debug_stat");
	private static AtomicLong count = new AtomicLong(0);
	private static AtomicLong errorCount = new AtomicLong(0);
	private static Map<String, AtomicLong> statVars = new ConcurrentHashMap<String, AtomicLong>();
	private static Map<String, AtomicLong> lastStatVars = new ConcurrentHashMap<String, AtomicLong>();
	private static Map<String, AtomicLong> maxStatVars = new ConcurrentHashMap<String, AtomicLong>();
	private static AtomicBoolean outOfMemory = new AtomicBoolean(false);
	
	private static Map<String, ProcessStat> processStats = new ConcurrentHashMap<String, ProcessStat>();
	private static Map<String, ProcessStat> processStatsLast = new ConcurrentHashMap<String, ProcessStat>();
	private static Map<String, ProcessStat> processStatsMax = new ConcurrentHashMap<String, ProcessStat>();
	
	private static AtomicBoolean pausePrint = new AtomicBoolean(false);
	private static Map<String, ThreadPoolExecutor> executors = new ConcurrentHashMap<String, ThreadPoolExecutor>();
	
	private static Set<String> cacheStatKeys = new HashSet<String>();
	
	static {
		printStat(5000);
	}
	public static void setPausePrint(boolean print) {
		pausePrint.set(print);
	}
	
	public static long inc() {
		return count.incrementAndGet();
	}

	public static long get() {
		return count.get();
	}
	
	public static long dec() {
		return count.decrementAndGet();
	}
	
	public static synchronized void registerVar(String var) {
		if(statVars.get(var) == null){
			statVars.put(var, new AtomicLong(0));
			lastStatVars.put(var, new AtomicLong(0));
			maxStatVars.put(var, new AtomicLong(0));
		}		
	}
	
	public static void registerExecutor(String name, ThreadPoolExecutor executor){
		executors.put(name, executor);
	}

	public static long inc(String var) {
		return inc(var, 1);
	}

	public static long inc(String var, int value) {
		AtomicLong c = statVars.get(var);
		if(c == null){
			registerVar(var);
			c = statVars.get(var);
		}
		
		long r = c.addAndGet(value);
		if (r < 0) {
			r = 0;
			c.set(0);
		}
		return r;
	}
	
	public static long dec(String var) {
		AtomicLong c = statVars.get(var);
		if (c != null)
			return c.decrementAndGet();
		else
			return 0;
	}
	
	public static long inc(int delta) {
		return count.addAndGet(delta);
	}
	
	public static void incProcessTime(String var, int processCount, long processTime){
		incProcessTime(processStats, var, processCount, processTime);
		incProcessTime(processStatsLast, var, processCount, processTime);
	}
	
	private static void incProcessTime(Map<String, ProcessStat> pstats, String var, int processCount, long processTime){
		ProcessStat ps = pstats.get(var);
		if(ps == null){
			ps = new ProcessStat();
			pstats.put(var, ps);
		}
		ps.addStat(processCount, processTime);
	}

	public static long incError() {
		return errorCount.incrementAndGet();
	}

	public static long decError() {
		return errorCount.decrementAndGet();
	}

	public static long getError() {
		return errorCount.get();
	}
	
	
	public static long incError(int delta) {
		return errorCount.addAndGet(delta);
	}
	
	private static long startTime;
	private static long interval;
	
	public StatLog(long startTime2, long interval2) {
		startTime = startTime2;
		interval = interval2;
	}
	
	public static void resetStartTime(long newTime) {
		startTime = newTime;
	}
	
	public static void addCacheStatKeySuffix(String keySuffix){
		cacheStatKeys.add(keySuffix);
	}
	
	/**
	 * 判断某个key是否是cache 统计key
	 * @param key
	 * @return
	 */
	public static boolean isCacheStatkey(String keySuffix){
		if(keySuffix == null){
			return false;
		}
		return cacheStatKeys.contains(keySuffix);
	}
	
	/**
	 * print stat info on the screen, this method will block until total is reached,
	 * @param total, -1 for infinity
	 * @param interval how long (second) to print a stat log
	 */
	public static StatLog printStat(long interval) {
		log.info("Start Api Server stat log.");
		StatLog t = new StatLog(System.currentTimeMillis(), interval);
		Thread thread = new Thread(t);
		thread.setDaemon(true);
		thread.start();
		
		return t;
	}
	
	public void run() {
		long lastCount = 0;
		long cnt = 0;
		long lastTime = 0;
		long max = 0;
		while (true) {
			
			try {
					synchronized (this) {
						wait(interval);
					}
					// Thread.sleep(interval);
				
				
				if (pausePrint.get())
					continue;
				
				long time2 = System.currentTimeMillis();
				if (time2 == 0)
					break;
				if (time2 == startTime)
					continue;
				cnt = count.get();
				long cur = (cnt - lastCount) * 1000l / (time2 - lastTime);
				if (cur > max)  max = cur;
				
				log.info("---------------------------");
				log.info("JAVA HEAP: " + memoryReport() + ", UP TIME: " + ((time2 - startTime) / 1000) + ", min: " + ((time2 - startTime) / 60000));
				SortedSet<String> keys = new TreeSet<String>(statVars.keySet());
				StringBuilder sb = new StringBuilder("[");
				boolean firstLoop = true;
				for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
					String var = iterator.next();
					AtomicLong c = statVars.get(var);
					AtomicLong last1 = lastStatVars.get(var);
					AtomicLong m1 = maxStatVars.get(var);
					
					long cnt1 = c.get();
					if (cnt1 == 0)
						continue;
					long max1 = m1.get();
					long lastCount1 = last1.get();
					
					long avg1 = cnt1 * 1000l / (time2 - startTime);
					long cur1 = (cnt1 - lastCount1) * 1000l / (time2 - lastTime);
					if (cur1 > max1)  max1 = (int) cur1;
	
					if (!firstLoop)
						sb.append(",");
					else
						firstLoop = false;
	
					// json-style output
					sb.append("{\"").append(var).append("\":[").append(cnt1).append(",")
						.append(avg1).append(",").append(cur1).append(",").append(max1).append("]}\n");
					
					m1.set(max1);
					last1.set(cnt1);
				}
				sb.append("]");
				log.info(sb.toString());
				
				//stat process time
				if(processStats.size() > 0){
					log.info(statProcessSt());
				}
				//stat executors
				sb.delete(0, sb.length());
				sb.append("pool:[ ");
				StringBuilder jsonLog = new StringBuilder();
				jsonLog.append("[");
				int i = 0;
				for(Map.Entry<String, ThreadPoolExecutor> entry : executors.entrySet()){
					sb.append(statExecutor(entry.getKey(), entry.getValue())).append(", ");
					if (i++ > 0) {
						jsonLog.append(",");
					}
					jsonLog.append(statJsonExecutor(entry.getKey(), entry.getValue()));
				}
				jsonLog.append("]");
				log.info(sb.append(" ]"));
				/** TODO yangwm need fix 
				LogCollectorFactory.getLogCollector().log("common-pool", "threadpool", jsonLog.toString());
				*/
				
				/** TODO yangwm need fix 
				//log clientBalancer stat log
				log.info(ClientBalancerStatLog.getStatStr());
				*/
				
				lastTime = time2;
				lastCount = cnt;
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		log.info("Stat log stop");
	}
	
	
	private StringBuilder statProcessSt(){
		StringBuilder pstatSb = new StringBuilder(processStats.size() * 64).append("processStat: ");
		for(Map.Entry<String, ProcessStat> entry : processStats.entrySet()){
			String psKey = entry.getKey();
			ProcessStat ps = entry.getValue();
			ProcessStat psLast = processStatsLast.get(psKey);
			ProcessStat psMax = processStatsMax.get(psKey);
			if(psMax == null || psMax.getAvgTime() < psLast.getAvgTime()){
				processStatsMax.put(psKey, psLast);
				psMax = processStatsMax.get(psKey);
			}
			
			if(ps.getAvgTime() > 0){
				pstatSb.append(entry.getKey()).append("{")
					.append(ps.getCount()).append("=").append(ps.getAvgTime()).append(",")
					.append(psLast.getCount()).append("=").append(psLast.getAvgTime()).append(",")
					.append(psMax.getCount()).append("=").append(psMax.getAvgTime()).append("},\n ");
			}
			//reset last stat
			processStatsLast.put(psKey, new ProcessStat());
		}
		if (pstatSb.lastIndexOf(",") > 0) {
			pstatSb.delete(pstatSb.lastIndexOf(","), pstatSb.length() - 1);
		}
		return pstatSb;
	}
	
	public static String memoryReport() {
		Runtime runtime = Runtime.getRuntime();

		double freeMemory = (double) runtime.freeMemory() / (1024 * 1024);
		double maxMemory = (double) runtime.maxMemory() / (1024 * 1024);
		double totalMemory = (double) runtime.totalMemory() / (1024 * 1024);
		double usedMemory = totalMemory - freeMemory;
		double percentFree = ((maxMemory - usedMemory) / maxMemory) * 100.0;
		if (percentFree < 10) {
			outOfMemory.set(true);
			log.error("Detected OutOfMemory potentia memory > 90%, stop broadcast presence !!!!!!");
			
		} else if (outOfMemory.get() == true && percentFree > 20) {
			outOfMemory.set(false);
			log.error("Detected memory return to normal, memory < 80%, resume broadcast presence.");
		}
		double percentUsed = 100 - percentFree;
		// int percent = 100 - (int) Math.round(percentFree);

		DecimalFormat mbFormat = new DecimalFormat("#0.00");
        DecimalFormat percentFormat = new DecimalFormat("#0.0");
		
        StringBuilder sb = new StringBuilder(" ");
		sb.append(mbFormat.format(usedMemory)).append("MB of ").append(mbFormat.format(maxMemory))
		.append(" MB (").append(percentFormat.format(percentUsed)).append("%) used");
		return sb.toString();
	}

	public static boolean isOutOfMemory() {
		return outOfMemory.get();
	}
	
	private String statExecutor(String name, ThreadPoolExecutor executor){
		StringBuilder strBuf = new StringBuilder(32);
		strBuf.append(name).append("{").append(executor.getQueue().size()).append(",")
			.append(executor.getCompletedTaskCount()).append(",")
			.append(executor.getTaskCount()).append(",")
			.append(executor.getActiveCount()).append(",")
			.append(executor.getCorePoolSize()).append("}\n");
		return strBuf.toString();
	}
	
	private String statJsonExecutor(String name, ThreadPoolExecutor executor){
		JsonBuilder jb = new JsonBuilder();
		jb.append("name", name);
		jb.append("act", executor.getActiveCount());
		jb.append("max", executor.getCorePoolSize());
		jb.flip();
		return jb.toString();	
	}

	public static class ProcessStat{
		 public AtomicLong count = new AtomicLong();
		 public AtomicLong time = new AtomicLong();
		 
		 public ProcessStat(){
		 }
		 
		 private void addStat(int pcount, long ptime){
			 this.count.addAndGet(pcount);
			 this.time.addAndGet(ptime);
		 }
		 
		 private long getCount(){
			 return count.get();
		 }
		 
		 private long getAvgTime(){
			 if(this.count.get() > 0){
				 return this.time.get() / this.count.get();
			 }
			 return 0;
		 }
	}
	
}
