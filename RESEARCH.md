# Research RMI

## Was ist RMI und welches Prinzip der verteilten Programmierung kommt dabei zur Anwendung?

Remote Method Invocation (RMI, deutsch etwa „Aufruf entfernter Methoden“), gelegentlich auch als Methodenfernaufruf bezeichnet, ist der Aufruf einer Methode eines entfernten Java-Objekts und realisiert die Java-eigene Art des Remote Procedure Call. „Entfernt“ bedeutet dabei, dass sich das Objekt in einer anderen Java Virtual Machine befinden kann, die ihrerseits auf einem entfernten Rechner oder auf dem lokalen Rechner laufen kann. Dabei sieht der Aufruf für das aufrufende Objekt (bzw. dessen Programmierer) genauso aus wie ein lokaler Aufruf, es müssen jedoch besondere Ausnahmen (Exceptions) abgefangen werden, die zum Beispiel einen Verbindungsabbruch signalisieren können.
Entfernte Objekte können zwar auch von einem bereits im Programm bekannten entfernten Objekt bereitgestellt werden, für die erste Verbindungsaufnahme werden aber die Adresse des Servers und ein Bezeichner (ein RMI-URL) benötigt. Für den Bezeichner liefert ein Namensdienst auf dem Server eine Referenz auf das entfernte Objekt zurück. Damit dies funktioniert, muss sich das entfernte Objekt im Server zuvor unter diesem Namen beim Namensdienst registriert haben. Der RMI-Namensdienst wird über statische Methoden der Klasse java.rmi.Naming angesprochen. Der Namensdienst ist als eigenständiges Programm implementiert und wird RMI Registry genannt.
[1]
## Was sind Stubs? Welche Aufgabe hat dabei das Proxy-Objekt?
Auf der Client-Seite kümmert sich der sogenannte Stub um den Netzwerktransport. Der Stub muss entweder lokal oder über das Netz für den Client verfügbar sein.
[1]
The stub is an object, acts as a gateway for the client side. All the outgoing requests are routed through it. It resides at the client side and represents the remote object. When the caller invokes method on the stub object, it does the following tasks:

    It initiates a connection with remote Virtual Machine (JVM),
    It writes and transmits (marshals) the parameters to the remote Virtual Machine (JVM),
    It waits for the result
    It reads (unmarshals) the return value or exception, and
    It finally, returns the value to the caller.

Der Stub/Skeleton Layer ist das Interface zwischen den Applikationen, dem
Applikationslayer, und dem Rest des RMI Systems. Dieser Layer kümmert sich also nicht um
den Transport; er liefert lediglich Daten an den RRL.
Ein Client, welcher eine Methode auf einem remote Server aufruft, benutzt in Wirklichkeit
einen Stub oder ein Proxy Objekt für das remote Objekt, also quasi ein Ersatz für das remote
Objekt.
Ein Skeleton für ein remote Objekt ist die serverseitige Grösse, welche die Methodenaufrufe
an die remote Objektimplementation weiterleitet.
Die Kommunikation der Stubs mit dem clientseitigen RRL geschieht auf folgende Art und
Weise:
    • Der Stub (clientseitig) empfängt den Aufruf einer entfernten Methode und initialisiert
einen Call, einen Verbindungsaufbau zum entfernten Objekt.
    • Der RRL liefert eine spezielle Art I/O Stream, einen 'marshal' (Eingabe/ Ausgabe)
Stream, mit dessen Hilfe die Kommunikation mit der Serverseite des RRL stattfindet.
    • Der Stub führt den Aufruf der entfernten Methode durch und übergibt alle Argumente
an diesen Stream.
    • Der RRL liefert die Rückgabewerte der Methode an den Stub.
    • Der Stub bestätigt dem RRL, dass der Methodenaufruf vollständig und abgeschlossen
ist.
[2]

## Was wird in der Registry gespeichert?
Laut interface definition wird nur der Port gespeichert. Der hostname wird bei den jewiligen Methode mit übergeben.

## Wie kommt das Remote-Interface zum Einsatz? Was ist bei der Definition von Methoden zu beachten?
The Remote interface serves to identify interfaces whose methods may be invoked from a non-local virtual machine. Any object that is a remote object must directly or indirectly implement this interface. Only those methods specified in a "remote interface", an interface that extends java.rmi.Remote are available remotely.
Implementation classes can implement any number of remote interfaces and can extend other remote implementation classes. RMI provides some convenience classes that remote object implementations can extend which facilitate remote object creation. These classes are java.rmi.server.UnicastRemoteObject and java.rmi.activation.Activatable.

## Was ist bei der Weitergabe von Objekten unabdingbar?
Das Objekt muss Serializable sein und daher auch das Interface `Serializable` implementieren.

## Welche Methoden des UnicastRemoteObject kommen bei der Server-Implementierung zum Einsatz?
Das Unicast Remote Objekt ist die Oberklasse für alle RMI Implementationsklassen. [2]
- `exportObject`
- `readObject`
- `unexportObject`

## Wie kann der Server ein sauberes Schließen ermöglichen? Was muss mit dem exportierten Objekt geschehen?
Die exportierten Objekte müssen "unexportiert" werden und die Registry "unbinded". 

## Quellen
[1] "Wikipedia"; "Remote Method Invocation"; zuletzt besucht am 21.03.2022; https://de.wikipedia.org/wiki/Remote_Method_Invocation
[2] "joller-voss"; "RMI Uebersicht und Einfuehrung"; zuletzt besucht am 21.03.2022; http://www.joller-voss.ch/ndkjava/notes/rmi/RMI_Uebersicht_und_Einfuehrung.pdf