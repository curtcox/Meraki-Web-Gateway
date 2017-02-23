class Clients {

    final String apiKey
    final Devices devices
    final Json json

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
            for (json in clientsFor(device.serial,2592000)) {
                if (json.id!=null) {
                    clients << clientFrom(json)
                }
            }
        }
        clients
    }

    Client clientFrom(json) {
        new Client(
            id:json.id,description:json.description,dhcpHostname:json.dhcpHostname,
            ip:json.ip,mac:json.mac,mdnsName:json.mdnsName,
            switchport:json.switchport,vlan:json.vlan)
    }

    def clientsFor(serial,timespan) {
        def meraki = new Meraki("/devices/$serial/clients?timespan=$timespan",null,apiKey)
        meraki.parsedJson()
    }

    static main(args) {
        System.out.println(of(Config.apiKey).all())
    }
}
