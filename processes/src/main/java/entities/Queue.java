package entities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import enums.QueueType;
import enums.Status;
import jakarta.persistence.Entity;


@Entity
public class Queue{
  private QueueType queueType;

  private List<Process> processes = new LinkedList<>();
  private List<Process> processed = new LinkedList<>();

  public Queue(QueueType queueType) {
    this.queueType = queueType;
  }

  public List<Process> getProcesses() {
    return processes;
  }

  public List<Process> getProcessed() {
    return processed;
  }

  public QueueType getQueueType() {
    return queueType;
  }

  public Process addProcess(int priority) {
    Process process = new Process(this, Status.QUEUED, priority);

    for (int i = 0; i < this.processes.size(); i++) {
      Process p = this.processes.get(i);

      if (p.getStatus() != Status.RUNNING && p.getPriority() > priority) {
        this.processes.add(i, process);
        break;
      } else if (i == (this.processes.size() - 1)){
        this.processes.add(process);
        break;
      }
    }

    if(this.isEmpty()) {
      this.processes.add(process);
      process.start();
    }


    return process;
  }

  public Process next() {
    if (this.isEmpty()) {
      return null;
    }

    removeProcessed();


    Process nextProcess = this.processes.getFirst();

    if (nextProcess.getStatus() != Status.RUNNING) {
      nextProcess.start();
    }

    return nextProcess;
  }

  public Process cancel(Long processId) {
    Process process = this.processes.stream()
        .filter(p -> p.getId().equals(processId))
        .findFirst()
        .orElse(null);

    if (process != null) {
      process.cancel();
      return process;
    }

    return null;
  }

  public List<Process> clearProcessed() {
    this.processed = new LinkedList<>();
    return processed;
  }

  private void removeProcessed() {
    if (this.isEmpty()) {
      return;
    }

    Iterator<Process> processIter = this.processes.iterator();

    while(processIter.hasNext()) {

      Process current = processIter.next();
      Status status = current.getStatus();

      if (status == Status.QUEUED) {
        break;
      }

      if (status == Status.DONE || status == Status.CANCELED) {
        this.processed.add(current);
        processIter.remove();
      }
    }
  }

  private Boolean isEmpty() {
    return this.processes.size() == 0;
  }

}
