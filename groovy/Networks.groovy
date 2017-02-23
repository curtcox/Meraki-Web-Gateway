class Networks {

    final String apiKey
    final Organizations organizations
    final Json json

    Networks(organizations,apiKey,json) {
        this.organizations = organizations
        this.apiKey        = apiKey
        this.json          = json
    }

    static Networks of(apiKey) {
        new Networks(Organizations.of(apiKey),apiKey,new Json())
    }

    def contentType() {
        'application/json'
    }

    def response() {
        json.from(all())
    }

    def Network[] all() {
        def networks = []
        for (org in organizations.all()) {
            for (json in networksFor(org.id)) {
                if (json.id!=null) {
                    networks << networkFrom(json)
                }
            }
        }
        networks
    }

    Network networkFrom(json) {
        new Network(json.id,json.organizationId,json.name,json.tags,json.timeZone,json.type)
    }

    def networksFor(orgId) {
        def meraki = new Meraki("/organizations/$orgId/networks",null,apiKey)
        meraki.parsedJson()
    }

    static main(args) {
        System.out.println(of(Config.apiKey).all())
    }

}
