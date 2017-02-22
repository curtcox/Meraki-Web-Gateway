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
if (matching(command,2,'organizations','licenseState'))    { return 'Return the license state for an organization' }
if (matching(command,2,'organizations','claim'))           { return 'Claim a device, license key, or order into an organization' }
if (matching(command,1,'networks'))                        { return 'Return a network' }
if (matching(command,2,'networks','bind'))                 { return bind() }
if (matching(command,2,'networks','unbind'))               { return unbind() }
if (matching(command,2,'networks','delete'))               { return delete() }
if (matching(command,3,'networks','devices','claim'))      { return claimDevice() }

return 'not found'

    }

    static def shortTextforAction(action) {
        if (action=='bind')          { return bind() }
        if (action=='unbind')        { return unbind() }
        if (action=='delete')        { return delete() }
        if (action=='devices/claim') { return claimDevice() }
        'not found'
    }

    static def bind()        { return 'Bind a network to a template' }
    static def unbind()      { return 'Unbind a network from a template' }
    static def delete()      { return 'Delete a network' }
    static def claimDevice() { return 'Claim a device into a network' }

    static def fullTextForAction(action) {
        def start = shortTextforAction(action)
        if (start=='not found') {
            return "No docs found for $action"
        }
        def end   = '=========='
        trimTo(start,end)
    }

    static def trimTo(start,end) {
        def fragment = docs()
        fragment = after(fragment,start)
        fragment = before(fragment,end)
        fragment
    }

    static def after(text,startText) {
        def start = text.indexOf(startText)
        text.substring(start)
    }

    static def before(text,endText) {
        def end = text.indexOf(endText)
        text.substring(0,end)
    }

    static def matching(command,slashes,... keys) {
        for (key in keys) {
            if (!command.contains(key)) {
                return false
            }
        }
        slashes == slashesIn(command)
    }

    static def slashesIn(command) {
        int counter = 0;
        for(int i=0; i<command.length(); i++ ) {
            if (command.charAt(i) == '/' ) {
                counter++
            }
        }
        counter
    }

}
