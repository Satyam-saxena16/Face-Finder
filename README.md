
Face Finder
=================

Face Finder is a web application that identifies and extracts a user's photos from a large collection. 
It’s especially helpful in scenarios like finding your pictures from a big wedding album.

Features
-----------
- Upload a selfie and multiple images.
- The backend processes and finds all matching faces from the photos.
- Instant display of matched images.
- Option to download results.

Tech Stack
-------------
- Frontend: HTML, CSS, JavaScript
- Backend: Java (Spring Boot), Python (for face recognition)

Folder Structure
-------------------
FACEFINDER2/
│
├── .mvn/
├── .vscode/
├── scripts/
│   └── face_finder.py
│
├── src/
│   └── main/
│       ├── java/
│       │   └── website/facefinder2/
│       │       ├── CorsConfig.java
│       │       ├── Facefinder2Application.java
│       │       └── FaceFinderController.java
│       │
│       └── resources/
│           ├── static/
│           │   ├── index.html
│           │   ├── logo.jpg
│           │   └── style.css
│           ├── templates/
│           └── application.properties
│
├── test/
├── target/
├── uploads/
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml


How to Run the Project
-------------------------

1. Clone the Repository
------------------------
git clone https://github.com/your-username/face-finder.git
cd face-finder

2. Backend (Spring Boot)
------------------------
Make sure you have Java and Maven installed.

mvn clean install
./mvn spring-boot:run

This will start your backend server at http://localhost:8080.

3. Python Face Matching Script
------------------------------
Ensure Python 3 and required packages are installed.

pip install face_recognition flask requests
python face_finder.py

Optional: Use ngrok to expose your local Python server if needed.

4. Frontend
-----------
Just open index.html in a browser.

Example Use Case
-------------------
Imagine you've received 1,000+ images from a wedding event. Instead of manually browsing each photo, 
upload your selfie and all the photos — and Face Finder will extract only the ones you're in.

Future Improvements
----------------------
- Drag-and-drop support
- Multiple face detection
- Cloud deployment
