package com.example.threaddesign;

/**
 * USAGE SCENARIO:
 * A thread need to take long time to get work done and it's impossible to interrupt it.
 * 
 * SOLUTION:
 * "The Java Virtual Machine exits when the only threads running are all daemon threads."
 * So let the working thread be a daemon thread and start it by another thread. 
 * Interrupt the another thread and the left working thread(which is a DAEMON thread) will be end because of JVM exiting.
 */

public class LongRunningTask
{
	private Thread starter;
	private boolean isFinished = false;
	
	public void start(Runnable runnable)
	{
		starter = new Thread() {
			@Override
			public void run()
			{
				Thread executor = new Thread(runnable);
				executor.setDaemon(true);
				try {
					executor.start();
					executor.join();
					isFinished = true;
					System.out.println("Task finished");
				} catch (InterruptedException e) {
					isFinished = true;
					System.out.println("Task expired");
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		};
		starter.start();
	}
	
	/**
	 * No matter whether work done or not, terminate the thread after specified millisecond. 
	 * @param millis
	 */
	public void terminate(long millis)
	{
		long expire = System.currentTimeMillis() + millis;
		while (!isFinished) {
			if (System.currentTimeMillis() >= expire) {
				starter.interrupt();
				isFinished = true;
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isFinished = false;
	}
	
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		LongRunningTask task = new LongRunningTask();
		
		Runnable r = new Runnable() {
			@Override
			public void run()
			{
				// case 1
//				try {
//					Thread.sleep(5_000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				// case 2
//				while (true) {
//					
//				}
			}
		};
		task.start(r);
		task.terminate(10_000);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
}
