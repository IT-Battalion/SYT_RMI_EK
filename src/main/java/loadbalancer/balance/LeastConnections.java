package loadbalancer.balance;

import compute.Compute;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LeastConnections implements BalanceMethod {
    private final Map<Compute, Integer> connections = new ConcurrentHashMap<Compute, Integer>();

    @Override
    public Compute getExecutionServer(List<Compute> servers) {
        AtomicReference<Compute> least = new AtomicReference<>();
        AtomicInteger least2 = new AtomicInteger();
        connections.forEach((k, e) -> {
            if (least.get() == null) {
                least.set(k);
                least2.set(e);
            } else if (least2.get() > e) {
                least.set(k);
                least2.set(e);
            }
        });
        return least.get();
    }
}
