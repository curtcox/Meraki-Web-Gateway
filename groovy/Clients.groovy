class Clients {

    final String apiKey
    final Devices devices
    final Json json
    final int timeSpan = 2592000

    Clients(devices,apiKey,json) {
        this.devices = devices
        this.apiKey  = apiKey
        this.json    = json
    }

    static Clients of(apiKey) {
        new Clients(Devices.of(apiKey),apiKey,new Json())
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
        System.out.println(of(Config.apiKey).all())
    }
}
