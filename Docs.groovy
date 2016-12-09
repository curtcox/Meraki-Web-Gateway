class Docs {

    static def docs() {
        return Page.of('api.txt')
    }

    static def shortTextforCommand(command) {

if (matching(command,1,'organizations'))                   { return 'Return an organization' }
if (matching(command,0,'organizations'))                   { return 'List the organizations that the user has privileges on' }
if (matching(command,2,'organizations','admins'))          { return 'List the dashboard administrators in this organization' }
if (matching(command,2,'organizations','networks'))        { return 'List the networks in an organization' }
if (matching(command,2,'organizations','configTemplates')) { return 'List the configuration templates for this organization' }
if (matching(command,1,'networks'))                        { return 'Return a network' }
if (matching(command,2,'networks','bind'))                 { return bind() }
return 'not found'

    }

    static def shortTextforAction(action) {
        if (action=='bind') { return bind() }
        return 'not found'
    }

    static def bind() { return 'Bind a network to a template' }

    static def fullTextForAction(action) {
        def start = shortTextforAction(action)
        def end   = '=========='
        return trimTo(start,end)
    }

    static def trimTo(start,end) {
        def fragment = docs()
        fragment = after(fragment,start)
        fragment = before(fragment,end)
        return fragment
    }

    static def after(text,startText) {
        def start = text.indexOf(startText)
        return text.substring(start)
    }

    static def before(text,endText) {
        def end = text.indexOf(endText)
        return text.substring(0,end)
    }

    static def matching(command,slashes,... keys) {
        for (key in keys) {
            if (!command.contains(key)) {
                return false
            }
        }
        return slashes == slashesIn(command)
    }

    static def slashesIn(command) {
        int counter = 0;
        for(int i=0; i<command.length(); i++ ) {
            if (command.charAt(i) == '/' ) {
                counter++
            }
        }
        return counter
    }

}
