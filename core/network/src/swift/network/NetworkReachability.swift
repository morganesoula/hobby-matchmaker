import Network
import Foundation

@objcMembers public class NetworkReachability: NSObject {

    private let monitor = NWPathMonitor()
    private let queue = DispatchQueue(label: "Monitor")

    @objc public func checkConnection(_ completion: @escaping (Bool) -> Void) {
        monitor.pathUpdateHandler = { path in
            completion(path.status == .satisfied)
            self.monitor.cancel()
        }

        monitor.start(queue: queue)
    }
}
