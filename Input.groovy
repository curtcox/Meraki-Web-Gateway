class Input {

    static def forParams(map) {
        return "<html><body><form method='post'>${fields(map)}</form></body></html>"
    }

    static def fields(map) {
        def out = ""
        map.each { key, value ->
            out = out + field(key,value)
        }
        out = out + '<input type="submit" value="Submit">'
        return out
    }

    static def field(key, value) {
        return "<input type='text' name='$key' value='$value'>"
    }


}
