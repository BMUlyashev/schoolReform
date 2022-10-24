CREATE TABLE autos (
    id SERIAL PRIMARY KEY ,
    brand TEXT,
    model TEXT NOT NULL ,
    price REAL
);

CREATE TABLE persons (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    age INTEGER CHECK ( age > 0 ),
    license BOOLEAN DEFAULT FALSE,
    autos_id INTEGER REFERENCES autos(id)
);
