package json

import groovy.json.*

class Json {

    def from(params) {
        JsonOutput.toJson(params)
    }

    def fixBooleans(json) {
        json = json.replaceAll('"true"','true')
        json = json.replaceAll('"false"','false')
        json
    }

    def keyValue(key, value) {
        new JsonSlurper().parseText("{ \"$key\" : \"$value\" }")
    }

    def parse(text) {
        if (text.trim().isEmpty()) {
            text = "{}"
        }
        new JsonSlurper().parseText(text)
    }

    def paramsFrom(request) {
        def params = firstValues(request.getParameterMap())
        def json   = from(params)
        fixBooleans(json)
    }

    def firstValues(map) {
        def out = new HashMap()
        map.each { key, values ->
            out.put(key,values[0])
        }
        out
    }

}
