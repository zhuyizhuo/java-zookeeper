package com.zhuyizhuo.java.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yizhuo on 2018/6/14.
 */
public class WatherDemo {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("192.168.229.128:2181,192.168.229.129:2181,192.168.229.130:2181", 4000, (watchedEvent) -> {
                System.out.println("111111" + watchedEvent.getState());
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()){
                    countDownLatch.countDown();
                }
        });
        countDownLatch.await();

//        zooKeeper.create("/zhuyizhuo/tes", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        Stat stat = zooKeeper.exists("/zhuyizhuo/zhuo", (watchedEvent) -> {
                System.out.println(watchedEvent.getType() + "->" + watchedEvent.getPath());
            }
        );

        zooKeeper.setData("/zhuyizhuo/zhuo","2".getBytes(),stat.getVersion());
    }
}
