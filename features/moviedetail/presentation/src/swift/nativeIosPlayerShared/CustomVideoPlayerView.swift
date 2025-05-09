import UIKit
import AVKit

@objcMembers public class CustomVideoPlayerView: UIView {

    private var playerViewController: AVPlayerViewController?
    private var player: AVPlayer?

    @objc public static let shared = CustomVideoPlayerView()

    @objc public func configureVideo(_ urlString: String) {
        guard let url = URL(string: urlString) else { return }

        if playerViewController == nil {
            let player = AVPlayer(url: url)
            let controller = AVPlayerViewController()
            controller.player = player
            controller.showsPlaybackControls = true

            controller.view.frame = self.bounds
            controller.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            self.addSubview(controller.view)

            if let parentVC = self.parentViewController() {
                parentVC.addChild(controller)
                controller.didMove(toParent: parentVC)
            }

            self.playerViewController = controller
            self.player = player

            player.play()
        } else {
            player?.replaceCurrentItem(with: AVPlayerItem(url: url))
            player?.play()
        }
    }

    private func parentViewController() -> UIViewController? {
        var parentResponder: UIResponder? = self
        while let responder = parentResponder {
            if let vc = responder as? UIViewController {
                return vc
            }
            parentResponder = responder.next
        }
        return nil
    }
}
