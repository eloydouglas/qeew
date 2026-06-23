package services;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Order;
import model.Payment;
import serializers.ProcessSerializer;

@RegisterRestClient(baseUri = "http://localhost:8080/queues")
public interface IProcess {
  @POST
  @Path("/payment")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Payment createPayment(ProcessSerializer process);

  @GET
  @Path("/payment/processed")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Payment> listPayments();

  @POST
  @Path("/order")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Order createOrder(ProcessSerializer process);

  @GET
  @Path("/order/processed")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Order> listOrder();

}