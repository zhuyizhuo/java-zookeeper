package com.zhuyizhuo.java.zookeeper.lock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yizhuo on 2018/6/18.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                try {
                    countDownLatch.await();
                    DistributedLock distributedlock = new DistributedLock();
                    distributedlock.lock();
                    Thread.sleep(5000);
                    distributedlock.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }
}
