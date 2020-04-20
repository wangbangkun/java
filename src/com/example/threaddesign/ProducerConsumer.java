package com.example.threaddesign;

import java.util.stream.Stream;

public class ProducerConsumer
{
	private int product = 0;
	
	private volatile boolean hasProduced = false;
	
	private static final Object LOCK = new Object();
	
	public void produce()
	{
		while (true) {
			synchronized (LOCK) {
				while (hasProduced) {
					try {
						LOCK.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				product++;
				hasProduced = true;
				LOCK.notifyAll();
				System.out.println(Thread.currentThread().getName() + " produced " + product);
			}
			
			try {
				Thread.sleep(5_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void consume()
	{
		while (true) {
			synchronized (LOCK) {
				while (!hasProduced) {
					try {
						LOCK.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				hasProduced = false;
				LOCK.notifyAll();
				System.out.println(Thread.currentThread().getName() + " consumed " + product);
			}
			
			try {
				Thread.sleep(5_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		ProducerConsumer pc = new ProducerConsumer();
		
		Stream.of("A", "B", "C").forEach(s -> {
			new Thread(s) {
				@Override
				public void run()
				{
					pc.produce();
				}
			}.start();
		});
		
		Stream.of("a", "b").forEach(s -> {
			new Thread(s) {
				@Override
				public void run()
				{
					pc.consume();
				}
			}.start();
		});
	}
}
