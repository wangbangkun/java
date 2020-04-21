package com.example.threaddesign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * USAGE SCENARIO:
 * Run maximum amount of thread(s) at the time.
 * 
 * SOLUTION:
 * Use LinkedList as lock and counter.
 */

public class ThreadAmount
{
	private static final int MAXIMUM = 5;
	
	private final static LinkedList<Object> LOCK = new LinkedList<>();
	
	public static Thread createNew(String name, Runnable task)
	{
		return new Thread(name) {
			@Override
			public void run()
			{
				System.out.println(name + " trying to start.");
				synchronized (LOCK) {
					while (LOCK.size() >= MAXIMUM) {
						try {
							System.out.println(name + " wait.");
							LOCK.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					LOCK.addLast(new Object());
				}
				
				System.out.println(name + " start");
				
				task.run();
				
				System.out.println(name + " end");
				
				synchronized (LOCK) {
					LOCK.removeFirst();
					LOCK.notifyAll();
				}
			}
		};
	}
		
	public static void main(String[] args)
	{
		ArrayList<Thread> list = new ArrayList<>();
		Arrays.asList("TASK A", "TASK B", "TASK C", "TASK D", "TASK E", "TASK F", "TASK G", "TASK H", "TASK I", "TASK J")
				.stream()
				.forEach(name -> {
					int millis = new Double(10000f * Math.random()).intValue();
					Thread t = createNew(name, () -> {
						try {
							Thread.sleep(millis);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					});
					list.add(t);
				}
		);
		list.stream().forEach(t -> {
			t.start();
		});
	}
}