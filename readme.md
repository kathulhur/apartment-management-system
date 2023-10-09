# Apartment Management System

## Overview

An apartment management system allowing apartment owners to track their apartments.
Records such as tenants, units can be stored by the system. Actions such as assigning
tenants in units can be done in the system. Owners can also manage invoices issued
to the tenants.

### Notable Features

-   Email notification
-   SMS notification

## Prerequisites

Before running the application, make sure you have the following prerequisites in place:

-   Java Development Kit (JDK) 17
-   Apache Maven
-   MySQL database 8.0.23 (if you will be using a different database, make sure to modify the dialect and the connector properties in the hibernate.cfg.xml file located in src/main/resouces)

### Getting Started

1. Clone this repository to your local machine.
2. Open a terminal or command prompt and navigate to the project directory.
3. Build the project using Maven:

#### shell

```Powershell

    mvn clean install

```

Before running the application, you need to provide the necessary environment variables. Create a .env file in the project root and fill in the required values:

```Powershell
# Database Configuration
DB_URL=[your_database_url]
DB_USERNAME=[your_database_username]
DB_PASSWORD=[your_database_password]

# SMS and Email Notification Configuration
INFOBIP_BASE_URL=[infobip_base_url]
INFOBIP_API_KEY=[infobip_api_key]
INFOBIP_SENDER_EMAIL_ADDRESS=[infobip_sender_email_address]
```

#### Run the application:

```Powershell
mvn clean javafx:run
```

(Assuming your terminal is pointed at the root directory of the app.)

You may also opt to use the tools provided by the Maven for Java extension.
In the explorer of the vscode you will see a MAVEN header, expand it and you will see the project name. Expand it, under the plugins -> javafx, click run.

The application's functionality relies on the following environment variables:

### Configuration

-   **DB_URL**: The URL of your MySQL database.
-   **DB_USERNAME**: The username for the database connection.
-   **DB_PASSWORD**: The password for the database connection.
-   **INFOBIP_BASE_URL**: The base URL for SMS and email notifications.
-   **INFOBIP_API_KEY**: The API key for the Infobip service.
-   **INFOBIP_SENDER_EMAIL_ADDRESS**: The sender email address for notifications.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please feel free to submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
