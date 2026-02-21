# TravelEasy
- Progetto di Ingegneria del Software di Giacomo Pisana, Eliana Scarpa, Giuliana Serio.
- Università degli studi di Catania, corso di laurea magistrale di Ingegneria Informatica.

## Requisiti
- JDK 17
- Maven 3.9.12

## Installazione Maven (Windows)
1. Scarica Maven:
   https://dlcdn.apache.org/maven/maven-3/3.9.12/binaries/apache-maven-3.9.12-bin.zip
2. Estrai lo zip, ad esempio in `C:\Tools\apache-maven-3.9.12`.
3. Aggiungi al `PATH` la cartella `C:\Tools\apache-maven-3.9.12\bin`.
4. Verifica l’installazione:
   ```powershell
   mvn -v
   ```

## Avvio progetto
```powershell
cd c:\Users\pisan\Desktop\IngSW\TravelEasy\travel-easy // o il vostro percorso
mvn -q javafx:run
```

## Build JAR
```powershell
cd c:\Users\pisan\Desktop\IngSW\TravelEasy\travel-easy
mvn -q -DskipTests package
```

Output:
- `travel-easy/target/travel-easy-1.0.0.jar`
