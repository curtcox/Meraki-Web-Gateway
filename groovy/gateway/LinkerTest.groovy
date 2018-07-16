package gateway

import javax.servlet.http.*

class LinkerTest extends test.Test {

    final HttpServletRequest request = Mock(HttpServletRequest)

    def "Linker can be made from factory"() {
        def server = 'big iron'

        when:
        def linker = Linker.of(request)

        then:
        request.serverName >> server

        expect:
        linker instanceof Linker
        linker.server == server
    }

    def "transform adds command to output"() {
      when:
      def linker  = Linker.of(request)
      def object  = []
      def command = 'bogus command'
      def out     = linker.transform(object, command)

      then:
      request.serverName >> ''
      request.pathInfo   >> ''

      expect:
      out.contains([command:command])
    }

    def "transform adds adds missing doc message when doc not found"() {
      when:
      def linker  = Linker.of(request)
      def object  = []
      def command = 'command with no docs'
      def out     = linker.transform(object, command)

      then:
      request.serverName >> ''
      request.pathInfo   >> ''

      expect:
      out.contains([doc:"not found"])
    }

    def "trasform lets status pass thru"() {
      when:
      def linker  = Linker.of(request)
      def status  = 418
      def object  = [[status:status]]
      def command = ''
      def out     = linker.transform(object, command)

      then:
      request.serverName >> ''
      request.pathInfo   >> ''

      expect:
      out.contains([status:status])
    }

    def "transform adds doc message when doc found"() {
      when:
      def linker  = Linker.of(request)
      def object  = []
      def command = 'GET organizations'
      def out = linker.transform(object, command)

      then:
      request.serverName >> ''
      request.pathInfo   >> ''

      expect:
      out.contains([doc:"List the organizations that the user has privileges on"])
    }

    def "transform replaces org id with link on /organizations"() {
      when:
      def linker  = Linker.of(request)
      def id      = 88
      def name    = 'Org name'
      def object  = [[id:id,name:name]]
      def command = 'GET organizations'
      def out = linker.transform(object, command)

      then:
      request.serverName >> 'server'
      request.pathInfo   >> '/organizations'
      request.serverPort >> 8080

      expect:
      out == [
         [id: 'http://server:8080/organizations/88', name: 'Org name'],
         [command: 'GET organizations'],
         [doc:     'List the organizations that the user has privileges on']
      ]
    }

    def "transform adds organization specific links on /organizations/[orgId]"() {
      when:
      def linker  = Linker.of(request)
      def id      = 1007
      def name    = 'MI6'
      def object  = [[id:id,name:name]]
      def command = 'GET organizations/1007'
      def out = linker.transform(object, command)

      then:
      request.serverName >> 's'
      request.pathInfo   >> '/organizations/1007'
      request.serverPort >> 80

      expect:
      out == [
         [id: 1007, name: 'MI6'],
         [command:            'GET organizations/1007'],
         [doc:                'Return an organization'],
         [admins:             'http://s/organizations/1007/admins'],
         [deviceStatuses:     'http://s/organizations/1007/deviceStatuses'],
         [licenseState:       'http://s/organizations/1007/licenseState'],
         [inventory:          'http://s/organizations/1007/inventory'],
         [snmp:               'http://s/organizations/1007/snmp'],
         [thirdPartyVPNPeers: 'http://s/organizations/1007/thirdPartyVPNPeers'],
         [samlRoles:          'http://s/organizations/1007/samlRoles'],
         [configTemplates:    'http://s/organizations/1007/configTemplates'],
         [networks:           'http://s/organizations/1007/networks']
      ]
    }

    def "transform adds network specific links on /networks/[networkId]"() {
      when:
      def linker  = Linker.of(request)
      def id      = 'L_7'
      def name    = '13th floor'
      def object  = [[id:id,name:name,organizationId:'O_3']]
      def command = 'GET networks/L_7'
      def out = linker.transform(object, command)

      then:
      request.serverName >> 's'
      request.pathInfo   >> '/networks/L_7'
      request.serverPort >> 80

      expect:
      out == [
         [id: 'L_7' , name: '13th floor', organizationId:'O_3'],
         [command:                   'GET networks/L_7'],
         [doc:                       'Return a network'],
         [devices:                   'http://s/networks/L_7/devices'],
         ['devices/claim':           'http://s/networks/L_7/devices/claim'],
         [siteToSiteVpn:             'http://s/networks/L_7/siteToSiteVpn'],
         ['traffic?timespan=2592000':'http://s/networks/L_7/traffic?timespan=2592000'],
         [accessPolicies:            'http://s/networks/L_7/accessPolicies'],
         [groupPolicies:             'http://s/networks/L_7/groupPolicies'],
         [ssids:                     'http://s/networks/L_7/ssids'],
         [vlans:                     'http://s/networks/L_7/vlans'],
         [bind:                      'http://s/networks/L_7/bind'],
         [unbind:                    'http://s/networks/L_7/unbind'],
         [delete:                    'http://s/networks/L_7/delete']
      ]
    }

    def "transform adds device specific links on /networks/[networkId]/devices"() {
      when:
      def linker  = Linker.of(request)
      def serial      = 'QXXX-XXXX-XXXX'
      def networkId    = 'L_8'
      def object  = [[serial:serial,networkId:networkId]]
      def command = 'GET networks/L_8/devices'
      def out = linker.transform(object, command)

      then:
      request.serverName >> 's'
      request.pathInfo   >> '/networks/L_8/devices'
      request.serverPort >> 80

      expect:
      out == [
         [serial: 'http://s/networks/L_8/devices/QXXX-XXXX-XXXX', networkId: 'L_8'],
         [command: 'GET networks/L_8/devices'],
         [doc:     'List the devices in a network'],
      ]
    }

}
