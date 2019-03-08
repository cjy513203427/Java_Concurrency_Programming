package com.packtpub.java7.concurrency.chapter1.recipe2.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;

import com.packtpub.java7.concurrency.chapter1.recipe2.task.Calculator;

/**
 *  Main class of the example
 *  前面的线程存在阻塞导致即将执行的线程阻塞，优先级低导致阻塞
 */
public class Main {

	/**
	 * Main method of the example
	 * @param args
	 */
	public static void main(String[] args) {

		// Thread priority infomation 
		System.out.printf("Minimum Priority: %s\n",Thread.MIN_PRIORITY);
		System.out.printf("Normal Priority: %s\n",Thread.NORM_PRIORITY);
		System.out.printf("Maximun Priority: %s\n",Thread.MAX_PRIORITY);
		
		Thread threads[];
		Thread.State status[];
		
		// Launch 10 threads to do the operation, 5 with the max
		// priority, 5 with the min
		threads=new Thread[10];
		status=new Thread.State[10];
		for (int i=0; i<10; i++){
			threads[i]=new Thread(new Calculator(i));
			if ((i%2)==0){
				threads[i].setPriority(Thread.MAX_PRIORITY);
			} else {
				threads[i].setPriority(Thread.MIN_PRIORITY);
			}
			threads[i].setName("Thread "+i);
		}
		
		
		// Wait for the finalization of the threads. Meanwhile, 
		// write the status of those threads in a file
		//从 Java 7 build 105 版本开始，Java 7 的编译器和运行环境支持新的 try-with-resources 语句，
		// 称为 ARM 块(Automatic Resource Management) ，自动资源管理
        String root = System.getProperty("user.dir");
        String FileName="log.txt";
        String filePath = root+File.separator+"Chapter 1\\ch1_recipe02\\src\\com\\packtpub\\java7\\concurrency\\chapter1\\recipe2\\core"+ File.separator+FileName;
		try (FileWriter file = new FileWriter(filePath);PrintWriter pw = new PrintWriter(file)){

			for (int i=0; i<10; i++){
				pw.println("Main : Status of Thread "+i+" : "+threads[i].getState());
				status[i]=threads[i].getState();
			}

			for (int i=0; i<10; i++){
				threads[i].start();
			}
			
			boolean finish=false;
			while (!finish) {
				for (int i=0; i<10; i++){
					if (threads[i].getState()!=status[i]) {
						writeThreadInfo(pw, threads[i],status[i]);
						status[i]=threads[i].getState();
					}
				}
				
				finish=true;
				for (int i=0; i<10; i++){
					finish=finish &&(threads[i].getState()==State.TERMINATED);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  This method writes the state of a thread in a file
	 * @param pw : PrintWriter to write the data
	 * @param thread : Thread whose information will be written
	 * @param state : Old state of the thread
	 */
	private static void writeThreadInfo(PrintWriter pw, Thread thread, State state) {
		pw.printf("Main : Id %d - %s\n",thread.getId(),thread.getName());
		pw.printf("Main : Priority: %d\n",thread.getPriority());
		pw.printf("Main : Old State: %s\n",state);
		pw.printf("Main : New State: %s\n",thread.getState());
		pw.printf("Main : ************************************\n");
	}

}
