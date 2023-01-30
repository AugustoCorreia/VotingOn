package br.com.augusto.votin_on;

import br.com.augusto.votin_on.config.KafkaConfig;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Log4j2
@SpringBootTest
class VotinOnApplicationTests {

	@Test
	 void testRestTemplateBean_ShouldCreateRestTemplate() {
		ApplicationContext context = SpringApplication.run(VotingOnApplication.class);
		RestTemplate restTemplate = context.getBean(RestTemplate.class);
		assertNotNull(restTemplate);
	}

	@Test
	@SneakyThrows
	 void testKafkaListener_ShouldReceiveMessage() {

		AtomicReference<String> recordValue = new AtomicReference<>();

		Map<String, Object> configs = new HashMap<>();
		configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configs.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
		configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configs);
		Consumer<String, String> consumer = consumerFactory.createConsumer();

		KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory,
				new ContainerProperties(KafkaConfig.GUIDE_RESULT));

		container.setupMessageListener((MessageListener<String, String>) record -> {
			log.info("Received message: " + record.value());
			assertEquals("value", record.value());

		});
		container.start();

		Map<String, Object> producerConfigs = new HashMap<>();
		producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs);
		KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
		template.send(KafkaConfig.GUIDE_RESULT, "key", "value");

		container.stop();
	}
}
