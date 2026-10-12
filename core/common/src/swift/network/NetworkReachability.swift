import Network
import Foundation

@objcMembers public class NetworkReachability: NSObject {

    private let monitor = NWPathMonitor()
    private let queue = DispatchQueue(label: "Monitor")

    @objc public func isConnected() -> Bool {
        let semaphore = DispatchSemaphore(value: 0)
        var result = false

        monitor.pathUpdateHandler = { path in
            result = (path.status == .satisfied)
            self.monitor.cancel()
            semaphore.signal()
        }

        monitor.start(queue: queue)

        let _ = semaphore.wait(timeout: .now() + 2.0)

        return result
    }
}
