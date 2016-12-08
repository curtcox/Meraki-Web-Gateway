class Exec {

    static def prompt(request) {
        def html = Page.of('prompt.html')
        def command = request.getParameter("cmd")
        if (command) {
            html = html.replaceAll('command', command.replaceAll('"', '&quot;'))
            def result = exec(command)
            html = html.replaceAll('result', result)
        }
        return html
    }

    static def exec(command) {
        System.err.println("command=" + command)
        def withPath = "scripts/$command"
        def result = withPath.execute()
        def text = result.text
        System.err.println("text=" + text)
        return "command=$command\r" +
                "result=$result\r" +
                "text=$text\r"
    }
}
