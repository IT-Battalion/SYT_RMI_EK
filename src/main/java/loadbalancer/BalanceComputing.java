package loadbalancer;

import compute.Balance;
import compute.Compute;
import compute.Task;
import loadbalancer.balance.BalanceMethod;
import loadbalancer.balance.RoundRobin;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BalanceComputing implements Balance {
    private static final List<Compute> servers = new CopyOnWriteArrayList<>();
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
    public synchronized void register(String engine) throws NotBoundException, RemoteException {
        System.out.println("Register Engine: " + (servers.size() + 1));
        servers.add((Compute) registry.lookup(engine));
    }

    @Override
    public synchronized void unregister(String engine) throws NotBoundException, RemoteException {
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
        if (servers.isEmpty()) {
            System.err.println("No Servers online.");
            return null;
        }
        Compute ce = balancerMethod.getExecutionServer(servers);
        System.out.println("Executing Task on " + servers.indexOf(ce));
        try {
            ce.ping();
        } catch (RemoteException ex) {
            System.out.println("Ping hat nicht geantwortet.");
            servers.remove(ce);
            if (servers.isEmpty()) {
                System.err.println("No Servers online.");
                return null;
            }
            ce = balancerMethod.getExecutionServer(servers);
        }
        return ce.executeTask(t);
    }

    @Override
    public void shutdownEngine() throws RemoteException, NotBoundException {
        for (Compute engine :
                servers) {
            engine.shutdownEngine();
        }
        System.out.println("Load Balancer Shutdown");
        UnicastRemoteObject.unexportObject(this, true);
        //System.exit(0);
    }

    @Override
    public long ping() throws RemoteException {
        return System.currentTimeMillis();
    }
}
