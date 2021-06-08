# BeBaS Capstone B21-0101 Theme Bangkit 2021
[![B21-CAP0101](https://circleci.com/gh/B21-CAP0101/bebas-capstone-2021.svg?style=svg)](https://circleci.com/gh/B21-CAP0101/bebas-capstone-2021)  
   Bersama Basmi kekeraSan (<i>Together Eradicate Violence</i>) is our application (B21-0101) to finish our Capstone Project in Bangkit 2021. This application will help every user to alert if his/her relatives/friends in danger.

## Replicate Our Application

### Android Replicate step
To replicate android application, just clone this repository.  
   Our Android folder is using Clean-Architecture principle.

#### Features and Explanation About our Android Application
In this project we use:  
1. [Koin](https://insert-koin.io/) for our Dependency Injection
2. [Room](https://developer.android.com/jetpack/androidx/releases/room) for our Database Services
3. [Firebase](https://firebase.google.com/) for our online server (Firestore and Firebase Storage)  

In this application, we seperated to 2 modules, app and core:  
1. app Module for application interface, widget, and background service (foreground service) to fetch danger data automatically if someone press panic button.
2. core Module for backend folder for this android application. This module contains every function that used for connect to Firebase or to store data to Room Database.

##### Optional for Firebase change Project
If you want to change with your own Firebase, please replace [google-services.json](https://github.com/B21-CAP0101/bebas-capstone-2021/blob/master/Android/core/google-services.json) file in [this directory](https://github.com/B21-CAP0101/bebas-capstone-2021/tree/master/Android/core). Make sure you add application to Firebase to got [your own google-services.json file](https://support.google.com/firebase/answer/7015592?hl=en)  

### Cloud Computing
1. To replicate cloud service, just copy command and paste to google cloud command line in this [directory](https://github.com/B21-CAP0101/bebas-capstone-2021/tree/master/Cloud%20Computing).
2. Don’t forget to change the service name with your own service name.

### Machine Learning
To replicate our model, simply copy and paste from our [notebook](https://github.com/B21-CAP0101/bebas-capstone-2021/blob/master/Machine%20Learning/training_custom_audio_model_in_python.ipynb).
