
Add command argument: message template.
```
-pt,--payloadtempate <arg>   payload template to be used.
```
for example:
```
./mqttloader -b tcp://127.0.0.1:1883 -v 3 -p 1 -s 1 -m 10000 -t application/sensor_data -pt '{ "ts": ${auto_increment}, "temperature": 32.1, "voltage": 321, "name": "d02", "devid": 2 }'
```
<b>-pt</b> specifies the message content template, where  <b>${auto_increment}</b>  is a placeholder, which is automatically replaced with the timestamp when mqttloader is started, and automatically increment every time a new message is published.
<br>

#
<br><br>

# MQTTLoader

MQTTLoader is a load testing tool (client tool) for MQTT.  
It supports both MQTT v5.0 and v3.1.1.

- [Usage (English)](https://github.com/dist-sys/mqttloader/blob/master/doc/usage_en.md)
- [Usage (Japanese)](https://github.com/dist-sys/mqttloader/blob/master/doc/usage_jp.md)

Below is an execution result sample.

```
-----Publisher-----
Maximum throughput[msg/s]: 53068
Average throughput[msg/s]: 49894.57
Number of published messages: 349262
Per second throughput[msg/s]: 44460, 47558, 52569, 53068, 51041, 51583, 48983

-----Subscriber-----
Maximum throughput[msg/s]: 53050
Average throughput[msg/s]: 49891.14
Number of received messages: 349238
Per second throughput[msg/s]: 44399, 47587, 52566, 53050, 51078, 51575, 48983
Maximum latency[ms]: 24
Average latency[ms]: 1.39
```

MQTTLoader is licensed under the Apache License, Version2.0.

## Contact
https://www.banno-lab.net/en/contact/
