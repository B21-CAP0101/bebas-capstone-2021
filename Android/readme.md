# Android Replicate step
   To replicate android application, just clone this repository.  
   Our Android folder is using Clean-Architecture principle.

## Features and Explanation About our Android Application
   In this project we use:  
1. [Koin](https://insert-koin.io/) for our Dependency Injection
2. [Room](https://developer.android.com/jetpack/androidx/releases/room) for our Database Services
3. [Firebase](https://firebase.google.com/) for our online server (Firestore and Firebase Storage)  

   In this application, we separated to 2 modules, app and core:  
1. app Module for application interface, widget, and background service (foreground service) to fetch danger data automatically if someone press panic button.
2. core Module for backend folder for this android application. This module contains every function that used for connect to Firebase or to store data to Room Database.

### Optional for Firebase change Project
   If you want to change with your own Firebase, please replace [google-services.json](https://github.com/B21-CAP0101/bebas-capstone-2021/blob/master/Android/core/google-services.json) file in [this directory](https://github.com/B21-CAP0101/bebas-capstone-2021/tree/master/Android/core). Make sure you add application to Firebase to got [your own google-services.json file](https://support.google.com/firebase/answer/7015592?hl=en)  