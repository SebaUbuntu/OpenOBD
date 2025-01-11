import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        initKoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
