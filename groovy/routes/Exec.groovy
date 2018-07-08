package routes

import meraki.*

class Exec {

    static def prompt(request) {
        def html = Page.of('prompt.html')
        def verb = request.getParameter("verb")
        def url  = request.getParameter("url")
        def json = request.getParameter("json")
        if (verb) {
            html = html.replace('GET', verb)
            html = html.replace('organizations', url)
            html = html.replace('{}', json)
            def result = exec(meraki(request),verb,url,json)
            html = html.replaceAll('result', result)
        }
        html
    }

    static def exec(meraki,verb,url,json) {
        if (json=='{}') {
            json = null
        }
        System.err.println("$verb $url $json")
        def result = meraki.makeRequest(verb,url,json)
        "verb=$verb\r url=$url\r json=$json\r result=$result\r"
    }

    static MerakiHttp meraki(request) {
        def check = new KeyChecker(request)
        new MerakiHttp(check.apiKeyFromSession())
    }
}
