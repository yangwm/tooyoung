package cc.tooyoung.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import cc.tooyoung.common.json.JsonBuilder;


public class TimeStatUtil {
	public final static int MC_TYPE = 1000000;
	public final static int REDIS_TYPE = 2000000;
	public final static int DB_TYPE = 3000000;
	public final static int[] TIMEARR = new int[] { 10, 20, 30, 40, 50, 100,200, 300, 500, 1000 };// 区间值列表

	private static class TimeStat {

		//定义两个map用来存储上下行各区间计数，避免获取时拼接字符串做key，影响性能
		ConcurrentHashMap<Integer, AtomicLong> wTimeMap = new ConcurrentHashMap<Integer, AtomicLong>();
		ConcurrentHashMap<Integer, AtomicLong> rTimeMap = new ConcurrentHashMap<Integer, AtomicLong>();
		AtomicLong wTotal = new AtomicLong(0);//上行总计数
		AtomicLong rTotal = new AtomicLong(0);//下行总计数

		//初始化
		{
			for (int t : TIMEARR) {
				wTimeMap.put(t, new AtomicLong(0));
				rTimeMap.put(t, new AtomicLong(0));
			}
		}

	}

	public static ConcurrentHashMap<Integer, TimeStat> map = new ConcurrentHashMap<Integer, TimeStat>();

	private static Thread printThread;

	static {
		printThread = new Thread("TimeStatUtil") {
			public void run() {
				while (true) {
					printMap();
					try {
						//5分钟执行一次
						Thread.sleep(300000);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		printThread.start();
	}
	
	public static void printMap(){
		for (int key : map.keySet()) {
			TimeStat timeStat = map.get(key);
			if(timeStat.wTotal.get() >0 || timeStat.rTotal.get()>0){
				map.put(key, new TimeStat());
				try {
					printStat(key, timeStat);
				} catch (Exception e) {
				}
			}
		}
		
	}

	//第一次执行时注册
	public static void register(int name) {
		if (map.get(name) == null) {
			map.putIfAbsent(name, new TimeStat());
		}
	}

	private static TimeStat getTimeStat(int name) {
		TimeStat timeStat = map.get(name);

		if (timeStat == null) {
			register(name);
			return map.get(name);
		}

		return timeStat;

	}

	//每次操作，将处理时间插入对应的区间内
	public static void addElapseTimeStat(int name, boolean isWriter,
			long startTime, long cost) {
		try {
			TimeStat timeStat = getTimeStat(name);
			if (cost == -1) {
				cost = System.currentTimeMillis() - startTime;
			}
			for (int t : TIMEARR) {
				if (cost < t) {
					if (isWriter) {
						timeStat.wTimeMap.get(t).incrementAndGet();
						timeStat.wTotal.incrementAndGet();
					} else {
						timeStat.rTimeMap.get(t).incrementAndGet();
						timeStat.rTotal.incrementAndGet();
					}
					return;
				}
			}
			//如果以上条件都不满足，说明大于数据最大值，则只在总数中记录
			if (isWriter) {
				timeStat.wTotal.incrementAndGet();
			} else {
				timeStat.rTotal.incrementAndGet();
			}
		} catch (Exception e) {
		}

	}

	//线程10分钟执行一次，将map中所有端口的数据输出到日志中
	public static void printStat(int key, TimeStat timestat) {
		String module;
		int port;
		if (key > DB_TYPE) {
			module = "DB";
			port = key - DB_TYPE;
		} else if (key > REDIS_TYPE) {
			module = "redis";
			port = key - REDIS_TYPE;
		} else {
			module = "memcached";
			port = key - MC_TYPE;
		}

		JsonBuilder wjb = new JsonBuilder();
		JsonBuilder rjb = new JsonBuilder();
		wjb.append("name", module + "_" + port + "_writer");
		rjb.append("name", module + "_" + port + "_read");
		wjb.append("total", timestat.wTotal.get());
		rjb.append("total", timestat.rTotal.get());
		
		for (int i = 0; i < TIMEARR.length; i++) {
			wjb.append(TIMEARR[i]+"", timestat.wTimeMap.get(TIMEARR[i]).get());
			rjb.append(TIMEARR[i]+"", timestat.rTimeMap.get(TIMEARR[i]).get());
		}
		
		/** TODO yangwm need fix 
		// 调用LogCollector
		LogCollectorFactory.getLogCollector().log(module, "resourceIntervalWriter", wjb.flip().toString());
		LogCollectorFacory.getLogCollector().log(module, "resourceIntervalRead", rjb.flip().toString());
		*/
	}

}
