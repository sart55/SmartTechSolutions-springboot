# SmartTechSolutions Database Schema

## ⚠️ STRICT RULES FOR AI
- Generate ONLY SELECT queries
- Use EXACT table names and column names (snake_case only)
- Do NOT use camelCase
- Do NOT guess names
- Always use JOINs based on foreign keys
- Use table aliases (p, c, q, etc.)
- Use PostgreSQL syntax
- Add LIMIT 50
## ⚠️ IMPORTANT NOTE

- Customer data is stored inside the `projects` table
- Use following columns:
    - customer_name
    - customer_email
    - customer_contact
    - customer_college
    - customer_branch

- Do NOT use customers table unless explicitly needed

---

# 📦 TABLES

## admins
Primary Key:
- id

Columns:
- id
- username
- password
- name

---

## users
Primary Key:
- id

Columns:
- id
- username
- password
- role

---

## customers
Primary Key:
- id

Columns:
- id
- name
- contact
- email
- college
- branch

---

## components
Primary Key:
- id

Columns:
- id
- name
- price
- quantity
- last_updated_by

---

## component_history
Primary Key:
- id

Columns:
- id
- name
- quantity
- price
- added_by
- date

---

## projects
Primary Key:
- id

Columns:
- id
- project_name
- customer_name
- description
- customer_email
- customer_contact
- customer_college
- customer_branch
- total_amount
- quotation_created
- status

---

## quotations
Primary Key:
- id

Foreign Keys:
- project_id → projects.id

Columns:
- id
- total_amount
- setup_charges
- dev_charges
- project_id
- created_at

---

## quotation_items
Primary Key:
- id

Foreign Keys:
- quotation_id → quotations.id

Columns:
- id
- component_name
- quantity
- price
- subtotal
- quotation_id

---

## payment
Primary Key:
- id

Foreign Keys:
- project_id → projects.id

Columns:
- id
- amount
- mode
- username
- created_at
- project_id

---

## comment
Primary Key:
- id

Foreign Keys:
- project_id → projects.id

Columns:
- id
- text
- username
- created_at
- project_id

---

# 🔗 RELATIONSHIPS (IMPORTANT FOR JOINS)

1. projects → quotations  
   projects.id = quotations.project_id

2. quotations → quotation_items  
   quotations.id = quotation_items.quotation_id

3. projects → payment  
   projects.id = payment.project_id

4. projects → comment  
   projects.id = comment.project_id

---

# 🔥 JOIN EXAMPLES (AI SHOULD FOLLOW THIS)

## Example 1: Project with payments
SELECT p.id, p.project_name, pay.amount
FROM projects p
JOIN payment pay ON p.id = pay.project_id
LIMIT 50;

---

## Example 2: Project with quotation
SELECT p.project_name, q.total_amount
FROM projects p
JOIN quotations q ON p.id = q.project_id
LIMIT 50;

---

## Example 3: Quotation with items
SELECT q.id, qi.component_name, qi.quantity
FROM quotations q
JOIN quotation_items qi ON q.id = qi.quotation_id
LIMIT 50;
