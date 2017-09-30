package com.mybatis.customcallback;

import org.mybatis.generator.api.ProgressCallback;

public class MyProgressCallBack implements ProgressCallback {

	@Override
	public void introspectionStarted(int totalTasks) {
		// TODO Auto-generated method stub
		System.out.println("introspectionStarted , and totalTasks is : " + totalTasks);

	}

	@Override
	public void generationStarted(int totalTasks) {
		// TODO Auto-generated method stub
		System.out.println("generationStarted , and totalTasks is : " + totalTasks);

	}

	@Override
	public void saveStarted(int totalTasks) {
		// TODO Auto-generated method stub
		System.out.println("saveStarted , and totalTasks is : " + totalTasks);

	}

	@Override
	public void startTask(String taskName) {
		System.out.println("startTask : " + taskName);

	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
		System.out.println("done ... ");

	}

	@Override
	public void checkCancel() throws InterruptedException {
		// TODO Auto-generated method stub

		System.out.println("checkCancel ...");
	}

}
