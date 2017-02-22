import javax.servlet.http.*

class Linker {

    final HttpServletRequest request
    final Json json

    Linker(request) {
        this(request,new Json())
    }

    Linker(request,json) {
        this.request = request
        this.json = json
    }

    def linkTo(path) {
        def server = request.getServerName()
        def port = request.getServerPort()
        def merakiPath = path.replaceAll('//', '/')
        return port == 80
            ? "http://$server$merakiPath"
            : "http://$server:$port$merakiPath"
    }

    ArrayList transform(ArrayList object, command) {
        replaceIdsWithLinks(object)
        addCommandInfo(object, command)
        addDocInfo(object, command)
        addOrganizationLinks(object)
        addNetworkLinks(object)
        object
    }

    def replaceIdsWithLinks(object) {
        for (item in object) {
            for (entry in item) {
                replaceValueWithLink(entry, 'id', '')
                replaceValueWithLink(entry, 'serial', '/clients?timespan=2592000')
            }
        }
    }

    def replaceValueWithLink(entry, key, suffix) {
        def path = request.pathInfo
        def value = entry.value
        if (entry.key == key && !path.endsWith(value.toString())) {
            def link = "${request.pathInfo}/$value$suffix"
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
        request.pathInfo.contains(page) && request.pathInfo.split('/').length == 3
    }

    def addOrganizationLinks(object) {
        if (onPage('/organizations/')) {
            addLinks(object, ['admins', 'licenseState', 'inventory', 'snmp', 'thirdPartyVPNPeers', 'samlRoles', 'configTemplates', 'networks'])
        }
    }

    def addNetworkLinks(object) {
        if (onPage('/networks/')) {
            addLinks(object, ['devices', 'devices/claim', 'siteToSiteVpn', 'traffic?timespan=2592000', 'accessPolicies', 'ssids', 'vlans', 'bind', 'unbind', 'delete'])
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
        jsonKeyValue(key, linkTo("${request.pathInfo}/$key"))
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
        Input.forParams([:],"Unknown Command for ${request.pathInfo}")
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
        return request.pathInfo.endsWith("/$name")
    }
}
