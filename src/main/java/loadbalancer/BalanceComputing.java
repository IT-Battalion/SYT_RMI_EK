package loadbalancer;

import compute.Balance;
import compute.Compute;
import compute.Task;
import loadbalancer.balance.BalanceMethod;
import loadbalancer.balance.RoundRobin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class BalanceComputing implements Balance {
    private static final List<Compute> servers = new ArrayList<>();
    private static Registry registry;
    private static final String name = "Compute";
    private static final BalanceMethod balancerMethod = new RoundRobin();

    public BalanceComputing() {
        super();
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Balance balancer = new BalanceComputing();
            Compute stub = (Compute) UnicastRemoteObject.exportObject(balancer, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind(name, stub);
            System.out.println("LoadBalancer bound");

            //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //while (!reader.readLine().equalsIgnoreCase("exit"));
        } catch (Exception e) {
            System.err.println("LoadBalancer exception:");
            e.printStackTrace();
        }
    }

    @Override
    public void register(String engine) throws NotBoundException, RemoteException {
        System.out.println("Register Engine: " + (servers.size() + 1));
        servers.add((Compute) registry.lookup(engine));
    }

    @Override
    public void unregister(String engine) throws NotBoundException, RemoteException {
        Compute comp = (Compute) registry.lookup(engine);
        if (servers.contains(comp)) {
            System.out.println("Unregister Engine: " + (servers.size() - 1));
            servers.remove(comp);
        } else {
            System.err.println("Engine not contained in Registered Servers.");
        }
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        Compute ce = balancerMethod.getExecutionServer(servers);
        System.out.println("Executing Task on " + servers.indexOf(ce));
        return ce.executeTask(t);
    }

    @Override
    public void shutdownEngine() throws RemoteException, NotBoundException {
        for (Compute engine :
                servers) {
            engine.shutdownEngine();
        }
        registry.unbind(name);
        UnicastRemoteObject.unexportObject(this, false);
        //System.exit(0);
    }
}
