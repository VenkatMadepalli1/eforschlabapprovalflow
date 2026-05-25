-- Create Company table
CREATE TABLE IF NOT EXISTS eforsch.company (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    company_no   VARCHAR(20)  NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on company_no for faster queries (since multiple companies can have same company_no)
CREATE INDEX idx_company_no ON eforsch.company(company_no);
