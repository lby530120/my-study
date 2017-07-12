package com.skylight.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

public class CliApiDemo implements Watcher {
	public final Integer CONNECTION_TIMEOUT = 1000;
	public ZooKeeper zk = null;
	private CountDownLatch connectedSemaphore = new CountDownLatch(1);

	/**
	 * 继承了watcher，需要实现process接口，这个接口类似一个回调函数 收到来自Server的Watcher通知后的处理。
	 */
	public void process(WatchedEvent event) {
		System.out.println("process方法收到事件通知：" + event.toString()+"\n");
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
		if( event.getType().equals(EventType.NodeChildrenChanged) ){  
			System.out.println("----------config is changed, please update--------------");  
		}
	}

	/**
	 * zookeeper连接方法，成功返回true，否则返回false
	 * 
	 * @return boolean
	 */
	public boolean ZK_con() {
		String con_string = "192.168.139.214:2181,192.168.139.215:2181,192.168.139.30:2181";
		try {
			zk = new ZooKeeper(con_string, CONNECTION_TIMEOUT, this);
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			System.out.println("连接创建失败，发生 InterruptedException");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("连接创建失败，发生 IOException");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * zookeeper 创建目录或者子目录，成功返回true，否则返回false
	 * 
	 * @param strPath
	 * @param strDir
	 * @return boolean
	 */
	public boolean ZK_create(String strPath, String strDir) {
		try {
			zk.create(strPath, strDir.getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;

		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 按路径得到某节点
	 * 
	 * @param strPath
	 * @return String
	 */
	public String ZK_getNode(String strPath) {
		String result = "";
		try {
			result = new String(zk.getData(strPath, false, null));
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 得到某路径下的子节点
	 * 
	 * @param strPath
	 * @return List of String
	 */
	public List<String> ZK_getChild(String strPath) {
		List<String> resultList = null;
		try {
			resultList = zk.getChildren(strPath, true);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	/**
	 * 设置节点数据
	 * 
	 * @param strPath
	 * @param strNewData
	 * @return boolean
	 */
	public boolean ZK_setNodeData(String strPath, String strNewData) {

		try {
			zk.setData(strPath, strNewData.getBytes(), -1);
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 判断节点是否存在
	 * 
	 * @param strPath
	 * @return boolean
	 */
	public boolean ZK_exists(String strPath) {

		try {
			zk.exists(strPath, true);
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除节点
	 * 
	 * @param strPath
	 * @return boolean
	 */
	public boolean ZK_delete(String strPath) {

		try {
			zk.delete(strPath, -1);
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 给某个目录节点重新设置访问权限
	 * @param path
	 * @return
	 * @throws KeeperException
	 */
	public boolean ZK_setACL(String path) throws KeeperException{
		try {
			ACL acl = new ACL(0, new Id("world", "anyone"));
			zk.setACL(path, Arrays.asList(new ACL[]{acl}), 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<ACL> ZK_getACL(String path, Stat stat){
		List<ACL> list = null;
		try {
			list =  zk.getACL(path, stat);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 关闭连接
	 * 
	 * @return boolean
	 */
	public boolean ZK_close() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Zookeeper查询命令:
	 * Zookeeper查询命令主要用来查询服务器端的数据，不会更改服务器端的数据。所有的查询命令都可以即刻从client连接的server立即返回，不需要leader进行协调
	 * 
	 * 1. exists:判断指定path的node是否存在，如果存在则返回true，否则返回false.  ==ZK_exists
     * 2. getData:从指定path获取该node的数据                                                      ==ZK_getNode
     * 3. getACL:获取指定path的ACL。                                                                   ==ZK_getACL
     * 4. getChildren:获取指定path的node的所有孩子结点。                                    ==ZK_getChild
     * 
     * Zookeeper修改命令
     * Zookeeper修改命令主要是用来修改节点数据或结构，或者权限信息。任何修改命令都需要提交到leader进行协调，协调完成后才返回。修改命令主要包括：
     * 
     * 1.   createSession：请求server创建一个session  ==ZK_con
	 * 2.   create：创建一个节点                                 ==ZK_create
	 * 3.   delete：删除一个节点                                 ==ZK_delete
	 * 4.   setData：修改一个节点的数据                     ==ZK_setNodeData
	 * 5.   setACL：修改一个节点的ACL                       ==ZK_setACL
 	 * 6.   closeSession：请求server关闭session          ==ZK_close
	 * @param args
	 */
	public static void main(String[] args) {

		// 创建一个CliApiDemo对象
		CliApiDemo myCliApiDemo = new CliApiDemo();

		// 创建一个与服务器的连接
		if (myCliApiDemo.ZK_con()) {
			System.out.println("ZK  connect succesfully!");
		} else {
			System.out.println("ZK  connect failed!");
			System.exit(0);
		}

		// 创建一个目录节点
		if (myCliApiDemo.ZK_create("/MyPath", "MyPath_Name")) {
			System.out.println("得到创建的节点/MyPath为："+myCliApiDemo.ZK_getNode("/MyPath"));
		} else {
			System.out.println("Create Node  failed!!!");
		}

		// 创建一个子目录节点
		if (myCliApiDemo.ZK_create("/MyPath/MyChildPath1", "child-1")) {
			System.out.println("得到创建的节点/MyPath/MyChildPath1为："+myCliApiDemo.ZK_getChild("/MyPath"));
			System.out.println("得到创建的节点/MyPath/MyChildPath1为："+myCliApiDemo.ZK_getNode("/MyPath/MyChildPath1"));
		} else {
			System.out.println("Create children Node  failed!!!");
		}

		// 修改子目录节点数据
		if (myCliApiDemo.ZK_setNodeData("/MyPath/MyChildPath1", "child-1-new")) {
			System.out.println("得到更新的节点/MyPath/MyChildPath1为："+myCliApiDemo.ZK_getNode("/MyPath/MyChildPath1"));
			System.out.println("目录节点状态：[" + myCliApiDemo.ZK_exists("/MyPath")	+ "]");
		} else {
			System.out.println("Set New Data  failed!!!");
		}

		// 创建另外一个子目录节点
		if (myCliApiDemo.ZK_create("/MyPath/MyChildPath2", "child-2")) {
			System.out.println(myCliApiDemo.ZK_getChild("/MyPath"));
		} else {
			System.out.println("Create children Node  failed!!!");
		}

		// 删除子目录节点
		myCliApiDemo.ZK_delete("/MyPath/MyChildPath2");
		myCliApiDemo.ZK_delete("/MyPath/MyChildPath1");
		// 删除父目录节点
		myCliApiDemo.ZK_delete("/MyPath");
		
		//用于验证创建好后其它应用从zookeeper同步信息
		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 关闭连接
		if(myCliApiDemo.ZK_close()){
			System.out.println("ZK  closed success !!!");
		}else{
			System.out.println("Create closed  failed!!!");
		}

	}

}
