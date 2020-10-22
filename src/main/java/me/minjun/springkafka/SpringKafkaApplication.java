package me.minjun.springkafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.beans.BeanProperty;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringKafkaApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(SpringKafkaApplication.class);
        app.addListeners((ApplicationStartedEvent event) -> {
            System.out.println("===== Kafka Started =====");
        });
        ConfigurableApplicationContext context = app.run(args);

        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);

        producer.sendMessage("hello world");
        listener.latch.await(10, TimeUnit.SECONDS);

        context.close();
    }

    @Bean
    public MessageProducer messageProducer(){
        return new MessageProducer();
    }
    public static class MessageProducer{
        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;

        @Value("${message.topic.name}")
        private String topicName;

        public void sendMessage(String message){
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("Unable to send message = [" + message + "] due to : " + throwable.getMessage());
                }

                @Override
                public void onSuccess(SendResult<String, String> stringStringSendResult) {
                    System.out.println("Sent message = [" + message + "] with offset = [" + stringStringSendResult.getRecordMetadata().offset() + "]");
                }
            });
        }
    }

    @Bean
    public MessageListener messageListener(){
        return new MessageListener();
    }
    public static class MessageListener{
        private CountDownLatch latch = new CountDownLatch(3);

        @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
        public void listenGroupFoo(String message){
            System.out.println("Received message in group 'foo' : " + message);
            latch.countDown();
        }
        @KafkaListener(topics = "${message.topic.name}", groupId = "bar", containerFactory = "barKafkaListenerContainerFactory")
        public void listenGroupBar(String message){
            System.out.println("Received message in group 'bar' : " + message);
            latch.countDown();
        }
    }



}
