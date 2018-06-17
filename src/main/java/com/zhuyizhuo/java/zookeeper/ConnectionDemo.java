package com.zhuyizhuo.java.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yizhuo on 2018/6/13.
 */
public class ConnectionDemo {

    public static void main(String[] args) {
        try {
            System.out.println("start...");
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zk = new ZooKeeper("192.168.229.128:2181,192.168.229.129:2181,192.168.229.130:2181", 4000,
                    new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            System.out.println("watchedEvent...");
                            if (Event.KeeperState.SyncConnected == watchedEvent.getState()){
                                countDownLatch.countDown();
                            }
                        }
                    });
            System.out.println("await...");
            countDownLatch.await();
            System.out.println(zk.getState());
            zk.create("/zhuyizhuo/test", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Stat stat = new Stat();
            byte[] data = zk.getData("/zhuyizhuo/test", null, stat);
            System.out.println(new String(data));
            System.out.println(stat.getVersion());

            stat = zk.setData("/zhuyizhuo/test", "5".getBytes(), stat.getVersion());

            System.out.println(stat.getVersion());

            byte[] data1 = zk.getData("/zhuyizhuo/test", null, stat);
            System.out.println(new String(data1));

            zk.delete("/zhuyizhuo/test",stat.getVersion());

            zk.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
