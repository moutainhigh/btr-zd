package com.baturu.zd.config.message;

import com.baturu.message.configuration.KafkaProducerConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("kafka.producer")
public class KafkaProducerConfigProperties {

	/**
	 * 服务的Ip和端口
	 */
	private String serverIpAndPortList;

	/**
	 * producer.type
	 * 消息发送的模式，同步或异步，异步的时候消息将会在本地buffer，并适时批量发送
	 * default sync
	 */
	private String producerType;

	/**
	 * request.required.acks
	 * producer接收消息ack的时机，默认为0
	 * 0：produce人不会等待broker发送ack
	 * 1：当leader接收到消息后发送ack
	 * 2：当所有的follower都同步消息成功后发送ack
	 */
	private String requestRequiredAcks;

	/**
	 * serializer.class
	 * 消息序列化类
	 * 向kafka发送数据，默认支持String和byte[]2种类型,分别支持String和二进制
	 * 包括kafka.serializer.StringEncoder和kafka.serializer.DefaultEncoder 2个类
	 * default kafka.serializer.DefaultEncoder
	 */
	private String serializerClass;


	KafkaProducerConfig targetProducerConfig() {
		return KafkaProducerConfig.builder()
				.kafkaBrokerIpAndPortList(serverIpAndPortList)
				.producerType(producerType)
				.requestRequiredAcks(requestRequiredAcks)
				.serializerClass(serializerClass)
				.build();
	}

}
