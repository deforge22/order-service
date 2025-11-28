CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        table_number INTEGER NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             item_name VARCHAR(255) NOT NULL,
                             quantity INTEGER NOT NULL,
                             unit_price DECIMAL(10, 2) NOT NULL,
                             notes TEXT,
                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_orders_table_number ON orders(table_number);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);