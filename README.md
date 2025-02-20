# Project Overview

## Features

### Admin 
- CRUD events
- CRUD products

### User
- Add tickets and products to cart
- Manage own cart
- Pay cart

### Guest
- Add products to cart

## Spond Api
- CRUD SpondEvents
---

## Tech Stack
**Frontend:** React (npm v10.7.0 tested) (node v18.20.3 tested)
**Backend:** Spring Boot  
**Database:** MySQL  
**External APIs:** [Spond API]([https://spond.com](https://github.com/Olen/Spond))  

---
## Prerequisites

### Environment Setup
- **Backend:**
  - Set the required environment variables in `.env` located in `backend/spond/`. also execute command `pip install spond`
  - Update the API key in `backend/src/main/java/com/example/backend/config/ApiKeyAuthService.java` (line 12).  
    **⚠️ It is recommended to change the API key for security purposes.**
  - Verify the correct version of `application.yml` based on the environment (local/docker). Configurations are specified in `backend/src/main/resources/` and defined in `db.txt`.

- **Frontend:**
  - Ensure `.env` in the `frontend/` directory is set correctly.

---
## Running the Application

### Docker (Currently Issues Exist)
**Note:** Docker currently has issues with the images directory.

### Manual Setup (Recommended)
1. **Create the Database:**
   - Run MySQL in XAMPP, the application will create the database with its tables and seed it.
2. **Configure Environment Variables:**
   - Create all `.env` files. Refer to the examples provided in the project.
3. **Run the Application:**
   - Start the backend.
     run Lab12BackendApplication.java in intellij or other IDE
   - Start the frontend.
     ```bash
     cd frontend/
     npm install
     npm start
     ```
   - Run this command in the directory /backend/spond to seed the events
     ```bash
     python .\spondApi.py
     ```

---
## Resources and References

### JWT Authentication
- **Frontend Implementation:** [React JWT Handling Tutorial](https://www.youtube.com/watch?v=bqFjrhRrvy8&t=1s)  
- **Backend Implementation:** [Spring Boot JWT Guide](https://app.pluralsight.com/ilx/video-courses/clips/7d9e33c7-01b8-4caa-9518-1b4d4801393c) (Part 4)
- **Route Protection React:** [Youtube Video](https://www.youtube.com/watch?v=W4hBA2gACik&t=1180s)
- **User Login Workflow React:** [Youtube Video](https://www.youtube.com/watch?v=9quu2psb_Ak) (have used this, changed later on but some stuff is still visible)
- **User Registration:**[Youtube Video](https://www.youtube.com/watch?v=LXqbrtMnvkY&t=251s) (have used this, changed later on but some stuff is still visible)
- 

### Relevant ChatGPT Conversations
- [Discussion 1](https://chatgpt.com/share/67699274-3754-8008-aa08-5adbd6e8bdde)  
- [Discussion 2](https://chatgpt.com/share/6769929f-10e4-8008-bc3d-53bd857be964)  
- [Discussion 3](https://chatgpt.com/share/676992fa-8aac-8008-bc0b-0d2ee64ad547)  
- [Discussion 4](https://chatgpt.com/share/67699341-4a2c-8008-8c79-e525028ca43f)  
- [Discussion 5](https://chatgpt.com/share/676993dd-7828-8008-a4b3-98d0a880c34d)  
- [Discussion 6](https://chatgpt.com/share/676993fc-2594-8008-be0f-9e3a3914b3c4)  
- [Discussion 7](https://chatgpt.com/share/6769940f-d3f4-8008-b18b-ef48c0547d19)
- [Discussion 8](https://chatgpt.com/share/67699868-b5d0-8008-85d1-6047ac234348)
- [Discussion 9](https://chatgpt.com/share/676a4567-fdb0-8008-9bad-639214e2bef9) documentation
- [Discussion 10](https://chatgpt.com/c/676995a8-9be8-8008-bed8-23778ebde367) README

### Discord Communities Consulted
- [Developer Support Discord](http://discord.gg/0xZXblUU30hYo1vJ)

---
## Troubleshooting

### Docker Issues
- Currently permission issues resources/images directory in the backend

