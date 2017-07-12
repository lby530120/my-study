package com.skylight.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 集群管理（Group Membership）
 * Zookeeper 能够很容易的实现集群管理的功能，如有多台 Server 组成一个服务集群，
 * 那么必须要一个“总管”知道当前集群中每台机器的服务状态，一旦有机器不能提供服务，
 * 集群中其它集群必须知道，从而做出调整重新分配服务策略。同样当增加集群的服务能力时，就会增加一台或多台 Server，同样也必须让“总管”知道
 * @author yangbin
 *
 */
public class AppClient {
	private String groupNode = "sgroup";
	private ZooKeeper zk;
	private Stat stat = new Stat();
	private volatile List<String> serverList;

	/**
	 * 连接zookeeper
	 */
	public void connectZookeeper() throws Exception {
		zk = new ZooKeeper("192.168.139.214:2181,192.168.139.215:2181,192.168.139.30:2181",
				30 * 1000, new Watcher() {
					public void process(WatchedEvent event) {
						// 如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听
						if (event.getType() == EventType.NodeChildrenChanged
								&& ("/" + groupNode).equals(event.getPath())) {
							try {
								updateServerList();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
		updateServerList();
	}

	
	/**
	 * 更新server列表
	 */
	private void updateServerList() throws Exception {
		List<String> newServerList = new ArrayList<String>();

		// 获取并监听groupNode的子节点变化
		// watch参数为true, 表示监听子节点变化事件.
		// 每次都需要重新注册监听, 因为一次注册, 只能监听一次事件, 如果还想继续保持监听, 必须重新注册
		List<String> subList = zk.getChildren("/" + groupNode, true);
		for (String subNode : subList) {
			// 获取每个子节点下关联的server地址
			byte[] data = zk.getData("/" + groupNode + "/" + subNode, false,
					stat);
			newServerList.add(new String(data, "utf-8"));
		}

		// 替换server列表
		serverList = newServerList;
		System.out.println("current active server address: " + serverList);
	}

	
	
	public static void main(String[] args) throws Exception {
		// 初始化log4j
		/*File f = new File("user.dir" + File.separator + "log");
		if (!f.exists()) {
			f.mkdir();
			new File("user.dir" + File.separator + "log" + File.separator
					+ "land.log");
		}
		PropertyConfigurator.configureAndWatch(File.separator
				+ System.getProperty("user.dir") + File.separator + "conf"
				+ File.separator + "log4j.properties");*/

		AppClient ac = new AppClient();
		ac.connectZookeeper();
		Thread.sleep(Long.MAX_VALUE);
	}
}