package br.com.augusto.votin_on;

import br.com.augusto.votin_on.config.KafkaConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@Log4j2
public class VotingOnApplication {
	public static void main(String[] args) {
		SpringApplication.run(VotingOnApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@KafkaListener(topics = KafkaConfig.GUIDE_RESULT, groupId = "group_id")
	public void listen(ConsumerRecord<String, String> consumerRecord) {
		log.info("Received message: " + consumerRecord.value());
	}
}

