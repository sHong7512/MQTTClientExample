# MQTTClientExample

mqtt client example

## Make Mac Broker (with Mosquitto)

- Install Mosquitto

```
brew install mosquitto
```

- Start Mosquitto

```
brew services start mosquitto
```

- Stop Mosquitto

```
brew services stop mosquitto
```

- Running Mosquitto Non-Service (= Broker) (My MacBook Path)
 
```
/usr/local/opt/mosquitto/sbin/mosquitto -c /usr/local/etc/mosquitto/mosquitto.conf
```

or

```
mosquitto -c /usr/local/etc/mosquitto/mosquitto.conf
```

- Config Path (My MacBook)

```
/usr/local/etc/mosquitto/mosquitto.conf
```

- Test Message Publishing

```
/usr/local/opt/mosquitto/bin/mosquitto_pub -h 127.0.0.1 -p 1883 -t topic -m "test message"
```

or

```
mosquitto_pub -h 127.0.0.1 -p 1883 -t topic -m "test message"
```

- Subscribe

```
/usr/local/opt/mosquitto/bin/mosquitto_sub -h 127.0.0.1 -p 1883 -t topic
```

or

```
mosquitto_sub -h 127.0.0.1 -p 1883 -t topic
```

- Adding to mosquitto.conf for external execution

```
bind_address 0.0.0.0 # Allow All IPs

allow_anonymous true # Allowing permissions to outsiders
```

- Error: Address already in use

```
ps -ef | grep mosquitto

sudo kill 12345
```
