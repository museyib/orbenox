-- Migration V3: Add database indexes for performance optimization

-- Indexes on foreign keys for better join performance
CREATE INDEX IF NOT EXISTS idx_product_brand_id ON product (brand_id);
CREATE INDEX IF NOT EXISTS idx_product_producer_id ON product (producer_id);
CREATE INDEX IF NOT EXISTS idx_product_country_id ON product (country_id);
CREATE INDEX IF NOT EXISTS idx_product_category_id ON product (product_category_id);
CREATE INDEX IF NOT EXISTS idx_product_class_id ON product (product_class_id);
CREATE INDEX IF NOT EXISTS idx_product_type_id ON product (product_type_id);
CREATE INDEX IF NOT EXISTS idx_product_group_id ON product (product_group_id);
CREATE INDEX IF NOT EXISTS idx_product_default_unit_id ON product (default_unit_id);

-- Index on frequently queried deleted column
CREATE INDEX IF NOT EXISTS idx_product_deleted ON product (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_brand_deleted ON brand (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_producer_deleted ON producer (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_product_category_deleted ON product_category (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_product_class_deleted ON product_class (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_product_type_deleted ON product_type (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_product_group_deleted ON product_group (deleted) WHERE deleted = false;

-- Index on product barcode for fast lookups
CREATE INDEX IF NOT EXISTS idx_product_barcode_barcode ON product_barcode (barcode);
CREATE INDEX IF NOT EXISTS idx_product_barcode_product_id ON product_barcode (product_id);
CREATE INDEX IF NOT EXISTS idx_product_default_barcode ON product (default_barcode);

-- Indexes on user and role relationships
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON app_user_role (app_user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON app_user_role (app_role_id);
CREATE INDEX IF NOT EXISTS idx_app_permission_role_id ON app_permission (app_role_id);
CREATE INDEX IF NOT EXISTS idx_app_permission_permission_id ON app_permission (id);
CREATE INDEX IF NOT EXISTS idx_permission_resource_id ON app_permission (resource_id);
CREATE INDEX IF NOT EXISTS idx_permission_action ON app_permission (action);

-- Index on username for authentication
CREATE INDEX IF NOT EXISTS idx_user_username ON "app_user" (username);
CREATE INDEX IF NOT EXISTS idx_user_deleted ON "app_user" (deleted) WHERE deleted = false;

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_product_group_deleted_id ON product (product_group_id, deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_product_category_deleted_id ON product (product_category_id, deleted) WHERE deleted = false;

-- Index on audit fields for reporting
CREATE INDEX IF NOT EXISTS idx_product_created_at ON product (created_at);
CREATE INDEX IF NOT EXISTS idx_product_updated_at ON product (updated_at);

-- Indexes on price list
CREATE INDEX IF NOT EXISTS idx_price_list_deleted ON price_list (deleted) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_price_list_parent_id ON price_list (parent_id);

-- Indexes on unit and unit dimension
CREATE INDEX IF NOT EXISTS idx_unit_dimension_id ON unit (unit_dimension_id);
CREATE INDEX IF NOT EXISTS idx_unit_deleted ON unit (deleted) WHERE deleted = false;

-- Comment on indexes for documentation
COMMENT ON INDEX idx_product_deleted IS 'Partial index for active products only';
COMMENT ON INDEX idx_product_barcode_barcode IS 'Fast lookup for barcode scanning operations';
COMMENT ON INDEX idx_user_username IS 'Fast authentication lookup by username';
