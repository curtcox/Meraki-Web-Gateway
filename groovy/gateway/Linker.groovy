package gateway

import json.*
import routes.*
import javax.servlet.http.*

class Linker {

    final String server
    final int port
    final String path
    final Json json

    static Linker of(request) {
        new Linker(request.serverName,request.serverPort,request.pathInfo,new Json())
    }

    Linker(server,port,path,json) {
        this.server = server
        this.port = port
        this.path = path
        this.json = json
    }

    def linkTo(path) {
        def merakiPath = path.replaceAll('//', '/')
        port == 80
            ? "http://$server$merakiPath"
            : "http://$server:$port$merakiPath"
    }

    ArrayList transform(ArrayList object, command) {
        replaceIdsWithLinks(object)
        addCommandInfo(object, command)
        addDocInfo(object, command)
        addOrganizationLinks(object)
        addNetworkLinks(object)
        addDevicesLinks(object)
        object
    }

    def replaceIdsWithLinks(object) {
        for (item in object) {
            for (entry in item) {
                replaceValueWithLink(entry, 'id', '')
                replaceValueWithLink(entry, 'serial', '')
            }
        }
    }

    def replaceValueWithLink(entry, key, suffix) {
        def value = entry.value
        if (entry.key == key && !path.endsWith(value.toString())) {
            def link = "$path/$value$suffix"
            entry.setValue(linkTo(withoutPrefixIfNotNeeded(link)))
        }
    }

    def withoutPrefixIfNotNeeded(link) {
        link = trimToBranch(link, '/organizations/', '/networks/')
        link = trimToBranch(link, '/networks/', '/devices/')
        link
    }

    def trimToBranch(link, root, branch) {
        if (link.contains(branch) && link.contains(root)) {
            return link.substring(link.indexOf(branch))
        }
        link
    }

    def onPage(page) {
        def parts = page.split(' ')
        for (part in parts) {
          if (!path.contains(part)) {
            return false
          }
        }
        path.split('/').length == parts.length + 2
    }

    def addOrganizationLinks(object) {
        if (onPage('/organizations/')) {
            addLinks(object,[
              'admins', 'deviceStatuses', 'licenseState', 'inventory', 'snmp',
               'thirdPartyVPNPeers', 'samlRoles', 'configTemplates', 'networks'
            ])
        }
    }

    def addNetworkLinks(object) {
        if (onPage('/networks/')) {
            addLinks(object, [
              'devices', 'devices/claim', 'siteToSiteVpn',
              'traffic?timespan=2592000', 'accessPolicies', 'groupPolicies',
               'ssids', 'vlans', 'bind', 'unbind', 'delete'
            ])
        }
    }

    def addDevicesLinks(object) {
        if (onPage('/networks/ /devices/')) {
            addLinks(object, ['performance', 'uplink'])
        }
    }

    def addCommandInfo(object, command) {
        object.add(jsonKeyValue("command", command.replaceAll('"',"'")))
    }

    def addDocInfo(object, command) {
        object.add(jsonKeyValue("doc", Docs.shortTextforCommand(command)))
    }

    def addLinks(object, keys) {
        for (key in keys) {
            object.add(linkForKey(key))
        }
    }

    def linkForKey(key) {
        jsonKeyValue(key, linkTo("$path/$key"))
    }

    def jsonKeyValue(key, value) {
        json.keyValue(key,value)
    }

    def inputForParams() {
        for (def entry : commandParamMap()) {
            def command = entry.key
            def params = entry.value
            if (onCommand(command)) {
                return Input.forParams(params,command)
            }
        }
        Input.forParams([:],"Unknown Command for $path")
    }

    def commandParamMap() {
        [
            'bind'          : ['configTemplateId': 'N_1234', 'autoBind': false],
            'unbind'        : [:],
            'delete'        : [:],
            'devices/claim' : ['serial': 'Q2XX-XXXX-XXXX']
        ]
    }

    def onCommand(name) {
        path.endsWith("/$name")
    }
}
