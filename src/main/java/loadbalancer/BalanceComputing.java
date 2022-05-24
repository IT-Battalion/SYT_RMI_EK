package loadbalancer;

import compute.Balance;
import compute.Compute;
import compute.Task;
import engine.ComputeEngine;
import loadbalancer.balance.BalanceMethod;
import loadbalancer.balance.RoundRobin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
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

    public static Logger log = LogManager.getLogger(BalanceComputing.class);

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Balance balancer = new BalanceComputing();
            Compute stub = (Compute) UnicastRemoteObject.exportObject(balancer, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind(name, stub);
            log.info("LoadBalancer bound");;

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
            balancer.shutdownEngine();
        } catch (AccessException e) {
            log.error("Operation is not permitted.");
        } catch (NotBoundException e) {
            log.error("The Name is currently not bound.");
        } catch (RemoteException e) {
            log.error("Registry Reference could not be created.");
        } catch (IOException e) {
            log.error("Reading Input failed", e);
        }
    }

    @Override
    public synchronized void register(String engine) throws NotBoundException, RemoteException {
        log.info("Register Engine: " + (servers.size() + 1));
        servers.add((Compute) registry.lookup(engine));
    }

    @Override
    public synchronized void unregister(String engine) throws NotBoundException, RemoteException {
        Compute comp = (Compute) registry.lookup(engine);
        if (servers.contains(comp)) {
            log.info("Unregister Engine: " + (servers.size() - 1));
            servers.remove(comp);
            registry.unbind(engine);
        } else {
            log.warn("Engine not contained in Registered Servers.");
        }
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        if (servers.isEmpty()) {
            log.error("No Servers online.");
            return null;
        }
        Compute ce = balancerMethod.getExecutionServer(servers);
        log.info("Executing Task on " + servers.indexOf(ce));
        try {
            ce.ping();
        } catch (RemoteException ex) {
            log.warn("Ping hat nicht geantwortet.");
            try {
                unregister(((ComputeEngine) ce).getRealName());
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
            if (servers.isEmpty()) {
                log.error("No Servers online.");
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
        log.info("Load Balancer Shutdown");
        UnicastRemoteObject.unexportObject(this, true);
        //System.exit(0);
    }

    @Override
    public long ping() throws RemoteException {
        return System.currentTimeMillis();
    }
}
