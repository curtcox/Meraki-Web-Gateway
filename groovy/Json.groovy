import groovy.json.*

class Json {

    static from(params) {
        return JsonOutput.toJson(params)
    }

    static def fixBooleans(json) {
        json = json.replaceAll('"true"','true')
        json = json.replaceAll('"false"','false')
        return json
    }

    static def keyValue(key, value) {
        return new JsonSlurper().parseText("{ \"$key\" : \"$value\" }")
    }

    static def parse(text) {
        if (text.trim().isEmpty()) {
            text = "{}"
        }
        return new JsonSlurper().parseText(text)
    }

    static def paramsFrom(request) {
        def params = firstValues(request.getParameterMap())
        def json   = from(params)
        return fixBooleans(json)
    }

    static def firstValues(map) {
        def out = new HashMap()
        map.each { key, values ->
            out.put(key,values[0])
        }
        return out
    }

}
