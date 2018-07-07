package domain

import java.time.*
import groovy.transform.*

@Canonical class ClientConnectionsSnapshot {

    final TimeRange timeRange
    final ClientConnection[] connections

    ClientConnectionsSnapshot(timeRange,connections) {
        this.timeRange   = timeRange
        this.connections = connections
    }
}
