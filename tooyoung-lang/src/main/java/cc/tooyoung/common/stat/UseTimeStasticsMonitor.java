/*
 * UseTimeStasticsMonitor.java created on 2010-7-26 上午10:44:19 by bwl (Liu Daoru)
 */

package cc.tooyoung.common.stat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import cc.tooyoung.common.json.JsonBuilder;
import cc.tooyoung.common.util.ApiLogger;

/**
 * Cache命中率和访问次数统计类
 * 
 */
public class UseTimeStasticsMonitor {

	public static final UseTimeStasticsMonitor webCommonMonitor = new UseTimeStasticsMonitor(
		"webCommonMonitor"); // web common 访问统计

	/**
	 * monitor池
	 */
	private static ConcurrentHashMap<String, UseTimeStasticsMonitor> STATIC_MONITORS = 
				new ConcurrentHashMap<String, UseTimeStasticsMonitor>(1000); 

	/**
	 * 间隔时间调整为30秒
	 */
	public static final long MILES_FIVE_MINUTES = 1000l * 30;
	
	/**
	 * 一天的毫秒数
	 */
	public static final long MILES_PER_DAY = 1000l * 60 * 60 * 24;

	/**
	 * 慢操作的编辑时间
	 */
	public static final int MILES_SLOW_PROC = 200;
	
	/**
	 * cache的名称，输出时标记cache用
	 */
	private String name = null;

	/**
	 * 五分钟的序号
	 */
	private AtomicLong minute5Index = new AtomicLong(0);

	/**
	 * 五分钟命中用户昵称和用户id cache的数量
	 */
	private AtomicLong hitCount = new AtomicLong(0);

	/**
	 * 五分钟全部请求次数
	 */
	private AtomicLong visitCount = new AtomicLong(0);

	/**
	 * 任务处理时间时间
	 */
	private AtomicLong totalMiles = new AtomicLong(0);

	/**
	 * 随机时延，防止每次都是5分的倍数导致log过于集中
	 */
	private AtomicLong randDelay = new AtomicLong(0);
	
	private static  AtomicBoolean isPause = new AtomicBoolean(false);
	
	/**
	 * 暂停采集
	 */
	public static void setPause(boolean pause){
		isPause.compareAndSet(! pause, pause);
	}
	/**
	 * 获取当前统计状态
	 * @return 
	 */
	public static boolean getStatus(){
		return isPause.get();
	}
	
	public UseTimeStasticsMonitor(String moduleName){
		this.name = moduleName;
		this.randDelay.compareAndSet(0, (long) (MILES_FIVE_MINUTES * Math.random()));
	}

	/**
	 * 任务开始
	 */
	public LinkedList<Long> start(LinkedList<Long> stamps, boolean debug) {
		if(isPause.get()) return null; //暂停采集
		if (stamps == null) {
			stamps = new LinkedList<Long>();
		}
		stamps.add(getCurrentTime());
		return stamps;
	}

	/**
	 * 标记时间记录点
	 * @param stamps
	 */
	public void mark(LinkedList<Long> stamps, boolean debug) {
		if (stamps == null) {
			return;
		}
		// 非debug模式，不记录时间戳
		stamps.add(getCurrentTime());
	}

	/**
	 * 获取当前时间戳
	 * @return
	 */
	private long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 任务结束
	 */
	public void end(LinkedList<Long> stamps, boolean hit, boolean debug) {
		if (stamps == null) { // 如果时间戳数组为null，则直接返回
			return;
		}
		end(stamps, null, hit, debug);
	}
	
	/**
	 * 任务结束
	 */
	public void end(LinkedList<Long> stamps, String debugInfo, boolean hit, boolean debug) {
		end(stamps, debugInfo, hit, debug, 0);
	}
	
