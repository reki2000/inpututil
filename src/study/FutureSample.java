package study;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureSample {
	private static void puts(String msg) { System.out.println(msg); }

	public void test() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		// 重い処理を登録
		Callable<String> heavy = new Callable<String>() {
			@Override public String call() throws InterruptedException {
				Thread.sleep(2000);
				return "Je suis fatigue!";
			}
		};
		
		// コールバックを登録
		FutureTask<String> myTask = new FutureTask<String>(heavy) {
			@Override protected void done() {
				try {
					puts("got result.");
					puts("result: " + get()); // ここではブロックしない
				} catch (InterruptedException e) {
					puts("Interrupted..");
				} catch (ExecutionException e) {
					puts("Error during execution.." + e.getMessage());
				}
			}
		};
		
		// 実行!!
		executor.execute(myTask);
		
		executor.shutdown();
	}
	
	public static void main(String[] args) {
		FutureSample sample = new FutureSample();
		puts("running..");
		sample.test();
		puts("comleted..");
	}
}
