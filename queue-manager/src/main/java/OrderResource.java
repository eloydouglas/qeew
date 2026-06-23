import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Order;
import serializers.OrderSerializer;
import serializers.ProcessSerializer;
import services.IProcess;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
  @Inject
  @RestClient
  IProcess processesService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Order create(OrderSerializer order) {
    int priority = 10;

    if (order.quantity <= 10) {
      priority = 5;
    } else if (order.quantity > 10) {
      priority = 1;
    }

    return processesService.createOrder(new ProcessSerializer(priority));
  }

  @GET
  public List<Order> list() {
    return processesService.listOrder();
  }
}
