package com.example.threaddesign;

/**
 * USAGE SCENARIO:
 * Fixing a thread that waits for the lock too long to die.
 * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class UserDefinedLock
{
	private boolean isLocked;
	
	private Collection<Thread> blockedThreads = new ArrayList<>();
	
	private Thread current;
	
	public UserDefinedLock()
	{
		isLocked = false;
	}
	
	public synchronized void lock() throws InterruptedException
	{
		while (isLocked) {
			blockedThreads.add(Thread.currentThread());
			wait();
		}
		current = Thread.currentThread();
		isLocked = true;
		blockedThreads.remove(Thread.currentThread());
	}
	
	public synchronized void lock(long millis) throws InterruptedException, TimeoutException
	{
		if (millis <= 0) {
			lock();
		}
		long expire = System.currentTimeMillis() + millis;
		while (isLocked) {
			if (System.currentTimeMillis() <= expire) {
				blockedThreads.add(Thread.currentThread());
				wait(millis);
			} else {
				throw new TimeoutException(Thread.currentThread() + " time out");
			}
		}
		current = Thread.currentThread();
		isLocked = true;
		blockedThreads.remove(Thread.currentThread());
	}
	
	public synchronized void unlock()
	{
		if (Thread.currentThread().equals(current)) {
			isLocked = false;
			notifyAll();
			System.out.println(Thread.currentThread() + " released lock.");
		}
	}
	
	public Collection<Thread> getBlockedThread()
	{
		return Collections.unmodifiableCollection(blockedThreads);
	}
	
	public int getBlockedSize()
	{
		return blockedThreads.size();
	}
	
	static class TimeoutException extends Exception
	{
		private static final long serialVersionUID = -6203469573608998046L;

		public TimeoutException(String msg)
		{
			super(msg);
		}
	}
	
	
	public static void main(String[] args)
	{
		final UserDefinedLock lock = new UserDefinedLock();
		Stream.of("T1", "T2", "T3", "T4").forEach(s -> {
				new Thread(() -> {
					long millis = new Double(10_000 * Math.random()).longValue();
					// case1
//					try {
//						lock.lock();
//						System.out.println(s + " get lock.");
//						Thread.sleep(millis);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} finally {
//						lock.unlock();
//					}
					// case2
//					try {
//						lock.lock(1L);
//						System.out.println(s + " get lock.");
//						Thread.sleep(millis);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} catch (TimeoutException e) {
//						System.out.println(e.getMessage());
//					} finally {
//						lock.unlock();
//					}
				}, s).start();
			});
	}
}
