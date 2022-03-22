package loadbalancer.balance;

import compute.Compute;

import java.util.List;

public class RoundRobin implements BalanceMethod {
    public static int lastServerIndex;

    public Compute getExecutionServer(List<Compute> servers) {
        int nextServer = ++lastServerIndex;
        if (nextServer < servers.size()) {
            return servers.get(nextServer);
        } else {
            lastServerIndex = 0;
            return servers.get(0);
        }
    }
}
