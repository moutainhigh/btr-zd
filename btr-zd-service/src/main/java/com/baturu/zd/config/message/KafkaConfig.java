package com.baturu.zd.config.message;

import com.baturu.message.configuration.KafkaConsumerConfig;
import com.baturu.message.configuration.KafkaProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

	@Autowired
	private KafkaConsumerConfigProperties kafkaConsumerConfigProperties;
	@Autowired
	private KafkaProducerConfigProperties kafkaProducerConfigProperties;

	@Bean
	public KafkaConsumerConfig kafkaConsumerConfig() {
		return kafkaConsumerConfigProperties.targetConsumerConfig();
	}

	@Bean
	public KafkaProducerConfig kafkaProducerConfig() {
		return kafkaProducerConfigProperties.targetProducerConfig();
	}
}
