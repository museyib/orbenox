const documentEndpoints = {
  SALES_ORDER: "/api/salesOrder",
  PRODUCT_APPROVE: "/api/productApproves"
};

export function documentEndpoint(typeCode) {
  return documentEndpoints[String(typeCode || "").toUpperCase()] || null;
}

export function documentTypeCode(document) {
  return document?.typeCode || document?.typeItem?.code || "";
}
