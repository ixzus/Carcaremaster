package com.lazycare.carcaremaster.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Package com.mngwyhouhzmb.common.thread
 * @Title TaskExecutor
 * @Description 线程池
 * @author LiuSiQing
 * @Time 2014年11月26日上午10:14:13
 */
public class TaskExecutor {

	private final static ExecutorService executorService = Executors
			.newFixedThreadPool(5);

	/**
	 * @Title 2014年11月26日
	 * @Description 私有构造函数
	 * @author LiuSiQing
	 * @Time 2014年11月26日上午10:14:26
	 */
	private TaskExecutor() {
	}

	/**
	 * @Title Execute
	 * @Description 执行现场
	 * @param task
	 */
	public static void Execute(Runnable task) {
		// if( null == executorService || executorService.isShutdown() ||
		// executorService.isTerminated() )
		// executorService = Executors.newFixedThreadPool(5);
		executorService.submit(task);
	}

	/**
	 * @Title IsTerminated
	 * @Description 线程是否执行完成
	 * @return
	 */
	public static boolean IsTerminated() {
		executorService.shutdown();
		while (true)
			if (executorService.isTerminated())
				break;
		return true;
	}

	/**
	 * @Title shutdownNow
	 * @Description 立即关闭线程池
	 * @return
	 */
	@Deprecated
	public static List<Runnable> shutdownNow() {
		List<Runnable> list = executorService.shutdownNow();
		// executorService = null;
		return list;
	}
}
