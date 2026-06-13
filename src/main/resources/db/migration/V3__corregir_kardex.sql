ALTER TABLE kardex
    MODIFY COLUMN cantidad  INT NOT NULL,
    MODIFY COLUMN stock     INT NOT NULL,
    RENAME COLUMN id TO kardex_id;