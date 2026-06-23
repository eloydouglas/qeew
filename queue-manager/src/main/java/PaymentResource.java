import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Payment;
import serializers.PaymentSerializer;
import serializers.ProcessSerializer;
import services.IProcess;

@Path("/payment")
@Produces(MediaType.APPLICATION_JSON)
public class PaymentResource {
  @Inject
  @RestClient
  IProcess processesService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Payment create(PaymentSerializer payment) {
    int priority = 10;

    if (payment.value >= 100 && payment.value <= 500) {
      priority = 5;
    } else if (payment.value > 500) {
      priority = 1;
    }

    return processesService.createPayment(new ProcessSerializer(priority));
  }

  @GET
  public List<Payment> list() {
    return processesService.listPayments();
  }

  
}
