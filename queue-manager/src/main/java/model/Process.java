package model;

import enums.Status;

public class Process {
  private Long id;
  private Status status;
  
  public Process(Long id, Status status) {
    this.id = id;
    this.status = status;
  }
  
  public Long getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

}
