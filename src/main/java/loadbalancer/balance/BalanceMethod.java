package loadbalancer.balance;

import compute.Compute;

import java.util.List;

public interface BalanceMethod {
    Compute getExecutionServer(List<Compute> servers);
}
