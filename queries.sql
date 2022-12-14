'GOBERNADORES=261
select count(1)
from candidatos
where cargo like "%GOBERNADOR REGIONAL%"
    and cargo not like "%VICEGOBERNADOR REGIONAL%";
'VICE=261
select count(1)
from candidatos
where cargo like "%VICEGOBERNADOR REGIONAL%";
'CONSEJERO=3475
select count(1)
from candidatos
where cargo like "%CONSEJERO REGIONAL%";
'ACCESITARIO=3474
select count(1)
from candidatos
where cargo like "%ACCESITARIO%";
'ALL=7471
select count(1)
from candidatos
where (cargo like "%GOBERNADOR REGIONAL%"
    and cargo not like "%VICEGOBERNADOR REGIONAL%")
    or
    (cargo like "%VICEGOBERNADOR REGIONAL%")
    or
    (cargo like "%CONSEJERO REGIONAL%")
    or
    (cargo like "%ACCESITARIO%");
'FILTER
select cast(idHojaVida as int) as idHojaVida, numeroDocumento
from candidatos
where (cargo like "%GOBERNADOR REGIONAL%"
    and cargo not like "%VICEGOBERNADOR REGIONAL%")
    or
    (cargo like "%VICEGOBERNADOR REGIONAL%")
    or
    (cargo like "%CONSEJERO REGIONAL%")
    or
    (cargo like "%ACCESITARIO%");


'LIMA

select idHojaVida, numeroDocumento
from candidatos
where
(
    cargo like "%REGIDOR PROVINCIAL%"
    or cargo like "%ALCALDE PROVINCIAL%"
)
and "postulaDepartamento" = "LIMA"