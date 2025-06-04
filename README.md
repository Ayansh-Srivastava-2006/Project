
---

## 🧠 Features

- **Login Authentication**  
  Role-based access for Admin and Customer using stored credentials.

- **Menu Management (OOP)**  
  `Menu_Item` superclass with subclasses `Appetizer`, `MainCourse`, `Drink` using inheritance and polymorphism.

- **Order Placement**  
  Customers can add/remove items, view totals, and generate bills.

- **Bill Generation**  
  Dynamic calculation with item-specific price handling via overridden `getPrice()` methods.

- **Modern UI with JavaFX**  
  Responsive layouts using `FXML`, styled with CSS.

---

## 🛠️ Technologies Used

- **Java 17**
- **JavaFX 17**
- **MySQL 8+**
- **JDBC (MySQL Connector)**
- **IntelliJ IDEA**

---

## 🧩 Database Schema

### `users` Table

| Column     | Type         | Description        |
|------------|--------------|--------------------|
| id         | INT (PK)     | Auto-increment ID  |
| username   | VARCHAR(50)  | Unique username    |
| password   | VARCHAR(100) | User password      |
| role       | VARCHAR(20)  | 'Admin' or 'Customer' |

### `menu_items` Table

| Column     | Type         | Description         |
|------------|--------------|---------------------|
| id         | INT (PK)     | Item ID             |
| name       | VARCHAR(100) | Item name           |
| category   | VARCHAR(50)  | Appetizer, Main etc |
| price      | DOUBLE       | Item price          |

---

## 🔌 How to Run

1. Open the file in intellij idea/eclipe ide.
2. Open the 'main' class file.
3. Make sure that that the mysql database server is setup on your device.
4. Replace the url in 'DBUtil.java' with your mysql url.
5. If using intellij idea connect the database in 'Database Tools and SQL' plugin as well.
6. Make sure that the name of the database and tables is the same as in the program or change the name of the database and table in the program accordingg to your database.
7. Setup the schemas in the other class files.
8. Run the database server.
9. Run the main class file.
