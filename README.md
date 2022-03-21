# Distributed Computing "*RMI Task Loadbalancer*" 

## Aufgabenstellung
Die detaillierte [Aufgabenstellung](TASK.md) beschreibt die notwendigen Schritte zur Realisierung.

## Implementierung
Zuerst habe ich mir die wikibooks Seite für die Fibonacci Implementierung angesehen und die Methode implementiert. [1]
Danach habe ich mir den Troubleshoot guide angesehen.

Zuerst habe ich wie folgt meine Java Versionen überprüft.
`javac -version`
```shell
javac 15.0.5
```
`java -version`
```shell
openjdk version "15.0.5" 2021-10-19
OpenJDK Runtime Environment Zulu15.36+13-CA (build 15.0.5+3-MTS)
OpenJDK 64-Bit Server VM Zulu15.36+13-CA (build 15.0.5+3-MTS, mixed mode, sharing)
```

Dann habe ich den Beispiel Code aus dem example-codes repository Ausgeführt.
```java
import java.net.SocketPermission;
import java.util.Properties;
import java.util.Set;

/**
 * This class is testing the java.security.AllPermission directive of the java.policy file
 *
 * Find on your System the file java.policy and add following lines for recursively
 * directory access to it:
 * 
 * grant codeBase "file:/home/user/workingspace/-" {
 *   permission java.security.AllPermission;
 * };
 *
 */
public class TestPermissions {

  public static void main(String[] args) {

    SecurityManager secManager = new SecurityManager();
    SocketPermission perm = new SocketPermission("localhost:1024-", "accept,connect,listen");

    if ( secManager != null )
      secManager.checkPermission(perm);
    System.setSecurityManager(secManager);

    Properties props = System.getProperties();
    Set<String> keys = props.stringPropertyNames();

    for( String o : keys )
      System.out.println(o+" = "+props.getProperty(o));

  }
}
```
Da es zuerst nicht funktioniert hat, habe ich eine neue Security Policy angelegt. Danach hat alles einwandfrei funktioniert.



## Quellen
[1] "Fibonacci Number Program"; wikibooks; zuletzt besucht am 21.03.2022; https://en.wikibooks.org/wiki/Algorithm_Implementation/Mathematics/Fibonacci_Number_Program
