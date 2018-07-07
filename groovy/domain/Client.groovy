package domain

@groovy.transform.Immutable class Client {
    String serial, id, description, dhcpHostname, ip, mac, mdnsName, switchport, vlan

    static Client from(serial,json) {
        new Client(
            serial:serial,
            id:json.id,description:json.description,dhcpHostname:json.dhcpHostname,
            ip:json.ip,mac:json.mac,mdnsName:json.mdnsName,
            switchport:json.switchport,vlan:json.vlan)
    }
}
