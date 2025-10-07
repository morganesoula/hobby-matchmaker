//
//  ContentView.swift
//  iosApp
//
//  Created by Morgane Soula on 05/10/2025.
//

/* import UIKit
import SwiftUI
import composeApp

final class EdgeToEdgeComposeController: UIViewController {

    private let builder: () -> UIViewController

    init(_ builder: @escaping () -> UIViewController) {
        self.builder = builder
        super.init(nibName: nil, bundle: nil)
        edgesForExtendedLayout = [.top, .bottom]
        extendedLayoutIncludesOpaqueBars = true
        additionalSafeAreaInsets = .zero
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func viewDidLoad() {
        super.viewDidLoad()

        let child = builder()
        addChild(child)

        child.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(child.view)
        NSLayoutConstraint.activate([
            child.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            child.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            child.view.topAnchor.constraint(equalTo: view.topAnchor),
            child.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])

        child.view.insetsLayoutMarginsFromSafeArea = false
        child.viewRespectsSystemMinimumLayoutMargins = false

        child.didMove(toParent: self)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        EdgeToEdgeComposeController {
            MainViewControllerKt.MainViewController()
        }
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
} */