	/**
	 * 任务结束
	 * @param debugLimit 输出调试信息的边界值，超过这个值才打印详细日志
	 */
	public void end(LinkedList<Long> stamps, String debugInfo, boolean hit, boolean debug, int debugLimit) {
		if (stamps == null) { // 如果时间戳数组为null，则直接返回
			return;
		}
		long last = getCurrentTime();
		stamps.add(last);
		//
		long start = stamps.removeFirst(); // 获取起始时间
		long cur;
		long useTime = 0L;
		// 处理各段处理时间日志
		if (stamps.size() > 0) { // 超过两条时间时才记录最后时间
			cur = stamps.getLast();
			useTime = cur - start;
		}
		cur = start;
		StringBuilder sb = null;
		if (debug || (debugLimit > 0 && useTime > debugLimit)) { // process timestamps when debug is true
			sb = new StringBuilder(64);
			sb.append("[useTime-").append(this.name).append("]");
			long temp = 0L;
			int index = 1;
			List<Long> steps = new ArrayList<Long>();
			for (Long stamp : stamps) {
				temp = cur;
				cur = stamp;
				sb.append("\t").append(index++).append(":").append(cur - temp);
				steps.add(cur - temp);
			}
			sb.append("\tTotal:").append(useTime);
			if (debugInfo != null) {
				sb.append("\tdebugInfo:").append(debugInfo);
			}
			String log = sb.toString();
			if (debug) {
				ApiLogger.debug(log); // 日志记录
			} else {
				ApiLogger.warn(log); // 日志记录
			}
			JsonBuilder jb = new JsonBuilder();
			jb.append("cost", useTime);
			jb.appendLongList("steps", steps);
			jb.flip();
			/** TODO yangwm need fix 
			LogCollectorFactory.getLogCollector().log(this.name, "useTime", jb.toString());
			*/
		} 
		if (useTime > 0) { // 有效时方计数
			this.visitCount.addAndGet(1);
			if (hit) {
				this.hitCount.addAndGet(1);
			}
		}
		this.totalMiles.addAndGet(useTime);
		// 每隔五分钟输出一次快照，这里的五分钟不是绝对的，应该是>=五分钟
		long curIndex = (last + this.randDelay.get()) / MILES_FIVE_MINUTES;
		if (curIndex >= this.minute5Index.get() && this.visitCount.get() > 100) {
			if (this.minute5Index.get() == 0) {
				this.minute5Index.compareAndSet(0, curIndex + 1);
			} else {
				this.minute5Index.compareAndSet(curIndex, curIndex + 1);
			}
			// 打印统计信息
			long vc = this.visitCount.get();
			if (vc > 0) {
				try {
					ApiLogger.info("[visitStas-" + this.name + "]\ttotal:" + visitCount + "\thit:" + hitCount
							+ "\ttotalTime:" + totalMiles + "\tavgTime: " + (totalMiles.get() / vc) + "\thitRate:"
							+ (this.hitCount.get() * 100 / vc));					
					
					JsonBuilder jb = new JsonBuilder();
					jb.append("total", vc);
					jb.append("hit", (this.hitCount.get() * 100 / vc));
					jb.append("avg", (totalMiles.get() / vc));
					jb.flip();
					
					/** TODO yangwm need fix 
					LogCollectorFactory.getLogCollector().log(this.name, "visitStas", jb.toString());
					*/
				} catch (Exception e) {
					ApiLogger.error("[UseTimeStasticsMonitor] Stastics error!", e);
				}
			}
			// 还原五分钟计数
			this.totalMiles.set(0);
			this.visitCount.set(0);
			this.hitCount.set(0);
		}
	}
	
	/**
	 * 获取统计示例方法
	 * @param keyObject
	 * @param info
	 * @return
	 */
	public static UseTimeStasticsMonitor getInstance(String info) {
		String key = "key_" + info;
		UseTimeStasticsMonitor monitor = STATIC_MONITORS.get(key);
		if (monitor != null) {
			return monitor;
		}
		ApiLogger.info("[UseTimeStasticsMonitor] key is:" + key);
		monitor = new UseTimeStasticsMonitor(info);
		STATIC_MONITORS.put(key, monitor);
		return monitor;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String cacheName) {
		this.name = cacheName;
	}

	public long getEndMiles() {
		return totalMiles.get();
	}

	public void setEndMiles(long endMiles) {
		this.totalMiles = new AtomicLong(endMiles);
	}

	public long getMinute1HitCount() {
		return hitCount.get();
	}

	public void setMinute1HitCount(long minute1HitCount) {
		this.hitCount = new AtomicLong(minute1HitCount);
	}

	public long getMinute1TotalCount() {
		return visitCount.get();
	}

	public void setMinute1TotalCount(long minute1TotalCount) {
		this.visitCount = new AtomicLong(minute1TotalCount);
	}

}
