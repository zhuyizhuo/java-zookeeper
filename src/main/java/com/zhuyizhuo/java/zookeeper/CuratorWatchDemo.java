package com.zhuyizhuo.java.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

/**
 * Created by yizhuo on 2018/6/15.
 */
public class CuratorWatchDemo {

    public static void main(String[] args) throws Exception {

        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("192.168.229.128:2181,192.168.229.129:2181,192.168.229.130:2181")
                .sessionTimeoutMs(4000).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("curator").build();

        curator.start();

//        addListenerWithTreeCache(curator, "/zhuo");

//        addListenerWithNodeCache(curator,"/zhuo");

        addlistenerWithPathChildCache(curator,"/zhuo");

        System.in.read();
    }

    //监听子节点的增删改
    private static void addlistenerWithPathChildCache(CuratorFramework curator, String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curator,path,true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
                System.out.println("Recived event : " + pathChildrenCacheEvent.getType() +"->"+ pathChildrenCacheEvent.getData().getPath());
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
    }

    //只有新增 修改收到通知
    private static void addListenerWithNodeCache(CuratorFramework curator, String path) throws Exception {
        final NodeCache nodeCache = new NodeCache(curator,path);
        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println("Recived event :" + nodeCache.getCurrentData().getPath());
            }
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }

    //增删改  子节点增删改 都会收到通知
    private static void addListenerWithTreeCache(CuratorFramework curator, String path) throws Exception {
        TreeCache treeCache = new TreeCache(curator,path);
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println(treeCacheEvent.getType()+"->" + treeCacheEvent.getData().getPath());
            }
        };
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }
}
