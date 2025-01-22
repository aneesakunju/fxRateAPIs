CREATE TABLE currencies (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(3) NOT NULL
);

INSERT INTO currencies (id, name, code) VALUES (1, 'US Dollar', 'USD');
INSERT INTO currencies (id, name, code) VALUES (2, 'Euro', 'EUR');
INSERT INTO currencies (id, name, code) VALUES (3, 'British Pound', 'GBP');
INSERT INTO currencies (id, name, code) VALUES (4, 'Japanese Yen', 'JPY');
INSERT INTO currencies (id, name, code) VALUES (5, 'Swiss Franc', 'CHF');
INSERT INTO currencies (id, name, code) VALUES (6, 'Canadian Dollar', 'CAD');
INSERT INTO currencies (id, name, code) VALUES (7, 'Australian Dollar', 'AUD');
INSERT INTO currencies (id, name, code) VALUES (8, 'New Zealand Dollar', 'NZD');
INSERT INTO currencies (id, name, code) VALUES (9, 'Chinese Yuan', 'CNY');
INSERT INTO currencies (id, name, code) VALUES (10, 'Swedish Krona', 'SEK');

CREATE TABLE fx_rate (
  id BIGSERIAL PRIMARY KEY ,
  target_currency_id INT NOT NULL,
  rate DECIMAL(10, 6) NOT NULL,
  effective_date DATE NOT NULL,
  FOREIGN KEY (target_currency_id) REFERENCES currencies(id)
);
