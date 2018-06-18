package com.zhuyizhuo.java.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by yizhuo on 2018/6/18.
 */
public class DistributedLock implements Lock,Watcher {

    private ZooKeeper zk;
    private String ROOT_PATH = "/locks";
    private String CURRENT_LOCK;
    private String WAIT_LOCK;

    private CountDownLatch countDownLatch ;

    public DistributedLock() {
        try {
            zk = new ZooKeeper("192.168.229.128:2181",5000,this);
            Stat exists = zk.exists(ROOT_PATH, false);
            if (exists == null){
                zk.create(ROOT_PATH,"0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean tryLock() {
        try {
            CURRENT_LOCK = zk.create(ROOT_PATH + "/", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "->" + CURRENT_LOCK + "尝试竞争锁");
            List<String> children = zk.getChildren(ROOT_PATH, false);
            SortedSet<String> sortedSet = new TreeSet<>();
            for (int i = 0; i < children.size(); i++) {
                sortedSet.add(ROOT_PATH + "/" +children.get(i));
            }
            String firstNode = sortedSet.first();
//            System.out.println(Thread.currentThread().getName() + "->firstNode  " + firstNode );
//            System.out.println(Thread.currentThread().getName() + "->CURRENT_LOCK " + CURRENT_LOCK);
            if (CURRENT_LOCK.equals(firstNode)){
                return true;
            }
            SortedSet<String> lessThanMe = sortedSet.headSet(CURRENT_LOCK);
            if (!lessThanMe.isEmpty()){
                WAIT_LOCK = lessThanMe.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void lock() {
        if (this.tryLock()){
            System.out.println(Thread.currentThread().getName() + "->" + CURRENT_LOCK+ "-> 成功获得锁");
            return;
        }
        waitForLock(WAIT_LOCK);
    }

    private void waitForLock(String prev) {
        try {
            Stat exists = zk.exists(prev, true);
            if (exists != null){
                System.out.println(Thread.currentThread().getName() + "->等待锁" + ROOT_PATH + "/"+ prev + "释放" );
                countDownLatch = new CountDownLatch(1);
                countDownLatch.await();
                System.out.println(Thread.currentThread().getName() + " -> 获得锁成功");
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "->释放锁"  + CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if (this.countDownLatch != null){
            countDownLatch.countDown();
        }
    }
}
