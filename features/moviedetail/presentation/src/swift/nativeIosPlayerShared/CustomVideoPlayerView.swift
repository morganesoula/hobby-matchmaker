import UIKit
import YouTubeiOSPlayerHelper

@objcMembers public class YoutubePlayerViewController: UIViewController, YTPlayerViewDelegate {
    private var playerView: YTPlayerView!
    private var videoId: String

    @objc public init(videoId: String) {
        self.videoId = videoId
        super.init(nibName: nil, bundle: nil)
    }

    @objc required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    @objc public override func viewDidLoad() {
        super.viewDidLoad()
        print("▶️ viewDidLoad called for videoId: \(videoId)")

        playerView = YTPlayerView()
        playerView.delegate = self
        playerView.frame = view.bounds
        playerView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(playerView)
        view.setNeedsLayout()

        let playerVars: [String: Any] = [
            "playsinline": 1,
            "autoplay": 0,
            "modestbranding": 1,
            "controls": 1,
        ]

        playerView.load(withVideoId: videoId, playerVars: playerVars)
    }

    @objc public func stop() {
        playerView.stopVideo()
    }

    @objc public override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        print("▶️ viewDidLayoutSubviews called")
        playerView.frame = view.bounds
    }

    @objc public override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        print("▶️ viewDidAppear called")
        playerView.playVideo()
    }

    @objc public func playerViewDidBecomeReady(_ playerView: YTPlayerView) {
        print("✅ YTPlayerView is ready to play")
        playerView.playVideo()
    }

    @objc public func playerView(_ playerView: YTPlayerView, didChangeTo state: YTPlayerState) {
        print("🎬 YTPlayerView changed state: \(state.rawValue)")
    }

    @objc public func playerView(_ playerView: YTPlayerView, receivedError error: YTPlayerError) {
        print("❌ YTPlayerView error: \(error)")
    }
}

@objcMembers public class YoutubePlayerContainer: NSObject {
    @objc public func makeUIViewController(videoId: String) -> UIViewController {
        return YoutubePlayerViewController(videoId: videoId)
    }
}
