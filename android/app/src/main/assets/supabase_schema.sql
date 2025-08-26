-- Supabase Database Schema for Hijaiyah App
-- Make sure this table exists in your Supabase database

-- Create hijaiyah table if it doesn't exist
CREATE TABLE IF NOT EXISTS hijaiyah (
    hijaiyah_id SERIAL PRIMARY KEY,
    latin_name VARCHAR(50) NOT NULL,
    arabic_char VARCHAR(10) NOT NULL,
    ordinal INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Insert sample data if table is empty
INSERT INTO hijaiyah (latin_name, arabic_char, ordinal) 
SELECT * FROM (VALUES
    ('alif', 'ا', 1),
    ('ba', 'ب', 2),
    ('ta', 'ت', 3),
    ('tsa', 'ث', 4),
    ('jim', 'ج', 5),
    ('ha', 'ح', 6),
    ('kho', 'خ', 7),
    ('dal', 'د', 8),
    ('dzal', 'ذ', 9),
    ('ro', 'ر', 10),
    ('zai', 'ز', 11),
    ('sin', 'س', 12),
    ('syin', 'ش', 13),
    ('shod', 'ص', 14),
    ('dhod', 'ض', 15),
    ('tho', 'ط', 16),
    ('dho', 'ظ', 17),
    ('ain', 'ع', 18),
    ('ghoin', 'غ', 19),
    ('fa', 'ف', 20),
    ('qof', 'ق', 21),
    ('kaf', 'ك', 22),
    ('lam', 'ل', 23),
    ('mim', 'م', 24),
    ('nun', 'ن', 25),
    ('waw', 'و', 26),
    ('ha2', 'ه', 27),
    ('ya', 'ي', 28)
) AS t(latin_name, arabic_char, ordinal)
WHERE NOT EXISTS (SELECT 1 FROM hijaiyah LIMIT 1);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_hijaiyah_ordinal ON hijaiyah(ordinal);
CREATE INDEX IF NOT EXISTS idx_hijaiyah_latin_name ON hijaiyah(latin_name);
