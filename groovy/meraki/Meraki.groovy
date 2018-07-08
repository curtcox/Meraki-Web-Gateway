package meraki

import json.*
import http.*

class Meraki {

    final String path
    final params
    final MerakiHttp http
    final Json json = new Json()

    Meraki(path, params, apiKey) {
        System.out.println("$path $params $apiKey")
        this.path   = new PathScrubber(path,apiKey).scrubbed()
        this.params = params
        this.http   = new MerakiHttp(apiKey)
    }

    String deletePath() {
        path.substring(0,path.indexOf('/delete'))
    }

    String verb() {
        if (matching('bind', 'unbind', 'claim')) {
            return 'POST'
        }
        if (matching('delete')) {
            return 'DELETE'
        }
        'GET'
    }

    def matching(... keys) {
        for (key in keys) {
            if (path.endsWith("/$key")) {
                return true
            }
        }
        false
    }

    def parsedJson() {
        def result = exec()
        try {
            return parsedJson(result)
        } catch (e) {
            def content   = result.content
            def errorPage = content.replaceAll("page", "page ($path)")
            throw new RuntimeException(errorPage, e)
        }
    }

    def parsedJson(HttpResponse result) {
        def parser = new Json()
        def parsed = parser.parse(result.content)
        def dup = new ArrayList()
        dup.addAll(parsed)
        dup.add(jsonKeyValue('status',result.status))
        dup
    }

    def jsonKeyValue(key, value) {
        json.keyValue(key,value)
    }

    String command() {
        if (verb() == 'GET') {
            return "GET $path"
        }

        if (verb() == 'DELETE') {
            return "DELETE ${deletePath()}"
        }

        "POST $path $params"
    }

    HttpResponse exec() {
        if (verb() == 'GET') {
            return http.get(path)
        }

        if (verb() == 'DELETE') {
            return http.delete(deletePath())
        }

        http.post(path,params)
    }

}
