import java.time.*
import groovy.transform.*

@Immutable(knownImmutableClasses = [Instant]) class TimeRange {
    Instant start, end
}
