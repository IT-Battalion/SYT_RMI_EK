package loadbalancer.balance;

import compute.Compute;

import java.util.List;

public class WeightedDistribution implements BalanceMethod{
    @Override
    public Compute getExecutionServer(List<Compute> servers) {
        return null;
    }
}
