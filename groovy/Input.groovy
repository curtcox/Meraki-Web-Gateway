class Input {

    static def forParams(map,action) {
        "<html><body>${docs(action)}${form(map,action)}</body></html>"
    }

    static def docs(action) {
        "<pre>${Docs.fullTextForAction(action)}</pre>"
    }

    static def form(map,action) {
        "<form method='post'>${fields(map)} ${submit(action)}</form>"
    }

    static def submit(name) {
        "<input type='submit' value='$name'>"
    }

    static def fields(map) {
        def out = ""
        map.each { key, value ->
            out = out + field(key,value)
        }
        out
    }

    static def field(key, value) {
        "$key <input type='text' name='$key' value='$value'>"
    }


}
