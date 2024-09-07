# CSV Processing API

This project provides a REST API for processing CSV files by evaluating formulas within cells using GraalVM.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven
- Postman (for API testing)

### Setup

1. **Clone the Repository:**

    ```bash
    https://github.com/RADHIKESHS/TheoreticalChallenge.git
    cd TheoreticalChallenge
    ```

2. **Build and Run the Application:**

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

## API Endpoints

### Process CSV

#### 1. Upload and Process CSV

- **Endpoint:** `POST /process-csv`
- **Description:** Uploads a CSV file, processes it by evaluating formulas within cells, and returns the processed CSV file.
- **Request:**
  - **Multipart File:** `file` (CSV file)
- **Response:**
  - **Content-Disposition Header:** `attachment;filename=processed_output.csv`
  - **Content-Type:** `application/csv`
  - **Body:** Processed CSV file

## Error Handling

The API provides specific error handling for CSV processing:

- **CsvProcessingException:** Returns a `400 Bad Request` response with the error message if an error occurs during CSV processing.
- **General Exceptions:** Returns a `500 Internal Server Error` response with a generic error message.

## How to Test

1. **Upload and Process CSV:**

    Use Postman to send a `POST` request to `http://localhost:8080/process-csv` with the CSV file in the `file` parameter.

## Security and Reliability

To ensure the security and reliability of the API:

- **Data Validation:** Ensure that the CSV data adheres to expected formats and values.
- **Error Handling:** Properly handle and log errors to provide meaningful feedback and support debugging.
- **Logging:** Use logging to monitor application behavior and issues.

## Contributing

Feel free to submit issues or pull requests. Ensure your contributions follow the project's coding standards and include appropriate tests.

Replace placeholders such as `your-username` and `your-repository` with your actual values.
