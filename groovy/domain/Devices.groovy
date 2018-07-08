package domain

import json.*
import meraki.*

class Devices {

    final String apiKey
    final Networks networks
    final Json json

    Devices(networks,apiKey,json) {
        this.networks = networks
        this.apiKey   = apiKey
        this.json     = json
    }

    static Devices of(apiKey) {
        new Devices(Networks.of(apiKey),apiKey,new Json())
    }

    def contentType() {
        'application/json'
    }

    def response() {
        json.from(all())
    }

    def Device[] all() {
        def devices = []
        for (network in networks.all()) {
            for (json in devicesFor(network.id)) {
                if (json.serial!=null) {
                    devices << deviceFrom(json)
                }
            }
        }
        devices
    }

    Device deviceFrom(json) {
        new Device(
            serial:json.serial,networkId:json.networkId,lanIp:json.lanIp,
            mac:json.mac,model:json.model,name:json.name,
            address:json.address,lat:json.lat,lng:json.lng)
    }

    def devicesFor(networkId) {
        def meraki = new Meraki("/networks/$networkId/devices",null,apiKey)
        meraki.parsedJson()
    }

    static main(args) {
        System.out.println(of(config.Config.apiKey).all())
    }
}
