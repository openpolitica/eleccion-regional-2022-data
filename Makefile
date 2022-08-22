candidatos:
	cat resultados.ndjson | jq -c | sqlite-utils insert elecciones.db candidatos --nl --flatten --truncate -

detalle:
	cat resultados-filtrados.ndjson  | jq -c | sqlite-utils insert elecciones.db candidatos-detalle --nl --flatten --truncate -