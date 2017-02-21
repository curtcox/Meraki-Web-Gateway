import javax.servlet.http.*

class Linker {

    final HttpServletRequest request

    Linker(request) {
        this.request = request
    }

    def linkTo(path) {
        def server = request.getServerName()
        def port = request.getServerPort()
        def merakiPath = path.replaceAll('//', '/')
        return port == 80
            ? "http://$server$merakiPath"
            : "http://$server:$port$merakiPath"
    }

    ArrayList transform(object, command) {
        replaceIdsWithLinks(object)
        def dup = new ArrayList()
        dup.addAll(object)
        addCommandInfo(dup, command)
        addDocInfo(dup, command)
        addOrganizationLinks(dup)
        addNetworkLinks(dup)
        return dup
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
        return link
    }

    def trimToBranch(link, root, branch) {
        if (link.contains(branch) && link.contains(root)) {
            return link.substring(link.indexOf(branch))
        }
        return link
    }

    def onPage(page) {
        return request.pathInfo.contains(page) && request.pathInfo.split('/').length == 3
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
        return jsonKeyValue(key, linkTo("${request.pathInfo}/$key"))
    }

    def jsonKeyValue(key, value) {
        return Json.keyValue(key,value)
    }

    def inputForParams() {
        for (def entry : commandParamMap()) {
            def command = entry.key
            def params = entry.value
            if (onCommand(command)) {
                return Input.forParams(params,command)
            }
        }
        return Input.forParams([:],"Unknown Command for ${request.pathInfo}")
    }

    def commandParamMap() {
        return [
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
