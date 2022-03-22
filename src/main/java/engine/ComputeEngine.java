/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package engine;

import compute.Balance;
import compute.Compute;
import compute.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeEngine implements Compute, Serializable {

    private static Registry registry;
    private static Balance balance;

    private final String realName;

    public ComputeEngine(String name) {
        super();
        this.realName = name;
    }

    @Override
    public <T> T executeTask(Task<T> t) {
        System.out.println("Executing Task");
        return t.execute();
    }

    public static Logger log = LogManager.getLogger(ComputeEngine.class);

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            if (registry == null) registry = LocateRegistry.getRegistry(args[0]);
            if (balance == null) balance = (Balance) registry.lookup(name);
            Compute engine = new ComputeEngine(name + args[1]);
            Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, Integer.parseInt(args[1]));

            registry.rebind(name + args[1], stub);
            balance.register(name + args[1]);

            log.info("ComputeEngine bound");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (!reader.readLine().equalsIgnoreCase("exit")) {}
            engine.shutdownEngine();
        } catch (Exception e) {
            log.error("ComputeEngine exception:");
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownEngine() throws RemoteException, NotBoundException {
        log.info("ComputeEngine shutdown");
        balance.unregister(this.realName);
        UnicastRemoteObject.unexportObject(this, true);
    }

    @Override
    public long ping() throws RemoteException {
        return System.currentTimeMillis();
    }

    public String getRealName() {
        return realName;
    }
}
