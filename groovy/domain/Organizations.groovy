package domain

class Organizations {

    final Meraki meraki

    Organizations(apiKey) {
        meraki = new Meraki("/organizations",null,apiKey)
    }

    static Organizations of(apiKey) {
        new Organizations(apiKey)
    }

    def Organization[] all() {
        def orgs = []
        for (json in meraki.parsedJson()) {
            def id = json.id
            if (id!=null) {
                orgs << organizationFrom(json)
            }
        }
        orgs
    }

    Organization organizationFrom(json) {
        new Organization(json.id,json.name)
    }

    static main(args) {
        System.out.println(of(config.Config.apiKey).all())
    }
}
