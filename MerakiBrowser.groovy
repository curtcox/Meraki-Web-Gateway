import groovy.json.*

class MerakiBrowser {

  final port
  final request
  final apiKey

MerakiBrowser(port,request,apiKey) {
  this.port    = port
  this.request = request
  this.apiKey  = apiKey
}

def linkTo(path) {
    return "http://localhost:$port${path.replaceAll('//','/')}"
}

def transform(object) {
    replaceIdsWithLinks(object)
    def dup = new ArrayList()
    dup.addAll(object)
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

def addLinks(object, keys) {
    for (key in keys) {
        object.add(linkForKey(key))
    }
}

def linkForKey(key) {
    def path = "${request.pathInfo}/$key"
    return new JsonSlurper().parseText("{ \"$key\" : \"${linkTo(path)}\" }")
}


def response() {
    def command = "${request.pathInfo}?${request.queryString}"
    return JsonOutput.toJson(transform(new Meraki().get(command,apiKey)))
}

}
