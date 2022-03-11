
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadedWorker {

  private final static String HOST = "54.213.105.81";
  private final static String VH = "/";
  private final static int PORT = 5672;
  private final static String QUEUE_NAME = "SKIER_TASK";
  private final static String USERNAME = "user";
  private final static String PASSWORD = "password";
  private final static int MAX_THREADS = 20;
  private static Gson gson = new Gson();


  public static void main(String[] argv) throws Exception {
    ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> map = new ConcurrentHashMap<>();
    Logger logger = LoggerFactory.getLogger(ThreadedWorker.class);
    //
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    factory.setVirtualHost(VH);
    factory.setPort(PORT);
    factory.setUsername(USERNAME);
    factory.setPassword(PASSWORD);
//    final Connection connection = factory.newConnection();
    Connection connection = factory.newConnection();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
          // max one message per receiver
          channel.basicQos(1);
          System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");
          logger.info(String.format(" [*] Thread waiting for messages. To exit press CTRL+C"));
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            LiftRide liftRide = gson.fromJson(message, LiftRide.class);
            System.out.println(" [x] Received '" + liftRide + "'");
            logger.info(String.format("Received (channel %d) %s",channel.getChannelNumber(),new String(delivery.getBody())));
            CopyOnWriteArrayList<String> list = map.getOrDefault(liftRide.getSkierId(), new CopyOnWriteArrayList<>());
            list.add(message);
            map.put(liftRide.getSkierId(), list);
            System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
            logger.info("Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
            System.out.println( String.format("Processed %s", new String(delivery.getBody())));
            logger.info(String.format("Processed %s", new String(delivery.getBody())));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          };
          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };

    // start threads and block to receive messages
//    Thread recv1 = new Thread(runnable);
//    Thread recv2 = new Thread(runnable);
//    recv1.start();
//    recv2.start();
//    Properties pro = new Properties();
//    pro.load(new FileReader("props.txt"));
//    int MAX_THREADS = Integer.valueOf(pro.getProperty("MAX_THREAD"));
    for (int i = 0; i < MAX_THREADS ; i++) {
      Thread recv = new Thread(runnable);
      recv.start();
    }
  }
}
