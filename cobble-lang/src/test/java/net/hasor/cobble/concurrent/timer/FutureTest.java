package net.hasor.cobble.concurrent.timer;
import net.hasor.cobble.concurrent.future.BasicFuture;
import org.junit.Test;

public class FutureTest {
    @Test
    public void timerTest() throws Throwable {
        BasicFuture<Object> future = new BasicFuture<>();

        future.onFailed(f -> {
            System.out.println("onFailed");
        }).onCancel(f -> {
            System.out.println("onCancel");
        }).onFinal(f -> {
            System.out.println("onFinal");
        });

        future.cancel();
    }
}
