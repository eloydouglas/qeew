package db;

import java.util.ArrayList;
import java.util.List;
import entities.Queue;
import enums.QueueType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MemoryDatabase {

  private List<Queue> queues = new ArrayList<>();

  public MemoryDatabase() {
    Queue q1 = new Queue(QueueType.ORDER);
    Queue q2 = new Queue(QueueType.PAYMENT);

    queues.add(q1);
    queues.add(q2);
  }

  public Queue createQueue(QueueType queueType) {
    Queue queue = new Queue(queueType);
    this.queues.add(queue);
    return queue;
  }

  public List<Queue> listQueues() {
    return this.queues;
  }

  public Queue findQueue(String queueType) {
    return queues.stream()
      .filter(q -> q.getQueueType().name().equals(queueType.toUpperCase()))
      .findFirst()
      .orElse(null);
  }
}
