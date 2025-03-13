```
 _____         _
|     |___ ___| |_ ___ ___
| | | | . |  _| '_| -_|  _|
|_|_|_|___|___|_,_|___|_|
v0.12.0
```

# Ejamplo de configuración para **Smart Campus UIS**

Este es un ejemplo de configuración en YAML para la simulación de sensores en la plataforma **Smart Campus UIS**

```yaml
name: Smart UIS Sample

protocols:
  - type: mqtt
    host: mosquitto
    port: 1883
    topic: device-messages
    clientId: mocker-client-1
    username: user
    password: password

sampler:
  type: loop
  delay: 1000

generators:

  - type: timestamp
    name: TimestampGenerator

  - type: boolean
    name: BooleanGenerator
    probability: 0.5

  - type: random_integer
    name: RandomInteger
    min: 10
    max: 100

  - type: random_double
    name: RandomDouble
    min: 10
    max: 100
    decimals: 3

  - type: string
    name: StringWeightedGenerator
    sampling: weighted
    values:
      - RUNNING
      - IDLE
      - STOPPED
    weights:
      - 0.8
      - 0.1
      - 0.1
```

## Ejemplo de Mensaje Simulado .txt

Este es un ejemplo de configuración del template para la simulación de sensores en la plataforma Smart UIS.

```txt
{
    "deviceUUID": "1",
    "timeStamp": "{{TimestampGenerator}}",
    "topic": "device-message",
    "alert": {{BooleanGenerator}},
    "status": "{{StringWeightedGenerator}}",
    "values": {
        "Temperature": {{RandomInteger}},
        "Pressure": {{RandomDouble}}
    }
}
```

Este JSON representa un mensaje de ejemplo generado a partir de los valores simulados definidos en la configuración YAML.
