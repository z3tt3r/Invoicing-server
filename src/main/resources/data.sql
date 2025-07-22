-- data.sql
-- Vkládání testovacích dat pro osoby a faktury

-- OSOBY (5 záznamů)
INSERT INTO person (name, identification_number, tax_number, account_number, bank_code, iban, telephone, mail, street, zip, city, country, note, hidden) VALUES
('Jan Novák', '12345678', 'CZ12345678', '1234567890', '0100', 'CZ123456789012345678901234', '+420777111222', 'jan.novak@example.com', 'Hlavní 1', '10000', 'Praha', 'CZECHIA', 'Osoba A - fyzická', FALSE),
('ABC Firma s.r.o.', '87654321', 'CZ87654321', '0987654321', '0800', 'CZ098765432109876543210987', '+420222333444', 'info@abcfirma.cz', 'Dlouhá 15', '11000', 'Brno', 'CZECHIA', 'Osoba B - právnická', FALSE),
('Peter Kováč', '98765432', 'SK98765432', '1122334455', '0900', 'SK112233445511223344551122', '+421901555666', 'peter.kovac@example.sk', 'Mierová 5', '81101', 'Bratislava', 'SLOVAKIA', 'Osoba C - fyzická', FALSE),
('DEF Solutions a.s.', '11223344', 'CZ11223344', '2233445566', '0300', 'CZ223344556622334455662233', '+420608777888', 'contact@defs.cz', 'Krátká 7', '60200', 'Ostrava', 'CZECHIA', 'Osoba D - právnická', FALSE),
('Anna Veselá', '55443322', 'CZ55443322', '3344556677', '0600', 'CZ334455667733445566773344', '+420721999000', 'anna.vesela@example.com', 'Náměstí 10', '30100', 'Plzeň', 'CZECHIA', 'Osoba E - fyzická', FALSE);

-- FAKTURY (10 záznamů)
-- Poznámka: ID kupujícího a prodávajícího odkazují na ID osob vytvořených výše (1-5)
INSERT INTO invoice (invoice_number, issued, due_date, product, price, vat, note, buyer_id, seller_id, hidden) VALUES
(20250001, '2025-01-01', '2025-01-15', 'Webdesign', 15000.00, 21, 'První faktura', 1, 2, FALSE),
(20250002, '2025-01-05', '2025-01-19', 'Marketingové služby', 5000.50, 21, 'Druhá faktura', 2, 1, FALSE),
(20250003, '2025-01-10', '2025-01-24', 'Software licence', 2500.00, 15, 'Třetí faktura', 3, 4, FALSE),
(20250004, '2025-01-15', '2025-01-29', 'Konzultace', 7500.20, 21, 'Čtvrtá faktura', 4, 3, FALSE),
(20250005, '2025-01-20', '2025-02-03', 'Hardware', 12000.00, 21, 'Pátá faktura', 5, 1, FALSE),
(20250006, '2025-01-25', '2025-02-08', 'Účetní služby', 3000.00, 21, 'Šestá faktura', 1, 5, FALSE),
(20250007, '2025-02-01', '2025-02-15', 'Grafické práce', 8000.75, 15, 'Sedmá faktura', 2, 3, FALSE),
(20250008, '2025-02-05', '2025-02-19', 'IT podpora', 4500.00, 21, 'Osmá faktura', 3, 2, FALSE),
(20250009, '2025-02-10', '2025-02-24', 'Školení', 6000.00, 21, 'Devátá faktura', 4, 5, FALSE),
(20250010, '2025-02-15', '2025-02-28', 'Pronájem serveru', 10000.00, 21, 'Desátá faktura', 5, 4, FALSE); -- <-- ZMĚNA ZDE: 2025-02-29 na 2025-02-28
