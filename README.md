# remon
リモートデスクトップ
Ver 0.1.0

* AES暗号化


# mvn
## client (client -> server(Desktop capture) or client -> through <- remote(Desktop capture))
```
mvn exec:java "-Dexec.mainClass=com.uchicom.remon.Main"
```

## server (client -> server(Desktop capture))
```
mvn exec:java "-Dexec.mainClass=com.uchicom.remon.Main" "-Dexec.args=-server -host localhost"
```

## through (client -> through <- remote(Desktop capture))
```
mvn exec:java "-Dexec.mainClass=com.uchicom.remon.Main" "-Dexec.args=-through -host localhost -receivePort 8081 -sendPort 8082"
```

## remote (client -> through <- remote(Desktop capture))
```
mvn exec:java "-Dexec.mainClass=com.uchicom.remon.Main" "-Dexec.args=-remote -host localhost"
```
