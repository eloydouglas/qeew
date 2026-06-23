package resources;
import java.util.List;

import db.MemoryDatabase;
import entities.Queue;
import entities.Process;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import serializers.ProcessSerializer;

@Path("/queues")
@Produces(MediaType.APPLICATION_JSON)
public class QueuesResource {

  @Inject
  MemoryDatabase db;

  @GET
  public List<Queue> list(){
    return db.listQueues();
  }

  @GET
  @Path("/{queueType}/processes")
  public List<Process> processes(@PathParam("queueType") String queueType){
    Queue queue = db.findQueue(queueType);

    if(queue != null) {
      return queue.getProcesses();
    }

    return null;
  }

  @GET
  @Path("/{queueType}/processed")
  public List<Process> processed(@PathParam("queueType") String queueType){
    Queue queue = db.findQueue(queueType);

    if(queue != null) {
      return queue.getProcessed();
    }

    return null;
  }

  @POST
  @Path("/{queueType}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Process queueProcess(@PathParam("queueType") String queueType, ProcessSerializer process) {
    Queue queue = db.findQueue(queueType);
    return queue.addProcess(process.priority);
  }

  @POST
  @Path("/{queueType}/processes/{id}/cancel")
  public Process queueProcess(@PathParam("queueType") String queueType, @PathParam("id") Long processId) {
    Queue queue = db.findQueue(queueType);
    return queue.cancel(processId);
  }

}
