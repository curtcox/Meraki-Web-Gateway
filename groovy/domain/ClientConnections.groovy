package domain

import json.*
import meraki.*
import java.time.*
import java.time.temporal.*

class ClientConnections {

    final String apiKey
    final Devices devices
    final Json json
    final int timeSpan
    Instant start

    ClientConnections(devices,apiKey,json,timeSpan) {
        this.devices  = devices
        this.apiKey   = apiKey
        this.json     = json
        this.timeSpan = timeSpan
    }

    static ClientConnections of(apiKey,timeSpan) {
        new ClientConnections(Devices.of(apiKey),apiKey,new Json(),timeSpan)
    }

    def contentType() {
        'application/json'
    }

    def response() {
        json.from(all())
    }

    def ClientConnectionsSnapshot all() {
        def connections = []
        for (device in devices.all()) {
            def serial = device.serial
            for (json in clientsFor(serial)) {
                if (json.id!=null) {
                    connections << connectionFrom(device,serial,json)
                }
            }
        }
        new ClientConnectionsSnapshot(timeRange(),connections)
    }

    ClientConnection connectionFrom(device,serial,json) {
        def client    = Client.from(serial,json)
        def usage     = new Usage(json.usage.sent,json.usage.recv)
        new ClientConnection(device:device,client:client,usage:usage)
    }

    def timeRange() {
        new TimeRange(start:start,end:end())
    }

    def clientsFor(serial) {
        start = Instant.now()
        def meraki = new Meraki("/devices/$serial/clients?timespan=$timeSpan",null,apiKey)
        meraki.parsedJson()
    }

    def end() {
        start.minus(timeSpan,ChronoUnit.SECONDS)
    }

    static main(args) {
        System.out.println(of(config.Config.apiKey).all())
    }
}
