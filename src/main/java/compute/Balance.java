package compute;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface Balance extends Compute {
    void register(String engine) throws RemoteException, NotBoundException;
    void unregister(String engine) throws RemoteException, NotBoundException;
}
