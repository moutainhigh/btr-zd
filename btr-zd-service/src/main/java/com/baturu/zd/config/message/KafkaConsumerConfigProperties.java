package com.baturu.zd.config.message;

import com.baturu.message.configuration.KafkaConsumerConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("kafka.consumer")
public class KafkaConsumerConfigProperties {

	/**
	 * 分组消费
	 */
	private String groupId;

	/**
	 * 服务的Ip和端口
	 */
	private String serverIpAndPortList;

	/**
	 * Zookeeper的ip和端口
	 */
	private String zookeeperIpAndPortList;

	/**
	 * rebalance.max.retries  default 4
	 */
	private Integer rebalanceMaxRetries;

	/**
	 * zookeeper.session.timeout.ms
	 */
	private String zookeeperSessionTimeout;

	/**
	 * zookeeper.session.timeout.ms
	 */
	private String zookeeperConnectionTimeout;

	/**
	 * zookeeper.sync.time.ms
	 */
	private String zookeeperSyncTime;

	/**
	 * auto.commit.enable
	 */
	private String autoCommitEnable;

	/**
	 * auto.commit.interval.ms
	 * consumer向ZooKeeper发送offset的时间间隔
	 * default 60000
	 */
	private String autoCommitInterval;

	KafkaConsumerConfig targetConsumerConfig() {

		return KafkaConsumerConfig.builder()
				.groupId(groupId)
				.zookeeperServerIpAndPortList(zookeeperIpAndPortList)
				.zookeeperSessionTimeout(zookeeperSessionTimeout)
				.zookeeperConnectionTimeout(zookeeperConnectionTimeout)
				.zookeeperSyncTime(zookeeperSyncTime)
				.autoCommitEnable(autoCommitEnable)
				.autoCommitInterval(autoCommitInterval)
				.build();
	}

}
