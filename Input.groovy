class Input {

    static def forParams(map,action) {
        return "<html><body>${form(map,action)}</body></html>"
    }

    static def form(map,action){
        return "<form method='post'>${fields(map)} ${submit(action)}</form>"
    }

    static def submit(name) {
        return "<input type='submit' value='$name'>"
    }

    static def fields(map) {
        def out = ""
        map.each { key, value ->
            out = out + field(key,value)
        }
        return out
    }

    static def field(key, value) {
        return "$key <input type='text' name='$key' value='$value'>"
    }


}
