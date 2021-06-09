const functions = require("firebase-functions");

exports.dangerCheck = functions.region("asia-southeast2").firestore
    .document("danger/{user}/record/{dangerID}").onCreate((snap, _) => {
      const newValue = snap.data();
      console.log(`ID DANGER TERBARU ${newValue["id"]}`);
      console.log(`LINK ${newValue["record"]}`);
    });
