package com.zhuyizhuo.java.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yizhuo on 2018/6/18.
 */
public class CuratorDemo {
    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CuratorDemo curatorDemo = new CuratorDemo();
                curatorDemo.lock();
            }).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }

    public void lock() {
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("192.168.229.128:2181")
                .sessionTimeoutMs(4000).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("curator").build();
        curator.start();

        InterProcessMutex interProcessMutex = new InterProcessMutex(curator,"/locks");
        try {
            interProcessMutex.acquire();
            System.out.println(Thread.currentThread().getName() + " ->获得锁");
            Thread.sleep(3000);
            interProcessMutex.release();
            System.out.println(Thread.currentThread().getName() + " ->释放锁");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
