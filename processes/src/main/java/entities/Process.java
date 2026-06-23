package entities;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

import enums.Status;

import jakarta.persistence.Entity;

import misc.Utils;

@Entity
public class Process{
  private Long id;
  
  @JsonIgnore
  private Queue queue;

  private Status status;
  private int priority;

  public Process(Queue queue, Status status, int priority) {
    this.id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    this.queue = queue;
    this.status = status;
    this.priority = priority;
  }

  public Long getId() {
    return id;
  }

  public Queue getQueue() {
    return queue;
  }

  public Status getStatus() {
    return status;
  }

  public int getPriority() {
    return priority;
  }

  public void start(){
    this.status = Status.RUNNING;
    Utils.setTimeout(() -> {
      this.done();
    }, 15000, TimeUnit.MILLISECONDS);
  }

  public void cancel(){
    if (this.status == Status.QUEUED) {
      this.status = Status.CANCELED;
    }
  }

  private void done(){
    this.status = Status.DONE;
    this.queue.next();
  }

  public String toString() {
    return "{ProcessId: " + this.getId().toString() + ", Status: " + this.getStatus() +  "}";
  }
}
