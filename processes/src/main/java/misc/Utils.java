package misc;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Utils {
  private static final ScheduledExecutorService scheduler =
    Executors.newScheduledThreadPool(1);

  /**
   * Executes the given action after a specified delay.
   *
   * @param action  The action to run after the delay.
   * @param delay   The delay amount.
   * @param unit    The time unit of the delay (e.g., TimeUnit.SECONDS).
   */
  public static void setTimeout(Runnable action, long delay, TimeUnit unit) {
      scheduler.schedule(action, delay, unit);
  }
  
}
