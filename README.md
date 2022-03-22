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

Anschließend habe ich die Klasse ComputePi in ComputeTasks umbenannt. Daher musste auch in der build.gradle der compute Tasks geändert werden, was ich zuerster vergessen habe. 
Danach habe ich das Program getestet und es ist problemlos gelaufen.

Nun habe ich mich an das "saubere Schließen" gesetzt.
Dafür habe ich im Compute Interface eine neue Methode definiert die für das herunterfahren des Servers gedacht ist. Diese habe ich dann implementiert und auf der Client seite, nachdem alle Berechnungen durchgeführt wurden aufgerufen. Anschließend habe ich das Programm nochmals getestet. Wieder hat es problemlos funktioniert und der Server wird nach den Berechnungen herunter gefahren.

Nun habe ich die Implementierung des LoadBalancers begonnen. Zuerst habe ich ein neues Interface `Balance` erstellt, welches Compute extended. In diesem habe ich dann zwei neue Methoden (`register`, `unregister`) erstellt.
Danach habe ich die Klasse BalanceComputing erstellt und einen neuen task im `build.gradle` erstellt. Dort habe ich dann das neue Interface implementiert. 
ComputeEngine wurde dann so abgeändert, dass sie die Tasks vom Balancer erhält und sich beim starten registriert, bzw. beim stoppen unregistert. 
Die Balancing Method wurde mittels Interface realisiert um auch andere Balancing methoden optimal implementieren zu können.

Das ausführen von Tasks und das Balancing hat danach bereits ausgezeichnet funktioniert. Jedoch gab es beim "sauberen schließen" der Server über den Balancer ein Problem.

Nachdem ich für die ArrayList eine ThreadSafe liste verwendet habe, und die register/unregister Methode synchronized habe, hat dann alles funktioniert. Am Ende habe ich noch mittels System.in (build.gradle) einen exit command eingebaut, mit welchem man ganz einfach die einzelnen Server & den LoadBalancer herunterfahren kann.

Dann habe ich EK begonnen. Da ich bereits im vorhinein daran gedacht habe, dass man verschiedene Balancing Strategien verwenden könnte, habe ich diese mittels StrategyPattern implementiert. Dementsprechend ist es realtiv einfach gewesen, unterschiedliche Methoden zu implementieren.

## Quellen
[1] "Fibonacci Number Program"; wikibooks; zuletzt besucht am 21.03.2022; https://en.wikibooks.org/wiki/Algorithm_Implementation/Mathematics/Fibonacci_Number_Program
