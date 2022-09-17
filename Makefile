all: candidatos detalle detalle_lima
candidatos:
	cat resultados.ndjson | jq -c | sqlite-utils insert elecciones.db candidatos --nl --flatten --truncate -

run_detalle:
	./mvnw exec:java -Dexec.mainClass="op.elecciones.AppDetalle"

detalle:
	cat resultados-filtrados.ndjson  | jq -c | sqlite-utils insert elecciones.db candidatos_detalle --nl --flatten --truncate --alter -
	cat resultados-filtrados.ndjson  | jq -c | sqlite-utils insert elecciones.db candidatos_detalle_v2 --nl  --truncate --alter -

detalle_lima:
	cat resultados-filtrados-lima.ndjson  | jq -c | sqlite-utils insert elecciones.db candidatos_detalle --nl --flatten --alter -
	cat resultados-filtrados-lima.ndjson  | jq -c | sqlite-utils insert elecciones.db candidatos_detalle_v2 --nl  --alter -
