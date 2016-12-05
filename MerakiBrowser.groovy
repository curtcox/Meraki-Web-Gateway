import groovy.json.*

class MerakiBrowser {

  final request
  final apiKey

MerakiBrowser(request,apiKey) {
  this.request = request
  this.apiKey  = apiKey
}

def linkTo(path) {
    def server     = request.getServerName()
    def port       = request.getServerPort()
    def merakiPath = path.replaceAll('//','/')
    return port == 80
        ? "http://$server$merakiPath"
        : "http://$server:$port$merakiPath"
}

def transform(object,command) {
    replaceIdsWithLinks(object)
    def dup = new ArrayList()
    dup.addAll(object)
    addCommandInfo(dup,command)
    addOrganizationLinks(dup)
    addNetworkLinks(dup)
    return dup
}

def replaceIdsWithLinks(object) {
    for (item in object) {
        for (entry in item) {
            replaceValueWithLink(entry,'id','')
            replaceValueWithLink(entry,'serial','/clients?timespan=2592000')
        }
    }
}

def replaceValueWithLink(entry,key,suffix) {
    def path = request.pathInfo
    def value = entry.value
    if (entry.key == key && !path.endsWith(value.toString())) {
        def link = "${request.pathInfo}/$value$suffix"
        entry.setValue(linkTo(withoutPrefixIfNotNeeded(link)))
    }
}

def withoutPrefixIfNotNeeded(link) {
    link = trimToBranch(link,'/organizations/','/networks/')
    link = trimToBranch(link,'/networks/'     ,'/devices/')
    return link
}

def trimToBranch(link,root,branch) {
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
        addLinks(object,['admins', 'licenseState', 'inventory', 'snmp', 'thirdPartyVPNPeers', 'samlRoles', 'configTemplates', 'networks'])
    }
}

def addNetworkLinks(object) {
    if (onPage('/networks/')) {
        addLinks(object,['devices', 'siteToSiteVpn', 'traffic?timespan=2592000', 'accessPolicies', 'ssids', 'vlans'])
    }
}

def addCommandInfo(object,command) {
    object.add(jsonKeyValue("command",command))
}

def addLinks(object, keys) {
    for (key in keys) {
        object.add(linkForKey(key))
    }
}

def linkForKey(key) {
    return jsonKeyValue(key,linkTo("${request.pathInfo}/$key"))
}

def jsonKeyValue(key,value) {
    return new JsonSlurper().parseText("{ \"$key\" : \"$value\" }")
}

def response() {
    def command = "${request.pathInfo}?${request.queryString}"
    def meraki  = new Meraki(command,apiKey)
    return JsonOutput.toJson(transform(meraki.json(),meraki.command()))
}

}
