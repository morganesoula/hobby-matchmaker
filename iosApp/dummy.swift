import FirebaseCore
import FirebaseAuth
import FirebaseFirestore

func forceLinking() {
    _ = FirebaseApp.app()
    _ = Auth.auth()
    _ = Firestore.firestore()
}
