import groovy.json.*

class Input {

  static def forParams(map) {
      return "<html><body><form>${fields(map)}</form></body></html>"
  }

    static def fields(map) {
        def out = ""
        map.each { entry ->
            out = out + field(entry.key,entry.value)
        }
        out = out + '<input type="submit" value="Submit">'
        return out
    }

    static def field(key,value) {
        return "<input type='text' name='$key' value='$value'>"
    }

    static def jsonFor(request) {
        return JsonOutput.toJson(request.getParameterMap())
    }

}
