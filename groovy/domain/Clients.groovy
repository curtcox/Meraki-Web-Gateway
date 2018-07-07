package domain

class Clients {

    final String apiKey
    final Devices devices
    final Json json
    final int timeSpan

    Clients(devices,apiKey,json,timeSpan) {
        this.devices  = devices
        this.apiKey   = apiKey
        this.json     = json
        this.timeSpan = timeSpan
    }

    static Clients of(apiKey,timeSpan) {
        new Clients(Devices.of(apiKey),apiKey,new Json(),timeSpan)
    }

    def contentType() {
        'application/json'
    }

    def response() {
        json.from(all())
    }

    def Client[] all() {
        def clients = []
        for (device in devices.all()) {
            def serial = device.serial
            for (json in clientsFor(serial)) {
                if (json.id!=null) {
                    clients << Client.from(serial,json)
                }
            }
        }
        clients
    }

    def clientsFor(serial) {
        def meraki = new Meraki("/devices/$serial/clients?timespan=$timeSpan",null,apiKey)
        meraki.parsedJson()
    }

    static main(args) {
        System.out.println(of(config.Config.apiKey).all())
    }
}
