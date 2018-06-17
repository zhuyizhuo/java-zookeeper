package com.zhuyizhuo.java.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Created by yizhuo on 2018/6/15.
 */
public class CuratorDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("192.168.229.128:2181,192.168.229.129:2181,192.168.229.130:2181")
                .sessionTimeoutMs(4000).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("curator").build();

        curator.start();

//        curator.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/zhuo/hello", "0".getBytes());

//        curator.delete().deletingChildrenIfNeeded().forPath("/zhuo");

        Stat stat = new Stat() ;
        curator.getData().storingStatIn(stat).forPath("/zhuo/hello");

        curator.setData().withVersion(stat.getVersion()).forPath("/zhuo/hello", "hello".getBytes());

        curator.close();
    }
}
